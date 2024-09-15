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

import com.github.jinahya.hello.util.JavaLangObjectUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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
class _Flow_LocalDate_Example_Test {

    private static class LocalDateSubscription implements Flow.Subscription {

        /**
         * Creates a new instance for specified subscriber.
         *
         * @param subscriber the subscriber.
         */
        LocalDateSubscription(final Flow.Subscriber<? super LocalDate> subscriber) {
            super();
            this.subscriber = Objects.requireNonNull(subscriber, "subscriber is null");
            this.accumulated = new AtomicLong();
            Thread.startVirtualThread(() -> {
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
                    while (!cancelled && accumulated.getAndDecrement() > 0) {
                        if (date == null) {
                            date = LocalDate.now();
                        } else {
                            date = date.plusDays(1L);
                        }
                        subscriber.onNext(date);
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
                log.debug("cancelled. returns...");
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

        private final Flow.Subscriber<? super LocalDate> subscriber;

        private final AtomicLong accumulated;

        private volatile boolean cancelled;

        private volatile LocalDate date;
    }

    private static class LocalDatePublisher implements Flow.Publisher<LocalDate> {

        private static class InstanceHolder {

            private static final _Flow_LocalDate_Example_Test.LocalDatePublisher INSTANCE
                    = new _Flow_LocalDate_Example_Test.LocalDatePublisher();

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
            reference.set(subscription);
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
    @Test
    void __() {
        final var reference = new AtomicReference<Flow.Subscription>();
        final var subscriber = new LocalDateSubscriber(reference) { // @formatter:off
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
        };
        LocalDatePublisher.getInstance().subscribe(subscriber);
        reference.get().request(ThreadLocalRandom.current().nextLong(1, 16));
        Awaitility.await()
                .timeout(2L, TimeUnit.SECONDS)
                .pollDelay(1L, TimeUnit.SECONDS)
                .untilAsserted(() -> Assertions.assertTrue(true));
        reference.get().cancel();
    }
}