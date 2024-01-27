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
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.ServerSocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
abstract class Calc {

    // ------------------------------------------------------------------------------ host/port/addr
    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    private static final int PORT = 30007;

    static final InetSocketAddress ADDR = new InetSocketAddress(HOST, PORT);

    // -------------------------------------------------------------------------------------- server

    static final int SERVER_THREADS = 128;

    /**
     * Returns a new thread-pool that uses {@value #SERVER_THREADS} thread(s).
     *
     * @param namePrefix a thread name prefix.
     * @return a new thread-pool that uses {@value #SERVER_THREADS} thread(s).
     */
    static ExecutorService newExecutorForServer(final String namePrefix) {
        return Executors.newFixedThreadPool(
                SERVER_THREADS,
                Thread.ofVirtual().name(namePrefix, 0L).factory()
        );
    }

    // -------------------------------------------------------------------------------------- client
    static final int CLIENT_COUNT = 8;

    // ------------------------------------------------------------------------------------- timeout
    static final long SO_TIMEOUT = 1L;

    static final TimeUnit SO_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long SO_TIMEOUT_MILLIS = SO_TIMEOUT_UNIT.toMillis(SO_TIMEOUT);

    // ----------------------------------------------------------------------------------------- log

    private static final String LOG_FORMAT_BOUND = "bound to {}";

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

    public static <T extends DatagramSocket> T logBound(final T socket) {
        Objects.requireNonNull(socket, "socket is null");
        log.info(LOG_FORMAT_BOUND, socket.getLocalSocketAddress());
        return socket;
    }
}
