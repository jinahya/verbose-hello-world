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

import io.helidon.common.reactive.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A pedagogical tour of <a href="https://helidon.io/">Helidon</a> Common Reactive's own
 * publisher-creation idioms — each test creates a {@link Single Single&lt;byte[]&gt;} or
 * {@link Multi Multi&lt;byte[]&gt;} that pulls the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> payload from either
 * {@link #synchronousService() the synchronous service} or
 * {@link #asynchronousService() the asynchronous service} directly (no intermediate Reactive
 * Streams publisher).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("reactive — Helidon")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Helidon_Test extends HelloWorldReactive__Test {

    // ---------------------------------------------------------------------------------------------
    @DisplayName("single-value idioms")
    @Nested
    class Single_Test {

        @DisplayName("should emit <hello-world-bytes> via <Single.just(byte[])>")
        @Test
        void __just() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var array = Single.just(HelloWorldUtils.array(synchronousService()))
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), array);
        }

        @DisplayName("""
                should emit <hello-world-bytes> via <Single.create(CompletionStage)>
                from <AsynchronousHelloWorld.applyAsync>""")
        @Test
        void __create_completionStage() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var array = Single.create(
                            asynchronousService().applyAsync(HelloWorldUtils::array))
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), array);
        }

        @DisplayName("should return <BYTES> via <Single.just(...).map(byte[]::length)>")
        @Test
        void __map() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var length = Single.just(HelloWorldUtils.array(synchronousService()))
                    .map(a -> a.length)
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertEquals(HelloWorld.BYTES, length);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("stream idioms")
    @Nested
    class Multi_Test {

        @DisplayName("should emit <hello-world-bytes> via <Multi.just(byte[]...)>")
        @Test
        void __just_varargs() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.just(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService())
                    )
                    .collectList()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertEquals(3, list.size());
            for (final var element : list) {
                assertArrayEquals(hello_world_byte_array(),
                                  element);
            }
        }

        @DisplayName("should emit <hello-world-bytes> via <Multi.create(Iterable)>")
        @Test
        void __from_iterable() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.create(List.of(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService())
                    ))
                    .collectList()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertEquals(2, list.size());
            for (final var element : list) {
                assertArrayEquals(hello_world_byte_array(),
                                  element);
            }
        }

        @DisplayName("should emit <hello-world-bytes> via <Multi.create(Stream)>")
        @Test
        void __from_stream() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var n = 5;
            final var list = Multi.create(Stream.generate(
                                    () -> HelloWorldUtils.array(synchronousService()))
                                                  .limit(n))
                    .collectList()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertEquals(n, list.size());
            for (final var element : list) {
                assertArrayEquals(hello_world_byte_array(),
                                  element);
            }
        }

        @DisplayName("should emit <hello-world-bytes> via <Multi.singleton(byte[])>")
        @Test
        void __singleton() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.singleton(HelloWorldUtils.array(synchronousService()))
                    .collectList()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertEquals(1, list.size());
            assertArrayEquals(hello_world_byte_array(),
                              list.get(0));
        }
    }
}
