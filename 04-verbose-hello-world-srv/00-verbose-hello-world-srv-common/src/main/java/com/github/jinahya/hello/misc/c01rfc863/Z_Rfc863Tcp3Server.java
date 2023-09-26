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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
class Z_Rfc863Tcp3Server {

    // @formatter:on
    public static void main(final String... args) throws Exception {
        final var group = AsynchronousChannelGroup.withThreadPool(Executors.newCachedThreadPool());
        try (var server = AsynchronousServerSocketChannel.open(group)) {
            server.setOption(StandardSocketOptions.SO_REUSEADDR, Boolean.TRUE);
            server.setOption(StandardSocketOptions.SO_REUSEPORT, Boolean.TRUE);
            server.bind(_Rfc863Constants.ADDR);
            log.info("bound to {}", server.getLocalAddress());
            JavaLangUtils.readLinesAndCloseWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    group::shutdownNow,            // <closeable>
                    null                           // <consumer>
            );
            server.accept(
                    null,
                    new CompletionHandler<AsynchronousSocketChannel, Void>() {
                        @Override
                        public void completed(final AsynchronousSocketChannel client,
                                              final Void a) {
                            final Supplier<AsynchronousSocketChannel> closer = () -> {
                                try {
                                    client.close();
                                } catch (final IOException ioe) {
                                    log.error("failed to close {}", client, ioe);
                                }
                                return null;
                            };
                            final var buffer =
                                    ByteBuffer.allocate(Z__Rfc863Constants.SERVER_BUFLEN);
                            client.<Void>read(
                                    buffer,
                                    null,
                                    new CompletionHandler<>() {
                                        @Override
                                        public void completed(final Integer r, final Void a) {
                                            if (r == -1) {
                                                closer.get();
                                                return;
                                            }
                                            if (!buffer.hasRemaining()) {
                                                buffer.clear();
                                            }
                                            client.read(buffer, null, this);
                                        }

                                        @Override
                                        public void failed(final Throwable exc, final Void a) {
                                            log.error("failed to read", exc);
                                            closer.get();
                                        }
                                    }
                            );
                            server.accept(null, this);
                        }

                        @Override
                        public void failed(final Throwable exc, final Void attachment) {
                            log.debug("failed to accept", exc);
                        }
                    }
            );
            final var terminated = group.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            assert terminated : "group hasn't been terminated";
        }
        // @formatter:off
    }

    private Z_Rfc863Tcp3Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
