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

    static final ThreadLocal<Integer> LOCAL_PORT = new ThreadLocal<>();

    /**
     * Creates a new instance.
     *
     * @param socketAddress a socket address to bind.
     */
    HelloWorldServerUdp(final SocketAddress socketAddress) {
        super(socketAddress);
    }

    @Override
    public synchronized void open() throws IOException {
        close();
        datagramSocket = new DatagramSocket(null);
        if (socketAddress instanceof InetSocketAddress
            && ((InetSocketAddress) socketAddress).getPort() > 0) {
            datagramSocket.setReuseAddress(true);
        }
        try {
            datagramSocket.bind(socketAddress);
        } catch (IOException ioe) {
            log.error("failed to bind to {}", socketAddress, ioe);
            throw ioe;
        }
        log.info("server bound to {}", datagramSocket.getLocalSocketAddress());
        LOCAL_PORT.set(datagramSocket.getLocalPort());
        final var thread = new Thread(() -> {
            while (!datagramSocket.isClosed()) {
                final var packet = new DatagramPacket(new byte[0], 0);
                try {
                    datagramSocket.receive(packet);
                } catch (final IOException ioe) {
                    if (datagramSocket.isClosed()) {
                        break;
                    }
                    log.error("failed to receive", ioe);
                    continue;
                }
                final var clientAddress = packet.getSocketAddress();
                log.debug("[S] received from {}", clientAddress);
                // TODO: Send "hello, world" bytes back to the client!
            }
            LOCAL_PORT.remove();
            datagramSocket = null;
        });
        thread.start();
        log.debug("server thread started.");
    }

    @Override
    public synchronized void close() throws IOException {
        if (datagramSocket == null || datagramSocket.isClosed()) {
            return;
        }
        datagramSocket.close();
    }

    private DatagramSocket datagramSocket;
}
