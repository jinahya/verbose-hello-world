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
class HelloWorld_Array_FlowPublisher_Mutiny_Test
        extends HelloWorld__FlowPublisher__Test<Flow.Publisher<byte[]>, byte[]> {

    HelloWorld_Array_FlowPublisher_Mutiny_Test() {
        super(HelloWorldFlow::ofArrays);
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
        @DisplayName("toUni() → first array (\"hello, world\")")
        void __toUni_firstArray() {
            final var array = Multi.createFrom().publisher(publisher())
                    .toUni()
                    .await().atMost(Duration.ofSeconds(10L));
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), array);
        }
    }

    @Nested
    class Multi_Test {

        @Test
        @DisplayName("request(n), n > 0 → at least n elements, no onComplete")
        void __random() {
            // ----------------------------------------------------------------------------------- given
            final var n = ThreadLocalRandom.current().nextInt(1, 8);
            // ------------------------------------------------------------------------------------ when
            final var subscriber = Multi.createFrom().publisher(publisher())
                    .subscribe().withSubscriber(AssertSubscriber.create(n));
            subscriber.awaitNextItems(n, Duration.ofSeconds(10L));
            // ------------------------------------------------------------------------------------ then
            final var expected = HelloWorldTestUtils.hello_world_byte_array();
            final var items = subscriber.getItems();
            Assertions.assertTrue(items.size() >= n);
            for (final var element : items) {
                Assertions.assertArrayEquals(expected, element);
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
                    try { subscriber.request(1L); } finally { lock.unlock(); }
                }
            }));
            canceller.set(Thread.ofVirtual().start(() -> {
                ReactiveHelloWorld__Publisher__TestUtils.sleep(1L, HelloWorld.BYTES);
                lock.lock();
                try { subscriber.cancel(); } finally { lock.unlock(); }
                terminated.set(true);
            }));
            canceller.get().join(Duration.ofSeconds(20L).toMillis());
            requester.get().join(Duration.ofSeconds(20L).toMillis());
            // ------------------------------------------------------------------------------------ then
            subscriber.assertNotTerminated();
        }
    }
}
