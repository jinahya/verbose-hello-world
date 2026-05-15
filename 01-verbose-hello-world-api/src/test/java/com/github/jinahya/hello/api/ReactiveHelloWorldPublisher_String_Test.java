package com.github.jinahya.hello.api;

import com.github.jinahya.hello.api.ReactiveStreamTests.LoggingStringSubscriber;
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

import static com.github.jinahya.hello.api.HelloWorldTestUtils.hello_world_string;

/**
 * Subscription-level tests for {@link ReactiveHelloWorldStringPublisher} — verifies the Reactive
 * Streams 1.0 contract (demand, completion, the single-terminal-signal rule (1.7), cancellation,
 * …) using a {@link Mockito#spy(Object) spied} {@link LoggingStringSubscriber} from
 * {@link ReactiveStreamTests}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ReactiveHelloWorldPublisher_String_Test
        extends ReactiveHelloWorldPublisher__Test<ReactiveHelloWorldStringPublisher, String> {

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorldPublisher_String_Test() {
        super(ReactiveHelloWorldStringPublisher::from);
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
}
