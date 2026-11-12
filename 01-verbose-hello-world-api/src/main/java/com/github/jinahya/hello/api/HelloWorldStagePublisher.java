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
 * {@value HelloWorld#BYTES}-byte snapshot of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, obtained from the
 * {@link CompletionStage} returned by an {@link AsynchronousHelloWorld}.
 * <p>
 * This class emits the same stream as {@link HelloWorldArrayPublisher}, but it does not call
 * {@link HelloWorld#set(byte[]) set(array)} on the producer thread. For each array it calls
 * {@link AsynchronousHelloWorld#applyAsync(java.util.function.Function) applyAsync(mapper)}, which
 * runs {@link HelloWorld#set(byte[]) set(array)} on the instance's dispatch strategy (for example,
 * the executor of an {@link ExecutorHelloWorld}).
 * <p>
 * <strong>Window.</strong> The producer keeps up to {@code window} stages in flight, so that many
 * {@link HelloWorld#set(byte[]) set(array)} calls may run at the same time on the dispatch
 * strategy. The stages are kept in a queue in the order they were started, and the producer waits
 * for the oldest one, submits its array, and starts a new stage to refill the window. The arrays
 * are therefore submitted in order, and while the producer parks on the
 * {@link SubmissionPublisher}'s backpressure no new stage is started, so at most {@code window}
 * stages are ever pending. A window of {@code 1} runs one {@link HelloWorld#set(byte[]) set(array)}
 * at a time.
 * <p>
 * <strong>Lifecycle.</strong> Each {@link #subscribe(Flow.Subscriber) subscribe} call allocates a
 * dedicated, per-subscription {@link SubmissionPublisher} and starts a single virtual thread that
 * loops while the {@link SubmissionPublisher} reports
 * {@link SubmissionPublisher#hasSubscribers() hasSubscribers()}. Downstream
 * {@link Flow.Subscription#cancel() cancel} removes the subscriber; the next loop iteration
 * observes the change, calls {@link SubmissionPublisher#close() close}, and the virtual thread
 * exits; the stages still in the window are
 * {@linkplain CompletableFuture#cancel(boolean) cancelled}. No external close is required, and this
 * class deliberately does <em>not</em> implement {@link AutoCloseable}: its lifetime is bounded by
 * its subscriber, not by a synchronous scope.
 * <p>
 * <strong>Threading.</strong> Each subscription owns one virtual thread named
 * {@code "hello-world-stage-producer"}, which parks on the oldest stage in the window and on the
 * {@link SubmissionPublisher}'s backpressure. The {@link HelloWorld#set(byte[]) set(array)} calls
 * run on the dispatch strategy of the {@link AsynchronousHelloWorld}. The
 * {@link SubmissionPublisher} defaults are used — dispatch on {@link ForkJoinPool#commonPool()}
 * with {@link Flow#defaultBufferSize()} per-subscriber buffer.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldArrayPublisher
 * @see AsynchronousHelloWorld
 */
public class HelloWorldStagePublisher implements Flow.Publisher<byte[]> {

    /**
     * Creates a new instance backed by the specified asynchronous service and window.
     *
     * @param service the {@link AsynchronousHelloWorld} whose
     *                {@link AsynchronousHelloWorld#applyAsync(java.util.function.Function)
     *                applyAsync(mapper)} produces each {@value HelloWorld#BYTES}-byte payload; must
     *                not be {@code null}.
     * @param window  the maximum number of stages in flight per subscription; must be positive.
     * @throws NullPointerException     if the {@code service} is {@code null}.
     * @throws IllegalArgumentException if the {@code window} is not positive.
     */
    public HelloWorldStagePublisher(final AsynchronousHelloWorld<?> service, final int window) {
        super();
        if (window <= 0) {
            throw new IllegalArgumentException("window(" + window + ") is not positive");
        }
        this.service = Objects.requireNonNull(service, "service is null");
        this.window = window;
    }

    /**
     * Creates a new instance backed by the specified asynchronous service, with a window of
     * {@link Runtime#availableProcessors()}.
     *
     * @param service the {@link AsynchronousHelloWorld} whose
     *                {@link AsynchronousHelloWorld#applyAsync(java.util.function.Function)
     *                applyAsync(mapper)} produces each {@value HelloWorld#BYTES}-byte payload; must
     *                not be {@code null}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public HelloWorldStagePublisher(final AsynchronousHelloWorld<?> service) {
        this(service, Runtime.getRuntime().availableProcessors());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @param subscriber the subscriber to register; must not be {@code null}.
     * @throws NullPointerException if the {@code subscriber} is {@code null}.
     * @implSpec Allocates a fresh per-subscription {@link SubmissionPublisher}, registers the
     * subscriber with it (firing {@link Flow.Subscriber#onSubscribe(Flow.Subscription) onSubscribe}
     * synchronously on the caller's thread), and starts a dedicated virtual thread that loops while
     * the {@link SubmissionPublisher} has the subscriber attached. Each iteration fills the window
     * up to {@code window} stages, each started with
     * {@link AsynchronousHelloWorld#applyAsync(java.util.function.Function)
     * service.applyAsync(HelloWorldUtils::array)}, which invokes
     * {@link HelloWorldUtils#array(HelloWorld) HelloWorldUtils.array(service)}, then
     * {@link CompletableFuture#join() join}s the oldest stage and
     * {@link SubmissionPublisher#submit(Object) submits} its array, parking on backpressure as
     * needed. On failure, the cause is routed via
     * {@link SubmissionPublisher#closeExceptionally(Throwable) closeExceptionally(t)}. On exit, the
     * stages left in the window are {@linkplain CompletableFuture#cancel(boolean) cancelled}.
     */
    @SuppressWarnings("resource")  // closed by the producer thread, not by a synchronous scope
    @Override
    public void subscribe(final Flow.Subscriber<? super byte[]> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        final var publisher = new SubmissionPublisher<byte[]>();
        publisher.subscribe(subscriber);
        Thread.ofVirtual().name("hello-world-stage-producer").start(() -> {
            final var stages = new ArrayDeque<CompletableFuture<byte[]>>(window);
            try {
                while (publisher.hasSubscribers()) {
                    while (stages.size() < window) {
                        stages.add(
                                service.applyAsync(HelloWorldUtils::array).toCompletableFuture()
                        );
                    }
                    publisher.submit(stages.remove().join());
                }
                publisher.close();
            } catch (final CompletionException ce) {
                publisher.closeExceptionally(Optional.ofNullable(ce.getCause()).orElse(ce));
            } catch (final Throwable t) {
                publisher.closeExceptionally(t);
            } finally {
                stages.forEach(stage -> stage.cancel(false));
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    private final AsynchronousHelloWorld<?> service;

    /**
     * The maximum number of stages in flight per subscription.
     */
    private final int window;
}
