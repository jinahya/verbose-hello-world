package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-lib
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Integer.max;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * An abstract class for unit-testing classes implement {@link HelloWorld} interface.
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
    @DisplayName("set(array, index) throws NullPointerException when array is null")
    @Test
    public void assertSetArrayIndexThrowsNullPointerExceptionWhenArrayIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld().set(null, 0));
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int)} method throws an {@code IndexOutOfBoundsException} when {@code index}
     * argument is negative.
     */
    @DisplayName("set(array, index) throws IndexOutOfBoundsException when index is negative")
    @Test
    public void assertSetArrayIndexThrowsIndexOutOfBoundsExceptionWhenIndexIsNegative() {
        final byte[] array = new byte[0];
        final int index = current().nextInt() | Integer.MIN_VALUE;
        assertThrows(IndexOutOfBoundsException.class, () -> helloWorld().set(array, index));
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int)} method throws an {@code IndexOutOfBoundsException} when ({@code
     * index} + {@link HelloWorld#BYTES}) is greater than {@code array.length}.
     */
    @DisplayName("set(array, index) throws IndexOutOfBoundsException when index + BYTES > array.length")
    @Test
    public void assertSetArrayIndexThrowsIndexOutOfBoundsExceptionWhenSpaceIsNotEnough() {
        final byte[] array = new byte[current().nextInt(BYTES << 1)];
        final int index = max(0, current().nextInt(array.length - BYTES + 1, array.length + BYTES));
        assertTrue(index + BYTES > array.length);
        assertThrows(IndexOutOfBoundsException.class, () -> helloWorld().set(array, index));
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int)} method sets "{@code hello, world}" bytes on specified array starting
     * at specified index.
     */
    @DisplayName("set(array, index) sets \"hello, world\" bytes on array starting at index")
    @Test
    public void assertSetArrayIndexSetsHelloWorldBytesOnArrayStartingAtIndex() {
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int)} method returns specified array.
     */
    @DisplayName("set(array, index) returns specified array")
    @Test
    public void assertSetArrayIndexReturnsArray() {
        final byte[] expected = new byte[BYTES];
        final byte[] actual = helloWorld().set(expected, 0);
        assertEquals(expected, actual);
    }
}
