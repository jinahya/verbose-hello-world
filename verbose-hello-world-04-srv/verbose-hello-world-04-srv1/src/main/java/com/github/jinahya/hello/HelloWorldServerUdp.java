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
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Objects;

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
     * @param service  an instance of {@link HelloWorld} interface.
     * @param endpoint a socket address to bind.
     */
    HelloWorldServerUdp(final HelloWorld service, final SocketAddress endpoint) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.endpoint = Objects.requireNonNull(endpoint, "endpoint is null");
    }

    @Override
    public synchronized void open() throws IOException {
        close();
        socket = new DatagramSocket(null);
        if (endpoint instanceof InetSocketAddress && ((InetSocketAddress) endpoint).getPort() > 0) {
            socket.setReuseAddress(true);
        }
        try {
            socket.bind(endpoint);
        } catch (final IOException ioe) {
            log.error("failed to bind; endpoint: {}", endpoint, ioe);
            throw ioe;
        }
        log.info("server is open; {}", socket.getLocalSocketAddress());
        LOCAL_PORT.set(socket.getLocalPort());
        new Thread(() -> {
            while (!socket.isClosed()) {
                // TODO: Receive an empty packet and send 'hello, world' bytes back to the client!
            }
            LOCAL_PORT.remove();
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

    private final SocketAddress endpoint;

    private DatagramSocket socket;
}
