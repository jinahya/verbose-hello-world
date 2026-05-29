package com.github.jinahya.hello.api.util;

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

import java.util.*;

/**
 * Range-validation helpers for {@code byte[]} arrays — the two conventional shapes used in
 * {@link HelloWorld}'s array-based API (start-only, {@code offset/length} window, and
 * {@code from/to} half-open interval).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaLangArrayUtils {

    /**
     * Asserts that {@code array} is non-{@code null} and {@code offset} is non-negative.
     *
     * @param array  the array being indexed; must not be {@code null}.
     * @param offset the candidate start offset; must be non-negative.
     * @param name   the parameter name to use in any thrown
     *               {@link IllegalArgumentException} message.
     * @throws NullPointerException     if {@code array} is {@code null}.
     * @throws IllegalArgumentException if {@code offset} is negative.
     */
    public static void requireValidStart(final byte[] array, final int offset, final String name) {
        Objects.requireNonNull(array, "array is null");
        if (offset < 0) {
            throw new IllegalArgumentException(name + "(" + offset + ") < 0");
        }
    }

    // ------------------------------------------------------------------------------- offset/length

    /**
     * Asserts that the {@code [offset, offset + length)} window lies entirely within
     * {@code array}.
     *
     * @param array  the array being indexed; must not be {@code null}.
     * @param offset the window's starting offset; must be non-negative.
     * @param length the window's length (negative values are detected via the upper-bound check).
     * @throws NullPointerException     if {@code array} is {@code null}.
     * @throws IllegalArgumentException if {@code offset} is negative or
     *                                  {@code offset + length} exceeds {@code array.length}.
     */
    public static void requireValidRange1(final byte[] array, final int offset, final int length) {
        requireValidStart(array, offset, "offset");
        if (offset + length > array.length) {
            throw new IllegalArgumentException(
                    "offset(" + offset + ") + length(" + length + ")" +
                    " > array.length(" + array.length + ")");
        }
    }

    // ------------------------------------------------------------------------------------- to/from

    /**
     * Asserts that the half-open interval {@code [from, to)} lies entirely within {@code array}.
     *
     * @param array the array being indexed; must not be {@code null}.
     * @param from  the inclusive start of the interval; must be non-negative.
     * @param to    the exclusive end of the interval; must satisfy
     *              {@code from <= to <= array.length}.
     * @throws NullPointerException     if {@code array} is {@code null}.
     * @throws IllegalArgumentException if {@code from} is negative, {@code to} is less than
     *                                  {@code from}, or {@code to} exceeds {@code array.length}.
     */
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
