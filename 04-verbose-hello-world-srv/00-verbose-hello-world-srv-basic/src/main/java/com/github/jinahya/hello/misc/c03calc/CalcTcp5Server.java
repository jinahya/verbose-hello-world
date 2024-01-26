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
class CalcTcp5Server extends _CalcTcp {

    private static void closeUnchecked(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to close " + closeable, ioe);
        }
    }

    // @formatter:off
    private static void write(final AsynchronousSocketChannel client,
                              final __CalcMessage3.OfBuffer message) {
        message.<Void>write(client, null, new CompletionHandler<>() {
            @Override public void completed(final Integer result, final Void attachment) {
                assert result > 0; // why?
                assert !message.hasRemaining();
                closeUnchecked(client);
            }
            @Override public void failed(final Throwable exc, final Void attachment) {
                log.error("failed to write", exc);
                closeUnchecked(client);
            }
        });
    }
    // @formatter:on

    // @formatter:off
    private static void read(final AsynchronousSocketChannel client) {
        final var message = new __CalcMessage3.OfBuffer().readyToReadFromClient();
        message.<Void>read(client, null, new CompletionHandler<>() {
            @Override public void completed(final Integer result, final Void attachment) {
                assert result > 0; // why?
                if (!message.hasRemaining()) {
                    message.calculateResult().readyToWriteToClient();
                    write(client, message);
                    return;
                }
                message.read(client, null, this);
            }
            @Override public void failed(final Throwable exc, final Void attachment) {
                log.error("failed to read", exc);
                closeUnchecked(client);
            }
        });
    }
    // @formatter:on

    // @formatter:off
    public static void main(final String... args) throws Exception {
        final var group = AsynchronousChannelGroup.withThreadPool(
                newExecutorForServer("tcp-5-server-")
        );
        try (var server = AsynchronousServerSocketChannel.open(group)) {
            // ----------------------------------------------------- read-quit!-and-count-down-latch
            final var latch = new CountDownLatch(1);
            JavaLangUtils.readLinesAndRunWhenTests(
                    "quit!"::equalsIgnoreCase,
                    latch::countDown
            );
            // ------------------------------------------------------------------------- set-options
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR, SERVER_BACKLOG));
            // ------------------------------------------------------------------------------ accept
            server.<Void>accept(null, new CompletionHandler<>() {
                @Override public void completed(final AsynchronousSocketChannel result,
                                                final Void attachment) {
                    read(result);
                    server.accept(null, this);
                }
                @Override public void failed(final Throwable exc, final Void attachment) {
                    if (server.isOpen()) {
                        log.error("failed to accept", exc);
                    }
                }
            });
            latch.await();
        }
        // -------------------------------------------------------- shutdown-group/await-termination
        group.shutdown();
        if (!group.awaitTermination(1L, TimeUnit.SECONDS)) {
            log.error("group not terminated");
        }
    }
    // @formatter:on
}
