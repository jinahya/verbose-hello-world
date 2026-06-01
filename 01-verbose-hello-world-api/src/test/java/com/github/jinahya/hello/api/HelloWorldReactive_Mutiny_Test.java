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

/**
 * A pedagogical tour of <a href="https://smallrye.io/smallrye-mutiny/">SmallRye Mutiny</a>'s own
 * publisher-creation idioms — each test creates a Mutiny {@link Uni Uni&lt;byte[]&gt;} or
 * {@link Multi Multi&lt;byte[]&gt;} that pulls the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> payload from either
 * {@link #synchronousService() the synchronous service} or
 * {@link #asynchronousService() the asynchronous service} directly (no intermediate Reactive
 * Streams publisher).
 * <p>
 * The element type is fixed to {@code byte[]} so each test reads as a pure showcase of the library
 * factory in question.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("reactive — Mutiny")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Mutiny_Test extends HelloWorldReactive__Test {

    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    @DisplayName("single-value idioms")
    @Nested
    class Uni_Test {

        @DisplayName("should emit <hello-world-bytes> via <Uni.createFrom().item(byte[])>")
        @Test
        void __createFrom_item() {
            // -------------------------------------------------------------------------- given/when
            final var array = Uni.createFrom()
                    .item(HelloWorldUtils.array(synchronousService()))
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        @DisplayName("should emit <hello-world-bytes> via <Uni.createFrom().item(Supplier)>")
        @Test
        void __createFrom_item_supplier() {
            // -------------------------------------------------------------------------- given/when
            final var array = Uni.createFrom()
                    .item(() -> HelloWorldUtils.array(synchronousService()))
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        @DisplayName("""
                should emit <hello-world-bytes>
                via <Uni.createFrom().completionStage(AsynchronousHelloWorld.applyAsync)>""")
        @Test
        void __createFrom_completionStage() {
            // -------------------------------------------------------------------------- given/when
            final var array = Uni.createFrom()
                    .completionStage(() -> asynchronousService().applyAsync(
                            HelloWorldUtils::array
                    ))
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        @DisplayName("""
                should return <BYTES>
                via <Uni.createFrom().item(...).onItem().transform(byte[]::length)>""")
        @Test
        void __onItem_transform() {
            // -------------------------------------------------------------------------- given/when
            final var length = Uni.createFrom()
                    .item(() -> HelloWorldUtils.array(synchronousService()))
                    .onItem().transform(a -> a.length)
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(HelloWorld.BYTES, length);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("stream idioms")
    @Nested
    class Multi_Test {

        @DisplayName("should emit <hello-world-bytes> via <Multi.createFrom().item(byte[])>")
        @Test
        void __createFrom_item() {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.createFrom()
                    .item(HelloWorldUtils.array(synchronousService()))
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(1, list.size());
            Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                         list.get(0));
        }

        @DisplayName("should emit <hello-world-bytes> via <Multi.createFrom().items(byte[]...)>")
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
            Assertions.assertEquals(3, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                             element);
            }
        }

        @DisplayName("should emit <hello-world-bytes> via <Multi.createFrom().iterable(List)>")
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
            Assertions.assertEquals(2, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                             element);
            }
        }

        @DisplayName(
                "should emit <hello-world-bytes> via <Multi.createFrom().<byte[]>emitter(...)>")
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
            Assertions.assertEquals(2, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                             element);
            }
        }

        @DisplayName("""
                should emit <hello-world-bytes>
                via <Multi.createBy().repeating().supplier(...).atMost(n)>""")
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
            Assertions.assertEquals(n, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                             element);
            }
        }

        @DisplayName("""
                should emit <hello-world-bytes>
                via <Multi.createFrom().completionStage(AsynchronousHelloWorld.applyAsync)>""")
        @Test
        void __createFrom_completionStage() {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.createFrom()
                    .completionStage(() -> asynchronousService().applyAsync(HelloWorldUtils::array))
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(1, list.size());
            Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                         list.get(0));
        }
    }
}
