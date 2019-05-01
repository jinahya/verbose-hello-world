package com.github.jinahya.hello;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * A class for testing {@link HelloWorldImpl}.
 */
abstract class AbstractHelloWorldImplTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
