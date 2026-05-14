package com.github.jinahya.hello.api;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.api.ReactiveHelloWorldPublisherUtils.lockAndRun;

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
 * <strong>Signal serialization (Rules 1.3 / 1.7).</strong> Every subscriber-signal site — the
 * producer's {@code onNext} and {@code onComplete}, the producer's {@code onError} from a failed
 * {@link HelloWorld#set(byte[]) service.set(...)}, and {@code onError} from
 * {@link Subscription#request(long) request(n &le; 0)} on the caller's thread — is wrapped in the
 * same {@link ReentrantLock}, satisfying <a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.3">Rule
 * 1.3</a>. Terminal sites additionally CAS the {@code terminated} flag, satisfying <a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.7">Rule
 * 1.7</a> — at most one of {@code onError}/{@code onComplete} ever fires.
 * <p>
 * <strong>Lifetime.</strong> The stream completes naturally after all {@value HelloWorld#BYTES}
 * bytes have been emitted ({@code onComplete}); a {@code request(n &le; 0)} call or a failed
 * {@code service.set(...)} terminates it early with {@code onError}; downstream {@code cancel()}
 * stops emission without a terminal signal (Rule 3.12).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldPublishers#ofBytes(HelloWorld)
 * @see ReactiveHelloWorldArrayPublisher
 */
final class ReactiveHelloWorldBytePublisher implements Publisher<Byte> {

    private static final System.Logger logger = System.getLogger(
            MethodHandles.lookup().lookupClass().getName()
    );

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

    // ---------------------------------------------------------------------------- java.lang.Object
    @Override
    public String toString() {
        return super.toString().substring(getClass().getPackageName().length() + 1);
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
        logger.log(System.Logger.Level.DEBUG, "subscribe({0}) / {1}", subscriber, this);
        Objects.requireNonNull(subscriber, "subscriber is null");
        final var demand = new AtomicLong();
        final var terminated = new AtomicBoolean();
        final var lock = new ReentrantLock();
        final var condition = lock.newCondition();
        final var subscription = new Subscription() {
            @Override public String toString() {
                return super.toString().substring(getClass().getPackageName().length() + 1);
            }
            @Override public void request(final long n) {
                logger.log(System.Logger.Level.DEBUG, "request({0}) / {1}", n, this);
                if (terminated.get()) { return; }
                if (n <= 0L) {
                    if (terminated.compareAndSet(false, true)) {
                        lockAndRun(lock, () -> {
                            condition.signalAll();
                            try {
                                subscriber.onError(new IllegalArgumentException(
                                        "n(" + n + ") is not positive"
                                ));
                            } catch (final Throwable st) { }
                        });
                    }
                    return;
                }
                demand.accumulateAndGet(n, (cur, inc) -> {
                    try { return Math.addExact(cur, inc); }
                    catch (final ArithmeticException ae) { return Long.MAX_VALUE; }
                });
                signal();
            }
            @Override public void cancel() {
                logger.log(System.Logger.Level.DEBUG, "cancel() / {0}", this);
                terminated.set(true);
                signal();
            }
            private void signal() {
                lockAndRun(lock, condition::signalAll);
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
                assert demand.get() > 0L || terminated.get();
                if (terminated.get()) { break; }
                assert demand.get() > 0L;
                demand.decrementAndGet();
                if (array == null) {
                    try {
                        array = service.set(new byte[HelloWorld.BYTES]);
                    } catch (final Throwable t) {
                        if (terminated.compareAndSet(false, true)) {
                            lockAndRun(lock, () -> {
                                try { subscriber.onError(t); } catch (final Throwable st) { }
                            });
                        }
                        return;
                    }
                }
                lock.lock();
                try {
                    try {
                        subscriber.onNext(array[index++]);
                    } catch (final Throwable t) {
                        terminated.set(true);
                        break;
                    }
                } finally { lock.unlock(); }
                if (index == HelloWorld.BYTES) { break; }
            }
            if (terminated.compareAndSet(false, true)) {
                assert array != null;
                assert index == array.length;
                lockAndRun(lock, () -> {
                    try { subscriber.onComplete(); } catch (final Throwable st) { }
                });
            }
        }); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;
}
