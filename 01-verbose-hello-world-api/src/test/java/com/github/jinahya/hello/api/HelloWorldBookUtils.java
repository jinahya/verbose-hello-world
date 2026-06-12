package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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
import java.util.stream.*;

/**
 * Package-private utilities shared by the test sources.
 * <ul>
 *   <li>{@link #toSimplifiedString(String)} — strips both the package prefix and any
 *       enclosing-class prefixes from a class-name-like string; used by overridden
 *       {@link Object#toString() toString()} methods to produce readable log output.</li>
 *   <li>{@link #formatArray(byte[])} renders a {@code byte[]} payload as bracketed
 *       {@code <hex>'<char>'} tokens (e.g. {@code [68'h' 65'e' …]}) for human-readable log lines;
 *       {@link #formatByte(byte)} is the per-byte building block.</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class HelloWorldBookUtils {

    /**
     * Strips both the package prefix and any enclosing-class prefixes from a class-name-like
     * {@code string}, returning the simple-name segment (plus anything after it, e.g. an
     * {@code @<hash>} suffix on an {@link Object#toString() Object.toString()} result).
     * <ul>
     *   <li>{@code "a.b.c.D"}        &rarr; {@code "D"}</li>
     *   <li>{@code "a.b.c.D$E$F"}    &rarr; {@code "F"}</li>
     *   <li>{@code "a.b.c.D@1f2"}    &rarr; {@code "D@1f2"}</li>
     *   <li>{@code "a.b.c.D$E@1f2"}  &rarr; {@code "E@1f2"}</li>
     *   <li>{@code "D"}              &rarr; {@code "D"}</li>
     * </ul>
     *
     * @param string the string to simplify; must not be {@code null}.
     * @return the substring starting at the byte after the last {@code '.'} or {@code '$'}, or
     * {@code string} itself if neither character is present.
     * @throws NullPointerException if {@code string} is {@code null}.
     */
    static String toSimplifiedString(final String string) {
        Objects.requireNonNull(string, "string is null");
        final var cut = Math.max(string.lastIndexOf('.'), string.lastIndexOf('$'));
        return cut < 0 ? string : string.substring(cut + 1);
    }

    static String formatByte(final byte b) {
        return String.format("%02x'%c'", b, b);
    }

    /**
     * Renders a {@code byte[]} as a bracketed, space-separated sequence of {@code <hex>'<char>'}
     * tokens — e.g. the {@value HelloWorld#BYTES}-byte hello-world payload becomes
     * {@code [68'h' 65'e' 6c'l' 6c'l' 6f'o' 2c',' 20' ' 77'w' 6f'o' 72'r' 6c'l' 64'd']}.
     * <p>
     * Each byte is formatted with {@code %02x'%c'} (lower-case hex, single-quoted ASCII
     * character).
     *
     * @param array the array to format; must not be {@code null}.
     * @return a human-readable representation of {@code array}; never {@code null}.
     * @throws NullPointerException if {@code array} is {@code null}.
     */
    static String formatArray(final byte[] array) {
        return IntStream.range(0, array.length)
                .mapToObj(i -> formatByte(array[i]))
                .collect(Collectors.joining(" ", "[", "]"));
    }

    private HelloWorldBookUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
