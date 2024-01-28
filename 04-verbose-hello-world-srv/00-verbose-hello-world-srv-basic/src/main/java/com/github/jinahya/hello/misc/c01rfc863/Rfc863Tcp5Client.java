package com.github.jinahya.hello.misc.c01rfc863;

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
class Rfc863Tcp5Client extends Rfc863Tcp {

    // @formatter:off
    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                logBound(client);
            }
            // ----------------------------------------------------------------------------- connect
            final var latch = new CountDownLatch(1);
            client.<Void>connect(ADDR, null, new CompletionHandler<>() {
                @Override public void completed(final Void result, final Void a) {
                    logConnected(client);
                    // --------------------------------------------------------------------- prepare
                    final var bytes = new AtomicInteger(logClientBytes(newRandomBytes()));
                    final var digest = newDigest();
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
                    client.<Void>write(sanitizer.get(), null, new CompletionHandler<>() {
                        @Override public void completed(final Integer w, final Void attachment) {
                            assert w > 0 || bytes.get() == 0; // why?
                            JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
                            if (bytes.addAndGet(-w) == 0) {
                                logDigest(digest);
                                latch.countDown();
                                return;
                            }
                            client.write(
                                    sanitizer.get(), // <src>
                                    null,            // <attachment>
                                    this             // <handler>
                            );
                        }
                        @Override public void failed(final Throwable exc, final Void attachment) {
                            log.error("failed to write", exc);
                            latch.countDown();
                        }
                    });
                }
                @Override public void failed(final Throwable exc, final Void a) {
                    log.error("failed to connect", exc);
                    latch.countDown();
                }
            });
            latch.await();
        }
    }
    // @formatter:on

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
