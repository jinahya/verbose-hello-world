package com.github.jinahya.hello;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A abstract class for unit-testing classes implement {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public abstract class AbstractHelloWorldTest {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns an instance of {@link HelloWorld} to test with.
     *
     * @return an instance of {@link HelloWorld}.
     */
    abstract HelloWorld helloWorld();

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Asserts {@link HelloWorld#set(byte[], int)} method throws a {@code NullPointerException} when {@code array}
     * argument is {@code null}.
     */
    @DisplayName("Assert set(byte[], int) method throws a NullPointerException when array is null")
    @Test
    public void assertSetArrayThrowsNullPointerExceptionIfArrayIsNull() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int)} method throws an {@code IndexOutOfBoundsException} when {@code index}
     * argument is negative.
     */
    @DisplayName("Assert set(byte[], int) method throws an IndexOutOfBoundsException when index is negative")
    @Test
    public void assertSetArrayThrowsIndexOutOfBoundsExceptionWhenIndexIsNegative() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int)} method throws an {@code IndexOutOfBoundsException} when {@code index}
     * argument plus {@link HelloWorld#BYTES} is greater than {@code array.length}.
     */
    @DisplayName("Assert set(byte[], int) method throws an IndexOutOfBoundsException when space is not enough")
    @Test
    public void assertSetArrayThrowsIndexOutOfBoundsExceptionWhenIndexPlusHelloWorldSizeIsGreaterThanArrayLength() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int)} method sets "{@code hello, world}" bytes on specified array starting
     * at specified index.
     */
    @DisplayName("Assert set(byte[], int) method sets \"hello, world\" bytes on array starting at index")
    @Test
    public void assertSetArraySetsHelloWorldBytesOnArrayStartingAtIndex() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int)} method returns specified array.
     */
    @DisplayName("Assert set(byte[], int) method returns specified array")
    @Test
    public void assertSetArrayReturnsSpecifiedArray() {
        // TODO: implement!
    }
}
