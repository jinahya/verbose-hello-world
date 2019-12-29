package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv
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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.SelectionKey.OP_CONNECT;
import static java.nio.channels.SelectionKey.OP_READ;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.concurrent.CompletableFuture.runAsync;

@Slf4j
abstract class AbstractHelloWorldMain {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Starts a new daemon thread which reads lines from {@link System#in} and closes specified closeable for "{@code
     * quit}".
     *
     * @param closeable the closeable to close.
     */
    static void readAndClose(final Closeable closeable) {
        if (closeable == null) {
            throw new NullPointerException("closeable is null");
        }
        final Thread thread = new Thread(() -> {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                try {
                    for (String line; (line = reader.readLine()) != null; ) {
                        if (line.equalsIgnoreCase("quit")) {
                            break;
                        }
                    }
                } finally {
                    closeable.close();
                }
            } catch (final IOException ioe) {
                log.error("failed to read and close", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Connects to specified socket address and reads {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and
     * prints it as a {@link StandardCharsets#US_ASCII US-ASCII} string followed by a new line character.
     *
     * @param remote the socket address to connect.
     */
    private static void connectAndPrint(final SocketAddress remote) throws IOException {
        try (Socket client = new Socket()) {
            client.connect(remote);
            final byte[] array = new byte[BYTES];
            new DataInputStream(client.getInputStream()).readFully(array);
            System.out.printf("%s%n", new String(array, US_ASCII));
        }
    }

    /**
     * Starts a new daemon thread which connects to specified server socket's local address and reads {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and prints it as a {@link StandardCharsets#US_ASCII US-ASCII}
     * string followed by a new line character.
     *
     * @param server the server socket to connect.
     * @see #connectAndPrint(SocketAddress)
     */
    static void connectAndPrint(final ServerSocket server) {
        final Thread thread = new Thread(() -> {
            try {
                connectAndPrint(server.getLocalSocketAddress());
            } catch (final IOException ioe) {
                log.error("failed to connect and print", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Connects to specified socket address and reads {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and
     * prints it as a {@link StandardCharsets#US_ASCII US-ASCII} string followed by a new line character.
     *
     * @param remote the socket address to connect.
     * @throws IOException if an I/O error occurs.
     */
    private static void connectAndPrintBlocking(final SocketAddress remote) throws IOException {
        try (SocketChannel client = SocketChannel.open()) {
            client.configureBlocking(true);
            final boolean connected = client.connect(remote);
            assert connected;
            final ByteBuffer buffer = allocate(BYTES);
            while (buffer.hasRemaining()) {
                final int read = client.read(buffer);
            }
            System.out.printf("%s%n", US_ASCII.decode(buffer).toString());
        }
    }

    /**
     * Starts a new thread which connects to specified server socket channel's local address and reads {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and prints it as a {@link StandardCharsets#US_ASCII US-ASCII}
     * string followed by a new line character.
     *
     * @param server the server socket channel to connect.
     * @see #connectAndPrintBlocking(SocketAddress)
     */
    static void connectAndPrintBlocking(final ServerSocketChannel server) {
        final Thread thread = new Thread(() -> {
            try {
                connectAndPrintBlocking(server.getLocalAddress());
            } catch (final IOException ioe) {
                log.error("failed to connect and print", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Connects to specified socket address and reads {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and
     * prints it as a {@link StandardCharsets#US_ASCII US-ASCII} string followed by a new line character.
     *
     * @param remote the server socket channel to connect.
     * @throws IOException if an I/O error occurs.
     * @see ServerSocketChannel#socket()
     * @see Socket#getLocalAddress()
     */
    private static void connectAndPrintNonBlocking(final SocketAddress remote) throws IOException {
        final Selector selector = Selector.open();
        try (SocketChannel client = SocketChannel.open()) {
            client.configureBlocking(false);
            final SelectionKey key;
            if (client.connect(remote)) {
                key = client.register(selector, OP_READ, allocate(BYTES));
            } else {
                key = client.register(selector, OP_CONNECT, null);
            }
            while (key.isValid()) {
                final int keys = selector.select();
                for (final Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext(); i.remove()) {
                    final SelectionKey selectionKey = i.next();
                    if (selectionKey.isConnectable()) {
                        if (client.finishConnect()) {
                            selectionKey.attach(allocate(BYTES));
                            selectionKey.interestOps(OP_READ);
                        }
                    } else if (selectionKey.isReadable()) {
                        final ReadableByteChannel channel = (ReadableByteChannel) selectionKey.channel();
                        final ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                        if (channel.read(buffer) == -1) {
                            log.error("reached to an unexpected end of stream");
                            channel.close();
                            continue;
                        }
                        if (!buffer.hasRemaining()) { // all bytes has been read
                            System.out.printf("%s%n", US_ASCII.decode(buffer).toString());
                            selectionKey.cancel();
                            channel.close();
                        }
                    }
                }
            }
        }
        selector.close();
    }

    /**
     * Starts a new thread which connects to specified server socket channel's local address and reads {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and prints it as a {@link StandardCharsets#US_ASCII US-ASCII}
     * string followed by a new line character.
     *
     * @param server the server socket channel to connect.
     * @see #connectAndPrintNonBlocking(SocketAddress)
     */
    static void connectAndPrintNonBlocking(final ServerSocketChannel server) {
        final Thread thread = new Thread(() -> {
            try {
                connectAndPrintNonBlocking(server.getLocalAddress());
            } catch (final IOException ioe) {
                log.error("failed to connect and print", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Connects to specified socket address and reads {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and
     * prints it as a {@link StandardCharsets#US_ASCII US-ASCII} string followed by a new line character.
     *
     * @param remote the server socket channel to connect.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while getting the result.
     * @throws ExecutionException   if failed to execute.
     */
    private static void connectAndPrintAsynchronous(SocketAddress remote)
            throws IOException, InterruptedException, ExecutionException {
        remote = new InetSocketAddress(InetAddress.getLocalHost(), ((InetSocketAddress)remote).getPort());
        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        final Void connected = client.connect(remote).get();
        final ByteBuffer buffer = allocate(BYTES);
        while (buffer.hasRemaining()) {
            final int read = client.read(buffer).get();
        }
        System.out.printf("%s%n", US_ASCII.decode(buffer).toString());
    }

    /**
     * Starts a new thread which connects to specified asynchronous server socket channel's local address and reads
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and prints it as a {@link StandardCharsets#US_ASCII
     * US-ASCII} string followed by a new line character.
     *
     * @param server the server socket channel to connect.
     * @see #connectAndPrintAsynchronous(SocketAddress)
     */
    static void connectAndPrintAsynchronous(final AsynchronousServerSocketChannel server) {
        final Thread thread = new Thread(() -> {
            try {
                final Void result = runAsync(() -> {
                    try {
                        connectAndPrintAsynchronous(server.getLocalAddress());
                    } catch (final Exception e) {
                        log.error("failed to connect and print", e);
                    }
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("failed to complete", e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Connects to specified socket address and reads {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and
     * prints it as a {@link StandardCharsets#US_ASCII US-ASCII} string followed by a new line character.
     *
     * @param remote the server socket channel to connect.
     * @throws IOException if an I/O error occurs.
     * @see ServerSocketChannel#socket()
     * @see Socket#getLocalAddress()
     */
    private static void connectAndPrintAsynchronous2(final SocketAddress remote) throws IOException {
        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(remote, null, new CompletionHandler<Void, Void>() {

            @Override
            public void completed(final Void result, final Void attachment) {
                final ByteBuffer buffer = allocate(BYTES);
                client.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {

                    @Override
                    public void completed(final Integer result, final ByteBuffer attachment) {
                        if (!attachment.hasRemaining()) {
                            attachment.flip();
                            System.out.printf("%s%n", US_ASCII.decode(attachment).toString());
                            try {
                                client.close();
                            } catch (final IOException ioe) {
                                log.error("failed to close client", ioe);
                            }
                            return;
                        }
                        client.read(attachment, attachment, this);
                    }

                    @Override
                    public void failed(final Throwable exc, final ByteBuffer attachment) {
                        log.error("failed to read", exc);
                    }
                });
            }

            @Override
            public void failed(final Throwable exc, final Void attachment) {
                log.error("failed to connect to {}", remote, exc);
            }
        });
    }

    /**
     * Starts a new thread which connects to specified server socket channel's local address and reads {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and prints it as a {@link StandardCharsets#US_ASCII US-ASCII}
     * string followed by a new line character.
     *
     * @param server the server socket channel to connect.
     * @see #connectAndPrintNonBlocking(SocketAddress)
     */
    static void connectAndPrintAsynchronous2(final AsynchronousServerSocketChannel server) {
        final Thread thread = new Thread(() -> {
            try {
                final Void result = runAsync(() -> {
                    try {
                        connectAndPrintAsynchronous2(server.getLocalAddress());
                    } catch (final IOException ioe) {
                        log.error("failed to connect and print", ioe);
                    }
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("failed to complete", e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Connects to specified socket address and reads {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and
     * prints it as a {@link StandardCharsets#US_ASCII US-ASCII} string followed by a new line character.
     *
     * @param remote the server socket channel to connect.
     * @throws IOException if an I/O error occurs.
     * @see ServerSocketChannel#socket()
     * @see Socket#getLocalAddress()
     */
    private static void connectAndPrintAsynchronous3(final SocketAddress remote) throws IOException {
        final AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(remote, null, new ConnectionCompletionHandler<Void>(
                (v, a1) -> {
                    final ByteBuffer buffer = allocate(BYTES);
                    client.read(buffer, buffer, new ReadingCompletionHandler<>(
                            (r, a2) -> {
                                final ByteBuffer attachment = a2.getAttachment();
                                if (!attachment.hasRemaining()) {
                                    attachment.flip();
                                    System.out.printf("%s%n", US_ASCII.decode(attachment).toString());
                                    try {
                                        client.close();
                                    } catch (final IOException ioe) {
                                        log.error("failed to close client", ioe);
                                    }
                                    return;
                                }
                                client.read(attachment, attachment, a2.getHandler());
                            },
                            (t, a2) -> {
                                log.error("failed to read", t);
                            }
                    ));
                },
                (t, a) -> {
                    log.error("failed to read", t);
                }
        ));
    }

    /**
     * Starts a new thread which connects to specified server socket channel's local address and reads {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and prints it as a {@link StandardCharsets#US_ASCII US-ASCII}
     * string followed by a new line character.
     *
     * @param server the server socket channel to connect.
     * @see #connectAndPrintAsynchronous3(SocketAddress)
     */
    static void connectAndPrintAsynchronous3(final AsynchronousServerSocketChannel server) {
        final Thread thread = new Thread(() -> {
            try {
                final Void result = runAsync(() -> {
                    try {
                        connectAndPrintAsynchronous3(server.getLocalAddress());
                    } catch (final IOException ioe) {
                        log.error("failed to connect and print", ioe);
                    }
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                log.error("failed to complete", e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
