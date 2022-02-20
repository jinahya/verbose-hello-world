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
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static com.github.jinahya.hello.IHelloWorldServerUtils.shutdownAndAwaitTermination;
import static java.util.Objects.requireNonNull;

@Slf4j
final class HelloWorldClientTcp {

    static void clients(final int count, final SocketAddress endpoint,
                        final Consumer<? super String> consumer) {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "count(" + count + ") is not positive");
        }
        requireNonNull(endpoint, "endpoint is null");
        requireNonNull(consumer, "consumer is null");
        var executor = Executors.newCachedThreadPool();
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                try (var client = SocketChannel.open()) {
                    client.connect(endpoint);
                    log.debug("[C] connected to {}", client.getRemoteAddress());
                    var array = new byte[HelloWorld.BYTES];
                    var buffer = ByteBuffer.wrap(array);
                    while (buffer.hasRemaining()) {
                        client.read(buffer);
                    }
                    var string = new String(array, StandardCharsets.US_ASCII);
                    consumer.accept(string);
                    return null;
                }
            });
        }
        shutdownAndAwaitTermination(executor);
    }

    private HelloWorldClientTcp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
