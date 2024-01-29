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

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp5Client extends CalcTcp {

    private static void closeUnchecked(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException ioe) {
            log.error("failed to close " + closeable, ioe);
        }
    }

    // @formatter:off
    public static void main(final String... args) throws Exception {
        final var group = AsynchronousChannelGroup.withThreadPool(
                newExecutorForClient("tcp-5-client-")
        );
        for (int i = 0; i < REQUEST_COUNT; i++) {
            // -------------------------------------------------------------------------------- open
            final var client = AsynchronousSocketChannel.open(group);
            // ----------------------------------------------------------------------------- connect
            client.<Void>connect(ADDR, null, new CompletionHandler<>() {
                @Override public void completed(final Void result, final Void a) {
                    log.debug("connected");
                    final var message = new _Message.OfBuffer()
                            .randomize()
                            .readyToWriteToServer();
                    final var latch = new CountDownLatch(1);
                    message.write(client, null, new CompletionHandler<Integer, Void>() {
                        @Override public void completed(final Integer w, final Void a) {
                            log.debug("written: {}", w);
                            assert w > 0;
                            if (message.hasRemaining()) {
                                message.write(client, null, this);
                                return;
                            }
                            log.debug("reading...");
                            message.readyToReadFromServer().read(
                                    client, null, new CompletionHandler<Integer, Void>() {
                                        @Override public void completed(Integer r, Void a) {
                                            log.debug("read: {}", r);
                                            assert r > 0;
                                            if (message.hasRemaining()) {
                                                message.read(client, null, this);
                                                return;
                                            }
                                            message.log();
                                            latch.countDown();
                                        }
                                        @Override
                                        public void failed(final Throwable exc, final Void a) {
                                            log.error("failed to read", exc);
                                            latch.countDown();
                                        }
                                    }
                            );
                        }
                        @Override public void failed(final Throwable exc, final Void a) {
                            log.error("failed to write", exc);
                            latch.countDown();
                        }
                    });
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
                    log.error("failed to connect", exc);
                    closeUnchecked(client);
                }
            });
        }
        // -------------------------------------------------------- shutdown-group/await-termination
        group.shutdown();
        if (!group.awaitTermination(1L, TimeUnit.MINUTES)) {
            log.error("group not terminated");
        }
    }
    // @formatter:on
}
