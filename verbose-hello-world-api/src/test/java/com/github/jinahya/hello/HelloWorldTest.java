package com.github.jinahya.hello;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * A class for testing {@link HelloWorld} class.
 */
@ExtendWith({MockitoExtension.class})
public class HelloWorldTest {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Asserts the value of {@link HelloWorld#SIZE} constant equals to the length of {@code hello, world} string in form
     * of bytes encoded with {@link StandardCharsets#US_ASCII} character set.
     */
    @Test
    void assertSizeEqualsToHelloWorldBytes() {
        assertEquals("hello, world".getBytes(StandardCharsets.US_ASCII).length, HelloWorld.SIZE);
    }

    // ----------------------------------------------------------------------------------------------------- set(byte[])

    /**
     * Asserts {@link HelloWorld#set(byte[])} throws {@code NullPointerException} when {@code array} is {@code null}.
     */
    @Test
    public void assertSetArrayThrowsNullPointerExceptionWhenArrayIsNull() {
        // @todo: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws {@code IndexOutOfBoundsException} when {@code array.length}
     * is less than {@link HelloWorld#SIZE}.
     */
    @Test
    public void assertSetArrayThrowsIndexOufOfBoundsExceptionWhenArrayLengthIsLessThanSize() {
        // @todo: implement!
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Test
    public void assertWriteStreamThrowsNullPointerExceptionWhenStreamIsNull() {
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Spy
    private HelloWorld helloWorld;
}
