package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-04-srv2
 * %%
 * Copyright (C) 2018 - 2021 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.wrap;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
final class HelloWorldClientTcp {

    static void clients(final int count, final SocketAddress endpoint,
                        final Consumer<? super String> consumer)
            throws IOException {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "count(" + count + ") is not positive");
        }
        requireNonNull(endpoint, "endpoint is null");
        requireNonNull(consumer, "consumer is null");
        try (var selector = Selector.open()) {
            for (int i = 0; i < count; i++) {
                final var client = SocketChannel.open();
                client.configureBlocking(false);
                if (client.connect(endpoint)) { // connected, immediately
                    log.debug("[C] connected to {}", client.getRemoteAddress());
                    client.register(selector, OP_READ);
                } else {
                    client.register(selector, OP_CONNECT);
                }
            }
            final var latch = new CountDownLatch(count);
            while (latch.getCount() > 0L) {
                if (selector.select(SECONDS.toMillis(1L)) == 0) {
                    continue;
                }
                final var keys = selector.selectedKeys();
                for (final var key : selector.selectedKeys()) {
                    final var channel = (SocketChannel) key.channel();
                    if (key.isConnectable()) { // ready-to-connect
                        try {
                            if (channel.finishConnect()) {
                                log.debug("[C] connected to {}",
                                          channel.getRemoteAddress());
                                key.interestOps(
                                        key.interestOps() & ~OP_CONNECT);
                                channel.register(selector, OP_READ);
                            }
                        } catch (final IOException ioe) {
                            log.error("failed to finish connect", ioe);
                            channel.close(); // key.cancel();
                            latch.countDown();
                        }
                        continue;
                    }
                    if (key.isReadable()) { // ready-to-read
                        final var buffer = wrap(new byte[BYTES]);
                        // TODO: fill buffer from the channel
                        consumer.accept(new String(buffer.array(), US_ASCII));
                        channel.close(); // key.cancel();
                        latch.countDown();
                        continue;
                    }
                    log.warn("unhandled selection key; {}", key);
                }
                keys.clear();
            } // end-of-while
            log.debug("[C] out of loop; {}", selector.keys().size());
        }
    }

    private HelloWorldClientTcp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
