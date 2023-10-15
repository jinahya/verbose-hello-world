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

import com.github.jinahya.hello.misc._Rfc86_Constants;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

final class _CalcConstants extends _Rfc86_Constants {

    // ------------------------------------------------------------------------------------- NETWORK
    static final InetAddress HOST = InetAddress.getLoopbackAddress();

    private static final int PORT = 30007;

    static final InetSocketAddress ADDR = new InetSocketAddress(HOST, PORT);

    // -------------------------------------------------------------------------------------- CLIENT
    static final int NUMBER_OF_REQUESTS = 128;

    private static final int CLIENT_TIMEOUT_DURATION = 60;

    static final TimeUnit CLIENT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final int CLIENT_TIMEOUT_MILLIS =
            (int) CLIENT_TIMEOUT_UNIT.toMillis(CLIENT_TIMEOUT_DURATION);

    // ------------------------------------------------------------------------------------- TIMEOUT
    private static final long CONNECT_TIMEOUT_DURATION = 2;

    static final TimeUnit CONNECT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final int CONNECT_TIMEOUT_MILLIS =
            (int) CONNECT_TIMEOUT_UNIT.toMillis(CONNECT_TIMEOUT_DURATION);

    private static final long READ_TIMEOUT_DURATION = 1;

    static final TimeUnit READ_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final int READ_TIMEOUT_MILLIS = (int) READ_TIMEOUT_UNIT.toMillis(READ_TIMEOUT_DURATION);

    private static final long SELECT_TIMEOUT_DURATION = 8;

    static final TimeUnit SELECT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final int SELECT_TIMEOUT_MILLIS =
            (int) SELECT_TIMEOUT_UNIT.toMillis(SELECT_TIMEOUT_DURATION);

    // ---------------------------------------------------------------------------------------------
    private _CalcConstants() {
        super();
        throw new AssertionError("instantiation is not allowed");
    }
}
