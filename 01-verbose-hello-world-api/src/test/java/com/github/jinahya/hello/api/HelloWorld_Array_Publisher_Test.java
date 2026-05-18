package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Flow;
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
 * Subscription-level tests for {@link HelloWorldArrayPublisher} against the
 * {@link Flow.Publisher Flow.Publisher} contract.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Array_Publisher_Test extends HelloWorld__Publisher_Test<byte[]> {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // ---------------------------------------------------------------------------------------------
    HelloWorld_Array_Publisher_Test() {
        super(HelloWorldArrayPublisher::from);
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("request(n), n > 0 → at least n elements")
    void __singleRandom() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 10);
        log.debug("n: {}", n);
        final var subscriber = loggingSpy(new Flow.Subscriber<byte[]>() {
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                subscription.request(n);
            }
            @Override public void onNext(final byte[] item) { }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            p.subscribe(subscriber);
            await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(n)).onNext(any()));
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(byte[].class);
        inOrder.verify(subscriber, times(n)).onNext(elementCaptor.capture());
        final var expected = hello_world_byte_array();
        final var elements = elementCaptor.getAllValues();
        assertEquals(n, elements.size());
        for (final var element : elements) {
            assertArrayEquals(expected, element);
        } // @formatter:on
    }

    @Test
    @DisplayName("multiple subscribers, each request(n) → each gets its own n elements")
    void __multipleRandom() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var count = ThreadLocalRandom.current().nextInt(2, 5);
        final var demands = new int[count];
        final var subscribers = new ArrayList<Flow.Subscriber<byte[]>>(count);
        for (int i = 0; i < count; i++) {
            final var n = ThreadLocalRandom.current().nextInt(1, 10);
            demands[i] = n;
            subscribers.add(loggingSpy(new Flow.Subscriber<byte[]>() {
                @Override public void onSubscribe(final Flow.Subscription subscription) {
                    subscription.request(n);
                }
                @Override public void onNext(final byte[] item) { }
                @Override public void onError(final Throwable throwable) { }
                @Override public void onComplete() { }
            }));
        }
        log.debug("demands: {}", demands);
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            for (final var subscriber : subscribers) {
                p.subscribe(subscriber);
            }
            await().atMost(TIMEOUT).untilAsserted(() -> {
                for (int i = 0; i < count; i++) {
                    verify(subscribers.get(i), times(demands[i])).onNext(any());
                }
            });
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        final var expected = hello_world_byte_array();
        for (int i = 0; i < count; i++) {
            final var subscriber = subscribers.get(i);
            final var inOrder = inOrder(subscriber);
            inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
            final var elementCaptor = forClass(byte[].class);
            inOrder.verify(subscriber, times(demands[i])).onNext(elementCaptor.capture());
            final var elements = elementCaptor.getAllValues();
            assertEquals(demands[i], elements.size());
            for (final var element : elements) {
                assertArrayEquals(expected, element);
            }
        } // @formatter:on
    }
}
