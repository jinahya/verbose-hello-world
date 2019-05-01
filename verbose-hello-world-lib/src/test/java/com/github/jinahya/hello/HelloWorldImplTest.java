package com.github.jinahya.hello;

import org.junit.jupiter.api.Test;

/**
 * A class for testing {@link HelloWorldImpl}.
 */
public class HelloWorldImplTest {

    /**
     * Asserts {@link HelloWorldImpl#set(byte[], int)} method throws a {@code NullPointerException} when {@code array}
     * argument is {@code null}.
     */
    @Test
    public void assertSetArrayThrowsNullPointerExceptionIfArrayIsNull() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorldImpl#set(byte[], int)} method throws an {@code IndexOutOfBoundsException} when {@code
     * index} argument is negative.
     */
    @Test
    public void assertSetArrayThrowsIndexOutOfBoundsExceptionWhenIndexIsNegative() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorldImpl#set(byte[], int)} method throws an {@code IndexOutOfBoundsException} when {@code
     * index} argument plus {@link HelloWorld#SIZE} is greater than {@code array.length}.
     */
    @Test
    public void assertSetArrayThrowsIndexOutOfBoundsExceptionWhenIndexPlusHelloWorldSizeIsGreaterThanArrayLength() {
        // TODO: implement!
    }
}
