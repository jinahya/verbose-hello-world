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

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
class CalcTcp5Client extends _CalcTcp {

    // @formatter:off
    private static void read(final AsynchronousSocketChannel client, final ByteBuffer buffer,
                             final CountDownLatch latch) {
        client.<Void>read(buffer, null, new CompletionHandler<>() {
            @Override public void completed(final Integer result, final Void attachment) {
                assert result > 0;
                assert !buffer.hasRemaining();
                __CalcMessage2.log(buffer);
                latch.countDown();
            }
            @Override public void failed(final Throwable exc, final Void attachment) {
                log.error("failed to read", exc);
                latch.countDown();
            }
        });
    }
    // @formatter:on

    // @formatter:off
    private static void write(final AsynchronousSocketChannel client, final ByteBuffer buffer,
                              final CountDownLatch latch) {
        client.<Void>write(buffer, null, new CompletionHandler<>() {
            @Override public void completed(final Integer result, final Void attachment) {
                assert result > 0;
                if (!buffer.hasRemaining()) {
                    buffer.limit(buffer.capacity());
                    assert buffer.remaining() == __CalcMessage2.LENGTH_RESULT;
                    // ------------------------------------------------------------------------ read
                    read(client, buffer, latch);
                    return;
                }
                client.write(buffer, null, this);
            }
            @Override public void failed(final Throwable exc, final Void attachment) {
                log.error("failed to write", exc);
                latch.countDown();
            }
        });
    }
    // @formatter:on

    // @formatter:on
    public static void main(final String... args) throws Exception {
        final var executor = newExecutorForClient("tcp-5-client-");
        final var group = AsynchronousChannelGroup.withThreadPool(executor);
        for (int i = 0; i < CLIENT_COUNT; i++) {
            // -------------------------------------------------------------------------------- open
            try (var client = AsynchronousSocketChannel.open(group)) {
                // ------------------------------------------------------------------ bind(optional)
                if (ThreadLocalRandom.current().nextBoolean()) {
                    client.bind(new InetSocketAddress(HOST, 0));
                }
                // ------------------------------------------------------------------------- connect
                final var latch = new CountDownLatch(1);
                client.connect(
                        ADDR,
                        __CalcMessage2.newRandomizedBufferForClient(),
                        new CompletionHandler<>() {
                            @Override
                            public void completed(final Void result, final ByteBuffer attachment) {
                                // ----------------------------------------------------------- write
                                write(client, attachment, latch);
                            }

                            @Override
                            public void failed(final Throwable exc, final ByteBuffer attachment) {
                                log.error("failed to connect", exc);
                                latch.countDown();
                            }
                        }
                );
                latch.await();
            } catch (final Exception e) {
                if (e instanceof InterruptedException ie) {
                    log.error("interrupted while awaiting the latch", ie);
                    Thread.currentThread().interrupt();
                }
                log.error("failed to request", e);
            }
        }
        // -------------------------------------------------------------------------- shutdown/await
        group.shutdown();
        if (!group.awaitTermination(1L, TimeUnit.SECONDS)) {
            log.error("group not terminated");
        }
        assert executor.isShutdown();
    }
    // @formatter:on
}
