package com.github.jinahya.hello.lib;

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

import com.github.jinahya.hello.api.HelloWorld;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

/**
 * An abstract base for tests that exercise the
 * {@link HelloWorld#set(byte[], int) set(array, index)} contract against arbitrary
 * {@link HelloWorld} implementations.
 * <p>
 * Concrete subclasses supply the implementations under test by overriding {@link #services()}; this
 * class supplies the four {@link TestFactory @TestFactory} methods that verify, for every supplied
 * service, the documented contract of {@link HelloWorld#set(byte[], int)}:
 * <ul>
 *   <li>{@link NullPointerException} when the {@code array} argument is {@code null}.</li>
 *   <li>{@link IndexOutOfBoundsException} when the {@code index} argument is negative.</li>
 *   <li>{@link IndexOutOfBoundsException} when {@code array.length} is less than
 *       {@code index + }{@value HelloWorld#BYTES}.</li>
 *   <li>Successful execution writes the
 *       <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to {@code array}
 *       starting at {@code index} and returns the same {@code array}.</li>
 * </ul>
 * Each verification is emitted as a {@link DynamicTest} per service so test reports show one row
 * per (test &times; service) pair.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PACKAGE)
abstract class HelloWorld__Test {

    /**
     * Returns the stream of {@link HelloWorld} implementations to test. Each emitted instance is
     * used as a parameter for every {@link TestFactory @TestFactory} verification defined in this
     * class, producing one {@link DynamicTest} per (test &times; service) pair.
     *
     * @return a non-{@code null} stream of {@link HelloWorld} instances; must be consumable.
     */
    abstract Stream<HelloWorld> services();

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that, for each service from {@link #services()},
     * {@link HelloWorld#set(byte[], int) set(array, index)} throws a {@link NullPointerException}
     * when the {@code array} argument is {@code null}. The {@code index} argument is a random
     * non-negative {@code int}.
     *
     * @return a stream of dynamic tests, one per service.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <array> argument is <null>"""
    )
    @TestFactory
    Stream<DynamicTest> _ThrowNullPointerException_ArrayIsNull() {
        return services().map(s -> {
            // ------------------------------------------------------------------------------- given
            final var array = (byte[]) null;
            final var index = ThreadLocalRandom.current().nextInt() & Integer.MAX_VALUE;
            return DynamicTest.dynamicTest(
                    String.format("%1$s.set(%2$s, %3$d)", s, array, index),
                    () -> {
                        // --------------------------------------------------------------- when/then
                        // assert: s.set(array:null, index) throws a <NullPointerException>
                        Assertions.assertThrows(
                                NullPointerException.class,
                                () -> s.set(array, index)
                        );
                    }
            );
        });
    }

    /**
     * Verifies that, for each service from {@link #services()},
     * {@link HelloWorld#set(byte[], int) set(array, index)} throws an
     * {@link IndexOutOfBoundsException} when the {@code index} argument is negative. The
     * {@code array} argument is a zero-length {@code byte[]} so the negative-index check fires
     * before any length-based check could.
     *
     * @return a stream of dynamic tests, one per service.
     */
    @DisplayName("""
            should throw an <IndexOutOfBoundsException>
            when the <index> argument is negative"""
    )
    @TestFactory
    Stream<DynamicTest> _ThrowIndexOutOfBoundsException_IndexIsNegative() {
        return services().map(s -> {
            // ------------------------------------------------------------------------------- given
            final var array = new byte[0];
            final var index = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
            return DynamicTest.dynamicTest(
                    String.format("%1$s.set(%2$s, %3$d)", s, array, index),
                    () -> {
                        // --------------------------------------------------------------- when/then
                        // assert: <service<array, index:negative)>
                        //         throws an <IndexOutOfBoundsException>
                        Assertions.assertThrows(
                                IndexOutOfBoundsException.class,
                                () -> s.set(array, index)
                        );
                    }
            );
        });
    }

    /**
     * Verifies that, for each service from {@link #services()},
     * {@link HelloWorld#set(byte[], int) set(array, index)} throws an
     * {@link IndexOutOfBoundsException} when {@code array.length} is strictly less than
     * {@code index + }{@value HelloWorld#BYTES} — i.e., the array is too short to hold the
     * {@value HelloWorld#BYTES}-byte payload starting at {@code index}. The {@code index} and
     * {@code array.length} pair is randomized but constrained so the precondition holds.
     *
     * @return a stream of dynamic tests, one per service.
     */
    @DisplayName("""
            should throw an <IndexOutOfBoundsException>
            when <array.length> is less than <index + HelloWorld.BYTES>"""
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
                    String.format("%1$s.set(%2$s, %3$d)", s, array, index),
                    () -> {
                        // --------------------------------------------------------------- when/then
                        // assert: <s.set(array, index(> array.length - 12))>
                        //         throws an <IndexOutOfBoundsException>
                        Assertions.assertThrows(
                                IndexOutOfBoundsException.class,
                                () -> s.set(array, index)
                        );
                    }
            );
        });
    }

    /**
     * Verifies that, for each service from {@link #services()},
     * {@link HelloWorld#set(byte[], int) set(array, index)} writes the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> into {@code array}
     * starting at {@code index}, and returns the same {@code array} reference. The {@code array}
     * and {@code index} pair is randomized but constrained so
     * {@code array.length >= index + }{@value HelloWorld#BYTES}.
     *
     * @return a stream of dynamic tests, one per service.
     */
    @DisplayName("should set <hello-world-bytes> on <array> starting at <index>")
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
                    String.format("%1$s.set(%2$s, %3$d)", s, array, index),
                    () -> {
                        // -------------------------------------------------------------------- when
                        final var result = s.set(array, index);
                        // -------------------------------------------------------------------- then
                        // assert: <result> is same as <array>
                        Assertions.assertSame(array, result);
                        // assert: 'hello, world' set on the <array> starting at <index>
                        Assertions.assertEquals('h', array[index]);
                        Assertions.assertEquals('e', array[index + 0x1]);
                        Assertions.assertEquals('l', array[index + 0x2]);
                        Assertions.assertEquals('l', array[index + 0x3]);
                        Assertions.assertEquals('o', array[index + 0x4]);
                        Assertions.assertEquals(',', array[index + 005]); // ?
                        Assertions.assertEquals(' ', array[index + 0x6]);
                        Assertions.assertEquals('w', array[index + 0x7]);
                        Assertions.assertEquals('o', array[index + 010]); // ?
                        Assertions.assertEquals('r', array[index + 011]); // ?
                        Assertions.assertEquals('l', array[index + 0xa]);
                        Assertions.assertEquals(0x64, array[index + 11]);
                    }
            );
        });
    }
}
