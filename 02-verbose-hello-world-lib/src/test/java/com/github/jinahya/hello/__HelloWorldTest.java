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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * An abstract class for testing classes implement {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
abstract class __HelloWorldTest {

    /**
     * Returns an instance of {@link HelloWorld} interface to test.
     *
     * @return an instance of {@link HelloWorld} interface.
     */
    abstract HelloWorld service();

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method throws a
     * {@code NullPointerException} when the {@code array} argument is {@code null}.
     */
    @DisplayName("set(null, )NullPointerException")
    @Test
    void _ThrowNullPointerException_ArrayIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        log.debug("service: {}", service);
        final var array = (byte[]) null;
        final var index = 0;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                NullPointerException.class,
                () -> service.set(array, index)
        );
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method throws an
     * {@code IndexOutOfBoundsException} when {@code index} argument is negative.
     */
    @DisplayName("set(, negative)IndexOutOfBoundsException")
    @Test
    void _ThrowIndexOutOfBoundsException_IndexIsNegative() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        log.debug("service: {}", service);
        final var array = new byte[0];
        final var index = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
        assert index < 0;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> service.set(array, index)
        );
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method throws an
     * {@code IndexOutOfBoundsException} when ({@code index} +
     * {@value com.github.jinahya.hello.HelloWorld#BYTES}) is greater than {@code array.length}.
     */
    @DisplayName("set(, greater than array.length - 12)IndexOutOfBoundsException")
    @Test
    void _ThrowsIndexOutOfBoundsException_SpaceIsNotEnough() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        log.debug("service: {}", service);
        final var array = new byte[
                ThreadLocalRandom.current().nextInt(HelloWorld.BYTES, HelloWorld.BYTES << 1)
                ];
        final var index = ThreadLocalRandom.current().nextInt(
                array.length - HelloWorld.BYTES + 1, array.length << 1);
        assert index > array.length - HelloWorld.BYTES;
        assert index + HelloWorld.BYTES > array.length;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                IndexOutOfBoundsException.class,
                () -> service.set(array, index)
        );
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method, when invoked with
     * {@code byte[12]}, and {@code 0}, sets "{@code hello, world}" bytes on specified array
     * starting at specified index.
     */
    @DisplayName("set(array, 0)")
    @Test
    void _SetHelloWorldBytesOnArrayStartingAtIndex_120() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        log.debug("service: {}", service);
        final var array = new byte[HelloWorld.BYTES];
        final var index = 0;
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(array, index);
        // ------------------------------------------------------------------------------------ then
        assertSame(array, result);
        if (!(service instanceof HelloWorldDemo)) return; // TODO: remove this line!
        Assertions.assertEquals((byte) 0x68, array[0x0]); // 'h'
        Assertions.assertEquals((byte) 0x65, array[001]); // 'e'
        Assertions.assertEquals((byte) 0x6C, array[002]); // 'l'
        Assertions.assertEquals((byte) 0x6C, array[0x3]); // 'l'
        Assertions.assertEquals((byte) 0x6F, array[0x4]); // 'o'
        Assertions.assertEquals((byte) 0x2C, array[0x5]); // ','
        Assertions.assertEquals((byte) 0x20, array[0x6]); // ' '
        Assertions.assertEquals((byte) 0x77, array[0x7]); // 'w'
        Assertions.assertEquals((byte) 0x6F, array[010]); // 'o'
        Assertions.assertEquals((byte) 0x72, array[011]); // 'r'
        Assertions.assertEquals((byte) 0x6C, array[0xA]); // 'l'
        Assertions.assertEquals((byte) 0x64, array[013]); // 'd'
    }

    /**
     * Asserts {@link HelloWorld#set(byte[], int) set(array, index)} method sets
     * "{@code hello, world}" bytes on specified array starting at specified index.
     */
    @DisplayName("set(array, index)")
    @Test
    void _SetHelloWorldBytesOnArrayStartingAtIndex_() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        log.debug("service: {}", service);
        final var array = new byte[
                ThreadLocalRandom.current().nextInt(HelloWorld.BYTES, HelloWorld.BYTES << 1)
                ];
        final var index = array.length == HelloWorld.BYTES
                          ? 0
                          : ThreadLocalRandom.current().nextInt(0, array.length - HelloWorld.BYTES);
        assert array.length >= HelloWorld.BYTES;
        assert array.length >= index + HelloWorld.BYTES;
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(array, index);
        // ------------------------------------------------------------------------------------ then
        assertSame(array, result);
        final var expected = "hello, world";
        final var actual = new String(result, index, HelloWorld.BYTES, StandardCharsets.US_ASCII);
        if (!(service instanceof HelloWorldDemo)) return; // TODO: remove this line!
        assertEquals(expected, actual);
    }
}
