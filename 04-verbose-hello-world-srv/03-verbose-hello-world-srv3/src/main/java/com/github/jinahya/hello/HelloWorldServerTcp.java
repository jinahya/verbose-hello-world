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

    private void handle(final Set<SelectionKey> keys, final Selector selector)
            throws IOException {
        requireNonNull(keys, "keys is null");
        requireNonNull(selector, "selector is null");
        for (final SelectionKey key : keys) {
            if (key.isAcceptable()) { // server; ready to accept
                final var channel = (ServerSocketChannel) key.channel();
                final var client = channel.accept();
                log.debug("[S] accepted from {}", client.getRemoteAddress());
                client.configureBlocking(false);
                client.register(selector, OP_WRITE);
                continue;
            }
            if (key.isWritable()) { // client; ready to write
                final SocketChannel channel = ((SocketChannel) key.channel());
                helloWorld().write(channel);
                log.debug("[S] written to {}", channel.getRemoteAddress());
                key.channel().close();
                continue;
            }
            log.warn("unhandled key: {}; opts: {}", key, key.interestOps());
        }
        keys.clear();
    }

    private void open_() throws IOException {
        close();
        server = ServerSocketChannel.open();
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
        final var selector = Selector.open();
        server.configureBlocking(false);
        server.register(selector, OP_ACCEPT);
        new Thread(() -> {
            while (!server.socket().isClosed()) {
                try {
                    if (selector.select() == 0) {
                        continue;
                    }
                    handle(selector.selectedKeys(), selector);
                } catch (final IOException ioe) {
                    if (server.socket().isClosed()) {
                        break;
                    }
                    log.error("failed to work", ioe);
                }
            }
            try {
                if (selector.select() > 0) {
                    handle(selector.selectedKeys(), selector);
                }
                selector.close();
            } catch (final IOException ioe) {
                log.error("failed to close {}", selector, ioe);
            }
            PORT.remove();
            server = null;
        }).start();
        log.debug("server thread started");
    }

    @Override
    public void open() throws IOException {
        try {
            lock.lock();
            open_();
        } finally {
            lock.unlock();
        }
    }

    private void close_() throws IOException {
        if (server == null || server.socket().isClosed()) {
            return;
        }
        server.close();
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

    private final Lock lock = new ReentrantLock();

    private ServerSocketChannel server;
}
