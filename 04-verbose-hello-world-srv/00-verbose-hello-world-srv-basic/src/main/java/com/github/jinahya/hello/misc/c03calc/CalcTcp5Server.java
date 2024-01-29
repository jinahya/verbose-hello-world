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
import java.io.UncheckedIOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp5Server extends CalcTcp {

    private static void closeUnchecked(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to close " + closeable, ioe);
        }
    }

    // @formatter:off
    public static void main(final String... args) throws Exception {
        final var group = AsynchronousChannelGroup.withThreadPool(
                newExecutorForServer("tcp-5-server-")
        );
        try (var server = AsynchronousServerSocketChannel.open(group)) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            try {
                server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            } catch (final IOException ioe) {
                log.error("failed to set SO_REUSEPORT", ioe);
            }
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR, SERVER_BACKLOG));
            // -------------------------------------------------- read-quit!/count-down-server-latch
            final var serverLatch = new CountDownLatch(1);
            JavaLangUtils.readLinesAndRunWhenTests(
                    "quit!"::equalsIgnoreCase,
                    serverLatch::countDown
            );
            // ------------------------------------------------------------------------------ accept
            server.<Void>accept(null, new CompletionHandler<>() {
                @Override
                public void completed(final AsynchronousSocketChannel client, final Void a) {
                    final var message = new _Message.OfBuffer().readyToReadFromClient();
                    final var latch = new CountDownLatch(1);
                    message.read(client, null, new CompletionHandler<Integer, Void>() {
                        @Override public void completed(final Integer r, final Void a) {
                            if (r == -1) {
                                log.error("premature eof");
                                latch.countDown();
                                return;
                            }
                            if (message.hasRemaining()) {
                                message.read(client, null, this);
                                return;
                            }
                            message.calculateResult().readyToWriteToClient().write(
                                    client,
                                    null,
                                    new CompletionHandler<Integer, Void>() {
                                        @Override
                                        public void completed(final Integer w, final Void a) {
                                            assert w > 0;
                                            if (message.hasRemaining()) {
                                                message.write(client, null, this);
                                                return;
                                            }
                                            latch.countDown();
                                        }
                                        @Override
                                        public void failed(final Throwable exc, final Void a) {
                                            log.debug("failed to write", exc);
                                            latch.countDown();
                                        }
                                    }
                            );
                        }
                        @Override public void failed(final Throwable exc, final Void a) {
                            log.error("failed to read", exc);
                            latch.countDown();
                        }
                    });
                    // ------------------------------------------------------------- accept-again!!!
                    if (server.isOpen()) {
                        server.accept(null, this);
                    }
                    // ---------------------------------------------------- await-latch/close-client
                    try {
                        latch.await();
                    } catch (final InterruptedException ie) {
                        log.error("interrupted while awaiting the latch", ie);
                        Thread.currentThread().interrupt();
                    } finally {
                        closeUnchecked(client);
                    }
                }
                @Override public void failed(final Throwable exc, final Void a) {
                    if (server.isOpen()) {
                        log.error("failed to accept", exc);
                    }
                }
            });
            serverLatch.await();
        }
        // -------------------------------------------------------- shutdown-group/await-termination
        group.shutdown();
        if (!group.awaitTermination(1L, TimeUnit.SECONDS)) {
            log.error("group not terminated");
        }
    }
    // @formatter:on
}
