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
     * Creates a new instance with specified local socket address to bind.
     *
     * @param endpoint the local socket address to bind.
     */
    HelloWorldServerUdp(final SocketAddress endpoint) {
        super(endpoint);
    }

    @Override
    public void open() throws IOException {
        close();
        socket = new DatagramSocket(null);
        if (endpoint instanceof InetSocketAddress &&
            ((InetSocketAddress) endpoint).getPort() > 0) {
            socket.setReuseAddress(true);
        }
        try {
            socket.bind(endpoint);
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("server bound to {}", socket.getLocalSocketAddress());
        PORT.set(socket.getLocalPort());
        new Thread(() -> {
            while (!socket.isClosed()) {
                final DatagramPacket clientPacket
                        = new DatagramPacket(new byte[0], 0);
                try {
                    socket.receive(clientPacket);
                } catch (final IOException ioe) {
                    if (socket.isClosed()) {
                        break;
                    }
                    log.error("failed to receive", ioe);
                    continue;
                }
            }
            PORT.remove();
            socket = null;
        }).start();
        log.debug("[S] server thread started");
    }

    @Override
    public void close() throws IOException {
        if (socket == null || socket.isClosed()) {
            return;
        }
        socket.close();
    }

    private DatagramSocket socket;
}
