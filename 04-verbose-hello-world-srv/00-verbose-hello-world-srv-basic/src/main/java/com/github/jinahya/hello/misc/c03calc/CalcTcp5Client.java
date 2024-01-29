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

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
        final var requests = new AtomicInteger(REQUEST_COUNT);
        final var index = new AtomicInteger();
        final var latch = new CountDownLatch(1);
        // ---------------------------------------------------------------------------- open/connect
        final var client = AsynchronousSocketChannel.open(group);
        client.connect(ADDR, client, new CompletionHandler<>() {
            @Override
            public void completed(final Void result, AsynchronousSocketChannel c) {
                final var message = new _Message.OfBuffer().randomize().readyToWriteToServer();
                message.write(c, c, new CompletionHandler<>() {
                    @Override
                    public void completed(final Integer w, final AsynchronousSocketChannel c) {
                        assert w > 0;
                        if (message.hasRemaining()) {
                            message.write(c, c, this);
                            return;
                        }
                        message.readyToReadFromServer().read(c, c, new CompletionHandler<>() {
                            @Override public void completed(final Integer r,
                                                            final AsynchronousSocketChannel c) {
                                assert r > 0;
                                if (message.hasRemaining()) {
                                    message.read(c, c, this);
                                    return;
                                }
                                message.log(
                                        index.getAndIncrement());
                                closeUnchecked(c);
                            }
                            @Override public void failed(final Throwable exc,
                                                         final AsynchronousSocketChannel d) {
                                log.error("failed to read", exc);
                                closeUnchecked(d);
                            }
                        });
                    }
                    @Override
                    public void failed(final Throwable exc, final AsynchronousSocketChannel c) {
                        log.error("failed to write", exc);
                        closeUnchecked(c);
                    }
                });
                if (requests.decrementAndGet() > 0) {
                    try {
                        c = AsynchronousSocketChannel.open(group);
                        c.connect(ADDR, c, this);
                    } catch (final IOException ioe) {
                        log.error("failed to open", ioe);
                    }
                    return;
                }
                latch.countDown();
            }
            @Override
            public void failed(final Throwable exc, final AsynchronousSocketChannel client) {
                log.error("failed to connect", exc);
                assert !client.isOpen();
            }
        });
        latch.await();
        // -------------------------------------------------------- shutdown-group/await-termination
        group.shutdown();
        if (!group.awaitTermination(1L, TimeUnit.MINUTES)) {
            log.error("group not terminated");
        }
    }
    // @formatter:on

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private CalcTcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
