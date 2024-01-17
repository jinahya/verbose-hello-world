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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.MessageDigest;
import java.util.concurrent.CountDownLatch;

@Slf4j
class Rfc862Tcp5Server {

    private static void write(final CountDownLatch latch, final MessageDigest digest,
                              final long[] bytes, final ByteBuffer buffer,
                              final AsynchronousSocketChannel client, final boolean read) {
        buffer.flip();
        final var hasRemaining = buffer.hasRemaining();
        client.<Void>write(
                buffer,                              // <src>
                _Rfc86_Constants.WRITE_TIMEOUT,      // <timeout>
                _Rfc86_Constants.WRITE_TIMEOUT_UNIT, // <unit>
                null,                                // <attachment>
                new CompletionHandler<>() {          // <handler>
                    @Override // @formatter:off
                    public void completed(final Integer result, final Void attachment) {
                        assert result >= 0;
                        assert result > 0 || !hasRemaining;
                        JavaSecurityUtils.updateDigest(digest, buffer, result);
                        buffer.compact();
                        if (read) {
                            read(latch, digest, bytes, buffer, client);
                            return;
                        }
                        if (buffer.position() == 0) {
                            _Rfc862Utils.logServerBytes(bytes[0]);
                            _Rfc862Utils.logDigest(digest);
                            try {
                                client.close();
                            } catch (final IOException ioe) {
                                throw new UncheckedIOException("failed to close " + client, ioe);
                            }
                            latch.countDown();
                            return;
                        }
                        write(latch, digest, bytes, buffer, client, read);
                    }
                    @Override
                    public void failed(final Throwable exc, final Void attachment) {
                        log.error("failed to write", exc);
                        latch.countDown();
                    } // @formatter:on
                }
        );
    }

    private static void read(final CountDownLatch latch, final MessageDigest digest,
                             final long[] bytes, final ByteBuffer buffer,
                             final AsynchronousSocketChannel client) {
        client.<Void>read(
                buffer,                             // <dst>
                _Rfc86_Constants.READ_TIMEOUT,      // <timeout>
                _Rfc86_Constants.READ_TIMEOUT_UNIT, // <unit>
                null,                               // <attachment>
                new CompletionHandler<>() {         // <handler
                    @Override // @formatter:off
                    public void completed(final Integer result, final Void attachment) {
                        assert result >= -1;
                        final var eof = result == -1;
                        if (!eof) {
                            bytes[0] += result;
                        }
                        write(latch, digest, bytes, buffer, client, !eof);
                    }
                    @Override
                    public void failed(final Throwable exc, final Void attachment) {
                        log.error("failed to read", exc);
                        latch.countDown();
                    } // @formatter:on
                }
        );
    }

    public static void main(final String... args) throws Exception {
        final var latch = new CountDownLatch(1);
        // ------------------------------------------------------------------------------------ open
        try (var server = AsynchronousServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(_Rfc862Constants.ADDR, 0);
            _TcpUtils.logBound(server);
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            final var bytes = new long[] {0L};
            final var buffer = _Rfc86_Utils.newBuffer();
            assert buffer.capacity() > 0;
            // ------------------------------------------------------------------------------ accept
            server.<Void>accept(
                    null,                       // <attachment>
                    new CompletionHandler<>() { // <handler>
                        @Override
                        public void completed(final AsynchronousSocketChannel result,
                                              final Void attachment) {
                            read(latch, digest, bytes, buffer, result);
                        }

                        @Override
                        public void failed(final Throwable exc, final Void attachment) {
                            log.error("failed to accept", exc);
                            latch.countDown();
                        }
                    }
            );
            // ------------------------------------------------------------------------------- await
            final var broken = latch.await(_Rfc86_Constants.SERVER_PROGRAM_TIMEOUT,
                                           _Rfc86_Constants.SERVER_PROGRAM_TIMEOUT_UNIT);
            if (!broken) {
                log.error("latch hasn't been broken");
            }
        }
    }

    private Rfc862Tcp5Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
