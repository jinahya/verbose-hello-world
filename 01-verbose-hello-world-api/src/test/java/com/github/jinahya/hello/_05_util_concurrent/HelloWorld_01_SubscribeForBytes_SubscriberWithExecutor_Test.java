package com.github.jinahya.hello._05_util_concurrent;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.stream.IntStream;

/**
 * A class for testing
 * {@link HelloWorld#subscribeForBytes(Subscriber, ExecutorService) subscribeForBytes(subscriber,
 * executor)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("subscribeForBytes(subscriber, executor)")
@Slf4j
class HelloWorld_01_SubscribeForBytes_SubscriberWithExecutor_Test extends _HelloWorldTest {

    @DisplayName("arguments")
    @Nested
    class ArgumentsTest {

        /**
         * Verifies that
         * {@link HelloWorld#subscribeForBytes(Subscriber, ExecutorService)
         * subscribeForBytes(subscriber, executor)} method throws a {@code NullPointerException}
         * when the {@code subscriber} argument is {@code null}.
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
                    () -> service.subscribeForBytes(subscriber, executor)
            );
        }

        /**
         * Verifies that
         * {@link HelloWorld#subscribeForBytes(Subscriber, ExecutorService)
         * subscribeForBytes(subscriber, executor)} method throws a {@code NullPointerException}
         * when the {@code executor} argument is {@code null}.
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
                    () -> service.subscribeForBytes(subscriber, executor)
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
     * {@link HelloWorld#subscribeForArrays(Subscriber, ExecutorService) publishArrays(subscriber,
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
            subscriber.onSubscribe(s -> s.request(BYTES))
            subscriber.onNext(item){BYTES}
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
        BDDMockito.willAnswer(i -> {                                                          // <2>
            final var item = i.getArgument(0, Byte.class);
            log.debug("onNext({})", String.format("%1$02x", item));
            return null;
        }).given(subscriber).onNext(ArgumentMatchers.notNull());
        final var onCompleteLatch = new CountDownLatch(1);
        BDDMockito.willAnswer(i -> {                                                          // <3>
            log.debug("onComplete()");
            onCompleteLatch.countDown();
            return null;
        }).given(subscriber).onComplete();
        final var executor = Executors.newSingleThreadExecutor(
                Thread.ofVirtual().name("byte-publisher-", 0L).factory()
        );
        // ------------------------------------------------------------------------------------ when
        service.subscribeForBytes(subscriber, executor);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(Mockito.notNull());
        Assertions.assertTrue(onCompleteLatch.await(1L, TimeUnit.SECONDS));
        Mockito.verify(subscriber, Mockito.times(HelloWorld.BYTES))
                .onNext(ArgumentMatchers.notNull());
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    /**
     * Verifies that
     * {@link HelloWorld#subscribeForBytes(Subscriber, ExecutorService)
     * subscribeForBytes(subscriber, executor)} method throws a {@code NullPointerException} when
     * the {@code subscriber} argument is {@code null}.
     */
    @DisplayName("""
            should invoke onSubscribe/onNext*/onComplete?
            """)
    @Test
    @SuppressWarnings({"unchecked"})
    void __Random() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        @SuppressWarnings({"unchecked"})
        final var subscriber = (Subscriber<Byte>) Mockito.mock(Subscriber.class);
        final var subscription = new AtomicReference<Subscription>();
        final var cancel = (BooleanSupplier) () -> ThreadLocalRandom.current().nextInt() % 23 == 0;
        BDDMockito.willAnswer(i -> {
            subscription.set(Mockito.spy(i.getArgument(0, Subscription.class)));
            log.debug("onSubscribe({})", subscription.get());
            if (cancel.getAsBoolean()) {
                log.debug("\t\\ randomly, cancelling the subscription...");
                subscription.get().cancel();
            }
            final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES) + 1;
            log.debug("\t\\ requesting {} item(s)...", n);
            subscription.get().request(n);
            return null;
        }).given(subscriber).onSubscribe(ArgumentMatchers.any());
        BDDMockito.willAnswer(i -> {
            final var item = i.getArgument(0, Byte.class);
            assert item != null : "item supposed to be not null";
            log.debug("onNext({})", String.format("0x%1$02x", item));
            if (cancel.getAsBoolean()) {
                log.debug("\t\\ randomly, cancelling the subscription...");
                subscription.get().cancel();
            }
            final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES) + 1;
            log.debug("\t\\ requesting {} item(s)...", n);
            subscription.get().request(n);
            return null;
        }).given(subscriber).onNext(ArgumentMatchers.any());
        final var onCompleteLatch = new CountDownLatch(1);
        BDDMockito.willAnswer(i -> {
            log.debug("onComplete()");
            onCompleteLatch.countDown();
            return null;
        }).given(subscriber).onComplete();
        final var executor = Executors.newSingleThreadExecutor(
                Thread.ofVirtual().name("byte-publisher-", 0L).factory()
        );
        // ------------------------------------------------------------------------------------ when
        service.subscribeForBytes(subscriber, executor);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(ArgumentMatchers.notNull());
        assert subscription.get() != null;
        {
            log.debug("explicitly, cancelling the subscription...");
            subscription.get().cancel();
        }
        {
            final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES) + 1;
            log.debug("requesting {} item(s)...", n);
            subscription.get().request(n);
        }
        final int s;
        {
            final var c = ArgumentCaptor.forClass(long.class);
            Mockito.verify(subscription.get(), Mockito.atLeast(0)).request(c.capture());
            s = c.getAllValues().stream().mapToInt(Math::toIntExact).sum();
        }
        Mockito.verify(subscriber, Mockito.atMost(s)).onNext(ArgumentMatchers.notNull());
        Mockito.verify(subscriber, Mockito.atMost(1)).onComplete();
    }
}
