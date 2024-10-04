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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S3577" // Test classes should comply with a naming convention
})
class _Flow_LocalDate_Example_Test {

    private static class LocalDateSubscription implements Flow.Subscription {

        private LocalDateSubscription(final Flow.Subscriber<? super LocalDate> subscriber) {
            super();
            this.subscriber = Objects.requireNonNull(subscriber, "subscriber is null");
            accumulated = new LongAdder();
            lock = new ReentrantLock();
            condition = lock.newCondition();
            Thread.ofPlatform().start(() -> {
                LocalDate date = null;
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        lock.lock();
                        while (!cancelled && accumulated.sum() == 0L) { // mind the spurious wakeup
                            try {
                                condition.await();
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        }
                        if (cancelled) {
                            log.debug("cancelled; interrupting self...");
                            Thread.currentThread().interrupt();
                            continue;
                        }
                        while (accumulated.sum() > 0) {
                            if (date == null) {
                                date = LocalDate.now();
                            } else {
                                date = date.plusDays(1L); // will it ever grow?
                            }
                            subscriber.onNext(date);
                            accumulated.decrement();
                        }
                    } finally {
                        lock.unlock();
                    }
                }
                log.debug("out of while loop");
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
                log.debug("cancelled, already; returns...");
                return;
            }
            try {
                lock.lock();
                accumulated.add(n);
                condition.signal();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void cancel() {
            log.debug("{}.cancel()", this);
            if (cancelled) {
                return;
            }
            try {
                lock.lock();
                cancelled = true;
                condition.signal();
            } finally {
                lock.unlock();
            }
        }

        private final Flow.Subscriber<? super LocalDate> subscriber;

        private final LongAdder accumulated;

        private volatile boolean cancelled;

        private final Lock lock;

        private final Condition condition;
    }

    private static class LocalDatePublisher implements Flow.Publisher<LocalDate> {

        private static class InstanceHolder {

            private static final LocalDatePublisher INSTANCE = new LocalDatePublisher();

            private InstanceHolder() {
                throw new AssertionError("instantiation is not allowed");
            }
        }

        static LocalDatePublisher getInstance() {
            return InstanceHolder.INSTANCE;
        }

        private LocalDatePublisher() {
            super();
        }

        @Override
        public String toString() {
            return JavaLangObjectUtils.toSimpleString(this);
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super LocalDate> subscriber) {
            log.debug("{}.subscribe({})", this, subscriber);
            Objects.requireNonNull(subscriber, "subscriber is null");
            final var subscription = new LocalDateSubscription(subscriber);
            subscriber.onSubscribe(subscription);
        }
    }

    private static class LocalDateSubscriber implements Flow.Subscriber<LocalDate> {

        private LocalDateSubscriber(final AtomicReference<Flow.Subscription> reference) {
            super();
            this.reference = Objects.requireNonNull(reference, "reference is null");
        }

        @Override
        public String toString() {
            return JavaLangObjectUtils.toSimpleString(this);
        }

        @Override
        public void onSubscribe(final Flow.Subscription subscription) {
            log.debug("onSubscribe({}", subscription);
            reference.set(Mockito.spy(subscription));
        }

        @Override
        public void onNext(final LocalDate item) {
            log.debug("onNext({})", item);
        }

        @Override
        public void onError(final Throwable throwable) {
            log.debug("onError({})", throwable, throwable);
        }

        @Override
        public void onComplete() {
            log.debug("onComplete()");
        }

        private final AtomicReference<Flow.Subscription> reference;
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("single subscriber for a publisher")
    @Test
    void __() {
        final var reference = new AtomicReference<Flow.Subscription>();
        final var subscriber = Mockito.spy(new LocalDateSubscriber(reference) { // @formatter:off
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                super.onSubscribe(subscription);
                if (ThreadLocalRandom.current().nextBoolean()) {
                    reference.get().request(1L);
                }
            }
            @Override public void onNext(final LocalDate item) {
                super.onNext(item);
                if (ThreadLocalRandom.current().nextBoolean()) {
                    reference.get().request(1L);
                }
            } // @formatter:on
        });
        // subscribe
        LocalDatePublisher.getInstance().subscribe(subscriber);
        // verify, <subscriber.onSubscribe(...)> invoked, once
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(ArgumentMatchers.notNull());
        // subscription
        final var subscription = reference.get();
        assert subscription != null;
        // request a random number of items
        subscription.request(ThreadLocalRandom.current().nextLong(2, 8));
        // await, for 1 sec
        AwaitilityTestUtils.awaitForOneSecond();
        // cancel the <subscription>
        subscription.cancel();
        // request some after the cancellation
        subscription.request(1L);
    }
}