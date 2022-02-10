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

import static com.github.jinahya.hello.HelloWorldClientTcp.readAndAccept;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.MINUTES;

@Slf4j
class HelloWorldClientTcp_
        implements IHelloWorldClient {

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
                executor.submit(
                        new HelloWorldClientTcp_(endpoint, selector, consumer));
            }
            if (!latch.await(1L, MINUTES)) {
                log.warn("latch is still not broken!");
            }
        }
    }

    /**
     * Creates a new instance with specified server endpoint.
     *
     * @param endpoint the server endpoint to connect.
     * @param selector a selector.
     */
    HelloWorldClientTcp_(final SocketAddress endpoint,
                         final Selector selector,
                         final Consumer<? super String> consumer) {
        super();
        this.endpoint = requireNonNull(endpoint, "endpoint is null");
        this.selector = requireNonNull(selector, "selector is null");
        this.consumer = requireNonNull(consumer, "consumer is null");
    }

    @Override
    public byte[] call() throws Exception {
        try (var client = SocketChannel.open()) {
            client.configureBlocking(false);
            final var key = client.register(selector, OP_CONNECT, this);
            if (client.connect(endpoint)) {
                key.cancel();
                final var channel = (SocketChannel) key.channel();
                return readAndAccept(channel, consumer);
            }
            while (!key.isConnectable()) {
                Thread.yield();
            }
            final var channel = (SocketChannel) key.channel();
            return readAndAccept(channel, consumer);
        }
    }

    /**
     * The server endpoint.
     */
    private final SocketAddress endpoint;

    private final Selector selector;

    private final Consumer<? super String> consumer;
}
