package com.github.jinahya.hello.api;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

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
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Mutiny_Test extends HelloWorldReactive__Test {

    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Uni<byte[]> — single-value idioms")
    class Uni_Test {

        @Test
        @DisplayName("Uni.createFrom().item(byte[]) → eager single value")
        void __createFrom_item() {
            // -------------------------------------------------------------------------- given/when
            final var array = Uni.createFrom()
                    .item(HelloWorldUtils.array(synchronousService()))
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Uni.createFrom().item(Supplier) → lazy single value")
        void __createFrom_item_supplier() {
            // -------------------------------------------------------------------------- given/when
            final var array = Uni.createFrom()
                    .item(() -> HelloWorldUtils.array(synchronousService()))
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Uni.createFrom().completionStage(AsynchronousHelloWorld#applyAsync)")
        void __createFrom_completionStage() {
            // -------------------------------------------------------------------------- given/when
            final var array = Uni.createFrom()
                    .completionStage(() -> asynchronousService().applyAsync(
                            HelloWorldUtils::array
                    ))
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Uni.createFrom().item(...).onItem().transform(...) → map")
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
    @Nested
    @DisplayName("Multi<byte[]> — stream idioms")
    class Multi_Test {

        @Test
        @DisplayName("Multi.createFrom().item(byte[]) → one item + onComplete")
        void __createFrom_item() {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.createFrom()
                    .item(HelloWorldUtils.array(synchronousService()))
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(1, list.size());
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), list.get(0));
        }

        @Test
        @DisplayName("Multi.createFrom().items(byte[]...) → varargs stream + onComplete")
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
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Multi.createFrom().iterable(List) → from existing collection")
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
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Multi.createFrom().<byte[]>emitter(...) → manual push emitter")
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
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Multi.createBy().repeating().supplier(...).atMost(n) → repeated supplier")
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
                Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName(
                "Multi.createFrom().completionStage(AsynchronousHelloWorld#applyAsync) → single-item Multi")
        void __createFrom_completionStage() {
            // -------------------------------------------------------------------------- given/when
            final var list = Multi.createFrom()
                    .completionStage(() -> asynchronousService().applyAsync(HelloWorldUtils::array))
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(1, list.size());
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), list.get(0));
        }
    }
}
