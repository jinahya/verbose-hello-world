package com.github.jinahya.hello.api;

import io.reactivex.rxjava3.core.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

/**
 * Demonstrates that the {@link org.reactivestreams.Publisher Publishers} produced by
 * {@link ReactiveHelloWorldPublishers} can be consumed by <strong>RxJava 3</strong> via
 * {@link Flowable#fromPublisher(org.reactivestreams.Publisher)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Disabled
@DisplayName("ReactiveHelloWorldPublishers — RxJava 3 interop")
@Slf4j
class ReactiveHelloWorldPublishersRxJava3Test {

    private static final byte[] ARRAY = "hello, world".getBytes(StandardCharsets.US_ASCII);

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

    @Nested
    @DisplayName("ofBytes via Flowable")
    class OfBytesTest {
        @Test
        @DisplayName("toList() yields 12 bytes matching the payload")
        void collectAll() {
            final var list = Flowable.fromPublisher(ReactiveHelloWorldPublishers.ofBytes(service))
                    .toList()
                    .blockingGet();
            Assertions.assertEquals(HelloWorld.BYTES, list.size());
            for (var i = 0; i < ARRAY.length; i++) {
                Assertions.assertEquals(ARRAY[i], list.get(i).byteValue());
            }
        }
    }

    @Nested
    @DisplayName("ofArrays via Flowable")
    class OfArraysTest {
        @Test
        @DisplayName("take(3) yields three byte[] of the payload")
        void takeThree() {
            final var arrays = Flowable.fromPublisher(ReactiveHelloWorldPublishers.ofArrays(service))
                    .take(3)
                    .toList()
                    .blockingGet();
            Assertions.assertEquals(3, arrays.size());
            for (final var array : arrays) {
                Assertions.assertArrayEquals(ARRAY, array);
            }
        }
    }

    @Nested
    @DisplayName("ofStrings via Flowable")
    class OfStringsTest {
        @Test
        @DisplayName("blockingFirst() yields \"hello, world\"")
        void blockingFirst() {
            final var s = Flowable.fromPublisher(ReactiveHelloWorldPublishers.ofStrings(service))
                    .blockingFirst();
            Assertions.assertEquals("hello, world", s);
        }
    }
}
