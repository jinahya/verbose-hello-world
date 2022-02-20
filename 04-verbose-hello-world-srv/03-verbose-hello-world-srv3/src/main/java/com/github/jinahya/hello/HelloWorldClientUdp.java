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
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Slf4j
public class HelloWorldClientUdp {

    static void clients(final int count, final SocketAddress endpoint,
                        final Consumer<? super String> consumer) {
        if (count <= 0) {
            throw new IllegalArgumentException(
                    "count(" + count + ") is not positive");
        }
        Objects.requireNonNull(endpoint, "endpoint is null");
        Objects.requireNonNull(consumer, "consumer is null");
        var executor = Executors.newCachedThreadPool();
        for (int i = 0; i < count; i++) {
            executor.submit(() -> {
                try (var client = DatagramChannel.open()) {
                    var src = ByteBuffer.allocate(0);
                    var sent = client.send(src, endpoint);
                    log.debug("[C] sent to {}", endpoint);
                    assert sent == src.capacity();
                    var dst = ByteBuffer.allocate(HelloWorld.BYTES);
                    var address = client.receive(dst);
                    log.debug("[C] received from {}", address);
                    assert address != null;
                    var array = dst.array();
                    var string = new String(array, StandardCharsets.US_ASCII);
                    consumer.accept(string);
                }
                return null;
            });
        }
        IHelloWorldServerUtils.shutdownAndAwaitTermination(executor);
    }

    private HelloWorldClientUdp() {
        throw new AssertionError("instantiation is not allowed");
    }
}
