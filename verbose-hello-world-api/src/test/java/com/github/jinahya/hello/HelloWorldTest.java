package com.github.jinahya.hello;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * A class for testing {@link HelloWorld} class.
 */
public class HelloWorldTest {

    /**
     * Asserts the value of {@link HelloWorld#SIZE} constant equals to the length of {@code hello, world} string in form
     * of bytes encoded with {@link StandardCharsets#US_ASCII} character set.
     */
    @Test
    void assertSizeEqualsToHelloWorldBytes() {
        final int expected = "hello, world".getBytes(StandardCharsets.US_ASCII).length;
        final int actual = HelloWorld.SIZE;
        Assertions.assertEquals(expected, actual, "HelloWorld.SIZE(" + actual + ") is not equal to " + expected);
    }
}
