package com.github.jinahya.hello.misc.c00rfc86_;

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

/**
 * Shared constants for {@link com.github.jinahya.hello.misc.c01rfc863} package and
 * {@link com.github.jinahya.hello.misc.c02rfc862} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class _Rfc86_Constants {

    // -------------------------------------------------------------------------- HOST / PORT / ADDR

    public static final InetAddress HOST = InetAddress.getLoopbackAddress();

    // ------------------------------------------------------------------------------------- TIMEOUT
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;

    // ------------------------------------------------------------------------------ ACCEPT_TIMEOUT

    public static final long ACCEPT_TIMEOUT = 16L;

    public static final TimeUnit ACCEPT_TIMEOUT_UNIT = TIMEOUT_UNIT;

    public static final long ACCEPT_TIMEOUT_MILLIS = ACCEPT_TIMEOUT_UNIT.toMillis(ACCEPT_TIMEOUT);

    // ----------------------------------------------------------------------------- CONNECT_TIMEOUT
    public static final long CONNECT_TIMEOUT = 32L;

    public static final TimeUnit CONNECT_TIMEOUT_UNIT = TimeUnit.MINUTES;

    public static final long CONNECT_TIMEOUT_MILLIS =
            CONNECT_TIMEOUT_UNIT.toMillis(CONNECT_TIMEOUT);

    // -------------------------------------------------------------------------------- READ_TIMEOUT
    public static final long READ_TIMEOUT = 1L;

    public static final TimeUnit READ_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public static final long READ_TIMEOUT_MILLIS = READ_TIMEOUT_UNIT.toMillis(READ_TIMEOUT);

    // ------------------------------------------------------------------------------- WRITE_TIMEOUT
    public static final long WRITE_TIMEOUT = 1L;

    public static final TimeUnit WRITE_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public static final long WRITE_TIMEOUT_MILLIS = WRITE_TIMEOUT_UNIT.toMillis(WRITE_TIMEOUT);

    // ------------------------------------------------------------------------------ SELECT_TIMEOUT
    public static final long SELECT_TIMEOUT = 1L;

    public static final TimeUnit SELECT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public static final long SELECT_TIMEOUT_MILLIS = SELECT_TIMEOUT_UNIT.toMillis(SELECT_TIMEOUT);

    // ------------------------------------------------------------------------------ SERVER_TIMEOUT
    public static final long SERVER_TIMEOUT = 60L;

    public static final TimeUnit SERVER_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public static final long SERVER_TIMEOUT_MILLIS = SERVER_TIMEOUT_UNIT.toMillis(SERVER_TIMEOUT);

    // ------------------------------------------------------------------------------ CLIENT_TIMEOUT
    public static final long CLIENT_TIMEOUT = 60L;

    public static final TimeUnit CLIENT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public static final long CLIENT_TIMEOUT_MILLIS = CLIENT_TIMEOUT_UNIT.toMillis(CLIENT_TIMEOUT);

    // ---------------------------------------------------------------------------------------------

    protected _Rfc86_Constants() {
        super();
    }
}
