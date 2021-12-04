package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * A class for unit-testing {@link HelloWorld#set(byte[])} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_01_Set_Array_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws a {@link NullPointerException} when the {@code array}
     * argument is {@code null}.
     */
    @DisplayName("set(null) throws NullPointerException")
    @Test
    void set_NullPointerException_ArrayIsNull() {
        final byte[] array = null;
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method throws an {@link IndexOutOfBoundsException} when {@code
     * array.length} is less than {@link HelloWorld#BYTES}.
     */
    @DisplayName("set(array) throws IndexOutOfBoundsException when array.length is less than BYTES")
    @Test
    void set_IndexOutOfBoundsException_ArrayLengthIsLessThanBYTES() {
        final int length = new Random().nextInt(HelloWorld.BYTES);
        final byte[] array = new byte[length];
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method invokes {@link HelloWorld#set(byte[], int)} method with given
     * {@code array} and {@code 0}.
     */
    @DisplayName("set(array) invokes set(array, 0)")
    @Test
    void set_InvokesSetArrayWithArrayAndZero_() {
        final byte[] array = new byte[HelloWorld.BYTES];
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[])} method returns given {@code array} argument.
     */
    @DisplayName("set(array) returns array")
    @Test
    void set_ReturnArray_() {
        final byte[] expected = new byte[HelloWorld.BYTES];
        final byte[] actual = helloWorld().set(expected);
        // TODO: Implement!
    }
}
