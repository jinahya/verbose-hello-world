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

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
class HelloWorldClientTcp {

    /**
     * Connects to specified endpoint, reads {@value com.github.jinahya.hello.HelloWorld#BYTES}
     * bytes, decodes those bytes into a string using {@link StandardCharsets#US_ASCII US_ASCII},
     * and accepts the string to specified consumer.
     *
     * @param endpoint the endpoint to connect.
     * @param consumer the consumer accepts the string.
     * @throws IOException if an I/O error occurs.
     */
    static void connect(SocketAddress endpoint, Consumer<? super String> consumer)
            throws IOException {
        Objects.requireNonNull(endpoint, "endpoint is null");
        Objects.requireNonNull(consumer, "consumer is null");
        try (var client = new Socket()) {                                           // <1>
            client.connect(endpoint);                                               // <2>
            log.debug("[C] connected to {}", client.getRemoteSocketAddress());
            var array = new byte[HelloWorld.BYTES];                                 // <3>
            var bytes = client.getInputStream().readNBytes(array, 0, array.length); // <4>
            assert bytes == HelloWorld.BYTES;
            var string = new String(array, 0, bytes, StandardCharsets.US_ASCII);    // <5>
            consumer.accept(string);                                                // <6>
        }
    }

    /**
     * Invokes {@link #connect(SocketAddress, Consumer)} method, with specified arguments, by
     * specified number of times.
     *
     * @param count    the number of times to call {@link #connect(SocketAddress, Consumer)}
     *                 method.
     * @param endpoint the endpoint to connect.
     * @param consumer the consumer accepts the string.
     * @throws IOException if an I/O error occurs.
     */
    static void connect(int count, SocketAddress endpoint, Consumer<? super String> consumer)
            throws IOException {
        if (count <= 0) {
            throw new IllegalArgumentException("count(" + count + ") is not positive");
        }
        Objects.requireNonNull(endpoint, "endpoint is null");
        Objects.requireNonNull(consumer, "consumer is null");
        for (int i = 0; i < count; i++) {
            connect(endpoint, consumer);
        }
    }

    private HelloWorldClientTcp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
