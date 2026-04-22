package com.github.jinahya.hello.api._java_nio;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Modifier;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Stream;

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

    public static Stream<Charset> charsetStream() {
        return Stream.concat(
                Arrays.stream(StandardCharsets.class.getFields())
                        .filter(f -> {
                            final var modifiers = f.getModifiers();
                            return Modifier.isStatic(modifiers) &&
                                   Modifier.isFinal(modifiers) &&
                                   Charset.class.isAssignableFrom(f.getType());
                        })
                        .map(f -> {
                            try {
                                return (Charset) f.get(null);
                            } catch (final IllegalAccessException iae) {
                                throw new RuntimeException(iae);
                            }
                        }),
                Stream.of("X-UTF-32BE-BOM", "X-UTF-32LE-BOM")
                        .map(Charset::forName)
        );
    }

    private _Java_Nio_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
