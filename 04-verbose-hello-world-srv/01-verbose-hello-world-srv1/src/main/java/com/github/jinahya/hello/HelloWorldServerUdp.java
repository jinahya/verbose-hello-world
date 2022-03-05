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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerUdp implements HelloWorldServer {

    @Override
    public synchronized void open(SocketAddress endpoint, Path dir) throws IOException {
        Objects.requireNonNull(endpoint, "endpoint is null");
        if (dir != null && !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        close();
        socket = new DatagramSocket(null);
        if (endpoint instanceof InetSocketAddress && ((InetSocketAddress) endpoint).getPort() > 0) {
            socket.setReuseAddress(true);
        }
        try {
            socket.bind(endpoint);
        } catch (IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("[S] bound to {}", socket.getLocalSocketAddress());
        if (dir != null) {
            HelloWorldServerUtils.writePortNumber(dir, socket.getLocalPort());
        }
        var thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                var received = new DatagramPacket(new byte[1], 1);
                try {
                    socket.receive(received);
                } catch (IOException ioe) {
                    if (socket.isClosed()) {
                        break;
                    }
                    log.error("failed to receive", ioe);
                    continue;
                }
                var address = received.getSocketAddress();
                log.debug("[S] received from {}", address);
                var array = new byte[HelloWorld.BYTES];
                service().set(array);
                var sending = new DatagramPacket(array, array.length, address);
                try {
                    socket.send(sending);
                    log.debug("[S] sent to {}", address);
                } catch (IOException ioe) {
                    if (socket.isClosed()) {
                        break;
                    }
                    log.error("failed to send to {}", address, ioe);
                }
            } // end-of-while
        });
        thread.start();
        log.debug("[S] server thread started");
    }

    @Override
    public synchronized void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
            log.debug("[S] server closed");
        }
    }

    private DatagramSocket socket;
}
