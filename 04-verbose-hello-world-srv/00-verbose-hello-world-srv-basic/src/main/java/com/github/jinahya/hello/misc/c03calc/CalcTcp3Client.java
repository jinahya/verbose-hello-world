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
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class CalcTcp3Client extends _CalcTcp {

    private static void registerClient(final Selector selector, final AtomicInteger clients) {
        while (clients.get() > 0) {
            final SocketChannel client;
            try {
                // -------------------------------------------------------------------------------- open
                client = SocketChannel.open(); // no-try-with-resources
                try {
                    // ---------------------------------------------------------------------- bind(optional)
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        client.bind(new InetSocketAddress(HOST, 0));
                    }
                    // -------------------------------------------------------------- configure-non-blocking
                    client.configureBlocking(false);
                    // ------------------------------------------------------------------------ connect(try)
                    if (client.connect(ADDR)) {
                        client.register(
                                selector,
                                SelectionKey.OP_WRITE,
                                __CalcMessage2.newRandomizedBufferForClient()
                        );
                    } else {
                        client.register(selector, SelectionKey.OP_CONNECT);
                    }
                    clients.decrementAndGet();
                    break;
                } catch (final IOException ioe) {
                    log.error("failed to (bind)/configure/register", ioe);
                    client.close();
                }
            } catch (final IOException ioe) {
                log.error("failed to register", ioe);
            }
        }
    }

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open()) {
            final var clients = new AtomicInteger(CLIENT_COUNT);
            registerClient(selector, clients);
            // ----------------------------------------------------------------------- selector-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    log.debug("zero selected; continuing..");
                    continue;
                }
                // ------------------------------------------------------------------------- process
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var key = i.next();
                    i.remove();
                    // ------------------------------------------------------------- connect(finish)
                    if (key.isConnectable()) {
                        final var channel = (SocketChannel) key.channel();
                        if (channel.finishConnect()) {
                            key.attach(
                                    new __CalcMessage3.OfBuffer().randomize().readyToWriteToServer()
                            );
                            key.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            key.interestOpsOr(SelectionKey.OP_WRITE);
                            assert !key.isWritable();
                            registerClient(selector, clients);
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (key.isWritable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var message = (__CalcMessage3.OfBuffer) key.attachment();
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
                        final var message = (__CalcMessage3.OfBuffer) key.attachment();
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
