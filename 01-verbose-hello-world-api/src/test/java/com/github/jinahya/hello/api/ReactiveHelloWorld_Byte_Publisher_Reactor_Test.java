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
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
class ReactiveHelloWorld_Byte_Publisher_Reactor_Test
        extends ReactiveHelloWorld__Publisher__Test<ReactiveHelloWorldBytePublisher, Byte> {

    ReactiveHelloWorld_Byte_Publisher_Reactor_Test() {
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
    @DisplayName("collectList() → 12 elements + onComplete")
    void __collectAll() {
        // ------------------------------------------------------------------------------ given/when
        final var list = Flux.from(publisher())
                .collectList()
                .block(Duration.ofSeconds(10L));
        // ------------------------------------------------------------------------------------ then
        Assertions.assertNotNull(list);
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, list.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], list.get(i).byteValue());
        }
    }

    @Test
    @DisplayName("take(n>12).collectList() → exactly 12 elements + onComplete")
    void __takeMoreThan12() {
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextLong(HelloWorld.BYTES + 1L, 1024L);
        // ------------------------------------------------------------------------------------ when
        final var list = Flux.from(publisher())
                .take(n)
                .collectList()
                .block(Duration.ofSeconds(10L));
        // ------------------------------------------------------------------------------------ then
        Assertions.assertNotNull(list);
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, list.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], list.get(i).byteValue());
        }
    }

    @Test
    @DisplayName("BaseSubscriber: request(12) → exactly 12 elements + onComplete")
    void __exactly12() throws InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var elements = new ArrayList<Byte>();
        final var terminated = new CountDownLatch(1);
        final var error = new AtomicReference<Throwable>();
        final var completed = new AtomicBoolean();
        final var subscriber = new BaseSubscriber<Byte>() {
            @Override public String toString() { return super.toString().substring(getClass().getPackageName().length() + 1); }
            @Override protected void hookOnSubscribe(final Subscription s) { s.request(HelloWorld.BYTES); }
            @Override protected void hookOnNext(final Byte e) { elements.add(e); }
            @Override protected void hookOnComplete() { completed.set(true); terminated.countDown(); }
            @Override protected void hookOnError(final Throwable t) { error.set(t); terminated.countDown(); }
        };
        // ------------------------------------------------------------------------------------ when
        Flux.from(publisher()).subscribe(subscriber);
        Assertions.assertTrue(terminated.await(10L, TimeUnit.SECONDS));
        // ------------------------------------------------------------------------------------ then
        Assertions.assertNull(error.get());
        Assertions.assertTrue(completed.get());
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, elements.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], elements.get(i).byteValue());
        }
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
        final var baseSubscriber = new BaseSubscriber<Byte>() {
            @Override public String toString() { return super.toString().substring(getClass().getPackageName().length() + 1); }
            @Override protected void hookOnSubscribe(final Subscription s) { /* manual */ }
            @Override protected void hookOnComplete() { completedOrErrored.set(true); }
            @Override protected void hookOnError(final Throwable t) { completedOrErrored.set(true); }
        };
        Flux.from(publisher()).subscribe(baseSubscriber);
        // ------------------------------------------------------------------------------------ when
        requester.set(Thread.ofVirtual().start(() -> {
            for (var i = 0; i < HelloWorld.BYTES; i++) {
                ReactiveHelloWorld__Publisher__TestUtils.sleep(Duration.ofSeconds(1L));
                if (terminated.get() && ThreadLocalRandom.current().nextBoolean()) break;
                lock.lock();
                try { baseSubscriber.request(1L); } finally { lock.unlock(); }
            }
        }));
        canceller.set(Thread.ofVirtual().start(() -> {
            ReactiveHelloWorld__Publisher__TestUtils.sleep(1L, HelloWorld.BYTES);
            lock.lock();
            try { baseSubscriber.cancel(); } finally { lock.unlock(); }
            terminated.set(true);
        }));
        canceller.get().join(Duration.ofSeconds(20L).toMillis());
        requester.get().join(Duration.ofSeconds(20L).toMillis());
        // ------------------------------------------------------------------------------------ then
        Assertions.assertFalse(completedOrErrored.get());
    }
}
