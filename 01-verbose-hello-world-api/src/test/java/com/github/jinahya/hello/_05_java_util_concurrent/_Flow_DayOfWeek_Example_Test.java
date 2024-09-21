package com.github.jinahya.hello._05_java_util_concurrent;

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

import com.github.jinahya.hello.AwaitilityTestUtils;
import com.github.jinahya.hello.util.JavaLangObjectUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.time.DayOfWeek;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class _Flow_DayOfWeek_Example_Test {

    private static class DayOfWeekSubscription implements Flow.Subscription {

        private DayOfWeekSubscription(final Flow.Subscriber<? super DayOfWeek> subscriber) {
            super();
            this.subscriber = Objects.requireNonNull(subscriber, "subscriber is null");
            accumulated = new AtomicLong();
            thread = Thread.ofPlatform().start(() -> {
                final var items = new LinkedList<>(Arrays.asList(DayOfWeek.values()));
                while (!Thread.currentThread().isInterrupted()) {
                    while (accumulated.get() == 0L) { // mind the spurious wakeup
                        synchronized (accumulated) {
                            try {
                                accumulated.wait();
                            } catch (final InterruptedException ie) {
                                break;
                            }
                        }
                    }
                    while (accumulated.getAndDecrement() > 0 && !items.isEmpty()) {
                        subscriber.onNext(items.removeFirst());
                    }
                    if (items.isEmpty()) {
                        subscriber.onComplete();
                        Thread.currentThread().interrupt();
                    }
                }
                log.debug("out-of-while-loop");
            });
        }

        @Override
        public String toString() {
            return JavaLangObjectUtils.toSimpleString(this);
        }

        @Override
        public void request(final long n) {
            log.debug("{}.request({})", this, n);
            if (n <= 0) {
                cancelled = true;
                subscriber.onError(new IllegalArgumentException("n(" + n + ") is negative"));
            }
            if (cancelled) {
                return;
            }
            accumulated.addAndGet(n);
            synchronized (accumulated) {
                accumulated.notify();
            }
        }

        @Override
        public void cancel() {
            log.debug("{}.cancel()", this);
            if (cancelled) {
                return;
            }
            cancelled = true;
            thread.interrupt();
        }

        private final Flow.Subscriber<? super DayOfWeek> subscriber;

        private final AtomicLong accumulated; // accumulated <n>s from <request(n)>

        private volatile boolean cancelled;

        private final Thread thread;
    }

    private static class DayOfWeekPublisher implements Flow.Publisher<DayOfWeek> {

        private static class InstanceHolder {

            private static final DayOfWeekPublisher INSTANCE = new DayOfWeekPublisher();

            private InstanceHolder() {
                throw new AssertionError("instantiation is not allowed");
            }
        }

        static DayOfWeekPublisher getInstance() {
            return InstanceHolder.INSTANCE;
        }

        private DayOfWeekPublisher() {
            super();
        }

        @Override
        public String toString() {
            return JavaLangObjectUtils.toSimpleString(this);
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super DayOfWeek> subscriber) {
            log.debug("{}.subscribe({})", this, subscriber);
            Objects.requireNonNull(subscriber, "subscriber is null");
            final var subscription = new DayOfWeekSubscription(subscriber);
            subscriber.onSubscribe(subscription);
        }
    }

    private static class DayOfWeekSubscriber implements Flow.Subscriber<DayOfWeek> {

        private DayOfWeekSubscriber() {
            super();
        }

        @Override
        public String toString() {
            return JavaLangObjectUtils.toSimpleString(this);
        }

        @Override
        public void onSubscribe(final Flow.Subscription subscription) {
            log.debug("{}.onSubscribe({}", this, subscription);
            if (this.subscription != null) {
                this.subscription.cancel();
            }
            this.subscription = subscription;
        }

        @Override
        public void onNext(final DayOfWeek item) {
            log.debug("{}.onNext({})", this, item);
        }

        @Override
        public void onError(final Throwable throwable) {
            log.debug("{}.onError({})", this, throwable, throwable);
        }

        @Override
        public void onComplete() {
            log.debug("{}.onComplete()", this);
        }

        Flow.Subscription subscription;
    }

    Flow.Subscriber<DayOfWeek> newSubscriber() {
        return Mockito.spy(new DayOfWeekSubscriber() { // @formatter:off
            @Override public String toString() {
                return DayOfWeekSubscriber.class.getSimpleName() + '@' + super.toString();
            }
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                super.onSubscribe(subscription);
                if (false && ThreadLocalRandom.current().nextBoolean()) { // noinspection
                    subscription.request(1L);
                }
            }
            @Override public void onNext(final DayOfWeek item) {
                super.onNext(item);
                if (false && ThreadLocalRandom.current().nextBoolean()) { // noinspection
                    subscription.request(1L);
                }
            } // @formatter:on
        });
    }

    @DisplayName("single subscriber for a publisher")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var subscriber = newSubscriber();
        // intercept, <subscriber.onSubscribe(subscription)> to wrap the <subscription> as a spy
        BDDMockito.willAnswer(i -> {
            i.getRawArguments()[0] = Mockito.spy(i.getArgument(0, Flow.Subscription.class));
            return i.callRealMethod();
        }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
        // ------------------------------------------------------------------------------------ when
        // subscribe
        DayOfWeekPublisher.getInstance().subscribe(subscriber);
        // ------------------------------------------------------------------------------------ then
        final Flow.Subscription subscription;
        {
            final var captor = ArgumentCaptor.forClass(Flow.Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(captor.capture());
            subscription = captor.getValue();
        }
        // request a random number of items
        final var n = ThreadLocalRandom.current().nextInt(5, 14);
        subscription.request(n);
        // await, <subscriber.onNext(item)> invoked, at most <n> times
        Awaitility.await().untilAsserted(() -> {
            Mockito.verify(subscriber, Mockito.times(Math.min(n, 7)))
                    .onNext(ArgumentMatchers.notNull());
        });
        if (n >= DayOfWeek.values().length) {
            Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        }
        // cancel the <subscription>
        subscription.cancel();
        // request some after the cancellation
        subscription.request(1L);
    }

    @DisplayName("multiple subscribers for a publisher")
    @Test
    void __multipleSubscribers() {
        // ----------------------------------------------------------------------------------- given
        final var subscribers = IntStream.range(0, 8)
                .mapToObj(i -> new DayOfWeekSubscriber())
                .map(Mockito::spy)
                .peek(s -> {
                    // intercept, <subscriber.onSubscribe(subscription)> to wrap the <subscription> as a spy
                    BDDMockito.willAnswer(i -> {
                        i.getRawArguments()[0] = Mockito.spy(
                                i.getArgument(0, Flow.Subscription.class));
                        return i.callRealMethod();
                    }).given(s).onSubscribe(ArgumentMatchers.notNull());
                })
                .toList();
        // ------------------------------------------------------------------------------------ when
        // subscribe all <subscribers>
        subscribers.forEach(s -> {
            DayOfWeekPublisher.getInstance().subscribe(s);
        });
        // ------------------------------------------------------------------------------------ then
        // verify, <onSubscribe(subscription)> invoked on each <subscriber>
        final List<Flow.Subscription> subscriptions;
        {
            final var captor = ArgumentCaptor.forClass(Flow.Subscription.class);
            subscribers.forEach(s -> {
                Mockito.verify(s, Mockito.times(1)).onSubscribe(captor.capture());
            });
            subscriptions = captor.getAllValues();
        }
        assert subscriptions.size() == subscribers.size();
        assert Set.copyOf(subscriptions).size() == subscriptions.size();
        // request a random number of items on each <subscription>
        subscriptions.forEach(s -> {
            s.request(ThreadLocalRandom.current().nextLong(5L, 15L));
        });
        // await some
        AwaitilityTestUtils.awaitFor(Duration.ofSeconds(2L));
        // cancel each <subscription>
        subscriptions.forEach(s -> {
            // cancel the <subscription>
            s.cancel();
            // request some after the cancellation
            s.request(1L);
        });
    }
}
