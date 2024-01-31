package com.github.jinahya.hello.misc.c04chat;

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

import com.github.jinahya.hello.util.JavaLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp1Server extends _ChatTcp {

    private record Receiver(Socket client, Queue<? super byte[]> queue) implements Runnable {

        private Receiver {
            Objects.requireNonNull(client, "client is null");
            Objects.requireNonNull(queue, "queue is null");
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array;
                try {
                    array = client.getInputStream()
                            .readNBytes(_Message.BYTES);
                } catch (IOException ioe) {
                    if (!client.isClosed()) {
                        log.error("failed to read", ioe);
                    }
                    break;
                }
                if (array.length == 0) {
                    break;
                }
                if (!queue.offer(array)) {
                    log.error("failed to offer");
                }
            }
            try {
                client.close();
            } catch (IOException ioe) {
                log.error("failed to close {}", client, ioe);
            }
        }
    }

    private record Sender(BlockingQueue<? extends byte[]> queue,
                          Iterable<? extends Socket> clients)
            implements Runnable {

        private Sender {
            Objects.requireNonNull(queue, "queue is null");
            Objects.requireNonNull(clients, "clients is null");
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] array;
                try {
                    if ((array = queue.poll(8L, TimeUnit.SECONDS)) == null) {
                        continue;
                    }
                } catch (InterruptedException ie) {
                    break;
                }
                synchronized (clients) {
                    for (var i = clients.iterator(); i.hasNext(); ) {
                        var client = i.next();
                        try {
                            client.getOutputStream().write(array);
                            client.getOutputStream().flush();
                        } catch (IOException ioe) {
                            if (!client.isClosed()) {
                                log.error("failed to send to {}", client, ioe);
                            }
                            try {
                                client.close();
                            } catch (IOException ioe2) {
                                log.error("failed to close {}", client, ioe2);
                            }
                            i.remove();
                        }
                    }
                }
            }
        }
    }

    public static void main(final String... args) throws Exception {
        try (var executor = newExecutorForServer("tcp-1-server-");
             var server = new ServerSocket()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR, SERVER_BACKLOG);
            // ------------------------------------------------------------- read-quit!/close-server
            JavaLangUtils.readLinesAndCloseWhenTests(
                    "quit!"::equalsIgnoreCase,
                    server
            );
            // ----------------------------------------------------------------------------- prepare
            final var clients = new CopyOnWriteArrayList<Socket>();
            final var messages = new ArrayBlockingQueue<___Message2>(1024);
            // -------------------------------------------------------------------------- take/write
            final var writer = executor.submit(() -> {
                for (___Message2 message; !Thread.currentThread().isInterrupted(); ) {
                    try {
                        message = messages.take();
                    } catch (final InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        continue;
                    }
                    for (var i = clients.iterator(); i.hasNext(); ) {
                        final var client = i.next();
                        try {
                            message.write(client.getOutputStream()).flush();
                        } catch (final IOException ioe) {
                            i.remove();
                            if (!client.isConnected()) {
                                continue;
                            }
                            log.error("failed to write to " + client, ioe);
                            try {
                                client.close();
                            } catch (final IOException ioe2) {
                                log.error("failed to close " + client, ioe2);
                            }
                        }
                    }
                }
            });
            while (!server.isClosed()) {
                // -------------------------------------------------------------------------- accept
                final Socket client;
                try {
                    client = server.accept();
                } catch (final IOException ioe) {
                    if (!server.isClosed()) {
                        log.error("failed to accept", ioe);
                    }
                    continue;
                }
                // ---------------------------------------------------------------------- read/offer
                executor.submit(() -> {
                    clients.add(client);
                    try (client) {
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                final var message = ___Message2.read(client.getInputStream());
                                if (!messages.offer(message)) {
                                    log.error("failed to offer message: " + message);
                                }
                            } catch (final IOException ioe) {
                                if (ioe instanceof EOFException) {
                                    Thread.currentThread().interrupt();
                                    continue;
                                }
                                log.error("failed to read", ioe);
                            }
                        }
                    }
                    return null;
                });
            }
            // --------------------------------------------------------------- shutdown-executor-now
            executor.shutdownNow();
        }
    }
}
