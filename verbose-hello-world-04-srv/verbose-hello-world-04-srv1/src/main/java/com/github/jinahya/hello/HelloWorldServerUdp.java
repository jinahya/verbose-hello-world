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
import java.net.SocketAddress;
import java.util.Objects;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerUdp implements IHelloWorldServer {

    static final ThreadLocal<SocketAddress> ENDPOINT = new ThreadLocal<>();

    /**
     * Creates a new instance.
     *
     * @param service an instance of {@link HelloWorld} interface.
     * @param address a socket address to bind.
     */
    HelloWorldServerUdp(final HelloWorld service, final SocketAddress address) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.address = Objects.requireNonNull(address, "address is null");
    }

    @Override
    public synchronized void open() throws IOException {
        if (socket != null) {
            throw new IllegalStateException("already started");
        }
        socket = new DatagramSocket();
        try {
            socket.bind(address);
        } catch (final IOException ioe) {
            log.error("failed to bind the datagram socket; addr: {}", address);
            close();
            throw ioe;
        }
        ENDPOINT.set(socket.getLocalSocketAddress());
        log.info("server is open; {}", ENDPOINT.get());
        new Thread(() -> {
            while (!socket.isClosed()) {
                final DatagramPacket packet = new DatagramPacket(new byte[0], 0);
                try {
                    socket.receive(packet);
                    final byte[] array = service.set(new byte[HelloWorld.BYTES]);
                    socket.send(new DatagramPacket(array, array.length, packet.getSocketAddress()));
                } catch (final IOException ioe) {
                    if (socket.isClosed()) {
                        break;
                    }
                    log.error("failed to receive/send", ioe);
                }
            }
            ENDPOINT.remove();
        }).start();
    }

    @Override
    public synchronized void close() throws IOException {
        if (socket == null || socket.isClosed()) {
            return;
        }
        socket.close();
    }

    private final HelloWorld service;

    private final SocketAddress address;

    private DatagramSocket socket;
}