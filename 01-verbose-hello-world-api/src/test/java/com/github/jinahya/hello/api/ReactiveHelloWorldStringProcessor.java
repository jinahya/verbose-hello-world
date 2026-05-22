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

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.api.HelloWorldBookUtils.loggingProxy;
import static com.github.jinahya.hello.api.ReactiveHelloWorldPublisherUtils.addDemand;

/**
 * A package-private multicast {@link Processor} that subscribes to an upstream {@code byte[]}
 * publisher (as a {@link Subscriber Subscriber&lt;byte[]&gt;}) and republishes each upstream
 * {@code byte[]} downstream as a {@link StandardCharsets#US_ASCII US-ASCII} {@link String} (as a
 * {@link Publisher Publisher&lt;String&gt;}).
 * <p>
 * Three concurrent moving parts:
 * <ol>
 *   <li>A single shared <strong>outer</strong> platform thread, started eagerly in the
 *       constructor, that loops: waits on {@link #lock} / {@link #condition} until
 *       {@link #ready} is observed {@code true} (and CAS-flipped back to {@code false}) &rarr;
 *       {@code subscription.request(1L)} on the upstream &rarr; acquires {@link #semaphore},
 *       blocking until {@link #onNext(byte[]) onNext} (or terminal {@code onError} /
 *       {@code onComplete}) releases it &rarr; loops.</li>
 *   <li>One <strong>worker</strong> platform thread per downstream subscriber (the per-subscriber
 *       {@link State}). The worker waits until it has {@code demand > 0} and its per-state queue
 *       is non-empty, then decrements demand, polls one {@code String} and delivers it to
 *       {@code downstream}; if it still has demand after the delivery, it sets {@link #ready}
 *       and signals the outer so the next upstream fetch can run.</li>
 *   <li>The producer thread <em>inside</em> the upstream publisher (one per fetch).</li>
 * </ol>
 * {@link #onNext(byte[]) onNext} encodes the upstream {@code byte[]} as US-ASCII and offers the
 * resulting {@link String} to every active subscriber's per-state queue, waking each waiting
 * worker, then releases {@link #semaphore} so the outer can issue the next {@code request(1L)}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldByteProcessor
 */
final class ReactiveHelloWorldStringProcessor implements Processor<byte[], String>, AutoCloseable {

    private static final class State implements Runnable {

        State(final Subscriber<? super String> downstream, final ReentrantLock lock,
              final Condition outerCondition, final AtomicBoolean outerReady) {
            super();
            this.downstream = downstream;
            this.lock = lock;
            this.outerCondition = outerCondition;
            this.outerReady = outerReady;
            this.innerCondition = lock.newCondition();
        }

        private void signal() { // @formatter:off
            lock.lock();
            try {
                innerCondition.signalAll();
            } finally {
                lock.unlock();
            } // @formatter:on
        }

        @Override
        public void run() { // @formatter:off
            while (true) {
                lock.lock();
                try {
                    while (!terminated.get() && (demand.get() == 0L || queue.isEmpty())) {
                        innerCondition.awaitUninterruptibly();
                    }
                } finally { lock.unlock(); }
                if (terminated.get()) { return; }
                assert demand.get() > 0L && !queue.isEmpty();
                demand.decrementAndGet();
                final var polled = queue.poll();
                assert polled != null;
                downstream.onNext(polled);
                if (!terminated.get() && demand.get() > 0L) {
                    outerReady.set(true);
                    lock.lock();
                    try { outerCondition.signalAll(); } finally { lock.unlock(); }
                }
            } // @formatter:on
        }

        // ---------------------------------------------------------------------------------------------
        private final Subscriber<? super String> downstream;

        private final ReentrantLock lock;

        private final Condition outerCondition;

        private final AtomicBoolean outerReady;

        private final Condition innerCondition;

        private final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);

        private final AtomicLong demand = new AtomicLong();

        private final AtomicBoolean terminated = new AtomicBoolean();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance backed by the specified upstream
     * {@link ReactiveHelloWorldArrayPublisher}.
     *
     * @param publisher the upstream {@link ReactiveHelloWorldArrayPublisher} that supplies a
     *                  {@code byte[]} payload per fetch.
     * @throws NullPointerException if the {@code publisher} is {@code null}.
     */
    ReactiveHelloWorldStringProcessor(final ReactiveHelloWorldArrayPublisher publisher) { // @formatter:off
        super();
        this.publisher = Objects.requireNonNull(publisher, "publisher is null");
        this.publisher.subscribe(this);
        assert subscription != null;
        Thread.ofPlatform().start(() -> {
            while (!closed.get() && !terminated.get()) {
                lock.lock();
                try {
                    while (!closed.get() && !terminated.get()
                            && !ready.compareAndSet(true, false)) {
                        condition.awaitUninterruptibly();
                    }
                } finally { lock.unlock(); }
                if (closed.get() || terminated.get()) { return; }
                subscription.request(1L);
                try { semaphore.acquire(); }
                catch (final InterruptedException _) {
                    Thread.currentThread().interrupt(); return;
                }
            }
        }); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onSubscribe(final Subscription s) {
        subscription = s;
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onNext(final byte[] t) { // @formatter:off
        final var encoded = new String(t, StandardCharsets.US_ASCII);
        for (final var state : states) {
            if (state.terminated.get()) { continue; }
            if (state.queue.offer(encoded)) { state.signal(); }
        }
        semaphore.release();                          // unblock outer for the next request(1)
        // @formatter:on
    }

    @Override
    public void onError(final Throwable t) {
        terminated.set(true);
        semaphore.release();
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onComplete() {
        terminated.set(true);
        semaphore.release();
        lock.lock();
        try {
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException  if the {@code s} is {@code null}.
     * @throws IllegalStateException if this processor is already {@linkplain #close() closed}.
     * @implSpec This method constructs a fresh {@link State}, adds it to {@link #states}, starts
     * the state as a per-subscriber worker platform thread, and then calls
     * {@code s.onSubscribe(...)} on the calling thread before returning. The worker (i.e.
     * {@link State#run()}) waits until it has {@code demand > 0} and its queue is non-empty, then
     * delivers one {@code String} to {@code s} and loops. The shared outer thread that fetches from
     * upstream is started once by the constructor, not here.
     */
    @Override
    public void subscribe(final Subscriber<? super String> s) { // @formatter:off
        Objects.requireNonNull(s, "s is null");
        if (closed.get()) {
            throw new IllegalStateException("processor is closed");
        }
        final var state = new State(s, lock, condition, ready);
        states.add(state);
        Thread.ofPlatform().start(state);
        s.onSubscribe(loggingProxy(Subscription.class, new Subscription() {
            @Override public void request(final long n) {
                if (state.terminated.get()) { return; }
                addDemand(state.demand, n);
                state.signal();
                ready.set(true);
                lock.lock();
                try { condition.signalAll(); } finally { lock.unlock(); }
            }
            @Override public void cancel() {
                state.terminated.set(true);
                states.remove(state);
                state.signal();
            }
        })); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void close() { // @formatter:off
        if (!closed.compareAndSet(false, true)) { return; }
        subscription.cancel();                          // stop upstream
        // wake the outer (whichever wait it's in)
        lock.lock();
        try { condition.signalAll(); } finally { lock.unlock(); }
        semaphore.release();
        // terminate all per-subscriber workers
        for (final var state : states) {
            state.terminated.set(true);
            state.signal();
        }
        states.clear(); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final ReactiveHelloWorldArrayPublisher publisher;

    private volatile Subscription subscription;

    private final AtomicBoolean terminated = new AtomicBoolean();

    // ---------------------------------------------------------------------------------------------
    private final ReentrantLock lock = new ReentrantLock();

    private final Condition condition = lock.newCondition();

    private final AtomicBoolean ready = new AtomicBoolean();

    private final List<State> states = new CopyOnWriteArrayList<>();

    private final Semaphore semaphore = new Semaphore(0);

    // ---------------------------------------------------------------------------------------------
    private final AtomicBoolean closed = new AtomicBoolean();
}
