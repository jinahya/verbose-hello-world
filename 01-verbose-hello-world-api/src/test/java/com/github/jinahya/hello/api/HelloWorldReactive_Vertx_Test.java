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

import io.vertx.core.*;
import io.vertx.core.Future;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A pedagogical tour of <a href="https://vertx.io/docs/">Vert.x</a>'s own publisher-creation idioms
 * — each test creates a {@link Future Future&lt;byte[]&gt;} that pulls the <a
 * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> payload from either
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
@DisplayName("reactive — Vert.x")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Vertx_Test extends HelloWorldReactive__Test {

    // ---------------------------------------------------------------------------------------------
    @DisplayName("single-value idioms")
    @Nested
    class Future_Test {

        @DisplayName("should emit <hello-world-bytes> via <Future.succeededFuture(byte[])>")
        @Test
        void __succeededFuture() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var array = Future.succeededFuture(
                            HelloWorldUtils.array(synchronousService()))
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        @DisplayName(
                "should emit <hello-world-bytes> via <Promise.promise()> then <complete(byte[])>")
        @Test
        void __promise() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final Promise<byte[]> promise = Promise.promise();
            promise.complete(HelloWorldUtils.array(synchronousService()));
            final var array = promise.future()
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        @DisplayName("""
                should emit <hello-world-bytes>
                via <Future.fromCompletionStage(AsynchronousHelloWorld.applyAsync)>""")
        @Test
        void __fromCompletionStage() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var array = Future.fromCompletionStage(
                            asynchronousService().applyAsync(HelloWorldUtils::array))
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
        }

        @DisplayName("should return <BYTES> via <Future.succeededFuture(...).map(byte[]::length)>")
        @Test
        void __map() throws Exception {
            // -------------------------------------------------------------------------- given/when
            final var length = Future.succeededFuture(
                            HelloWorldUtils.array(synchronousService()))
                    .map(a -> a.length)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .get(10L, TimeUnit.SECONDS);
            // -------------------------------------------------------------------------------- then
            assertEquals(HelloWorld.BYTES, length.intValue());
        }
    }
}
