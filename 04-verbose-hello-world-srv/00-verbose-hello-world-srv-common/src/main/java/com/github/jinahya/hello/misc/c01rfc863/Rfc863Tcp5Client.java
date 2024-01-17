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
import com.github.jinahya.hello.util.JavaSecurityUtils;
import com.github.jinahya.hello.util._ExcludeFromCoverage_FailingCase;
import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import com.github.jinahya.hello.util._TcpUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp5Client {

    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open()) {
            // -------------------------------------------------------------------------------- BIND
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc863Constants.ADDR.getAddress(), 0));
                _TcpUtils.logBound(client);
            }
            final var latch = new CountDownLatch(1);
            // ----------------------------------------------------------------------------- CONNECT
            client.<Void>connect(_Rfc863Constants.ADDR, null, new CompletionHandler<>() {
                @Override // @formatter:off
                public void completed(final Void result, final Void attachment) {
                    _TcpUtils.logConnectedUnchecked(client);
                    final var bytes = new int[] {
                            _Rfc863Utils.logClientBytes(_Rfc86_Utils.newRandomBytes())
                    };
                    final var digest = _Rfc863Utils.newDigest();
                    final var buffer = _Rfc86_Utils.newBuffer();
                    ThreadLocalRandom.current().nextBytes(buffer.array());
                    buffer.limit(Math.min(buffer.limit(), bytes[0]));
                    client.<Void>write(
                            buffer,                              // <src>
                            _Rfc86_Constants.WRITE_TIMEOUT,      // <timeout>
                            _Rfc86_Constants.WRITE_TIMEOUT_UNIT, // <unit>
                            null,
                            new CompletionHandler<>() {          // <handler>
                                @Override
                                public void completed(final Integer result, final Void a) {
                                    JavaSecurityUtils.updateDigest(digest, buffer, result);
                                    if ((bytes[0] -= result) == 0) {
                                        _Rfc863Utils.logDigest(digest);
                                        latch.countDown();
                                        return;
                                    }
                                    if (!buffer.hasRemaining()) {
                                        ThreadLocalRandom.current().nextBytes(buffer.array());
                                        buffer.clear().limit(Math.min(buffer.limit(), bytes[0]));
                                    }
                                    client.write(
                                            buffer,                                 // <src>
                                            _Rfc86_Constants.WRITE_TIMEOUT,     // <timeout>
                                            _Rfc86_Constants.WRITE_TIMEOUT_UNIT,   // <unit>
                                            null,                            // <attachment>
                                            this                                // <handler>
                                    );
                                }
                                @_ExcludeFromCoverage_FailingCase
                                @Override
                                public void failed(final Throwable exc, final Void a) {
                                    log.error("failed to write", exc);
                                    latch.countDown();
                                }
                            }
                    );
                }
                @_ExcludeFromCoverage_FailingCase
                @Override
                public void failed(final Throwable exc, final Void attachment) {
                    log.error("failed to connect", exc);
                    latch.countDown();
                } // @formatter:on
            });
            final var terminated = latch.await(_Rfc86_Constants.CLIENT_PROGRAM_TIMEOUT,
                                               _Rfc86_Constants.CLIENT_PROGRAM_TIMEOUT_UNIT);
            assert terminated : "latch hasn't been broken";
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc863Tcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
