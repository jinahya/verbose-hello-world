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

import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * A {@link Flow.Publisher} of {@code byte[]} elements — each a freshly assembled,
 * {@value HelloWorld#BYTES}-byte snapshot of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, sourced directly from a
 * {@link HelloWorld} service and multicast through a wrapped {@link SubmissionPublisher}.
 * <p>
 * On the first {@link #subscribe(Flow.Subscriber) subscribe}, a single producer task is lazily
 * dispatched on the
 * {@linkplain #HelloWorldArrayPublisher(HelloWorld, Executor, int, BiConsumer) caller-supplied
 * executor}. The task loops until {@link #close() closed}: each iteration calls
 * {@link HelloWorldUtils#array(HelloWorld) HelloWorldUtils.array(service)} to obtain a fresh
 * {@code byte[]} and
 * {@linkplain SubmissionPublisher#offer(Object, long, TimeUnit, BiPredicate) offers} it through the
 * inner {@link SubmissionPublisher} (multicast — every downstream subscriber sees every emitted
 * array). If {@link HelloWorld#set(byte[]) service.set(...)} throws, the producer calls
 * {@link SubmissionPublisher#closeExceptionally(Throwable) inner.closeExceptionally(t)}, which is
 * dispatched to all current subscribers as {@code onError(t)} (Rule 1.4).
 * <p>
 * The class mirrors {@link SubmissionPublisher}'s constructor flavors:
 * <ul>
 *   <li>{@link #HelloWorldArrayPublisher(HelloWorld) (service)} — all defaults
 *       ({@link ForkJoinPool#commonPool()}, {@link Flow#defaultBufferSize()}, no error
 *       handler).</li>
 *   <li>{@link #HelloWorldArrayPublisher(HelloWorld, Executor, int) (service, executor,
 *       maxBufferCapacity)} — explicit executor and buffer; no error handler.</li>
 *   <li>{@link #HelloWorldArrayPublisher(HelloWorld, Executor, int, BiConsumer) (service,
 *       executor, maxBufferCapacity, handler)} — full control.</li>
 * </ul>
 * The given {@code executor} is used for <em>both</em> the producer task and the inner
 * {@link SubmissionPublisher}'s dispatch, so it must be able to make progress with at least one
 * task pinned by the producer plus one task per active subscriber. The lifecycle of the
 * {@code executor} is the caller's responsibility — this class does not close it.
 * <p>
 * Spec compliance:
 * <ul>
 *   <li><strong>Rule 1.4</strong> (failure → {@code onError}) is honoured: any {@link Throwable}
 *       from {@code service.set(...)} terminates the producer and propagates downstream via
 *       {@link SubmissionPublisher#closeExceptionally(Throwable)}.</li>
 *   <li><strong>Rule 1.9</strong> (NPE on {@code null} subscriber) is enforced locally.</li>
 *   <li><strong>Rule 1.10 / 1.11</strong> (multi-subscribe) follow from the shared multicast
 *       inner; note that late subscribers see the stream from <em>their</em> subscription point,
 *       not from the first emitted array.</li>
 *   <li><strong>Rules 1.1, 1.3, 1.5, 1.6, 1.7, 1.8</strong> and Rule 2.13 (subscriber throwing
 *       in {@code onSubscribe}) are delegated to {@link SubmissionPublisher}.</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldBytePublisher
 */
public class HelloWorldArrayPublisher implements Flow.Publisher<byte[]>, AutoCloseable {

    /**
     * Creates a new instance with full control over the inner {@link SubmissionPublisher}'s
     * configuration.
     *
     * @param service           the {@link HelloWorld} service; must not be {@code null}.
     * @param executor          the {@link Executor} used for both the producer task and the inner
     *                          {@link SubmissionPublisher}'s dispatch; must not be {@code null}.
     * @param maxBufferCapacity the inner {@link SubmissionPublisher}'s maximum buffer capacity per
     *                          subscriber; must be positive.
     * @param handler           if non-{@code null}, the procedure invoked when a subscriber's
     *                          {@code onNext} throws — see
     *                          {@link SubmissionPublisher#SubmissionPublisher(Executor, int,
     *                          BiConsumer) the inner constructor}.
     * @throws NullPointerException     if {@code service} or {@code executor} is {@code null}.
     * @throws IllegalArgumentException if {@code maxBufferCapacity} is non-positive.
     */
    public HelloWorldArrayPublisher(
            final HelloWorld service,
            final Executor executor,
            final int maxBufferCapacity,
            final @Nullable BiConsumer<
                    ? super Flow.Subscriber<? super byte[]>,
                    ? super Throwable> handler) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.executor = Objects.requireNonNull(executor, "executor is null");
        this.inner = new SubmissionPublisher<>(executor, maxBufferCapacity, handler);
    }

    /**
     * Creates a new instance with the specified executor and buffer capacity, and no error
     * handler.
     *
     * @param service           the {@link HelloWorld} service; must not be {@code null}.
     * @param executor          the {@link Executor} used for both the producer task and the inner
     *                          {@link SubmissionPublisher}'s dispatch; must not be {@code null}.
     * @param maxBufferCapacity the inner {@link SubmissionPublisher}'s maximum buffer capacity per
     *                          subscriber; must be positive.
     * @throws NullPointerException     if {@code service} or {@code executor} is {@code null}.
     * @throws IllegalArgumentException if {@code maxBufferCapacity} is non-positive.
     */
    public HelloWorldArrayPublisher(final HelloWorld service,
                                    final Executor executor,
                                    final int maxBufferCapacity) {
        this(service, executor, maxBufferCapacity, null);
    }

    /**
     * Creates a new instance with all defaults: {@link ForkJoinPool#commonPool()} executor,
     * {@link Flow#defaultBufferSize()} buffer capacity, and no error handler.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                {@value HelloWorld#BYTES}-byte payload via
     *                {@link HelloWorld#set(byte[]) set(array)}; must not be {@code null}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public HelloWorldArrayPublisher(final HelloWorld service) {
        this(service, ForkJoinPool.commonPool(), Flow.defaultBufferSize());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param subscriber the subscriber to register; must not be {@code null}.
     * @throws NullPointerException if the {@code subscriber} is {@code null}.
     * @implSpec Registers the subscriber with the shared inner {@link SubmissionPublisher} and, on
     * the first call only (CAS-guarded), dispatches a producer task on {@link #executor} that
     * offers fresh arrays into the inner until {@link #close()} is invoked or an upstream failure
     * occurs. See the class-level javadoc for full lifecycle and spec-compliance notes.
     */
    @Override
    public void subscribe(final Flow.Subscriber<? super byte[]> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        inner.subscribe(subscriber);
        if (started.compareAndSet(false, true)) {
            executor.execute(() -> {
                while (!inner.isClosed()) {
                    try {
                        inner.offer(
                                HelloWorldUtils.array(service), // <item>
                                100,                            // <timeout>
                                TimeUnit.MILLISECONDS,          // <unit>
                                (s, i) -> false                 // <onDrop>
                        );
                    } catch (final IllegalStateException ise) {
                        assert inner.isClosed();
                        return;
                    } catch (final Throwable t) {
                        inner.closeExceptionally(t);
                        return;
                    }
                }
            });
        }
    }

    /**
     * Closes the inner {@link SubmissionPublisher} — propagating {@code onComplete} to all current
     * subscribers — and signals the producer task to exit (it observes
     * {@link SubmissionPublisher#isClosed() isClosed()} within at most 100 ms).
     */
    @Override
    public void close() {
        inner.close();
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;

    private final Executor executor;

    private final SubmissionPublisher<byte[]> inner;

    private final AtomicBoolean started = new AtomicBoolean();
}
