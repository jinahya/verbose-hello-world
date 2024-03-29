package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-lib
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A class for testing {@link HelloWorldImpl} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldImplTest {

    /**
     * Verifies that {@link HelloWorldImpl#set(byte[], int)} method throws
     * {@link NullPointerException} when the {@code array} argument is {@code null}.
     */
    @DisplayName("set(null, ) -> NullPointerException")
    @Test
    void set_ThrowNullPointerException_ArrayIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var instance = new HelloWorldImpl();
        final var array = (byte[]) null;
        final var index = 0;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                NullPointerException.class,
                () -> instance.set(array, index)
        );
    }

    /**
     * Verifies that {@link HelloWorldImpl#set(byte[], int)} method throws
     * {@link IndexOutOfBoundsException} when the {@code index} argument is negative.
     */
    @DisplayName("set(, negative) -> IndexOutOfBoundsException")
    @Test
    void set_IndexOutOfBoundsException_IndexIsNegative() {
        // ----------------------------------------------------------------------------------- given
        final var instance = new HelloWorldImpl();
        final var array = new byte[0];
        final var index = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> instance.set(array, index)
        );
    }

    /**
     * Verifies that {@link HelloWorldImpl#set(byte[], int)} method throws
     * {@link IndexOutOfBoundsException} when the {@code index} argument is greater than
     * ({@code array.length - }{@value HelloWorld#BYTES}).
     */
    @DisplayName("set(, negative) -> IndexOutOfBoundsException")
    @Test
    void set_IndexOutOfBoundsException_IndexIsGreaterThanArraySizeMinus12() {
        // ----------------------------------------------------------------------------------- given
        final var instance = new HelloWorldImpl();
        final byte[] array;
        {
            final var length = ThreadLocalRandom.current().nextInt(
                    HelloWorld.BYTES, HelloWorld.BYTES << 1
            );
            array = new byte[length];
        }
        final var index = ThreadLocalRandom.current().nextInt(
                array.length - HelloWorld.BYTES + 1,
                array.length << 1
        );
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> instance.set(array, index)
        );
    }

    /**
     * Verifies that {@link HelloWorldImpl#set(byte[], int)} method sets the hello-world-bytes on
     * {@code array} starting at {@code index}, and returns given {@code array}.
     */
    @DisplayName("set(array, index) -> array with 'hello, world' at index")
    @Test
    void set__() {
        // ----------------------------------------------------------------------------------- given
        final var instance = new HelloWorldImpl();
        final byte[] array;
        {
            final var length = ThreadLocalRandom.current().nextInt(
                    HelloWorld.BYTES,
                    HelloWorld.BYTES << 1
            );
            array = new byte[length];
        }
        final var index = ThreadLocalRandom.current().nextInt(array.length - HelloWorld.BYTES);
        // ------------------------------------------------------------------------------------ when
        final var result = instance.set(array, index);
        // ------------------------------------------------------------------------------------ then
        // TODO: assert 'hello, world' bytes set on the array starting at index
        // TODO: assert result is same as array
    }
}
