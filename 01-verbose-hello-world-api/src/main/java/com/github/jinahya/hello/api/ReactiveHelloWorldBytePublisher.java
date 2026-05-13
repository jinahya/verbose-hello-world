package com.github.jinahya.hello.api;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A package-private implementation of {@link ReactiveHelloWorldPublisher} that publishes the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as individual {@link Byte}
 * elements, one per byte of the payload, in order.
 * <p>
 * The instance carries a single field — the wrapped {@link HelloWorld} {@code service} — and is
 * intended to be reused across any number of subscribers. Each
 * {@link #subscribe(org.reactivestreams.Subscriber) subscribe} call allocates fresh per-subscriber
 * state (demand counter, terminated flag, lock, source array, index) and starts a dedicated
 * <em>virtual</em> thread to drive emission. Virtual threads are always daemon threads, so a
 * subscriber that is dropped without {@code cancel}ing or fully consuming the stream cannot block
 * JVM shutdown.
 * <p>
 * Spec compliance is split across two surfaces:
 * <ul>
 *   <li>The inner {@link org.reactivestreams.Subscription Subscription} is the control surface
 *       observed by the subscriber. It accumulates demand into an {@link java.util.concurrent.atomic.AtomicLong
 *       AtomicLong} (capped at {@link Long#MAX_VALUE} on overflow) and signals the producer thread
 *       via a {@link java.util.concurrent.locks.Condition Condition}.</li>
 *   <li>The producer thread owns all subscriber signals — every {@code onNext}, {@code onError},
 *       and {@code onComplete} is invoked from that single thread, satisfying
 *       <a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.3">Rule
 *       1.3</a> (sequential signalling) without an explicit serial channel.</li>
 * </ul>
 * <p>
 * <strong>Signal serialization (Rule 1.3).</strong> Every site that may invoke a subscriber
 * method — the producer's {@code onNext} and {@code onComplete}, the producer's {@code onError}
 * from a failed {@link HelloWorld#set(byte[]) service.set}, and {@code onError} from
 * {@link org.reactivestreams.Subscription#request(long) request(n &le; 0)} on the caller's
 * thread — is wrapped in the same {@link java.util.concurrent.locks.ReentrantLock ReentrantLock}.
 * Terminal sites additionally guard their emission with
 * {@code terminated.compareAndSet(false, true)} so that at most one of
 * {@code onError}/{@code onComplete} ever fires (Rule 1.7).
 * <p>
 * The source byte array is allocated and populated <em>lazily</em>, inside the producer thread,
 * immediately before the first {@code onNext}. A subscriber that subscribes and cancels without
 * ever {@code request}ing pays no allocation cost.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldPublisher#newInstanceForBytes(HelloWorld)
 */
non-sealed class ReactiveHelloWorldBytePublisher
        implements ReactiveHelloWorldPublisher<Byte> {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance wrapping the specified {@link HelloWorld} service.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    ReactiveHelloWorldBytePublisher(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the {@code subscriber} is {@code null}.
     * @implSpec The default implementation invokes
     * {@link org.reactivestreams.Subscriber#onSubscribe(org.reactivestreams.Subscription)
     * onSubscribe} with a fresh control-surface
     * {@link org.reactivestreams.Subscription Subscription}, then starts a virtual thread that:
     * <ol>
     *   <li>parks on a {@link java.util.concurrent.locks.Condition Condition} until demand arrives
     *       or the subscription is terminated,</li>
     *   <li>on the first iteration with demand, lazily calls
     *       {@link HelloWorld#set(byte[]) service.set(new byte[HelloWorld.BYTES])} to obtain the
     *       payload (any thrown exception is routed to {@code onError}),</li>
     *   <li>emits one {@link Byte} per iteration via
     *       {@link org.reactivestreams.Subscriber#onNext(Object) onNext}, decrementing demand,</li>
     *   <li>breaks out and signals
     *       {@link org.reactivestreams.Subscriber#onComplete() onComplete} once all
     *       {@value HelloWorld#BYTES} bytes have been emitted.</li>
     * </ol>
     * Reentrant {@code request} calls from inside {@code onNext} are safe — the additional demand
     * is accumulated by the same producer thread and picked up on the next iteration of the
     * emission loop, satisfying
     * <a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#3.2">Rule
     * 3.2</a> without unbounded recursion
     * (<a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#3.3">Rule
     * 3.3</a>).
     */
    @Override
    public void subscribe(final Subscriber<? super Byte> subscriber) { // @formatter:off
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
            byte[] array = null;
            int index = 0;
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
                } finally { lock.unlock(); }
                if (terminated.get()) { break; }
                assert demand.get() > 0L;
                demand.decrementAndGet();
                if (array == null) {
                    try {
                        array = service.set(new byte[HelloWorld.BYTES]);
                    } catch (final Throwable t) {
                        if (terminated.compareAndSet(false, true)) {         // Rule 1.7
                            lock.lock();
                            try { try { subscriber.onError(t); } catch (final Throwable st) { }
                            } finally { lock.unlock(); }
                        }
                        return;
                    }
                }
                lock.lock();
                try {
                    if (terminated.get()) { break; }                         // Rule 3.12
                    try {
                        subscriber.onNext(array[index++]);
                    } catch (final Throwable t) {
                        terminated.set(true);
                        break;
                    }
                } finally { lock.unlock(); }
                if (index == HelloWorld.BYTES) { break; }
            }
            if (terminated.compareAndSet(false, true)) {                     // Rule 1.7
                lock.lock();
                try { try { subscriber.onComplete(); } catch (final Throwable st) { }
                } finally { lock.unlock(); }
            }
        }); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;
}
