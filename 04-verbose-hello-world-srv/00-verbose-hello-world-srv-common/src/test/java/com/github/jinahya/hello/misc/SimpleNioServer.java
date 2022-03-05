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

    private static void server(ServerSocketChannel server, Selector selector) throws IOException {
        server.configureBlocking(false);
        server.register(selector, OP_ACCEPT);
        while (!currentThread().isInterrupted()) {
//            log.debug("[S] in loop...");
            if (selector.select() == 0) {
                continue;
            }
            var keys = selector.selectedKeys();
            for (var key : keys) {
                if (key.isAcceptable()) {
                    var channel = (ServerSocketChannel) key.channel();
                    var client = channel.accept();
//                    log.debug("[S] connected from {}",
//                              client.getRemoteAddress());
                    client.configureBlocking(false);
                    client.register(selector, OP_WRITE);
                    continue;
                }
                if (key.isWritable()) {
                    var channel = (SocketChannel) key.channel();
//                    log.debug("[S] writing to {}", channel);
                    for (var b = allocate(1); b.hasRemaining(); ) {
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
            var keys = selector.selectedKeys();
            for (var key : keys) {
                if (key.isWritable()) {
                    var channel = (SocketChannel) key.channel();
                    var attachment = (ByteBuffer) key.attachment();
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

    private static void clients(SocketAddress remote, Selector selector) throws Exception {
        var count = 32;
        var executor = newCachedThreadPool();
        for (int i = 0; i < count; i++) {
            executor.submit((Callable<Void>) () -> {
                var client = SocketChannel.open();
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
            var keys = selector.selectedKeys();
            for (var key : keys) {
                var channel = (SocketChannel) key.channel();
                if (key.isConnectable()) {
                    try {
                        if (channel.finishConnect()) {
//                            log.debug("[C] connected to {}",
//                                      channel.getRemoteAddress());
                            key.interestOps(key.interestOps() & ~OP_CONNECT);
                            key.interestOps(key.interestOps() | OP_READ);
                        }
                    } catch (IOException ioe) {
                        log.error("failed to finish connect", ioe);
                        channel.close();
                    }
                    continue;
                }
                if (key.isReadable()) {
//                    log.debug("[C] reading from {}",
//                              channel.getRemoteAddress());
                    for (var b = allocate(1); b.hasRemaining(); ) {
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
            var keys = selector.selectedKeys();
            for (var key : keys) {
                var channel = (SocketChannel) key.channel();
                if (key.isReadable()) {
                    var attachment = (ByteBuffer) key.attachment();
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

    public static void main(String... args) throws Exception {
        getSystemClassLoader().setDefaultAssertionStatus(true);
        var serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(getLoopbackAddress(), 0));
        log.debug("server bound to {}", serverSocket.getLocalAddress());
        var serverSelector = Selector.open();
        Thread serverThread = new Thread(() -> {
            try {
                server(serverSocket, serverSelector);
            } catch (IOException ioe) {
                log.error("io error in server", ioe);
            }
        });
        serverThread.start();
        log.debug("server thread started");

        var clientSelector = Selector.open();
        var clientsThread = new Thread(() -> {
            try {
                clients(serverSocket.getLocalAddress(), clientSelector);
            } catch (Exception e) {
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
