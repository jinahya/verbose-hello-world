package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A package-private <em>cycle-batched multicast</em> {@link Processor} of {@link Byte} elements.
 * <p>
 * The class itself plays the role of {@link Subscriber Subscriber&lt;Byte&gt;} to an upstream
 * {@link ReactiveHelloWorldBytePublisher} instance, so
 * {@link #onSubscribe(Subscription) onSubscribe}, {@link #onNext(Byte) onNext},
 * {@link #onError(Throwable) onError}, and {@link #onComplete() onComplete} are real, executed code
 * (not dead).
 * <p>
 * Three concurrent moving parts:
 * <ol>
 *   <li>A lazily-started <strong>outer</strong> virtual thread that loops:
 *       <ol>
 *         <li>{@code statesQueue.take()} blocks until at least one downstream subscriber is
 *             waiting,</li>
 *         <li>reset cycle state, then {@code upstreamPublisher.subscribe(this)} — which
 *             synchronously calls {@link #onSubscribe(Subscription) this.onSubscribe} (where
 *             {@code request(12)} happens),</li>
 *         <li>wait for the cycle to complete (signaled by the 12th {@code onNext} or by
 *             {@code onError}),</li>
 *         <li>loop.</li>
 *       </ol>
 *   </li>
 *   <li>One <strong>worker</strong> virtual thread per downstream subscriber, started by
 *       {@link #subscribe(Subscriber)}. The worker calls {@code downstream.onSubscribe(...)},
 *       offers its {@code State} onto {@code statesQueue} (waking the outer), and then loops on
 *       its own per-subscriber {@link BlockingQueue}{@code <Byte>} delivering bytes as
 *       {@code request(n)} demands.</li>
 *   <li>The producer virtual thread <em>inside</em> the byte publisher (one per cycle) — emits the
 *       12 bytes and self-terminates.</li>
 * </ol>
 * Subscribers who join while a cycle is in flight all share that cycle's 12-byte payload (because
 * the outer drains {@code statesQueue} at the 12th {@code onNext} moment, pulling in everyone who
 * joined since the cycle started). Subscribers who join between cycles trigger the next cycle when
 * the outer next reaches {@code take()}.
 * <p>
 * {@link #subscribe(Subscriber) subscribe} is non-blocking: it constructs a fresh {@code State},
 * starts the worker thread, and returns immediately. The {@code downstream.onSubscribe}, the
 * offer into {@code statesQueue}, and the subsequent delivery all happen on the worker thread.
 * <p>
 * <strong>Spec caveat (Rule 2.12).</strong> Because the outer loops, the same instance
 * ({@code this}) is subscribed to {@code upstreamPublisher} more than once across cycles —
 * which spec Rule 2.12 forbids ("a Subscriber must not be subscribed more than once").
 * {@link ReactiveHelloWorldBytePublisher} does not enforce that rule, so it works in practice,
 * but a strict spec-conformant implementation would use a fresh {@code Subscriber<Byte>} per
 * cycle instead.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldBytePublisher
 */
final class ReactiveHelloWorldByteProcessor implements Processor<Byte, Byte> {

    private static final class State implements Runnable {

        State(final Subscriber<? super Byte> downstream) {
            super();
            this.downstream = downstream;
        }

        private void signal() { // @formatter:off
            lock.lock();
            try { condition.signalAll(); } finally { lock.unlock(); } // @formatter:on
        }

        @Override
        public void run() { // @formatter:off
            lock.lock();
            try {
                while (!terminated.get() && queue.isEmpty()) {
                    condition.awaitUninterruptibly();
                }
            } finally { lock.unlock(); }
            if (terminated.get()) { return; }
            while (!queue.isEmpty()) {
                lock.lock();
                try {
                    while (!terminated.get() && demand.get() == 0L) {
                        condition.awaitUninterruptibly();
                    }
                } finally { lock.unlock(); }
                if (terminated.get()) { return; }
                demand.decrementAndGet();
                downstream.onNext(queue.poll());
            }
            downstream.onComplete(); // @formatter:on
        }

        // ---------------------------------------------------------------------------------------------
        private final Subscriber<? super Byte> downstream;

        private final BlockingQueue<Byte> queue = new ArrayBlockingQueue<>(HelloWorld.BYTES);

        private final AtomicLong demand = new AtomicLong();

        private final AtomicBoolean terminated = new AtomicBoolean();

        private final ReentrantLock lock = new ReentrantLock();

        private final Condition condition = lock.newCondition();
    }

    // ---------------------------------------------------------------------------------------------
    static ReactiveHelloWorldByteProcessor from(final HelloWorld service) {
        return new ReactiveHelloWorldByteProcessor(service);
    }

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorldByteProcessor(final HelloWorld service) {
        super();
        this.publisher = new ReactiveHelloWorldBytePublisher(service);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onSubscribe(final Subscription s) {
        assert s != null;
        s.request(HelloWorld.BYTES);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onNext(final Byte t) {
        assert t != null;
        assert index < array.length;
        array[index++] = t;
    }

    @Override
    public void onError(final Throwable t) {
    }

    @Override
    public void onComplete() { // @formatter:off
        final var drained = new ArrayList<State>();
        queue.drainTo(drained);
        for (final var state : drained) {
            if (state.terminated.get()) { continue; }
            for (final byte element : array) {
                final var offered = state.queue.offer(element);
                assert offered;
            }
            state.signal();
        }
        assert latch != null;
        latch.countDown(); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the {@code s} is {@code null}.
     * @implSpec This method constructs a fresh {@link State} and starts a per-subscriber worker
     * virtual thread, then returns immediately. The worker calls {@code s.onSubscribe(...)}, offers
     * its {@code State} to the shared {@code statesQueue} (waking the outer thread), and loops on
     * its own queue delivering to {@code s}.
     */
    @Override
    public void subscribe(final Subscriber<? super Byte> s) { // @formatter:off
        Objects.requireNonNull(s, "s is null");
        final var state = new State(s);
        s.onSubscribe(new Subscription() {
            @Override public void request(final long n) {
                assert n > 0L;
                if (state.terminated.get()) { return; }
                state.demand.addAndGet(n);
                state.signal();
            }
            @Override public void cancel() {
                state.terminated.set(true);
                state.signal();
            }
        });
        Thread.ofVirtual().start(state);
        if (thread == null) {
            synchronized (this) {
                if (thread == null) {
                    thread = Thread.ofVirtual().start(() -> {
                        while (true) {
                            lock.lock();
                            try {
                                while (queue.isEmpty()) {
                                    condition.awaitUninterruptibly();
                                }
                            } finally { lock.unlock(); }
                            index = 0;
                            latch = new CountDownLatch(1);
                            publisher.subscribe(this);
                            try { latch.await(); }
                            catch (final InterruptedException _) { }
                        }
                    });
                }
            }
        }
        lock.lock();
        try {
            final var offered = queue.offer(state);
            assert offered;
            condition.signalAll();
        } finally { lock.unlock(); } // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final ReactiveHelloWorldBytePublisher publisher;

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    private final BlockingQueue<State> queue = new LinkedBlockingQueue<>();

    private volatile @Nullable Thread thread;

    private final byte[] array = new byte[HelloWorld.BYTES];

    private int index = 0;

    private volatile @Nullable CountDownLatch latch;
}
