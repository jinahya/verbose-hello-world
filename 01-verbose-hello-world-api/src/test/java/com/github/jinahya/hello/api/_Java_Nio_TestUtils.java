package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
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

import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.provider.*;

import java.nio.*;
import java.util.stream.*;

@Slf4j
@SuppressWarnings({"java:S101"})
public final class _Java_Nio_TestUtils {

    static Stream<ByteOrder> byteOrderStream() {
        return Stream.of(
                ByteOrder.BIG_ENDIAN,
                ByteOrder.LITTLE_ENDIAN
        );
    }

    static Stream<Arguments> namedByteOrderStream() {
        return Stream.of(
                Arguments.of(Named.of(ByteOrder.BIG_ENDIAN.toString(), ByteOrder.BIG_ENDIAN)),
                Arguments.of(Named.of(ByteOrder.LITTLE_ENDIAN.toString(), ByteOrder.LITTLE_ENDIAN)),
                Arguments.of(Named.of("nativeOrder", ByteOrder.nativeOrder()))
        );
    }

    private _Java_Nio_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
