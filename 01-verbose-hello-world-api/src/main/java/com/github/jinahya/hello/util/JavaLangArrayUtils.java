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

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public final class JavaLangArrayUtils {

    /**
     * Randomizes specified number of elements in specified array starting at specified offset.
     *
     * @param array  the array whose elements are randomized.
     * @param offset the starting offset of the {@code array}.
     * @param length the number of elements to be randomized.
     * @return given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if either {@code offset} or {@code length} is negative, or
     *                                   {@code offset} plus {@code length} is greater than
     *                                   {@code array.length}.
     */
    public static byte[] randomize(final byte[] array, final int offset, final int length) {
        Objects.requireNonNull(array, "array is null");
        if (offset < 0) {
            throw new IndexOutOfBoundsException("offset(" + offset + ") < 0");
        }
        if (length < 0) {
            throw new IndexOutOfBoundsException("length(" + length + ") < 0");
        }
        if (((long) length + offset) > array.length) {
            throw new IndexOutOfBoundsException(
                    "length(" + length + ") + offset(" + offset + ")" +
                    " > array.length(" + array.length + ")"
            );
        }
        final var src = new byte[length];
        ThreadLocalRandom.current().nextBytes(src);
        System.arraycopy(src, 0, array, offset, length);
        return array;
    }

    /**
     * Randomizes specified number of elements in specified array starting at specified offset.
     *
     * @param array  the array whose elements are randomized.
     * @param offset the starting offset of the {@code array}.
     * @return given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code offset} negative or greater than
     *                                   {@code array.length}.
     */
    public static byte[] randomize(final byte[] array, final int offset) {
        return randomize(
                Objects.requireNonNull(array, "array is null"),
                offset,
                array.length - offset
        );
    }

    /**
     * Randomizes specified number of elements in specified array starting at specified offset.
     *
     * @param array the array whose elements are randomized.
     * @return given {@code array}.
     * @throws NullPointerException if {@code array} is {@code null}.
     */
    public static byte[] randomize(final byte[] array) {
        return randomize(array, 0);
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaLangArrayUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
