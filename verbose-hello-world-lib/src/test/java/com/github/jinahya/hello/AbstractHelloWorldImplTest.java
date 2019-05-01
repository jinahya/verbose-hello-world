package com.github.jinahya.hello;

import org.junit.jupiter.api.Test;

/**
 * A class for testing {@link HelloWorldImpl}.
 */
abstract class AbstractHelloWorldImplTest {

    /**
     * Returns an instance of {@link HelloWorld} for testing.
     *
     * @return an instance of {@link HelloWorld}.
     */
    abstract HelloWorld helloWorld();

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

    /**
     * Asserts {@link HelloWorldImpl#set(byte[], int)} method sets hello world bytes on specified array starting at
     * specified index.
     */
    @Test
    public void assertSetArraySetsHelloWorldBytesOnArrayStartingAtIndex() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorldImpl#set(byte[], int)} method returns specified array.
     */
    @Test
    public void assertSetArrayReturnsSpecifiedArray() {
        // TODO: implement!
    }
}
