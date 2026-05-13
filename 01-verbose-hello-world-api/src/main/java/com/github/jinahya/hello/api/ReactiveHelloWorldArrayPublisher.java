package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A package-private implementation of {@link ReactiveHelloWorldPublisher} that publishes
 * {@code byte[]} elements — each a freshly assembled snapshot of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, of length
 * {@value HelloWorld#BYTES}.
 * <p>
 * Unlike {@link ReactiveHelloWorldBytePublisher} which is sourced directly from a
 * {@link HelloWorld}, this publisher is composed on top of another
 * {@link ReactiveHelloWorldPublisher} of {@link Byte} elements. <strong>For each unit of downstream
 * demand</strong>, the producer thread initiates a new subscription to the upstream byte publisher;
 * the inner byte subscriber accumulates {@value HelloWorld#BYTES} bytes into a fresh {@code byte[]}
 * and, on the upstream's {@code onComplete}, emits the assembled array downstream via
 * {@code onNext}. Multiple upstream subscriptions may run concurrently if demand arrives faster
 * than they complete; all downstream signals are serialized through a single lock to keep
 * <a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.3">Rule
 * 1.3</a> intact.
 * <p>
 * The stream is open-ended — it does not naturally complete; only {@code cancel()} (or an upstream
 * {@code onError}) terminates it.
 * <p>
 * The instance carries a single field — the wrapped upstream byte publisher — and is intended to be
 * reused across any number of subscribers. Each
 * {@link #subscribe(org.reactivestreams.Subscriber) subscribe} call allocates fresh per-subscriber
 * state (demand counter, terminated flag, lock) and starts a dedicated <em>virtual</em> thread to
 * drive demand. Virtual threads are always daemon threads, so a subscriber that is dropped without
 * {@code cancel}ing the subscription cannot block JVM shutdown.
 * <p>
 * <strong>Signal serialization (Rule 1.3).</strong> Every site that may invoke a subscriber
 * method — each worker's {@code onNext} and {@code onError}, plus {@code onError} from
 * {@link org.reactivestreams.Subscription#request(long) request(n &le; 0)} on the caller's
 * thread — is wrapped in the same {@link java.util.concurrent.locks.ReentrantLock ReentrantLock}.
 * Terminal sites additionally guard their emission with
 * {@code terminated.compareAndSet(false, true)} so that at most one of
 * {@code onError}/{@code onComplete} ever fires (Rule 1.7).
 * <p>
 * The order in which assembled {@code byte[]} elements reach the downstream is the race-determined
 * order of upstream completions, not the request order. Since every emitted array contains the
 * same payload, this ordering does not affect observable behaviour.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldBytePublisher
 */
non-sealed class ReactiveHelloWorldArrayPublisher
        implements ReactiveHelloWorldPublisher<byte[]> {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance wrapping the specified upstream byte publisher.
     *
     * @param publisher the upstream {@link ReactiveHelloWorldPublisher} of {@link Byte} that
     *                  supplies the individual bytes for each assembled array.
     * @throws NullPointerException if the {@code publisher} is {@code null}.
     */
    ReactiveHelloWorldArrayPublisher(final ReactiveHelloWorldPublisher<Byte> publisher) {
        super();
        this.publisher = Objects.requireNonNull(publisher, "publisher is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @implSpec The default implementation invokes
     * {@link org.reactivestreams.Subscriber#onSubscribe(org.reactivestreams.Subscription) onSubscribe}
     * with a fresh control-surface {@link org.reactivestreams.Subscription Subscription}, then
     * starts a virtual thread that loops as follows:
     * <ol>
     *   <li>parks on a {@link java.util.concurrent.locks.Condition Condition} until demand arrives
     *       or the subscription is terminated,</li>
     *   <li>for each unit of demand, subscribes to the upstream byte publisher with an inner
     *       {@link Subscriber} that requests {@value HelloWorld#BYTES} elements, accumulates each
     *       received byte into a fresh {@code byte[]}, and — on the upstream's {@code onComplete} —
     *       signals downstream {@code onNext(byte[])} (or, on the upstream's {@code onError}, the
     *       downstream {@code onError}). All downstream signals from inner subscribers are
     *       serialized through the producer's lock so that no two threads ever invoke a downstream
     *       method concurrently.</li>
     * </ol>
     * The loop only exits via cancellation; this publisher does not naturally complete.
     * <p>
     * Reentrant {@code request} calls from inside {@code onNext} are safe — the additional demand
     * is accumulated and picked up on the next iteration of the demand loop, satisfying
     * <a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#3.2">Rule
     * 3.2</a> without unbounded recursion
     * (<a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#3.3">Rule
     * 3.3</a>).
     * @throws NullPointerException if the {@code subscriber} is {@code null}.
     */
    @Override
    public void subscribe(final Subscriber<? super byte[]> subscriber) { // @formatter:off
        Objects.requireNonNull(subscriber, "subscriber is null");
        final var demand = new AtomicLong();
        final var terminated = new AtomicBoolean();
        final var lock = new ReentrantLock();
        final var condition = lock.newCondition();
        final var subscription = new Subscription() {
            @Override public void request(final long n) {
                if (terminated.get()) return;                                // Rule 3.6
                if (n <= 0L) {                                              // Rule 3.9
                    if (terminated.compareAndSet(false, true)) {             // Rule 1.7
                        lock.lock();
                        try {
                            condition.signalAll();
                            try {
                                subscriber.onError(
                                        new IllegalArgumentException(
                                                "n(" + n + ") is not positive")
                                );
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
                if (terminated.get()) break;
                demand.decrementAndGet();
                Thread.ofVirtual().start(() -> {
                    final var array = new byte[HelloWorld.BYTES];
                    final var index = new AtomicInteger();
                    final var error = new AtomicReference<Throwable>();
                    final var latch = new CountDownLatch(1);
                    publisher.subscribe(new Subscriber<>() {
                        @Override public void onSubscribe(final Subscription s) {
                            s.request(HelloWorld.BYTES);
                        }
                        @Override public void onNext(final Byte b) {
                            array[index.getAndIncrement()] = b;
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
                        if (terminated.get()) return;                       // Rule 3.12
                        final var t = error.get();
                        if (t != null) {
                            if (terminated.compareAndSet(false, true)) {    // Rule 1.7
                                try { subscriber.onError(t); } catch (final Throwable st) { }
                            }
                            return;
                        }
                        try { subscriber.onNext(array); } catch (final Throwable st) {
                            terminated.set(true);
                        }
                    } finally { lock.unlock(); }
                });
            }
        }); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final ReactiveHelloWorldPublisher<Byte> publisher;
}
