package com.github.jinahya.hello.api;

import io.smallrye.mutiny.Multi;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Demonstrates that the {@link org.reactivestreams.Publisher Publishers} produced by
 * {@link ReactiveHelloWorldPublishers} can be consumed by <strong>SmallRye Mutiny</strong> via
 * {@code Multi.createFrom().publisher(...)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Disabled
@DisplayName("ReactiveHelloWorldPublishers — Mutiny interop")
@Slf4j
class ReactiveHelloWorldPublishersMutinyTest {

    private static final byte[] ARRAY = "hello, world".getBytes(StandardCharsets.US_ASCII);

    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

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
    @DisplayName("ofBytes via Multi")
    class OfBytesTest {
        @Test
        @DisplayName("collect.asList() yields 12 bytes matching the payload")
        void collectAll() {
            final var list = Multi.createFrom().publisher(HelloWorldFlow.ofBytes(service))
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            Assertions.assertEquals(HelloWorld.BYTES, list.size());
            for (var i = 0; i < ARRAY.length; i++) {
                Assertions.assertEquals(ARRAY[i], list.get(i).byteValue());
            }
        }
    }

    @Nested
    @DisplayName("ofArrays via Multi")
    class OfArraysTest {
        @Test
        @DisplayName("select.first(3) yields three byte[] of the payload")
        void takeThree() {
            final var arrays = Multi.createFrom().publisher(HelloWorldFlow.ofArrays(service))
                    .select().first(3)
                    .collect().asList()
                    .await().atMost(TIMEOUT);
            Assertions.assertEquals(3, arrays.size());
            for (final var array : arrays) {
                Assertions.assertArrayEquals(ARRAY, array);
            }
        }
    }

    @Nested
    @DisplayName("ofStrings via Multi")
    class OfStringsTest {
        @Test
        @DisplayName("select.first().toUni() yields \"hello, world\"")
        void firstOne() {
            final var s = Multi.createFrom().publisher(HelloWorldFlow.ofStrings(service))
                    .select().first()
                    .toUni()
                    .await().atMost(TIMEOUT);
            Assertions.assertEquals("hello, world", s);
        }
    }
}
