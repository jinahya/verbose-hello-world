package com.github.jinahya.hello.miscellaneous;

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

import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

/**
 * A class providing test utilities for {@link java.nio.charset} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({"java:S101"})
public final class _Java_Nio_Charset_TestUtils {

    /**
     * Returns a stream of {@link Charset}s consisting of every {@link StandardCharsets} constant
     * plus the {@code X-UTF-32BE-BOM} and {@code X-UTF-32LE-BOM} variants.
     *
     * @return a stream of charsets.
     */
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

    /**
     * Returns a stream of {@link CharsetEncoder}s, one per encodable charset from
     * {@link #charsetStream()}.
     *
     * @return a stream of charset encoders.
     */
    public static Stream<CharsetEncoder> charsetEncoderStream() {
        return charsetStream()
                .filter(Charset::canEncode)
                .map(Charset::newEncoder);
    }

    /**
     * Returns a stream of {@link CharsetDecoder}s, one per charset from {@link #charsetStream()}.
     *
     * @return a stream of charset decoders.
     */
    public static Stream<CharsetDecoder> charsetDecoderStream() {
        return charsetStream()
                .map(Charset::newDecoder);
    }

    private _Java_Nio_Charset_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
