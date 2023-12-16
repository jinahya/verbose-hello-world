package com.github.jinahya.hello;

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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;

/**
 * .
 *
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc768">RFC 768 User Datagram Protocol</a>
 */
public final class Rfc768Constants {

    public static final int HEADER_SOURCE_PORT_SIZE = Short.SIZE;

    public static final int HEADER_DESTINATION_PORT_SIZE = Short.SIZE;

    public static final int HEADER_LENGTH_SIZE = Short.SIZE;

    public static final int HEADER_CHECKSUM_SIZE = Short.SIZE;

    public static final int HEADER_SIZE =
            HEADER_SOURCE_PORT_SIZE
            + HEADER_DESTINATION_PORT_SIZE
            + HEADER_LENGTH_SIZE
            + HEADER_CHECKSUM_SIZE;

    public static final int HEADER_BYTES = HEADER_SIZE / Byte.SIZE;

    public static final int PROTOCOL_NUMBER = 0x17;

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc768Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
