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

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.jinahya.hello.api.HelloWorldBookUtils.loggingProxy;
import static com.github.jinahya.hello.api.HelloWorldBookUtils.loggingSubscriber;
import static com.github.jinahya.hello.api.ReactiveHelloWorldPublisherUtils.addDemand;

/**
 * A package-private {@link Publisher} of {@code byte[]} elements — each a freshly assembled,
 * {@value HelloWorld#BYTES}-byte snapshot of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
 * <p>
 * Unlike {@link ReactiveHelloWorldBytePublisher} which sources from a {@link HelloWorld} directly,
 * this publisher is composed on top of another {@link Publisher} of {@link Byte} elements. For each
 * unit of downstream demand, the producer virtual thread <em>sequentially</em> opens a fresh
 * subscription to the upstream byte publisher, accumulates {@value HelloWorld#BYTES} bytes into a
 * {@code byte[]}, and — once the upstream {@code onComplete}s — emits the assembled array
 * downstream via {@code onNext}.
 * <p>
 * <strong>Threading.</strong> Each {@link #subscribe(Subscriber) subscribe} call allocates fresh
 * per-subscriber state (demand counter, terminated flag, lock) and starts a single dedicated
 * producer <em>virtual</em> thread that parks on demand. Virtual threads are always daemon threads,
 * so an abandoned subscriber cannot block JVM shutdown. Upstream subscriptions run sequentially —
 * one per demand unit — never concurrently.
 * <p>
 * <strong>Signal serialization (Rules 1.3 / 1.7).</strong> The producer virtual thread is the sole
 * sender of downstream signals, so signals are naturally serialized (<a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.3">Rule
 * 1.3</a>). Downstream {@code onNext}/{@code onError} sites fire <em>outside</em> the internal
 * {@link ReentrantLock} (the lock guards only the demand/condition pair). Terminal sites CAS the
 * {@code terminated} flag, satisfying <a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.7">Rule
 * 1.7</a> — at most one terminal ever fires.
 * <p>
 * <strong>Lifetime.</strong> The stream is open-ended — it does not naturally complete; downstream
 * {@code cancel()} stops emission without a terminal signal (Rule 3.12).
 * <p>
 * <strong>Didactic scope.</strong> This class is written to <em>introduce</em> the Reactive
 * Streams workflow, not to be a hardened implementation. {@code request(n &le; 0)} is guarded by an
 * {@code assert} rather than routed to {@code onError}, and exceptions thrown by upstream signals
 * or by the downstream subscriber are <em>not</em> caught — they propagate out of the producer
 * thread. A production-grade publisher would handle both as terminal {@code onError} signals.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldBytePublisher
 */
final class ReactiveHelloWorldArrayPublisher implements Publisher<byte[]> {

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
        Objects.requireNonNull(subscriber, "subscriber is null");
        final var demand = new AtomicLong();
        final var terminated = new AtomicBoolean();
        final var lock = new ReentrantLock();
        final var condition = lock.newCondition();
        subscriber.onSubscribe(loggingProxy(Subscription.class, new Subscription() {
            @Override public void request(final long n) {
                if (terminated.get()) { return; }
                addDemand(demand, n);
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
                if (terminated.get()) { return; }
                assert demand.get() > 0L;
                demand.decrementAndGet();
                final var array = new byte[HelloWorld.BYTES];
                final var index = new AtomicInteger();
                final var error = new AtomicReference<Throwable>();
                final var latch = new CountDownLatch(1);
                publisher.subscribe(loggingSubscriber(new Subscriber<>() {
                    @Override public String toString() {
                        return super.toString().substring(getClass().getPackageName().length() + 1);
                    }
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
                }));
                try { latch.await(); } catch (final InterruptedException _) {
                    Thread.currentThread().interrupt();
                    return;
                }
                final var t = error.get();
                if (t != null) {
                    if (terminated.compareAndSet(false, true)) {
                        subscriber.onError(t);
                    }
                    return;
                }
                subscriber.onNext(array);
            }
        }); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final ReactiveHelloWorldBytePublisher publisher;
}
