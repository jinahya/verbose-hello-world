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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Integer.MAX_VALUE;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * An abstract class for testing classes implement {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
abstract class HelloWorldTest {

    /**
     * Returns an instance of {@link HelloWorld} interface to test.
     *
     * @return an instance of {@link HelloWorld} interface.
     */
    abstract HelloWorld serviceInstance();

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method throws a
     * {@code NullPointerException} when the {@code array} argument is {@code null}.
     */
    @DisplayName("set(null, index) throws NullPointerException")
    @Test
    void set_ThrowNullPointerException_ArrayIsNull() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var array = (byte[]) null;
        var index = current().nextInt() & MAX_VALUE;
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(NullPointerException.class,
                     () -> service.set((byte[]) null, 0));
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method throws an
     * {@code IndexOutOfBoundsException} when {@code index} argument is negative.
     */
    @DisplayName("set(array, index < 0)IndexOutOfBoundsException")
    @Test
    void set_ThrowIndexOutOfBoundsException_IndexIsNegative() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var array = new byte[0];
        var index = current().nextInt() | Integer.MIN_VALUE;
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(IndexOutOfBoundsException.class,
                     () -> service.set(array, index));
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method throws an
     * {@code IndexOutOfBoundsException} when ({@code index} +
     * {@value com.github.jinahya.hello.HelloWorld#BYTES}) is greater than {@code array.length}.
     */
    @DisplayName(
            "set(array, index > (array.length - 12))IndexOutOfBoundsException")
    @Test
    void set_ThrowsIndexOutOfBoundsException_SpaceIsNotEnough() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var array = new byte[BYTES << 1];
        var index = current().nextInt(BYTES + 1, MAX_VALUE);
        // ------------------------------------------------------------------------------- WHEN/THEN
        assertThrows(IndexOutOfBoundsException.class,
                     () -> service.set(array, index));
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method sets
     * "{@code hello, world}" bytes on specified array starting at specified index.
     */
    @DisplayName("""
            set(array, index) sets 'hello, world' bytes
            on array starting at index""")
    @Test
    void set_SetHelloWorldBytesOnArrayStartingAtIndex_() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        log.debug("service: {}", service);
        var array = new byte[BYTES];
        var index = 0;
        // ------------------------------------------------------------------------------------ WHEN
        var result = service.set(array, index);
        // ------------------------------------------------------------------------------------ THEN
        // TODO: Assert, array contains 'hello, world' bytes.
        assertSame(array, result);
    }
}
