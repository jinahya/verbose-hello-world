package com.github.jinahya.hello.miscellaneous.c03chat;

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

import com.github.jinahya.hello.HelloWorldServerConstants;
import com.github.jinahya.hello.HelloWorldServerUtils;
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
public class ChatUdp1Server {

    private static final Map<SocketAddress, Instant> ADDRESSES = new ConcurrentHashMap<>();

    static final Duration THAN = Duration.ofSeconds(8L);

    private record Sender(BlockingQueue<? extends byte[]> queue, DatagramSocket socket)
            implements Runnable {

        private Sender {
            Objects.requireNonNull(queue, "queue is null");
            Objects.requireNonNull(socket, "socket is null");
        }

        @Override
        public void run() {
            log.debug("sender started");
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array;
                try {
                    if ((array = queue.poll(8L, TimeUnit.SECONDS)) == null) {
                        continue;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                log.debug("sending...");
                var older = Instant.now().minus(THAN);
                for (var i = ADDRESSES.entrySet().iterator(); i.hasNext(); ) {
                    var entry = i.next();
                    var timestamp = entry.getValue();
                    if (timestamp.isBefore(older)) {
                        i.remove();
                        continue;
                    }
                    var address = entry.getKey();
                    try {
                        socket.send(new DatagramPacket(array, array.length, address));
                    } catch (IOException ioe) {
                        log.error("[S] failed to send to {}", address, ioe);
                    }
                }
            }
            log.debug("sender finished");
        }
    }

    private record Receiver(DatagramSocket socket, BlockingQueue<? super byte[]> queue)
            implements Runnable {

        private Receiver {
            Objects.requireNonNull(socket, "socket is null");
            Objects.requireNonNull(queue, "queue is null");
        }

        @Override
        public void run() {
            log.debug("receiver started");
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array = _ChatMessage.newArray();
                var packet = new DatagramPacket(array, array.length);
                log.debug("receiving");
                try {
                    socket.receive(packet);
                } catch (IOException ioe) {
                    if (!socket.isClosed()) {
                        log.error("[S] failed to receive", ioe);
                    }
                    Thread.currentThread().interrupt();
                    continue;
                }
                log.debug("received");
                var address = packet.getSocketAddress();
                if (HelloWorldServerUtils.isKeep(_ChatMessage.getMessage(array))) {
                    log.debug("keep received");
                    ADDRESSES.put(address, Instant.now());
                    continue;
                }
                try {
                    if (!queue.offer(array, 1L, TimeUnit.SECONDS)) {
                        log.error("[C] failed to offer");
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            log.debug("receiver finished");
        }
    }

    public static void main(String... args) throws Exception {
        var executor = Executors.newCachedThreadPool();
        var futures = new ArrayList<Future<?>>();
        try (var server = new DatagramSocket(null)) {
            server.bind(new InetSocketAddress(
                    InetAddress.getByName("0.0.0.0"), _ChatConstants.PORT
            ));
            log.debug("[S] bound to {}", server.getLocalSocketAddress());
            var queue = new ArrayBlockingQueue<byte[]>(1024);
            futures.add(executor.submit(new Receiver(server, queue)));
            futures.add(executor.submit(new Sender(queue, server)));
            var latch = new CountDownLatch(1);
            HelloWorldLangUtils.callWhenRead(
                    v -> !server.isClosed() && !Thread.currentThread().isInterrupted(),
                    HelloWorldServerConstants.QUIT,
                    () -> {
                        latch.countDown();
                        return null;
                    },
                    l -> {
                    }
            );
            latch.await(); // InterruptedException
        }
        futures.forEach(f -> f.cancel(true));
        executor.shutdown();
        if (!executor.awaitTermination(8L, TimeUnit.SECONDS)) {
            log.error("[S] executor has not been terminated");
        }
    }

    private ChatUdp1Server() {
        throw new AssertionError("instantiation is not allowed");
    }
}
