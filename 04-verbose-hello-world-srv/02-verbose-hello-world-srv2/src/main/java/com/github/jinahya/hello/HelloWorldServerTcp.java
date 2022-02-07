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
import java.net.SocketAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.HelloWorld.BYTES;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerTcp
        extends AbstractHelloWorldServer {

    static final ThreadLocal<Integer> LOCAL_PORT = new ThreadLocal<>();

    /**
     * Creates a new instance.
     *
     * @param socketAddress a socket address to bind.
     */
    HelloWorldServerTcp(final SocketAddress socketAddress) {
        super(socketAddress);
    }

    private void open_() throws IOException {
        close();
        serverSocket = new ServerSocket();
        if (socketAddress instanceof InetSocketAddress &&
            ((InetSocketAddress) socketAddress).getPort() > 0) {
            serverSocket.setReuseAddress(true);
        }
        try {
            serverSocket.bind(socketAddress);
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", socketAddress);
            throw ioe;
        }
        log.info("[S] server is open; {}",
                 serverSocket.getLocalSocketAddress());
        LOCAL_PORT.set(serverSocket.getLocalPort());
        new Thread(() -> {
            while (!serverSocket.isClosed()) {
                try {
                    final var socket = serverSocket.accept();
                    log.debug("[S] connected from {}",
                              socket.getRemoteSocketAddress());
                    new Thread(() -> {
                        try (socket) {
                            final byte[] array = new byte[BYTES];
                            helloWorld().set(array);
                            socket.getOutputStream().write(array);
                            socket.getOutputStream().flush();
                        } catch (final IOException ioe) {
                            log.error("failed to send to {}",
                                      socket.getRemoteSocketAddress(), ioe);
                        }
                    }).start();
                } catch (final IOException ioe) {
                    if (serverSocket.isClosed()) {
                        break;
                    }
                    log.error("failed to accept", ioe);
                }
            }
            LOCAL_PORT.remove();
            serverSocket = null;
        }).start();
        log.debug("[S] server thread started");
    }

    @Override
    public void open() throws IOException {
        try {
            lock.lock();
            open_();
        } finally {
            lock.unlock();
        }
    }

    private void close_() throws IOException {
        if (serverSocket == null || serverSocket.isClosed()) {
            return;
        }
        serverSocket.close();
    }

    @Override
    public void close() throws IOException {
        try {
            lock.lock();
            close_();
        } finally {
            lock.unlock();
        }
    }

    private final Lock lock = new ReentrantLock();

    private ServerSocket serverSocket;
}
