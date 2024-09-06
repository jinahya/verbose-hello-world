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

import java.io.Closeable;
import java.io.IOException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
class Rfc863Tcp5Server extends Rfc863Tcp {

    private static void closeUnchecked(final Closeable closeable) {
        try {
            closeable.close();
        } catch (final IOException ioe) {
            log.error("failed to close " + closeable, ioe);
        }
    }

    // @formatter:off
    public static void main(final String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR, 1));
            // ------------------------------------------------------------------------------ accept
            final var latch = new CountDownLatch(1);
            server.<Void>accept(null, new CompletionHandler<>() {
                @Override
                public void completed(final AsynchronousSocketChannel client, final Void a) {
                    logAccepted(client);
                    final var digest = newDigest();
                    final var bytes = new AtomicLong();
                    final var buffer = newBuffer();
                    // ------------------------------------------------------------------------ read
                    client.<Void>read(buffer, null, new CompletionHandler<>() {
                        @Override
                        public void completed(final Integer result, final Void attachment) {
                            if (result == -1) {
                                logServerBytes(bytes.get());
                                logDigest(digest);
                                closeUnchecked(client);
                                latch.countDown();
                                return;
                            }
                            JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, result);
                            bytes.addAndGet(result);
                            if (!buffer.hasRemaining()) {
                                buffer.clear();
                            }
                            client.read(buffer, null, this);
                        }
                        @Override public void failed(final Throwable exc, final Void attachment) {
                            log.error("failed to read", exc);
                            closeUnchecked(client);
                            latch.countDown();
                        }
                    });
                }
                @Override public void failed(final Throwable exc, final Void attachment) {
                    log.error("failed to accept", exc);
                    latch.countDown();
                }
            });
            latch.await();
        }
    }
    // @formatter:on

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp5Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
