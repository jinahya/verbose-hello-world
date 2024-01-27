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
class Rfc862Tcp5Client extends Rfc862Tcp {

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
            if (Objects.requireNonNull(bytes, "bytes is null").get() < 0) {
                throw new IllegalArgumentException("bytes(" + bytes.get() + ") < 0");
            }
            Objects.requireNonNull(digest, "digest is null");
            if (Objects.requireNonNull(latch, "latch is null").getCount() != 1) {
                throw new IllegalArgumentException("latch.count != 1");
            }
        }

        // ----------------------------------------------------- java.nio.channels.CompletionHandler
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
            log.error("failed to handle; mode: " + attachment, exc);
            assert latch.getCount() == 1L;
            latch.countDown();
        }

        // ----------------------------------------------------------------------------------- write
        private void write() {
            if (!buffer.hasRemaining()) {
                JavaNioByteBufferUtils.randomize(
                        buffer.clear().limit(Math.min(buffer.remaining(), bytes.get()))
                );
            }
            assert buffer.hasRemaining() || bytes.get() == 0;
            client.write(
                    buffer,     // <src>
                    Mode.WRITE, // <attachment>
                    this        // <handler>
            );
        }

        private void writeCompleted(final int w) {
            assert w >= 0; // wny?
            assert w > 0 || bytes.get() == 0;
            JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
            if (bytes.addAndGet(-w) == 0) { // no more bytes to write
                logDigest(digest);
                if (buffer.position() == 0) { // no more bytes to read, either
                    assert latch.getCount() == 1L;
                    latch.countDown();
                    return;
                }
            }
            assert bytes.get() > 0 || buffer.position() > 0;
            if (buffer.position() == 0) { // no bytes to read; keep writing
                write();
                return;
            }
            assert buffer.position() > 0; // read bytes
            buffer.flip();
            read();
        }

        // ------------------------------------------------------------------------------------ read
        private void read() {
            assert buffer.hasRemaining();
            client.read(
                    buffer,    // <dst>
                    Mode.READ, // <attachment>
                    this       // <handler>
            );
        }

        private void readCompleted(final int r) {
            assert r >= -1;
            if (r == -1) {
                failed(new EOFException("unexpected eof"), Mode.READ);
                return;
            }
            assert r > 0; // why?
            if (buffer.hasRemaining()) { // keep reading
                read();
                return;
            }
            assert buffer.position() == buffer.limit(); // !buffer.hasRemaining()
            if (bytes.get() > 0) { // has bytes to write
                buffer.limit(
                        buffer.position() +
                        Math.min(buffer.capacity() - buffer.position(), bytes.get())
                );
                write();
                return;
            }
            assert bytes.get() == 0; // no bytes to write
            assert latch.getCount() == 1L;
            latch.countDown();
        }
    }

    // ---------------------------------------------------------------------------------------------
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
                    final var buffer = newBuffer().limit(0);
                    final var bytes = new AtomicInteger(logClientBytes(newRandomBytes()));
                    final var digest = newDigest();
                    // ----------------------------------------------------------------------- write
                    new Handler(client, buffer, bytes, digest, latch).write();
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
