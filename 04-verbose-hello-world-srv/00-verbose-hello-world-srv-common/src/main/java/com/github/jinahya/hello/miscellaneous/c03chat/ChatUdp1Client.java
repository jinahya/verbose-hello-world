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
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatUdp1Client {

    private static final Duration THAN = ChatUdp1Server.THAN.minusSeconds(1L);

    private record Receiver(DatagramSocket socket) implements Runnable {

        private Receiver {
            Objects.requireNonNull(socket, "socket is null");
        }

        @Override
        public void run() {
            log.debug("receiver started");
            var array = _ChatMessage.newArray();
            var packet = new DatagramPacket(array, array.length);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket.receive(packet);
                } catch (PortUnreachableException pue) { // connect
                    Thread.currentThread().interrupt();
                    continue;
                } catch (IOException ioe) {
                    if (!socket.isClosed()) {
                        log.error("[C] failed to receive", ioe);
                    }
                    Thread.currentThread().interrupt();
                    continue;
                }
                _ChatMessage.printToSystemOut(array);
            }
            log.debug("receiver is finishing");
        }
    }

    private record Sender(BlockingQueue<? extends byte[]> queue, DatagramSocket socket,
                          SocketAddress address)
            implements Runnable {

        private Sender {
            Objects.requireNonNull(queue, "queue is null");
            Objects.requireNonNull(socket, "socket is null");
            Objects.requireNonNull(address, "address is null");
        }

        @Override
        public void run() {
            log.debug("sender starts");
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array;
                try {
                    if ((array = queue.poll(1L, TimeUnit.SECONDS)) == null) {
                        continue;
                    } else {
                        log.debug("polled: {}", _ChatMessage.getMessage(array));
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                var packet = new DatagramPacket(array, array.length, address);
                try {
                    socket.send(packet);
                    log.debug("sent: {}", _ChatMessage.getMessage(array));
                } catch (IOException ioe) {
                    if (!socket.isClosed()) {
                        log.error("[C] failed to send", ioe);
                    }
                    Thread.currentThread().interrupt();
                }
            }
            log.debug("sender is finishing");
        }
    }

    public static void main(String... args) throws Exception {
        InetAddress addr;
        try {
            addr = InetAddress.getByName(args[0]);
        } catch (ArrayIndexOutOfBoundsException aioobe) {
            addr = InetAddress.getLoopbackAddress();
        }
        var address = new InetSocketAddress(addr, _ChatConstants.PORT);
        var executor = Executors.newCachedThreadPool();
        var futures = new ArrayList<Future<?>>();
        try (var client = new DatagramSocket(null)) {
            log.debug("[C] address: {}", address);
            var connect = ThreadLocalRandom.current().nextBoolean();
            if (connect) {
                try {
                    client.connect(address);
                    log.debug("[C] connected to {}, through {}", client.getRemoteSocketAddress(),
                              client.getLocalSocketAddress());
                } catch (SocketException se) {
                    log.error("[C] unable to connect to {}", address);
                    connect = false;
                }
            }
            futures.add(executor.submit(new Receiver(client)));
            var queue = new ArrayBlockingQueue<byte[]>(1);
            futures.add(executor.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    var array = _ChatMessage.newArray(HelloWorldServerConstants.KEEP);
                    try {
                        if (!queue.offer(array, 1L, TimeUnit.SECONDS)) {
                            log.error("[C] failed to offer keep");
                        } else {
                            log.debug("keep offered");
                        }
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    try {
                        Thread.sleep(THAN.toMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }));
            futures.add(executor.submit(new Sender(queue, client, address)));
            var latch = new CountDownLatch(1);
            HelloWorldLangUtils.callWhenRead(
                    v -> !Thread.currentThread().isInterrupted(),
                    HelloWorldServerConstants.QUIT,
                    () -> {
                        latch.countDown();
                        return null;
                    },
                    l -> {
                        var array = _ChatMessage.newArray();
                        _ChatMessage.setTimestampWithCurrentTimeMillis(array);
                        _ChatMessage.setMessage(array, _ChatUtils.prependUsername(l));
                        try {
                            if (!queue.offer(array, 1L, TimeUnit.SECONDS)) {
                                log.error("failed to offer");
                            }
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                    }
            );
            latch.await(); // InterruptedException
            if (connect) {
                client.disconnect(); // UncheckedIOException
            }
        }
        futures.forEach(f -> f.cancel(true));
        executor.shutdown();
        var terminated = executor.awaitTermination(8L, TimeUnit.SECONDS);
    }

    private ChatUdp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}
