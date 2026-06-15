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

import java.util.*;
import java.util.concurrent.*;

/**
 * A {@link Flow.Publisher} of {@code byte[]} elements — each a freshly assembled,
 * {@value HelloWorld#BYTES}-byte chunk of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, produced by chunking the
 * output of a {@link HelloWorldBytePublisher} into fixed-size arrays.
 * <p>
 * Each {@link #subscribe(Flow.Subscriber) subscribe} call allocates a dedicated, per-subscription
 * outer {@link SubmissionPublisher} and starts a single virtual thread that drives the chunking
 * loop. On each iteration the thread subscribes to the shared {@link HelloWorldBytePublisher}; the
 * inner {@link Flow.Subscriber} {@code request}s {@value HelloWorld#BYTES} bytes up front and
 * stashes each into a fresh {@code byte[HelloWorld.BYTES]}; the thread then awaits inner
 * {@code onComplete} via a {@link CompletableFuture} and
 * {@linkplain SubmissionPublisher#submit(Object) submits} the assembled chunk to the outer
 * publisher — parking on the outer publisher's backpressure if the subscriber's buffer is full.
 * <p>
 * <strong>Why a dedicated virtual thread?</strong> {@link HelloWorldBytePublisher} delivers
 * {@code onComplete} on its executor's thread (default {@link ForkJoinPool#commonPool()}); if we
 * parked on outer-side backpressure from inside that callback we would tie up that executor. By
 * coordinating on a per-subscription virtual thread, the parking happens on a cheap virtual thread
 * and the inner Subscriber callbacks stay non-blocking — they only stash bytes and trip a future.
 * <p>
 * <strong>Lifecycle.</strong> The producer thread loops while the outer
 * {@link SubmissionPublisher} reports
 * {@link SubmissionPublisher#hasSubscribers() hasSubscribers()}. Downstream
 * {@link Flow.Subscription#cancel() cancel} removes the subscriber; the next loop iteration
 * observes the change, calls {@link SubmissionPublisher#close() close}, and the virtual thread
 * exits. No external close is required, and this class deliberately does <em>not</em> implement
 * {@link AutoCloseable}: its lifetime is bounded by its subscriber, not by a synchronous scope.
 * <p>
 * <strong>Threading.</strong> Each subscription owns one virtual thread named
 * {@code "hello-world-chunk-producer"}. Virtual threads are always daemon, so an abandoned
 * subscriber cannot block JVM shutdown. The outer {@link SubmissionPublisher} defaults are used —
 * dispatch on {@link ForkJoinPool#commonPool()} with {@link Flow#defaultBufferSize()}
 * per-subscriber buffer.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldArrayPublisher
 * @see HelloWorldBytePublisher
 */
public class HelloWorldChunkPublisher implements Flow.Publisher<byte[]> {

    /**
     * Creates a new instance backed by the specified service.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                {@value HelloWorld#BYTES}-byte payload via
     *                {@link HelloWorld#set(byte[]) set(array)}; must not be {@code null}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public HelloWorldChunkPublisher(final HelloWorld service) {
        super();
        upstream = new HelloWorldBytePublisher(
                Objects.requireNonNull(service, "service is null")
        );
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param subscriber the subscriber to register; must not be {@code null}.
     * @throws NullPointerException if the {@code subscriber} is {@code null}.
     * @implSpec Allocates a fresh per-subscription outer {@link SubmissionPublisher}, registers the
     * subscriber with it (firing {@link Flow.Subscriber#onSubscribe(Flow.Subscription) onSubscribe}
     * synchronously on the caller's thread), and starts a dedicated virtual thread that loops while
     * the outer publisher has the subscriber attached. Each iteration subscribes a non-blocking
     * sink to {@link #upstream} which {@code request}s {@value HelloWorld#BYTES} bytes up front,
     * fills a fresh {@code byte[HelloWorld.BYTES]} on {@code onNext}, and completes a
     * {@link CompletableFuture} on {@code onComplete}/{@code onError}; the producer thread then
     * {@link CompletableFuture#join() join}s the future and
     * {@link SubmissionPublisher#submit(Object) submits} the array, parking on backpressure as
     * needed. On inner failure, the cause is routed via
     * {@link SubmissionPublisher#closeExceptionally(Throwable) closeExceptionally(t)}.
     */
    @SuppressWarnings("resource")  // closed by the producer thread, not by a synchronous scope
    @Override
    public void subscribe(final Flow.Subscriber<? super byte[]> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        final var publisher = new SubmissionPublisher<byte[]>();
        publisher.subscribe(subscriber);
        Thread.ofVirtual().name("hello-world-chunk-producer").start(() -> {
            while (publisher.hasSubscribers()) {
                final var done = new CompletableFuture<byte[]>();
                upstream.subscribe(new Flow.Subscriber<>() { // @formatter:off
                    private final byte[] array = new byte[HelloWorld.BYTES];
                    private int index;
                    @Override
                    public void onSubscribe(final Flow.Subscription s) {
                        s.request(HelloWorld.BYTES);
                    }
                    @Override
                    public void onNext(final Byte b) {
                        array[index++] = b;
                    }
                    @Override
                    public void onError(final Throwable t) {
                        done.completeExceptionally(t);
                    }
                    @Override
                    public void onComplete() {
                        done.complete(array);
                    }
                }); // @formatter:on
                final byte[] array;
                try {
                    array = done.join();
                } catch (final CompletionException ce) {
                    publisher.closeExceptionally(
                            Optional.ofNullable(ce.getCause()).orElse(ce)
                    );
                    return;
                }
                publisher.submit(array);
            }
            publisher.close();
        });
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorldBytePublisher upstream;
}
