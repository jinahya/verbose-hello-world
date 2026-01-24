package com.github.jinahya.hello.api.reactive;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An abstract base class for implementing Reactive Streams {@link org.reactivestreams.Publisher}
 * with common backpressure and cancellation handling.
 * <p>
 * This class provides shared functionality for tracking pending requests and cancellation state,
 * which is commonly needed across multiple publisher implementations.
 * <p>
 * This is a package-private utility class for use within test implementations.
 *
 * @param <T> the type of element published
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
abstract class AbstractReactiveHelloWorldPublisher<T> implements Publisher<T> {

    /**
     * Creates a new subscription for the given subscriber.
     * <p>
     * Subclasses should implement this method to provide the actual subscription logic, using the
     * provided {@code pending} and {@code cancelled} state trackers.
     *
     * @param subscriber the subscriber to create a subscription for
     * @param pending    the atomic long for tracking pending requests
     * @param cancelled  the atomic boolean for tracking cancellation state
     * @return a new subscription
     */
    protected abstract Subscription createSubscription(
            Subscriber<? super T> subscriber,
            AtomicLong pending,
            AtomicBoolean cancelled
    );

    @Override
    public final void subscribe(final Subscriber<? super T> subscriber) {
        final var pending = new AtomicLong(0);
        final var cancelled = new AtomicBoolean(false);
        subscriber.onSubscribe(createSubscription(subscriber, pending, cancelled));
    }

    /**
     * Checks if the publisher should continue processing, i.e., not cancelled.
     *
     * @param cancelled the cancellation state
     * @return {@code true} if not cancelled, {@code false} otherwise
     */
    protected static boolean isNotCancelled(final AtomicBoolean cancelled) {
        return !cancelled.get();
    }

    /**
     * Handles completion when all pending requests are fulfilled.
     * <p>
     * This method should be called after emitting an item and decrementing the pending count.
     *
     * @param subscriber the subscriber to complete
     * @param pending    the atomic long tracking pending requests
     * @param cancelled  the atomic boolean tracking cancellation state
     */
    protected static <T> void completeIfNoPending(
            final Subscriber<? super T> subscriber,
            final AtomicLong pending,
            final AtomicBoolean cancelled
    ) {
        final var remaining = pending.get();
        if (remaining == 0 && isNotCancelled(cancelled)) {
            subscriber.onComplete();
        }
    }

    /**
     * Creates a standard subscription with common request and cancel handling.
     * <p>
     * This helper method creates a subscription that: - Tracks pending requests - Handles
     * cancellation - Calls the provided request handler for each requested item
     *
     * @param subscriber     the subscriber
     * @param pending        the atomic long for tracking pending requests
     * @param cancelled      the atomic boolean for tracking cancellation state
     * @param requestHandler the handler to call for each requested item
     * @return a new subscription
     */
    protected static <T> Subscription createStandardSubscription(
            final Subscriber<? super T> subscriber,
            final AtomicLong pending,
            final AtomicBoolean cancelled,
            final RequestHandler<T> requestHandler
    ) {
        return new Subscription() {
            @Override
            public void request(final long n) {
                if (cancelled.get()) {
                    return;
                }
                pending.addAndGet(n); // no overflow handled
                for (long i = 0; i < n && !cancelled.get(); i++) {
                    requestHandler.handle(subscriber, pending, cancelled);
                }
            }

            @Override
            public void cancel() {
                cancelled.set(true);
            }
        };
    }

    /**
     * Functional interface for handling individual request items.
     *
     * @param <T> the type of element
     */
    @FunctionalInterface
    protected interface RequestHandler<T> {

        /**
         * Handles a single requested item.
         *
         * @param subscriber the subscriber to potentially emit to
         * @param pending    the atomic long tracking pending requests
         * @param cancelled  the atomic boolean tracking cancellation state
         */
        void handle(Subscriber<? super T> subscriber, AtomicLong pending, AtomicBoolean cancelled);
    }
}
