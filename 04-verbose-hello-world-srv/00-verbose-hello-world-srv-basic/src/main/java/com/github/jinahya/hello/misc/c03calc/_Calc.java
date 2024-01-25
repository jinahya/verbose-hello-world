package com.github.jinahya.hello.misc.c03calc;

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
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101" // class _Calc...
})
abstract class _Calc {

    // ------------------------------------------------------------------------------ host/port/addr
    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    private static final int PORT = 30007;

    static final InetSocketAddress ADDR = new InetSocketAddress(HOST, PORT);

    // ------------------------------------------------------------------------------- server/client
    static final int CLIENT_THREADS = 8;
    static final int CLIENT_COUNT = 8;

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
