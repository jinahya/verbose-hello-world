package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * Shared test fixtures for the JDK {@link Flow} side of the {@code ReactiveHelloWorld*} test suite
 * — the {@link Flow}-flavoured counterpart to {@link ReactiveStreamTests}.
 * <p>
 * Currently three nested classes:
 * <ul>
 *   <li>{@link LoggingFlowSubscription LoggingFlowSubscription&lt;T&gt;} — a decorator around a
 *       {@link Flow.Subscription} that logs each {@code request(n)} / {@code cancel()} call
 *       before delegating;</li>
 *   <li>{@link LoggingFlowSubscriber LoggingFlowSubscriber&lt;T&gt;} — a {@link Flow.Subscriber}
 *       that logs every received signal ({@code onSubscribe}, {@code onNext}, {@code onError},
 *       {@code onComplete}) at {@code DEBUG} level;</li>
 *   <li>{@link LoggingSubmissionPublisher LoggingSubmissionPublisher&lt;T&gt;} — a
 *       {@link SubmissionPublisher} that logs each {@code subscribe(...)} call before delegating
 *       to the superclass.</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class FlowTests {

    static class LoggingFlowSubscription<T> implements Flow.Subscription {

        static Flow.Subscription from(final Flow.Subscription subscription) {
            return new LoggingFlowSubscription<>(subscription);
        }

        private LoggingFlowSubscription(final Flow.Subscription subscription) {
            super();
            this.subscription = Objects.requireNonNull(subscription, "subscription is null");
        }

        @Override
        public String toString() {
            return HelloWorldBookUtils.toSimplifiedString(super.toString());
        }

        @Override
        public void request(final long n) {
            log.debug("request({}) / {}", n, this);
            subscription.request(n);
        }

        @Override
        public void cancel() {
            log.debug("cancel() / {}", this);
            subscription.cancel();
        }

        private final Flow.Subscription subscription;
    }

    static class LoggingFlowSubscriber<T> implements Flow.Subscriber<T> {

        @Override
        public String toString() {
            return HelloWorldBookUtils.toSimplifiedString(super.toString());
        }

        @Override
        public void onSubscribe(final Flow.Subscription subscription) {
            log.debug("onSubscribe({}) / {}", subscription, this);
        }

        @Override
        public void onNext(final T item) {
            log.debug("onNext({}) / {}", item, this);
        }

        @Override
        public void onError(final Throwable throwable) {
            log.debug("onError({}) / {}", throwable, this);
        }

        @Override
        public void onComplete() {
            log.debug("onComplete() / {}", this);
        }
    }

    static class LoggingSubmissionPublisher<T> extends SubmissionPublisher<T> {

        @Override
        public String toString() {
            return HelloWorldBookUtils.toSimplifiedString(super.toString());
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super T> subscriber) {
            log.debug("subscribe({} / {})", subscriber, this);
            super.subscribe(subscriber);
        }
    }

    private FlowTests() {
        throw new AssertionError("instantiation is not allowed");
    }
}
