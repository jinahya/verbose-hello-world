package com.github.jinahya.hello.api;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

/**
 * A pedagogical tour of <a href="https://vertx.io/docs/">Vert.x</a>'s own
 * publisher-creation idioms — each test creates a {@link Future Future&lt;byte[]&gt;} that pulls
 * the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> payload from either
 * {@link #synchronousService() the synchronous service} or
 * {@link #asynchronousService() the asynchronous service} directly (no intermediate Reactive
 * Streams publisher).
 * <p>
 * Vert.x's core reactive-value type is {@link Future Future&lt;T&gt;} — a 0/1-value asynchronous
 * primitive analogous to Reactor's {@code Mono}, Mutiny's {@code Uni}, RxJava's {@code Single}, and
 * Helidon's {@code Single}. Vert.x has no first-party multi-value publisher type in core; its
 * stream story routes through {@link io.vertx.core.streams.ReadStream ReadStream} (callback-based,
 * not a publisher-creation idiom) or through the {@code vertx-reactive-streams} /
 * {@code vertx-rx-java3} / {@code vertx-mutiny} bridges. Consequently this test exposes only the
 * single-value section.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class ReactiveHelloWorld_Vertx_Test extends ReactiveHelloWorld__Test {

    // ---------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return HelloWorldBookUtils.toSimplifiedString(super.toString());
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(synchronousService());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Future<byte[]> — single-value idioms")
    class Future_Test {

        @Test
        @DisplayName("Future.succeededFuture(byte[]) → eager single value")
        void __succeededFuture() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var array = Future.succeededFuture(
                            HelloWorldUtils.array(synchronousService()))
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Promise.promise() then complete(byte[]) → manually fulfilled future")
        void __promise() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final Promise<byte[]> promise = Promise.promise();
            promise.complete(HelloWorldUtils.array(synchronousService()));
            final var array = promise.future()
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Future.fromCompletionStage(AsynchronousHelloWorld#applyAsync) → from CompletionStage")
        void __fromCompletionStage() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var array = Future.fromCompletionStage(
                            asynchronousService().applyAsync(HelloWorldUtils::array))
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Future.succeededFuture(...).map(...) → transform")
        void __map() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var length = Future.succeededFuture(
                            HelloWorldUtils.array(synchronousService()))
                    .map(a -> a.length)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(HelloWorld.BYTES, length.intValue());
        }
    }
}
