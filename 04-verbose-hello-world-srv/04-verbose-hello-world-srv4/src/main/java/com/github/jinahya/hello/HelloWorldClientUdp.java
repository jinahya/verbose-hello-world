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
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class HelloWorldClientUdp {

    private static void handle(Set<SelectionKey> keys, SocketAddress endpoint,
                               Consumer<? super String> consumer, CountDownLatch latch)
            throws IOException {
        for (var key : keys) {
            var channel = (DatagramChannel) key.channel();
            if (key.isWritable()) {
                channel.send(ByteBuffer.allocate(0), endpoint);
                continue;
            }
            if (key.isReadable()) {
                var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                channel.receive(dst);
                consumer.accept(new String(dst.array(), StandardCharsets.UTF_8));
                latch.countDown();
                continue;
            }
            log.error("unhandled key: {}", key);
        }
        keys.clear();
    }

    static void runClients(int count, SocketAddress endpoint, Consumer<? super String> consumer)
            throws IOException {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "count(" + count + ") is not positive");
        }
        Objects.requireNonNull(endpoint, "endpoint is null");
        Objects.requireNonNull(consumer, "consumer is null");
        try (var selector = Selector.open()) {
            for (int i = 0; i < count; i++) {
                var client = DatagramChannel.open();
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }
            CountDownLatch latch = new CountDownLatch(count);
            while (latch.getCount() > 0L && !Thread.currentThread().isInterrupted()) {
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
            } catch (InterruptedException ie) {
                log.error("interrupted while awaiting latch", ie);
            }
        }
    }

    private HelloWorldClientUdp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
