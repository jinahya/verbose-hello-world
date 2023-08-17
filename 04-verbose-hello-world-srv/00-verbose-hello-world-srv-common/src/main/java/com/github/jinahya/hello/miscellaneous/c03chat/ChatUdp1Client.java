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
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ChatUdp1Client {

    private static final Duration KEEP_DURATION = ChatUdp1Server.KEEP_DURATION.dividedBy(2L);

    static {
        assert KEEP_DURATION.toSeconds() > 0;
    }

    /**
     * A runnable which continuously receives messages through a specified socket, and prints
     * received message to the {@link System#out}.
     *
     * @param socket the socket from which messages are received.
     */
    private record Receiver(DatagramSocket socket) implements Runnable {

        private Receiver {
            Objects.requireNonNull(socket, "socket is null");
        }

        @Override
        public void run() {
            var array = _ChatMessage.newEmptyArray();
            var packet = new DatagramPacket(array, array.length);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    socket.receive(packet);
                    _ChatMessage.printToSystemOut(array);
                } catch (IOException ioe) {
                    if (!socket.isClosed()) {
                        log.error("[C] failed to receive", ioe);
                    }
                }
            }
        }
    }

    /**
     * A runnable which continuously polls messages from specified queue, and sends them to
     * specified endpoint through specified socket.
     *
     * @param queue   the queue from which message are polled.
     * @param address the endpoint to send messages.
     * @param socket  the socket through which messages are sent.
     */
    private record Sender(BlockingQueue<? extends byte[]> queue, SocketAddress address,
                          DatagramSocket socket)
            implements Runnable {

        private Sender {
            Objects.requireNonNull(queue, "queue is null");
            Objects.requireNonNull(address, "address is null");
            Objects.requireNonNull(socket, "socket is null");
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array;
                try {
                    if ((array = queue.poll(1L, TimeUnit.SECONDS)) == null) {
                        continue;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                var packet = new DatagramPacket(array, array.length, address);
                try {
                    socket.send(packet);
                } catch (IOException ioe) {
                    if (!socket.isClosed()) {
                        log.error("[C] failed to send", ioe);
                    }
                }
            }
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
        log.debug("[C] address: {}", address);
        var queue = new LinkedBlockingQueue<byte[]>();
        var executor = Executors.newScheduledThreadPool(3); // receive, send, and keep
        var futures = new ArrayList<Future<?>>();
        executor.scheduleAtFixedRate(
                () -> {
                    var array = _ChatMessage.arrayOf(HelloWorldServerConstants.KEEP);
                    if (!queue.offer(array)) {
                        log.error("[C] failed to offer keep");
                    }
                },
                KEEP_DURATION.toSeconds(),
                KEEP_DURATION.toSeconds(),
                TimeUnit.SECONDS
        );
        try (var client = new DatagramSocket(null)) {
            log.debug("[S]: SO_RCVBUF: {}", client.getOption(StandardSocketOptions.SO_RCVBUF));
            log.debug("[S]: SO_SNFBUD: {}", client.getOption(StandardSocketOptions.SO_SNDBUF));
            futures.add(executor.submit(new Receiver(client)));
            futures.add(executor.submit(new Sender(queue, address, client)));
            var latch = new CountDownLatch(1);
            HelloWorldLangUtils.callWhenRead(
                    HelloWorldServerConstants.QUIT,
                    () -> {
                        latch.countDown();
                        return null;
                    },
                    l -> {
                        if (!queue.offer(_ChatMessage.arrayOf(_ChatUtils.prependUsername(l)))) {
                            log.error("[C] failed to offer message");
                        }
                    }
            );
            latch.await(); // InterruptedException
        }
        futures.forEach(f -> f.cancel(true));
        executor.shutdown();
        if (!executor.awaitTermination(8L, TimeUnit.SECONDS)) {
            log.error("[C] executor has not been terminated");
        }
    }

    private ChatUdp1Client() {
        throw new AssertionError("instantiation is not allowed");
    }
}