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
import static com.github.jinahya.hello.IHelloWorldServerUtils.shutdownAndAwaitTermination;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerTcp
        extends AbstractHelloWorldServer {

    static final ThreadLocal<Integer> PORT = new ThreadLocal<>();

    /**
     * Creates a new instance with specified local socket address.
     *
     * @param endpoint the local socket address to bind.
     */
    HelloWorldServerTcp(final SocketAddress endpoint) {
        super(endpoint);
    }

    private void open_() throws IOException {
        close();
        server = new ServerSocket();
        if (endpoint instanceof InetSocketAddress &&
            ((InetSocketAddress) endpoint).getPort() > 0) {
            server.setReuseAddress(true);
        }
        try {
            server.bind(endpoint);
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", endpoint);
            throw ioe;
        }
        log.info("[S] server bound to {}", server.getLocalSocketAddress());
        PORT.set(server.getLocalPort());
        new Thread(() -> {
            var executor = newCachedThreadPool();
            while (!server.isClosed()) {
                try {
                    final var client = server.accept();
                    executor.submit(() -> {
                        log.debug("[S] connected from {}",
                                  client.getRemoteSocketAddress());
                        try (client) {
                            final byte[] array = new byte[BYTES];
                            service().set(array);
                            client.getOutputStream().write(array);
                            client.getOutputStream().flush();
                        } catch (final IOException ioe) {
                            log.error("failed to send to {}",
                                      client.getRemoteSocketAddress(), ioe);
                        }
                    });
                } catch (final IOException ioe) {
                    if (server.isClosed()) {
                        break;
                    }
                    log.error("failed to accept", ioe);
                }
            }
            shutdownAndAwaitTermination(executor, 8L, SECONDS);
            PORT.remove();
            server = null;
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
        if (server == null || server.isClosed()) {
            return;
        }
        server.close();
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

    private ServerSocket server;
}
