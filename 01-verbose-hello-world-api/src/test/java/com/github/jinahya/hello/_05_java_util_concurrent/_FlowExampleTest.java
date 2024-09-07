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

import com.github.jinahya.hello.HelloWorldTestUtils;
import com.github.jinahya.hello.util.JavaLangObjectUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S3577" // class _Flow...
})
class _FlowExampleTest {

    // @formatter:off
    private static class DayOfWeekPublisher implements Flow.Publisher<DayOfWeek> {
        @Override public String toString() {
            return JavaLangObjectUtils.toSimpleString(this);
        }
        @Override public void subscribe(final Flow.Subscriber<? super DayOfWeek> subscriber) {
            log.debug("{}.subscribe({})", this, subscriber);
            Objects.requireNonNull(subscriber, "subscriber is null");
            final var dayOfWeekValues = new ArrayList<>(Arrays.asList(DayOfWeek.values()));
            subscriber.onSubscribe(new Flow.Subscription() {
                @Override public String toString() {
                    return "subscription" + JavaLangObjectUtils.toSimpleString(this);
                }
                @Override public void request(final long n) {
                    log.debug("{}.request({})", this, n);
                    if (n <= 0L) {
                        cancelInternal();
                    }
                    if (cancelled) {
                        return;
                    }
                    for (long i = 0; i < n && !dayOfWeekValues.isEmpty(); i++) {
                        subscriber.onNext(dayOfWeekValues.removeFirst());
                    }
                    if (dayOfWeekValues.isEmpty()) {
                        if (!cancelled) {
                            cancelInternal();
                            subscriber.onComplete();
                        }
                    }
                }
                @Override  public void cancel() {
                    log.debug("{}.cancel()", this);
                    cancelInternal();
                }
                private void cancelInternal() { cancelled = true; }
                private boolean cancelled;
            });
        }
    }
    // @formatter:on

    @DisplayName("DayOfWeekPublisher")
    @Nested
    class DayOfWeekPublisherTest {

        @DisplayName("request(random)")
        @Test
        void __RequestRandom() { // @formatter:off
            new DayOfWeekPublisher().subscribe(new Flow.Subscriber<>() {
                @Override public String toString() {
                    return "subscriber" + JavaLangObjectUtils.toSimpleString(this);
                }
                @Override public void onSubscribe(final Flow.Subscription subscription) {
                    log.debug("{}.onSubscribe({})", this, subscription);
                    subscription.request(
                            ThreadLocalRandom.current().nextLong(
                                    ChronoField.DAY_OF_WEEK.range().getMaximum() << 1
                            ) + 1
                    );
                }
                @Override public void onNext(final DayOfWeek item) {
                    log.debug("{}.onNext({})", this, item);
                }
                @Override public void onError(final Throwable throwable) {
                    log.error("{}.onError({})", this, throwable, throwable);
                }
                @Override public void onComplete() {
                    log.debug("{}.onComplete()", this);
                }
            });
        } // @formatter:on

        @DisplayName("request(one-by-one)")
        @Test
        void __RequestOneByOne() { // @formatter:off
            new DayOfWeekPublisher().subscribe(new Flow.Subscriber<>() {
                @Override public String toString() {
                    return "subscriber" + JavaLangObjectUtils.toSimpleString(this);
                }
                @Override public void onSubscribe(final Flow.Subscription subscription) {
                    log.debug("{}.onSubscribe({})", this, subscription);
                    this.subscription = subscription;
                    this.subscription.request(1L);
                }
                @Override public void onNext(final DayOfWeek item) {
                    log.debug("{}.onNext({})", this, item);
                    if (!completed) {
                        this.subscription.request(1L);
                    }
                }
                @Override public void onError(final Throwable throwable) {
                    log.error("{}.onError({})", this, throwable, throwable);
                    this.completed = true;
                }
                @Override public void onComplete() {
                    log.debug("{}.onComplete()", this);
                    this.completed = true;
                }
                private Flow.Subscription subscription;
                private boolean completed;
            });
        } // @formatter:on
    }

    // @formatter:off
    private static class LocalDatePublisher implements Flow.Publisher<LocalDate> {
        @Override public void subscribe(final Flow.Subscriber<? super LocalDate> subscriber) {
            log.debug("subscribe({})", JavaLangObjectUtils.toSimpleString(subscriber));
            Objects.requireNonNull(subscriber, "subscriber is null");
            final var reference = new AtomicReference<>(LocalDate.now());
            final var threadReference = new AtomicReference<Thread>();
            subscriber.onSubscribe(new Flow.Subscription() {
                @Override public void request(final long n) {
                    log.debug("request({})", n);
                    if (n <= 0L) { cancelInternal(); }
                    if (cancelled) { return; }
                    final var previousThread = threadReference.get();
                    final var thread = Thread.ofPlatform().start(() -> {
                        if (previousThread != null) {
                            try {
                                previousThread.join();
                            } catch (final InterruptedException e) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        for (long i = 0; !cancelled && i < n; i++) { // what if n == Long.MAX_VALUE?
                            subscriber.onNext(reference.getAndAccumulate(
                                    null,
                                    (d1, d2) -> d1.plusDays(1L)
                            ));
                        }
                    });
                    threadReference.set(thread);
                }
                @Override public void cancel() {
                    log.debug("cancel()");
                    cancelInternal();
                }
                private void cancelInternal() { cancelled = true; }
                private volatile boolean cancelled = false;
            });
        }
    }
    // @formatter:on

    @DisplayName("LocalDatePublisher")
    @Nested
    class LocalDatePublisherTest {

        @Disabled
        @Test
        void __() { // @formatter:off
            new LocalDatePublisher().subscribe(new Flow.Subscriber<>() {
                @Override public String toString() {
                    return JavaLangObjectUtils.toSimpleString(this);
                }
                @Override public void onSubscribe(final Flow.Subscription subscription) {
                    log.debug("{}.onSubscribe({})", this,
                              JavaLangObjectUtils.toSimpleString(subscription));
                    this.subscription = subscription;
                    this.subscription.request(1L);
                    Thread.ofPlatform().start(() -> {
                        HelloWorldTestUtils.awaitForOneSecond();
                        log.debug("cancelling...");
                        log.debug("cancelling...");
                        log.debug("cancelling...");
                        log.debug("cancelling...");
                        log.debug("cancelling...");
                        this.subscription.cancel();
                    });
                }
                @Override public void onNext(final LocalDate item) {
                    log.debug("{}.onNext({})", this, item);
                    subscription.request(1L);
                }
                @Override public void onError(final Throwable throwable) {
                    log.error("{}.onError({})", this,throwable, throwable);
                }
                @Override public void onComplete() {
                    log.debug("{}.onComplete()", this);
                }
                private Flow.Subscription subscription;
            });
        } // @formatter:on
    }
}
