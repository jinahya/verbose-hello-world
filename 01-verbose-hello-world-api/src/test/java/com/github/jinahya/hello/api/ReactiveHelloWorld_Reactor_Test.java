package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

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
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class ReactiveHelloWorld_Reactor_Test extends ReactiveHelloWorld__Test {

    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    // ---------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return ReactiveHelloWorldTestUtils.toSimplifiedString(super.toString());
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(synchronousService());
    }

    private static void assertPayload(final byte[] array) {
        Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * The book-comparison set: the same four scenarios in every {@code _Reactive_<Lib>_Test} file,
     * shown side-by-side across libraries. Each library expresses the same intent through its own
     * publisher-creation idiom — produce a single value or a stream of values, from either the
     * synchronous {@link HelloWorld} service or the asynchronous {@link AsynchronousHelloWorld}
     * service.
     */
    @Nested
    @DisplayName("Introduction — sync/async × single/multiple")
    class Introduction_Test {

        private static final int N = 3;

        @Test
        @DisplayName("HelloWorld → single byte[]")
        void __sync_single() {
            final var array = Mono.fromSupplier(
                            () -> HelloWorldUtils.array(synchronousService()))
                    .block(TIMEOUT);
            assertPayload(array);
        }

        @Test
        @DisplayName("HelloWorld → N byte[]")
        void __sync_multiple() {
            final var list = Flux.range(0, N)
                    .map(i -> HelloWorldUtils.array(synchronousService()))
                    .collectList()
                    .block(TIMEOUT);
            Assertions.assertEquals(N, list.size());
            list.forEach(ReactiveHelloWorld_Reactor_Test::assertPayload);
        }

        @Test
        @DisplayName("AsynchronousHelloWorld → single byte[]")
        void __async_single() {
            final var array = Mono.fromCompletionStage(
                            () -> asynchronousService().applyAsync(HelloWorldUtils::array))
                    .block(TIMEOUT);
            assertPayload(array);
        }

        @Test
        @DisplayName("AsynchronousHelloWorld → N byte[]")
        void __async_multiple() {
            final var list = Flux.range(0, N)
                    .flatMap(i -> Mono.fromCompletionStage(
                            () -> asynchronousService().applyAsync(HelloWorldUtils::array)))
                    .collectList()
                    .block(TIMEOUT);
            Assertions.assertEquals(N, list.size());
            list.forEach(ReactiveHelloWorld_Reactor_Test::assertPayload);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Mono<byte[]> — single-value idioms")
    class Mono_Test {

        @Test
        @DisplayName("Mono.just(byte[]) → eager single value")
        void __just() {
            StepVerifier.create(Mono.just(HelloWorldUtils.array(synchronousService())))
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @Test
        @DisplayName("Mono.fromSupplier(Supplier) → lazy single value")
        void __fromSupplier() {
            StepVerifier.create(
                            Mono.fromSupplier(() -> HelloWorldUtils.array(synchronousService()))
                    )
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @Test
        @DisplayName("Mono.fromCallable(Callable) → lazy single value, may throw")
        void __fromCallable() {
            StepVerifier.create(
                            Mono.fromCallable(() -> HelloWorldUtils.array(synchronousService()))
                    )
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @Test
        @DisplayName("Mono.fromCompletionStage(AsynchronousHelloWorld#applyAsync)")
        void __fromCompletionStage() {
            StepVerifier.create(
                            Mono.fromCompletionStage(() -> asynchronousService()
                                    .applyAsync(HelloWorldUtils::array))
                    )
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @Test
        @DisplayName("Mono.fromSupplier(...).map(...) → transform")
        void __map() {
            StepVerifier.create(
                            Mono.fromSupplier(() -> HelloWorldUtils.array(synchronousService()))
                                    .map(a -> a.length))
                    .expectNext(HelloWorld.BYTES)
                    .expectComplete()
                    .verify(TIMEOUT);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Flux<byte[]> — stream idioms")
    class Flux_Test {

        @Test
        @DisplayName("Flux.just(byte[]) → one item + onComplete")
        void __just_single() {
            StepVerifier.create(
                            Flux.just(HelloWorldUtils.array(synchronousService())))
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @Test
        @DisplayName("Flux.just(byte[]...) → varargs stream + onComplete")
        void __just_varargs() {
            StepVerifier.create(Flux.just(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService())))
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @Test
        @DisplayName("Flux.fromIterable(List) → from existing collection")
        void __fromIterable() {
            StepVerifier.create(Flux.fromIterable(List.of(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService()))))
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @Test
        @DisplayName("Flux.create(FluxSink) → manual push sink")
        void __create() {
            StepVerifier.create(Flux.<byte[]>create(sink -> {
                        sink.next(HelloWorldUtils.array(synchronousService()));
                        sink.next(HelloWorldUtils.array(synchronousService()));
                        sink.complete();
                    }))
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @Test
        @DisplayName("Flux.range(0, n).map(...) → indexed stream")
        void __range_map() {
            final var n = 5;
            StepVerifier.create(
                            Flux.range(0, n)
                                    .map(i -> HelloWorldUtils.array(synchronousService())))
                    .expectNextCount(n)
                    .expectComplete()
                    .verify(TIMEOUT);
        }

        @Test
        @DisplayName("Flux.from(Mono.fromCompletionStage(AsynchronousHelloWorld#applyAsync))")
        void __from_mono_completionStage() {
            StepVerifier.create(Flux.from(
                            Mono.fromCompletionStage(() -> asynchronousService().applyAsync(
                                    HelloWorldUtils::array))))
                    .assertNext(ReactiveHelloWorld_Reactor_Test::assertPayload)
                    .expectComplete()
                    .verify(TIMEOUT);
        }
    }
}
