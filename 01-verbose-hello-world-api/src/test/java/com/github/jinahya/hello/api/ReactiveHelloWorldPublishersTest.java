package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Test class for {@link ReactiveHelloWorldPublishers} — exercises the three publishers (Byte,
 * byte[], String) produced by the static factory.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Disabled
@DisplayName("ReactiveHelloWorldPublishers")
@Slf4j
class ReactiveHelloWorldPublishersTest {

    private static final String STRING = "hello, world";

    private static final byte[] ARRAY = STRING.getBytes(StandardCharsets.US_ASCII);

    private static final Duration AWAIT_TIMEOUT = Duration.ofSeconds(10L);

    // ---------------------------------------------------------------------------------------------

    /**
     * A reusable subscriber for testing. Collects items and tracks completion / error state with
     * {@code volatile} fields so it's safe to read from a test thread while the publisher's
     * producer thread fires signals.
     */
    static class SubscriberForTesting<E> implements Subscriber<E> {

        SubscriberForTesting(final long initialRequest) {
            this.initialRequest = initialRequest;
        }

        SubscriberForTesting() {
            this(0L);
        }

        @Override
        public void onSubscribe(final Subscription s) {
            this.subscription = s;
            if (initialRequest > 0L) {
                s.request(initialRequest);
            }
        }

        @Override
        public void onNext(final E item) {
            items.add(item);
        }

        @Override
        public void onError(final Throwable t) {
            error = t;
        }

        @Override
        public void onComplete() {
            completed = true;
        }

        void request(final long n) {
            subscription.request(n);
        }

        void cancel() {
            subscription.cancel();
        }

        private final long initialRequest;
        volatile Subscription subscription;
        final List<E> items = new ArrayList<>();
        volatile Throwable error;
        volatile boolean completed;
    }

    private static <E> void awaitCompletionOrError(final SubscriberForTesting<E> subscriber) {
        Awaitility.await()
                .atMost(AWAIT_TIMEOUT)
                .until(() -> subscriber.completed || subscriber.error != null);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * A minimal {@link HelloWorld} service that copies "hello, world" bytes into the supplied
     * array. Overrides both {@code set(byte[], int)} (abstract) and {@code set(byte[])} (default,
     * currently returns {@code null} in {@link HelloWorld}) so our publishers — which call the
     * one-arg form — actually get a populated array back.
     */
    private final HelloWorld service = new HelloWorld() {
        @Override
        public byte[] set(final byte[] array, final int index) {
            System.arraycopy(ARRAY, 0, array, index, HelloWorld.BYTES);
            return array;
        }

        @Override
        public byte[] set(final byte[] array) {
            return set(array, 0);
        }
    };

    // ---------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("ofBytes")
    class OfBytesTest {

        @Test
        @DisplayName("request all bytes — emits 12 Bytes in order, then onComplete")
        void requestAll() {
            final var publisher = ReactiveHelloWorldPublishers.ofBytes(service);
            final var subscriber = new SubscriberForTesting<Byte>(Long.MAX_VALUE);
            publisher.subscribe(subscriber);
            awaitCompletionOrError(subscriber);
            Assertions.assertTrue(subscriber.completed, "should complete");
            Assertions.assertNull(subscriber.error, "should not error");
            Assertions.assertEquals(HelloWorld.BYTES, subscriber.items.size());
            for (var i = 0; i < ARRAY.length; i++) {
                Assertions.assertEquals(ARRAY[i], subscriber.items.get(i).byteValue(),
                                        () -> "byte mismatch at index");
            }
        }

        @Test
        @DisplayName("request fewer than 12 — emits only what was requested, no onComplete")
        void requestPartial() throws InterruptedException {
            final var n = ThreadLocalRandom.current().nextInt(1, HelloWorld.BYTES);
            final var publisher = ReactiveHelloWorldPublishers.ofBytes(service);
            final var subscriber = new SubscriberForTesting<Byte>(n);
            publisher.subscribe(subscriber);
            Thread.sleep(100L);
            Assertions.assertFalse(subscriber.completed, "should not complete with partial demand");
            Assertions.assertNull(subscriber.error);
            Assertions.assertEquals(n, subscriber.items.size());
        }

        @Test
        @DisplayName("request(-1) — fires onError(IllegalArgumentException) [Rule 3.9]")
        void requestNegative() {
            final var publisher = ReactiveHelloWorldPublishers.ofBytes(service);
            final var subscriber = new SubscriberForTesting<Byte>();
            publisher.subscribe(subscriber);
            subscriber.request(-1L);
            awaitCompletionOrError(subscriber);
            Assertions.assertFalse(subscriber.completed, "should not complete");
            Assertions.assertInstanceOf(IllegalArgumentException.class, subscriber.error);
        }

        @Test
        @DisplayName("cancel — eventually stops emission [Rule 3.12]")
        void cancelStopsEmission() throws InterruptedException {
            final var publisher = ReactiveHelloWorldPublishers.ofBytes(service);
            final var subscriber = new SubscriberForTesting<Byte>(Long.MAX_VALUE);
            publisher.subscribe(subscriber);
            subscriber.cancel();
            Thread.sleep(100L);
            // cancel is "eventually" — some in-flight onNext may slip through (Rules 2.8 / 3.12)
            Assertions.assertFalse(subscriber.completed, "cancel must not deliver onComplete");
            Assertions.assertNull(subscriber.error, "cancel must not deliver onError");
        }

        @Test
        @DisplayName("two subscribers — each gets independent stream of 12 bytes")
        void twoSubscribers() {
            final var publisher = ReactiveHelloWorldPublishers.ofBytes(service);
            final var s1 = new SubscriberForTesting<Byte>(Long.MAX_VALUE);
            final var s2 = new SubscriberForTesting<Byte>(Long.MAX_VALUE);
            publisher.subscribe(s1);
            publisher.subscribe(s2);
            awaitCompletionOrError(s1);
            awaitCompletionOrError(s2);
            Assertions.assertEquals(HelloWorld.BYTES, s1.items.size());
            Assertions.assertEquals(HelloWorld.BYTES, s2.items.size());
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("ofArrays")
    class OfArraysTest {

        @Test
        @DisplayName("request n arrays — emits n byte[]s, each containing the full payload")
        void requestN() {
            final var n = ThreadLocalRandom.current().nextLong(1L, 6L);
            final var publisher = ReactiveHelloWorldPublishers.ofArrays(service);
            final var subscriber = new SubscriberForTesting<byte[]>(n);
            publisher.subscribe(subscriber);
            Awaitility.await()
                    .atMost(AWAIT_TIMEOUT)
                    .until(() -> subscriber.items.size() >= n || subscriber.error != null);
            Assertions.assertNull(subscriber.error);
            Assertions.assertTrue(subscriber.items.size() >= n,
                                  () -> "expected ≥" + n + " arrays, got " + subscriber.items.size());
            for (final var array : subscriber.items) {
                Assertions.assertArrayEquals(ARRAY, array);
            }
        }

        @Test
        @DisplayName("request(-1) — fires onError(IllegalArgumentException) [Rule 3.9]")
        void requestNegative() {
            final var publisher = ReactiveHelloWorldPublishers.ofArrays(service);
            final var subscriber = new SubscriberForTesting<byte[]>();
            publisher.subscribe(subscriber);
            subscriber.request(-1L);
            awaitCompletionOrError(subscriber);
            Assertions.assertInstanceOf(IllegalArgumentException.class, subscriber.error);
        }

        @Test
        @DisplayName("cancel — stops emission [Rule 3.12]")
        void cancelStopsEmission() throws InterruptedException {
            final var publisher = ReactiveHelloWorldPublishers.ofArrays(service);
            final var subscriber = new SubscriberForTesting<byte[]>(Long.MAX_VALUE);
            publisher.subscribe(subscriber);
            subscriber.cancel();
            Thread.sleep(100L);
            Assertions.assertFalse(subscriber.completed);
            Assertions.assertNull(subscriber.error);
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("ofStrings")
    class OfStringsTest {

        @Test
        @DisplayName("request n strings — emits n \"hello, world\" strings")
        void requestN() {
            final var n = ThreadLocalRandom.current().nextLong(1L, 6L);
            final var publisher = ReactiveHelloWorldPublishers.ofStrings(service);
            final var subscriber = new SubscriberForTesting<String>(n);
            publisher.subscribe(subscriber);
            Awaitility.await()
                    .atMost(AWAIT_TIMEOUT)
                    .until(() -> subscriber.items.size() >= n || subscriber.error != null);
            Assertions.assertNull(subscriber.error);
            Assertions.assertTrue(subscriber.items.size() >= n);
            for (final var string : subscriber.items) {
                Assertions.assertEquals(STRING, string);
            }
        }

        @Test
        @DisplayName("request(-1) — fires onError(IllegalArgumentException) [Rule 3.9]")
        void requestNegative() {
            final var publisher = ReactiveHelloWorldPublishers.ofStrings(service);
            final var subscriber = new SubscriberForTesting<String>();
            publisher.subscribe(subscriber);
            subscriber.request(-1L);
            awaitCompletionOrError(subscriber);
            Assertions.assertInstanceOf(IllegalArgumentException.class, subscriber.error);
        }

        @Test
        @DisplayName("cancel — stops emission [Rule 3.12]")
        void cancelStopsEmission() throws InterruptedException {
            final var publisher = ReactiveHelloWorldPublishers.ofStrings(service);
            final var subscriber = new SubscriberForTesting<String>(Long.MAX_VALUE);
            publisher.subscribe(subscriber);
            subscriber.cancel();
            Thread.sleep(100L);
            Assertions.assertFalse(subscriber.completed);
            Assertions.assertNull(subscriber.error);
        }
    }
}
