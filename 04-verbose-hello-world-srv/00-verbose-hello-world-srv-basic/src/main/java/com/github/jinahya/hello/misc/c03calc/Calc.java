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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    /**
     * The number of requests.
     */
    static final int REQUEST_COUNT = 64;

    // ------------------------------------------------------------------------------------- timeout
    static final long SO_TIMEOUT = 4L;

    static final TimeUnit SO_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long SO_TIMEOUT_MILLIS = SO_TIMEOUT_UNIT.toMillis(SO_TIMEOUT);
}
