package com.github.jinahya.hello._05_util_concurrent;

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import com.sun.tools.jconsole.JConsoleContext;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * A class for testing
 * {@link HelloWorld#publishArrays(Subscriber, ExecutorService) publishArrays(subscriber, executor)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("publishArrays(subscriber, executor)")
@Slf4j
class HelloWorld_02_PublishArraysSubscriberExecutor_Test extends _HelloWorldTest {

    @DisplayName("arguments")
    @Nested
    class ArgumentsTest {

        /**
         * Verifies that
         * {@link HelloWorld#publishArrays(Subscriber, ExecutorService) publishArrays(subscriber,
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
            final var subscriber = (Subscriber<byte[]>) null;
            final var executor = Mockito.mock(ExecutorService.class);
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    NullPointerException.class,
                    () -> service.publishArrays(subscriber, executor)
            );
        }

        /**
         * Verifies that
         * {@link HelloWorld#publishArrays(Subscriber, ExecutorService) publishArrays(subscriber,
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
            final var subscriber = (Subscriber<byte[]>) Mockito.mock(Subscriber.class);
            final var executor = (ExecutorService) null;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    NullPointerException.class,
                    () -> service.publishArrays(subscriber, executor)
            );
        }
    }

    @BeforeEach
    void beforeEach() {
        putBuffer_willReturnTheBuffer(b -> {
            if (b == null || !b.hasRemaining()) {
                return;
            }
            IntStream.range(0, b.remaining()).forEach(i -> b.put((byte) i));
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
    void __RequestOnSubscribe() throws InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        @SuppressWarnings({"unchecked"})
        final var subscriber = (Subscriber<byte[]>) Mockito.mock(Subscriber.class);
        final var subscription= new AtomicReference<Subscription>();
        final var n = ThreadLocalRandom.current().nextInt(8) + 1;
        BDDMockito.willAnswer(i -> {                                                          // <1>
            subscription.set(i.getArgument(0, Subscription.class));
            log.debug("onSubscribe({})", subscription.get());
            try {
                subscription.get().request(n);
            } catch (final Throwable t) {
                log.error("failed to request({})", n, t);
            }
            return null;
        }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
        final var onNextLatch = new CountDownLatch(n);
        BDDMockito.willAnswer(i -> {                                                          // <2>
            final var item = i.getArgument(0, byte[].class);
            log.debug("[{}] onNext({})", String.format("%1$02d", n - onNextLatch.getCount()),
                      HexFormat.of().formatHex(item));
            assert onNextLatch.getCount() > 0;
            onNextLatch.countDown();
            if (onNextLatch.getCount() == 0) {
                subscription.get().cancel();
            }
            return null;
        }).given(subscriber).onNext(ArgumentMatchers.notNull());
        final var onErrorLatch = new CountDownLatch(1);
        BDDMockito.willAnswer(i -> {                                                          // <4>
            final var t = i.getArgument(0, Throwable.class);
            log.debug("onError({})", t, t);
            assert onErrorLatch.getCount() > 0;
            onErrorLatch.countDown();
            return null;
        }).given(subscriber).onError(ArgumentMatchers.notNull());
        final var onCompleteLatch = new CountDownLatch(1);
        BDDMockito.willAnswer(i -> {                                                          // <3>
            log.debug("onComplete()");
            assert onCompleteLatch.getCount() > 0;
            onCompleteLatch.countDown();
            return null;
        }).given(subscriber).onComplete();
        final var executor = Executors.newSingleThreadExecutor(
                Thread.ofVirtual().name("arrays-publisher").factory()
        );
        // ------------------------------------------------------------------------------------ when
        service.publishArrays(subscriber, executor);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(ArgumentMatchers.notNull());
        Assertions.assertTrue(onNextLatch.await(60L, TimeUnit.SECONDS));
        Mockito.verify(subscriber, Mockito.times(n)).onNext(ArgumentMatchers.notNull());
//        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }
}
