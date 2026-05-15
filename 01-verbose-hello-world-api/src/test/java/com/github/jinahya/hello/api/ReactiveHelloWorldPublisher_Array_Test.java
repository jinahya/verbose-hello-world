package com.github.jinahya.hello.api;

import com.github.jinahya.hello.api.ReactiveStreamTests.LoggingArraySubscriber;
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

/**
 * Subscription-level tests for {@link ReactiveHelloWorldArrayPublisher} — verifies the Reactive
 * Streams 1.0 contract (demand, completion, the single-terminal-signal rule (1.7), cancellation,
 * …) using a {@link Mockito#spy(Object) spied} {@link LoggingArraySubscriber} from
 * {@link ReactiveStreamTests}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ReactiveHelloWorldPublisher_Array_Test
        extends ReactiveHelloWorldPublisher__Test<ReactiveHelloWorldArrayPublisher, byte[]> {

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorldPublisher_Array_Test() {
        super(ReactiveHelloWorldArrayPublisher::from);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return ReactiveHelloWorldTestUtils.toSimplifiedString(super.toString());
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
        final var subscriber = Mockito.spy(new LoggingArraySubscriber() {
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
        final var elementCaptor = ArgumentCaptor.forClass(byte[].class);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        Assertions.assertArrayEquals(
                HelloWorldTestUtils.hello_world_byte_array(),
                elementCaptor.getValue()
        ); // @formatter:on
    }

    @Test
    @DisplayName("request(n), n > 0 → exactly n elements, no onComplete")
    void __random() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 8);
        final var subscriber = Mockito.spy(new LoggingArraySubscriber() {
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
        final var elementCaptor = ArgumentCaptor.forClass(byte[].class);
        inOrder.verify(subscriber, Mockito.times(n)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        final byte[] expected = HelloWorldTestUtils.hello_world_byte_array();
        final List<byte[]> elements = elementCaptor.getAllValues();
        Assertions.assertEquals(n, elements.size());
        for (final var element : elements) {
            Assertions.assertArrayEquals(expected, element);
        } // @formatter:on
    }
}
