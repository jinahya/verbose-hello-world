package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.api.HelloWorldBookTestUtils.loggingSpy;
import static com.github.jinahya.hello.api.HelloWorldTestUtils.hello_world_byte_array;
import static com.github.jinahya.hello.api.ReactiveHelloWorld__PublisherTestUtils.sleep;
import static java.util.Arrays.copyOf;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Subscription-level tests for {@link ReactiveHelloWorldBytePublisher} — verifies the Reactive
 * Streams 1.0 contract (demand, completion, the single-terminal-signal rule (1.7), cancellation, …)
 * using a {@link Mockito#spy(Object) spied} {@link Subscriber} wrapped in a logging proxy via
 * {@link HelloWorldBookUtils#loggingProxy(Class, Object)}.
 * <p>
 * The constructor passes
 * {@link ReactiveHelloWorldBytePublisher#ReactiveHelloWorldBytePublisher(HelloWorld) new
 * ReactiveHelloWorldBytePublisher(service)} (as a method reference) to
 * {@link ReactiveHelloWorld__PublisherTest super}, which builds the mock {@link HelloWorld} service
 * and the logging-wrapped publisher. The mock is stubbed by the inherited {@code @BeforeEach} hook
 * in the base class — see {@link ReactiveHelloWorld__PublisherTest#stubService()}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorld__PublisherTest
 * @see ReactiveHelloWorldBytePublisher
 */
@Slf4j
class ReactiveHelloWorld_Byte_PublisherTest
        extends ReactiveHelloWorld__PublisherTest<Byte> {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorld_Byte_PublisherTest() {
        super(ReactiveHelloWorldBytePublisher::new);
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("request(12) → 12 elements, onComplete")
    void __exactly12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingSpy(new Subscriber<Byte>() {
            @Override public void onSubscribe(final Subscription s) {
                s.request(HelloWorld.BYTES);
            }
            @Override public void onNext(final Byte b) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(1)).onComplete());
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(Byte.class);
        inOrder.verify(subscriber, times(HelloWorld.BYTES)).onNext(elementCaptor.capture());
        inOrder.verify(subscriber, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        final var expected = hello_world_byte_array();
        final var elements = elementCaptor.getAllValues();
        assertEquals(expected.length, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            assertEquals(expected[i], elements.get(i));
        } // @formatter:on
    }

    @Test
    @DisplayName("request(n), n ∈ [1, 12) → n elements, no onComplete")
    void __randomLessThan12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, HelloWorld.BYTES);
        final var subscriber = loggingSpy(new Subscriber<Byte>() {
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final Byte b) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(n)).onNext(any()));
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(Byte.class);
        inOrder.verify(subscriber, times(n)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        final var expected = copyOf(hello_world_byte_array(), n);
        final var elements = elementCaptor.getAllValues();
        assertEquals(expected.length, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            assertEquals(expected[i], elements.get(i));
        } // @formatter:on
    }

    @Test
    @DisplayName("request(n), n > 12 → 12 elements, onComplete")
    void __requestMoreThan12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextLong(HelloWorld.BYTES + 1L, 1024L);
        final var subscriber = loggingSpy(new Subscriber<Byte>() {
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final Byte b) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(1)).onComplete());
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(Byte.class);
        inOrder.verify(subscriber, times(HelloWorld.BYTES)).onNext(elementCaptor.capture());
        inOrder.verify(subscriber, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        final var expected = hello_world_byte_array();
        final var elements = elementCaptor.getAllValues();
        assertEquals(expected.length, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            assertEquals(expected[i], elements.get(i));
        } // @formatter:on
    }

    @DisplayName("request(1) repeatedly with concurrent cancel → no onError, no onComplete")
    @Test
    void __cancel() throws InterruptedException { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var lock = new ReentrantLock();
        final var terminated = new AtomicBoolean();
        final var requester = new AtomicReference<Thread>();
        final var canceller = new AtomicReference<Thread>();
        final var subscriber = loggingSpy(new Subscriber<Byte>() {
            @Override public void onSubscribe(final Subscription s) {
                requester.set(Thread.ofVirtual().start(() -> {
                    for (var i = 0; i < HelloWorld.BYTES; i++) {
                        sleep(Duration.ofSeconds(1L));
                        lock.lock();
                        try { if (terminated.get()) { break; } s.request(1L);
                        } finally { lock.unlock(); }
                    }
                }));
                canceller.set(Thread.ofVirtual().start(() -> {
                    sleep(3L, 6L);
                    lock.lock();
                    try { s.cancel(); terminated.set(true); } finally { lock.unlock(); }
                }));
            }
            @Override public void onNext(final Byte b) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        canceller.get().join(Duration.ofSeconds(20L).toMillis());
        requester.get().join(Duration.ofSeconds(20L).toMillis());
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, atMost(HelloWorld.BYTES)).onNext(any());
        verify(subscriber, never()).onError(any());
        verify(subscriber, never()).onComplete(); // @formatter:on
    }
}
