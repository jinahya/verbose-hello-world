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
    public void assertSetArrayThrowsNullPointerExceptionWhenArrayIsNull() {
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
    public void assertSetArrayThrowsIndexOutOfBoundsExceptionWhenIndexPlusHelloWorldBytesIsGreaterThanArrayLength() {
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
