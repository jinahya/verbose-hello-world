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
 * {@link HelloWorld#subscribeForArrays(Subscriber, ExecutorService) subscribeForArrays(subscriber,
 * executor)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("subscribeForArrays(subscriber, executor)")
@Slf4j
class HelloWorld_02_SubscribeForArrays_SubscriberWithExecutor_Test extends _HelloWorldTest {

    @DisplayName("arguments")
    @Nested
    class ArgumentsTest {

        /**
         * Verifies that
         * {@link HelloWorld#subscribeForArrays(Subscriber, ExecutorService)
         * subscribeForArrays(subscriber, executor)} method throws a {@code NullPointerException}
         * when the {@code subscriber} argument is {@code null}.
         */
        @DisplayName("""
                should throw a NullPointerException
                when [subscriber] is null""")
        @Test
        void _ThrowNullPointerException_SubscriberIsNull() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var subscriber = (Subscriber<byte[]>) null;
            final var executor = Mockito.mock(ExecutorService.class);
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    NullPointerException.class,
                    () -> service.subscribeForArrays(subscriber, executor)
            );
        }

        /**
         * Verifies that
         * {@link HelloWorld#subscribeForArrays(Subscriber, ExecutorService)
         * subscribeForArrays(subscriber, executor)} method throws a {@code NullPointerException}
         * when the {@code executor} argument is {@code null}.
         */
        @DisplayName("""
                should throw a NullPointerException
                when [executor] is null""")
        @Test
        void _ThrowNullPointerException_ExecutorIsNull() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            @SuppressWarnings({"unchecked"})
            final var subscriber = (Subscriber<byte[]>) Mockito.mock(Subscriber.class);
            final var executor = (ExecutorService) null;
            // --------------------------------------------------------------------------- when/then
            Assertions.assertThrows(
                    NullPointerException.class,
                    () -> service.subscribeForArrays(subscriber, executor)
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
     * {@link HelloWorld#subscribeForArrays(Subscriber, ExecutorService)
     * subscribeForArrays(subscriber, executor)} method invokes, on the {@code subscriber},
     * <ul>
     *   <li>{@link Subscriber#onSubscribe(Subscription) onSubscribe(subscription)}
     *       which invokes {@link Subscription#request(long) subscription.request(n)}</li>
     *   <li>{@link Subscriber#onNext(Object) subscriber.onNext(item)} <em>n</em> times</li>
     * </ul> in order.
     */
    @DisplayName("""
            should invoke
            subscriber.onSubscribe(s -> s.request(n))
            subscriber.onNext(item){n}
            """)
    @Test
    void __RequestOnSubscribe() throws InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        @SuppressWarnings({"unchecked"})
        final var subscriber = (Subscriber<byte[]>) Mockito.mock(Subscriber.class);
        final var subscription = new AtomicReference<Subscription>();
        final var n = ThreadLocalRandom.current().nextInt(8) + 1;
        BDDMockito.willAnswer(i -> {                                                          // <1>
            subscription.set(Mockito.spy(i.getArgument(0, Subscription.class)));
            log.debug("onSubscribe({})", subscription.get());
            log.debug("\t\\ request {} item(s)...", n);
            subscription.get().request(n);
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
        BDDMockito.willAnswer(i -> {                                                          // <3>
            final var t = i.getArgument(0, Throwable.class);
            log.debug("onError({})", t, t);
            assert onErrorLatch.getCount() > 0;
            onErrorLatch.countDown();
            return null;
        }).given(subscriber).onError(ArgumentMatchers.notNull());
        final var onCompleteLatch = new CountDownLatch(1);
        BDDMockito.willAnswer(i -> {                                                          // <4>
            log.debug("onComplete()");
            assert onCompleteLatch.getCount() > 0;
            onCompleteLatch.countDown();
            return null;
        }).given(subscriber).onComplete();
        final var executor = Executors.newSingleThreadExecutor(
                Thread.ofVirtual().name("array-publisher-", 0L).factory()
        );
        // ------------------------------------------------------------------------------------ when
        service.subscribeForArrays(subscriber, executor);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(ArgumentMatchers.notNull());
        Assertions.assertTrue(onNextLatch.await(4L, TimeUnit.SECONDS));
        Mockito.verify(subscriber, Mockito.times(n)).onNext(ArgumentMatchers.notNull());
    }
}
