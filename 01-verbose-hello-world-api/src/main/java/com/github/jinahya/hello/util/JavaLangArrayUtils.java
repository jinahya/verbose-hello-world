package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
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

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public final class JavaLangArrayUtils {

    public static void requireValidStart(final byte[] array, final int offset, final String name) {
        Objects.requireNonNull(array, "array is null");
        if (offset < 0) {
            throw new IllegalArgumentException(name + "(" + offset + ") < 0");
        }
    }

    // ------------------------------------------------------------------------------- offset/length
    public static void requireValidRange1(final byte[] array, final int offset, final int length) {
        requireValidStart(array, offset, "offset");
        if (offset + length > array.length) {
            throw new IllegalArgumentException(
                    "offset(" + offset + ") + length(" + length + ")" +
                    " > array.length(" + array.length + ")");
        }
    }

    // ------------------------------------------------------------------------------------- to/from
    public static void requireValidRange2(final byte[] array, final int from, final int to) {
        requireValidStart(array, from, "from");
        if (to < from) {
            throw new IllegalArgumentException("to(" + to + ") < from(" + from + ")");
        }
        if (to > array.length) {
            throw new IllegalArgumentException(
                    "to(" + to + ") > array.length(" + array.length + ")");
        }
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaLangArrayUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
