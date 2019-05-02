package com.github.jinahya.hello;

import java.nio.charset.StandardCharsets;

/**
 * An implementation of {@link HelloWorld} for demonstration.
 */
class HelloWorldDemo implements HelloWorld {

    @Override
    public byte[] set(byte[] array, final int index) {
        final byte[] bytes = "hello, world".getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(bytes, 0, array, index, bytes.length);
        return array;
    }
}
