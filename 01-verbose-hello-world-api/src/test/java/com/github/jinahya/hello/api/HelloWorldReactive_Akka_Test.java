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

import akka.actor.*;
import akka.stream.javadsl.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.api.HelloWorldUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A pedagogical tour of <a href="https://doc.akka.io/docs/akka/current/stream/">Akka Streams</a>'s
 * own publisher-creation idioms — each test creates a {@link Source Source&lt;byte[],?&gt;} that
 * pulls the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> payload from either
 * {@link #synchronousService() the synchronous service} or
 * {@link #asynchronousService() the asynchronous service} directly (no intermediate Reactive
 * Streams publisher).
 * <p>
 * Akka has only a single source type, so the nested split is by <em>terminal sink</em>:
 * single-value sinks ({@link Sink#head()}) versus multi-value sinks ({@link Sink#seq()}).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Akka_Test extends HelloWorldReactive__Test {

    private static ActorSystem system;

    @BeforeAll
    static void setUpSystem() {
        system = ActorSystem.create("ReactiveHelloWorld_Akka_Test");
    }

    @AfterAll
    static void shutDownSystem() {
        system.terminate();
        system.getWhenTerminated().toCompletableFuture().orTimeout(10L, TimeUnit.SECONDS).join();
    }

    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Source → Sink.head — single-value idioms")
    class Sink_head_Test {

        @Test
        @DisplayName("Source.single(byte[]).runWith(Sink.head()) → first element")
        void __single() {
            // -------------------------------------------------------------------------- given/when
            final var array = Source.single(array(synchronousService()))
                    .runWith(Sink.head(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), array);
        }

        @Test
        @DisplayName("Source.lazySingle(Supplier).runWith(Sink.head()) → lazy single")
        void __lazySingle() {
            // -------------------------------------------------------------------------- given/when
            final var array = Source.lazySingle(
                            () -> array(synchronousService()))
                    .runWith(Sink.head(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), array);
        }

        @Test
        @DisplayName(
                "Source.completionStage(AsynchronousHelloWorld#applyAsync).runWith(Sink.head())")
        void __completionStage() {
            // -------------------------------------------------------------------------- given/when
            final var array = Source.completionStage(
                            asynchronousService().applyAsync(HelloWorldUtils::array))
                    .runWith(Sink.head(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), array);
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Source → Sink.seq — stream idioms")
    class Sink_seq_Test {

        @Test
        @DisplayName("Source.from(Iterable).runWith(Sink.seq()) → collected list")
        void __from_iterable() {
            // -------------------------------------------------------------------------- given/when
            final var list = Source.from(List.of(
                            array(synchronousService()),
                            array(synchronousService()),
                            array(synchronousService())
                    ))
                    .runWith(Sink.<byte[]>seq(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(3, list.size());
            for (final var element : list) {
                assertArrayEquals(hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Source.range(0, n-1).map(...).runWith(Sink.seq()) → indexed stream")
        void __range_map() {
            // -------------------------------------------------------------------------- given/when
            final var n = 5;
            final var list = Source.range(0, n - 1)
                    .map(i -> array(synchronousService()))
                    .runWith(Sink.<byte[]>seq(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(n, list.size());
            for (final var element : list) {
                assertArrayEquals(hello_world_byte_array(), element);
            }
        }

        @Test
        @DisplayName("Source.repeat(byte[]).take(n).runWith(Sink.seq()) → repeated value")
        void __repeat_take() {
            // -------------------------------------------------------------------------- given/when
            final var n = 5;
            final var list = Source.repeat(array(synchronousService()))
                    .take(n)
                    .runWith(Sink.<byte[]>seq(), system)
                    .toCompletableFuture()
                    .orTimeout(10L, TimeUnit.SECONDS)
                    .join();
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(n, list.size());
            for (final var element : list) {
                assertArrayEquals(hello_world_byte_array(), element);
            }
        }
    }
}
