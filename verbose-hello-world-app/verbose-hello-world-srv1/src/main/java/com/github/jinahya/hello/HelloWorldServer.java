package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv1
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Objects;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServer {

    static final ThreadLocal<SocketAddress> ENDPOINT = new ThreadLocal<>();

    /**
     * Creates a new instance.
     */
    HelloWorldServer(final HelloWorld service, final SocketAddress endpoint, final int backlog) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint is null");
        this.backlog = backlog;
    }

    /**
     * Starts this server.
     *
     * @throws IOException if an I/O error occurs.
     */
    synchronized void start() throws IOException {
        if (serverSocket != null) {
            throw new IllegalStateException("already started");
        }
        serverSocket = new ServerSocket();
        try {
            serverSocket.bind(endpoint, backlog);
        } catch (final IOException ioe) {
            log.error("failed to bind the server socket; endpoint: {}, backlog: {}", endpoint, backlog);
            stop();
            throw ioe;
        }
        ENDPOINT.set(serverSocket.getLocalSocketAddress());
        new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try (Socket socket = serverSocket.accept()) {
                    try {
                        service.write(socket.getOutputStream()).flush();
                    } catch (final IOException ioe) {
                        log.error("failed to write", ioe);
                    }
                } catch (final IOException ioe) {
                    if (serverSocket.isClosed()) {
                        break;
                    }
                    log.error("failed to accept", ioe);
                }
            }
        }).start();
    }

    /**
     * Stops this server.
     *
     * @throws IOException if an I/O error occurs.
     */
    synchronized void stop() throws IOException {
        if (serverSocket == null) {
            return;
        }
        try {
            serverSocket.close();
        } finally {
            serverSocket = null;
            ENDPOINT.remove();
        }
    }

    private final HelloWorld service;

    private final SocketAddress endpoint;

    private final int backlog;

    private ServerSocket serverSocket;
}
