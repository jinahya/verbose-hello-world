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

import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.MINUTES;

@Slf4j
public class HelloWorldClientTcp
        implements Callable<byte[]> {

    static void clients(final int count, final SocketAddress endpoint,
                        final Consumer<? super String> consumer) {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "count(" + count + ") is not positive");
        }
        Objects.requireNonNull(endpoint, "endpoint is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    final var bytes = new HelloWorldClientTcp(endpoint).call();
                    consumer.accept(
                            new String(bytes, StandardCharsets.US_ASCII));
                } catch (final Exception e) {
                    log.error("failed to call for {}", endpoint, e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        try {
            if (!latch.await(1L, MINUTES)) {
                log.warn("latch remained unbroken!");
            }
        } catch (final InterruptedException ie) {
            log.error("interrupted while awaiting latch", ie);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Creates a new instance which communicate with specified server endpoint.
     *
     * @param endpoint the server endpoint.
     */
    HelloWorldClientTcp(final SocketAddress endpoint) {
        super();
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint is null");
    }

    @Override
    public byte[] call() throws Exception {
        try (var socket = new Socket()) {
            socket.setSoTimeout((int) TimeUnit.SECONDS.toMillis(8L));
            socket.connect(endpoint);
            log.debug("[C] connected to {}", socket.getRemoteSocketAddress());
            return socket.getInputStream().readNBytes(HelloWorld.BYTES);
        }
    }

    /**
     * The server endpoint to connect.
     */
    private final SocketAddress endpoint;
}
