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

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

final class _Rfc862Constants {

    private static final int _RFC862_PORT = 7;

    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    private static final int PORT = _RFC862_PORT + 50000;

    static final InetSocketAddress ADDR = new InetSocketAddress(HOST, PORT);

    // ------------------------------------------------------------------------------------- TIMEOUT

    static final TimeUnit ACCEPT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long ACCEPT_TIMEOUT_DURATION = 32L;

    static final long ACCEPT_TIMEOUT_IN_MILLIS =
            ACCEPT_TIMEOUT_UNIT.toMillis(ACCEPT_TIMEOUT_DURATION);

    static final TimeUnit CONNECT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long CONNECT_TIMEOUT_DURATION = 2L;

    static final long CONNECT_TIMEOUT_IN_MILLIS =
            CONNECT_TIMEOUT_UNIT.toMillis(CONNECT_TIMEOUT_DURATION);

    static final TimeUnit READ_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long READ_TIMEOUT_DURATION = 1L;

    static final long READ_TIMEOUT_IN_MILLIS =
            READ_TIMEOUT_UNIT.toMillis(READ_TIMEOUT_DURATION);

    static final TimeUnit WRITE_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long WRITE_TIMEOUT_DURATION = 8L;

    static final long WRITE_TIMEOUT_IN_MILLIS =
            WRITE_TIMEOUT_UNIT.toMillis(WRITE_TIMEOUT_DURATION);

    // -------------------------------------------------------------------------------------- DIGEST
    static final String ALGORITHM = "SHA-256";

    static final Duration SO_TIMEOUT = Duration.ofSeconds(16L);

    private _Rfc862Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
