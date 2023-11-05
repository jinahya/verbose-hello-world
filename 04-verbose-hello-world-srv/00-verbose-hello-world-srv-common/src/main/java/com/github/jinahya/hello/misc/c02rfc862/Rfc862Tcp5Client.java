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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import com.github.jinahya.hello.util.JavaSecurityUtils;
import com.github.jinahya.hello.util._TcpUtils;
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

@Slf4j
class Rfc862Tcp5Client {

    private static void read(final CountDownLatch latch, final AsynchronousSocketChannel client,
                             final MessageDigest digest, final int[] bytes,
                             final ByteBuffer buffer) {
        assert bytes[0] >= 0;
        final var limit = buffer.limit();
        buffer.flip();
        assert buffer.hasRemaining(); // why?
        client.<Void>read(
                buffer,
                _Rfc86_Constants.READ_TIMEOUT,
                _Rfc86_Constants.READ_TIMEOUT_UNIT,
                null,
                new CompletionHandler<>() {
                    @Override
                    public void completed(final Integer result, final Void attachment) {
                        buffer.position(buffer.limit()).limit(limit);
                        assert result >= -1;
                        if (result == -1) {
                            if (bytes[0] > 0) {
                                throw new UncheckedIOException(new EOFException("unexpected eof"));
                            }
                            latch.countDown();
                            return;
                        }
                        assert result > 0; // why?
                        if (bytes[0] > 0) {
                            write(latch, client, digest, bytes, buffer);
                            return;
                        }
                        assert bytes[0] == 0;
                        read(latch, client, digest, bytes, buffer);
                    }

                    @Override
                    public void failed(final Throwable exc, final Void attachment) {
                        log.error("failed to read", exc);
                        latch.countDown();
                    }
                }
        );
    }

    private static void write(final CountDownLatch latch, final AsynchronousSocketChannel client,
                              final MessageDigest digest, final int[] bytes,
                              final ByteBuffer buffer) {
        if (!buffer.hasRemaining()) {
            ThreadLocalRandom.current().nextBytes(buffer.array());
            buffer.clear().limit(Math.min(buffer.limit(), bytes[0]));
        }
        final var hasRemaining = buffer.hasRemaining();
        assert hasRemaining || bytes[0] == 0;
        client.<Void>write(
                buffer,
                _Rfc86_Constants.WRITE_TIMEOUT,
                _Rfc86_Constants.WRITE_TIMEOUT_UNIT,
                null,
                new CompletionHandler<>() {
                    @Override
                    public void completed(final Integer result, final Void attachment) {
                        assert result >= 0;
                        assert result > 0 || !hasRemaining;
                        JavaSecurityUtils.updateDigest(digest, buffer, result);
                        bytes[0] -= result;
                        if (bytes[0] == 0) {
                            _Rfc862Utils.logDigest(digest);
                            try {
                                client.shutdownOutput();
                            } catch (final IOException ioe) {
                                throw new UncheckedIOException("failed to shutdown output", ioe);
                            }
                        }
                        read(latch, client, digest, bytes, buffer);
                    }

                    @Override
                    public void failed(final Throwable exc, final Void attachment) {
                        log.error("failed to write", exc);
                        latch.countDown();
                    }
                }
        );
    }

    public static void main(final String... args) throws IOException, InterruptedException {
        final var latch = new CountDownLatch(1);
        // ------------------------------------------------------------------------------------ open
        try (var client = AsynchronousSocketChannel.open()) {
            // ---------------------------------------------------------------------- bind(optional)
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc862Constants.ADDR.getAddress(), 0));
                _TcpUtils.logBound(client);
            }
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            var bytes = new int[] {_Rfc86_Utils.newRandomBytes()};
            assert bytes[0] >= 0;
            _Rfc862Utils.logClientBytes(bytes[0]);
            final var buffer = _Rfc86_Utils.newBuffer();
            assert buffer.hasArray();
            buffer.position(buffer.limit()); // for what?
            assert !buffer.hasRemaining();
            // ---------------------------------------------------------------------- connect(async)
            client.<Void>connect(
                    _Rfc862Constants.ADDR,
                    null,
                    new CompletionHandler<>() {
                        @Override
                        public void completed(final Void result, final Void attachment) {
                            _TcpUtils.logConnectedUnchecked(client);
                            write(latch, client, digest, bytes, buffer);
                        }

                        @Override
                        public void failed(final Throwable exc, final Void attachment) {
                            log.error("failed to connect", exc);
                            latch.countDown();
                        }
                    }
            );
            // ------------------------------------------------------------------------- await-latch
            final var broken = latch.await(_Rfc86_Constants.CLIENT_PROGRAM_TIMEOUT,
                                           _Rfc86_Constants.CLIENT_PROGRAM_TIMEOUT_UNIT);
            if (!broken) {
                log.error("latch hasn't been broken");
            }
        }
    }

    private Rfc862Tcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
