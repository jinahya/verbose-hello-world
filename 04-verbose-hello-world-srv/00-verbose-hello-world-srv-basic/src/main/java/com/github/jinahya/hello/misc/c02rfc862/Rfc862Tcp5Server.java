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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
@SuppressWarnings({
        "java:S4274" // > Replace this assert with a proper check
})
class Rfc862Tcp5Server extends _Rfc862Tcp {

    static int sent = 0; // TODO: remove!

    // @formatter:on
    private record Handler(AsynchronousSocketChannel client, ByteBuffer buffer, LongAdder bytes,
                           MessageDigest digest, CountDownLatch latch)
            implements CompletionHandler<Integer, Handler.Mode> {

        private enum Mode {
            READ,
            WRITE
        }

        // -----------------------------------------------------------------------------------------
        private Handler {
            Objects.requireNonNull(client, "client is null");
            if (Objects.requireNonNull(buffer, "buffer is null").remaining() == 0) {
                throw new IllegalArgumentException("buffer.remaining is zero");
            }
            if (Objects.requireNonNull(bytes, "bytes is null").longValue() != 0L) {
                throw new IllegalArgumentException("bytes.value != 0");
            }
            Objects.requireNonNull(digest, "digest is null");
            if (Objects.requireNonNull(latch, "latch is null").getCount() != 2L) {
                throw new IllegalArgumentException("latch.count != 2");
            }
        }

        // -----------------------------------------------------------------------------------------
        @Override
        public void completed(final Integer result, final Mode attachment) {
            if (attachment == Mode.READ) {
                readCompleted(result);
                return;
            }
            assert attachment == Mode.WRITE;
            writeCompleted(result);
        }

        @Override
        public void failed(final Throwable exc, final Mode attachment) {
            log.error("failed to handle; mode: " + attachment, exc);
            assert latch.getCount() > 0;
            do {
                latch.countDown();
            } while (latch.getCount() > 0);
        }

        // ------------------------------------------------------------------------------------ read
        private void read() {
            assert buffer.hasRemaining();
            client.read(buffer, Mode.READ, this);
        }

        private void readCompleted(final int r) {
            assert r >= -1;
            if (r == -1) { // reached to an eof
                assert latch.getCount() == 2L;
                latch.countDown(); // 2 -> 1
            } else {
                assert r > 0; // why?
                bytes.add(r);
            }
            write();
        }

        // ----------------------------------------------------------------------------------- write
        private void write() {
            buffer.flip();
            client.write(buffer, Mode.WRITE, this);
        }

        private void writeCompleted(final int w) {
            assert w >= 0;
            sent += w;
            JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, w);
            buffer.compact();
            if (buffer.position() > 0) { // has remaining bytes to write; keep writing
                write();
                return;
            }
            assert buffer.position() == 0;
            if (latch.getCount() == 1L) { // reached to an eof; no need to read anymore
                // ----------------------------------------------------------------------------- log
                log.debug("[server] closing client...");
                try {
                    client.close();
                    assert !client.isOpen();
                } catch (final IOException ioe) {
                    log.error("failed to close " + client, ioe);
                }
                logServerBytes(bytes.longValue());
                logDigest(digest);
                log.debug("sent: {}", sent);
                assert latch.getCount() == 1L;
                latch.countDown(); // 1 -> 0
                return;
            }
            assert latch.getCount() == 2L; // not reached to an eof, yet; read more
            read();
        }
    }
    // @formatter:on

    // @formatter:off
    public static void main(final String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            logBound(server.bind(ADDR, 0));
            // ------------------------------------------------------------------------------ accept
            final var latch = new CountDownLatch(2);
            server.<Void>accept(null, new CompletionHandler<>() {
                @Override public void completed(final AsynchronousSocketChannel result,
                                                final Void attachment) {
                    logAccepted(result);
                    final var buffer = newBuffer();
                    final var bytes = new LongAdder();
                    final var digest = newDigest();
                    new Handler(result, buffer, bytes, digest, latch).read();
                }
                @Override public void failed(final Throwable exc, final Void attachment) {
                    log.error("failed to accept", exc);
                    assert latch.getCount() == 2L;
                    latch.countDown();
                    latch.countDown();
                }
            });
            latch.await();
            log.debug("[server] end-of-try");
        }
        log.debug("[server] end-of-main");
    }
    // @formatter:on

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp5Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}