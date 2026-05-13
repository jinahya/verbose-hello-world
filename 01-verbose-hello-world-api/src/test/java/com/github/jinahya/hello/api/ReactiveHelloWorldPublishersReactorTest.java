package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;

/**
 * Demonstrates that the {@link org.reactivestreams.Publisher Publishers} produced by
 * {@link ReactiveHelloWorldPublishers} can be consumed by <strong>Project Reactor</strong> via
 * {@link Flux#from(org.reactivestreams.Publisher)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Disabled
@DisplayName("ReactiveHelloWorldPublishers — Project Reactor interop")
@Slf4j
class ReactiveHelloWorldPublishersReactorTest {

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
    @DisplayName("ofBytes via Flux")
    class OfBytesTest {
        @Test
        @DisplayName("collectList() yields 12 bytes matching the payload")
        void collectAll() {
            final var list = Flux.from(ReactiveHelloWorldPublishers.ofBytes(service))
                    .collectList()
                    .block();
            Assertions.assertNotNull(list);
            Assertions.assertEquals(HelloWorld.BYTES, list.size());
            for (var i = 0; i < ARRAY.length; i++) {
                Assertions.assertEquals(ARRAY[i], list.get(i).byteValue());
            }
        }
    }

    @Nested
    @DisplayName("ofArrays via Flux")
    class OfArraysTest {
        @Test
        @DisplayName("take(3) yields three byte[] of the payload")
        void takeThree() {
            final var arrays = Flux.from(ReactiveHelloWorldPublishers.ofArrays(service))
                    .take(3)
                    .collectList()
                    .block();
            Assertions.assertNotNull(arrays);
            Assertions.assertEquals(3, arrays.size());
            for (final var array : arrays) {
                Assertions.assertArrayEquals(ARRAY, array);
            }
        }
    }

    @Nested
    @DisplayName("ofStrings via Flux")
    class OfStringsTest {
        @Test
        @DisplayName("blockFirst() yields \"hello, world\"")
        void blockFirst() {
            final var s = Flux.from(ReactiveHelloWorldPublishers.ofStrings(service))
                    .blockFirst();
            Assertions.assertEquals("hello, world", s);
        }
    }
}
