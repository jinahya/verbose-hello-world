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
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerTcp
        extends AbstractHelloWorldServer {

    static final ThreadLocal<Integer> PORT = new ThreadLocal<>();

    /**
     * Creates a new instance.
     *
     * @param socketAddress a socket address to bind.
     */
    HelloWorldServerTcp(final SocketAddress socketAddress) {
        super(socketAddress);
    }

    @Override
    public synchronized void open() throws IOException {
        close();
        server = new ServerSocket();
        if (endpoint instanceof InetSocketAddress
            && ((InetSocketAddress) endpoint).getPort() > 0) {
            server.setReuseAddress(true);
        }
        try {
            server.bind(endpoint);
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("server bound to {}", server.getLocalSocketAddress());
        PORT.set(server.getLocalPort());
        new Thread(() -> {
            while (!server.isClosed()) {
                try (var client = server.accept()) {
                    log.debug("[S] connected from {}",
                              client.getRemoteSocketAddress());
                    // TODO: Send "hello, world" bytes through the socket!
                } catch (final IOException ioe) {
                    if (server.isClosed()) {
                        break;
                    }
                    log.error("failed to accept/send", ioe);
                }
            }
            PORT.remove();
            server = null;
        }).start();
        log.debug("server thread started");
    }

    @Override
    public synchronized void close() throws IOException {
        if (server == null || server.isClosed()) {
            return;
        }
        server.close();
    }

    private ServerSocket server;
}
