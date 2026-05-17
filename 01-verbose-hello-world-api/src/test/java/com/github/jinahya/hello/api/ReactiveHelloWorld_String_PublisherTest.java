package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.api.HelloWorldBookTestUtils.loggingSpy;
import static com.github.jinahya.hello.api.HelloWorldTestUtils.hello_world_string;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Subscription-level tests for {@link ReactiveHelloWorldStringPublisher} — verifies the Reactive
 * Streams 1.0 contract (demand, completion, the single-terminal-signal rule (1.7), cancellation, …)
 * using a {@link Mockito#spy(Object) spied} {@link Subscriber} wrapped in a logging proxy via
 * {@link HelloWorldBookUtils#loggingProxy(Class, Object)}.
 * <p>
 * The constructor passes {@link ReactiveHelloWorldStringPublisher#from(HelloWorld)
 * ReactiveHelloWorldStringPublisher::from} to {@link ReactiveHelloWorld__PublisherTest super},
 * which builds the mock {@link HelloWorld} service and the logging-wrapped publisher. The mock
 * is stubbed by the inherited {@code @BeforeEach} hook in the base class — see
 * {@link ReactiveHelloWorld__PublisherTest#stubService()}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorld__PublisherTest
 * @see ReactiveHelloWorldStringPublisher
 */
@Slf4j
class ReactiveHelloWorld_String_PublisherTest
        extends ReactiveHelloWorld__PublisherTest<String> {

    private static final Duration TIMEOUT = Duration.ofSeconds(10L);

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorld_String_PublisherTest() {
        super(ReactiveHelloWorldStringPublisher::from);
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("request(1) → 1 element, no onComplete")
    void __exactly1() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingSpy(new Subscriber<String>() {
            @Override public void onSubscribe(final Subscription s) { s.request(1L); }
            @Override public void onNext(final String item) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(1)).onNext(any()));
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(String.class);
        inOrder.verify(subscriber, times(1)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        assertEquals(hello_world_string(), elementCaptor.getValue()); // @formatter:on
    }

    @Test
    @DisplayName("request(n), n > 0 → n elements, no onComplete")
    void __random() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 8);
        final var subscriber = loggingSpy(new Subscriber<String>() {
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final String item) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(n)).onNext(any()));
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(String.class);
        inOrder.verify(subscriber, times(n)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        final var expected = hello_world_string();
        final var elements = elementCaptor.getAllValues();
        assertEquals(n, elements.size());
        for (final var element : elements) {
            assertEquals(expected, element);
        } // @formatter:on
    }
}
