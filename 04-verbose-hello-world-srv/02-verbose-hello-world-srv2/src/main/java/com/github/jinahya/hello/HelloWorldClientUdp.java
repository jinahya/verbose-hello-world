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

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
class HelloWorldClientUdp
        implements Callable<byte[]> {

    static void clients(final int count, final SocketAddress endpoint,
                        final Consumer<? super String> consumer)
            throws InterruptedException {
        requireNonNull(endpoint, "endpoint is null");
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "count(" + count + ") is not positive");
        }
        requireNonNull(consumer, "consumer is null");
        final var latch = new CountDownLatch(count);
        for (var i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    final var bytes = new HelloWorldClientUdp(endpoint).call();
                    consumer.accept(new String(bytes, US_ASCII));
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
    HelloWorldClientUdp(final SocketAddress endpoint) {
        super();
        this.endpoint = requireNonNull(endpoint, "endpoint is null");
    }

    @Override
    public byte[] call() throws Exception {
        try (var socket = new DatagramSocket()) {
            log.debug("[C] local socket address: {}",
                      socket.getLocalSocketAddress());
            socket.send(new DatagramPacket(new byte[0], 0, endpoint));
            log.debug("[C] send to {}", endpoint);
            final var array = new byte[BYTES];
            final var packet = new DatagramPacket(array, array.length);
            socket.setSoTimeout((int) SECONDS.toMillis(8L));
            socket.receive(packet);
            assert packet.getLength() == array.length;
            log.debug("[C] received from {}", packet.getSocketAddress());
            return array;
        }
    }

    /**
     * The endpoint on which the server is listening.
     */
    private final SocketAddress endpoint;
}
