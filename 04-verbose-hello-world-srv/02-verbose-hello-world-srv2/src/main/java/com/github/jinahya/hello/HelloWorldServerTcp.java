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
import java.nio.file.Path;
import java.util.concurrent.Executors;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.IHelloWorldServerUtils.shutdownAndAwaitTermination;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerTcp
        extends AbstractHelloWorldServer {

    /**
     * Creates a new instance.
     */
    HelloWorldServerTcp() {
        super();
    }

    @Override
    void openInternal(final SocketAddress endpoint, final Path dir)
            throws IOException {
        server = new ServerSocket();
        if (endpoint instanceof InetSocketAddress &&
            ((InetSocketAddress) endpoint).getPort() > 0) {
            server.setReuseAddress(true);
        }
        try {
            server.bind(endpoint);
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", endpoint);
            throw ioe;
        }
        log.info("[S] server bound to {}", server.getLocalSocketAddress());
        if (dir != null) {
            IHelloWorldServerUtils.writePortNumber(dir, server.getLocalPort());
        }
        new Thread(() -> {
            final var executor = Executors.newCachedThreadPool();
            while (!server.isClosed()) {
                try {
                    final var client = server.accept();
                    executor.submit(() -> {
                        try (client) {
                            log.debug("[S] connected from {}",
                                      client.getRemoteSocketAddress());
                            final byte[] array = new byte[BYTES];
                            service().set(array);
                            client.getOutputStream().write(array);
                            client.getOutputStream().flush();
                        } catch (final IOException ioe) {
                            log.error("failed to send to {}",
                                      client.getRemoteSocketAddress(), ioe);
                        }
                    });
                } catch (final IOException ioe) {
                    if (server.isClosed()) {
                        break;
                    }
                    log.error("failed to accept", ioe);
                }
            } // end-of-while
            shutdownAndAwaitTermination(executor, 8L, SECONDS);
        }).start();
        log.debug("[S] server thread started");
    }

    @Override
    void closeInternal() throws IOException {
        if (server == null || server.isClosed()) {
            return;
        }
        server.close();
    }

    private ServerSocket server;
}
