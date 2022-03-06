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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
class HelloWorldClientUdp2 {

    /**
     * Runs specified number of clients which each sends an empty packet to specified endpoint,
     * receives {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes, decodes those bytes into a
     * string using {@link StandardCharsets#US_ASCII} charset, and accepts the string to specified
     * consumer.
     *
     * @param count    the number of clients to run.
     * @param endpoint the endpoint send/receive packet to/from.
     * @param consumer the consumer accepts the decoded string.
     * @throws InterruptedException if the current thread interrupted while waiting all clients to
     *                              finish.
     */
    static void runClients(int count, SocketAddress endpoint, Consumer<? super String> consumer)
            throws InterruptedException {
        if (count <= 0) {
            throw new IllegalArgumentException("count(" + count + ") is not positive");
        }
        Objects.requireNonNull(endpoint, "endpoint is null");
        Objects.requireNonNull(consumer, "consumer is null");
        var latch = new CountDownLatch(count);
        for (var i = 0; i < count; i++) {
            new Thread(() -> {
                try (var client = new DatagramSocket(null)) {
                    var sending = new DatagramPacket(new byte[0], 0, endpoint);
                    client.send(sending); // IOException
                    log.debug("[C] sent to {}", endpoint);
                    var array = new byte[HelloWorld.BYTES];
                    var received = new DatagramPacket(array, array.length);
                    client.setSoTimeout((int) TimeUnit.SECONDS.toMillis(1L)); // SocketException
                    client.receive(received); // IOException
                    log.debug("[C] received from {}", received.getSocketAddress());
                    var length = received.getLength();
                    assert length == array.length;
                    var string = new String(array, 0, length, StandardCharsets.US_ASCII);
                    consumer.accept(string);
                } catch (IOException ioe) {
                    log.error("failed to send/receive to/from {}", endpoint, ioe);
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        HelloWorldServerUtils.await(latch);
    }

    private HelloWorldClientUdp2() {
        throw new AssertionError("instantiation is not allowed");
    }
}
