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
class HelloWorldServerUdp implements IHelloWorldServer {

    static final ThreadLocal<Integer> LOCAL_PORT = new ThreadLocal<>();

    /**
     * Creates a new instance.
     *
     * @param helloWorld              an instance of {@link HelloWorld} interface.
     * @param socketAddress           a socket address to bind.
     * @param executorServiceSupplier a supplier for an executor service.
     */
    HelloWorldServerUdp(final HelloWorld helloWorld, final SocketAddress socketAddress,
                        final Supplier<? extends ExecutorService> executorServiceSupplier) {
        super();
        this.helloWorld = Objects.requireNonNull(helloWorld, "service is null");
        this.socketAddress = Objects.requireNonNull(socketAddress, "endpoint is null");
        this.executorServiceSupplier = Objects.requireNonNull(
                executorServiceSupplier, "executorServiceSupplier is null");
    }

    @Override
    public void open() throws IOException {
        try {
            lock.lock();
            close();
            datagramSocket = new DatagramSocket(null);
            if (socketAddress instanceof InetSocketAddress &&
                ((InetSocketAddress) socketAddress).getPort() > 0) {
                datagramSocket.setReuseAddress(true);
            }
            try {
                datagramSocket.bind(socketAddress);
            } catch (final IOException ioe) {
                log.error("failed to bind; endpoint: {}", socketAddress, ioe);
                throw ioe;
            }
            log.info("server is open; {}", datagramSocket.getLocalSocketAddress());
            LOCAL_PORT.set(datagramSocket.getLocalPort());
            final Thread thread = new Thread(() -> {
                final ExecutorService executorService = executorServiceSupplier.get();
                while (!datagramSocket.isClosed()) {
                    final DatagramPacket packet = new DatagramPacket(new byte[0], 0);
                    try {
                        datagramSocket.receive(packet);
                        final Future<Void> future = executorService.submit(() -> {
                            final SocketAddress clientAddress = packet.getSocketAddress();
                            log.debug("[S] received from {}", clientAddress);
                            final byte[] array = new byte[HelloWorld.BYTES];
                            helloWorld.set(array);
                            datagramSocket.send(
                                    new DatagramPacket(array, array.length, clientAddress));
                            log.debug("[S] sent to {}", clientAddress);
                            return null;
                        });
                    } catch (final IOException ioe) {
                        if (datagramSocket.isClosed()) {
                            break;
                        }
                        log.error("failed to receive", ioe);
                    }
                }
                executorService.shutdown();
                try {
                    final boolean terminated = executorService.awaitTermination(
                            8L, TimeUnit.SECONDS);
                    log.debug("executor service terminated: {}", terminated);
                } catch (final InterruptedException ie) {
                    log.error("interrupted while awaiting executor service to be terminated", ie);
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
            if (datagramSocket == null || datagramSocket.isClosed()) {
                return;
            }
            datagramSocket.close();
        } finally {
            lock.unlock();
        }
    }

    private final HelloWorld helloWorld;

    private final SocketAddress socketAddress;

    private final Lock lock = new ReentrantLock();

    private final Supplier<? extends ExecutorService> executorServiceSupplier;

    private DatagramSocket datagramSocket;
}
