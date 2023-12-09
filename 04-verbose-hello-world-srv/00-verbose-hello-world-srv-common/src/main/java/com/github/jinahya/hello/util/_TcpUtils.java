package com.github.jinahya.hello.util;

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
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
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
@SuppressWarnings({
        "java:S101" // class _Tcp...
})
public final class _TcpUtils {

    private static final String LOG_FORMAT_BOUND = "bound to {}";

    private static final String LOG_FORMAT_ACCEPTED = "accepted from {}, through {}";

    private static final String LOG_FORMAT_CONNECTED = "connected to {}, through {}";

    // --------------------------------------------------------------------------------------- bound
    public static <T extends ServerSocket> T logBound(final T server) {
        Objects.requireNonNull(server, "server is null");
        log.info(LOG_FORMAT_BOUND, server.getLocalSocketAddress());
        return server;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends ServerSocketChannel> T logBound(final T server)
            throws IOException {
        Objects.requireNonNull(server, "server is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logBound(server.socket()).getChannel();
        }
        log.info(LOG_FORMAT_BOUND, server.getLocalAddress());
        return server;
    }

    public static <T extends AsynchronousServerSocketChannel> T logBound(final T server)
            throws IOException {
        Objects.requireNonNull(server, "server is null");
        log.info(LOG_FORMAT_BOUND, server.getLocalAddress());
        return server;
    }

    public static <T extends Socket> T logBound(final T client) {
        Objects.requireNonNull(client, "client is null");
        if (!client.isBound()) {
            throw new IllegalArgumentException("not bound: " + client);
        }
        log.info(LOG_FORMAT_BOUND, client.getLocalSocketAddress());
        return client;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends SocketChannel> T logBound(final T channel)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logBound(channel.socket()).getChannel();
        }
        log.info(LOG_FORMAT_BOUND, channel.getLocalAddress());
        return channel;
    }

    public static <T extends AsynchronousSocketChannel> T logBound(final T channel)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        log.info(LOG_FORMAT_BOUND, channel.getLocalAddress());
        return channel;
    }

    // ------------------------------------------------------------------------------------ accepted
    public static <T extends Socket> T logAccepted(final T client) {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_ACCEPTED, client.getRemoteSocketAddress(),
                 client.getLocalSocketAddress());
        return client;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends SocketChannel> T logAccepted(final T client)
            throws IOException {
        Objects.requireNonNull(client, "client is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logAccepted(client.socket()).getChannel();
        }
        log.info(LOG_FORMAT_ACCEPTED, client.getRemoteAddress(), client.getLocalAddress());
        return client;
    }

    public static <T extends AsynchronousSocketChannel> T logAccepted(final T client)
            throws IOException {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_ACCEPTED, client.getRemoteAddress(), client.getLocalAddress());
        return client;
    }

    public static <T extends AsynchronousSocketChannel> T logAcceptedUnchecked(final T client) {
        try {
            return logAccepted(client);
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to log accepted for " + client, ioe);
        }
    }

    // ----------------------------------------------------------------------------------- connected
    public static <T extends Socket> T logConnected(final T client) {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_CONNECTED, client.getRemoteSocketAddress(),
                 client.getLocalSocketAddress());
        return client;
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends SocketChannel> T logConnected(final T client)
            throws IOException {
        Objects.requireNonNull(client, "client is null");
        if (ThreadLocalRandom.current().nextBoolean()) {
            return (T) logConnected(client.socket()).getChannel();
        }
        log.info(LOG_FORMAT_CONNECTED, client.getRemoteAddress(), client.getLocalAddress());
        return client;
    }

    public static <T extends AsynchronousSocketChannel> T logConnected(final T client)
            throws IOException {
        Objects.requireNonNull(client, "client is null");
        log.info(LOG_FORMAT_CONNECTED, client.getRemoteAddress(), client.getLocalAddress());
        return client;
    }

    public static <T extends AsynchronousSocketChannel> T logConnectedUnchecked(final T client) {
        try {
            return logConnected(client);
        } catch (final IOException ioe) {
            throw new UncheckedIOException("failed to log connected for " + client, ioe);
        }
    }

    // ---------------------------------------------------------------------------------------------

    private _TcpUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
