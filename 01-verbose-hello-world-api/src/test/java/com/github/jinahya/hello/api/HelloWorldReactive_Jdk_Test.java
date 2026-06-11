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

import com.github.jinahya.hello.api.ReactiveStreamTests.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A pedagogical tour of the JDK's built-in {@link SubmissionPublisher SubmissionPublisher&lt;T&gt;}
 * (in {@code java.util.concurrent}, since Java 9) — the reference implementation of
 * {@link Flow.Publisher} — consumed by an explicit {@link Flow.Subscriber} implementation that uses
 * {@link Flow.Subscription} for back-pressure. Each test publishes the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> payload obtained from either
 * {@link #synchronousService() the synchronous service} or
 * {@link #asynchronousService() the asynchronous service}, and a hand-rolled
 * {@code Flow.Subscriber} appends each {@code onNext} item to a list.
 * <p>
 * No external reactive library is involved — this is the JDK-native counterpart to the
 * {@code ReactiveHelloWorld_<Library>_Test} tours.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("reactive — JDK")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldReactive_Jdk_Test extends HelloWorldReactive__Test {

    private static final long TIMEOUT = 10L;

    private static final int N = 3;

    // ---------------------------------------------------------------------------------------------

    private static void assertPayload(final byte[] array) {
        assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), array);
    }

    /**
     * A minimal {@link Flow.Subscriber} that, on
     * {@link Flow.Subscriber#onSubscribe(Flow.Subscription) onSubscribe}, signals unbounded demand
     * via {@link Flow.Subscription#request(long) Flow.Subscription.request(Long.MAX_VALUE)};
     * appends each {@code onNext} item to {@code sink}; and completes {@code done} on terminal
     * signal (or completes it exceptionally on {@code onError}).
     *
     * @param <T> the element type
     */
    private static final class CollectingSubscriber<T> implements Flow.Subscriber<T> {

        private CollectingSubscriber(final List<T> sink, final CompletableFuture<Void> done) {
            super();
            this.sink = Objects.requireNonNull(sink, "sink is null");
            this.done = Objects.requireNonNull(done, "done is null");
        }

        @Override
        public String toString() {
            return HelloWorldBookUtils.toSimplifiedString(super.toString());
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscription = HelloWorldBookUtils.loggingSubscription(subscription);
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(final T item) {
            final String formatted;
            if (item instanceof byte[] array) {
                formatted = HelloWorldBookUtils.formatArray(array);
            } else {
                formatted = String.valueOf(item);
            }
            log.debug("onNext({}) / {}", formatted, this);
            sink.add(item);
        }

        @Override
        public void onError(final Throwable t) {
            log.debug("onError({}) / {}", t, this);
            done.completeExceptionally(t);
        }

        @Override
        public void onComplete() {
            log.debug("onComplete() / {}", this);
            done.complete(null);
        }

        private final List<T> sink;

        private final CompletableFuture<Void> done;
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("""
            should emit a single <hello-world-bytes> from <HelloWorld>
            through a <SubmissionPublisher>""")
    @Test
    void __sync_single() throws Exception { // @formatter:off
        final var items = new ArrayList<byte[]>();
        final var done = new CompletableFuture<Void>();
        try (var publisher = new LoggingSubmissionPublisher<byte[]>()) {
            publisher.subscribe(new CollectingSubscriber<>(items, done));
            publisher.submit(HelloWorldUtils.array(synchronousService()));
            publisher.close();
            done.get(TIMEOUT, TimeUnit.SECONDS);
        }
        assertEquals(1, items.size());
        assertPayload(items.get(0)); // @formatter:on
    }

    @DisplayName("""
            should emit <N> copies of <hello-world-bytes> from <HelloWorld>
            through a <SubmissionPublisher>""")
    @Test
    void __sync_multiple() throws Exception { // @formatter:off
        final var items = new ArrayList<byte[]>();
        final var done = new CompletableFuture<Void>();
        try (var publisher = new SubmissionPublisher<byte[]>()) {
            publisher.subscribe(new CollectingSubscriber<>(items, done));
            for (var i = 0; i < N; i++) {
                publisher.submit(HelloWorldUtils.array(synchronousService()));
            }
            publisher.close();
            done.get(TIMEOUT, TimeUnit.SECONDS);
        }
        assertEquals(N, items.size());
        items.forEach(HelloWorldReactive_Jdk_Test::assertPayload); // @formatter:on
    }

    @DisplayName("""
            should emit a single <hello-world-bytes> from <AsynchronousHelloWorld>
            through a <SubmissionPublisher>""")
    @Test
    void __async_single() throws Exception { // @formatter:off
        final var items = new ArrayList<byte[]>();
        final var done = new CompletableFuture<Void>();
        try (var publisher = new SubmissionPublisher<byte[]>()) {
            publisher.subscribe(new CollectingSubscriber<>(items, done));
            asynchronousService().applyAsync(HelloWorldUtils::array)
                    .thenAccept(publisher::submit)
                    .toCompletableFuture()
                    .get(TIMEOUT, TimeUnit.SECONDS);
            publisher.close();
            done.get(TIMEOUT, TimeUnit.SECONDS);
        }
        assertEquals(1, items.size());
        assertPayload(items.get(0)); // @formatter:on
    }

    @DisplayName("""
            should emit <N> copies of <hello-world-bytes> from <AsynchronousHelloWorld>
            through a <SubmissionPublisher>""")
    @Test
    void __async_multiple() throws Exception { // @formatter:off
        final var items = new ArrayList<byte[]>();
        final var done = new CompletableFuture<Void>();
        try (var publisher = new SubmissionPublisher<byte[]>()) {
            publisher.subscribe(new CollectingSubscriber<>(items, done));
            final var futures = IntStream.range(0, N)
                    .mapToObj(i -> asynchronousService().applyAsync(HelloWorldUtils::array)
                            .thenAccept(publisher::submit)
                            .toCompletableFuture())
                    .toArray(CompletableFuture<?>[]::new);
            CompletableFuture.allOf(futures).get(TIMEOUT, TimeUnit.SECONDS);
            publisher.close();
            done.get(TIMEOUT, TimeUnit.SECONDS);
        }
        assertEquals(N, items.size());
        items.forEach(HelloWorldReactive_Jdk_Test::assertPayload); // @formatter:on
    }
}
