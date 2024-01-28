package com.github.jinahya.hello.misc.c02rfc862;

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

import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

@Slf4j
class Rfc862Tcp5Client extends Rfc862Tcp {

    @SuppressWarnings({
            "java:S3776"
    })
    // @formatter:off
    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                logBound(client.bind(new InetSocketAddress(HOST, 0)));
            }
            // ----------------------------------------------------------------------------- connect
            final var latch = new CountDownLatch(1);
            client.<Void>connect(ADDR, null, new CompletionHandler<>() {
                @Override public void completed(final Void result, final Void attachment) {
                    logConnected(client);
                    // --------------------------------------------------------------------- prepare
                    final var digest = newDigest();
                    final var bytes = new AtomicInteger(logClientBytes(newRandomBytes()));
                    final var buffer = newBuffer().limit(0);
                    final Supplier<ByteBuffer> sanitizer = () -> {
                        if (!buffer.hasRemaining()) {
                            ThreadLocalRandom.current().nextBytes(buffer.array());
                            buffer.clear().limit(Math.min(buffer.capacity(), bytes.get()));
                            assert buffer.hasRemaining() || bytes.get() == 0;
                        }
                        return buffer;
                    };
                    // ----------------------------------------------------------------------- write
                    abstract class Handler implements CompletionHandler<Integer, Handler> {
                        @Override
                        public final void failed(final Throwable exc, final Handler attachment) {
                            log.error("failed to handler in " + getClass(), exc);
                            latch.countDown();
                        }
                    }
                    class WriteHandler extends Handler {
                        @Override
                        public void completed(final Integer result, final Handler attachment) {
                            assert result > 0 || bytes.get() == 0;
                            JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, result);
                            if (bytes.addAndGet(-result) == 0) {
                                logDigest(digest);
                            }
                            if (buffer.position() > 0) {
                                buffer.flip();
                                client.read(
                                        buffer,    // <dst>
                                        this,      // <attachment>
                                        attachment // <handler>
                                );
                                return;
                            }
                            client.write(
                                    sanitizer.get(), // <src>
                                    attachment,      // <attachment>
                                    this             // <handler>
                            );
                        }
                    }
                    class ReadHandler extends Handler {
                        @Override
                        public void completed(final Integer result, final Handler attachment) {
                            assert result > 0;
                            if (buffer.hasRemaining()) {
                                client.read(buffer, attachment, this);
                                return;
                            }
                            if (bytes.get() > 0) {
                                client.write(
                                        sanitizer.get(), // <src>
                                        this,            // <attachment>
                                        attachment       // <handler>
                                );
                                return;
                            }
                            assert bytes.get() == 0; // no need to write, either
                            latch.countDown();
                        }
                    }
                    client.write(
                            sanitizer.get(),   // <src>
                            new ReadHandler(), // <attachment>
                            new WriteHandler() // <handler>
                    );
                }
                @Override public void failed(final Throwable exc, final Void attachment) {
                    log.error("failed to connect", exc);
                    latch.countDown();
                }
            });
            latch.await();
        }
    }
    // @formatter:on

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
