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
import java.util.function.Consumer;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class HelloWorldClientTcp
        implements IHelloWorldClient {

    static void clients(final int count, final SocketAddress endpoint,
                        final Consumer<? super String> consumer)
            throws InterruptedException {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "count(" + count + ") is not positive");
        }
        requireNonNull(endpoint, "endpoint is null");
        requireNonNull(consumer, "consumer is null");
        for (int i = 0; i < count; i++) {
            try {
                final var bytes = new HelloWorldClientTcp(endpoint).call();
                consumer.accept(new String(bytes, US_ASCII));
            } catch (final Exception e) {
                log.error("failed to call for {}", endpoint, e);
            }
        }
    }

    /**
     * Creates a new instance which connects to specified endpoint.
     *
     * @param endpoint the endpoint to connect.
     */
    HelloWorldClientTcp(final SocketAddress endpoint) {
        super();
        this.endpoint = requireNonNull(endpoint, "endpoint is null");
    }

    @Override
    public byte[] call() throws Exception {
        try (var socket = new Socket()) {
            socket.setSoTimeout((int) SECONDS.toMillis(8L));
            // TODO: Connect to the endpoint, read 12 bytes, and return it.
            return new byte[0];
        }
    }

    /**
     * The server endpoint.
     */
    private final SocketAddress endpoint;
}
