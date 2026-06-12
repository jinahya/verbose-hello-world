package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import org.jspecify.annotations.*;
import org.reactivestreams.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

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
 *   <li>A lazily-started <strong>outer</strong> platform thread (DCL-guarded on {@link #thread})
 *       that loops:
 *       <ol>
 *         <li>waits on {@link #lock} / {@link #condition} until the shared state queue
 *             ({@link #queue}) is non-empty (i.e. at least one downstream subscriber has
 *             registered),</li>
 *         <li>resets the cycle buffer index ({@link #index} = 0), then
 *             {@code publisher.subscribe(this)} — which synchronously calls
 *             {@link #onSubscribe(Subscription) this.onSubscribe} (where {@code request(12)}
 *             happens),</li>
 *         <li>acquires the cycle semaphore ({@link #semaphore}), blocking until
 *             {@link #onComplete()} releases it at the end of the 12-byte cycle,</li>
 *         <li>loops.</li>
 *       </ol>
 *   </li>
 *   <li>One <strong>worker</strong> platform thread per downstream subscriber, started by
 *       {@link #subscribe(Subscriber)}. The worker loops on its own per-subscriber
 *       {@link BlockingQueue}{@code <Byte>} (capacity 12), delivering bytes as
 *       {@code request(n)} demands, and calls {@code downstream.onComplete()} when the queue
 *       drains.</li>
 *   <li>The producer thread <em>inside</em> the byte publisher (one per cycle) — emits the
 *       12 bytes and self-terminates.</li>
 * </ol>
 * Subscribers who join while a cycle is in flight all share that cycle's 12-byte payload (because
 * {@link #onComplete()} drains the entire state queue at the 12th-{@code onNext} moment, copying
 * the cycle buffer to everyone who joined since the cycle started). Subscribers who join between
 * cycles trigger the next cycle when the outer next wakes from its wait.
 * <p>
 * {@link #subscribe(Subscriber) subscribe} is non-blocking: it constructs a fresh {@code State},
 * calls {@code downstream.onSubscribe(...)} on the calling thread, starts the per-subscriber
 * worker, lazily starts the shared outer thread on the first call, and offers the {@code State}
 * onto the shared state queue before returning. All subsequent {@code onNext} / {@code onComplete}
 * deliveries to the downstream happen on its worker thread.
 * <p>
 * <strong>Spec caveat (Rule 2.12).</strong> Because the outer loops, the same instance
 * ({@code this}) is subscribed to {@link #publisher} more than once across cycles — which spec
 * Rule 2.12 forbids ("a Subscriber must not be subscribed more than once").
 * {@link ReactiveHelloWorldBytePublisher} does not enforce that rule, so it works in practice,
 * but a strict spec-conformant implementation would use a fresh {@code Subscriber<Byte>} per
 * cycle instead.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldBytePublisher
 */
final class ReactiveHelloWorldByteProcessor implements Processor<Byte, Byte>, AutoCloseable {

    private static final class State implements Runnable {

        State(final Subscriber<? super Byte> downstream, final ReentrantLock lock) {
            super();
            this.downstream = downstream;
            this.lock = lock;
            this.condition = lock.newCondition();
        }

        private void signal() { // @formatter:off
            lock.lock();
            try {
                condition.signalAll();
            } finally {
                lock.unlock();
            } // @formatter:on
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
                final var polled = queue.poll();
                assert polled != null;
                downstream.onNext(polled);
            }
            downstream.onComplete(); // @formatter:on
        }

        // ---------------------------------------------------------------------------------------------
        private final Subscriber<? super Byte> downstream;

        private final BlockingQueue<Byte> queue = new ArrayBlockingQueue<>(HelloWorld.BYTES);

        private final AtomicLong demand = new AtomicLong();

        private final AtomicBoolean terminated = new AtomicBoolean();

        private final ReentrantLock lock;

        private final Condition condition;
    }

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorldByteProcessor(final HelloWorld service) {
        super();
        this.publisher = new ReactiveHelloWorldBytePublisher(service);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onSubscribe(final Subscription s) {
        s.request(HelloWorld.BYTES);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onNext(final Byte t) {
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
        semaphore.release(); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the {@code s} is {@code null}.
     * @implSpec This method constructs a fresh {@link State}, calls {@code s.onSubscribe(...)} on
     * the calling thread, starts a per-subscriber worker platform thread, lazily starts the shared
     * outer thread on the first call, and offers the {@code State} onto the shared state queue
     * ({@link #queue}) before returning. The worker loops on its own queue delivering bytes to
     * {@code s} as {@code request(n)} demands, then calls {@code s.onComplete()} when the queue
     * drains.
     */
    @Override
    public void subscribe(final Subscriber<? super Byte> s) { // @formatter:off
        Objects.requireNonNull(s, "s is null");
        final var state = new State(s, lock);
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
        Thread.ofPlatform().start(state);
        if (thread == null) {
            synchronized (this) {
                if (thread == null) {
                    thread = Thread.ofPlatform().start(() -> {
                        while (!closed.get()) {
                            lock.lock();
                            try {
                                while (!closed.get() && queue.isEmpty()) {
                                    condition.awaitUninterruptibly();
                                }
                            } finally { lock.unlock(); }
                            if (closed.get()) { return; }
                            index = 0;
                            publisher.subscribe(this);
                            try { semaphore.acquire(); }
                            catch (final InterruptedException _) {
                                Thread.currentThread().interrupt(); return;
                            }
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
        } finally {
            lock.unlock();
        } // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        semaphore.release();
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // ---------------------------------------------------------------------------------------------
    private final ReactiveHelloWorldBytePublisher publisher;

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    private final BlockingQueue<State> queue = new LinkedBlockingQueue<>();

    private volatile @Nullable Thread thread;

    private final byte[] array = new byte[HelloWorld.BYTES];

    private int index = 0;

    private final Semaphore semaphore = new Semaphore(0);

    private final AtomicBoolean closed = new AtomicBoolean();
}
