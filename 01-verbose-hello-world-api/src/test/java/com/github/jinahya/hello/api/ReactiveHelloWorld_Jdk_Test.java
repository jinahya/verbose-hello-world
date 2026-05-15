package com.github.jinahya.hello.api;

import com.github.jinahya.hello.api.ReactiveStreamTests.LoggingSubmissionPublisher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

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
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class ReactiveHelloWorld_Jdk_Test extends ReactiveHelloWorld__Test {

    private static final long TIMEOUT = 10L;

    private static final Duration DURATION = Duration.ofSeconds(TIMEOUT);

    private static final int N = 3;

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
            return ReactiveHelloWorldTestUtils.toSimplifiedString(super.toString());
        }

        @Override
        public void onSubscribe(Flow.Subscription subscription) {
            subscription = FlowTests.LoggingFlowSubscription.from(subscription);
            log.debug("onSubscribe({}) / {}", subscription, this);
            subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(final T item) {
            final String formatted;
            if (item instanceof byte[] array) {
                formatted = ReactiveHelloWorldTestUtils.formatArray(array);
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
    @Test
    @DisplayName("HelloWorld → single byte[]")
    void __sync_single() throws Exception { // @formatter:off
        final var items = new ArrayList<byte[]>();
        final var done = new CompletableFuture<Void>();
        try (var publisher = new LoggingSubmissionPublisher<byte[]>()) {
            publisher.subscribe(new CollectingSubscriber<>(items, done));
            publisher.submit(HelloWorldUtils.array(synchronousService()));
            publisher.close();
            done.get(TIMEOUT, TimeUnit.SECONDS);
        }
        Assertions.assertEquals(1, items.size());
        assertPayload(items.get(0)); // @formatter:on
    }

    @Test
    @DisplayName("HelloWorld → N byte[]")
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
        Assertions.assertEquals(N, items.size());
        items.forEach(ReactiveHelloWorld_Jdk_Test::assertPayload); // @formatter:on
    }

    @Test
    @DisplayName("AsynchronousHelloWorld → single byte[]")
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
        Assertions.assertEquals(1, items.size());
        assertPayload(items.get(0)); // @formatter:on
    }

    @Test
    @DisplayName("AsynchronousHelloWorld → N byte[]")
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
        Assertions.assertEquals(N, items.size());
        items.forEach(ReactiveHelloWorld_Jdk_Test::assertPayload); // @formatter:on
    }
}
