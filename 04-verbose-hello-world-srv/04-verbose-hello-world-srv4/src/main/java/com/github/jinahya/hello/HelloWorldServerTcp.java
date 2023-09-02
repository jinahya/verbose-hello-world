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
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Path;
import java.util.Set;

import static java.lang.Boolean.TRUE;
import static java.lang.Thread.currentThread;
import static java.net.StandardSocketOptions.SO_REUSEADDR;
import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_WRITE;
import static java.util.Objects.requireNonNull;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerTcp extends AbstractHelloWorldServer {

    /**
     * Creates a new instance with specified local address to bind.
     */
    public HelloWorldServerTcp() {
        super();
    }

    /**
     * Handles specified selection keys.
     *
     * @param keys     the selection keys to handle.
     * @param selector a selector.
     * @throws IOException if an I/O error occurs.
     */
    private void handle(Set<SelectionKey> keys, Selector selector) throws IOException {
        requireNonNull(keys, "keys is null");
        if (keys.isEmpty()) {
            throw new IllegalArgumentException("empty keys");
        }
        requireNonNull(selector, "selector is null");
        for (var key : keys) {
            if (key.isAcceptable()) { // server; ready to accept
                var channel = (ServerSocketChannel) key.channel();
                var client = channel.accept();
                log.debug("[S] accepted from {}", client.getRemoteAddress());
                client.configureBlocking(false);
                client.register(selector, OP_WRITE);
                continue;
            }
            if (key.isWritable()) { // client; ready to write
                try (var channel = ((SocketChannel) key.channel())) {
                    service().write(channel);
                    log.debug("[S] written to {}", channel.getRemoteAddress());
                }
                continue;
            }
            log.warn("unhandled key: {}", key);
        }
        keys.clear();
    }

    @Override
    protected void openInternal(SocketAddress endpoint, Path dir) throws IOException {
        var server = ServerSocketChannel.open();
        if (endpoint instanceof InetSocketAddress
            && ((InetSocketAddress) endpoint).getPort() > 0) {
            server.setOption(SO_REUSEADDR, TRUE);
        }
        try {
            server.bind(endpoint);
        } catch (IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("server bound to {}", server.getLocalAddress());
        if (dir != null) {
            HelloWorldServerUtils.writePortNumber(dir, server.socket()
                    .getLocalPort());
        }
        thread = new Thread(() -> {
            try (var selector = Selector.open()) {
                server.configureBlocking(false);
                server.register(selector, OP_ACCEPT);
                while (!currentThread().isInterrupted()) {
                    if (selector.select() == 0) {
                        continue;
                    }
                    handle(selector.selectedKeys(), selector);
                } // end-of-while
                log.debug("[S] out of loop");
                server.keyFor(selector).cancel();
                if (selector.selectNow() > 0) {
                    handle(selector.selectedKeys(), selector);
                }
            } catch (IOException ioe) {
                log.error("io error in server thread", ioe);
            }
            log.debug("end of server thread");
        });
        thread.start();
        log.debug("server thread started");
    }

    @Override
    protected void closeInternal() throws IOException {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException ie) {
                log.error("interrupted while joining server thread", ie);
                currentThread().interrupt();
            }
        }
        thread = null;
        if (server != null && server.isOpen()) {
            server.close();
        }
        server = null;
    }

    private ServerSocketChannel server;

    private Thread thread;
}
