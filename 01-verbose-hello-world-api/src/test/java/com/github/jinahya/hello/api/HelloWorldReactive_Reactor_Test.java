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
import org.junit.jupiter.api.*;
import reactor.core.publisher.*;
import reactor.test.*;

import java.time.*;
import java.util.*;

import static com.github.jinahya.hello.api.HelloWorldUtils.*;
import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests <a href="https://projectreactor.io/">Project Reactor</a>'s publisher-creation idioms — each
 * test builds a {@link Mono Mono&lt;byte[]&gt;} or {@link Flux Flux&lt;byte[]&gt;} pulling the <a
 * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> from the synchronous or
 * asynchronous service. Verification uses Reactor's own {@link StepVerifier}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorldReactive / Reactor")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Reactor_Test extends HelloWorldReactive__Test {

    /**
     * Maximum time to wait for the reactive pipeline to emit.
     */
    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    // ---------------------------------------------------------------------------------------------
    private static void assertPayload(final byte[] array) {
        assertArrayEquals(hello_world_byte_array(), array);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The book-comparison set: the same four scenarios in every {@code _Reactive_<Lib>_Test} file,
     * shown side-by-side across libraries. Each library expresses the same intent through its own
     * publisher-creation idiom — produce a single value or a stream of values, from either the
     * synchronous {@link HelloWorld} service or the asynchronous {@link AsynchronousHelloWorld}
     * service.
     */
    @DisplayName("introduction")
    @Nested
    class Introduction_Test {

        /**
         * Number of items emitted in the multi-element tests.
         */
        private static final int N = 3;

        /**
         * Asserts that {@code Mono.fromSupplier(() -> array(synchronousService()))} emits one
         * {@code hello-world-bytes}.
         */
        @DisplayName("sync / single")
        @Test
        void __sync_single() {
            final var array = Mono
                    .fromSupplier(() -> array(synchronousService()))
                    .block(TIMEOUT);
            assertPayload(array);
        }

        /**
         * Asserts that {@code Flux.range(0, N).map(...)} emits {@link #N} {@code hello-world-bytes}
         * items pulled from the synchronous service.
         */
        @DisplayName("sync / multiple")
        @Test
        void __sync_multiple() {
            final var list = Flux.range(0, N)
                    .map(i -> array(synchronousService()))
                    .collectList()
                    .block(TIMEOUT);
            assertEquals(N, list.size());
            list.forEach(HelloWorldReactive_Reactor_Test::assertPayload);
        }

        /**
         * Asserts that
         * {@code Mono.fromCompletionStage(() -> asynchronousService().applyAsync(...))} emits one
         * {@code hello-world-bytes}.
         */
        @DisplayName("async / single")
        @Test
        void __async_single() {
            final var array = Mono.fromCompletionStage(
                            () -> asynchronousService().applyAsync(HelloWorldUtils::array)
                    )
                    .block(TIMEOUT);
            assertPayload(array);
        }

        /**
         * Asserts that {@code Flux.range(0, N).flatMap(...)} emits {@link #N}
         * {@code hello-world-bytes} items produced by the asynchronous service.
         */
        @DisplayName("async / multiple")
        @Test
        void __async_multiple() {
            final var list = Flux.range(0, N)
                    .flatMap(i -> Mono.fromCompletionStage(
                            () -> asynchronousService().applyAsync(HelloWorldUtils::array)))
                    .collectList()
                    .block(TIMEOUT);
            assertEquals(N, list.size());
            list.forEach(HelloWorldReactive_Reactor_Test::assertPayload);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("single-value idioms")
    @Nested
    class Mono_Test {

        /**
         * Asserts that {@code Mono.just(byte[])} emits the {@code hello-world-bytes}.
         */
        @DisplayName("Mono.just")
        @Test
        void __just() {
            StepVerifier.create(Mono.just(array(synchronousService())))
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        /**
         * Asserts that {@code Mono.fromSupplier(Supplier)} emits the {@code hello-world-bytes}.
         */
        @DisplayName("Mono.fromSupplier")
        @Test
        void __fromSupplier() {
            StepVerifier.create(
                            Mono.fromSupplier(() -> array(synchronousService()))
                    )
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        /**
         * Asserts that {@code Mono.fromCallable(Callable)} emits the {@code hello-world-bytes}.
         */
        @DisplayName("Mono.fromCallable")
        @Test
        void __fromCallable() {
            StepVerifier.create(
                            Mono.fromCallable(() -> array(synchronousService()))
                    )
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        /**
         * Asserts that
         * {@code Mono.fromCompletionStage(() -> asynchronousService().applyAsync(...))} emits the
         * {@code hello-world-bytes}.
         */
        @DisplayName("Mono.fromCompletionStage")
        @Test
        void __fromCompletionStage() {
            StepVerifier.create(
                            Mono.fromCompletionStage(() -> asynchronousService()
                                    .applyAsync(HelloWorldUtils::array))
                    )
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        /**
         * Asserts that {@code Mono.fromSupplier(...).map(byte[]::length)} emits
         * {@link HelloWorld#BYTES}.
         */
        @DisplayName("Mono.map")
        @Test
        void __map() {
            StepVerifier.create(
                            Mono.fromSupplier(() -> array(synchronousService()))
                                    .map(a -> a.length))
                    .expectNext(HelloWorld.BYTES)
                    .expectComplete()
                    .verify(TIMEOUT);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("stream idioms")
    @Nested
    class Flux_Test {

        /**
         * Asserts that {@code Flux.just(byte[])} emits one {@code hello-world-bytes}.
         */
        @DisplayName("Flux.just(single)")
        @Test
        void __just_single() {
            StepVerifier.create(
                            Flux.just(array(synchronousService())))
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        /**
         * Asserts that {@code Flux.just(byte[]...)} emits each {@code hello-world-bytes} element.
         */
        @DisplayName("Flux.just(varargs)")
        @Test
        void __just_varargs() {
            StepVerifier.create(Flux.just(
                            array(synchronousService()),
                            array(synchronousService()),
                            array(synchronousService())))
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        /**
         * Asserts that {@code Flux.fromIterable(List)} emits each {@code hello-world-bytes}
         * element.
         */
        @DisplayName("Flux.fromIterable")
        @Test
        void __fromIterable() {
            StepVerifier.create(Flux.fromIterable(List.of(
                            array(synchronousService()),
                            array(synchronousService()))))
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        /**
         * Asserts that {@code Flux.create(FluxSink)} emits each pushed {@code hello-world-bytes}
         * element.
         */
        @DisplayName("Flux.create")
        @Test
        void __create() {
            StepVerifier.create(Flux.<byte[]>create(sink -> {
                        sink.next(array(synchronousService()));
                        sink.next(array(synchronousService()));
                        sink.complete();
                    }))
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        /**
         * Asserts that {@code Flux.range(0, n).map(...)} emits {@code n} {@code hello-world-bytes}
         * elements.
         */
        @DisplayName("Flux.range + map")
        @Test
        void __range_map() {
            final var n = 5;
            StepVerifier.create(
                            Flux.range(0, n)
                                    .map(i -> array(synchronousService())))
                    .expectNextCount(n)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        /**
         * Asserts that
         * {@code Flux.from(Mono.fromCompletionStage(asynchronousService.applyAsync(...)))} emits
         * one {@code hello-world-bytes}.
         */
        @DisplayName("Flux.from(Mono.fromCompletionStage)")
        @Test
        void __from_mono_completionStage() {
            StepVerifier.create(Flux.from(
                            Mono.fromCompletionStage(() -> asynchronousService().applyAsync(
                                    HelloWorldUtils::array))))
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }
    }
}
