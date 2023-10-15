package com.github.jinahya.hello.misc;

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

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utilities for for {@link com.github.jinahya.hello.misc.c01rfc863} package and
 * {@link com.github.jinahya.hello.misc.c02rfc862} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class _TcpUtils {

    // -------------------------------------------------------------------------------------- SERVER
    private static final String LOG_FORMAT_BOUND_SERVER = "bound to {}";

    private static final String LOG_FORMAT_ACCEPTED = "accepted from {}, through {}";

    public static <T extends ServerSocket> T logBound(final T server) {
        Objects.requireNonNull(server, "server is null");
        log.info(LOG_FORMAT_BOUND_SERVER, server.getLocalSocketAddress());
        return server;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends ServerSocketChannel> T logBound(final T channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logBound(channel.socket()).getChannel();
        }
        log.info(LOG_FORMAT_BOUND_SERVER, channel.getLocalAddress());
        return channel;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends AsynchronousServerSocketChannel> T logBound(final T channel)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        log.info(LOG_FORMAT_BOUND_SERVER, channel.getLocalAddress());
        return channel;
    }

    public static <T extends Socket> T logAccepted(final T client) {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_ACCEPTED, client.getRemoteSocketAddress(),
                 client.getLocalSocketAddress());
        return client;
    }

    public static <T extends SocketChannel> T logAccepted(final T client) {
        Objects.requireNonNull(client, "client is null");
        try {
            log.info(LOG_FORMAT_ACCEPTED, client.getRemoteAddress(), getLocalAddress(client));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        return client;
    }

    public static <T extends AsynchronousSocketChannel> T logAccepted(final T client) {
        Objects.requireNonNull(client, "client is null");
        try {
            log.info(LOG_FORMAT_ACCEPTED, client.getRemoteAddress(), getLocalAddress(client));
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        return client;
    }

    // -------------------------------------------------------------------------------------- CLIENT
    private static final String LOG_FORMAT_BOUND_CLIENT = "(optionally) bound to {}";

    public static <T extends Socket> T logBound(final T client) {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_BOUND_CLIENT, client.getLocalSocketAddress());
        return client;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends SocketChannel> T logBound(final T channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logBound(channel.socket()).getChannel();
        }
        log.info(LOG_FORMAT_BOUND_CLIENT, channel.getLocalAddress());
        return channel;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends AsynchronousSocketChannel> T logBound(final T channel)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        log.info(LOG_FORMAT_BOUND_CLIENT, channel.getLocalAddress());
        return channel;
    }

    private static final String LOG_FORMAT_CONNECTED = "connected to {}, through {}";

    private static SocketAddress getLocalAddress(final NetworkChannel channel)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        return channel.getLocalAddress();
    }

    public static <T extends Socket> T logConnected(final T client) {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_CONNECTED, client.getRemoteSocketAddress(),
                 client.getLocalSocketAddress());
        return client;
    }

    public static <T extends SocketChannel> T logConnected(final T client) {
        Objects.requireNonNull(client, "client is null");
        try {
            log.info(LOG_FORMAT_CONNECTED, client.getRemoteAddress(), getLocalAddress(client));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        return client;
    }

    public static <T extends AsynchronousSocketChannel> T logConnected(final T client) {
        Objects.requireNonNull(client, "client is null");
        try {
            log.info(LOG_FORMAT_CONNECTED, client.getRemoteAddress(), getLocalAddress(client));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
        return client;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    private _TcpUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
