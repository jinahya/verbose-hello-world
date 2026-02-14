package com.github.jinahya.hello.api;

import java.util.Objects;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class HelloWorldValidator {

    static byte[] requireValid(final byte[] array) {
        Objects.requireNonNull(array, "array is null");
        if (array.length < HelloWorld.BYTES) {
            throw new IllegalArgumentException(
                    "array.length(" + array.length + ") < " + HelloWorld.BYTES
            );
        }
        return array;
    }

    static void requireValid(final byte[] array, final int index) {
        requireValid(array);
        if (index < 0) {
            throw new IllegalArgumentException("index(" + index + ") < 0");
        }
        if (index + HelloWorld.BYTES > array.length) {
            throw new IllegalArgumentException(
                    "index(" + index + ") + " + HelloWorld.BYTES +
                    " > array.length(" + array.length
                    + ")"
            );
        }
    }

    private HelloWorldValidator() {
        throw new AssertionError("instantiation is not allowed");
    }
}
