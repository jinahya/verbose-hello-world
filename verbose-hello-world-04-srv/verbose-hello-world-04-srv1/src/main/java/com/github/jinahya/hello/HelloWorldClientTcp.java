package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-04-srv1
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

import java.net.Socket;
import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class HelloWorldClientTcp
        implements Callable<byte[]> {

    static void clients(final int count, final SocketAddress endpoint,
                        final Consumer<byte[]> consumer)
            throws InterruptedException {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "count(" + count + ") is not positive");
        }
        Objects.requireNonNull(endpoint, "endpoint is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final CountDownLatch latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    final byte[] b = new HelloWorldClientTcp(endpoint).call();
                    consumer.accept(b);
                } catch (final Exception e) {
                    log.error("failed to call for {}", endpoint, e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        if (!latch.await(1L, TimeUnit.MINUTES)) {
            log.warn("latch is still not broken!");
        }
    }

    /**
     * Creates a new instance which connects to specified endpoint.
     *
     * @param endpoint the endpoint to connect.
     */
    HelloWorldClientTcp(final SocketAddress endpoint) {
        super();
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint is null");
    }

    @Override
    public byte[] call() throws Exception {
        final byte[] array = new byte[HelloWorld.BYTES];
        try (Socket socket = new Socket()) {
            socket.setSoTimeout(10000); // 10 secs
            // TODO: Connect to the endpoint and read 'hello, world' bytes into array!
        }
        return array;
    }

    /**
     * The endpoint on which the server is listening.
     */
    private final SocketAddress endpoint;
}
