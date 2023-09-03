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
import com.github.jinahya.hello.util.HelloWorldLangUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Slf4j
class ChatTcp1Server {

    private record Receiver(Socket client, Queue<? super byte[]> queue)
            implements Runnable {

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
                            .readNBytes(_ChatMessage.BYTES);
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

    public static void main(String... args) throws Exception {
        var executor = Executors.newCachedThreadPool();
        var futures = new ArrayList<Future<?>>();
        var clients = Collections.synchronizedList(new ArrayList<Socket>());
        var queue = new ArrayBlockingQueue<byte[]>(1024);
        futures.add(executor.submit(new Sender(queue, clients)));
        try (var server = new ServerSocket() /* IOException */) {
            server.bind(new InetSocketAddress(InetAddress.getByName("::"),
                                              _ChatConstants.PORT));
            log.info("bound on {}", server.getLocalSocketAddress());
            HelloWorldLangUtils.readLinesAndCallWhenTests(
                    HelloWorldServerUtils::isQuit, // <predicate>
                    () -> {                        // <callable>
                        server.close();
                        return null;
                    },
                    l -> {                        // <consumer>
                        // does nothing
                    }
            );
            while (!server.isClosed()) {
                try {
                    var client = server.accept(); // IOException
                    log.info("accepted from {} through {}",
                             client.getRemoteSocketAddress(),
                             client.getLocalSocketAddress());
                    clients.add(client);
                    futures.add(executor.submit(new Receiver(client, queue)));
                } catch (IOException ioe) {
                    if (!server.isClosed()) {
                        log.error("failed to accept", ioe);
                    }
                    break;
                }
            }
        }
        futures.forEach(f -> f.cancel(true));
        for (var client : clients) {
            try {
                client.close(); // IOException
            } catch (IOException ioe) {
                log.error("failed to close " + client, ioe);
            }
        }
        executor.shutdown();
        if (!executor.awaitTermination(8L, TimeUnit.SECONDS)) {
            log.error("executor has not been terminated");
        }
    }
}
