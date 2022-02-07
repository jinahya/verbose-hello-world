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

import static com.github.jinahya.hello.HelloWorld.BYTES;

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
    public void open() throws IOException {
        close();
        datagramSocket = new DatagramSocket(null);
        if (socketAddress instanceof InetSocketAddress
            && ((InetSocketAddress) socketAddress).getPort() > 0) {
            datagramSocket.setReuseAddress(true);
        }
        try {
            datagramSocket.bind(socketAddress);
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", socketAddress, ioe);
            throw ioe;
        }
        log.info("[S] server bound to {}", datagramSocket.getLocalSocketAddress());
        LOCAL_PORT.set(datagramSocket.getLocalPort());
        new Thread(() -> {
            while (!datagramSocket.isClosed()) {
                final var clientPacket = new DatagramPacket(new byte[0], 0);
                try {
                    datagramSocket.receive(clientPacket);
                    final var clientAddress = clientPacket.getSocketAddress();
                    log.debug("[S] received from {}", clientAddress);
                    new Thread(() -> {
                        final var array = new byte[BYTES];
                        helloWorld().set(array);
                        final var serverPacket = new DatagramPacket(
                                array, array.length, clientAddress);
                        try {
                            datagramSocket.send(serverPacket);
                            log.debug("[S] send to {}", clientAddress);
                        } catch (final IOException ioe) {
                            log.error("failed to send", ioe);
                        }
                    }).start();
                } catch (final IOException ioe) {
                    if (datagramSocket.isClosed()) {
                        break;
                    }
                    log.error("failed to receive", ioe);
                }
            }
            LOCAL_PORT.remove();
            datagramSocket = null;
        }).start();
        log.debug("[S] server thread started");
    }

    @Override
    public void close() throws IOException {
        if (datagramSocket == null || datagramSocket.isClosed()) {
            return;
        }
        datagramSocket.close();
    }

    private DatagramSocket datagramSocket;
}
