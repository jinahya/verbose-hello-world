package com.github.jinahya.hello.api;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
class HelloWorld_Byte_FlowPublisher_Mutiny_Test
        extends HelloWorld__FlowPublisher__Test<Flow.Publisher<Byte>, Byte> {

    HelloWorld_Byte_FlowPublisher_Mutiny_Test() {
        super(HelloWorldFlow::ofBytes);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return super.toString().substring(getClass().getPackageName().length() + 1);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
    }

    @Nested
    class Uni_Test {

        @Test
        @DisplayName("toUni() → first byte ('h')")
        void __toUni_firstByte() {
            final var b = Multi.createFrom().publisher(publisher())
                    .toUni()
                    .await().atMost(Duration.ofSeconds(10L));
            Assertions.assertEquals((byte) 'h', b.byteValue());
        }

        @Test
        @DisplayName("collect().last() → last byte ('d')")
        void __collectLast_lastByte() {
            final var b = Multi.createFrom().publisher(publisher())
                    .collect().last()
                    .await().atMost(Duration.ofSeconds(10L));
            Assertions.assertEquals((byte) 'd', b.byteValue());
        }

        @Test
        @DisplayName("collect().asList() → 12-byte payload")
        void __collectAsList_fullPayload() {
            final var list = Multi.createFrom().publisher(publisher())
                    .collect().asList()
                    .await().atMost(Duration.ofSeconds(10L));
            final var expected = HelloWorldTestUtils.hello_world_byte_array();
            Assertions.assertEquals(expected.length, list.size());
            for (var i = 0; i < expected.length; i++) {
                Assertions.assertEquals(expected[i], list.get(i).byteValue());
            }
        }
    }

    @Nested
    class Multi_Test {

        // ---------------------------------------------------------------------------------------------
        @Test
        @DisplayName("request(12) → exactly 12 elements + onComplete")
        void __exactly12() {
            // ------------------------------------------------------------------------------ given/when
            final var subscriber = Multi
                    .createFrom().publisher(publisher())
                    .subscribe().withSubscriber(AssertSubscriber.create(HelloWorld.BYTES));
            subscriber.awaitCompletion(Duration.ofSeconds(10L));
            // ------------------------------------------------------------------------------------ then
            final var expected = HelloWorldTestUtils.hello_world_byte_array();
            final var items = subscriber.getItems();
            Assertions.assertEquals(expected.length, items.size());
            for (var i = 0; i < expected.length; i++) {
                Assertions.assertEquals(expected[i], items.get(i).byteValue());
            }
        }

        @Test
        @DisplayName("request(n), n > 12 → exactly 12 elements + onComplete")
        void __requestMoreThan12() {
            // ----------------------------------------------------------------------------------- given
            final var n = ThreadLocalRandom.current().nextLong(HelloWorld.BYTES + 1L, 1024L);
            // ------------------------------------------------------------------------------------ when
            final var subscriber = Multi
                    .createFrom().publisher(publisher())
                    .subscribe().withSubscriber(AssertSubscriber.create(n));
            subscriber.awaitCompletion(Duration.ofSeconds(10L));
            // ------------------------------------------------------------------------------------ then
            final var expected = HelloWorldTestUtils.hello_world_byte_array();
            final var items = subscriber.getItems();
            Assertions.assertEquals(expected.length, items.size());
            for (var i = 0; i < expected.length; i++) {
                Assertions.assertEquals(expected[i], items.get(i).byteValue());
            }
        }

        @Test
        @DisplayName("concurrent request(1) and cancel → no terminal signal")
        @SuppressWarnings({"java:S2925"})
        void __cancel() throws InterruptedException {
            // ----------------------------------------------------------------------------------- given
            final var lock = new ReentrantLock();
            final var terminated = new AtomicBoolean();
            final var requester = new AtomicReference<Thread>();
            final var canceller = new AtomicReference<Thread>();
            final var subscriber = Multi.createFrom().publisher(publisher())
                    .subscribe().withSubscriber(AssertSubscriber.create(0L));
            // ------------------------------------------------------------------------------------ when
            requester.set(Thread.ofVirtual().start(() -> {
                for (var i = 0; i < HelloWorld.BYTES; i++) {
                    ReactiveHelloWorld__Publisher__TestUtils.sleep(Duration.ofSeconds(1L));
                    if (terminated.get() && ThreadLocalRandom.current().nextBoolean()) break;
                    lock.lock();
                    try {
                        subscriber.request(1L);
                    } finally {
                        lock.unlock();
                    }
                }
            }));
            canceller.set(Thread.ofVirtual().start(() -> {
                ReactiveHelloWorld__Publisher__TestUtils.sleep(1L, HelloWorld.BYTES);
                lock.lock();
                try {
                    subscriber.cancel();
                } finally {
                    lock.unlock();
                }
                terminated.set(true);
            }));
            canceller.get().join(Duration.ofSeconds(20L).toMillis());
            requester.get().join(Duration.ofSeconds(20L).toMillis());
            // ------------------------------------------------------------------------------------ then
            subscriber.assertNotTerminated();
        }
    }
}
