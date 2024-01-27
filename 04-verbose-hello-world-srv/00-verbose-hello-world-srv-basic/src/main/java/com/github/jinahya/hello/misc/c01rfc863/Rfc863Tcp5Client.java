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

import com.github.jinahya.hello.util.JavaNioByteBufferUtils;
import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.MessageDigest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class Rfc863Tcp5Client extends Rfc863Tcp {

    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(HOST, 0));
                logBound(client);
            }
            final var latch = new CountDownLatch(1);
            // ----------------------------------------------------------------------------- connect
            client.<Void>connect(ADDR, null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final Void result, final Void a) {
                    logConnected(client);
                    // --------------------------------------------------------------------- prepare
                    final var bytes = new AtomicInteger(logClientBytes(newRandomBytes()));
                    final var buffer = JavaNioByteBufferUtils.randomize(newBuffer());
                    buffer.limit(Math.min(buffer.limit(), bytes.get()));
                    // ----------------------------------------------------------------------- write
                    client.write(buffer, newDigest(), new CompletionHandler<>() {
                        @Override public void completed(final Integer w, final MessageDigest a) {
                            JavaSecurityMessageDigestUtils.updateDigest(a, buffer, w);
                            if (bytes.addAndGet(-w) == 0) {
                                logDigest(a);
                                latch.countDown();
                                return;
                            }
                            if (!buffer.hasRemaining()) {
                                JavaNioByteBufferUtils.randomize(
                                        buffer.clear().limit(Math.min(buffer.limit(), bytes.get()))
                                );
                            }
                            client.write(buffer, a, this);
                        }
                        @Override public void failed(final Throwable exc, final MessageDigest a) {
                            log.error("failed to write", exc);
                            latch.countDown();
                        }
                    });
                }
                @Override public void failed(final Throwable exc, final Void a) {
                    log.error("failed to connect", exc);
                    latch.countDown();
                } // @formatter:on
            });
            latch.await();
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
