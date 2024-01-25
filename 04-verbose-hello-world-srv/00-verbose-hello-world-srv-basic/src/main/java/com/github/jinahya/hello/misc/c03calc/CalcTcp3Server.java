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
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp3Server extends _CalcTcp {

    private static void closeUnchecked(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException ioe) {
            log.debug("failed to close {}", closeable, ioe);
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
             var executor = Executors.newCachedThreadPool(
                     Thread.ofVirtual().name("calc-tcp-3-server-", 0L).factory())) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR));
            // ------------------------------------------------------------------ configure/register
            server.configureBlocking(false);
            final var serverKey = server.register(selector, SelectionKey.OP_ACCEPT);
            // --------------------------------------------------------- read-quit!-and-close-server
            JavaLangUtils.readLinesAndCallWhenTests(
                    "quit!"::equalsIgnoreCase, // <predicate>
                    () -> {                    // <callable>
                        server.close();
                        assert !serverKey.isValid();
                        selector.wakeup();
                        return null;
                    }
            );
            // ---------------------------------------------------------------------- selection-loop
            while (selector.keys().stream().anyMatch(SelectionKey::isValid)) {
                try {
                    if (selector.select() == 0) {
                        continue;
                    }
                } catch (final IOException ioe) {
                    log.error("failed to select", ioe);
                    continue;
                }
                // --------------------------------------------------------------- selected-key-loop
                for (final var i = selector.selectedKeys().iterator(); i.hasNext(); ) {
                    final var key = i.next();
                    i.remove();
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
                            client.configureBlocking(false).register(
                                    selector,
                                    SelectionKey.OP_READ,
                                    ByteBuffer.allocate(__CalcMessage2.BYTES)
                                            .limit(__CalcMessage2.INDEX_RESULT)
                            );
                        } catch (final IOException ioe) {
                            log.error("failed to configure/register " + client, ioe);
                            cancel(key);
                            continue;
                        }
                    }
                    // ------------------------------------------------------------------------ read
                    if (key.isReadable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var buffer = (ByteBuffer) key.attachment();
                        final var r = channel.read(buffer);
                        if (r == -1) {
                            log.error("premature eof");
                            cancel(key);
                            continue;
                        }
                        assert r >= 0;
                        if (!buffer.hasRemaining()) {
                            key.interestOpsAnd(~SelectionKey.OP_READ);
                            executor.submit(() -> {
                                __CalcMessage2.calculateResult(buffer)
                                        .limit(buffer.capacity())
                                        .position(__CalcMessage2.INDEX_RESULT);
                                key.interestOpsOr(SelectionKey.OP_WRITE);
                                selector.wakeup();
                                return null;
                            });
                        }
                    }
                    // ----------------------------------------------------------------------- write
                    if (key.isWritable()) {
                        final var channel = (SocketChannel) key.channel();
                        final var buffer = (ByteBuffer) key.attachment();
                        final var w = channel.write(buffer);
                        assert w >= 0;
                        if (!buffer.hasRemaining()) {
                            key.interestOpsAnd(~SelectionKey.OP_WRITE); // redundant
                            cancel(key);
                        }
                    }
                } // end-of-selectedKeys-loop
            } // end-of-selection-loop
            executor.shutdown();
            final boolean terminated = executor.awaitTermination(10L, TimeUnit.SECONDS);
            assert terminated : "executor hasn't been terminated";
        }
    }
}
