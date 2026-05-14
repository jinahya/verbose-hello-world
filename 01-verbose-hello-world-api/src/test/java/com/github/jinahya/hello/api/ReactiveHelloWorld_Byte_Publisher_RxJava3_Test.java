package com.github.jinahya.hello.api;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
class ReactiveHelloWorld_Byte_Publisher_RxJava3_Test
        extends ReactiveHelloWorld__Publisher__Test<ReactiveHelloWorldBytePublisher, Byte> {

    ReactiveHelloWorld_Byte_Publisher_RxJava3_Test() {
        super(ReactiveHelloWorldBytePublisher::new);
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

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("toList().blockingGet() → 12 elements + onComplete")
    void __collectAll() {
        // ------------------------------------------------------------------------------ given/when
        final var list = Flowable.fromPublisher(publisher()).toList().blockingGet();
        // ------------------------------------------------------------------------------------ then
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, list.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], list.get(i).byteValue());
        }
    }

    @Test
    @DisplayName("take(n>12).toList() → exactly 12 elements + onComplete")
    void __takeMoreThan12() {
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextLong(HelloWorld.BYTES + 1L, 1024L);
        // ------------------------------------------------------------------------------------ when
        final var list = Flowable.fromPublisher(publisher()).take(n).toList().blockingGet();
        // ------------------------------------------------------------------------------------ then
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, list.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], list.get(i).byteValue());
        }
    }

    @Test
    @DisplayName("TestSubscriber: request(12) → exactly 12 elements + onComplete")
    void __exactly12() {
        // ------------------------------------------------------------------------------ given/when
        final var subscriber = new TestSubscriber<Byte>(0L);
        Flowable.fromPublisher(publisher()).subscribe(subscriber);
        subscriber.request(HelloWorld.BYTES);
        subscriber.awaitDone(10L, TimeUnit.SECONDS).assertComplete().assertNoErrors();
        // ------------------------------------------------------------------------------------ then
        final var values = subscriber.values();
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, values.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], values.get(i).byteValue());
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
        final var subscriber = new TestSubscriber<Byte>(0L);
        Flowable.fromPublisher(publisher()).subscribe(subscriber);
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
        subscriber.assertNotComplete().assertNoErrors();
    }
}
