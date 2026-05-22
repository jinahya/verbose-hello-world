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
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * A {@link Flow.Publisher} of {@code byte[]} elements — each a freshly assembled,
 * {@value HelloWorld#BYTES}-byte snapshot of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, produced directly by a
 * {@link HelloWorld} service.
 * <p>
 * Each {@link #subscribe(Flow.Subscriber) subscribe} call allocates a dedicated, per-subscription
 * {@link SubmissionPublisher} and starts a single virtual thread that feeds it. The wrapped
 * {@link SubmissionPublisher} handles all Reactive-Streams-spec machinery (demand accounting,
 * signal serialization, cancel semantics, {@code request(n &le; 0)} validation, terminal
 * deduplication); this class contributes only the producer loop. Because each subscription owns its
 * own {@link SubmissionPublisher} and producer thread, there is no shared state and no multicast —
 * subscribers are fully isolated.
 * <p>
 * <strong>Lifecycle.</strong> The producer thread loops while the per-subscription
 * {@link SubmissionPublisher} reports
 * {@link SubmissionPublisher#hasSubscribers() hasSubscribers()}. Downstream
 * {@link Flow.Subscription#cancel() cancel} removes the subscriber from the
 * {@link SubmissionPublisher}; the next iteration of the producer loop observes the change, calls
 * {@link SubmissionPublisher#close() close}, and the virtual thread exits. No external close is
 * required, and this class deliberately does <em>not</em> implement {@link AutoCloseable}: its
 * lifetime is bounded by its subscriber, not by a synchronous scope.
 * <p>
 * <strong>Threading.</strong> Each subscription owns one virtual thread named
 * {@code "hello-world-array-producer"}. Virtual threads are always daemon, so an abandoned
 * subscriber cannot block JVM shutdown. The {@link SubmissionPublisher} defaults are used —
 * dispatch on {@link java.util.concurrent.ForkJoinPool#commonPool()} with
 * {@link Flow#defaultBufferSize()} per-subscriber buffer.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldBytePublisher
 */
public class HelloWorldArrayPublisher implements Flow.Publisher<byte[]> {

    /**
     * Creates a new instance backed by the specified service.
     *
     * @param service the {@link HelloWorld} service that produces the
     *                {@value HelloWorld#BYTES}-byte payload via
     *                {@link HelloWorld#set(byte[]) set(array)}; must not be {@code null}.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    public HelloWorldArrayPublisher(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
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
     * the {@link SubmissionPublisher} has the subscriber attached, on each iteration submitting a
     * fresh {@code byte[HelloWorld.BYTES]} filled via
     * {@link HelloWorld#set(byte[]) service.set(...)}. On
     * {@link HelloWorld#set(byte[]) service.set(...)} throwing, the producer routes the failure via
     * {@link SubmissionPublisher#closeExceptionally(Throwable) closeExceptionally(t)}.
     */
    @SuppressWarnings("resource")  // closed by the producer thread, not by a synchronous scope
    @Override
    public void subscribe(final Flow.Subscriber<? super byte[]> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        final var publisher = new SubmissionPublisher<byte[]>();
        publisher.subscribe(subscriber);
        Thread.ofVirtual().name("hello-world-array-producer").start(() -> {
            try {
                while (publisher.hasSubscribers()) {
                    publisher.submit(service.set(new byte[HelloWorld.BYTES]));
                }
                publisher.close();
            } catch (final Throwable t) {
                publisher.closeExceptionally(t);
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;
}
