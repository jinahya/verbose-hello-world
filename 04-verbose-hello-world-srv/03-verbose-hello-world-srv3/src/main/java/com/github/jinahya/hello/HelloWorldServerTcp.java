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
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
class HelloWorldServerTcp
        extends AbstractHelloWorldServer {

    static final ThreadLocal<Integer> PORT = new ThreadLocal<>();

    /**
     * Creates a new instance with specified local address to bind.
     *
     * @param endpoint the local socket address to bind.
     */
    HelloWorldServerTcp(final SocketAddress endpoint) {
        super(endpoint);
    }

    /**
     * Handles specified selection keys.
     *
     * @param keys     the selection keys to handle.
     * @param selector a selector.
     * @throws IOException if an I/O error occurs.
     */
    private void handle(final Set<SelectionKey> keys, final Selector selector)
            throws IOException {
        requireNonNull(keys, "keys is null");
        if (keys.isEmpty()) {
            throw new IllegalArgumentException("empty keys");
        }
        requireNonNull(selector, "selector is null");
        for (final var key : keys) {
            if (key.isAcceptable()) { // server; ready to accept
                final var channel = (ServerSocketChannel) key.channel();
                final var client = channel.accept();
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

    private void open_() throws IOException {
        final var server = ServerSocketChannel.open();
        if (endpoint instanceof InetSocketAddress
            && ((InetSocketAddress) endpoint).getPort() > 0) {
            server.setOption(SO_REUSEADDR, TRUE);
        }
        try {
            server.bind(endpoint);
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("server bound to {}", server.getLocalAddress());
        PORT.set(server.socket().getLocalPort());
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
            } catch (final IOException ioe) {
                log.error("io error in server thread", ioe);
            }
            log.debug("end of server thread");
        });
        thread.start();
        log.debug("server thread started");
    }

    @Override
    public void open() throws IOException {
        try {
            lock.lock();
            close();
            open_();
        } finally {
            lock.unlock();
        }
    }

    private void close_() throws IOException {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join();
            } catch (final InterruptedException ie) {
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

    @Override
    public void close() throws IOException {
        try {
            lock.lock();
            close_();
        } finally {
            lock.unlock();
        }
    }

    // a lock to synchronize method calls.
    private final Lock lock = new ReentrantLock();

    private ServerSocketChannel server;

    private Thread thread;
}
