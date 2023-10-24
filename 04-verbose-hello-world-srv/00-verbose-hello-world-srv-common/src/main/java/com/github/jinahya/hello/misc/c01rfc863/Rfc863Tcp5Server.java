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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

import static com.github.jinahya.hello.misc._TcpUtils.logAccepted;
import static com.github.jinahya.hello.misc._TcpUtils.logBound;
import static com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants.ACCEPT_TIMEOUT;
import static com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants.ACCEPT_TIMEOUT_UNIT;

@Slf4j
class Rfc863Tcp5Server {

    /**
     * Closes specified channel and count down specified latch.
     *
     * @param channel the socket channel to close.
     * @param latch   the latch to count down.
     */
    private static void closeAndCountDown(final AsynchronousSocketChannel channel,
                                          final CountDownLatch latch) {
        try {
            channel.close();
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to close " + channel, ioe);
        } finally {
            latch.countDown();
        }
    }

    public static void main(final String... args) throws Exception {
        try (var server = AsynchronousServerSocketChannel.open()) {
            // ------------------------------------------------------------------------------- REUSE
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            // -------------------------------------------------------------------------------- BIND
            server.bind(_Rfc863Constants.ADDR, 1);
            logBound(server);
            // ----------------------------------------------------------------------------- RECEIVE
            final var latch = new CountDownLatch(1);
            // ------------------------------------------------------------------------------ accept
            server.<Void>accept(
                    null,                       // <attachment>
                    new CompletionHandler<>() { // <handler>
                        @Override // @formatter:off
                        public void completed(final AsynchronousSocketChannel result,
                                              final Void a) {
                            logAccepted(result);
                            final var digest = _Rfc863Utils.newDigest();
                            final var bytes = new long[1];
                            final var buffer = _Rfc86_Utils.newBuffer();
                            final var slice = buffer.slice();
                            // ---------------------------------------------------------------- read
                            result.<Void>read(
                                    buffer,                             // <dst>
                                    _Rfc86_Constants.READ_TIMEOUT,      // <timeout>
                                    _Rfc86_Constants.READ_TIMEOUT_UNIT, // <unit>
                                    null,                               // <attachment>
                                    new CompletionHandler<>() {         // <handler>
                                        @Override
                                        public void completed(final Integer r, final Void a) {
                                            if (r == -1) {
                                                _Rfc863Utils.logServerBytes(bytes[0]);
                                                _Rfc863Utils.logDigest(digest);
                                                closeAndCountDown(result, latch);
                                                return;
                                            }
                                            bytes[0] += r;
                                            digest.update(
                                                    slice.position(buffer.position() - r)
                                                            .limit(buffer.position())
                                            );
                                            // ------------------------------------------------ read
                                            if (!buffer.hasRemaining()) {
                                                buffer.clear();
                                            }
                                            result.read(
                                                    buffer,                             // <dst>
                                                    _Rfc86_Constants.READ_TIMEOUT,      // <timeout>
                                                    _Rfc86_Constants.READ_TIMEOUT_UNIT, // <unit>
                                                    null,                            // <attachment>
                                                    this                                // <handler>
                                            );
                                        }
                                        @Override
                                        public void failed(final Throwable exc, final Void a) {
                                            log.error("failed to read", exc);
                                            closeAndCountDown(result, latch);
                                        }
                                    }
                            );
                        }
                        @Override
                        public void failed(final Throwable exc, final Void attachment) {
                            log.error("failed to accept", exc);
                        } // @formatter:on
                    }
            );
            final var terminated = latch.await(ACCEPT_TIMEOUT, ACCEPT_TIMEOUT_UNIT);
            assert terminated : "latch hasn't been broken";
        }
    }

    private Rfc863Tcp5Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
