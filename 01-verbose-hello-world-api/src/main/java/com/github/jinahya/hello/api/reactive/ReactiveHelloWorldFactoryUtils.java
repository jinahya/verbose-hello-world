package com.github.jinahya.hello.api.reactive;

import java.util.List;
import java.util.Objects;

/**
 * Utility methods for {@link ReactiveHelloWorldFactory} implementations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class ReactiveHelloWorldFactoryUtils {

    /**
     * Converts a list of bytes to a byte array.
     *
     * @param list the list of bytes
     * @return a byte array containing the bytes from the list
     * @throws NullPointerException if {@code list} is null
     */
    static byte[] toByteArray(final List<Byte> list) {
        Objects.requireNonNull(list, "list is null");
        final var array = new byte[list.size()];
        for (var i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    // ---------------------------------------------------------------------------------------------
    private ReactiveHelloWorldFactoryUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
