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
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Thread.currentThread;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

@Slf4j
public class HelloWorldClientUdp {

    private static void handle(final Set<SelectionKey> keys,
                               final SocketAddress endpoint,
                               final Consumer<? super String> consumer,
                               final CountDownLatch latch)
            throws IOException {
        for (final var key : keys) {
            final var channel = (DatagramChannel) key.channel();
            if (key.isWritable()) {
                channel.send(allocate(0), endpoint);
                continue;
            }
            if (key.isReadable()) {
                final var dst = allocate(BYTES);
                channel.receive(dst);
                consumer.accept(new String(dst.array(), UTF_8));
                latch.countDown();
                continue;
            }
            log.error("unhandled key: {}", key);
        }
        keys.clear();
    }

    static void runClients(final int count, final SocketAddress endpoint,
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
                final var client = DatagramChannel.open();
                client.configureBlocking(false);
                client.register(selector, OP_READ | OP_WRITE);
            }
            final CountDownLatch latch = new CountDownLatch(count);
            while (latch.getCount() > 0L && !currentThread().isInterrupted()) {
                if (selector.select() == 0) {
                    continue;
                }
                handle(selector.selectedKeys(), endpoint, consumer, latch);
            } // end-of-while
            if (selector.selectNow() > 0) {
                handle(selector.selectedKeys(), endpoint, consumer, latch);
            }
            try {
                if (!latch.await(1L, TimeUnit.MINUTES)) {
                    log.warn("latch is still not broken!");
                }
            } catch (final InterruptedException ie) {
                log.error("interrupted while awaiting latch", ie);
            }
        }
    }

    private HelloWorldClientUdp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
