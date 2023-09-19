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

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

public abstract class _Rfc86_Constants {

    // -------------------------------------------------------------------------- HOST / PORT / ADDR

    public static final InetAddress HOST = InetAddress.getLoopbackAddress();

    // ------------------------------------------------------------------------------------- TIMEOUT
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    // ------------------------------------------------------------------------------ ACCEPT_TIMEOUT

    public static final long ACCEPT_TIMEOUT_DURATION = 32L;

    public static final TimeUnit ACCEPT_TIMEOUT_UNIT = TIMEOUT_UNIT;

    public static final long ACCEPT_TIMEOUT_IN_MILLIS =
            ACCEPT_TIMEOUT_UNIT.toMillis(ACCEPT_TIMEOUT_DURATION);

    // ----------------------------------------------------------------------------- CONNECT_TIMEOUT
    public static final long CONNECT_TIMEOUT_DURATION = 1L;

    public static final TimeUnit CONNECT_TIMEOUT_UNIT = TIMEOUT_UNIT;

    public static final long CONNECT_TIMEOUT_IN_MILLIS =
            CONNECT_TIMEOUT_UNIT.toMillis(CONNECT_TIMEOUT_DURATION);

    // -------------------------------------------------------------------------------- READ_TIMEOUT
    public static final long READ_TIMEOUT_DURATION = 2L;

    public static final TimeUnit READ_TIMEOUT_UNIT = TIMEOUT_UNIT;

    public static final long READ_TIMEOUT_IN_MILLIS =
            READ_TIMEOUT_UNIT.toMillis(READ_TIMEOUT_DURATION);

    // ------------------------------------------------------------------------------- WRITE_TIMEOUT
    public static final long WRITE_TIMEOUT_DURATION = 8L;

    public static final TimeUnit WRITE_TIMEOUT_UNIT = TIMEOUT_UNIT;

    public static final long WRITE_TIMEOUT_IN_MILLIS =
            WRITE_TIMEOUT_UNIT.toMillis(WRITE_TIMEOUT_DURATION);

    // ------------------------------------------------------------------------------ SERVER_TIMEOUT
    public static final long SERVER_TIMEOUT_DURATION = ACCEPT_TIMEOUT_DURATION;

    public static final TimeUnit SERVER_TIMEOUT_UNIT = ACCEPT_TIMEOUT_UNIT;

    public static final long SERVER_TIMEOUT_IN_MILLIS =
            SERVER_TIMEOUT_UNIT.toMillis(SERVER_TIMEOUT_DURATION);

    // ------------------------------------------------------------------------------ CLIENT_TIMEOUT
    public static final long CLIENT_TIMEOUT_DURATION = SERVER_TIMEOUT_DURATION;

    public static final TimeUnit CLIENT_TIMEOUT_UNIT = SERVER_TIMEOUT_UNIT;

    public static final long CLIENT_TIMEOUT_IN_MILLIS =
            CLIENT_TIMEOUT_UNIT.toMillis(CLIENT_TIMEOUT_DURATION);

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance.
     */
    protected _Rfc86_Constants() {
        super();
    }
}
