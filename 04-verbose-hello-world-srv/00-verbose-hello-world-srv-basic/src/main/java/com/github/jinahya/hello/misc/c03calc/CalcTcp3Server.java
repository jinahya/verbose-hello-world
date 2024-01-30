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

import com.github.jinahya.hello.util.JavaLangUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp3Server extends CalcTcp {

    private static void closeUnchecked(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException ioe) {
            log.error("failed to close {}", closeable, ioe);
        }
    }

    private static void cancel(final SelectionKey key) {
        try {
            key.channel().close();
            assert !key.isValid();
        } catch (final IOException ioe) {
            log.error("failed to close " + key.channel(), ioe);
            key.cancel();
            assert !key.isValid();
        }
    }

    public static void main(final String... args) throws IOException, InterruptedException {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open();
             var executor = newExecutorForServer("tcp-3-server-")) {
            // --------------------------------------------------------------------------- configure
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            try {
                server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            } catch (final IOException ioe) {
                log.error("failed to set SO_REUSEPORT", ioe);
            }
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR, SERVER_BACKLOG);
            // ----------------------------------------------------- configure-non-blocking/register
            final var serverKey = server.configureBlocking(false).register(
                    selector,              // <sel>
                    SelectionKey.OP_ACCEPT // <ops>
            );
            // --------------------------------------------- read-quit!/close-server/wakeup-selector
            JavaLangUtils.readLinesAndCallWhenTests(
                    "quit!"::equalsIgnoreCase,
                    () -> {
                        server.close();
                        assert !serverKey.isValid();
                        selector.wakeup(); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                        return null;
                    }
            );
            // ---------------------------------------------------------------------- selection-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                if (selector.select() == 0) {
                    continue;
                }
                // ------------------------------------------------------------------------- process
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var selectedKey = i.next();
                    // ---------------------------------------------------------------------- accept
                    if (selectedKey.isAcceptable()) {
                        final var channel = (ServerSocketChannel) selectedKey.channel();
                        assert channel == server;
                        final SocketChannel client;
                        try {
                            client = channel.accept();
                        } catch (final IOException ioe) {
                            if (channel.isOpen()) {
                                log.debug("failed to accept", ioe);
                            }
                            continue;
                        }
                        final var clientKey = client.configureBlocking(false).register(
                                selector,                                       // <sel>
                                SelectionKey.OP_READ,                           // <ops>
                                new _Message.OfBuffer().readyToReadFromClient() // <att>
                        );
                        assert !clientKey.isReadable();
                    }
                    // ------------------------------------------------------------------------ read
                    if (selectedKey.isReadable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        final var message = (_Message.OfBuffer) selectedKey.attachment();
                        assert message.hasRemaining();
                        final var r = message.read(channel);
                        if (r == -1) {
                            log.error("premature eof");
                            cancel(selectedKey);
                            continue;
                        }
                        assert r > 0;
                        if (!message.hasRemaining()) {
                            channel.shutdownInput();
                            selectedKey.interestOpsAnd(~SelectionKey.OP_READ);
                            assert selectedKey.isReadable();
                            message.calculateResult(executor, m -> {
                                m.readyToWriteToClient();
                                selectedKey.interestOpsOr(SelectionKey.OP_WRITE);
                                assert !selectedKey.isWritable();
                                selector.wakeup(); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                            });
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (selectedKey.isWritable()) {
                        final var channel = (SocketChannel) selectedKey.channel();
                        final var message = (_Message.OfBuffer) selectedKey.attachment();
                        assert message.hasRemaining();
                        final var w = message.write(channel);
                        assert w >= 0;
                        if (!message.hasRemaining()) {
                            selectedKey.interestOpsAnd(~SelectionKey.OP_WRITE);
                            assert selectedKey.isWritable();
                            cancel(selectedKey);
                            assert !selectedKey.isValid();
                        }
                    }
                }
            }
            // ------------------------------------------------- shutdown-executor/await-termination
            executor.shutdown();
            if (!executor.awaitTermination(1L, TimeUnit.SECONDS)) {
                log.error("executor not terminated");
            }
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcTcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
