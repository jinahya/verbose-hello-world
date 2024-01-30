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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class CalcTcp3Client extends CalcTcp {

    private static void connect(final Selector selector) throws IOException {
        final var client = SocketChannel.open();
        try {
            // -------------------------------------------------------------- configure-non-blocking
            client.configureBlocking(false);
            // ------------------------------------------------------------------------ connect(try)
            if (client.connect(ADDR)) {
                final var clientKey = client.register(
                        selector,
                        SelectionKey.OP_WRITE,
                        new _Message.OfBuffer().randomize().readyToWriteToServer()
                );
                assert !clientKey.isWritable();
                connect(selector);
            } else {
                final var clientKey = client.register(
                        selector,
                        SelectionKey.OP_CONNECT
                );
                assert !clientKey.isConnectable();
            }
        } catch (final IOException ioe) {
            log.error("failed to configure-non-blocking/connect(try)", ioe);
            client.close();
        }
    }

    public static void main(final String... args) throws Exception {
        try (var selector = Selector.open()) {
            final var requests = new AtomicInteger(REQUEST_COUNT);
            final var index = new AtomicInteger();
            // ------------------------------------------------------------------------ connect(try)
            if (requests.getAndDecrement() > 0) {
                connect(selector);
            }
            // ----------------------------------------------------------------------- selector-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                // ------------------------------------------------------------------------- process
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    // ------------------------------------------------------------- connect(finish)
                    if (selectedKey.isConnectable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        if (channel.finishConnect()) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_CONNECT);
                            selectedKey.attach(
                                    new _Message.OfBuffer()
                                            .randomize()
                                            .readyToWriteToServer()
                            );
                            selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                            assert !selectedKey.isWritable();
                            // -------------------------------------------------------- connect(try)
                            if (requests.getAndDecrement() > 0) {
                                connect(selector);
                            }
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (selectedKey.isWritable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        final var message = (_Message.OfBuffer) selectedKey.attachment();
                        assert message.hasRemaining();
                        final var w = message.write(channel);
                        assert w > 0; // why?
                        if (!message.hasRemaining()) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                            assert selectedKey.isWritable();
                            message.readyToReadFromServer();
                            selectedKey.interestOpsOr(SelectionKey.OP_READ);
                            assert !selectedKey.isReadable();
                        }
                    }
                    // ------------------------------------------------------------------------ read
                    if (selectedKey.isReadable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        final var message = (_Message.OfBuffer) selectedKey.attachment();
                        assert message.hasRemaining();
                        final var r = message.read(channel);
                        if (r == -1) {
                            log.error("premature eof");
                            channel.close();
                            assert !selectedKey.isValid();
                            continue;
                        }
                        assert r > 0; // why?
                        if (!message.hasRemaining()) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                            message.log(index.getAndIncrement());
                            channel.close();
                            assert !selectedKey.isValid();
                        }
                    }
                }
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcTcp3Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
