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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S3577" // class _Flow...
})
class _Flow_DayOfWeek_Example_Test {

    private static class DayOfWeekSubscription implements Flow.Subscription {

        /**
         * Creates a new instance for specified subscriber.
         *
         * @param subscriber the subscriber.
         */
        DayOfWeekSubscription(final Flow.Subscriber<? super DayOfWeek> subscriber) {
            super();
            this.subscriber = Objects.requireNonNull(subscriber, "subscriber is null");
            accumulated = new AtomicLong();
            Thread.startVirtualThread(() -> {
                final List<DayOfWeek> items = new LinkedList<>(Arrays.asList(DayOfWeek.values()));
                while (!cancelled) {
                    while (!cancelled && accumulated.get() == 0L) {
                        synchronized (accumulated) {
                            try {
                                accumulated.wait();
                            } catch (final InterruptedException ie) {
                                break;
                            }
                        }
                    }
                    if (cancelled) {
                        continue;
                    }
                    while (!cancelled && accumulated.getAndDecrement() > 0 && !items.isEmpty()) {
                        final var item = items.removeFirst();
                        subscriber.onNext(item);
                    }
                    if (cancelled) {
                        continue;
                    }
                    if (items.isEmpty()) {
                        cancelled = true;
                        subscriber.onComplete();
                    }
                }
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
                accumulated.notifyAll();
            }
        }

        @Override
        public void cancel() {
            log.debug("{}.cancel()", this);
            if (cancelled) {
                return;
            }
            cancelled = true;
            synchronized (accumulated) {
                accumulated.notifyAll();
            }
        }

        private final Flow.Subscriber<? super DayOfWeek> subscriber;

        private final AtomicLong accumulated;

        private volatile boolean cancelled;
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

        private DayOfWeekSubscriber(final AtomicReference<Flow.Subscription> reference) {
            super();
            this.reference = Objects.requireNonNull(reference, "reference is null");
        }

        @Override
        public String toString() {
            return JavaLangObjectUtils.toSimpleString(this);
        }

        @Override
        public void onSubscribe(final Flow.Subscription subscription) {
            log.debug("{}.onSubscribe({}", this, subscription);
            reference.set(subscription);
        }

        @Override
        public void onNext(final DayOfWeek item) {
            log.debug("{}.onNext({})", this,item);
        }

        @Override
        public void onError(final Throwable throwable) {
            log.debug("{}.onError({})", this, throwable, throwable);
        }

        @Override
        public void onComplete() {
            log.debug("{}.onComplete()", this);
        }

        private final AtomicReference<Flow.Subscription> reference;
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    void __() {
        final var reference = new AtomicReference<Flow.Subscription>();
        final var subscriber = new DayOfWeekSubscriber(reference) { // @formatter:off
            @Override public String toString() {
                return DayOfWeekSubscriber.class.getSimpleName() + '@' + super.toString();
            }
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                super.onSubscribe(subscription);
                if (ThreadLocalRandom.current().nextBoolean()) {
                    reference.get().request(1L);
                }
            }
            @Override public void onNext(final DayOfWeek item) {
                super.onNext(item);
                if (ThreadLocalRandom.current().nextBoolean()) {
                    reference.get().request(1L);
                }
            } // @formatter:on
        };
        // subscribe
        DayOfWeekPublisher.getInstance().subscribe(subscriber);
        // request a random number of items
        reference.get().request(ThreadLocalRandom.current().nextLong(1, 8));
        // await, for 1 sec
        AwaitilityTestUtils.awaitForOneSecond();
        // cancel the subscription
        reference.get().cancel();
        // request some after the cancellation
        reference.get().request(1L);
    }
}