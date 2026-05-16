package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.api.HelloWorldBookTestUtils.loggingSpy;
import static com.github.jinahya.hello.api.HelloWorldTestUtils.hello_world_byte_array;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Subscription-level tests for {@link ReactiveHelloWorldArrayPublisher} — verifies the Reactive
 * Streams 1.0 contract (demand, completion, the single-terminal-signal rule (1.7), cancellation, …)
 * using a {@link Mockito#spy(Object) spied} {@link Subscriber} wrapped in a logging proxy via
 * {@link HelloWorldBookUtils#loggingProxy(Class, Object)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ReactiveHelloWorldPublisher_Array_Test
        extends ReactiveHelloWorldPublisher__Test<byte[]> {

    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorldPublisher_Array_Test() {
        super(ReactiveHelloWorldArrayPublisher::from);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("request(1) → exactly 1 element, no onComplete")
    void __exactly1() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingSpy(new Subscriber<byte[]>() {
            @Override public void onSubscribe(final Subscription s) { s.request(1L); }
            @Override public void onNext(final byte[] item) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> verify(subscriber, times(1)).onNext(any()));
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(byte[].class);
        inOrder.verify(subscriber, times(1)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        assertArrayEquals(hello_world_byte_array(), elementCaptor.getValue()); // @formatter:on
    }

    @Test
    @DisplayName("request(n), n > 0 → exactly n elements, no onComplete")
    void __random() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 8);
        final var subscriber = loggingSpy(new Subscriber<byte[]>() {
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final byte[] item) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(n)).onNext(any()));
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(byte[].class);
        inOrder.verify(subscriber, times(n)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        final var elements = elementCaptor.getAllValues();
        assertEquals(n, elements.size());
        for (final var element : elements) {
            assertArrayEquals(hello_world_byte_array(), element);
        } // @formatter:on
    }
}
