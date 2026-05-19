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

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;

/**
 * A {@link Flow.Publisher} of individual {@link Byte} elements — one per byte of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, from {@code 'h'} through
 * {@code 'd'}.
 * <p>
 * Each {@link #subscribe(Flow.Subscriber) subscribe(subscriber)} call is fully independent:
 * <ul>
 *   <li>A fresh {@link SubmissionPublisher} (the "inner") is allocated — backed by the
 *       {@linkplain #HelloWorldBytePublisher(HelloWorld, Executor) caller-supplied executor} and
 *       sized to {@value HelloWorld#BYTES} (the exact payload length) — and the given subscriber
 *       is registered with it. Because {@link SubmissionPublisher#subscribe(Flow.Subscriber)
 *       inner.subscribe} runs synchronously on the caller's thread, the subscriber's
 *       {@link Flow.Subscriber#onSubscribe(Flow.Subscription) onSubscribe} fires before this
 *       method returns.</li>
 *   <li>A producer task is then dispatched via the same {@code executor}; it iterates over the
 *       {@link HelloWorldUtils#array(HelloWorld) bytes returned by the service},
 *       {@linkplain SubmissionPublisher#submit(Object) submits} each one through the inner, and
 *       finally {@link SubmissionPublisher#close() closes} the inner — propagating
 *       {@code onNext} × {@value HelloWorld#BYTES} followed by {@code onComplete} to the
 *       subscriber.</li>
 *   <li>If {@link HelloWorld#set(byte[]) service.set(...)} throws, the producer catches the
 *       failure and calls
 *       {@link SubmissionPublisher#closeExceptionally(Throwable) inner.closeExceptionally(t)},
 *       which is dispatched to the subscriber as {@code onError(t)}. The success-path
 *       {@code close()} and the failure-path {@code closeExceptionally(...)} are mutually
 *       exclusive — exactly one terminal signal fires (Rule 1.7).</li>
 * </ul>
 * <p>
 * Executor:
 * <ul>
 *   <li>The same executor is used for both the producer task <em>and</em>
 *       {@link SubmissionPublisher}'s internal dispatch — so it must be able to make progress
 *       even with both running.</li>
 *   <li>For this class specifically — a bounded payload of {@value HelloWorld#BYTES} bytes
 *       matched by a buffer of the same size — every {@code submit(...)} fits without blocking,
 *       so even a single-threaded executor is safe (producer runs to completion, then dispatch
 *       drains).</li>
 *   <li>The default constructor uses {@link ForkJoinPool#commonPool()}, which is the JDK's
 *       general-purpose async pool. Pass any other {@link Executor} to override —
 *       {@link java.util.concurrent.Executors#newVirtualThreadPerTaskExecutor()} is a fine
 *       alternative when you want a fresh virtual thread per task.</li>
 *   <li>Lifecycle of the executor is the caller's responsibility; this class does not close
 *       it.</li>
 * </ul>
 * <p>
 * Spec compliance:
 * <ul>
 *   <li><strong>Rule 1.9</strong> (NPE on {@code null} subscriber) is enforced locally by
 *       {@link Objects#requireNonNull(Object, String)} <em>before</em> any inner state is
 *       allocated.</li>
 *   <li><strong>Rule 1.10 / 1.11</strong> (subscribers may subscribe again; multi-subscribe is
 *       supported) follow from the per-call fresh inner — every subscriber gets its own producer
 *       and sees the full sequence from {@code 'h'}.</li>
 *   <li><strong>Rules 1.1, 1.3, 1.5, 1.6, 1.7, 1.8</strong> (demand respect, signal
 *       serialization, terminal-after-terminal, cancellation) are delegated to
 *       {@link SubmissionPublisher}, which the JDK ships with TCK-verified behaviour.</li>
 *   <li>A {@link Flow.Subscriber Subscriber} that violates Rule 2.13 by throwing from
 *       {@code onSubscribe} is handled by {@link SubmissionPublisher} itself: the throwable is
 *       caught internally and re-surfaced as {@code onError(t)} on the same subscriber. This
 *       class does <em>not</em> need extra defensive logic for that case.</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldArrayPublisher
 */
public class HelloWorldBytePublisher implements Flow.Publisher<Byte> {

    /**
     * Creates a new instance backed by the specified service, dispatching the producer task and the
     * inner {@link SubmissionPublisher}'s consumer-side signals on the specified executor.
     *
     * @param service  the {@link HelloWorld} service that produces the
     *                 {@value HelloWorld#BYTES}-byte payload via
     *                 {@link HelloWorld#set(byte[]) set(array)}; must not be {@code null}.
     * @param executor the {@link Executor} used for both the producer task and the inner
     *                 {@link SubmissionPublisher}'s dispatch; must not be {@code null}, and its
     *                 lifecycle is the caller's responsibility.
     * @throws NullPointerException if either argument is {@code null}.
     */
    public HelloWorldBytePublisher(final HelloWorld service, final Executor executor) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.executor = Objects.requireNonNull(executor, "executor is null");
    }

    /**
     * Creates a new instance backed by the specified service, using
     * {@link ForkJoinPool#commonPool()} as the executor.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                {@value HelloWorld#BYTES}-byte payload via
     *                {@link HelloWorld#set(byte[]) set(array)}; must not be {@code null}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public HelloWorldBytePublisher(final HelloWorld service) {
        this(service, ForkJoinPool.commonPool());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param subscriber the subscriber to register; must not be {@code null}.
     * @throws NullPointerException if the {@code subscriber} is {@code null}.
     * @implSpec Allocates a fresh per-subscribe {@link SubmissionPublisher} backed by
     * {@link #executor} with capacity {@value HelloWorld#BYTES}, registers the subscriber with it
     * (firing {@link Flow.Subscriber#onSubscribe(Flow.Subscription) onSubscribe} synchronously on
     * the caller's thread), and dispatches a producer task on {@link #executor} that submits
     * exactly {@value HelloWorld#BYTES} bytes followed by {@code onComplete} (success) or
     * {@code onError(t)} (if {@link HelloWorld#set(byte[]) service.set(...)} throws). See the
     * class-level javadoc for full lifecycle and spec-compliance notes.
     */
    @Override
    public void subscribe(final Flow.Subscriber<? super Byte> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        final var inner = new SubmissionPublisher<Byte>(executor, HelloWorld.BYTES);
        inner.subscribe(subscriber);
        executor.execute(() -> {
            try {
                for (final var b : HelloWorldUtils.array(service)) {
                    inner.submit(b);
                }
                inner.close();
            } catch (final Throwable t) {
                inner.closeExceptionally(t);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;

    private final Executor executor;
}
