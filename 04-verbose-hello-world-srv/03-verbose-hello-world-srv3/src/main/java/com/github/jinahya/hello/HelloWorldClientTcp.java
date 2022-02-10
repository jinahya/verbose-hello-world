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

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
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
import static java.util.concurrent.Executors.newCachedThreadPool;

@Slf4j
final class HelloWorldClientTcp {

    static byte[] readAndAccept(final ReadableByteChannel channel,
                                final Consumer<? super String> consumer)
            throws IOException {
        requireNonNull(channel, "channel is null");
        requireNonNull(consumer, "consumer is null");
        final var array = new byte[BYTES];
        final ByteBuffer buffer = wrap(array);
        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw new EOFException("unexpected end-of-stream");
            }
        }
        consumer.accept(new String(array, US_ASCII));
        return array;
    }

    static void clients(final int count, final SocketAddress endpoint,
                        final Consumer<? super String> consumer)
            throws InterruptedException, IOException {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "count(" + count + ") is not positive");
        }
        requireNonNull(endpoint, "endpoint is null");
        requireNonNull(consumer, "consumer is null");
        final var executor = newCachedThreadPool();
        try (var selector = Selector.open()) {
            final CountDownLatch latch = new CountDownLatch(count);
            for (int i = 0; i < count; i++) {
                final var client = SocketChannel.open();
                client.configureBlocking(false);
                final var key = client.register(selector, OP_CONNECT);
                if (client.connect(endpoint)) { // connected, immediately
                    key.cancel();
                    client.register(selector, OP_READ);
                }
            }
            while (latch.getCount() > 0L) {
                if (selector.select() == 0) {
                    continue;
                }
                final var keys = selector.selectedKeys();
                for (var key : selector.selectedKeys()) {
                    if (key.isConnectable()) { // ready-to-connect
                        key.channel().register(selector, OP_READ);
                        continue;
                    }
                    if (key.isReadable()) { // ready-to-read
                        final var channel = (SocketChannel) key.channel();
                        final var array = new byte[BYTES];
                        final var buffer = wrap(array);
                        while (buffer.hasRemaining()) {
                            if (channel.read(buffer) == -1) {
                                throw new EOFException("unexpected eof");
                            }
                        }
                        consumer.accept(new String(array, US_ASCII));
                        latch.countDown();
                        continue;
                    }
                    log.warn("unhandled selection key; {}", key);
                }
                keys.clear();
            } // end-of-while
        }
    }

    private HelloWorldClientTcp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
