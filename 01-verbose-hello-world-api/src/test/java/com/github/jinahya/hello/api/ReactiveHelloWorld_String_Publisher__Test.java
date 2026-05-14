package com.github.jinahya.hello.api;

import com.github.jinahya.hello.api.ReactiveHelloWorld__Publisher__Tests.LoggingStringSubscriber;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.api.HelloWorldTestUtils.hello_world_string;
import static com.github.jinahya.hello.api.ReactiveHelloWorld__Publisher__TestUtils.sleep;

@Slf4j
class ReactiveHelloWorld_String_Publisher__Test
        extends ReactiveHelloWorld__Publisher__Test<ReactiveHelloWorldStringPublisher, String> {

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorld_String_Publisher__Test() {
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

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("request(1) → exactly 1 element, no onComplete")
    void __exactly1() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var subscriber = Mockito.spy(new LoggingStringSubscriber() {
            @Override public void onSubscribe(final Subscription s) {
                super.onSubscribe(s);
                s.request(1L);
            }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        Awaitility.await().atMost(Duration.ofSeconds(10L)).untilAsserted(
                () -> Mockito.verify(subscriber, Mockito.times(1)).onNext(ArgumentMatchers.any())
        );
        // ------------------------------------------------------------------------------------ then
        final var inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onSubscribe(ArgumentMatchers.notNull());
        final var elementCaptor = ArgumentCaptor.forClass(String.class);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        Assertions.assertEquals(hello_world_string(), elementCaptor.getValue()); // @formatter:on
    }

    @Test
    @DisplayName("request(n), n > 0 → exactly n elements, no onComplete")
    void __random() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 8);
        final var subscriber = Mockito.spy(new LoggingStringSubscriber() {
            @Override public void onSubscribe(final Subscription s) {
                super.onSubscribe(s);
                s.request(n);
            }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        Awaitility.await().atMost(Duration.ofSeconds(10L)).untilAsserted(
                () -> Mockito.verify(subscriber, Mockito.times(n)).onNext(ArgumentMatchers.any())
        );
        // ------------------------------------------------------------------------------------ then
        final var inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onSubscribe(ArgumentMatchers.notNull());
        final var elementCaptor = ArgumentCaptor.forClass(String.class);
        inOrder.verify(subscriber, Mockito.times(n)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        final var expected = hello_world_string();
        final List<String> elements = elementCaptor.getAllValues();
        Assertions.assertEquals(n, elements.size());
        for (final var element : elements) {
            Assertions.assertEquals(expected, element);
        } // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("one thread request(1) with sleep, another thread cancel → no terminal signal")
    @Test
    @SuppressWarnings({
            "java:S2925" // "Thread.sleep" should not be used in tests
    })
    void __cancel() throws InterruptedException { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var lock = new ReentrantLock();        final var terminated = new AtomicBoolean();
        final var requester = new AtomicReference<Thread>();
        final var canceller = new AtomicReference<Thread>();
        final var subscriber = Mockito.spy(new LoggingStringSubscriber() {
            @Override public void onSubscribe(final Subscription s) {
                super.onSubscribe(s);
                requester.set(Thread.ofVirtual().start(() -> {
                    for (var i = 0; i < HelloWorld.BYTES; i++) {
                        sleep(Duration.ofSeconds(1L));
                        if (terminated.get() && ThreadLocalRandom.current().nextBoolean()) {
                            break;
                        }
                        lock.lock(); try { s.request(1L); } finally { lock.unlock(); }
                    }
                }));
                canceller.set(Thread.ofVirtual().start(() -> {
                    sleep(1L, HelloWorld.BYTES);
                    lock.lock(); try { s.cancel(); } finally { lock.unlock(); }
                    terminated.set(true);
                }));
            }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        canceller.get().join(Duration.ofSeconds(20L).toMillis());
        requester.get().join(Duration.ofSeconds(20L).toMillis());
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(ArgumentMatchers.notNull());
        Mockito.verify(subscriber, Mockito.atMost(HelloWorld.BYTES)).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onComplete(); // @formatter:on
    }
}
