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
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * An abstract class for testing classes implement {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
abstract class _HelloWorldTest {

    /**
     * Returns a stream of {@link HelloWorld} interface to test.
     *
     * @return a stream of {@link HelloWorld} interface.
     */
    abstract Stream<HelloWorld> services();

    /**
     * Verifies {@link HelloWorld#set(byte[], int) set(array, index)} method throws a
     * {@link NullPointerException} when the {@code array} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the array argument is null"""
    )
    @TestFactory
    Stream<DynamicTest> _ThrowNullPointerException_ArrayIsNull() {
        return services().map(s -> {
            // ------------------------------------------------------------------------------- given
            final var array = (byte[]) null;
            final var index = ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE;
            return DynamicTest.dynamicTest(
                    String.format("%1$s.set(%2$s, %3$d)", s, Arrays.toString(array), index),
                    () -> {
                        // --------------------------------------------------------------- when/then
                        Assertions.assertThrows(
                                NullPointerException.class,
                                () -> s.set(array, index)
                        );
                    }
            );
        });
    }

    /**
     * Verifies {@link HelloWorld#set(byte[], int) set(array, index)} method throws an
     * {@link IndexOutOfBoundsException} when the {@code index} argument is negative.
     */
    @DisplayName("""
            should throw an IndexOutOfBoundsException
            when the index argument is negative"""
    )
    @TestFactory
    Stream<DynamicTest> _ThrowIndexOutOfBoundsException_IndexIsNegative() {
        return services().map(s -> {
            // ------------------------------------------------------------------------------- given
            final var array = new byte[0];
            final var index = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
            return DynamicTest.dynamicTest(
                    String.format("%1$s.set(%2$s, %3$d)", s, Arrays.toString(array), index),
                    () -> {
                        // --------------------------------------------------------------- when/then
                        Assertions.assertThrows(
                                IndexOutOfBoundsException.class,
                                () -> s.set(array, index)
                        );
                    }
            );
        });
    }

    /**
     * Verifies {@link HelloWorld#set(byte[], int) set(array, index)} method throws an
     * {@link IndexOutOfBoundsException} when the {@code array.length} is less than
     * ({@code index + }{@value HelloWorld#BYTES}).
     */
    @DisplayName("""
            should throw an IndexOutOfBoundsException
            when array.length is less than (index + HelloWorld.BYTES)"""
    )
    @TestFactory
    Stream<DynamicTest> _ThrowIndexOutOfBoundsException_ArrayLengthLessThanIndexPlusBytes() {
        return services().map(s -> {
            // ------------------------------------------------------------------------------- given
            final var array = new byte[ThreadLocalRandom.current().nextInt(HelloWorld.BYTES << 1)];
            final var index = ThreadLocalRandom.current().nextInt(
                    Math.max(0, array.length - HelloWorld.BYTES + 1),
                    HelloWorld.BYTES << 2
            );
            assert array.length < (index + HelloWorld.BYTES);
            return DynamicTest.dynamicTest(
                    String.format("%1$s.set(%2$s, %3$d)", s, Arrays.toString(array), index),
                    () -> {
                        // --------------------------------------------------------------- when/then
                        Assertions.assertThrows(
                                IndexOutOfBoundsException.class,
                                () -> s.set(array, index)
                        );
                    }
            );
        });
    }

    /**
     * Verifies {@link HelloWorldImpl#set(byte[], int) set(array, index)} method sets the
     * <em>hello-world-bytes</em> on {@code array} starting at {@code index}, and returns given
     * {@code array}.
     */
    @DisplayName("should set hello-world-bytes on array starting at index")
    @TestFactory
    Stream<DynamicTest> _SetHelloWorldBytesOnArrayStartingAtIndex_() {
        return services().map(s -> {
            // ------------------------------------------------------------------------------- given
            final var array = new byte[
                    ThreadLocalRandom.current().nextInt(HelloWorld.BYTES, HelloWorld.BYTES << 1)
                    ];
            final var index = ThreadLocalRandom.current().nextInt(
                    array.length - HelloWorld.BYTES + 1
            );
            assert array.length >= index + HelloWorld.BYTES;
            return DynamicTest.dynamicTest(
                    String.format("%1$s.set(%2$s, %3$d)", s, Arrays.toString(array), index),
                    () -> {
                        // -------------------------------------------------------------------- when
                        final var result = s.set(array, index);
                        // -------------------------------------------------------------------- then
                        // DONE: assert, 'hello, world' set on array starting at index
                        final var buffer = ByteBuffer.wrap(array, index, HelloWorld.BYTES);
                        Assertions.assertEquals('h', buffer.get());
                        Assertions.assertEquals('e', buffer.get());
                        Assertions.assertEquals('l', buffer.get());
                        Assertions.assertEquals('l', buffer.get());
                        Assertions.assertEquals('o', buffer.get());
                        Assertions.assertEquals(',', buffer.get());
                        Assertions.assertEquals(' ', buffer.get());
                        Assertions.assertEquals('w', buffer.get());
                        Assertions.assertEquals('o', buffer.get());
                        Assertions.assertEquals('r', buffer.get());
                        Assertions.assertEquals('l', buffer.get());
                        Assertions.assertEquals('d', buffer.get());
                        // DONE: assert, result is same as array
                        Assertions.assertSame(array, result);
                    }
            );
        });
    }
}
