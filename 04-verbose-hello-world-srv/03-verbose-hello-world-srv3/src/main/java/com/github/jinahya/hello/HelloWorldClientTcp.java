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

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
final class HelloWorldClientTcp {

    static void runClients(int count, SocketAddress endpoint, Consumer<? super String> consumer) {
        if (count <= 0) {
            throw new IllegalArgumentException("count(" + count + ") is not positive");
        }
        Objects.requireNonNull(endpoint, "endpoint is null");
        Objects.requireNonNull(consumer, "consumer is null");
        var executor = Executors.newFixedThreadPool(Math.min(128, count));
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                try (var client = SocketChannel.open()) {
                    client.connect(endpoint);
                    log.debug("[C] connected to {}", client.getRemoteAddress());
                    var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
                    while (buffer.hasRemaining()) {
                        if (client.read(buffer) == -1) {
                            log.error("premature eof at {}", buffer.position());
                        }
                    }
                    var array = buffer.array();
                    var length = buffer.position();
                    var string = new String(array, 0, length, StandardCharsets.US_ASCII);
                    consumer.accept(string);
                }
                return null;
            });
        }
        IHelloWorldServerUtils.shutdownAndAwaitTermination(executor);
    }

    private HelloWorldClientTcp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
