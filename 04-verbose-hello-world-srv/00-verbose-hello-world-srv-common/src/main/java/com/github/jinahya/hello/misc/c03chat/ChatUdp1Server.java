package com.github.jinahya.hello.misc.c03chat;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

// https://www.rfc-editor.org/rfc/rfc862
@Slf4j
class ChatUdp1Server {

    static final Duration DURATION_TO_KEEP_ADDRESSES = Duration.ofSeconds(8L);

    private record Sender(DatagramSocket socket,
                          BlockingQueue<? extends byte[]> messages,
                          Map<SocketAddress, Instant> addresses)
            implements Runnable {

        private Sender {
            Objects.requireNonNull(socket, "socket is null");
            Objects.requireNonNull(messages, "messages is null");
            Objects.requireNonNull(addresses, "addresses is null");
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array;
                try {
                    if ((array = messages.poll(8L, TimeUnit.SECONDS)) == null) {
                        continue;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                var packet = new DatagramPacket(array, array.length);
                var instantToKeep = Instant.now()
                        .minus(DURATION_TO_KEEP_ADDRESSES);
                for (var i = addresses.entrySet().iterator(); i.hasNext(); ) {
                    var entry = i.next();
                    if (entry.getValue().isBefore(instantToKeep)) {
                        i.remove();
                        continue;
                    }
                    packet.setSocketAddress(entry.getKey());
                    try {
                        socket.send(packet);
                    } catch (IOException ioe) {
                        if (!socket.isClosed()) {
                            log.error("failed to send to {}",
                                      packet.getSocketAddress(), ioe);
                        }
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    private record Receiver(DatagramSocket socket,
                            BlockingQueue<? super byte[]> messages,
                            Map<SocketAddress, Instant> addresses)
            implements Runnable {

        private Receiver {
            Objects.requireNonNull(socket, "socket is null");
            Objects.requireNonNull(messages, "messages is null");
            Objects.requireNonNull(addresses, "addresses is null");
        }

        @Override
        public void run() {
            var array = _ChatMessage.OfArray.empty();
            var packet = new DatagramPacket(array, array.length);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket.receive(packet);
                } catch (IOException ioe) {
                    if (!socket.isClosed()) {
                        log.error("failed to receive", ioe);
                    }
                    Thread.currentThread().interrupt();
                    continue;
                }
                var address = packet.getSocketAddress();
                addresses.put(address, Instant.now());
                if (HelloWorldServerUtils.isKeep(
                        _ChatMessage.OfArray.getMessage(array))) {
                    continue;
                }
                if (!messages.offer(_ChatMessage.OfArray.copyOf(array))) {
                    log.error("failed to offer");
                }
            }
        }
    }

    public static void main(String... args) throws Exception {
        var executor = Executors.newScheduledThreadPool(3);
        var futures = new ArrayList<Future<?>>(2);
        try (var server = new DatagramSocket(null)) {
            log.debug("[S]: SO_RCVBUF: {}",
                      server.getOption(StandardSocketOptions.SO_RCVBUF));
            log.debug("[S]: SO_SNFBUD: {}",
                      server.getOption(StandardSocketOptions.SO_SNDBUF));
            server.bind(new InetSocketAddress(
                    InetAddress.getByName("0.0.0.0"), _ChatConstants.PORT
            ));
            log.debug("bound to {}", server.getLocalSocketAddress());
            var messages = new ArrayBlockingQueue<byte[]>(1024);
            var addresses = new ConcurrentHashMap<SocketAddress, Instant>();
            futures.add(
                    executor.submit(new Receiver(server, messages, addresses)));
            futures.add(
                    executor.submit(new Sender(server, messages, addresses)));
            var latch = new CountDownLatch(1);
            JavaLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        latch.countDown();
                        return null;
                    },
                    l -> {                         // <consumer>
                    }
            );
            latch.await(); // InterruptedException
        }
        futures.forEach(f -> f.cancel(true));
        executor.shutdown();
        if (!executor.awaitTermination(8L, TimeUnit.SECONDS)) {
            log.error("executor has not been terminated");
        }
    }

    private ChatUdp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
