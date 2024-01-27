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
            key.cancel();
            assert !key.isValid();
        }
    }

    public static void main(final String... args) throws IOException, InterruptedException {
        try (var selector = Selector.open();
             var server = ServerSocketChannel.open();
             var executor = newExecutorForServer("tcp-3-server-")) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR, SERVER_BACKLOG));
            // ----------------------------------------------------- configure-non-blocking/register
            final var serverKey = server
                    .configureBlocking(false)
                    .register(selector, SelectionKey.OP_ACCEPT);
            // ------------------------------------------------------------- read-quit!/close-server
            JavaLangUtils.readLinesAndCallWhenTests(
                    "quit!"::equalsIgnoreCase,
                    () -> {
                        server.close();
                        assert !serverKey.isValid();
                        selector.keys().forEach(SelectionKey::cancel);
                        selector.wakeup(); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                        return null;
                    }
            );
            // ---------------------------------------------------------------------- selection-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                // -------------------------------------------------------------------------- select
                try {
                    if (selector.select() == 0) {
                        continue;
                    }
                } catch (final IOException ioe) {
                    log.error("failed to select", ioe);
                    continue;
                }
                // ------------------------------------------------------------------------- process
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final var key = i.next();
                    // ---------------------------------------------------------------------- accept
                    if (key.isAcceptable()) {
                        final var channel = (ServerSocketChannel) key.channel();
                        assert channel == server;
                        final SocketChannel client;
                        try {
                            client = channel.accept();
                        } catch (final IOException ioe) {
                            log.debug("failed to accept", ioe);
                            continue;
                        }
                        try {
                            final var clientKey = client.configureBlocking(false).register(
                                    selector,
                                    SelectionKey.OP_READ,
                                    new _Message.OfBuffer().readyToReadFromClient()
                            );
                            assert !clientKey.isReadable();
                        } catch (final IOException ioe) {
                            log.error("failed to configure/register " + client, ioe);
                            closeUnchecked(client);
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
                            cancel(key);
                            continue;
                        }
                        assert r >= 0;
                        if (!message.hasRemaining()) {
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                            message.calculateResult(executor, m -> {
                                m.readyToWriteToClient();
                                key.interestOpsOr(SelectionKey.OP_WRITE);
                                assert !key.isWritable();
                                selector.wakeup(); // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                            });
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
                            key.interestOpsAnd(~SelectionKey.OP_WRITE); // redundant
                            cancel(key);
                        }
                    }
                }
            }
            // ------------------------------------------------- shutdown-executor/await-termination
            executor.shutdown();
            if (!executor.awaitTermination(10L, TimeUnit.SECONDS)) {
                log.error("executor not terminated");
            }
        }
    }
}
