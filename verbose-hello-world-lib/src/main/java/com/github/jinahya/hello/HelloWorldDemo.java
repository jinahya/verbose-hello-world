package com.github.jinahya.hello;

import static java.lang.System.arraycopy;
import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * An implementation of {@link HelloWorld} for demonstration.
 */
class HelloWorldDemo implements HelloWorld {

    @Override
    public byte[] set(byte[] array, final int index) {
        arraycopy("hello, world".getBytes(US_ASCII), 0, array, index, SIZE);
        return array;
    }
}
