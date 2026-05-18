package com.github.jinahya.hello.api;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.api.HelloWorldBookUtils.loggingProxy;

/**
 * A package-private {@link Publisher} of individual {@link Byte} elements — one per byte of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, in order.
 * <p>
 * The instance carries a single field — the wrapped {@link HelloWorld} {@code service} — and is
 * reusable across any number of subscribers. The source byte array is allocated <em>lazily</em>,
 * inside the producer thread, immediately before the first {@code onNext}; a subscriber that
 * subscribes and cancels without ever {@code request}ing pays no allocation cost.
 * <p>
 * <strong>Threading.</strong> Each {@link #subscribe(Subscriber) subscribe} call allocates fresh
 * per-subscriber state (demand counter, terminated flag, lock, source array, index) and starts a
 * dedicated <em>virtual</em> thread to drive emission. Virtual threads are always daemon threads,
 * so an abandoned subscriber cannot block JVM shutdown. The producer thread owns every
 * {@code onNext}/{@code onError}/{@code onComplete}; the subscription's {@code request}/
 * {@code cancel} run on whichever thread the subscriber calls them from.
 * <p>
 * <strong>Signal serialization (Rules 1.3 / 1.7).</strong> The producer virtual thread is the
 * sole sender of {@code onNext} and {@code onComplete}, so signals are naturally serialized (<a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.3">Rule
 * 1.3</a>). The terminal {@code onComplete} site CAS-guards the {@code terminated} flag, satisfying
 * <a href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.7">Rule
 * 1.7</a> — at most one terminal ever fires.
 * <p>
 * <strong>Lifetime.</strong> The stream completes naturally after all {@value HelloWorld#BYTES}
 * bytes have been emitted ({@code onComplete}); downstream {@code cancel()} stops emission without
 * a terminal signal (Rule 3.12).
 * <p>
 * <strong>Didactic scope.</strong> This class is written to <em>introduce</em> the Reactive
 * Streams workflow, not to be a hardened implementation. {@code request(n &le; 0)} is guarded by an
 * {@code assert} rather than routed to {@code onError}, and exceptions thrown by
 * {@link HelloWorld#set(byte[]) service.set(...)} are <em>not</em> caught — they propagate out of
 * the producer thread. A production-grade publisher would handle both as terminal {@code onError}
 * signals.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldArrayPublisher
 */
final class ReactiveHelloWorldBytePublisher implements Publisher<Byte> {

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
     * @throws NullPointerException if the {@code s} is {@code null}.
     * @implSpec The default implementation invokes
     * {@link org.reactivestreams.Subscriber#onSubscribe(org.reactivestreams.Subscription)
     * onSubscribe} with a fresh control-surface
     * {@link org.reactivestreams.Subscription Subscription}, then starts a virtual thread that:
     * <ol>
     *   <li>parks on a {@link java.util.concurrent.locks.Condition Condition} until demand arrives
     *       or the subscription is terminated,</li>
     *   <li>on the first iteration with demand, lazily calls
     *       {@link HelloWorld#set(byte[]) service.set(new byte[HelloWorld.BYTES])} to obtain the
     *       payload,</li>
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
    public void subscribe(final Subscriber<? super Byte> s) { // @formatter:off
        Objects.requireNonNull(s, "s is null");
        final var demand = new AtomicLong();
        final var terminated = new AtomicBoolean();
        final var lock = new ReentrantLock();
        final var condition = lock.newCondition();
        s.onSubscribe(loggingProxy(Subscription.class, new Subscription() {
            @Override public void request(final long n) {
                if (terminated.get()) { return; }
                ReactiveHelloWorldPublisherUtils.addDemand(demand, n);
                signal();
            }
            @Override public void cancel() {
                terminated.set(true);
                signal();
            }
            private void signal() {
                lock.lock();
                try { condition.signalAll(); } finally { lock.unlock(); }
            }
        }));
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
                if (terminated.get()) { return; }
                assert demand.get() > 0L;
                demand.decrementAndGet();
                if (array == null) {
                    array = service.set(new byte[HelloWorld.BYTES]);
                }
                s.onNext(array[index++]);
                if (index == HelloWorld.BYTES) {
                    if (terminated.compareAndSet(false, true)) {
                        s.onComplete();
                    }
                    return;
                }
            }
        }); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;
}
