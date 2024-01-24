package com.github.jinahya.hello.misc.c02rfc862;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
abstract class __Rfc862 {

    // -------------------------------------------------------------------------- host / port / addr

    public static final InetAddress HOST_IPv4;

    static {
        try {
            HOST_IPv4 = InetAddress.getByName("127.0.0.1");
        } catch (final UnknownHostException uhe) {
            throw new RuntimeException(uhe);
        }
    }

    public static final InetAddress HOST_IPv6;

    static {
        try {
            HOST_IPv6 = InetAddress.getByName("::1");
        } catch (final UnknownHostException uhe) {
            throw new RuntimeException(uhe);
        }
    }

    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    private static final int RFC862_PORT = 7;

    static final int PORT = RFC862_PORT + 50000;

    static final InetSocketAddress ADDR = new InetSocketAddress(HOST, PORT);

    // -------------------------------------------------------------------------------------- digest

    /**
     * Returns a new message digest object that implements the specified digest algorithm..
     *
     * @return the name of the algorithm.
     */
    static MessageDigest newDigest(final String algorithm) {
        Objects.requireNonNull(algorithm, "algorithm is null");
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException nsae) {
            throw new IllegalArgumentException("algorithm is unknown: " + algorithm, nsae);
        }
    }

    public static void logDigest(final MessageDigest digest,
                                 final Function<? super byte[], ? extends CharSequence> printer) {
        Objects.requireNonNull(digest, "digest is null");
        Objects.requireNonNull(printer, "printer is null");
        log.info("digest: {}", printer.apply(digest.digest()));
    }

    public static void logDigest(final String algorithm, final byte[] array, final int offset,
                                 final int length,
                                 final Function<? super byte[], ? extends CharSequence> printer) {
        final var digest = newDigest(algorithm);
        digest.update(
                array,  // <input>
                offset, // <offset>
                length  // <len>
        );
        logDigest(digest, printer);
    }

    public static void logDigest(final String algorithm, final ByteBuffer buffer,
                                 final Function<? super byte[], ? extends CharSequence> printer) {
        final var digest = newDigest(algorithm);
        digest.update(
                buffer // input
        );
        logDigest(digest, printer);
    }

    /**
     * Returns a new message digest implements {@code SHA-1} algorithm.
     *
     * @return a new message digest.
     */
    static MessageDigest newDigest() {
        try {
            return MessageDigest.getInstance("SHA-1");
        } catch (final NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
    }

    static void logDigest(final MessageDigest digest) {
        log.info("digest: {}", HexFormat.of().formatHex(digest.digest()));
    }

    static void logDigest(final byte[] array, final int offset, final int length) {
        final var digest = newDigest();
        digest.update(array, offset, length);
        logDigest(digest);
    }

    static void logDigest(final ByteBuffer buffer) {
        final var digest = newDigest();
        digest.update(buffer);
        logDigest(digest);
    }

    // --------------------------------------------------------------------------------------- bytes
    private static final int BOUND_RANDOM_BYTES = 8192;

    /**
     * Returns a new {@code int} between {@code 0}(inclusive) and
     * {@value #BOUND_RANDOM_BYTES}(exclusive).
     *
     * @return a new {@code int} between {@code 0}(inclusive) and
     * {@value #BOUND_RANDOM_BYTES}(exclusive).
     */
    static int newRandomBytes() {
        return ThreadLocalRandom.current().nextInt(BOUND_RANDOM_BYTES);
    }

    /**
     * Logs specified client bytes.
     *
     * @param bytes the client bytes to log.
     */
    static int logClientBytes(final int bytes) {
        if (bytes < 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("sending {} bytes", bytes);
        return bytes;
    }

    /**
     * Logs specified server bytes.
     *
     * @param bytes the server bytes to log.
     */
    static long logServerBytes(final long bytes) {
        if (bytes < 0L) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is negative");
        }
        log.info("{} bytes received (and discarded)", bytes);
        return bytes;
    }

    // -------------------------------------------------------------------------------- array/buffer
    private static final int MIN_ARRAY_LENGTH = 1;

    private static final int MAX_ARRAY_LENGTH = 1024;

    private static byte[] array() {
        return new byte[
                ThreadLocalRandom.current().nextInt(MAX_ARRAY_LENGTH) + MIN_ARRAY_LENGTH
                ];
    }

    /**
     * Returns a new array of random number of bytes.
     *
     * @return a new array of random number of bytes.
     */
    public static byte[] newArray() {
        final var array = array();
        log.debug("array.length: {}", array.length);
        return array;
    }

    /**
     * Returns a new byte buffer {@link ByteBuffer#wrap(byte[]) wraps} the result of
     * {@link #newArray()}.
     *
     * @return a new byte buffer {@link ByteBuffer#wrap(byte[]) wraps} the result of
     * {@link #newArray()}.
     * @see #newArray()
     */
    public static ByteBuffer newBuffer() {
        final var buffer = ByteBuffer.wrap(array());
        log.debug("buffer.capacity: {}", buffer.capacity());
        return buffer;
    }

    // ----------------------------------------------------------------------------------------- log
    private static final String LOG_FORMAT_BOUND = "bound to {}";

    private static final String LOG_FORMAT_ACCEPTED = "accepted from {}, through {}";

    private static final String LOG_FORMAT_CONNECTED = "connected to {}, through {}";

    static <T extends ServerSocket> T logBound(final T server) {
        Objects.requireNonNull(server, "server is null");
        log.info(LOG_FORMAT_BOUND, server.getLocalSocketAddress());
        return server;
    }

    @SuppressWarnings({"unchecked"})
    static <T extends ServerSocketChannel> T logBound(final T server) {
        Objects.requireNonNull(server, "server is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logBound(server.socket()).getChannel();
        }
        try {
            log.info(LOG_FORMAT_BOUND, server.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return server;
    }

    static <T extends AsynchronousServerSocketChannel> T logBound(final T server) {
        Objects.requireNonNull(server, "server is null");
        try {
            log.info(LOG_FORMAT_BOUND, server.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return server;
    }

    static <T extends Socket> T logBound(final T client) {
        Objects.requireNonNull(client, "client is null");
        if (!client.isBound()) {
            throw new IllegalArgumentException("not bound: " + client);
        }
        log.info(LOG_FORMAT_BOUND, client.getLocalSocketAddress());
        return client;
    }

    @SuppressWarnings({"unchecked"})
    static <T extends SocketChannel> T logBound(final T channel) {
        Objects.requireNonNull(channel, "channel is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logBound(channel.socket()).getChannel();
        }
        try {
            log.info(LOG_FORMAT_BOUND, channel.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get localAddress from " + channel, ioe);
        }
        return channel;
    }

    static <T extends AsynchronousSocketChannel> T logBound(final T channel) {
        Objects.requireNonNull(channel, "channel is null");
        try {
            log.info(LOG_FORMAT_BOUND, channel.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get localAddress from " + channel, ioe);
        }
        return channel;
    }

    static <T extends Socket> T logAccepted(final T client) {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_ACCEPTED, client.getRemoteSocketAddress(),
                 client.getLocalSocketAddress());
        return client;
    }

    @SuppressWarnings({"unchecked"})
    static <T extends SocketChannel> T logAccepted(final T client) {
        Objects.requireNonNull(client, "client is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logAccepted(client.socket()).getChannel();
        }
        try {
            log.info(LOG_FORMAT_ACCEPTED, client.getRemoteAddress(), client.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get addresses from " + client, ioe);
        }
        return client;
    }

    static <T extends AsynchronousSocketChannel> T logAccepted(final T client) {
        Objects.requireNonNull(client, "client is null");
        try {
            log.info(
                    LOG_FORMAT_ACCEPTED,
                    Optional.ofNullable(client.getRemoteAddress())
                            .orElseThrow(
                                    () -> new IllegalArgumentException("not connected: " + client)),
                    client.getLocalAddress()
            );
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get addresses from " + client, ioe);
        }
        return client;
    }

    static <T extends Socket> T logConnected(final T client) {
        if (!Objects.requireNonNull(client, "client is null").isConnected()) {
            throw new IllegalArgumentException("not connected: " + client);
        }
        log.info(LOG_FORMAT_CONNECTED, client.getRemoteSocketAddress(),
                 client.getLocalSocketAddress());
        return client;
    }

    @SuppressWarnings({"unchecked"})
    static <T extends SocketChannel> T logConnected(final T client) {
        if (!Objects.requireNonNull(client, "client is null").isConnected()) {
            throw new IllegalArgumentException("not connected: " + client);
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logConnected(client.socket()).getChannel();
        }
        try {
            log.info(LOG_FORMAT_CONNECTED, client.getRemoteAddress(), client.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get addresses from " + client, ioe);
        }
        return client;
    }

    static <T extends AsynchronousSocketChannel> T logConnected(final T client) {
        Objects.requireNonNull(client, "client is null");
        try {
            log.info(
                    LOG_FORMAT_CONNECTED,
                    Optional.ofNullable(client.getRemoteAddress()).orElseThrow(
                            () -> new IllegalArgumentException("not connected: " + client)),
                    client.getLocalAddress()
            );
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get addresses from " + client, ioe);
        }
        return client;
    }
}
