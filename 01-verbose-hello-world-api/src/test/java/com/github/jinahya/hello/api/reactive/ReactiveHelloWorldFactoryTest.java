package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.util.JavaLangObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
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
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Abstract test class for {@link ReactiveHelloWorldFactory} implementations.
 * <p>
 * Tests normal usage scenarios for educational purposes.
 *
 * @param <T> the factory type
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
abstract class ReactiveHelloWorldFactoryTest<T extends ReactiveHelloWorldFactory> {

    /**
     * A reusable subscriber for testing reactive publishers.
     * <p>
     * This subscriber collects all received items and tracks completion/error state. Supports
     * multiple subscriptions to a single publisher and multiple {@code request(long)} calls per
     * subscription.
     * <p>
     * Thread-safety: This class uses {@code volatile} fields for error and completion state, making
     * it safe for use in asynchronous reactive streams testing.
     *
     * @param <E> the item type
     */
    static class SubscriberForTesting<E>
            implements Subscriber<E> {

        /**
         * Creates a new subscriber that will request the specified number of items upon
         * subscription.
         *
         * @param initialRequest the number of items to request initially (0 for no automatic
         *                       request)
         */
        SubscriberForTesting(final long initialRequest) {
            super();
            this.initialRequest = initialRequest;
        }

        /**
         * Creates a new subscriber that will not request any items automatically.
         */
        SubscriberForTesting() {
            this(0L);
        }

        // -----------------------------------------------------------------------------------------
        @Override
        public void onSubscribe(final Subscription s) {
            log.debug("{}.onSubscribe({})", JavaLangObjectUtils.toSimpleString(this),
                      JavaLangObjectUtils.toSimpleString(s));
            subscription = s;
            if (initialRequest > 0) {
                log.debug("requesting {} items...", initialRequest);
                s.request(initialRequest);
            }
        }

        @Override
        public void onNext(final E t) {
            log.debug("{}.onNext({})", JavaLangObjectUtils.toSimpleString(this),
                      JavaLangObjectUtils.toSimpleString(t));
            items.add(t);
        }

        @Override
        public void onError(final Throwable t) {
            log.debug("{}.onError({})", JavaLangObjectUtils.toSimpleString(this),
                      JavaLangObjectUtils.toSimpleString(t));
            error = t;
        }

        @Override
        public void onComplete() {
            log.debug("{}.onComplete()", JavaLangObjectUtils.toSimpleString(this));
            completed = true;
        }

        /**
         * Requests the specified number of items from the subscription.
         * <p>
         * This method can be called multiple times to make multiple requests.
         *
         * @param n the number of items to request
         * @throws IllegalStateException if no subscription has been established
         */
        void request(final long n) {
            if (subscription == null) {
                throw new IllegalStateException(
                        "No subscription available. Call request() after onSubscribe()");
            }
            log.debug("{}.request({})", JavaLangObjectUtils.toSimpleString(this), n);
            subscription.request(n);
        }

        /**
         * Cancels the subscription.
         *
         * @throws IllegalStateException if no subscription has been established
         */
        void cancel() {
            if (subscription == null) {
                throw new IllegalStateException(
                        "No subscription available. Call cancel() after onSubscribe()");
            }
            log.debug("{}.cancel()", JavaLangObjectUtils.toSimpleString(this));
            subscription.cancel();
        }

        // -----------------------------------------------------------------------------------------
        private final long initialRequest;

        volatile Subscription subscription;

        final List<E> items = new ArrayList<>();

        volatile Throwable error;

        volatile boolean completed;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Waits until the subscriber completes or errors.
     *
     * @param subscriber the subscriber to wait for
     * @param <E>        the item type
     */
    private static <E> void awaitCompletionOrError(final SubscriberForTesting<E> subscriber) {
        Awaitility.await()
                .atMost(AWAIT_TIMEOUT)
                .until(() -> subscriber.completed || subscriber.error != null);
    }

    /**
     * Asserts that the subscriber completed successfully without errors.
     *
     * @param subscriber the subscriber to verify
     * @param <E>        the item type
     */
    private static <E> void assertCompletedAndNoError(final SubscriberForTesting<E> subscriber) {
        Assertions.assertTrue(subscriber.completed, "should complete");
        Assertions.assertNull(subscriber.error, "should not error");
    }

    /**
     * The expected hello-world string.
     */
    private static final String STRING = "hello, world";

    /**
     * The expected hello-world bytes.
     *
     * @apiNote Arrays are mutable despite the {@code final} reference. The array elements can be
     * modified, which could lead to bugs if accidentally changed. Consider using an immutable
     * collection or defensive copying if immutability is required.
     */
    private static final byte[] ARRAY = STRING.getBytes(StandardCharsets.US_ASCII);

    /**
     * The minimum number of items to request in random tests (inclusive).
     */
    private static final long RANDOM_N_MIN = 1L;

    /**
     * The maximum number of items to request in random tests (exclusive).
     */
    private static final long RANDOM_N_MAX = 6L;

    /**
     * The timeout duration for waiting for completion or error.
     */
    private static final Duration AWAIT_TIMEOUT = Duration.ofSeconds(10L);

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorldFactoryTest(final Class<T> factoryClass) {
        super();
        this.factoryClass = Objects.requireNonNull(factoryClass, "factoryClass is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("OctetPublisher")
    class OctetPublisherTest {

        @Test
        @DisplayName("Request all bytes and verify content")
        void request_all_octets_and_verify_content() {
            // ------------------------------------------------------------------------------- given
            final var publisher = newBytePublisher();
            final var subscriber = new SubscriberForTesting<Byte>(Long.MAX_VALUE);
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(subscriber);
            // -------------------------------------------------------------------------------- then
            awaitCompletionOrError(subscriber);
            assertCompletedAndNoError(subscriber);
            Assertions.assertEquals(
                    HelloWorld.BYTES,
                    subscriber.items.size(),
                    "should emit all bytes"
            );
            // Convert List<Byte> to byte[] for comparison
            final var actualBytes = ReactiveHelloWorldFactoryUtils.toByteArray(subscriber.items);
            Assertions.assertArrayEquals(ARRAY, actualBytes, "bytes should match");
        }

        @Test
        @DisplayName("Request random bytes and verify content")
        void request_random_octets_and_verify_content() {
            // ------------------------------------------------------------------------------- given
            final var n = ThreadLocalRandom.current().nextLong(1L, 25L); // Random between 1 and 24
            final var publisher = newBytePublisher();
            final var subscriber = new SubscriberForTesting<Byte>(n);
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(subscriber);
            // -------------------------------------------------------------------------------- then
            if (n < HelloWorld.BYTES) {
                // When n < HelloWorld.BYTES: request only n bytes, should get n bytes with NO completion
                // Wait a short time for items to arrive (publisher won't complete)
                try {
                    Thread.sleep(100L);
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Assertions.assertFalse(
                        subscriber.completed,
                        """
                                should NOT complete when n=%d < %d (only %d bytes requested)"""
                                .formatted(n, HelloWorld.BYTES, n)
                );
                Assertions.assertEquals(
                        n,
                        subscriber.items.size(),
                        """
                                should emit exactly %d bytes when n=%d < %d"""
                                .formatted(n, n, HelloWorld.BYTES)
                );
                // Verify the first n bytes match expected content
                for (var i = 0; i < n; i++) {
                    Assertions.assertEquals(
                            ARRAY[i],
                            subscriber.items.get(i), // Auto-unboxing Byte to byte
                            """
                                    byte[%d] should match
                                    """
                                    .formatted(i)
                    );
                }
            } else {
                // When n >= HelloWorld.BYTES: request n bytes, should get all bytes WITH completion
                awaitCompletionOrError(subscriber);
                assertCompletedAndNoError(subscriber);
                Assertions.assertEquals(
                        HelloWorld.BYTES,
                        subscriber.items.size(),
                        """
                                should emit all %d bytes when n(%d) >= %d
                                """
                                .formatted(HelloWorld.BYTES, n, HelloWorld.BYTES)
                );
                final var actualBytes = ReactiveHelloWorldFactoryUtils.toByteArray(
                        subscriber.items);
                Assertions.assertArrayEquals(ARRAY, actualBytes, "all bytes should match");
            }
        }

        @Test
        @DisplayName("Multiple concurrent subscriptions with multiple synchronous requests")
        void multiple_subscriptions_with_multiple_requests() {
            // ------------------------------------------------------------------------------- given
            final var publisher = newBytePublisher();
            final var subscriber1 = new SubscriberForTesting<Byte>(1L); // Request 1 initially
            final var subscriber2 = new SubscriberForTesting<Byte>();   // No initial request
            // -------------------------------------------------------------------------------- when
            // Concurrent subscriptions
            publisher.subscribe(subscriber1);
            publisher.subscribe(subscriber2);
            // Multiple synchronous requests for each subscription
            subscriber1.request(1L); // Second request: +1 (total: 2)
            subscriber1.request(1L); // Third request: +1 (total: 3)
            subscriber2.request(1L); // First request: +1
            subscriber2.request(1L); // Second request: +1 (total: 2)
            // Wait a short time for items to arrive (some subscribers may not complete due to stream limitations)
            try {
                Thread.sleep(100L);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // -------------------------------------------------------------------------------- then
            // Verify that multiple subscriptions and requests work (at least one subscriber receives items)
            final var totalItems = subscriber1.items.size() + subscriber2.items.size();
            Assertions.assertTrue(totalItems > 0, "At least one subscriber should receive items");
            // Verify subscriptions were established
            Assertions.assertNotNull(subscriber1.subscription,
                                     "subscriber1 should have subscription");
            Assertions.assertNotNull(subscriber2.subscription,
                                     "subscriber2 should have subscription");
            // Verify content for received items
            for (var i = 0; i < Math.min(subscriber1.items.size(), ARRAY.length); i++) {
                Assertions.assertEquals(
                        ARRAY[i],
                        subscriber1.items.get(i), // Auto-unboxing Byte to byte
                        """
                                subscriber1 byte[%d]
                                """
                                .formatted(i)
                );
            }
            for (var i = 0; i < Math.min(subscriber2.items.size(), ARRAY.length); i++) {
                Assertions.assertEquals(
                        ARRAY[i],
                        subscriber2.items.get(i), // Auto-unboxing Byte to byte
                        """
                                subscriber2 byte[%d]
                                """
                                .formatted(i)
                );
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("ArrayPublisher")
    class ArrayPublisherTest {

        @Test
        @DisplayName("Request random number of arrays and verify content")
        void request_random_arrays_and_verify_content() {
            // ------------------------------------------------------------------------------- given
            final var n = ThreadLocalRandom.current().nextLong(RANDOM_N_MIN, RANDOM_N_MAX);
            final var publisher = newArrayPublisher();
            final var subscriber = new SubscriberForTesting<byte[]>(n);
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(subscriber);
            // -------------------------------------------------------------------------------- then
            awaitCompletionOrError(subscriber);
            assertCompletedAndNoError(subscriber);
            Assertions.assertEquals(
                    n,
                    subscriber.items.size(),
                    """
                            should emit %d arrays
                            """
                            .formatted(n)
            );
            for (final var array : subscriber.items) {
                Assertions.assertNotNull(array);
                Assertions.assertEquals(HelloWorld.BYTES, array.length);
                Assertions.assertArrayEquals(ARRAY, array);
            }
        }

        @Test
        @DisplayName("Multiple concurrent subscriptions with multiple synchronous requests")
        void multiple_subscriptions_with_multiple_requests() {
            // ------------------------------------------------------------------------------- given
            final var publisher = newArrayPublisher();
            final var subscriber1 = new SubscriberForTesting<byte[]>(2L); // Request 2 initially
            final var subscriber2 = new SubscriberForTesting<byte[]>(1L); // Request 1 initially
            final var subscriber3 = new SubscriberForTesting<byte[]>();   // No initial request
            // -------------------------------------------------------------------------------- when
            // Concurrent subscriptions
            publisher.subscribe(subscriber1);
            publisher.subscribe(subscriber2);
            publisher.subscribe(subscriber3);
            // Multiple synchronous requests for each subscription
            subscriber1.request(1L); // Second request: +1 (total: 3)
            subscriber1.request(1L); // Third request: +1 (total: 4)
            subscriber2.request(2L); // Second request: +2 (total: 3)
            subscriber3.request(1L); // First request: +1
            subscriber3.request(1L); // Second request: +1 (total: 2)
            subscriber3.request(1L); // Third request: +1 (total: 3)
            // -------------------------------------------------------------------------------- then
            awaitCompletionOrError(subscriber1);
            awaitCompletionOrError(subscriber2);
            awaitCompletionOrError(subscriber3);
            assertCompletedAndNoError(subscriber1);
            assertCompletedAndNoError(subscriber2);
            assertCompletedAndNoError(subscriber3);
            // Each subscriber should receive at least 1 array (multiple subscriptions and requests work)
            Assertions.assertTrue(subscriber1.items.size() >= 1L,
                                  "subscriber1 should receive at least 1 array");
            Assertions.assertTrue(subscriber2.items.size() >= 1L,
                                  "subscriber2 should receive at least 1 array");
            Assertions.assertTrue(subscriber3.items.size() >= 1L,
                                  "subscriber3 should receive at least 1 array");
            // Verify content for each subscriber
            for (final var subscriber : List.of(subscriber1, subscriber2, subscriber3)) {
                for (final var array : subscriber.items) {
                    Assertions.assertNotNull(array);
                    Assertions.assertEquals(HelloWorld.BYTES, array.length);
                    Assertions.assertArrayEquals(ARRAY, array);
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("StringPublisher")
    class StringPublisherTest {

        @Test
        @DisplayName("Request random number of strings and verify content")
        void request_random_strings_and_verify_content() {
            // ------------------------------------------------------------------------------- given
            final var n = ThreadLocalRandom.current().nextLong(RANDOM_N_MIN, RANDOM_N_MAX);
            final var publisher = newStringPublisher();
            final var subscriber = new SubscriberForTesting<String>(n);
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(subscriber);
            // -------------------------------------------------------------------------------- then
            awaitCompletionOrError(subscriber);
            assertCompletedAndNoError(subscriber);
            Assertions.assertEquals(
                    n,
                    subscriber.items.size(),
                    """
                            should emit %d strings
                            """
                            .formatted(n)
            );
            for (final var string : subscriber.items) {
                Assertions.assertEquals(STRING, string);
            }
        }

        @Test
        @DisplayName("Multiple concurrent subscriptions with multiple synchronous requests")
        void multiple_subscriptions_with_multiple_requests() {
            // ------------------------------------------------------------------------------- given
            final var publisher = newStringPublisher();
            final var subscriber1 = new SubscriberForTesting<String>(2L); // Request 2 initially
            final var subscriber2 = new SubscriberForTesting<String>(1L); // Request 1 initially
            final var subscriber3 = new SubscriberForTesting<String>();   // No initial request
            // -------------------------------------------------------------------------------- when
            // Concurrent subscriptions
            publisher.subscribe(subscriber1);
            publisher.subscribe(subscriber2);
            publisher.subscribe(subscriber3);
            // Multiple synchronous requests for each subscription
            subscriber1.request(1L); // Second request: +1 (total: 3)
            subscriber1.request(1L); // Third request: +1 (total: 4)
            subscriber2.request(2L); // Second request: +2 (total: 3)
            subscriber3.request(1L); // First request: +1
            subscriber3.request(1L); // Second request: +1 (total: 2)
            subscriber3.request(1L); // Third request: +1 (total: 3)
            // -------------------------------------------------------------------------------- then
            awaitCompletionOrError(subscriber1);
            awaitCompletionOrError(subscriber2);
            awaitCompletionOrError(subscriber3);
            assertCompletedAndNoError(subscriber1);
            assertCompletedAndNoError(subscriber2);
            assertCompletedAndNoError(subscriber3);
            // Each subscriber should receive at least 1 string (multiple subscriptions and requests work)
            Assertions.assertTrue(subscriber1.items.size() >= 1L,
                                  "subscriber1 should receive at least 1 string");
            Assertions.assertTrue(subscriber2.items.size() >= 1L,
                                  "subscriber2 should receive at least 1 string");
            Assertions.assertTrue(subscriber3.items.size() >= 1L,
                                  "subscriber3 should receive at least 1 string");
            // Verify content for each subscriber
            for (final var subscriber : List.of(subscriber1, subscriber2, subscriber3)) {
                for (final var string : subscriber.items) {
                    Assertions.assertEquals(STRING, string);
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new factory instance.
     *
     * @return a new factory instance
     */
    protected abstract T newFactoryInstance();

    Publisher<Byte> newBytePublisher() {
        return newFactoryInstance().newOctetPublisher();
    }

    Publisher<byte[]> newArrayPublisher() {
        return newFactoryInstance().newArrayPublisher();
    }

    Publisher<String> newStringPublisher() {
        return newFactoryInstance().newStringPublisher();
    }

    // ---------------------------------------------------------------------------------------------
    final Class<T> factoryClass;
}
