package com.github.jinahya.hello.misc.c04chat;

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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
abstract class Chat {

    // ------------------------------------------------------------------------------ HOST/PORT/ADDR
    private static final String HOST_NAME = "::";

    static final InetAddress HOST;

    static {
        try {
            HOST = InetAddress.getByName(HOST_NAME);
        } catch (final UnknownHostException uhe) {
            throw new InstantiationError(uhe.getMessage());
        }
    }

    static final int PORT = 7 + 40000;

    static final InetSocketAddress ADDR = new InetSocketAddress(HOST, PORT);

    // --------------------------------------------------------------------------------------- QUIT!
    static final String QUIT = "quit!";

    // -------------------------------------------------------------------------------------- SERVER

    static final int SERVER_THREADS = 1048576;

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

    // -------------------------------------------------------------------------------------- CLIENT
    static final int CLIENT_THREADS = 32;

    /**
     * Returns a new thread-pool that uses {@value #CLIENT_THREADS} thread(s).
     *
     * @param namePrefix a thread name prefix.
     * @return a new thread-pool that uses {@value #CLIENT_THREADS} thread(s).
     */
    static ExecutorService newExecutorForClient(final String namePrefix) {
        return Executors.newFixedThreadPool(
                CLIENT_THREADS,
                Thread.ofVirtual().name(namePrefix, 0L).factory()
        );
    }

    /**
     * The number of requests.
     */
    static final int REQUEST_COUNT = 16;

    // ---------------------------------------------------------------------------------- SO_TIMEOUT
    static final long SO_TIMEOUT = 1L;

    static final TimeUnit SO_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long SO_TIMEOUT_MILLIS = SO_TIMEOUT_UNIT.toMillis(SO_TIMEOUT);

    // ----------------------------------------------------------------------------------- user.name
    static final String USER_NAME =
            Optional.ofNullable(System.getProperty("user.name"))
                    .orElse("unknown");
}