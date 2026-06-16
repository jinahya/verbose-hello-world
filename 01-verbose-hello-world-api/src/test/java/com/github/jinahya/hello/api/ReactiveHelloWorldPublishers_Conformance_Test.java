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

import io.helidon.common.reactive.*;
import io.reactivex.rxjava3.core.*;
import io.vertx.ext.reactivestreams.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.reactivestreams.*;
import reactor.core.publisher.*;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Conformance tests for {@link ReactiveHelloWorldBytePublisher} and
 * {@link ReactiveHelloWorldArrayPublisher} — each consumed through a different
 * Reactive-Streams-compatible library (or a hand-rolled subscriber, for libraries that do not
 * expose a collect idiom).
 * <p>
 * Each library is a {@link Nested} class. Inside, two tests subscribe the library's collector (or,
 * for Vert.x and the JDK, a minimal hand-rolled subscriber) to each of the two publisher shapes and
 * assert that the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> payload arrives
 * intact — twelve {@link Byte} elements for {@link ReactiveHelloWorldBytePublisher} (which
 * completes naturally), and {@value #N} copies of the payload for the open-ended
 * {@link ReactiveHelloWorldArrayPublisher} (with a library-side {@code take(N)} / {@code limit(N)}
 * / cancel-after-{@value #N}).
 * <p>
 * This is the exact scenario that surfaced the {@code subscriber.onNext(...)}-under-publisher-lock
 * AB-BA deadlock against subscribers that re-enter the publisher while holding their own monitor or
 * lock — Mutiny's {@code AssertSubscriber} ({@code synchronized(this)} around both {@code request}
 * and {@code onItem}) and Helidon's {@code Multi.collectList()} (internal lock after a
 * {@code Long.MAX_VALUE} request). With the publishers now signalling outside their internal lock,
 * every library × every publisher finishes within {@value #TIMEOUT_SECONDS} seconds.
 * <p>
 * The {@link HelloWorld} service is mocked once per test method by this class's constructor —
 * JUnit's default {@code PER_METHOD} test-instance lifecycle gives every test a fresh mock without
 * a {@code @BeforeEach} hook.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldBytePublisher
 * @see ReactiveHelloWorldArrayPublisher
 */
@DisplayName("ReactiveHelloWorld publishers / conformance")
class ReactiveHelloWorldPublishers_Conformance_Test {

    /**
     * Maximum time in seconds to wait for a library to collect the payload.
     */
    private static final long TIMEOUT_SECONDS = 10L;

    /**
     * Maximum time to wait for a library to collect the payload.
     */
    private static final Duration TIMEOUT = Duration.ofSeconds(TIMEOUT_SECONDS);

    /**
     * Number of items taken from the open-ended {@code ofArrays} / {@code ofStrings} streams.
     */
    private static final int N = 3;

    // ---------------------------------------------------------------------------------------------
    private static void assertBytes(final List<Byte> bytes) {
        assertEquals(HelloWorld.BYTES, bytes.size());
        final var expected = HelloWorld__TestUtils.hello_world_byte_array();
        for (var i = 0; i < expected.length; i++) {
            assertEquals(expected[i], bytes.get(i));
        }
    }

    private static void assertArrays(final List<byte[]> arrays) {
        assertEquals(N, arrays.size());
        final var expected = HelloWorld__TestUtils.hello_world_byte_array();
        for (final var array : arrays) {
            assertArrayEquals(expected, array);
        }
    }

    private static void assertStrings(final List<String> strings) {
        assertEquals(N, strings.size());
        final var expected = hello_world_string();
        for (final var string : strings) {
            assertEquals(expected, string);
        }
    }

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorldPublishers_Conformance_Test() {
        super();
        service = mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        set_array_sets_hello_world_bytes(service);
    }

    private final HelloWorld service;

    // ============================================================================================
    @DisplayName("Reactor")
    @Nested
    class Reactor_Test {

        /**
         * Verifies that Reactor's {@code Flux.from(ofBytes).collectList()} collects all twelve
         * bytes and completes.
         */
        @DisplayName("ofBytes")
        @Test
        void ofBytes__() {
            final var list = Flux.from(new ReactiveHelloWorldBytePublisher(service))
                    .collectList()
                    .block(TIMEOUT);
            assertBytes(list);
        }

        /**
         * Verifies that Reactor's {@code Flux.from(ofArrays).take(N).collectList()} collects
         * {@value #N} arrays.
         */
        @DisplayName("ofArrays")
        @Test
        void ofArrays__() {
            final var list = Flux.from(new ReactiveHelloWorldArrayPublisher(
                            new ReactiveHelloWorldBytePublisher(service)))
                    .take(N)
                    .collectList()
                    .block(TIMEOUT);
            assertArrays(list);
        }
    }

    // ============================================================================================
    @DisplayName("RxJava 3")
    @Nested
    class RxJava3_Test {

        /**
         * Verifies that RxJava 3's {@code Flowable.fromPublisher(ofBytes).toList()} collects all
         * twelve bytes.
         */
        @DisplayName("ofBytes")
        @Test
        void ofBytes__() {
            final var list = Flowable.fromPublisher(new ReactiveHelloWorldBytePublisher(service))
                    .toList()
                    .blockingGet();
            assertBytes(list);
        }

        /**
         * Verifies that RxJava 3's {@code Flowable.fromPublisher(ofArrays).take(N).toList()}
         * collects {@value #N} arrays.
         */
        @DisplayName("ofArrays")
        @Test
        void ofArrays__() {
            final var list = Flowable.fromPublisher(new ReactiveHelloWorldArrayPublisher(
                            new ReactiveHelloWorldBytePublisher(service)))
                    .take(N)
                    .toList()
                    .blockingGet();
            assertArrays(list);
        }
    }

    // ============================================================================================

    /**
     * Mutiny's {@code Multi.createFrom().publisher(...)} takes {@link Flow.Publisher}; bridge our
     * {@link Publisher} via {@link FlowAdapters#toFlowPublisher(Publisher)}.
     */
    @DisplayName("Mutiny")
    @Nested
    class Mutiny_Test {

        /**
         * Verifies that Mutiny's {@code Multi.createFrom().publisher(...).collect().asList()}
         * collects all twelve bytes.
         */
        @DisplayName("ofBytes")
        @Test
        void ofBytes__() {
            final var list = io.smallrye.mutiny.Multi.createFrom()
                    .publisher(FlowAdapters.toFlowPublisher(
                            new ReactiveHelloWorldBytePublisher(service)))
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            assertBytes(list);
        }

        /**
         * Verifies that Mutiny's {@code Multi.createFrom().publisher(...).select().first(N)}
         * collects {@value #N} arrays.
         */
        @DisplayName("ofArrays")
        @Test
        void ofArrays__() {
            final var list = io.smallrye.mutiny.Multi.createFrom()
                    .publisher(FlowAdapters.toFlowPublisher(
                            new ReactiveHelloWorldArrayPublisher(
                                    new ReactiveHelloWorldBytePublisher(service))))
                    .select().first(N)
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            assertArrays(list);
        }
    }

    // ============================================================================================

    /**
     * Helidon's {@link Multi} consumes {@link Flow.Publisher}; bridge our {@link Publisher} via
     * {@link FlowAdapters#toFlowPublisher(Publisher)}.
     */
    @DisplayName("Helidon")
    @Nested
    class Helidon_Test {

        /**
         * Verifies that Helidon's {@code Multi.create(Flow.Publisher).collectList()} collects all
         * twelve bytes.
         */
        @DisplayName("ofBytes")
        @Test
        void ofBytes__() {
            final var list = Multi.create(FlowAdapters.toFlowPublisher(
                            new ReactiveHelloWorldBytePublisher(service)))
                    .collectList()
                    .await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertBytes(list);
        }

        /**
         * Verifies that Helidon's {@code Multi.create(Flow.Publisher).limit(N).collectList()}
         * collects {@value #N} arrays.
         */
        @DisplayName("ofArrays")
        @Test
        void ofArrays__() {
            final var list = Multi.create(FlowAdapters.toFlowPublisher(
                            new ReactiveHelloWorldArrayPublisher(
                                    new ReactiveHelloWorldBytePublisher(service))))
                    .limit(N)
                    .collectList()
                    .await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertArrays(list);
        }
    }

    // ============================================================================================
    @DisplayName("Akka")
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Akka_Test {

        private akka.actor.ActorSystem system;

        /**
         * Creates the Akka {@link akka.actor.ActorSystem} shared by tests in this nested class.
         */
        @BeforeAll
        void startSystem() {
            system = akka.actor.ActorSystem.create("ReactiveHelloWorldPublishers_Conformance_Akka");
        }

        /**
         * Terminates the Akka {@link akka.actor.ActorSystem} after all tests.
         *
         * @throws Exception if the termination times out.
         */
        @AfterAll
        void stopSystem() throws Exception {
            system.terminate();
            system.getWhenTerminated()
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

        /**
         * Verifies that Akka's {@code Source.fromPublisher(ofBytes).runWith(Sink.seq())} collects
         * all twelve bytes.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("ofBytes")
        @Test
        void ofBytes__() throws Exception {
            final var list = akka.stream.javadsl.Source
                    .fromPublisher(new ReactiveHelloWorldBytePublisher(service))
                    .runWith(akka.stream.javadsl.Sink.<Byte>seq(), system)
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertBytes(list);
        }

        /**
         * Verifies that Akka's {@code Source.fromPublisher(ofArrays).take(N).runWith(Sink.seq())}
         * collects {@value #N} arrays.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("ofArrays")
        @Test
        void ofArrays__() throws Exception {
            final var list = akka.stream.javadsl.Source
                    .fromPublisher(new ReactiveHelloWorldArrayPublisher(
                            new ReactiveHelloWorldBytePublisher(service)))
                    .take(N)
                    .runWith(akka.stream.javadsl.Sink.<byte[]>seq(), system)
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertArrays(list);
        }
    }

    // ============================================================================================
    @DisplayName("Pekko")
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class Pekko_Test {

        private org.apache.pekko.actor.ActorSystem system;

        /**
         * Creates the Pekko {@link org.apache.pekko.actor.ActorSystem} shared by tests in this
         * nested class.
         */
        @BeforeAll
        void startSystem() {
            system = org.apache.pekko.actor.ActorSystem.create(
                    "ReactiveHelloWorldPublishers_Conformance_Pekko");
        }

        /**
         * Terminates the Pekko {@link org.apache.pekko.actor.ActorSystem} after all tests.
         *
         * @throws Exception if the termination times out.
         */
        @AfterAll
        void stopSystem() throws Exception {
            system.terminate();
            system.getWhenTerminated()
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

        /**
         * Verifies that Pekko's {@code Source.fromPublisher(ofBytes).runWith(Sink.seq())} collects
         * all twelve bytes.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("ofBytes")
        @Test
        void ofBytes__() throws Exception {
            final var list = org.apache.pekko.stream.javadsl.Source
                    .fromPublisher(new ReactiveHelloWorldBytePublisher(service))
                    .runWith(org.apache.pekko.stream.javadsl.Sink.<Byte>seq(), system)
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertBytes(list);
        }

        /**
         * Verifies that Pekko's {@code Source.fromPublisher(ofArrays).take(N).runWith(Sink.seq())}
         * collects {@value #N} arrays.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("ofArrays")
        @Test
        void ofArrays__() throws Exception {
            final var list = org.apache.pekko.stream.javadsl.Source
                    .fromPublisher(new ReactiveHelloWorldArrayPublisher(
                            new ReactiveHelloWorldBytePublisher(service)))
                    .take(N)
                    .runWith(org.apache.pekko.stream.javadsl.Sink.<byte[]>seq(), system)
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertArrays(list);
        }
    }

    // ============================================================================================

    /**
     * Vert.x exposes a {@link Publisher} as a Vert.x
     * {@link io.vertx.core.streams.ReadStream ReadStream} via {@link ReactiveReadStream}; this is
     * the only way to consume a Reactive Streams source through Vert.x's stream model.
     */
    @DisplayName("Vert.x")
    @Nested
    class Vertx_Test {

        private <T> List<T> collect(final Publisher<T> publisher, final int max) throws Exception {
            final var rs = ReactiveReadStream.<T>readStream();
            final var collected = new ArrayList<T>();
            final var done = new CompletableFuture<List<T>>();
            rs.exceptionHandler(done::completeExceptionally);
            rs.handler(item -> {
                if (done.isDone()) {
                    return;
                }
                collected.add(item);
                if (max > 0 && collected.size() >= max) {
                    done.complete(collected);
                    rs.pause();
                }
            });
            rs.endHandler(v -> done.complete(collected));
            publisher.subscribe(rs);
            return done.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

        /**
         * Verifies that Vert.x's {@link ReactiveReadStream} collects all twelve bytes from
         * {@code ofBytes} and reaches the end-of-stream callback.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("ofBytes")
        @Test
        void ofBytes__() throws Exception {
            assertBytes(collect(new ReactiveHelloWorldBytePublisher(service), 0));
        }

        /**
         * Verifies that Vert.x's {@link ReactiveReadStream} collects the first {@value #N} arrays
         * from {@code ofArrays}.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("ofArrays")
        @Test
        void ofArrays__() throws Exception {
            assertArrays(collect(new ReactiveHelloWorldArrayPublisher(
                    new ReactiveHelloWorldBytePublisher(service)), N));
        }
    }

    // ============================================================================================

    /**
     * The JDK side: bridge our {@link Publisher} to {@link Flow.Publisher} via
     * {@link FlowAdapters#toFlowPublisher(Publisher)} and drive it with a hand-rolled
     * {@link Flow.Subscriber}.
     */
    @DisplayName("JDK Flow")
    @Nested
    class Jdk_Test {

        private <T> List<T> collect(final Publisher<T> publisher, final int max) throws Exception {
            final var collected = new ArrayList<T>();
            final var done = new CompletableFuture<List<T>>();
            FlowAdapters.toFlowPublisher(publisher).subscribe(new Flow.Subscriber<>() {
                private Flow.Subscription subscription;

                @Override
                public void onSubscribe(final Flow.Subscription s) {
                    subscription = s;
                    s.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(final T item) {
                    if (done.isDone()) {
                        return;
                    }
                    collected.add(item);
                    if (max > 0 && collected.size() >= max) {
                        subscription.cancel();
                        done.complete(collected);
                    }
                }

                @Override
                public void onError(final Throwable t) {
                    done.completeExceptionally(t);
                }

                @Override
                public void onComplete() {
                    done.complete(collected);
                }
            });
            return done.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

        /**
         * Verifies that a hand-rolled {@link Flow.Subscriber} requesting {@link Long#MAX_VALUE}
         * collects all twelve bytes from {@code ofBytes} and receives {@code onComplete}.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("ofBytes")
        @Test
        void ofBytes__() throws Exception {
            assertBytes(collect(new ReactiveHelloWorldBytePublisher(service), 0));
        }

        /**
         * Verifies that a hand-rolled {@link Flow.Subscriber} that cancels after {@value #N} items
         * collects {@value #N} arrays from {@code ofArrays}.
         *
         * @throws Exception if an error occurs.
         */
        @DisplayName("ofArrays")
        @Test
        void ofArrays__() throws Exception {
            assertArrays(collect(new ReactiveHelloWorldArrayPublisher(
                    new ReactiveHelloWorldBytePublisher(service)), N));
        }
    }
}
