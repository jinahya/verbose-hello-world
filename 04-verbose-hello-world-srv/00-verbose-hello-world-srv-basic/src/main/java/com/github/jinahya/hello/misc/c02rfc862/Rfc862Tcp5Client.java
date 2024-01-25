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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SuppressWarnings({
        "java:S4274" // > Replace this assert with a property check.
})
class Rfc862Tcp5Client extends _Rfc862Tcp {

    // @formatter:on
    private record Handler(AsynchronousSocketChannel client, ByteBuffer buffer, AtomicInteger bytes,
                           MessageDigest digest, CountDownLatch latch)
            implements CompletionHandler<Integer, Handler.Mode> {

        private enum Mode {
            READ,
            WRITE
        }

        // -----------------------------------------------------------------------------------------
        private Handler {
            Objects.requireNonNull(client, "client is null");
            if (Objects.requireNonNull(buffer, "buffer is null").capacity() == 0) {
                throw new IllegalArgumentException("buffer.capacity is zero");
            }
            Objects.requireNonNull(bytes, "bytes is null");
            Objects.requireNonNull(digest, "digest is null");
            Objects.requireNonNull(latch, "latch is null");
        }

        // -----------------------------------------------------------------------------------------
        @Override
        public void completed(final Integer result, final Mode attachment) {
            if (attachment == Mode.WRITE) {
                writeCompleted(result);
                return;
            }
            assert attachment == Mode.READ;
            readCompleted(result);
        }

        @Override
        public void failed(final Throwable exc, final Mode attachment) {
            log.error("failed to handle", exc);
            assert latch.getCount() > 0;
            do {
                latch.countDown();
            } while (latch.getCount() > 0);
        }

        // ----------------------------------------------------------------------------------- write
        private void write() {
            if (!buffer.hasRemaining()) {
                JavaNioByteBufferUtils.randomize(
                        buffer.clear().limit(Math.min(buffer.remaining(), bytes.get()))
                );
            }
            client.write(
                    buffer,     // <src>
                    Mode.WRITE, // <attachment>
                    this        // <handler>
            );
        }

        private void writeCompleted(final int w) {
            log.debug("writeCompleted({})", w);
            assert w >= 0;
            JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
            if (bytes.addAndGet(-w) == 0) { // all bytes written
                log.debug("all written");
                logDigest(digest);
                assert latch.getCount() == 2L;
                latch.countDown(); // 2 -> 1
            }
            read();
        }

        // ------------------------------------------------------------------------------------ read
        private void read() {
            client.read(
                    buffer.flip(), // <dst>
                    Mode.READ,     // <attachment>
                    this           // <handler>
            );
        }

        private void readCompleted(final int r) {
            log.debug("readCompleted({})", r);
            assert r >= -1;
            buffer.position(buffer.limit())
                    .limit(buffer.position() + Math.min(buffer.capacity() - buffer.position(),
                                                        bytes.get()));
            if (r == -1) { // reached to an eof
                if (bytes.get() > 0) { // not all bytes has been written, yet
                    failed(new EOFException("unexpected eof"), Mode.READ);
                    return;
                }
                assert bytes.get() == 0;
                assert latch.getCount() == 1L;
                latch.countDown(); // 1 -> 0
                return;
            }
            // ------------------------------------------------------------------------------- write
            write();
        }
    }

    // @formatter:off
    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                logBound(client.bind(new InetSocketAddress(HOST, 0)));
            }
            // ----------------------------------------------------------------------------- connect
            final var latch = new CountDownLatch(2); // <all-written> + <reached-to-an-eof>
            client.<Void>connect(ADDR, null, new CompletionHandler<>() {
                @Override public void completed(final Void result, final Void attachment) {
                    logConnected(client);
                    // --------------------------------------------------------------------- prepare
                    final var buffer = newBuffer().limit(0);
                    final var bytes = new AtomicInteger(logClientBytes(newRandomBytes()));
                    final var digest = newDigest();
                    // ----------------------------------------------------------------------- write
                    new Handler(client, buffer, bytes, digest, latch).write();
                }
                @Override public void failed(final Throwable exc, final Void attachment) {
                    log.error("failed to connect", exc);
                    assert latch.getCount() == 2L;
                    do { latch.countDown(); } while (latch.getCount() > 0L);
                }
            });
            latch.await();
        }
    }
    // @formatter:on

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
