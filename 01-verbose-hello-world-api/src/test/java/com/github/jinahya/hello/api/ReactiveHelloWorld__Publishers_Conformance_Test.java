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
class ReactiveHelloWorldPublishers_Conformance_Test {

    private static final long TIMEOUT_SECONDS = 10L;

    private static final Duration TIMEOUT = Duration.ofSeconds(TIMEOUT_SECONDS);

    /**
     * Number of items taken from the open-ended {@code ofArrays} / {@code ofStrings} streams.
     */
    private static final int N = 3;

    // ---------------------------------------------------------------------------------------------
    private static void assertBytes(final List<Byte> bytes) {
        Assertions.assertEquals(HelloWorld.BYTES, bytes.size());
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], bytes.get(i));
        }
    }

    private static void assertArrays(final List<byte[]> arrays) {
        Assertions.assertEquals(N, arrays.size());
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        for (final var array : arrays) {
            Assertions.assertArrayEquals(expected, array);
        }
    }

    private static void assertStrings(final List<String> strings) {
        Assertions.assertEquals(N, strings.size());
        final var expected = HelloWorldTestUtils.hello_world_string();
        for (final var string : strings) {
            Assertions.assertEquals(expected, string);
        }
    }

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorldPublishers_Conformance_Test() {
        super();
        service = Mockito.mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service);
    }

    private final HelloWorld service;

    // ============================================================================================
    @Nested
    @DisplayName("Project Reactor")
    class Reactor_Test {

        @Test
        @DisplayName("ofBytes → Flux.from(pub).collectList() → 12 bytes + onComplete")
        void ofBytes__() {
            final var list = Flux.from(new ReactiveHelloWorldBytePublisher(service))
                    .collectList()
                    .block(TIMEOUT);
            assertBytes(list);
        }

        @Test
        @DisplayName("ofArrays → Flux.from(pub).take(N).collectList() → N byte[]")
        void ofArrays__() {
            final var list = Flux.from(ReactiveHelloWorldArrayPublisher.from(service))
                    .take(N)
                    .collectList()
                    .block(TIMEOUT);
            assertArrays(list);
        }
    }

    // ============================================================================================
    @Nested
    @DisplayName("RxJava 3")
    class RxJava3_Test {

        @Test
        @DisplayName("ofBytes → Flowable.fromPublisher(pub).toList() → 12 bytes")
        void ofBytes__() {
            final var list = Flowable.fromPublisher(new ReactiveHelloWorldBytePublisher(service))
                    .toList()
                    .blockingGet();
            assertBytes(list);
        }

        @Test
        @DisplayName("ofArrays → Flowable.fromPublisher(pub).take(N).toList() → N byte[]")
        void ofArrays__() {
            final var list = Flowable.fromPublisher(ReactiveHelloWorldArrayPublisher.from(service))
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
    @Nested
    @DisplayName("SmallRye Mutiny")
    class Mutiny_Test {

        @Test
        @DisplayName("ofBytes → Multi.createFrom().publisher(Flow.Publisher).collect().asList()")
        void ofBytes__() {
            final var list = io.smallrye.mutiny.Multi.createFrom()
                    .publisher(FlowAdapters.toFlowPublisher(
                            new ReactiveHelloWorldBytePublisher(service)))
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            assertBytes(list);
        }

        @Test
        @DisplayName("ofArrays → Multi.createFrom().publisher(...).select().first(N) → N byte[]")
        void ofArrays__() {
            final var list = io.smallrye.mutiny.Multi.createFrom()
                    .publisher(FlowAdapters.toFlowPublisher(
                            ReactiveHelloWorldArrayPublisher.from(service)))
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
    @Nested
    @DisplayName("Helidon Common Reactive")
    class Helidon_Test {

        @Test
        @DisplayName("ofBytes → Multi.create(Flow.Publisher).collectList() → 12 bytes")
        void ofBytes__() {
            final var list = Multi.create(FlowAdapters.toFlowPublisher(
                            new ReactiveHelloWorldBytePublisher(service)))
                    .collectList()
                    .await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertBytes(list);
        }

        @Test
        @DisplayName("ofArrays → Multi.create(Flow.Publisher).limit(N).collectList() → N byte[]")
        void ofArrays__() {
            final var list = Multi.create(FlowAdapters.toFlowPublisher(
                            ReactiveHelloWorldArrayPublisher.from(service)))
                    .limit(N)
                    .collectList()
                    .await(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertArrays(list);
        }
    }

    // ============================================================================================
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Akka Streams")
    class Akka_Test {

        private akka.actor.ActorSystem system;

        @BeforeAll
        void startSystem() {
            system = akka.actor.ActorSystem.create("ReactiveHelloWorldPublishers_Conformance_Akka");
        }

        @AfterAll
        void stopSystem() throws Exception {
            system.terminate();
            system.getWhenTerminated()
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("ofBytes → Source.fromPublisher(pub).runWith(Sink.seq()) → 12 bytes")
        void ofBytes__() throws Exception {
            final var list = akka.stream.javadsl.Source
                    .fromPublisher(new ReactiveHelloWorldBytePublisher(service))
                    .runWith(akka.stream.javadsl.Sink.<Byte>seq(), system)
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertBytes(list);
        }

        @Test
        @DisplayName("ofArrays → Source.fromPublisher(pub).take(N).runWith(Sink.seq()) → N byte[]")
        void ofArrays__() throws Exception {
            final var list = akka.stream.javadsl.Source
                    .fromPublisher(ReactiveHelloWorldArrayPublisher.from(service))
                    .take(N)
                    .runWith(akka.stream.javadsl.Sink.<byte[]>seq(), system)
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertArrays(list);
        }
    }

    // ============================================================================================
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Apache Pekko Streams")
    class Pekko_Test {

        private org.apache.pekko.actor.ActorSystem system;

        @BeforeAll
        void startSystem() {
            system = org.apache.pekko.actor.ActorSystem.create(
                    "ReactiveHelloWorldPublishers_Conformance_Pekko");
        }

        @AfterAll
        void stopSystem() throws Exception {
            system.terminate();
            system.getWhenTerminated()
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("ofBytes → Source.fromPublisher(pub).runWith(Sink.seq()) → 12 bytes")
        void ofBytes__() throws Exception {
            final var list = org.apache.pekko.stream.javadsl.Source
                    .fromPublisher(new ReactiveHelloWorldBytePublisher(service))
                    .runWith(org.apache.pekko.stream.javadsl.Sink.<Byte>seq(), system)
                    .toCompletableFuture()
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            assertBytes(list);
        }

        @Test
        @DisplayName("ofArrays → Source.fromPublisher(pub).take(N).runWith(Sink.seq()) → N byte[]")
        void ofArrays__() throws Exception {
            final var list = org.apache.pekko.stream.javadsl.Source
                    .fromPublisher(ReactiveHelloWorldArrayPublisher.from(service))
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
    @Nested
    @DisplayName("Vert.x (vertx-reactive-streams)")
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

        @Test
        @DisplayName("ofBytes → ReactiveReadStream → 12 bytes + end")
        void ofBytes__() throws Exception {
            assertBytes(collect(new ReactiveHelloWorldBytePublisher(service), 0));
        }

        @Test
        @DisplayName("ofArrays → ReactiveReadStream → first N byte[]")
        void ofArrays__() throws Exception {
            assertArrays(collect(ReactiveHelloWorldArrayPublisher.from(service), N));
        }
    }

    // ============================================================================================

    /**
     * The JDK side: bridge our {@link Publisher} to {@link Flow.Publisher} via
     * {@link FlowAdapters#toFlowPublisher(Publisher)} and drive it with a hand-rolled
     * {@link Flow.Subscriber}.
     */
    @Nested
    @DisplayName("JDK Flow (java.util.concurrent.Flow)")
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

        @Test
        @DisplayName("ofBytes → Flow.Subscriber requesting MAX_VALUE → 12 bytes + onComplete")
        void ofBytes__() throws Exception {
            assertBytes(collect(new ReactiveHelloWorldBytePublisher(service), 0));
        }

        @Test
        @DisplayName("ofArrays → Flow.Subscriber cancel-after-N → N byte[]")
        void ofArrays__() throws Exception {
            assertArrays(collect(ReactiveHelloWorldArrayPublisher.from(service), N));
        }
    }
}
