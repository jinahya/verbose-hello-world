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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

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
     * @param helloWorld              an instance of {@link HelloWorld}
     *                                interface.
     * @param socketAddress           a socket address to bind.
     * @param backlog                 a value of backlog.
     * @param executorServiceSupplier a supplier for an executor service.
     */
    HelloWorldServerTcp(final HelloWorld helloWorld,
                        final SocketAddress socketAddress,
                        final int backlog,
                        final Supplier<? extends ExecutorService> executorServiceSupplier) {
        super();
        this.service = Objects.requireNonNull(helloWorld, "helloWorld is null");
        this.endpoint = Objects.requireNonNull(socketAddress,
                                               "socketAddress is null");
        this.backlog = backlog;
        this.executorServiceSupplier = Objects.requireNonNull(
                executorServiceSupplier, "executorServiceSupplier is null");
    }

    @Override
    public void open() throws IOException {
        try {
            lock.lock();
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
            log.info("server is open; {}",
                     serverSocket.getLocalSocketAddress());
            LOCAL_PORT.set(serverSocket.getLocalPort());
            final Thread thread = new Thread(() -> {
                final ExecutorService executorService
                        = executorServiceSupplier.get();
                while (!serverSocket.isClosed()) {
                    try {
                        final Socket socket = serverSocket.accept();
                        final Future<Void> futurue = executorService.submit(
                                () -> {
                                    try (Socket s = socket) {
                                        log.debug(
                                                "[S] connected from {}; local: {}",
                                                socket.getRemoteSocketAddress(),
                                                socket.getLocalSocketAddress());
                                        final byte[] array
                                                = new byte[HelloWorld.BYTES];
                                        service.set(array);
                                        s.getOutputStream().write(array);
                                        s.getOutputStream().flush();
                                    }
                                    return null;
                                });
                    } catch (final IOException ioe) {
                        if (serverSocket.isClosed()) {
                            break;
                        }
                        log.error("failed to accept", ioe);
                    }
                }
                executorService.shutdown();
                try {
                    final boolean terminated
                            = executorService.awaitTermination(8L,
                                                               TimeUnit.SECONDS);
                    log.debug("executor service terminated: {}", terminated);
                } catch (final InterruptedException ie) {
                    log.error(
                            "interrupted while awaiting executor service to be terminated",
                            ie);
                }
                LOCAL_PORT.remove();
            });
            thread.setDaemon(true);
            thread.start();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        try {
            lock.lock();
            if (serverSocket == null || serverSocket.isClosed()) {
                return;
            }
            serverSocket.close();
        } finally {
            lock.unlock();
        }
    }

    private final HelloWorld service;

    private final SocketAddress endpoint;

    private final int backlog;

    private final Supplier<? extends ExecutorService> executorServiceSupplier;

    private final Lock lock = new ReentrantLock();

    private ServerSocket serverSocket;
}
