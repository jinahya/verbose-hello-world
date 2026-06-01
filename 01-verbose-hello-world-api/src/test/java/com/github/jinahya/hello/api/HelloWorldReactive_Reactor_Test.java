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

/**
 * A pedagogical tour of <a href="https://projectreactor.io/">Project Reactor</a>'s own
 * publisher-creation idioms — each test creates a Reactor {@link Mono Mono&lt;byte[]&gt;} or
 * {@link Flux Flux&lt;byte[]&gt;} that pulls the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> payload from either
 * {@link #synchronousService() the synchronous service} or
 * {@link #asynchronousService() the asynchronous service} directly (no intermediate Reactive
 * Streams publisher).
 * <p>
 * Verification uses Reactor's own {@link StepVerifier}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("reactive — Reactor")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Reactor_Test extends HelloWorldReactive__Test {

    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    // ---------------------------------------------------------------------------------------------
    private static void assertPayload(final byte[] array) {
        Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
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

        private static final int N = 3;

        @DisplayName("""
                should emit a single <hello-world-bytes> from <HelloWorld>
                through <Mono.fromSupplier>""")
        @Test
        void __sync_single() {
            final var array = Mono
                    .fromSupplier(() -> array(synchronousService()))
                    .block(TIMEOUT);
            assertPayload(array);
        }

        @DisplayName("""
                should emit <N> copies of <hello-world-bytes> from <HelloWorld>
                through <Flux.range(0, N).map>""")
        @Test
        void __sync_multiple() {
            final var list = Flux.range(0, N)
                    .map(i -> array(synchronousService()))
                    .collectList()
                    .block(TIMEOUT);
            Assertions.assertEquals(N, list.size());
            list.forEach(HelloWorldReactive_Reactor_Test::assertPayload);
        }

        @DisplayName("""
                should emit a single <hello-world-bytes> from <AsynchronousHelloWorld>
                through <Mono.fromCompletionStage>""")
        @Test
        void __async_single() {
            final var array = Mono.fromCompletionStage(
                            () -> asynchronousService().applyAsync(HelloWorldUtils::array)
                    )
                    .block(TIMEOUT);
            assertPayload(array);
        }

        @DisplayName("""
                should emit <N> copies of <hello-world-bytes> from <AsynchronousHelloWorld>
                through <Flux.range(0, N).flatMap>""")
        @Test
        void __async_multiple() {
            final var list = Flux.range(0, N)
                    .flatMap(i -> Mono.fromCompletionStage(
                            () -> asynchronousService().applyAsync(HelloWorldUtils::array)))
                    .collectList()
                    .block(TIMEOUT);
            Assertions.assertEquals(N, list.size());
            list.forEach(HelloWorldReactive_Reactor_Test::assertPayload);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("single-value idioms")
    @Nested
    class Mono_Test {

        @DisplayName("should emit <hello-world-bytes> via <Mono.just(byte[])>")
        @Test
        void __just() {
            StepVerifier.create(Mono.just(array(synchronousService())))
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @DisplayName("should emit <hello-world-bytes> via <Mono.fromSupplier(Supplier)>")
        @Test
        void __fromSupplier() {
            StepVerifier.create(
                            Mono.fromSupplier(() -> array(synchronousService()))
                    )
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @DisplayName("should emit <hello-world-bytes> via <Mono.fromCallable(Callable)>")
        @Test
        void __fromCallable() {
            StepVerifier.create(
                            Mono.fromCallable(() -> array(synchronousService()))
                    )
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @DisplayName("""
                should emit <hello-world-bytes>
                via <Mono.fromCompletionStage(AsynchronousHelloWorld.applyAsync)>""")
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

        @DisplayName("should return <BYTES> via <Mono.fromSupplier(...).map(byte[]::length)>")
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

        @DisplayName("should emit <hello-world-bytes> via <Flux.just(byte[])>")
        @Test
        void __just_single() {
            StepVerifier.create(
                            Flux.just(array(synchronousService())))
                    .assertNext(HelloWorldReactive_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @DisplayName("should emit <hello-world-bytes> via <Flux.just(byte[]...)>")
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

        @DisplayName("should emit <hello-world-bytes> via <Flux.fromIterable(List)>")
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

        @DisplayName("should emit <hello-world-bytes> via <Flux.create(FluxSink)>")
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

        @DisplayName("should emit <hello-world-bytes> via <Flux.range(0, n).map(...)>")
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

        @DisplayName("""
                should emit <hello-world-bytes>
                via <Flux.from(Mono.fromCompletionStage(AsynchronousHelloWorld.applyAsync))>""")
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
