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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.IHelloWorldServerUtils.shutdownAndAwaitTermination;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerUdp
        extends AbstractHelloWorldServer {

    static final ThreadLocal<Integer> PORT = new ThreadLocal<>();

    /**
     * Creates a new instance with specified local socket address.
     *
     * @param endpoint the local socket address to bind.
     */
    HelloWorldServerUdp(final SocketAddress endpoint) {
        super(endpoint);
    }

    private void open_() throws IOException {
        close();
        socket = new DatagramSocket(null);
        if (endpoint instanceof InetSocketAddress
            && ((InetSocketAddress) endpoint).getPort() > 0) {
            socket.setReuseAddress(true);
        }
        try {
            socket.bind(endpoint);
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("[S] server bound to {}", socket.getLocalSocketAddress());
        PORT.set(socket.getLocalPort());
        new Thread(() -> {
            final var executor = Executors.newCachedThreadPool();
            while (!socket.isClosed()) {
                final var packet = new DatagramPacket(new byte[0], 0);
                try {
                    socket.receive(packet);
                    final var address = packet.getSocketAddress();
                    log.debug("[S] received from {}", address);
                    executor.submit(() -> {
                        final var array = new byte[BYTES];
                        helloWorld().set(array);
                        packet.setData(array);
                        try {
                            socket.send(packet);
                            log.debug("[S] send to {}", address);
                        } catch (final IOException ioe) {
                            log.error("failed to send", ioe);
                        }
                    });
                } catch (final IOException ioe) {
                    if (socket.isClosed()) {
                        break;
                    }
                    log.error("failed to receive", ioe);
                }
            }
            shutdownAndAwaitTermination(executor, 8L, SECONDS);
            PORT.remove();
            socket = null;
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
        if (socket == null || socket.isClosed()) {
            return;
        }
        socket.close();
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

    private DatagramSocket socket;

    private final Lock lock = new ReentrantLock();
}
