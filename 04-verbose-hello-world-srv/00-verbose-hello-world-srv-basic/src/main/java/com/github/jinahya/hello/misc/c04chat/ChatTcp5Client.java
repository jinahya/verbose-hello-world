package com.github.jinahya.hello.misc.c04chat;

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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
class ChatTcp5Client extends ChatTcp {

    // @formatter:off
    private static void startWriting(final AsynchronousSocketChannel client,
                                     final BlockingQueue<String> lines) {
        Thread.ofPlatform().daemon().start(() -> {
            try {
                new _ChatMessage.OfBuffer()
                        .message(lines.take())
                        .timestamp(Instant.now())
                        .readyToWriteToServer()
                        .write(client, new CompletionHandler<>() {
                            @Override
                            public void completed(final Integer w, final _ChatMessage.OfBuffer a) {
                                if (!a.hasRemaining()) {
                                    try {
                                        a.message(lines.take())
                                                .timestamp(Instant.now())
                                                .readyToWriteToServer();
                                    } catch (final InterruptedException ie) {
                                        log.error("interrupted while taking a line", ie);
                                        Thread.currentThread().interrupt();
                                        return;
                                    }
                                }
                                a.write(client, this);
                            }
                            @Override
                            public void failed(final Throwable exc, final _ChatMessage.OfBuffer a) {
                                log.error("failed to write", exc);
                            }
                        });
            } catch (final InterruptedException ie) {
                log.error("interrupted while taking a line", ie);
                Thread.currentThread().interrupt();
            }
        });
    }
    // @formatter:on

    // @formatter:on
    private static void startReading(final AsynchronousSocketChannel client) {
        Thread.ofPlatform().daemon().start(() -> {
            new _ChatMessage.OfBuffer()
                    .readyToReadFromServer()
                    .read(client, new CompletionHandler<>() {
                        @Override
                        public void completed(final Integer r, final _ChatMessage.OfBuffer a) {
                            if (r == -1) {
                                log.error("premature eof");
                                return;
                            }
                            if (!a.hasRemaining()) {
                                a.print().readyToReadFromServer();
                            }
                            a.read(client, this);
                        }

                        @Override
                        public void failed(final Throwable exc, final _ChatMessage.OfBuffer a) {
                            log.error("failed to read", exc);
                        }
                    });
        });
    }
    // @formatter:on

    // @formatter:off
    public static void main(String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLoopbackAddress();
        }
        try (var client = AsynchronousSocketChannel.open()) {
            // ----------------------------------------------------------------------------- prepare
            final var latch = new CountDownLatch(1);
            final var lines = new LinkedBlockingQueue<String>(8);
            // -------------------------------- read-quit!/count-down-latch-or-else-offer-to-<lines>
            JavaLangUtils.readLinesAndRunWhenTests(
                    QUIT::equalsIgnoreCase,
                    latch::countDown,
                    l -> {
                        if (l.isBlank()) {
                            return;
                        }
                        if (!lines.offer(l)) {
                            log.error("failed to offer");
                        }
                    }
            );
            // ----------------------------------------------------------------------------- connect
            client.<Void>connect(
                    new InetSocketAddress(addr, PORT),
                    null,
                    new CompletionHandler<>() {
                        @Override public void completed(final Void result, final Void attachment) {
                            startWriting(client, lines);
                            startReading(client);
                        }
                        @Override public void failed(final Throwable exc, final Void attachment) {
                            log.error("failed to connect", exc);
                        }
                    }
            );
            // ------------------------------------------------------------------------- await-latch
            latch.await();
        }
    }
    // @formatter:off

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private ChatTcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
