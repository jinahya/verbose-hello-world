package com.github.jinahya.hello.api;

import io.helidon.common.reactive.Multi;
import io.helidon.common.reactive.Single;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Helidon_Test extends HelloWorldReactive__Test {

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(synchronousService());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Single<byte[]> — single-value idioms")
    class Single_Test {

        @Test
        @DisplayName("Single.just(byte[]) → eager single value")
        void __just() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var array = Single.just(HelloWorldUtils.array(synchronousService()))
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Single.create(CompletionStage) → from AsynchronousHelloWorld#applyAsync")
        void __create_completionStage() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var array = Single.create(
                            asynchronousService().applyAsync(HelloWorldUtils::array))
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Single.just(...).map(...) → transform")
        void __map() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var length = Single.just(HelloWorldUtils.array(synchronousService()))
                    .map(a -> a.length)
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(HelloWorld.BYTES, length);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Multi<byte[]> — stream idioms")
    class Multi_Test {

        @Test
        @DisplayName("Multi.just(byte[]...) → varargs stream + onComplete")
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
            Assertions.assertEquals(3, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Multi.from(Iterable) → from existing collection")
        void __from_iterable() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.create(List.of(
                            HelloWorldUtils.array(synchronousService()),
                            HelloWorldUtils.array(synchronousService())
                    ))
                    .collectList()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(2, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Multi.create(Stream) → from java.util.stream.Stream")
        void __from_stream() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var n = 5;
            final var list = Multi.create(Stream.generate(
                                    () -> HelloWorldUtils.array(synchronousService()))
                            .limit(n))
                    .collectList()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(n, list.size());
            for (final var element : list) {
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Multi.singleton(byte[]) → one item + onComplete")
        void __singleton() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.singleton(HelloWorldUtils.array(synchronousService()))
                    .collectList()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(1, list.size());
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), list.get(0));
        }
    }
}
