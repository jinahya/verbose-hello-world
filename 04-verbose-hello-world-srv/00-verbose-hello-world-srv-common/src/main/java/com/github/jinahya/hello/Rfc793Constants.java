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

import com.github.jinahya.hello.util.ExcludeFromCoverage_PrivateConstructor_Obviously;

/**
 * .
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc793">RFC 793: TRANSMISSION
 * CONTROL PROTOCOL</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc9293">RFC 9293: Transmission
 * Control Protocol (TCP)</a>
 */
public final class Rfc793Constants {

    public static final int HEADER_SOURCE_PORT_BYTES = Short.BYTES;

    public static final int HEADER_DESTINATION_PORT_BYTES = Short.BYTES;

    public static final int HEADER_SEQUENCE_NUMBER_PORT_BYTES = Integer.BYTES;

    public static final int HEADER_ACKNOWLEDGMENT_NUMBER_PORT_BYTES = Integer.BYTES;

    public static final int HEADER_DATA_OFFSET_SIZE = Byte.SIZE >> 1;

    public static final int HEADER_RSRVD_SIZE = Byte.SIZE >> 1;

    public static final int HEADER_CWR_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_ECE_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_URG_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_ACK_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_PSH_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_RST_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_SYN_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_FIN_SIZE = Byte.SIZE >> 3;

    public static final int HEADER_WINDOW_BYTES = Short.BYTES;

    public static final int HEADER_CHECKSUM_BYTES = Short.BYTES;

    public static final int HEADER_URGENT_POINTER_BYTES = Short.BYTES;

    @ExcludeFromCoverage_PrivateConstructor_Obviously
    private Rfc793Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
