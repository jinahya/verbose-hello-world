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

import com.github.jinahya.hello.util.JavaNioByteBufferUtils;
import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.MessageDigest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
class Rfc862Tcp5Client extends _Rfc862Tcp {

    private static void write(final AsynchronousSocketChannel client, final ByteBuffer buffer,
                              final AtomicInteger bytes, final MessageDigest digest,
                              final CountDownLatch latch) {
        assert !buffer.hasRemaining();
        JavaNioByteBufferUtils.randomize(
                buffer.clear().limit(Math.min(buffer.limit(), bytes.get()))
        );
        assert buffer.hasRemaining() || bytes.get() == 0;
        client.<Void>write(buffer, null, new CompletionHandler<>() { // @formatter:off
            @Override public void completed(final Integer result, final Void attachment) {
                JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, result);
                // shutdown output when no bytes remained to write
                if (bytes.addAndGet(-result) == 0) {
                    logDigest(digest);
                    try {
                        client.shutdownOutput();
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException("failed to shutdown output", ioe);
                    }
                }
                // read written bytes
                if (buffer.position() > 0) {
                    read(client, buffer.flip(), bytes, digest, latch);
                }
            }
            @Override public void failed(final Throwable exc, final Void attachment) {
                log.error("failed to write", exc);
                latch.countDown();
            } // @formatter:on
        });
    }

    private static void read(final AsynchronousSocketChannel client, final ByteBuffer buffer,
                             final AtomicInteger bytes, final MessageDigest digest,
                             final CountDownLatch latch) {
        assert buffer.hasRemaining();
        client.<Void>read(buffer, null, new CompletionHandler<>() { // @formatter:off
            @Override public void completed(final Integer result, final Void attachment) {
                if (result == -1) {
                    throw new UncheckedIOException(new EOFException("unexpected eof"));
                }
                // keep reading until buffer has remaining
                if (buffer.hasRemaining()) {
                    client.read(buffer, null, this);
                    return;
                }
                // write remaining byts to write
                if (bytes.get() > 0) {
                    write(client, buffer, bytes, digest, latch);
                    return;
                }
                // no remaining bytes to read or write
                latch.countDown();
            }
            @Override public void failed(final Throwable exc, final Void attachment) {
                log.error("failed to read", exc);
                latch.countDown();
            } // @formatter:on
        });
    }

    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                logBound(client.bind(new InetSocketAddress(HOST, 0)));
            }
            // ----------------------------------------------------------------------------- prepare
            final var digest = newDigest();
            var bytes = new AtomicInteger(logClientBytes(newRandomBytes()));
            // ----------------------------------------------------------------------------- connect
            final var latch = new CountDownLatch(1);
            client.connect(ADDR, newBuffer().limit(0), new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final Void result, final ByteBuffer attachment) {
                    logConnected(client);
                    write(client, attachment, bytes, digest, latch);
                }
                @Override public void failed(final Throwable exc, final ByteBuffer attachment) {
                    log.error("failed to connect", exc);
                    latch.countDown();
                } // @formatter:on
            });
            latch.await();
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
