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
import java.net.InetSocketAddress;
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
class HelloWorldServerTcp implements IHelloWorldServer {

    static final ThreadLocal<Integer> LOCAL_PORT = new ThreadLocal<>();

    /**
     * Creates a new instance.
     *
     * @param service  an instance of {@link HelloWorld} interface.
     * @param endpoint a socket address to bind.
     * @param backlog  a value of backlog.
     */
    HelloWorldServerTcp(final HelloWorld service, final SocketAddress endpoint,
                        final int backlog) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint is null");
        this.backlog = backlog;
    }

    @Override
    public synchronized void open() throws IOException {
        close();
        serverSocket = new ServerSocket();
        if (endpoint instanceof InetSocketAddress &&
            ((InetSocketAddress) endpoint).getPort() > 0) {
            serverSocket.setReuseAddress(true);
        }
        try {
            serverSocket.bind(endpoint, backlog);
        } catch (final IOException ioe) {
            log.error("failed to bind; endpoint: {}, backlog: {}", endpoint,
                      backlog, ioe);
            throw ioe;
        }
        log.info("server is open; {}", serverSocket.getLocalSocketAddress());
        LOCAL_PORT.set(serverSocket.getLocalPort());
        final Thread thread = new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try (Socket socket = serverSocket.accept()) {
                    log.debug("[S] connected from {}; local: {}",
                              socket.getRemoteSocketAddress(),
                              socket.getLocalSocketAddress());
                    // TODO: Send 'hello, world' bytes through the socket!
                } catch (final IOException ioe) {
                    if (serverSocket.isClosed()) {
                        break;
                    }
                    log.error("failed to accept/send", ioe);
                }
            }
            LOCAL_PORT.remove();
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public synchronized void close() throws IOException {
        if (serverSocket == null || serverSocket.isClosed()) {
            return;
        }
        serverSocket.close();
    }

    private final HelloWorld service;

    private final SocketAddress endpoint;

    private final int backlog;

    private ServerSocket serverSocket;
}
