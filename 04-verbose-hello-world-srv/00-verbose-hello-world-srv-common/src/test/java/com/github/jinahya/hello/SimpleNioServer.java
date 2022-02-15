package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv1
 * %%
 * Copyright (C) 2018 - 2022 Jinahya, Inc.
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
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Callable;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.net.InetAddress.getLoopbackAddress;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.channels.SelectionKey.OP_WRITE;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
class SimpleNioServer {

    private static void server(final ServerSocketChannel server,
                               final Selector selector)
            throws IOException {
        server.configureBlocking(false);
        server.register(selector, OP_ACCEPT);
        while (!currentThread().isInterrupted()) {
//            log.debug("[S] in loop...");
            if (selector.select() == 0) {
                continue;
            }
            final var keys = selector.selectedKeys();
            for (final var key : keys) {
                if (key.isAcceptable()) {
                    final var channel = (ServerSocketChannel) key.channel();
                    final var client = channel.accept();
//                    log.debug("[S] connected from {}",
//                              client.getRemoteAddress());
                    client.configureBlocking(false);
                    client.register(selector, OP_WRITE);
                    continue;
                }
                if (key.isWritable()) {
                    final var channel = (SocketChannel) key.channel();
//                    log.debug("[S] writing to {}", channel);
                    for (final var b = allocate(1); b.hasRemaining(); ) {
                        channel.write(b);
                    }
                    channel.close();
                    continue;
                }
                log.warn("unhandled key: {}", key);
            }
            keys.clear();
        } // end-of-while
        log.debug("[S] out-of-loop");
        if (selector.selectNow() > 0) {
            final var keys = selector.selectedKeys();
            for (final var key : keys) {
                if (key.isWritable()) {
                    final var channel = (SocketChannel) key.channel();
                    final var attachment = (ByteBuffer) key.attachment();
                    if (attachment == null) {
                        key.attach(allocate(1));
                    } else if (!attachment.hasRemaining()) {
                        channel.close();
                    } else {
                        channel.write(attachment);
                    }
                    continue;
                }
                log.warn("unhandled key: {}", key);
            }
            keys.clear();
        }
    }

    private static void clients(final SocketAddress remote,
                                final Selector selector)
            throws Exception {
        final var count = 32;
        final var executor = newCachedThreadPool();
        for (int i = 0; i < count; i++) {
            executor.submit((Callable<Void>) () -> {
                final var client = SocketChannel.open();
                client.configureBlocking(false);
                if (client.connect(remote)) {
//                    log.debug("[C] connected, immediately");
//                    log.debug("[C] registering for OP_READ");
                    client.register(selector, OP_READ);
                } else {
//                    log.debug("[C] registering for OP_CONNECT");
                    client.register(selector, OP_CONNECT);
                }
                return null;
            });
        }
        log.debug("client thread(s) started");
        executor.shutdown();
        if (!executor.awaitTermination(4L, SECONDS)) {
            log.warn("executor not terminated!");
        }
        while (!currentThread().isInterrupted()) {
//            log.debug("[C] in loop...");
            if (selector.select() == 0) {
                continue;
            }
            final var keys = selector.selectedKeys();
            for (final var key : keys) {
                final var channel = (SocketChannel) key.channel();
                if (key.isConnectable()) {
                    try {
                        if (channel.finishConnect()) {
//                            log.debug("[C] connected to {}",
//                                      channel.getRemoteAddress());
                            key.interestOps(key.interestOps() & ~OP_CONNECT);
                            key.interestOps(key.interestOps() | OP_READ);
                        }
                    } catch (final IOException ioe) {
                        log.error("failed to finish connect", ioe);
                        channel.close();
                    }
                    continue;
                }
                if (key.isReadable()) {
//                    log.debug("[C] reading from {}",
//                              channel.getRemoteAddress());
                    for (final var b = allocate(1); b.hasRemaining(); ) {
                        if (channel.read(b) == -1) {
                            log.error("eof");
                        }
                    }
                    key.interestOps(key.interestOps() & ~OP_READ);
                    key.channel().close();
                    continue;
                }
                log.warn("unhandled key: {}", key);
            }
            keys.clear();
        } // end-of-while
        log.debug("[C] out-of-loop");
        if (selector.selectNow() > 0) {
            final var keys = selector.selectedKeys();
            for (final var key : keys) {
                final var channel = (SocketChannel) key.channel();
                if (key.isReadable()) {
                    final var attachment = (ByteBuffer) key.attachment();
                    if (attachment == null) {
                        key.attach(allocate(1));
                    } else if (!attachment.hasRemaining()) { // all read
                        channel.close();
                    } else {
//                    log.debug("[C] reading...");
                        if (channel.read(attachment) == -1) {
                            log.error("eof");
                        }
                    }
                    continue;
                }
                log.warn("unhandled key: {}", key);
            }
            keys.clear();
        }
    }

    public static void main(final String... args) throws Exception {
        getSystemClassLoader().setDefaultAssertionStatus(true);
        final var serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(getLoopbackAddress(), 0));
        log.debug("server bound to {}", serverSocket.getLocalAddress());

        final var serverSelector = Selector.open();
        final Thread serverThread = new Thread(() -> {
            try {
                server(serverSocket, serverSelector);
            } catch (final IOException ioe) {
                log.error("io error in server", ioe);
            }
        });
        serverThread.start();
        log.debug("server thread started");

        final var clientSelector = Selector.open();
        final var clientsThread = new Thread(() -> {
            try {
                clients(serverSocket.getLocalAddress(), clientSelector);
            } catch (final Exception e) {
                log.error("error in clients", e);
            }
        });
        clientsThread.start();
        log.debug("client(s) thread started");

        log.debug("sleeping...");
        sleep(10000L);
//        await().atLeast(ofSeconds(8L)).with().pollInterval(ofSeconds(8L));

        log.debug("interrupting the client(s) thread...");
        clientsThread.interrupt();
        clientsThread.join();
        log.debug("joined to the client(s) thread");

        log.debug("interrupting the server thread...");
        serverThread.interrupt();
        serverThread.join();
        log.debug("joined to the server thread...");
        serverSocket.close();
    }
}
