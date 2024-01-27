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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
abstract class Rfc862Tcp extends Rfc862 {

    // --------------------------------------------------------------------------------- clientBytes
    private static final int BOUND_BYTES = 1024;

    /**
     * Returns a new {@code int} between {@code 0}(inclusive) and {@value #BOUND_BYTES}(exclusive).
     *
     * @return a new {@code int} between {@code 0}(inclusive) and {@value #BOUND_BYTES}(exclusive).
     */
    static int newRandomBytes() {
        return ThreadLocalRandom.current().nextInt(BOUND_BYTES);
    }

    // -------------------------------------------------------------------------------- array/buffer
    private static final int MAX_ARRAY_LENGTH = 128;

    private static byte[] array() {
        return new byte[
                ThreadLocalRandom.current().nextInt(MAX_ARRAY_LENGTH) + 1
                ];
    }

    /**
     * Returns a new array of random number of bytes.
     *
     * @return a new array of random number of bytes.
     */
    static byte[] newArray() {
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
    static ByteBuffer newBuffer() {
        final var buffer = ByteBuffer.wrap(array());
        log.debug("buffer.capacity: {}", buffer.capacity());
        return buffer;
    }

    // ------------------------------------------------------------------------------------ logBound
    static <T extends ServerSocket> T logBound(final T server) {
        Objects.requireNonNull(server, "server is null");
        logBound(server.getLocalSocketAddress());
        return server;
    }

    @SuppressWarnings({"unchecked"})
    static <T extends ServerSocketChannel> T logBound(final T server) {
        Objects.requireNonNull(server, "server is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logBound(server.socket()).getChannel();
        }
        try {
            logBound(server.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return server;
    }

    static <T extends AsynchronousServerSocketChannel> T logBound(final T server) {
        Objects.requireNonNull(server, "server is null");
        try {
            logBound(server.getLocalAddress());
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
        logBound(client.getLocalSocketAddress());
        return client;
    }

    @SuppressWarnings({"unchecked"})
    static <T extends SocketChannel> T logBound(final T channel) {
        Objects.requireNonNull(channel, "channel is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logBound(channel.socket()).getChannel();
        }
        try {
            logBound(channel.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get localAddress from " + channel, ioe);
        }
        return channel;
    }

    static <T extends AsynchronousSocketChannel> T logBound(final T channel) {
        Objects.requireNonNull(channel, "channel is null");
        try {
            logBound(channel.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get localAddress from " + channel, ioe);
        }
        return channel;
    }

    // --------------------------------------------------------------------------------- logAccepted
    private static final String LOG_FORMAT_ACCEPTED = "accepted from {}, through {}";

    private static void logAccepted(final SocketAddress from, final SocketAddress through) {
        Objects.requireNonNull(from, "from is null");
        Objects.requireNonNull(through, "through is null");
        log.info(LOG_FORMAT_ACCEPTED, from, through);
    }

    static <T extends Socket> T logAccepted(final T client) {
        Objects.requireNonNull(client, "client is null");
        logAccepted(client.getRemoteSocketAddress(), client.getLocalSocketAddress());
        return client;
    }

    @SuppressWarnings({"unchecked"})
    static <T extends SocketChannel> T logAccepted(final T client) {
        Objects.requireNonNull(client, "client is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logAccepted(client.socket()).getChannel();
        }
        try {
            logAccepted(client.getRemoteAddress(), client.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get addresses from " + client, ioe);
        }
        return client;
    }

    static <T extends AsynchronousSocketChannel> T logAccepted(final T client) {
        Objects.requireNonNull(client, "client is null");
        try {
            logAccepted(client.getRemoteAddress(), client.getLocalAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get addresses from " + client, ioe);
        }
        return client;
    }

    // -------------------------------------------------------------------------------- logConnected
    static void logConnected(final Socket socket) {
        if (!Objects.requireNonNull(socket, "socket is null").isConnected()) {
            throw new IllegalArgumentException("not connected: " + socket);
        }
        logConnected(socket.getRemoteSocketAddress());
    }

    static void logConnected(final SocketChannel channel) {
        if (!Objects.requireNonNull(channel, "channel is null").isConnected()) {
            throw new IllegalArgumentException("not connected: " + channel);
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            logConnected(channel.socket());
            return;
        }
        try {
            logConnected(channel.getRemoteAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get remoteAddress from " + channel, ioe);
        }
    }

    static void logConnected(final AsynchronousSocketChannel channel) {
        Objects.requireNonNull(channel, "channel is null");
        try {
            logConnected(channel.getRemoteAddress());
        } catch (final IOException ioe) {
            throw new RuntimeException("failed to get remoteAddress from " + channel, ioe);
        }
    }
}
