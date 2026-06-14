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

import io.smallrye.mutiny.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.time.*;
import java.util.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests <a href="https://smallrye.io/smallrye-mutiny/">SmallRye Mutiny</a>'s publisher-creation
 * idioms — each test builds a {@link Uni Uni&lt;byte[]&gt;} or {@link Multi Multi&lt;byte[]&gt;}
 * pulling the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> from the
 * synchronous or asynchronous service.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorldReactive / Mutiny")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Mutiny_Test extends HelloWorldReactive__Test {

    /**
     * Maximum time to wait for the reactive pipeline to emit.
     */
    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    @DisplayName("single-value idioms")
    @Nested
    class Uni_Test {

        /**
         * Asserts that {@code Uni.createFrom().item(byte[])} emits the {@code hello-world-bytes}.
         */
        @DisplayName("Uni.createFrom().item(byte[])")
        @Test
        void __createFrom_item() {
            // -------------------------------------------------------------------------- given/when
            final var array = Uni.createFrom()
                    .item(HelloWorldUtils.array(synchronousService()))
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), array);
        }

        /**
         * Asserts that {@code Uni.createFrom().item(Supplier)} emits the
         * {@code hello-world-bytes}.
         */
        @DisplayName("Uni.createFrom().item(Supplier)")
        @Test
        void __createFrom_item_supplier() {
            // -------------------------------------------------------------------------- given/when
            final var array = Uni.createFrom()
                    .item(() -> HelloWorldUtils.array(synchronousService()))
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), array);
        }

        /**
         * Asserts that {@code Uni.createFrom().completionStage(...)} bridged to
         * {@code asynchronousService.applyAsync(...)} emits the {@code hello-world-bytes}.
         */
        @DisplayName("Uni.createFrom().completionStage")
        @Test
        void __createFrom_completionStage() {
            // -------------------------------------------------------------------------- given/when
            final var array = Uni.createFrom()
                    .completionStage(() -> asynchronousService().applyAsync(
                            HelloWorldUtils::array
                    ))
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), array);
        }

        /**
         * Asserts that {@code Uni.createFrom().item(...).onItem().transform(byte[]::length)} emits
         * {@link HelloWorld#BYTES}.
         */
        @DisplayName("Uni.onItem().transform")
        @Test
        void __onItem_transform() {
            // -------------------------------------------------------------------------- given/when
            final var length = Uni.createFrom()
                    .item(() -> HelloWorldUtils.array(synchronousService()))
                    .onItem().transform(a -> a.length)
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            assertEquals(HelloWorld.BYTES, length);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("stream idioms")
    @Nested
    class Multi_Test {

        /**
         * Asserts that {@code Multi.createFrom().item(byte[])} emits one element as the
         * {@code hello-world-bytes}.
         */
        @DisplayName("Multi.createFrom().item(byte[])")
        @Test
        void __createFrom_item() {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.createFrom()
                    .item(HelloWorldUtils.array(synchronousService()))
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            assertEquals(1, list.size());
            assertArrayEquals(hello_world_byte_array(),
                              list.get(0));
        }

        /**
         * Asserts that {@code Multi.createFrom().items(byte[]...)} emits all elements as
         * {@code hello-world-bytes}.
         */
        @DisplayName("Multi.createFrom().items(varargs)")
        @Test
        void __createFrom_items() {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.createFrom()
                    .items(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService())
                    )
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            assertEquals(3, list.size());
            for (final var element : list) {
                assertArrayEquals(hello_world_byte_array(),
                                  element);
            }
        }

        /**
         * Asserts that {@code Multi.createFrom().iterable(List)} emits all elements as
         * {@code hello-world-bytes}.
         */
        @DisplayName("Multi.createFrom().iterable")
        @Test
        void __createFrom_iterable() {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.createFrom()
                    .iterable(List.of(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService())
                    ))
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            assertEquals(2, list.size());
            for (final var element : list) {
                assertArrayEquals(hello_world_byte_array(),
                                  element);
            }
        }

        /**
         * Asserts that {@code Multi.createFrom().<byte[]>emitter(...)} emits all pushed elements as
         * {@code hello-world-bytes}.
         */
        @DisplayName("Multi.createFrom().emitter")
        @Test
        void __createFrom_emitter() {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.createFrom().<byte[]>emitter(emitter -> {
                        emitter.emit(HelloWorldUtils.array(synchronousService()));
                        emitter.emit(HelloWorldUtils.array(synchronousService()));
                        emitter.complete();
                    })
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            assertEquals(2, list.size());
            for (final var element : list) {
                assertArrayEquals(hello_world_byte_array(),
                                  element);
            }
        }

        /**
         * Asserts that {@code Multi.createBy().repeating().supplier(...).atMost(n)} emits {@code n}
         * elements of {@code hello-world-bytes}.
         */
        @DisplayName("Multi.createBy().repeating()")
        @Test
        void __createBy_repeating() {
            // -------------------------------------------------------------------------- given/when
            final var n = 5;
            final var list = Multi.createBy()
                    .repeating().supplier(() -> HelloWorldUtils.array(synchronousService()))
                    .atMost(n)
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            assertEquals(n, list.size());
            for (final var element : list) {
                assertArrayEquals(hello_world_byte_array(),
                                  element);
            }
        }

        /**
         * Asserts that {@code Multi.createFrom().completionStage(...)} bridged to
         * {@code asynchronousService.applyAsync(...)} emits the {@code hello-world-bytes}.
         */
        @DisplayName("Multi.createFrom().completionStage")
        @Test
        void __createFrom_completionStage() {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.createFrom()
                    .completionStage(() -> asynchronousService().applyAsync(HelloWorldUtils::array))
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            assertEquals(1, list.size());
            assertArrayEquals(hello_world_byte_array(),
                              list.get(0));
        }
    }
}
