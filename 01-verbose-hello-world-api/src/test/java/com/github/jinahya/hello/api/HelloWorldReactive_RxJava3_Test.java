package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import io.reactivex.rxjava3.core.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests <a href="https://github.com/ReactiveX/RxJava">RxJava 3</a>'s publisher-creation idioms —
 * each test builds a {@link Single Single&lt;byte[]&gt;}, {@link Maybe Maybe&lt;byte[]&gt;}, or
 * {@link Flowable Flowable&lt;byte[]&gt;} pulling the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> from the synchronous or
 * asynchronous service.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorldReactive / RxJava 3")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_RxJava3_Test extends HelloWorldReactive__Test {

    // ---------------------------------------------------------------------------------------------
    @DisplayName("exactly-one-value idioms")
    @Nested
    class Single_Test {

        /**
         * Asserts that {@code Single.just(byte[])} emits the {@code hello-world-bytes}.
         */
        @DisplayName("Single.just")
        @Test
        void __just() {
            // -------------------------------------------------------------------------- given/when
            final var array = Single.just(HelloWorldUtils.array(synchronousService()))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        /**
         * Asserts that {@code Single.fromCallable(Callable)} emits the {@code hello-world-bytes}.
         */
        @DisplayName("Single.fromCallable")
        @Test
        void __fromCallable() {
            // -------------------------------------------------------------------------- given/when
            final var array = Single.fromCallable(
                            () -> HelloWorldUtils.array(synchronousService()))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        /**
         * Asserts that {@code Single.fromCompletionStage(asynchronousService.applyAsync(...))}
         * emits the {@code hello-world-bytes}.
         */
        @DisplayName("Single.fromCompletionStage")
        @Test
        void __fromCompletionStage() {
            // -------------------------------------------------------------------------- given/when
            final var array = Single.fromCompletionStage(
                            asynchronousService().applyAsync(HelloWorldUtils::array))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        /**
         * Asserts that {@code Single.fromCallable(...).map(byte[]::length)} emits
         * {@link HelloWorld#BYTES}.
         */
        @DisplayName("Single.map")
        @Test
        void __map() {
            // -------------------------------------------------------------------------- given/when
            final var length = Single.fromCallable(
                            () -> HelloWorldUtils.array(synchronousService()))
                    .map(a -> a.length)
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertEquals(HelloWorld.BYTES, length);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("0-or-1 value idioms")
    @Nested
    class Maybe_Test {

        /**
         * Asserts that {@code Maybe.just(byte[])} emits the {@code hello-world-bytes}.
         */
        @DisplayName("Maybe.just")
        @Test
        void __just() {
            // -------------------------------------------------------------------------- given/when
            final var array = Maybe.just(HelloWorldUtils.array(synchronousService()))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        /**
         * Asserts that {@code Maybe.fromCallable(Callable)} emits the {@code hello-world-bytes}.
         */
        @DisplayName("Maybe.fromCallable")
        @Test
        void __fromCallable() {
            // -------------------------------------------------------------------------- given/when
            final var array = Maybe.fromCallable(
                            () -> HelloWorldUtils.array(synchronousService()))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        /**
         * Asserts that {@code Maybe.fromCompletionStage(asynchronousService.applyAsync(...))} emits
         * the {@code hello-world-bytes}.
         */
        @DisplayName("Maybe.fromCompletionStage")
        @Test
        void __fromCompletionStage() {
            // -------------------------------------------------------------------------- given/when
            final var array = Maybe.fromCompletionStage(
                            asynchronousService().applyAsync(HelloWorldUtils::array))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("backpressured stream idioms")
    @Nested
    class Flowable_Test {

        /**
         * Asserts that {@code Flowable.just(byte[])} emits one {@code hello-world-bytes}.
         */
        @DisplayName("Flowable.just(single)")
        @Test
        void __just_single() {
            // -------------------------------------------------------------------------- given/when
            final var list = Flowable.just(HelloWorldUtils.array(synchronousService()))
                    .toList()
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertEquals(1, list.size());
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                              list.get(0));
        }

        /**
         * Asserts that {@code Flowable.just(byte[]...)} emits each {@code hello-world-bytes}
         * element.
         */
        @DisplayName("Flowable.just(varargs)")
        @Test
        void __just_varargs() {
            // -------------------------------------------------------------------------- given/when
            final var list = Flowable.just(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService())
                    )
                    .toList()
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertEquals(3, list.size());
            for (final var element : list) {
                assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                  element);
            }
        }

        /**
         * Asserts that {@code Flowable.fromIterable(List)} emits each {@code hello-world-bytes}
         * element.
         */
        @DisplayName("Flowable.fromIterable")
        @Test
        void __fromIterable() {
            // -------------------------------------------------------------------------- given/when
            final var list = Flowable.fromIterable(List.of(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService())
                    ))
                    .toList()
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertEquals(2, list.size());
            for (final var element : list) {
                assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                  element);
            }
        }

        /**
         * Asserts that {@code Flowable.create(emitter, BackpressureStrategy.BUFFER)} emits each
         * pushed {@code hello-world-bytes} element.
         */
        @DisplayName("Flowable.create")
        @Test
        void __create() {
            // -------------------------------------------------------------------------- given/when
            final var list = Flowable.<byte[]>create(emitter -> {
                        emitter.onNext(HelloWorldUtils.array(synchronousService()));
                        emitter.onNext(HelloWorldUtils.array(synchronousService()));
                        emitter.onComplete();
                    }, BackpressureStrategy.BUFFER)
                    .toList()
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertEquals(2, list.size());
            for (final var element : list) {
                assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                  element);
            }
        }

        /**
         * Asserts that {@code Flowable.range(0, n).map(...)} emits {@code n}
         * {@code hello-world-bytes} elements.
         */
        @DisplayName("Flowable.range + map")
        @Test
        void __range_map() {
            // -------------------------------------------------------------------------- given/when
            final var n = 5;
            final var list = Flowable.range(0, n)
                    .map(i -> HelloWorldUtils.array(synchronousService()))
                    .toList()
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            assertEquals(n, list.size());
            for (final var element : list) {
                assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                  element);
            }
        }
    }
}
