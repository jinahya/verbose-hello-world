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
import java.nio.file.Path;
import java.util.concurrent.Executors;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerUdp
        extends AbstractHelloWorldServer {

    /**
     * Creates a new instance.
     */
    HelloWorldServerUdp() {
        super();
    }

    @Override
    void openInternal(final SocketAddress endpoint, final Path dir)
            throws IOException {
        socket = new DatagramSocket(null);
        if (endpoint instanceof InetSocketAddress
            && ((InetSocketAddress) endpoint).getPort() > 0) {
            socket.setReuseAddress(true);
        }
        try {
            socket.bind(endpoint);
            log.info("[S] server bound to {}", socket.getLocalSocketAddress());
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        if (dir != null) {
            IHelloWorldServerUtils.writePortNumber(dir, socket.getLocalPort());
        }
        new Thread(() -> {
            final var executor = Executors.newCachedThreadPool();
            while (!socket.isClosed()) {
                final var array = new byte[HelloWorld.BYTES];
                final var received = new DatagramPacket(array, array.length);
                try {
                    socket.receive(received);
                    executor.submit(() -> {
                        final var address = received.getSocketAddress();
                        log.debug("[S] received from {}", address);
                        service().set(array);
                        final var sending = new DatagramPacket(
                                array, array.length, address);
                        try {
                            socket.send(sending);
                            log.debug("[S] sent to {}", address);
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
            } // end-of-while
            IHelloWorldServerUtils.shutdownAndAwaitTermination(executor);
        }).start();
        log.debug("[S] server thread started");
    }

    @Override
    void closeInternal() throws IOException {
        if (socket == null || socket.isClosed()) {
            return;
        }
        socket.close();
    }

    private DatagramSocket socket;
}
