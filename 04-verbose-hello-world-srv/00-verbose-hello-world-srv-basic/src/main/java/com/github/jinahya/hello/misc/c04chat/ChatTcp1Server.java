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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
class ChatTcp1Server extends ChatTcp {

    public static void main(final String... args) throws Exception {
        try (var executor = newExecutorForServer("tcp-1-server-");
             var server = new ServerSocket()) {
            // -------------------------------------------------------------------------------- bind
            server.bind(ADDR, SERVER_BACKLOG);
            // ------------------------------------------------------------- read-quit!/close-server
            JavaLangUtils.readLinesAndCloseWhenTests(
                    QUIT::equalsIgnoreCase,
                    server
            );
            // ----------------------------------------------------------------------------- prepare
            final var clients = new CopyOnWriteArrayList<Socket>();
            final var messages = new LinkedBlockingQueue<_ChatMessage.OfArray>();
            // -------------------------------------------------------------------------- take/write
            executor.submit(() -> {
                for (_ChatMessage.OfArray m; !Thread.currentThread().isInterrupted(); ) {
                    try {
                        m = messages.take();
                        m.print();
                    } catch (final InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        continue;
                    }
                    log.debug("clients.size: {}", clients.size());
                    for (final var client : clients) {
                        try {
                            m.write(client.getOutputStream()).flush();
                        } catch (final IOException e) {
                            if (!client.isClosed()) {
                                log.error("failed to write", e);
                            }
                            clients.remove(client);
                        }
                    }
                }
            });
            // ------------------------------------------------------------------------- server-loop
            while (!server.isClosed()) {
                // --------------------------------------------------- accept-client/add-to-the-list
                final Socket client;
                try {
                    client = server.accept();
                } catch (final IOException ioe) {
                    if (!server.isClosed()) {
                        log.error("failed to accept", ioe);
                    }
                    continue;
                }
                clients.add(client);
                // ------------------------------------------------- read-message/offer-to-the-queue
                executor.submit(() -> {
                    try (client) {
                        while (!client.isClosed() && !Thread.currentThread().isInterrupted()) {
                            final var message = new _ChatMessage.OfArray()
                                    .read(client.getInputStream());
                            if (!messages.offer(message)) {
                                log.error("failed to offer message: " + message);
                            }
                        }
                    } catch (final IOException ioe) {
                        if (!client.isClosed()) {
                            log.error("failed to read", ioe);
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
