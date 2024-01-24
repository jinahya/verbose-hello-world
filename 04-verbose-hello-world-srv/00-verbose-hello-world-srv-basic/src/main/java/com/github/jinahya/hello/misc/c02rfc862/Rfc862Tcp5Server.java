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

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.MessageDigest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
class Rfc862Tcp5Server extends _Rfc862Tcp {

    private static void read(final AsynchronousSocketChannel client, final ByteBuffer buffer,
                             final LongAdder bytes, final MessageDigest digest,
                             final CountDownLatch latch) {
        assert buffer.capacity() > 0;
        if (!buffer.hasRemaining()) {
            buffer.clear();
        }
        assert buffer.hasRemaining();
        client.<Void>read(buffer, null, new CompletionHandler<>() { // @formatter:off
            @Override public void completed(final Integer result, final Void attachment) {
                if (result == -1) {
                    logServerBytes(bytes.longValue());
                    latch.countDown();
                    return;
                }
                assert result > 0; // why?
                bytes.add(result);
                // write all bytes back to the client
                if (buffer.position() > 0) {
                    write(client, buffer.flip(), bytes, digest, latch);
                    return;
                }
                // keep reading
                client.read(buffer, attachment, this);
            }
            @Override public void failed(final Throwable exc, final Void attachment) {
                log.error("failed to read", exc);
                latch.countDown();
            } // @formatter:on
        });
    }

    private static void write(final AsynchronousSocketChannel client, final ByteBuffer buffer,
                              final LongAdder bytes, final MessageDigest digest,
                              final CountDownLatch latch) {
        assert buffer.hasRemaining();
        client.<Void>write(buffer, null, new CompletionHandler<>() { // @formatter:off
            @Override public void completed(final Integer result, final Void attachment) {
                assert result > 0; // why?
                JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, result);
                // keep writing while buffer has remaining
                if (buffer.hasRemaining()) {
                    client.write(buffer, attachment, this);
                    return;
                }
                read(client, buffer, bytes, digest, latch);
            }
            @Override public void failed(final Throwable exc, final Void attachment) {
                log.error("failed to write", exc);
                latch.countDown();
            } // @formatter:on
        });
    }

    public static void main(final String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR, 0));
            // ------------------------------------------------------------------------------ accept
            final var latch = new CountDownLatch(1);
            server.accept(newBuffer(), new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final AsynchronousSocketChannel client,
                                                final ByteBuffer attachment) {
                    read(logAccepted(client), attachment, new LongAdder(), newDigest(), latch);
                }
                @Override public void failed(final Throwable exc, final ByteBuffer attachment) {
                    log.error("failed to accept", exc);
                    latch.countDown();
                } // @formatter:on
            });
            // ------------------------------------------------------------------------------- await
            latch.await();
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp5Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
