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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
class ReactiveHelloWorld_String_Publisher_RxJava3_Test
        extends ReactiveHelloWorld__Publisher__Test<ReactiveHelloWorldStringPublisher, String> {

    ReactiveHelloWorld_String_Publisher_RxJava3_Test() {
        super(service -> new ReactiveHelloWorldStringPublisher(
                new ReactiveHelloWorldArrayPublisher(
                        new ReactiveHelloWorldBytePublisher(service)
                )
        ));
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("take(n).toList() → exactly n elements")
    void __random() {
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 8);
        // ------------------------------------------------------------------------------------ when
        final var list = Flowable.fromPublisher(publisher()).take(n).toList().blockingGet();
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(n, list.size());
        final var expected = HelloWorldTestUtils.hello_world_string();
        for (final var element : list) {
            Assertions.assertEquals(expected, element);
        }
    }

    @Test
    @DisplayName("concurrent request(1) and cancel → no terminal signal")
    @SuppressWarnings({"java:S2925"})
    void __cancel() throws InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var lock = new ReentrantLock();              // Rule 2.7
        final var terminated = new AtomicBoolean();
        final var requester = new AtomicReference<Thread>();
        final var canceller = new AtomicReference<Thread>();
        final var subscriber = new TestSubscriber<String>(0L);
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
