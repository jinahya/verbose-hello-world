package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
class ReactiveHelloWorld_String_Publisher_Reactor_Test
        extends ReactiveHelloWorld__Publisher__Test<ReactiveHelloWorldStringPublisher, String> {

    ReactiveHelloWorld_String_Publisher_Reactor_Test() {
        super(ReactiveHelloWorldStringPublisher::from);
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
    @DisplayName("take(n).collectList() → exactly n elements")
    void __random() {
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 8);
        // ------------------------------------------------------------------------------------ when
        final var list = Flux.from(publisher())
                .take(n)
                .collectList()
                .block(Duration.ofSeconds(10L));
        // ------------------------------------------------------------------------------------ then
        Assertions.assertNotNull(list);
        Assertions.assertEquals(n, list.size());
        final var expected = HelloWorldTestUtils.hello_world_string();
        for (final var element : list) {
            Assertions.assertEquals(expected, element);
        }
    }

    @Test
    @DisplayName("BaseSubscriber: request(1) → exactly 1 element, no onComplete")
    @SuppressWarnings({"java:S2925"})
    void __exactly1() throws InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var count = new AtomicInteger();
        final var element = new AtomicReference<String>();
        final var completedOrErrored = new AtomicBoolean();
        final var subscriber = new BaseSubscriber<String>() {
            @Override public String toString() { return super.toString().substring(getClass().getPackageName().length() + 1); }
            @Override protected void hookOnSubscribe(final Subscription s) { s.request(1L); }
            @Override protected void hookOnNext(final String e) { element.set(e); count.incrementAndGet(); }
            @Override protected void hookOnComplete() { completedOrErrored.set(true); }
            @Override protected void hookOnError(final Throwable t) { completedOrErrored.set(true); }
        };
        // ------------------------------------------------------------------------------------ when
        Flux.from(publisher()).subscribe(subscriber);
        Thread.sleep(500L);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(1, count.get());
        Assertions.assertEquals(HelloWorldTestUtils.hello_world_string(), element.get());
        Assertions.assertFalse(completedOrErrored.get());
    }

    @Test
    @DisplayName("concurrent request(1) and cancel → no terminal signal")
    @SuppressWarnings({"java:S2925"})
    void __cancel() throws InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var lock = new ReentrantLock();
        final var terminated = new AtomicBoolean();
        final var completedOrErrored = new AtomicBoolean();
        final var requester = new AtomicReference<Thread>();
        final var canceller = new AtomicReference<Thread>();
        final var baseSubscriber = new BaseSubscriber<String>() {
            @Override public String toString() { return super.toString().substring(getClass().getPackageName().length() + 1); }
            @Override
            protected void hookOnSubscribe(final Subscription s) { /* manual */ }

            @Override
            protected void hookOnComplete() {
                completedOrErrored.set(true);
            }

            @Override
            protected void hookOnError(final Throwable t) {
                completedOrErrored.set(true);
            }
        };
        Flux.from(publisher()).subscribe(baseSubscriber);
        // ------------------------------------------------------------------------------------ when
        requester.set(Thread.ofVirtual().start(() -> {
            for (var i = 0; i < HelloWorld.BYTES; i++) {
                ReactiveHelloWorld__Publisher__TestUtils.sleep(Duration.ofSeconds(1L));
                if (terminated.get() && ThreadLocalRandom.current().nextBoolean()) break;
                lock.lock();
                try {
                    baseSubscriber.request(1L);
                } finally {
                    lock.unlock();
                }
            }
        }));
        canceller.set(Thread.ofVirtual().start(() -> {
            ReactiveHelloWorld__Publisher__TestUtils.sleep(1L, HelloWorld.BYTES);
            lock.lock();
            try {
                baseSubscriber.cancel();
            } finally {
                lock.unlock();
            }
            terminated.set(true);
        }));
        canceller.get().join(Duration.ofSeconds(20L).toMillis());
        requester.get().join(Duration.ofSeconds(20L).toMillis());
        // ------------------------------------------------------------------------------------ then
        Assertions.assertFalse(completedOrErrored.get());
    }
}
