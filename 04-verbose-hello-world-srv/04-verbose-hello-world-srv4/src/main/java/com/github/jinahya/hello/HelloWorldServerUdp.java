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
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Thread.currentThread;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;

/**
 * A class serves {@code hello, world} to clients.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldServerUdp
        extends AbstractHelloWorldServer {

    static final ThreadLocal<Integer> PORT = new ThreadLocal<>();

    /**
     * Creates a new instance with specified local socket address to bind.
     *
     * @param endpoint the local socket address to bind.
     */
    protected HelloWorldServerUdp(final SocketAddress endpoint) {
        super(endpoint);
    }

    private void handle(final Set<SelectionKey> keys) throws IOException {
        for (final var key : keys) {
            final var channel = (DatagramChannel) key.channel();
            if (key.isReadable()) {
                final var address = channel.receive(allocate(0));
                if (address != null) {
                    log.debug("received from {}", address);
                    key.interestOps(key.interestOps() & ~OP_READ);
                    key.interestOps(key.interestOps() | OP_WRITE);
                    key.attach(address);
                }
                continue;
            }
            if (key.isWritable()) {
                final var src = allocate(BYTES);
                service().put(src);
                // TODO: flip the src!
                final var target = (SocketAddress) key.attachment();
                if (channel.send(src, target) == src.capacity()) {
                    key.interestOps(key.interestOps() & ~OP_WRITE);
                    key.interestOps(key.interestOps() | OP_READ);
                }
                continue;
            }
            log.warn("unhandled key: {}", key);
        }
        keys.clear();
    }

    private void open_() throws IOException {
        final var server = DatagramChannel.open();
        if (endpoint instanceof InetSocketAddress &&
            ((InetSocketAddress) endpoint).getPort() > 0) {
            server.socket().setReuseAddress(true);
        }
        try {
            server.bind(endpoint);
        } catch (final IOException ioe) {
            log.error("failed to bind to {}", endpoint, ioe);
            throw ioe;
        }
        log.info("server bound to {}", server.getLocalAddress());
        PORT.set(server.socket().getLocalPort());
        new Thread(() -> {
            try (var selector = Selector.open()) {
                server.configureBlocking(false);
                server.register(selector, OP_READ);
                while (!currentThread().isInterrupted()) {
                    if (selector.select() == 0) {
                        continue;
                    }
                    handle(selector.selectedKeys());
                } // end-of-while
                if (selector.selectNow() > 0) {
                    handle(selector.selectedKeys());
                }
            } catch (final IOException ioe) {
                log.error("io error in server thread", ioe);
            }
            PORT.remove();
        }).start();
        log.debug("[S] server thread started");
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
        if (thead == null || !thead.isAlive()) {
            return;
        }
        thead.interrupt();
        try {
            thead.join();
        } catch (final InterruptedException ie) {
            log.error("interrupted while joining server thread", ie);
            currentThread().interrupt();
        }
        thead = null;
        if (server == null || !server.isOpen()) {
            return;
        }
        server.close();
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

    private final Lock lock = new ReentrantLock();

    private Thread thead;

    private DatagramChannel server;
}
