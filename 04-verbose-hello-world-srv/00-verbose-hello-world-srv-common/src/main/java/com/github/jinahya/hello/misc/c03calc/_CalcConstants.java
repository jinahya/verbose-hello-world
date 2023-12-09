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

import com.github.jinahya.hello.misc.c00rfc86_._Rfc86_Constants;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@SuppressWarnings({
        "java:S101" // class _Calc...
})
final class _CalcConstants
        extends _Rfc86_Constants {

    // ------------------------------------------------------------------------------------- NETWORK
    private static final InetAddress HOST = InetAddress.getLoopbackAddress();

    private static final int PORT = 30007;

    static final InetSocketAddress ADDR = new InetSocketAddress(HOST, PORT);

    // -------------------------------------------------------------------------------------- SERVER
    static final int SERVER_THREADS = 64;

    static final int SERVER_BACKLOG = SERVER_THREADS << 1;

    static final long SERVER_PROGRAM_TIMEOUT = 60L;

    static final TimeUnit SERVER_PROGRAM_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long SERVER_PROGRAM_TIMEOUT_MILLIS =
            SERVER_PROGRAM_TIMEOUT_UNIT.toMillis(SERVER_PROGRAM_TIMEOUT);

    // -------------------------------------------------------------------------------------- CLIENT
    static final int TOTAL_REQUESTS = 8;

    static final int CLIENT_THREADS = 32;

    static final long CLIENT_PROGRAM_TIMEOUT = 8L;

    static final TimeUnit CLIENT_PROGRAM_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long CLIENT_PROGRAM_TIMEOUT_MILLIS = CLIENT_PROGRAM_TIMEOUT_UNIT.toMillis(
            CLIENT_PROGRAM_TIMEOUT);

    // ---------------------------------------------------------------------------------------------
    static final long CONNECT_TIMEOUT = 1L;

    static final TimeUnit CONNECT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final int CONNECT_TIMEOUT_MILLIS = (int) CONNECT_TIMEOUT_UNIT.toMillis(CONNECT_TIMEOUT);

    static final long READ_TIMEOUT = 1L;

    static final TimeUnit READ_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long READ_TIMEOUT_MILLIS = READ_TIMEOUT_UNIT.toMillis(READ_TIMEOUT);

    static final long WRITE_TIMEOUT = 1L;

    static final TimeUnit WRITE_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long WRITE_TIMEOUT_MILLIS = WRITE_TIMEOUT_UNIT.toMillis(WRITE_TIMEOUT);

    static final long SELECT_TIMEOUT = 8L;

    static final TimeUnit SELECT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long SELECT_TIMEOUT_MILLIS = SELECT_TIMEOUT_UNIT.toMillis(SELECT_TIMEOUT);

    // ---------------------------------------------------------------------------------------------
    private _CalcConstants() {
        super();
        throw new AssertionError("instantiation is not allowed");
    }
}
