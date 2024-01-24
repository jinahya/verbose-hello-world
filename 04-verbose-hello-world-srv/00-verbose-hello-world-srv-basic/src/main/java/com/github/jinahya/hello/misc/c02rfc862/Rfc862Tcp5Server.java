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
import com.github.jinahya.hello.util.JavaSecurityMessageDigestUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.security.MessageDigest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;

@Slf4j
class Rfc862Tcp5Server {

    private static void write(final AsynchronousSocketChannel client, final ByteBuffer buffer,
                              final LongAdder adder, final MessageDigest digest,
                              final CountDownLatch latch) {
        client.<Void>write(
                buffer,                              // <src>
                _Rfc86_Constants.WRITE_TIMEOUT,      // <timeout>
                _Rfc86_Constants.WRITE_TIMEOUT_UNIT, // <unit>
                null,                                // <attachment>
                new CompletionHandler<>() {          // <handler>
                    @Override // @formatter:off
                    public void completed(final Integer result, final Void attachment) {
                        JavaSecurityMessageDigestUtils.updateDigest(digest, buffer, result);
                        if (latch.getCount() == 2L) {
                            read(client, buffer.compact(), adder, digest, latch);
                            return;
                        }
                        assert latch.getCount() == 1L;
                        if (!buffer.hasRemaining()) {
                            log.debug("[server] closing client...");
                            try {
                                client.close();
                            } catch (final IOException ioe) {
                                throw new RuntimeException("failed to close " + client, ioe);
                            }
                            latch.countDown();
                            return;
                        }
                        write(client, buffer, adder, digest, latch);
                    }
                    @Override
                    public void failed(final Throwable exc, final Void attachment) {
                        log.error("failed to write", exc);
                        while (latch.getCount() > 0L) {
                            latch.countDown();
                        }
                    } // @formatter:on
                }
        );
    }

    private static void read(final AsynchronousSocketChannel client, final ByteBuffer buffer,
                             final LongAdder adder, final MessageDigest digest,
                             final CountDownLatch latch) {
        client.<Void>read(
                buffer,                             // <dst>
                _Rfc86_Constants.READ_TIMEOUT,      // <timeout>
                _Rfc86_Constants.READ_TIMEOUT_UNIT, // <unit>
                null,                               // <attachment>
                new CompletionHandler<>() {         // <handler
                    @Override // @formatter:off
                    public void completed(final Integer result, final Void a) {
                        if (result == -1) {
                            log.debug("reached to an eof");
                            latch.countDown();
                        } else {
                            adder.add(result);
                        }
                        write(client, buffer.flip(), adder, digest, latch);
                    }
                    @Override
                    public void failed(final Throwable exc, final Void a) {
                        log.error("failed to read", exc);
                        while (latch.getCount() > 0L) {
                            latch.countDown();
                        }
                    } // @formatter:on
                }
        );
    }

    public static void main(final String... args) throws Exception {
        // ------------------------------------------------------------------------------------ open
        try (var server = AsynchronousServerSocketChannel.open()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(_Rfc862Constants.ADDR, 0);
            _TcpUtils.logBound(server);
            // ----------------------------------------------------------------------------- prepare
            final var digest = _Rfc862Utils.newDigest();
            final var bytes = new LongAdder();
            final var buffer = _Rfc86_Utils.newBuffer();
            assert buffer.capacity() > 0;
            // ------------------------------------------------------------------------------ accept
            final var latch = new CountDownLatch(2);
            server.<Void>accept(null, new CompletionHandler<>() { // @formatter:off
                @Override public void completed(final AsynchronousSocketChannel client, final Void a) {
                    read(_TcpUtils.logAccepted(client), buffer, bytes, digest, latch);
                }
                @Override public void failed(final Throwable exc, final Void a) {
                    log.error("failed to accept", exc);
                    latch.countDown();
                } // @formatter:on
            });
            // ------------------------------------------------------------------------------- await
            final var broken = latch.await(_Rfc86_Constants.SERVER_PROGRAM_TIMEOUT,
                                           _Rfc86_Constants.SERVER_PROGRAM_TIMEOUT_UNIT);
            if (!broken) {
                log.error("latch hasn't been broken");
            }
            _Rfc862Utils.logServerBytes(bytes.longValue());
            _Rfc862Utils.logDigest(digest);
            log.debug("[server] closing server...");
        }
        log.debug("[server] end-of-main");
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc862Tcp5Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
