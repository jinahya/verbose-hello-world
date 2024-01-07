package com.github.jinahya.hello._05_util_concurrent;

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.util.HexFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.IntStream;

/**
 * A class for testing
 * {@link HelloWorld#publishBytes(Subscriber, ExecutorService) publishBytes(subscriber, executor)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("publishBytes(subscriber, executor)")
@Slf4j
class HelloWorld_01_PublishBytesSubscriberExecutor_Test extends _HelloWorldTest {

    @DisplayName("arguments")
    @Nested
    class ArgumentsTest {

        /**
         * Verifies that
         * {@link HelloWorld#publishBytes(Subscriber, ExecutorService) publishBytes(subscriber,
         * executor)} method throws a {@code NullPointerException} when the {@code subscriber}
         * argument is {@code null}.
         */
        @DisplayName("""
                should throw a NullPointerException
                when subscriber is null""")
        @Test
        void _ThrowNullPointerException_SubscriberIsNull() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var subscriber = (Subscriber<Byte>) null;
            final var executor = Mockito.mock(ExecutorService.class);
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    NullPointerException.class,
                    () -> service.publishBytes(subscriber, executor)
            );
        }

        /**
         * Verifies that
         * {@link HelloWorld#publishBytes(Subscriber, ExecutorService) publishBytes(subscriber,
         * executor)} method throws a {@code NullPointerException} when the {@code executor}
         * argument is {@code null}.
         */
        @DisplayName("""
                should throw a NullPointerException
                when executor is null""")
        @Test
        void _ThrowNullPointerException_ExecutorIsNull() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var subscriber = (Subscriber<Byte>) Mockito.mock(Subscriber.class);
            final var executor = (ExecutorService) null;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    NullPointerException.class,
                    () -> service.publishBytes(subscriber, executor)
            );
        }
    }

    @BeforeEach
    void beforeEach() {
        putBuffer_willReturnTheBuffer(b -> {
            if (b == null) {
                return;
            }
            IntStream.range(0, HelloWorld.BYTES).forEach(i -> b.put((byte) i));
        });
    }

    /**
     * Verifies that
     * {@link HelloWorld#publishArrays(Subscriber, ExecutorService) publishArrays(subscriber,
     * executor)} method invokes, on the {@code subscriber},
     * <ul>
     *   <li>{@link Subscriber#onSubscribe(Subscription) onSubscribe(subscription)}
     *       which invokes {@link Subscription#request(long) subscription.request(12)}</li>
     *   <li>{@link Subscriber#onNext(Object) subscriber.onNext(item)} 12 times</li>
     *   <li>{@link Subscriber#onComplete() subscriber.onComplete()}</li>
     * </ul> in order.
     */
    @DisplayName("""
            should invoke
            subscriber.onSubscribe(s -> s.request(n))
            subscriber.onNext(item){n}
            subscriber.onComplete()
            """)
    @Test
    void __Request12OnSubscribe() throws InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        @SuppressWarnings({"unchecked"})
        final var subscriber = (Subscriber<Byte>) Mockito.mock(Subscriber.class);
        BDDMockito.willAnswer(i -> {                                                          // <1>
            final var subscription = i.getArgument(0, Subscription.class);
            log.debug("onSubscribe({})", subscription);
            subscription.request(HelloWorld.BYTES);
            return null;
        }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
        final var counter = new AtomicInteger();
        BDDMockito.willAnswer(i -> {                                                          // <2>
            final var item = i.getArgument(0, Byte.class);
            log.debug("[{}] onNext({})", String.format("%1$02d", counter.getAndIncrement()),
                      String.format("%1$02x", item));
            return null;
        }).given(subscriber).onNext(ArgumentMatchers.notNull());
        final var completed = new CountDownLatch(1);
        BDDMockito.willAnswer(i -> {                                                          // <3>
            log.debug("onComplete()");
            completed.countDown();
            return null;
        }).given(subscriber).onComplete();
        final var executor = Executors.newSingleThreadExecutor(
                Thread.ofVirtual().name("publisher").factory()
        );
        // ------------------------------------------------------------------------------------ when
        service.publishBytes(subscriber, executor);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(Mockito.notNull());
        Assertions.assertTrue(completed.await(4L, TimeUnit.SECONDS));
        Mockito.verify(subscriber, Mockito.times(HelloWorld.BYTES))
                .onNext(ArgumentMatchers.notNull());
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    /**
     * Verifies that
     * {@link HelloWorld#publishBytes(Subscriber, ExecutorService) publishBytes(subscriber,
     * executor)} method throws a {@code NullPointerException} when the {@code subscriber} argument
     * is {@code null}.
     */
    @DisplayName("""
            should invoke onNext+/onComplete
            """)
    @Test
    @SuppressWarnings({"unchecked"})
    void __() throws InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        @SuppressWarnings({"unchecked"})
        final var subscriber = (Subscriber<Byte>) Mockito.mock(Subscriber.class);
        final var accumulator = new LongAccumulator((x, y) -> x - y, HelloWorld.BYTES);
        final var subscriptions = new Subscription[1];
        final var completed = new CountDownLatch(1);
        final boolean cancelRandomly = false;
        BDDMockito.willAnswer(i -> {
            final var subscription = i.getArgument(0, Subscription.class);
            log.debug("onSubscribe({})", subscription);
            subscriptions[0] = subscription;
            if (cancelRandomly && ThreadLocalRandom.current().nextBoolean()) {
                log.debug("randomly canceling the subscription...");
                subscriptions[0].cancel();
                // return null; // commented out, intentionally
            }
            final var n = ThreadLocalRandom.current().nextInt(accumulator.intValue() >> 2) + 1;
            accumulator.accumulate(n);
            log.debug("requesting {} item(s)...", n);
            subscriptions[0].request(n);
            return null;
        }).given(subscriber).onSubscribe(ArgumentMatchers.any());
        final var index = new AtomicInteger();
        BDDMockito.willAnswer(i -> {
            final var item = i.getArgument(0, Byte.class);
            assert item != null : "item supposed to be not null";
            log.debug("[{}] onNext({})", String.format("%1$02d", index.getAndIncrement()),
                      String.format("0x%1$02x", item));
            if (cancelRandomly && ThreadLocalRandom.current().nextBoolean()) {
                log.debug("randomly canceling the subscription...");
                subscriptions[0].cancel();
                // return null; // commented out, intentionally
            }
            if (ThreadLocalRandom.current().nextInt() % 3 == 0) {
                final var r = accumulator.get();
                if (r > 1) {
                    final var n = ThreadLocalRandom.current().nextLong(r - 1) + 1;
                    accumulator.accumulate(n);
                    log.debug("requesting {} more item(s)...", n);
                    subscriptions[0].request(n);
                }
            }
            return null;
        }).given(subscriber).onNext(ArgumentMatchers.any());
        BDDMockito.willAnswer(i -> {
            log.debug("onComplete()");
            completed.countDown();
            return null;
        }).given(subscriber).onComplete();
        final var executor = Executors.newSingleThreadExecutor();
        // ------------------------------------------------------------------------------------ when
        service.publishBytes(subscriber, executor);
        assert subscriptions[0] != null;
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(subscriber, Mockito.atMost(HelloWorld.BYTES))
                .onNext(ArgumentMatchers.notNull());
        if (ThreadLocalRandom.current().nextBoolean()) {
            final var r = accumulator.get();
            if (r > 0L) {
                accumulator.accumulate(r);
                log.debug("requesting {} last item(s)...", r);
                subscriptions[0].request(r);
            }
            Assertions.assertEquals(0L, accumulator.get());
            final var broken = completed.await(1L, TimeUnit.SECONDS);
            Assertions.assertTrue(broken, "should've been completed");
        }
        if (ThreadLocalRandom.current().nextBoolean()) {
            log.debug("canceling the subscription...");
            subscriptions[0].cancel();
            final long n = ThreadLocalRandom.current().nextInt(8) + 1L;
            log.debug("requesting {} more item(s), on the canceled subscription...", n);
            subscriptions[0].request(n);
        }
    }
}
