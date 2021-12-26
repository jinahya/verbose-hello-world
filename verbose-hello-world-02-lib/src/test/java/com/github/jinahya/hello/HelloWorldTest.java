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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * An abstract class for testing classes implement {@link HelloWorld}
 * interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
abstract class HelloWorldTest {

    /**
     * Returns an instance of {@link HelloWorld} interface to test.
     *
     * @return an instance of {@link HelloWorld} interface.
     */
    abstract HelloWorld helloWorld();

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method
     * throws a {@code NullPointerException} when the {@code array} argument is
     * {@code null}.
     */
    @DisplayName("set(null, index) throws NullPointerException")
    @Test
    void set_ThrowNullPointerException_ArrayIsNull() {
        final HelloWorld helloWorld = helloWorld();
        Assumptions.assumeTrue(helloWorld != null);
        final byte[] array = null;
        final int index = new Random().nextInt() & Integer.MAX_VALUE;
        Assertions.assertThrows(NullPointerException.class,
                                () -> helloWorld.set(array, index));
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method
     * throws an {@code IndexOutOfBoundsException} when {@code index} argument
     * is negative.
     */
    @DisplayName("set(array, !positive) throws IndexOutOfBoundsException")
    @Test
    void set_ThrowIndexOutOfBoundsException_IndexIsNegative() {
        final HelloWorld helloWorld = helloWorld();
        Assumptions.assumeTrue(helloWorld != null);
        final byte[] array = new byte[0];
        final int index = current().nextInt() | Integer.MIN_VALUE;
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                                () -> helloWorld.set(array, index));
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method
     * throws an {@code IndexOutOfBoundsException} when ({@code index} + {@value
     * com.github.jinahya.hello.HelloWorld#BYTES}) is greater than {@code
     * array.length}.
     */
    @DisplayName(
            "set(array, index) throws IndexOutOfBoundsException when index + BYTES > array.length")
    @Test
    void set_ThrowsIndexOutOfBoundsException_SpaceIsNotEnough() {
        final HelloWorld helloWorld = helloWorld();
        Assumptions.assumeTrue(helloWorld != null);
        final byte[] array = new byte[HelloWorld.BYTES];
        final int index = new Random().nextInt(HelloWorld.BYTES - 1) + 1;
        Assertions.assertThrows(IndexOutOfBoundsException.class,
                                () -> helloWorld.set(array, index));
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method sets
     * "{@code hello, world}" bytes on specified array starting at specified
     * index.
     */
    @DisplayName(
            "set(array, index) sets \"hello, world\" bytes on array starting at index")
    @Test
    void set_SetsHelloWorldBytesOnArrayStartingAtIndex_() {
        final HelloWorld helloWorld = helloWorld();
        Assumptions.assumeTrue(helloWorld != null);
        final byte[] array = new byte[HelloWorld.BYTES];
        final int index = 0;
        helloWorld.set(array, index);
        // TODO: implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method
     * returns specified array.
     */
    @DisplayName("set(array, index) returns array")
    @Test
    void set_ReturnArray_() {
        final HelloWorld helloWorld = helloWorld();
        Assumptions.assumeTrue(helloWorld != null);
        final byte[] array = new byte[HelloWorld.BYTES];
        final int index = 0;
        final byte[] actual = helloWorld.set(array, index);
        Assertions.assertSame(array, actual);
    }
}
