package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Flow;

import static com.github.jinahya.hello.api.HelloWorldBookTestUtils.loggingSpy;
import static com.github.jinahya.hello.api.HelloWorldTestUtils.hello_world_byte_array;
import static com.github.jinahya.hello.api.HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Subscription-level tests for {@link HelloWorldByteFlowPublisher} against the
 * {@link Flow.Publisher Flow.Publisher} contract.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Byte_Publisher_Test extends HelloWorld__Publisher_Test<Byte> {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // ---------------------------------------------------------------------------------------------
    HelloWorld_Byte_Publisher_Test() {
        super(HelloWorldByteFlowPublisher::new);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        set_array_sets_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("request(12) → exactly 12 elements + onComplete")
    void __exactly12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingSpy(new Flow.Subscriber<Byte>() {
            @Override public void onSubscribe(final Flow.Subscription s) {
                s.request(Long.MAX_VALUE);
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
}
