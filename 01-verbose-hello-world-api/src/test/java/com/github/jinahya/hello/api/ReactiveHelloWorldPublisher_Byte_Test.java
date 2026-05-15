package com.github.jinahya.hello.api;

import com.github.jinahya.hello.api.ReactiveStreamTests.LoggingByteSubscriber;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.api.ReactiveHelloWorldPublisher__TestUtils.sleep;

/**
 * Subscription-level tests for {@link ReactiveHelloWorldBytePublisher} — verifies the Reactive
 * Streams 1.0 contract (demand, completion, the single-terminal-signal rule (1.7), cancellation, …)
 * using a {@link Mockito#spy(Object) spied} {@link LoggingByteSubscriber} from
 * {@link ReactiveStreamTests}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ReactiveHelloWorldPublisher_Byte_Test
        extends ReactiveHelloWorldPublisher__Test<ReactiveHelloWorldBytePublisher, Byte> {

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorldPublisher_Byte_Test() {
        super(ReactiveHelloWorldBytePublisher::new);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + Integer.toHexString(hashCode());
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("request(12) → exactly 12 elements + onComplete")
    void __exactly12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var subscriber = Mockito.spy(new LoggingByteSubscriber() {
            @Override public void onSubscribe(final Subscription s) {
                super.onSubscribe(s);
                s.request(HelloWorld.BYTES);
            }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        Awaitility.await().atMost(Duration.ofSeconds(10L)).untilAsserted(
                () -> Mockito.verify(subscriber, Mockito.times(1)).onComplete()
        );
        // ------------------------------------------------------------------------------------ then
        final var inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onSubscribe(ArgumentMatchers.notNull());
        final var elementCaptor = ArgumentCaptor.forClass(Byte.class);
        inOrder.verify(subscriber, Mockito.times(HelloWorld.BYTES)).onNext(elementCaptor.capture());
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        final byte[] expected = HelloWorldTestUtils.hello_world_byte_array();
        final List<Byte> elements = elementCaptor.getAllValues();
        Assertions.assertEquals(expected.length, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            Assertions.assertEquals(expected[i], elements.get(i));
        } // @formatter:on
    }

    @Test
    @DisplayName("request(n), n ∈ [1, 12) → only n elements, no onComplete")
    void __randomLessThan12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, HelloWorld.BYTES);
        final var subscriber = Mockito.spy(new LoggingByteSubscriber() {
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
        final var elementCaptor = ArgumentCaptor.forClass(Byte.class);
        inOrder.verify(subscriber, Mockito.times(n)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        final byte[] expected = Arrays.copyOf(HelloWorldTestUtils.hello_world_byte_array(), n);
        final List<Byte> elements = elementCaptor.getAllValues();
        Assertions.assertEquals(expected.length, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            Assertions.assertEquals(expected[i], elements.get(i));
        } // @formatter:on
    }

    @Test
    @DisplayName("request(n), n > 12 → exactly 12 elements + onComplete")
    void __requestMoreThan12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextLong(HelloWorld.BYTES + 1L, 1024L);
        final var subscriber = Mockito.spy(new LoggingByteSubscriber() {
            @Override public void onSubscribe(final Subscription s) {
                super.onSubscribe(s);
                s.request(n);
            }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        Awaitility.await().atMost(Duration.ofSeconds(10L)).untilAsserted(
                () -> Mockito.verify(subscriber, Mockito.times(1)).onComplete()
        );
        // ------------------------------------------------------------------------------------ then
        final var inOrder = Mockito.inOrder(subscriber);
        inOrder.verify(subscriber, Mockito.times(1)).onSubscribe(ArgumentMatchers.notNull());
        final var elementCaptor = ArgumentCaptor.forClass(Byte.class);
        inOrder.verify(subscriber, Mockito.times(HelloWorld.BYTES)).onNext(elementCaptor.capture());
        inOrder.verify(subscriber, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        final byte[] expected = HelloWorldTestUtils.hello_world_byte_array();
        final List<Byte> elements = elementCaptor.getAllValues();
        Assertions.assertEquals(expected.length, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            Assertions.assertEquals(expected[i], elements.get(i));
        } // @formatter:on
    }

    @DisplayName("one thread request(1) with sleep, another thread cancel → no terminal signal")
    @Test
    @SuppressWarnings({
            "java:S2925" // "Thread.sleep" should not be used in tests
    })
    void __cancel() throws InterruptedException { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var lock = new ReentrantLock();
        final var terminated = new AtomicBoolean();
        final var requester = new AtomicReference<Thread>();
        final var canceller = new AtomicReference<Thread>();
        final var subscriber = Mockito.spy(new LoggingByteSubscriber() {
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
