package com.github.jinahya.hello._05_util_concurrent;

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAccumulator;

/**
 * A class for testing {@link HelloWorld#subscribeForBytes(Flow.Subscriber)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("subscribeForBytes(Subscriber<Byte>)")
@Slf4j
class HelloWorld_01_SubscribeForBytes_Test extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        setArray_willReturnArray(a -> {
            if (a != null) {
                for (int i = 0; i < a.length; i++) {
                    a[i] = (byte) i;
                }
            }
            return a;
        });
    }

    /**
     * Verifies that
     * {@link HelloWorld#subscribeForBytes(Flow.Subscriber) subscribeForBytes(subscriber)} method
     * throws a {@code NullPointerException} when the {@code subscriber} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when subscriber argument is null""")
    @Test
    void _ThrowNullPointerException_SubscriberIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var subscriber = (Flow.Subscriber<Byte>) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.subscribeForBytes(subscriber)
        );
    }

    /**
     * Verifies that
     * {@link HelloWorld#subscribeForBytes(Flow.Subscriber) subscribeForBytes(subscriber)} method
     * throws a {@code NullPointerException} when the {@code subscriber} argument is {@code null}.
     */
    @DisplayName("""
            should invoke onNext+/onComplete
            """)
    @Test
    @SuppressWarnings({"unchecked"})
    void _OnNextOnComplete_() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        @SuppressWarnings({"unchecked"})
        final var subscriber = (Flow.Subscriber<Byte>) Mockito.mock(Flow.Subscriber.class);
        final var accumulator = new LongAccumulator((x, y) -> x - y, HelloWorld.BYTES);
        final var subscriptions = new Flow.Subscription[1];
        BDDMockito.willAnswer(i -> {
            subscriptions[0] = i.getArgument(0, Flow.Subscription.class);
            log.debug("subscription: {}", subscriptions[0]);
            final var n = ThreadLocalRandom.current().nextInt(accumulator.intValue() >> 1) + 1;
            accumulator.accumulate(n);
            log.debug("requesting {} item(s) in onSubscribe...", n);
            subscriptions[0].request(n);
            return null;
        }).given(subscriber).onSubscribe(ArgumentMatchers.any());
        BDDMockito.willAnswer(i -> {
            final var item = i.getArgument(0, Byte.class);
            log.debug("item: {}", String.format("0x%1$02x", item));
            if (ThreadLocalRandom.current().nextInt() % 3 == 0) {
                final var r = accumulator.get();
                if (r > 0) {
                    final var n = ThreadLocalRandom.current().nextLong(r) + 1;
                    accumulator.accumulate(n);
                    log.debug("requesting {} more item(s) in onNext...", n);
                    subscriptions[0].request(n);
                }
            }
            return null;
        }).given(subscriber).onNext(ArgumentMatchers.notNull());
        BDDMockito.willAnswer(i -> {
            log.debug("completed");
            return null;
        }).given(subscriber).onComplete();
        // ------------------------------------------------------------------------------------ when
        service.subscribeForBytes(subscriber);
        // ------------------------------------------------------------------------------------ when
        Mockito.verify(subscriber, Mockito.atMost(HelloWorld.BYTES)).onNext(Mockito.notNull());
        final var r = accumulator.get();
        if (r > 0L) {
            accumulator.accumulate(r);
            log.debug("requesting {} last item(s)", r);
            subscriptions[0].request(r);
        }
        Assertions.assertEquals(0, accumulator.get());
    }
}
