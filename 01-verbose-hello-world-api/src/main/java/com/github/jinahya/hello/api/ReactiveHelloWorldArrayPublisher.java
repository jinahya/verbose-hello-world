package com.github.jinahya.hello.api;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.api.ReactiveHelloWorldPublisherUtils.lockAndRun;

/**
 * A package-private {@link Publisher} of {@code byte[]} elements — each a freshly assembled,
 * {@value HelloWorld#BYTES}-byte snapshot of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
 * <p>
 * Unlike {@link ReactiveHelloWorldBytePublisher} which sources from a {@link HelloWorld} directly,
 * this publisher is composed on top of another {@link Publisher} of {@link Byte} elements. <strong>
 * For each unit of downstream demand</strong>, the producer thread spawns a fresh worker virtual
 * thread that opens a new subscription to the upstream byte publisher, accumulates
 * {@value HelloWorld#BYTES} bytes into a {@code byte[]}, and — on the upstream's {@code onComplete}
 * — emits the assembled array downstream via {@code onNext}.
 * <p>
 * <strong>Threading.</strong> Each {@link #subscribe(Subscriber) subscribe} call allocates fresh
 * per-subscriber state (demand counter, terminated flag, lock) and starts a dedicated producer
 * <em>virtual</em> thread that parks on demand. Virtual threads are always daemon threads, so an
 * abandoned subscriber cannot block JVM shutdown. Multiple upstream subscriptions may run
 * concurrently if demand arrives faster than they complete — the workers race independently.
 * <p>
 * <strong>Signal serialization (Rules 1.3 / 1.7).</strong> Every subscriber-signal site — each
 * worker's downstream {@code onNext} (on upstream-completion) and {@code onError} (on
 * upstream-error), plus {@code onError} from {@link Subscription#request(long) request(n &le; 0)}
 * on the caller's thread — is wrapped in the same {@link ReentrantLock}, satisfying <a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.3">Rule
 * 1.3</a>. Terminal sites additionally CAS the {@code terminated} flag, satisfying <a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.7">Rule
 * 1.7</a> — at most one terminal ever fires.
 * <p>
 * <strong>Lifetime.</strong> The stream is open-ended — it does not naturally complete; only a
 * {@code cancel()} (or an upstream {@code onError}, or a {@code request(n &le; 0)}) terminates it.
 * The order in which assembled arrays reach the downstream is the race-determined order of upstream
 * completions; since every emitted array contains the same payload, ordering does not affect
 * observable behaviour.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldPublishers#ofArrays(HelloWorld)
 * @see ReactiveHelloWorldBytePublisher
 * @see ReactiveHelloWorldStringPublisher
 */
final class ReactiveHelloWorldArrayPublisher implements Publisher<byte[]> {

    private static final System.Logger logger = System.getLogger(
            MethodHandles.lookup().lookupClass().getName()
    );

    static ReactiveHelloWorldArrayPublisher from(final HelloWorld service) {
        return new ReactiveHelloWorldArrayPublisher(new ReactiveHelloWorldBytePublisher(service));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance wrapping the specified upstream byte publisher.
     *
     * @param publisher the upstream {@link Publisher} of {@link Byte} that supplies the individual
     *                  bytes for each assembled array.
     * @throws NullPointerException if the {@code publisher} is {@code null}.
     */
    ReactiveHelloWorldArrayPublisher(final ReactiveHelloWorldBytePublisher publisher) {
        super();
        this.publisher = Objects.requireNonNull(publisher, "publisher is null");
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
     * {@link org.reactivestreams.Subscription Subscription}, then starts a virtual thread that
     * loops as follows:
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
     */
    @Override
    public void subscribe(final Subscriber<? super byte[]> subscriber) { // @formatter:off
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
        try { subscriber.onSubscribe(subscription); } catch (final Throwable t) {
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
                assert demand.get() > 0L || terminated.get();
                if (terminated.get()) { break; }
                assert demand.get() > 0L;
                demand.decrementAndGet();
                Thread.ofVirtual().start(() -> {
                    final var array = new byte[HelloWorld.BYTES];
                    final var index = new AtomicInteger();
                    final var error = new AtomicReference<Throwable>();
                    final var latch = new CountDownLatch(1);
                    publisher.subscribe(new Subscriber<>() {
                        @Override public String toString() {
                            return super.toString().substring(getClass().getPackageName().length() + 1);
                        }
                        @Override public void onSubscribe(final Subscription s) {
                            logger.log(System.Logger.Level.DEBUG, "onSubscribe({0}) / {1}", s, this);
                            s.request(HelloWorld.BYTES);
                        }
                        @Override public void onNext(final Byte b) {
                            logger.log(System.Logger.Level.DEBUG, "onNext({0}) / {1}",
                                       String.format("%02x'%c'", b, b), this);
                            array[index.getAndIncrement()] = b;
                        }
                        @Override public void onError(final Throwable t) {
                            logger.log(System.Logger.Level.DEBUG, "onError({0}) / {1}", t, this);
                            error.set(t);
                            latch.countDown();
                        }
                        @Override public void onComplete() {
                            logger.log(System.Logger.Level.DEBUG, "onComplete() / {0}", this);
                            latch.countDown();
                        }
                    });
                    try { latch.await(); } catch (final InterruptedException _) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    final var t = error.get();
                    if (t != null) {
                        if (terminated.compareAndSet(false, true)) {
                            lockAndRun(lock, () -> {
                                try { subscriber.onError(t); } catch (final Throwable st) { }
                            });
                        }
                        return;
                    }
                    lockAndRun(lock, () -> {
                        try { subscriber.onNext(array); } catch (final Throwable st) {
                            terminated.set(true);
                        }
                    });
                });
            }
        }); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final ReactiveHelloWorldBytePublisher publisher;
}
