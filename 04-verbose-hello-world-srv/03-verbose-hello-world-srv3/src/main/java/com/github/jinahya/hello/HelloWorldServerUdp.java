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
import java.util.concurrent.TimeUnit;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerUdp
        implements IHelloWorldServer {

    static final ThreadLocal<Integer> LOCAL_PORT = new ThreadLocal<>();

    /**
     * Creates a new instance.
     *
     * @param helloWorld      an instance of {@link HelloWorld} interface.
     * @param socketAddress   a socket address to bind.
     * @param executorService a supplier for an executor service.
     */
    HelloWorldServerUdp(final HelloWorld helloWorld,
                        final SocketAddress socketAddress,
                        final ExecutorService executorService) {
        super();
        this.helloWorld = Objects.requireNonNull(helloWorld, "service is null");
        this.socketAddress = Objects.requireNonNull(
                socketAddress, "endpoint is null");
        this.executorService = Objects.requireNonNull(
                executorService, "executorService is null");
    }

    @Override
    public void open() throws IOException {
        close();
        datagramSocket = new DatagramSocket(null);
        if (socketAddress instanceof InetSocketAddress &&
            ((InetSocketAddress) socketAddress).getPort() > 0) {
            datagramSocket.setReuseAddress(true);
        }
        try {
            datagramSocket.bind(socketAddress);
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", socketAddress, ioe);
            throw ioe;
        }
        log.info("server is open; {}", datagramSocket.getLocalSocketAddress());
        LOCAL_PORT.set(datagramSocket.getLocalPort());
        final Thread thread = new Thread(() -> {
            while (!datagramSocket.isClosed()) {
                final DatagramPacket clientPacket
                        = new DatagramPacket(new byte[0], 0);
                try {
                    datagramSocket.receive(clientPacket);
                } catch (final IOException ioe) {
                    if (datagramSocket.isClosed()) {
                        break;
                    }
                    log.error("failed to receive", ioe);
                    continue;
                }
                executorService.submit(() -> {
                    final SocketAddress clientAddress
                            = clientPacket.getSocketAddress();
                    log.debug("[S] received from {}", clientAddress);
                    final byte[] array = new byte[HelloWorld.BYTES];
                    helloWorld.set(array);
                    final DatagramPacket serverPacket = new DatagramPacket(
                            array, array.length, clientAddress);
                    datagramSocket.send(serverPacket);
                    log.debug("[S] sent to {}", clientAddress);
                    return null;
                });
            }
            log.debug("shutting down {}", executorService);
            executorService.shutdown();
            log.debug("awaiting {} to be terminated", executorService);
            try {
                final boolean terminated = executorService.awaitTermination(
                        8L, TimeUnit.SECONDS);
                log.debug("terminated: {}", terminated);
            } catch (final InterruptedException ie) {
                log.error("interrupted", ie);
                Thread.currentThread().interrupt();
            }
            LOCAL_PORT.remove();
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void close() throws IOException {
        if (datagramSocket == null || datagramSocket.isClosed()) {
            return;
        }
        log.debug("closing {}", datagramSocket);
        datagramSocket.close();
    }

    private final HelloWorld helloWorld;

    private final SocketAddress socketAddress;

    private final ExecutorService executorService;

    private DatagramSocket datagramSocket;
}
