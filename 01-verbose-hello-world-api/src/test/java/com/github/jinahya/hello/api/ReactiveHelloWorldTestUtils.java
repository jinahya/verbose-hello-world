package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Package-private utilities shared by the {@code ReactiveHelloWorld*} family of tests.
 * <ul>
 *   <li>{@link #toSimplifiedString(String)} — strips both the package prefix and any
 *       enclosing-class prefixes from a class-name-like string; used by overridden
 *       {@link Object#toString() toString()} methods throughout the test sources to produce
 *       readable log output.</li>
 *   <li>{@link #formatArray(byte[])} — renders a {@code byte[]} payload as
 *       {@code [<hex>'<char>' <hex>'<char>' ...]} (e.g. {@code [68'h' 65'e' …]}); used by the
 *       byte-array logging subscribers to produce human-readable {@code onNext} log lines.</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class ReactiveHelloWorldTestUtils {

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

    private ReactiveHelloWorldTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
