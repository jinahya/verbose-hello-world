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

import com.github.jinahya.hello.util.HelloWorldServerUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
class HelloWorldServerTcp1
        implements HelloWorldServer {

    @Override
    public synchronized void open(SocketAddress endpoint, Path dir) throws IOException {
        Objects.requireNonNull(endpoint, "endpoint is null");
        if (dir != null && !Files.isDirectory(dir)) {
            throw new IllegalArgumentException("not a directory: " + dir);
        }
        close();
        server = new ServerSocket();
        if (endpoint instanceof InetSocketAddress
            && ((InetSocketAddress) endpoint).getPort() > 0) {
            server.setReuseAddress(true);
        }
        try {
            server.bind(endpoint);
        } catch (IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("[S] bound to {}", server.getLocalSocketAddress());
        if (dir != null) {
            var port = server.getLocalPort();
            HelloWorldServerUtils.writePortNumber(dir, port);
        }
        var thread = new Thread(() -> {
            while (true) {
                try (var client = server.accept()) {
                    var address = client.getRemoteSocketAddress();
                    log.debug("[S] connected from {}", address);
                    var array = new byte[HelloWorld.BYTES];
                    service().set(array);
                    client.getOutputStream().write(array);
                    client.getOutputStream().flush();
                    log.debug("[S] written to {}", address);
                } catch (IOException ioe) {
                    if (server.isClosed()) {
                        break;
                    }
                    log.error("failed to serve", ioe);
                }
            }
        });
        thread.start();
        log.debug("[S] server thread started");
    }

    @Override
    public synchronized void close() throws IOException {
        if (server != null && !server.isClosed()) {
            server.close();
            log.debug("[S] server closed");
        }
    }

    private ServerSocket server;
}
