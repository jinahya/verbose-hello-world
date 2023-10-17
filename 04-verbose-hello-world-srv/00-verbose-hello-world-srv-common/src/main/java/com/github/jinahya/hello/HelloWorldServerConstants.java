package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-srv
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import java.util.concurrent.TimeUnit;

/**
 * Constants for Hello World servers.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class HelloWorldServerConstants {

    public static final String QUIT = "quit!";

    public static final String QUIT_AND_ENTER = QUIT + System.lineSeparator();

    public static final String KEEP = "keep!";

    public static final String SOLI = "soli!";

    // ---------------------------------------------------------------------------------------------

    static final int PORT = Short.MAX_VALUE;

    // ------------------------------------------------------------------------------------- TIMEOUT

    static final TimeUnit ACCEPT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long ACCEPT_TIMEOUT_DURATION = 32L;

    static final long ACCEPT_TIMEOUT_IN_MILLIS =
            ACCEPT_TIMEOUT_UNIT.toMillis(ACCEPT_TIMEOUT_DURATION);

    static final TimeUnit CONNECT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    static final long CONNECT_TIMEOUT_DURATION = 1L;

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

    private HelloWorldServerConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
