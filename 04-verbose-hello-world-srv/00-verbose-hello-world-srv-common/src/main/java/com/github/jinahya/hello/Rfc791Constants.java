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

/**
 * .
 *
 * @see <a href="https://www.rfc-editor.org/rfc/rfc791">RFC 791: INTERNET
 * PROTOCOL</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc791">RFC 791: INTERNET
 * PROTOCOL</a>
 */
public final class Rfc791Constants {

    public static final int HEADER_VERSION_SIZE = Byte.SIZE >> 1;

    public static final int HEADER_IHL_SIZE = Byte.SIZE - HEADER_VERSION_SIZE;

    public static final int HEADER_IHL_MIN_VALUE = 5;

    public static final int HEADER_IHL_MAX_VALUE = 15;

    public static final int HEADER_DSCP_SIZE = Byte.SIZE - 2;

    public static final int HEADER_ECN_SIZE = Byte.SIZE - HEADER_DSCP_SIZE;

    public static final int HEADER_TYPE_OF_SERVICE_SIZE = HEADER_DSCP_SIZE
                                                          + HEADER_ECN_SIZE;

    public static final int HEADER_TOTAL_LENGTH_SIZE = Short.SIZE;

    public static final int HEADER_IDENTIFICATION_SIZE = Short.SIZE;

    public static final int HEADER_FLAGS_SIZE = Byte.SIZE >> 1;

    public static final int HEADER_FRAGMENT_OFFSET_SIZE = Short.SIZE
                                                          - HEADER_FLAGS_SIZE;

    public static final int HEADER_TIME_TO_LIVE_SIZE = Byte.SIZE;

    public static final int HEADER_PROTOCOL_SIZE = Byte.SIZE;

    public static final int HEADER_HEADER_CHECKSUM_SIZE = Short.SIZE;

    public static final int HEADER_SOURCE_ADDRESS_SIZE = Integer.SIZE;

    public static final int HEADER_DESTINATION_ADDRESS_SIZE = Integer.SIZE;

    public static final int HEADER_OPTIONS_SIZE = Integer.SIZE - Byte.SIZE;

    public static final int HEADER_PADDING_SIZE = Integer.SIZE
                                                  - HEADER_OPTIONS_SIZE;

    public static final int MIN_HEADER_SIZE =
            HEADER_VERSION_SIZE
            + HEADER_IHL_SIZE
            + HEADER_TYPE_OF_SERVICE_SIZE
            + HEADER_TOTAL_LENGTH_SIZE
            + HEADER_IDENTIFICATION_SIZE
            + HEADER_FLAGS_SIZE
            + HEADER_FRAGMENT_OFFSET_SIZE
            + HEADER_TIME_TO_LIVE_SIZE
            + HEADER_PROTOCOL_SIZE
            + HEADER_HEADER_CHECKSUM_SIZE
            + HEADER_SOURCE_ADDRESS_SIZE
            + HEADER_DESTINATION_ADDRESS_SIZE
            + ((HEADER_IHL_MIN_VALUE - 5) << 3);

    public static final int MAX_HEADER_SIZE =
            MIN_HEADER_SIZE + ((HEADER_IHL_MAX_VALUE - HEADER_IHL_MIN_VALUE)
                               << 5);

    public static final int MIN_HEADER_BYTES = MIN_HEADER_SIZE >> 3;

    public static final int MAX_HEADER_BYTES = MAX_HEADER_SIZE >> 3;

    private Rfc791Constants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
