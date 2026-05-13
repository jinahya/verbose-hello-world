package com.github.jinahya.hello.api;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A package-private implementation of {@link ReactiveHelloWorldPublisher} that publishes
 * {@link String} elements — each decoded in {@link StandardCharsets#US_ASCII US-ASCII} from a
 * {@code byte[]} obtained from the upstream byte-array publisher.
 * <p>
 * This publisher follows the same structural pattern as {@link ReactiveHelloWorldArrayPublisher}: a
 * producer virtual thread parks on demand, and for each unit of downstream demand spawns a fresh
 * worker virtual thread that initiates a new subscription to the upstream byte-array publisher. The
 * worker requests <strong>one</strong> {@code byte[]}, awaits delivery via a
 * {@link CountDownLatch}, cancels the upstream subscription, decodes the bytes into a
 * {@link String}, and emits the string downstream.
 * <p>
 * The instance carries a single field — the wrapped upstream byte-array publisher — and is intended
 * to be reused across any number of subscribers. Each
 * {@link #subscribe(org.reactivestreams.Subscriber) subscribe} call allocates fresh per-subscriber
 * state (demand counter, terminated flag, lock) and starts a dedicated <em>virtual</em> thread to
 * drive demand. Virtual threads are always daemon threads, so a subscriber that is dropped without
 * {@code cancel}ing the subscription cannot block JVM shutdown.
 * <p>
 * <strong>Signal serialization (Rule 1.3).</strong> Every site that may invoke a subscriber
 * method — each worker's {@code onNext} and {@code onError}, plus {@code onError} from
 * {@link org.reactivestreams.Subscription#request(long) request(n &le; 0)} on the caller's thread —
 * is wrapped in the same {@link ReentrantLock}. Terminal sites additionally guard their emission
 * with {@code terminated.compareAndSet(false, true)} so that at most one of
 * {@code onError}/{@code onComplete} ever fires (Rule 1.7).
 * <p>
 * The stream's natural lifetime mirrors the upstream's — if upstream errors, downstream errors; if
 * upstream completes without delivering a {@code byte[]} for a pending worker, downstream
 * completes. (When stacked on top of the open-ended {@link ReactiveHelloWorldArrayPublisher}, this
 * publisher is open-ended too, since the inner subscription is cancelled after one delivery and the
 * upstream's natural completion is never observed.)
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldArrayPublisher
 */
non-sealed class ReactiveHelloWorldStringPublisher
        implements ReactiveHelloWorldPublisher<String> {

    /**
     * A single-use {@link Processor} that subscribes to an upstream {@code byte[]} publisher and
     * republishes each {@code byte[]} as a {@link String} decoded in
     * {@link StandardCharsets#US_ASCII US-ASCII}.
     * <p>
     * Expected wiring (typical of any {@link Processor}):
     * <pre>{@code
     *     final var encoder = new StringEncoder();
     *     upstream.subscribe(encoder);          // upstream calls encoder.onSubscribe(...)
     *     encoder.subscribe(downstream);        // downstream gets its Subscription via onSubscribe
     * }</pre>
     * Demand is forwarded <strong>1:1</strong> — each downstream {@code request(n)} is forwarded as
     * {@code upstream.request(n)}; each upstream {@code onNext(byte[])} produces one downstream
     * {@code onNext(String)}.
     * <p>
     * One downstream subscriber per instance. Downstream signals are serialized through an
     * internal {@link ReentrantLock} (Rule 1.3), and terminal sites are gated by
     * {@code terminated.compareAndSet(false, true)} so that at most one of
     * {@code onError}/{@code onComplete} ever fires (Rule 1.7).
     */
    private static class StringEncoder implements Processor<byte[], String> {

        // -------------------------------------------------------------------------------- CONSTRUCTORS

        /**
         * Creates a new instance wrapping the specified upstream byte-array publisher.
         *
         * @param publisher the upstream {@link Publisher} of {@code byte[]} that supplies the bytes
         *                  for each decoded string.
         * @throws NullPointerException if the {@code publisher} is {@code null}.
         */
        StringEncoder(final Publisher<? extends byte[]> publisher) {
            super();
            this.upstreamPublisher = Objects.requireNonNull(publisher, "publisher is null");
        }

        // ------------------------------------------------------------------- Publisher<String> side
        @Override
        public void subscribe(final Subscriber<? super String> s) {
            Objects.requireNonNull(s, "s is null");
            this.downstreamSubscriber = s;
            s.onSubscribe(new Subscription() {
                @Override
                public void request(final long n) {
                    if (terminated.get()) { return; }                        // Rule 3.6
                    if (n <= 0L) {                                           // Rule 3.9
                        if (terminated.compareAndSet(false, true)) {         // Rule 1.7
                            lock.lock();
                            try {
                                try {
                                    downstreamSubscriber.onError(new IllegalArgumentException(
                                            "n(" + n + ") is not positive"
                                    ));
                                } catch (final Throwable st) { }
                            } finally { lock.unlock(); }
                            final var u = upstreamSubscription;
                            if (u != null) { u.cancel(); }
                        }
                        return;
                    }
                    final var u = upstreamSubscription;
                    if (u != null) { u.request(n); }                         // 1:1 passthrough
                }

                @Override
                public void cancel() {                                       // Rule 3.5, 3.7
                    terminated.set(true);
                    final var u = upstreamSubscription;
                    if (u != null) { u.cancel(); }
                }
            });
        }

        // ------------------------------------------------------------------- Subscriber<byte[]> side
        @Override
        public void onSubscribe(final Subscription s) {
            this.upstreamSubscription = s;
        }

        @Override
        public void onNext(final byte[] bytes) {
            lock.lock();
            try {
                if (terminated.get()) { return; }                            // Rule 3.12 / 1.7
                try {
                    downstreamSubscriber.onNext(new String(bytes, StandardCharsets.US_ASCII));
                } catch (final Throwable t) {
                    terminated.set(true);
                }
            } finally { lock.unlock(); }
            if (terminated.get()) {
                final var u = upstreamSubscription;
                if (u != null) { u.cancel(); }
            }
        }

        @Override
        public void onError(final Throwable t) {
            if (terminated.compareAndSet(false, true)) {                     // Rule 1.7
                lock.lock();
                try {
                    try { downstreamSubscriber.onError(t); } catch (final Throwable st) { }
                } finally { lock.unlock(); }
            }
        }

        @Override
        public void onComplete() {
            if (terminated.compareAndSet(false, true)) {                     // Rule 1.7
                lock.lock();
                try {
                    try { downstreamSubscriber.onComplete(); } catch (final Throwable st) { }
                } finally { lock.unlock(); }
            }
        }

        // ------------------------------------------------------------------------------------------
        private final Publisher<? extends byte[]> upstreamPublisher;
        private Subscription upstreamSubscription;
        private final ReentrantLock lock = new ReentrantLock();
        private final AtomicBoolean terminated = new AtomicBoolean();
        private Subscriber<? super String> downstreamSubscriber;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance wrapping the specified upstream byte-array publisher.
     *
     * @param publisher the upstream {@link ReactiveHelloWorldPublisher} of {@code byte[]} that
     *                  supplies the bytes for each decoded string.
     * @throws NullPointerException if the {@code publisher} is {@code null}.
     */
    ReactiveHelloWorldStringPublisher(final ReactiveHelloWorldPublisher<byte[]> publisher) {
        super();
        this.publisher = Objects.requireNonNull(publisher, "publisher is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the {@code subscriber} is {@code null}.
     * @implSpec The default implementation invokes
     * {@link org.reactivestreams.Subscriber#onSubscribe(org.reactivestreams.Subscription)
     * onSubscribe} with a fresh control-surface
     * {@link org.reactivestreams.Subscription Subscription}, then starts a virtual thread that
     * loops as follows:
     * <ol>
     *   <li>parks on a {@link java.util.concurrent.locks.Condition Condition} until demand arrives
     *       or the subscription is terminated,</li>
     *   <li>for each unit of demand, spawns a fresh worker virtual thread which subscribes to the
     *       upstream byte-array publisher with an inner {@link Subscriber} that
     *       {@code request(1L)}s one {@code byte[]}; on receiving it the worker cancels the
     *       upstream subscription, decodes the bytes into a {@link String} via
     *       {@link String#String(byte[], java.nio.charset.Charset)
     *       new String(bytes, StandardCharsets.US_ASCII)}, and signals downstream
     *       {@code onNext(string)} (or, on the upstream's {@code onError}, the downstream
     *       {@code onError}; on the upstream's {@code onComplete} without delivery, the downstream
     *       {@code onComplete}). All downstream signals from workers are serialized through the
     *       producer's lock.</li>
     * </ol>
     * The loop only exits via cancellation; this publisher does not naturally complete unless the
     * upstream completes a worker's subscription without delivering an element.
     * <p>
     * Reentrant {@code request} calls from inside {@code onNext} are safe — the additional demand
     * is accumulated and picked up on the next iteration of the demand loop, satisfying
     * <a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#3.2">Rule
     * 3.2</a> without unbounded recursion
     * (<a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#3.3">Rule
     * 3.3</a>).
     */
    @Override
    public void subscribe(final Subscriber<? super String> subscriber) { // @formatter:off
        Objects.requireNonNull(subscriber, "subscriber is null");
        final var demand = new AtomicLong();
        final var terminated = new AtomicBoolean();
        final var lock = new ReentrantLock();
        final var condition = lock.newCondition();
        final var subscription = new Subscription() {
            @Override public void request(final long n) {
                if (terminated.get()) { return; }                            // Rule 3.6
                if (n <= 0L) {                                              // Rule 3.9
                    if (terminated.compareAndSet(false, true)) {             // Rule 1.7
                        lock.lock();
                        try {
                            condition.signalAll();
                            try {
                                subscriber.onError(new IllegalArgumentException(
                                        "n(" + n + ") is not positive"
                                ));
                            } catch (final Throwable st) { }
                        } finally { lock.unlock(); }
                    }
                    return;
                }
                demand.accumulateAndGet(n, (cur, inc) -> {
                    try { return Math.addExact(cur, inc); }
                    catch (final ArithmeticException ae) { return Long.MAX_VALUE; }
                });
                signal();
            }
            @Override public void cancel() {                                // Rule 3.5, 3.7
                terminated.set(true);
                signal();
            }
            private void signal() {
                lock.lock();
                try { condition.signalAll(); } finally { lock.unlock(); }
            }
        };
        try {
            subscriber.onSubscribe(subscription);
        } catch (final Throwable t) {
            terminated.set(true);
            return;
        }
        Thread.ofVirtual().start(() -> {
            while (true) {
                lock.lock();
                try {
                    while (demand.get() == 0L && !terminated.get()) {
                        try {
                            condition.await();
                        } catch (final InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                } finally {
                    lock.unlock();
                }
                if (terminated.get()) { break; }
                demand.decrementAndGet();
                Thread.ofVirtual().start(() -> {
                    final var result = new AtomicReference<byte[]>();
                    final var error = new AtomicReference<Throwable>();
                    final var latch = new CountDownLatch(1);
                    publisher.subscribe(new Subscriber<>() {
                        private Subscription upstreamSub;
                        @Override public void onSubscribe(final Subscription s) {
                            this.upstreamSub = s;
                            s.request(1L);
                        }
                        @Override public void onNext(final byte[] bytes) {
                            result.set(bytes);
                            upstreamSub.cancel();
                            latch.countDown();
                        }
                        @Override public void onError(final Throwable t) {
                            error.set(t);
                            latch.countDown();
                        }
                        @Override public void onComplete() {
                            latch.countDown();
                        }
                    });
                    try {
                        latch.await();
                    } catch (final InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    lock.lock();
                    try {
                        if (terminated.get()) { return; }                   // Rule 3.12
                        final var t = error.get();
                        if (t != null) {
                            if (terminated.compareAndSet(false, true)) {    // Rule 1.7
                                try { subscriber.onError(t); }
                                catch (final Throwable st) { }
                            }
                            return;
                        }
                        final var bytes = result.get();
                        if (bytes == null) {
                            // upstream completed without delivering a byte[]
                            if (terminated.compareAndSet(false, true)) {    // Rule 1.7
                                try { subscriber.onComplete(); }
                                catch (final Throwable st) { }
                            }
                            return;
                        }
                        try {
                            subscriber.onNext(new String(bytes, StandardCharsets.US_ASCII));
                        } catch (final Throwable st) {
                            terminated.set(true);
                        }
                    } finally { lock.unlock(); }
                });
            }
        }); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final ReactiveHelloWorldPublisher<byte[]> publisher;
}
