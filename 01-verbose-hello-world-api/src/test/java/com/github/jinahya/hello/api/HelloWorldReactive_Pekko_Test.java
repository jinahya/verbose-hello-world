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

import lombok.*;
import lombok.extern.slf4j.*;
import org.apache.pekko.actor.*;
import org.apache.pekko.stream.javadsl.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests <a href="https://pekko.apache.org/docs/pekko/current/stream/">Apache Pekko Streams</a>'s
 * publisher-creation idioms — each test builds a {@link Source Source&lt;byte[],?&gt;} pulling the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> from the synchronous or
 * asynchronous service. Pekko is the Apache fork of Akka 2.6 and is API-compatible with the Akka
 * tests; the nested split is by terminal sink: single-value ({@link Sink#head()}) versus
 * multi-value ({@link Sink#seq()}).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("reactive — Pekko")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Pekko_Test extends HelloWorldReactive__Test {

    private static ActorSystem system;

    /**
     * Creates an {@link ActorSystem} shared by all tests in this class.
     */
    @BeforeAll
    static void setUpSystem() {
        system = ActorSystem.create("ReactiveHelloWorld_Pekko_Test");
    }

    /**
     * Terminates the {@link ActorSystem} after all tests.
     */
    @AfterAll
    static void shutDownSystem() {
        system.terminate();
        system.getWhenTerminated().toCompletableFuture().orTimeout(10L, TimeUnit.SECONDS).join();
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("single-value sinks")
    @Nested
    class Sink_head_Test {

        /**
         * Asserts that {@code Source.single(byte[]).runWith(Sink.head())} emits the
         * {@code hello-world-bytes}.
         */
        @DisplayName(
                "should emit <hello-world-bytes> via <Source.single(byte[]).runWith(Sink.head())>")
        @Test
        void __single() {
            // -------------------------------------------------------------------------- given/when
            final var array = Source.single(HelloWorldUtils.array(synchronousService()))
                    .runWith(Sink.head(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        /**
         * Asserts that {@code Source.lazySingle(Supplier).runWith(Sink.head())} emits the
         * {@code hello-world-bytes}.
         */
        @DisplayName("""
                should emit <hello-world-bytes>
                via <Source.lazySingle(Supplier).runWith(Sink.head())>""")
        @Test
        void __lazySingle() {
            // -------------------------------------------------------------------------- given/when
            final var array = Source.lazySingle(
                            () -> HelloWorldUtils.array(synchronousService()))
                    .runWith(Sink.head(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        /**
         * Asserts that
         * {@code Source.completionStage(asynchronousService.applyAsync(...)) .runWith(Sink.head())}
         * emits the {@code hello-world-bytes}.
         */
        @DisplayName("""
                should emit <hello-world-bytes>
                via <Source.completionStage(AsynchronousHelloWorld.applyAsync)
                .runWith(Sink.head())>""")
        @Test
        void __completionStage() {
            // -------------------------------------------------------------------------- given/when
            final var array = Source.completionStage(
                            asynchronousService().applyAsync(HelloWorldUtils::array))
                    .runWith(Sink.head(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("multi-value sinks")
    @Nested
    class Sink_seq_Test {

        /**
         * Asserts that {@code Source.from(Iterable).runWith(Sink.seq())} emits all elements as
         * {@code hello-world-bytes}.
         */
        @DisplayName(
                "should emit <hello-world-bytes> via <Source.from(Iterable).runWith(Sink.seq())>")
        @Test
        void __from_iterable() {
            // -------------------------------------------------------------------------- given/when
            final var list = Source.from(List.of(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService())
                    ))
                    .runWith(Sink.seq(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            assertEquals(3, list.size());
            for (final var element : list) {
                assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), element);
            }
        }

        /**
         * Asserts that {@code Source.range(0, n-1).map(...).runWith(Sink.seq())} emits {@code n}
         * elements of {@code hello-world-bytes}.
         */
        @DisplayName("""
                should emit <hello-world-bytes>
                via <Source.range(0, n-1).map(...).runWith(Sink.seq())>""")
        @Test
        void __range_map() {
            // -------------------------------------------------------------------------- given/when
            final var n = 5;
            final var list = Source.range(0, n - 1)
                    .map(i -> HelloWorldUtils.array(synchronousService()))
                    .runWith(Sink.<byte[]>seq(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            assertEquals(n, list.size());
            for (final var element : list) {
                assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                  element);
            }
        }

        /**
         * Asserts that {@code Source.repeat(byte[]).take(n).runWith(Sink.seq())} emits {@code n}
         * elements of {@code hello-world-bytes}.
         */
        @DisplayName("""
                should emit <hello-world-bytes>
                via <Source.repeat(byte[]).take(n).runWith(Sink.seq())>""")
        @Test
        void __repeat_take() {
            // -------------------------------------------------------------------------- given/when
            final var n = 5;
            final var list = Source.repeat(HelloWorldUtils.array(synchronousService()))
                    .take(n)
                    .runWith(Sink.<byte[]>seq(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            assertEquals(n, list.size());
            for (final var element : list) {
                assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                  element);
            }
        }
    }
}
