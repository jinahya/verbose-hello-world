package com.github.jinahya.hello.misc.c03calc;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class CalcTcp3Client extends CalcTcp {

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open()) {
            for (int i = 0; i < CLIENT_COUNT; i++) {
                final var client = SocketChannel.open();
                try {
                    // -------------------------------------------------------------- bind(optional)
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        client.bind(new InetSocketAddress(HOST, 0));
                    }
                    // ------------------------------------------------------ configure-non-blocking
                    client.configureBlocking(false);
                    // ---------------------------------------------------------------- connect(try)
                    if (client.connect(ADDR)) {
                        client.register(
                                selector,
                                SelectionKey.OP_WRITE,
                                new _Message.OfBuffer().randomize().readyToWriteToServer()
                        );
                    } else {
                        client.register(selector, SelectionKey.OP_CONNECT);
                    }
                } catch (final IOException ioe) {
                    log.error("failed to open/configure/connect(try)", ioe);
                    client.close();
                }
            }
            // ----------------------------------------------------------------------- selector-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    log.debug("zero selected; continuing..");
                    continue;
                }
                // ----------------------------------------------------------- process-selected-keys
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var key = i.next();
                    // ------------------------------------------------------------- connect(finish)
                    if (key.isConnectable()) {
                        final var channel = (SocketChannel) key.channel();
                        try {
                            if (channel.finishConnect()) {
                                key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                                key.attach(
                                        new _Message.OfBuffer()
                                                .randomize()
                                                .readyToWriteToServer()
                                );
                                key.interestOpsOr(SelectionKey.OP_WRITE);
                                assert !key.isWritable();
                            }
                        } catch (final IOException ioe) {
                            log.error("failed to finish connecting", ioe);
                            channel.close();
                            assert !key.isValid();
                            continue;
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (key.isWritable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var message = (_Message.OfBuffer) key.attachment();
                        assert message.hasRemaining();
                        final var w = message.write(channel);
                        assert w >= 0;
                        if (!message.hasRemaining()) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE);
                            message.readyToReadFromServer();
                            key.interestOpsOr(SelectionKey.OP_READ);
                            assert !key.isReadable();
                        }
                    }
                    // ------------------------------------------------------------------------ read
                    if (key.isReadable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var message = (_Message.OfBuffer) key.attachment();
                        assert message.hasRemaining();
                        final var r = message.read(channel);
                        if (r == -1) {
                            log.error("premature eof");
                            key.interestOpsAnd(~SelectionKey.OP_READ); // redundant
                            channel.close();
                            assert !key.isValid();
                            continue;
                        }
                        assert r >= 0;
                        if (!message.hasRemaining()) {
                            key.interestOpsAnd(~SelectionKey.OP_READ); // redundant
                            channel.close();
                            assert !key.isValid();
                            message.log();
                        }
                    }
                }
            }
        }
    }
}
