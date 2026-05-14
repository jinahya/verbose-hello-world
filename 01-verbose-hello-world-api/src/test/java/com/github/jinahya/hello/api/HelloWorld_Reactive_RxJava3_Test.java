package com.github.jinahya.hello.api;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * A pedagogical tour of <a href="https://github.com/ReactiveX/RxJava">RxJava 3</a>'s own
 * publisher-creation idioms — each test creates a {@link Single Single&lt;byte[]&gt;},
 * {@link Maybe Maybe&lt;byte[]&gt;}, or {@link Flowable Flowable&lt;byte[]&gt;} that pulls the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> payload from either
 * {@link #synchronousService() the synchronous service} or
 * {@link #asynchronousService() the asynchronous service} directly (no intermediate Reactive
 * Streams publisher).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Reactive_RxJava3_Test extends HelloWorld_Reactive__Test {

    // ---------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString().substring(getClass().getPackageName().length() + 1);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(synchronousService());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Single<byte[]> — exactly-one-value idioms")
    class Single_Test {

        @Test
        @DisplayName("Single.just(byte[]) → eager single value")
        void __just() {
            // -------------------------------------------------------------------------- given/when
            final var array = Single.just(HelloWorldUtils.array(synchronousService()))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Single.fromCallable(Callable) → lazy single value")
        void __fromCallable() {
            // -------------------------------------------------------------------------- given/when
            final var array = Single.fromCallable(
                            () -> HelloWorldUtils.array(synchronousService()))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Single.fromCompletionStage(AsynchronousHelloWorld#applyAsync)")
        void __fromCompletionStage() {
            // -------------------------------------------------------------------------- given/when
            final var array = Single.fromCompletionStage(
                            asynchronousService().applyAsync(HelloWorldUtils::array))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Single.fromCallable(...).map(...) → transform")
        void __map() {
            // -------------------------------------------------------------------------- given/when
            final var length = Single.fromCallable(
                            () -> HelloWorldUtils.array(synchronousService()))
                    .map(a -> a.length)
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(HelloWorld.BYTES, length);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Maybe<byte[]> — 0-or-1 value idioms")
    class Maybe_Test {

        @Test
        @DisplayName("Maybe.just(byte[]) → eager single value")
        void __just() {
            // -------------------------------------------------------------------------- given/when
            final var array = Maybe.just(HelloWorldUtils.array(synchronousService()))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Maybe.fromCallable(Callable) → lazy single value")
        void __fromCallable() {
            // -------------------------------------------------------------------------- given/when
            final var array = Maybe.fromCallable(
                            () -> HelloWorldUtils.array(synchronousService()))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Maybe.fromCompletionStage(AsynchronousHelloWorld#applyAsync)")
        void __fromCompletionStage() {
            // -------------------------------------------------------------------------- given/when
            final var array = Maybe.fromCompletionStage(
                            asynchronousService().applyAsync(HelloWorldUtils::array))
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Flowable<byte[]> — backpressured stream idioms")
    class Flowable_Test {

        @Test
        @DisplayName("Flowable.just(byte[]) → one item + onComplete")
        void __just_single() {
            // -------------------------------------------------------------------------- given/when
            final var list = Flowable.just(HelloWorldUtils.array(synchronousService()))
                    .toList()
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(1, list.size());
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), list.get(0));
        }

        @Test
        @DisplayName("Flowable.just(byte[]...) → varargs stream + onComplete")
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
            Assertions.assertEquals(3, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Flowable.fromIterable(List) → from existing collection")
        void __fromIterable() {
            // -------------------------------------------------------------------------- given/when
            final var list = Flowable.fromIterable(List.of(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService())
                    ))
                    .toList()
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(2, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Flowable.create(emitter, BackpressureStrategy.BUFFER) → manual push emitter")
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
            Assertions.assertEquals(2, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Flowable.range(0, n).map(...) → indexed stream")
        void __range_map() {
            // -------------------------------------------------------------------------- given/when
            final var n = 5;
            final var list = Flowable.range(0, n)
                    .map(i -> HelloWorldUtils.array(synchronousService()))
                    .toList()
                    .blockingGet();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(n, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }
    }
}
