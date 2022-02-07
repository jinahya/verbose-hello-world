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
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.MINUTES;

@Slf4j
public class HelloWorldClientTcp
        implements Callable<byte[]> {

    static void clients(final int count, final SocketAddress endpoint,
                        final Consumer<? super String> consumer)
            throws InterruptedException {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "count(" + count + ") is not positive");
        }
        requireNonNull(endpoint, "endpoint is null");
        requireNonNull(consumer, "consumer is null");
        final var latch = new CountDownLatch(count);
        for (int i = 0; i < count; i++) {
            new Thread(() -> {
                try {
                    final var b = new HelloWorldClientTcp(endpoint).call();
                    consumer.accept(new String(b, US_ASCII));
                } catch (final Exception e) {
                    log.error("failed to call for {}", endpoint, e);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        if (!latch.await(1L, MINUTES)) {
            log.warn("latch is still not broken!");
        }
    }

    /**
     * Creates a new instance which communicate with specified server endpoint.
     *
     * @param endpoint the server endpoint.
     */
    HelloWorldClientTcp(final SocketAddress endpoint) {
        super();
        this.endpoint = requireNonNull(endpoint, "endpoint is null");
    }

    @Override
    public byte[] call() throws Exception {
        try (var socket = new Socket()) {
            socket.setSoTimeout(10000); // 10 secs
            socket.connect(endpoint);
            log.debug("[C] connected to {}", socket.getRemoteSocketAddress());
            final var array = new byte[BYTES];
            for (var offset = 0; (offset < array.length); ) {
                final var length = array.length - offset;
                final var bytes = socket.getInputStream().read(
                        array, offset, length);
                if (bytes == -1) {
                    throw new EOFException("unexpected end-of-stream");
                }
                offset += bytes;
            }
            return array;
        }
    }

    /**
     * The server endpoint.
     */
    private final SocketAddress endpoint;
}
