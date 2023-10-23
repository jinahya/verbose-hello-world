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

import com.github.jinahya.hello.misc._TcpUtils;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;
import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Utils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class Rfc863Tcp5Client {

    public static void main(final String... args) throws Exception {
        try (var client = AsynchronousSocketChannel.open(null)) {
            // -------------------------------------------------------------------------------- BIND
            if (ThreadLocalRandom.current().nextBoolean()) {
                client.bind(new InetSocketAddress(_Rfc86_Constants.HOST, 0));
                _TcpUtils.logBound(client);
            }
            final var latch = new CountDownLatch(1);
            // ----------------------------------------------------------------------------- CONNECT
            client.<Void>connect(
                    _Rfc863Constants.ADDR,      // <remote>
                    null,                       // <attachment>
                    new CompletionHandler<>() { // <handler>
                        @Override
                        public void completed(final Void result, final Void attachment) {
                            _TcpUtils.logConnected(client);
                            final var bytes = new int[1];
                            bytes[0] = _Rfc86_Utils.randomBytes();
                            _Rfc863Utils.logClientBytes(bytes[0]);
                            final var digest = _Rfc863Utils.newDigest();
                            final var buffer = _Rfc86_Utils.newBuffer();
                            final var slice = buffer.slice();
                            ThreadLocalRandom.current().nextBytes(buffer.array());
                            buffer.limit(Math.min(buffer.limit(), bytes[0]));
                            client.<Void>write(
                                    buffer,                              // <src>
                                    _Rfc86_Constants.WRITE_TIMEOUT,      // <timeout>
                                    _Rfc86_Constants.WRITE_TIMEOUT_UNIT, // <unit>
                                    null,
                                    new CompletionHandler<>() {          // <handler>
                                        @Override
                                        public void completed(final Integer result,
                                                              final Void attachment) {
                                            digest.update(
                                                    slice.position(buffer.position() - result)
                                                            .limit(buffer.position())
                                            );
                                            if ((bytes[0] -= result) == 0) {
                                                _Rfc863Utils.logDigest(digest);
                                                latch.countDown();
                                                return;
                                            }
                                            if (!buffer.hasRemaining()) {
                                                ThreadLocalRandom.current()
                                                        .nextBytes(buffer.array());
                                                buffer.clear()
                                                        .limit(Math.min(buffer.limit(), bytes[0]));
                                            }
                                            client.write(
                                                    buffer,                              // <src>
                                                    _Rfc86_Constants.WRITE_TIMEOUT,     // <timeout>
                                                    _Rfc86_Constants.WRITE_TIMEOUT_UNIT, // <unit>
                                                    null,                            // <attachment>
                                                    this                                // <handler>
                                            );
                                        }

                                        @Override
                                        public void failed(final Throwable exc,
                                                           final Void attachment) {
                                            log.error("failed to write", exc);
                                            latch.countDown();
                                        }
                                    }
                            );
                        }

                        @Override
                        public void failed(final Throwable exc, final Void attachment) {
                            log.error("failed to connect", exc);
                            latch.countDown();
                        }
                    }
            );
            final var terminated = latch.await(_Rfc86_Constants.CLIENT_TIMEOUT,
                                               _Rfc86_Constants.CLIENT_TIMEOUT_UNIT);
            assert terminated : "latch hasn't been broken";
        }
    }

    private Rfc863Tcp5Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
