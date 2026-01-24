package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

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

    ReactiveHelloWorldFactoryTest(final Class<T> factoryClass) {
        super();
        this.factoryClass = Objects.requireNonNull(factoryClass, "factoryClass is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("BytePublisher")
    class BytePublisherTest {

        @Test
        @DisplayName("Request all bytes and verify content")
        void request_all_bytes_and_verify_content() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var publisher = newBytePublisher();
            final var items = new ArrayList<Byte>();
            final var errorRef = new AtomicReference<Throwable>();
            final var completed = new AtomicBoolean(false);
            final var subscriber = new Subscriber<Byte>() {
                @Override
                public void onSubscribe(final Subscription s) {
                    s.request(Long.MAX_VALUE);
                }

                @Override
                public void onNext(final Byte t) {
                    items.add(t);
                }

                @Override
                public void onError(final Throwable t) {
                    errorRef.set(t);
                }

                @Override
                public void onComplete() {
                    completed.set(true);
                }
            };
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(subscriber);
            // -------------------------------------------------------------------------------- then
            AwaitilityTestUtils.awaitForOneSecond();
            Assertions.assertTrue(completed.get(), "should complete");
            Assertions.assertNull(errorRef.get(), "should not error");
            Assertions.assertEquals(
                    HelloWorld.BYTES,
                    items.size(),
                    "should emit all bytes"
            );
            final var expected = "hello, world".getBytes(StandardCharsets.US_ASCII);
            for (var i = 0; i < expected.length; i++) {
                Assertions.assertEquals(
                        expected[i],
                        items.get(i).byteValue(),
                        "byte[" + i + "] should match"
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
        void request_random_arrays_and_verify_content() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var n = ThreadLocalRandom.current().nextLong(1L, 6L); // random between 1 and 5
            final var publisher = newArrayPublisher();
            final var arrays = new ArrayList<byte[]>();
            final var completed = new AtomicBoolean(false);
            final var subscriber = new Subscriber<byte[]>() {
                @Override
                public void onSubscribe(final Subscription s) {
                    s.request(n);
                }

                @Override
                public void onNext(final byte[] t) {
                    arrays.add(t);
                }

                @Override
                public void onError(final Throwable t) {
                }

                @Override
                public void onComplete() {
                    completed.set(true);
                }
            };
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(subscriber);
            // -------------------------------------------------------------------------------- then
            AwaitilityTestUtils.awaitForOneSecond();
            Assertions.assertTrue(completed.get(), "should complete");
            Assertions.assertEquals(n, arrays.size(), "should emit " + n + " arrays");
            final var expected = "hello, world".getBytes(StandardCharsets.US_ASCII);
            for (final var array : arrays) {
                Assertions.assertNotNull(array);
                Assertions.assertEquals(HelloWorld.BYTES, array.length);
                Assertions.assertArrayEquals(expected, array);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    @Nested
    @DisplayName("StringPublisher")
    class StringPublisherTest {

        @Test
        @DisplayName("Request random number of strings and verify content")
        void request_random_strings_and_verify_content() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var n = ThreadLocalRandom.current().nextLong(1L, 6L); // random between 1 and 5
            final var publisher = newStringPublisher();
            final var strings = new ArrayList<String>();
            final var completed = new AtomicBoolean(false);
            final var subscriber = new Subscriber<String>() {
                @Override
                public void onSubscribe(final Subscription s) {
                    s.request(n);
                }

                @Override
                public void onNext(final String t) {
                    strings.add(t);
                }

                @Override
                public void onError(final Throwable t) {
                }

                @Override
                public void onComplete() {
                    completed.set(true);
                }
            };
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(subscriber);
            // -------------------------------------------------------------------------------- then
            AwaitilityTestUtils.awaitForOneSecond();
            Assertions.assertTrue(completed.get(), "should complete");
            Assertions.assertEquals(n, strings.size(), "should emit " + n + " strings");
            for (final var string : strings) {
                Assertions.assertEquals("hello, world", string);
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
        return newFactoryInstance().newBytePublisher();
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
