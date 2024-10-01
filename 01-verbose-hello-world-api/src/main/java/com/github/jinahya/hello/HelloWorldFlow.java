package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import com.github.jinahya.hello.util._ExcludeFromCoverage_PrivateConstructor_Obviously;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Interrelated classes for establishing flow-controlled <em>hello-world</em> components based on
 * interfaces defined in {@link Flow} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class HelloWorldFlow {

    /**
     * An abstract class for publishing the <em>hello-world-bytes</em>.
     *
     * @param <T> item type parameter.
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    public abstract static class HelloWorldPublisher<T> implements Flow.Publisher<T> {

        /**
         * A publisher publishes each byte of <em>hello-world-bytes</em>.
         *
         * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
         */
        public static class OfByte extends HelloWorldPublisher<Byte> {

            /**
             * Creates a new instance.
             *
             * @param service  a service for the <em>hello-world-bytes</em>.
             * @param executor an executor service for publishing items asynchronously.
             */
            public OfByte(final HelloWorld service, final ExecutorService executor) {
                super(service, executor);
            }

            @Override
            public String toString() {
                return String.format("[byte-publisher@%1$08x]", hashCode());
            }

            @Override
            public void subscribe(final Flow.Subscriber<? super Byte> subscriber) {
                Objects.requireNonNull(subscriber, "subscriber is null");
                log.debug("{}.subscribe({})", this, subscriber); // NOSONAR
                Objects.requireNonNull(subscriber, "subscriber is null");
                final var array = service.set(new byte[HelloWorld.BYTES]);
                final var index = new AtomicInteger(); // index of the next item in the <array>
                final var accumulated = new AtomicLong(); // accumulated <n> of the <request(n)>
                final var lock = new ReentrantLock();
                final var condition = lock.newCondition();
                final var future = executor.submit(() -> {
                    while (!Thread.currentThread().isInterrupted()) {
                        lock.lock();
                        try {
                            while (accumulated.get() == 0L) {
                                try {
                                    condition.await();
                                } catch (final InterruptedException ie) {
                                    log.info("interrupted while awaiting the condition", ie);
                                    Thread.currentThread().interrupt();
                                    break;
                                }
                            }
                            while (accumulated.getAndDecrement() > 0L) {
                                subscriber.onNext(array[index.get()]);
                                if (index.incrementAndGet() == array.length) {
                                    subscriber.onComplete();
                                    Thread.currentThread().interrupt();
                                }
                            }
                        } finally {
                            lock.unlock();
                        }
                    }
                    log.debug("out-of-while-loop: {}", this);
                });
                subscriber.onSubscribe(new Flow.Subscription() { // @formatter:off
                    @Override public String toString() {
                        return String.format("[subscription-for-%1$s]", subscriber);
                    }
                    @Override public void request(final long n) {
                        log.debug("{}.request({})", this, n); // NOSONAR
                        if (n <= 0L) {
                            future.cancel(true);
                            subscriber.onError(new IllegalArgumentException("n(" + n + ") <= 0L"));
                        }
                        if (future.isDone() || future.isCancelled()) {
                            return;
                        }
                        try {
                            lock.lock();
                            if (accumulated.addAndGet(n) < 0L) { // overflowed
                                accumulated.set(Long.MAX_VALUE);
                            }
                            condition.signal();
                        } finally {
                            lock.unlock();
                        }
                    }
                    @Override public void cancel() {
                        log.debug("{}.cancel()", this); // NOSONAR
                        future.cancel(true);
                    } // @formatter:on
                });
            }
        }

        /**
         * A publisher publishes arrays of the <em>hello-world-bytes</em>.
         *
         * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
         */
        public static class OfArray extends HelloWorldPublisher<byte[]> {

            /**
             * Creates a new instance with specified service and executor.
             *
             * @param service  the service for the <em>hello-world-bytes</em>.
             * @param executor the executor for publishing items asynchronously.
             */
            public OfArray(final HelloWorld service, final ExecutorService executor) {
                super(service, executor);
            }

            @Override
            public String toString() {
                return String.format("[array-publisher@%1$08x]", hashCode());
            }

            @Override
            public void subscribe(final Flow.Subscriber<? super byte[]> subscriber) {
                Objects.requireNonNull(subscriber, "subscriber is null");
                log.debug("{}.subscribe({})", this, subscriber);
                final var accumulated = new AtomicLong(); // accumulated <n> from <request(n)>
                final var lock = new ReentrantLock();
                final var condition = lock.newCondition();
                final var publisher = new OfByte(service, executor);
                final var future = executor.submit(() -> {
                    final var thread = Thread.currentThread();
                    while (!thread.isInterrupted()) {
                        lock.lock();
                        try {
                            // await <condition> while <accumulated> is still <zero>
                            while (accumulated.get() == 0L) {
                                try {
                                    condition.await();
                                } catch (final InterruptedException ie) {
                                    thread.interrupt();
                                    return;
                                }
                            }
                        } finally {
                            lock.unlock();
                        }
                        // decrease the <accumulated> by <one>
                        accumulated.decrementAndGet();
                        // subscribe to a byte-publisher, publish an array on its <onComplete()>
                        publisher.subscribe(new HelloWorldSubscriber.OfByte() { // @formatter:off
                            @Override public void onSubscribe(final Flow.Subscription subscription) {
                                super.onSubscribe(subscription);
                                subscription.request(array.length);
                            }
                            @Override public void onNext(final Byte item) {
                                super.onNext(item);
                                array[index++] = item;
                            }
                            @Override public void onError(final Throwable throwable) {
                                super.onError(throwable);
                                subscriber.onError(throwable);
                                thread.interrupt();
                            }
                            @Override public void onComplete() {
                                super.onComplete();
                                assert index == array.length;
                                subscriber.onNext(array);
                            }
                            private final byte[] array = new byte[HelloWorld.BYTES];
                            private int index = 0;
                        }); // @formatter:on
                    }
                    log.debug("out-of-while-loop: {}", this);
                });
                subscriber.onSubscribe(new Flow.Subscription() { // @formatter:off
                    @Override public String toString() {
                        return String.format("[subscription-for-%1$s]", subscriber);
                    }
                    @Override public void request(final long n) {
                        log.debug("{}.request({})", this, n);
                        if (n <= 0L) {
                            future.cancel(true);
                            subscriber.onError(new IllegalArgumentException("n(" + n + ") < 0L"));
                        }
                        if (future.isCancelled() || future.isDone()) {
                            return;
                        }
                        lock.lock();
                        try {
                            if (accumulated.addAndGet(n) < 0L) { // overflow
                                accumulated.set(Long.MAX_VALUE);
                            }
                            condition.signal();
                        } finally {
                            lock.unlock();
                        }
                    }
                    @Override public void cancel() {
                        log.debug("{}.cancel()", this);
                        future.cancel(true);
                    } // @formatter:on
                });
            }
        }

        /**
         * A publisher publishes byte buffers of the <em>hello-world-bytes</em>.
         *
         * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
         */
        public static class OfBuffer extends HelloWorldPublisher<ByteBuffer> {

            /**
             * Creates a new instance with specified service and executor.
             *
             * @param service  the service for the <em>hello-world-bytes</em>.
             * @param executor the executor for publishing items asynchronously.
             */
            public OfBuffer(final HelloWorld service, final ExecutorService executor) {
                super(service, executor);
                publisher = new OfArray(service, executor);
            }

            @Override
            public String toString() {
                return String.format("[buffer-publisher@%1$08x]", hashCode());
            }

            @Override
            public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
                Objects.requireNonNull(subscriber, "subscriber is null");
                log.debug("{}.subscribe({})", this, subscriber);
                final var processor = new Flow.Processor<byte[], ByteBuffer>() { // @formatter:off
                    @Override public String toString() {
                        return String.format("[array-to-buffer-processor@%08x]", hashCode());
                    }
                    @Override
                    public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
                        log.debug("{}.subscribe({})", this, subscriber);
                        subscriber.onSubscribe(new Flow.Subscription() {
                            @Override public String toString() {
                                return String.format("[subscription-for-%1$s@%2$08x]", subscriber,
                                                     hashCode());
                            }
                            @Override public void request(final long n) {
                                log.debug("{}.request({})", this, n);
                                subscription.request(n);
                            }
                            @Override public void cancel() {
                                log.debug("{}.cancel()", this);
                                subscription.cancel();
                            }
                        });
                    }
                    @Override public void onSubscribe(final Flow.Subscription subscription) {
                        log.debug("{}.onSubscribe({})", this, subscription); // NOSONAR
                        this.subscription = subscription;
                    }
                    @Override public void onNext(final byte[] item) {
                        log.debug("{}.onNext({})", this, item); // NOSONAR
                        subscriber.onNext(ByteBuffer.wrap(item));
                    }
                    @Override public void onError(final Throwable throwable) {
                        log.error("{}.onError({})", this, throwable, throwable); // NOSONAR
                        subscriber.onError(throwable);
                    }
                    @Override public void onComplete() {
                        log.debug("{}.onComplete()", this); // NOSONAR
                        subscriber.onComplete();
                    }
                    private Flow.Subscription subscription;
                }; // @formatter:on
                publisher.subscribe(processor);
                processor.subscribe(subscriber);
            }

            private final Flow.Publisher<byte[]> publisher;
        }

        /**
         * A publisher publishes strings of the <em>hello-world-bytes</em>.
         *
         * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
         */
        public static class OfString extends HelloWorldPublisher<String> {

            /**
             * Creates a new instance with specified service and executor.
             *
             * @param service  the service for the <em>hello-world-bytes</em>.
             * @param executor the executor for publishing items asynchronously.
             */
            public OfString(final HelloWorld service, final ExecutorService executor) {
                super(service, executor);
                publisher = new OfBuffer(service, executor);
            }

            @Override
            public String toString() {
                return String.format("[string-publisher@%1$08x]", hashCode());
            }

            @Override
            public void subscribe(final Flow.Subscriber<? super String> subscriber) {
                Objects.requireNonNull(subscriber, "subscriber is null");
                log.debug("{}.subscribe({})", this, subscriber);
                final var processor = new Flow.Processor<ByteBuffer, String>() { // @formatter:off
                    @Override public String toString() {
                        return String.format("[buffer-to-string-processor@%08x]", hashCode());
                    }
                    @Override
                    public void subscribe(final Flow.Subscriber<? super String> subscriber) {
                        log.debug("{}.subscribe({})", this, subscriber);
                        subscriber.onSubscribe(new Flow.Subscription() {
                            @Override public String toString() {
                                return String.format("[subscription-for-%1$s@%2$08x]", subscriber,
                                                     hashCode());
                            }
                            @Override public void request(final long n) {
                                log.debug("{}.request({})", this, n);
                                subscription.request(n);
                            }
                            @Override public void cancel() {
                                log.debug("{}.cancel()", this);
                                subscription.cancel();
                            }
                        });
                    }
                    @Override public void onSubscribe(final Flow.Subscription subscription) {
                        log.debug("{}.onSubscribe({})", this, subscription);
                        this.subscription = subscription;
                    }
                    @Override public void onNext(final ByteBuffer item) {
                        log.debug("{}.onNext({})", this, item);
                        subscriber.onNext(StandardCharsets.US_ASCII.decode(item).toString());
                    }
                    @Override public void onError(final Throwable throwable) {
                        log.error("{}.onError({})", this, throwable, throwable);
                        subscriber.onError(throwable);
                    }
                    @Override public void onComplete() {
                        log.debug("{}.onComplete()", this);
                        subscriber.onComplete();
                    }
                    private Flow.Subscription subscription;
                }; // @formatter:on
                publisher.subscribe(processor);
                processor.subscribe(subscriber);
            }

            private final Flow.Publisher<ByteBuffer> publisher;
        }

        /**
         * Creates a new instance with specified service and executor.
         *
         * @param service  an instance of {@link HelloWorld} interface.
         * @param executor an executor for asynchronously publish items.
         */
        protected HelloWorldPublisher(final HelloWorld service, final ExecutorService executor) {
            super();
            this.service = Objects.requireNonNull(service, "service is null");
            this.executor = Objects.requireNonNull(executor, "executor is null");
        }

        final HelloWorld service;

        final ExecutorService executor;
    }

    /**
     * An abstract class for subscribing to {@link HelloWorldPublisher}.
     *
     * @param <T> item type parameter.
     */
    @Slf4j
    public abstract static class HelloWorldSubscriber<T> implements Flow.Subscriber<T> {

        /**
         * A subscriber for {@link HelloWorldPublisher.OfByte}.
         */
        public static class OfByte extends HelloWorldSubscriber<Byte> {

            /**
             * Creates a new instance.
             */
            public OfByte() {
                super(i -> String.format("0x%1$02x('%2$c')", i, (char) i.byteValue()));
            }

            @Override
            public String toString() {
                return String.format("[byte-subscriber@%1$08x]", hashCode());
            }
        }

        /**
         * A subscriber for {@link HelloWorldPublisher.OfArray}.
         */
        public static class OfArray extends HelloWorldSubscriber<byte[]> {

            /**
             * Creates a new instance.
             */
            public OfArray() {
                super(Arrays::toString);
            }

            @Override
            public String toString() {
                return String.format("[array-subscriber@%1$08x]", hashCode());
            }
        }

        /**
         * A subscriber for {@link HelloWorldPublisher.OfBuffer}.
         */
        public static class OfBuffer extends HelloWorldSubscriber<ByteBuffer> {

            /**
             * Creates a new instance.
             */
            public OfBuffer() {
                super(Objects::toString);
            }

            @Override
            public String toString() {
                return String.format("[buffer-subscriber@%1$08x]", hashCode());
            }
        }

        /**
         * A subscriber for {@link HelloWorldPublisher.OfString}.
         */
        public static class OfString extends HelloWorldSubscriber<String> {

            /**
             * Creates a new instance.
             */
            public OfString() {
                super(v -> {});
            }

            @Override
            public String toString() {
                return String.format("[string-subscriber@%1$08x]", hashCode());
            }
        }

        /**
         * Creates a new instance with specified consumer.
         *
         * @param consumer the consumer accepts each item published via {@link #onNext(Object)}.
         */
        private HelloWorldSubscriber(final Consumer<? super T> consumer) {
            super();
            this.consumer = Objects.requireNonNull(consumer, "consumer is null");
        }

        @Override
        public void onSubscribe(final Flow.Subscription subscription) {
            log.debug("{}.onSubscribe({})", this, subscription);
            if (this.subscription != null) {
                this.subscription.cancel();
            }
            this.subscription = subscription;
        }

        @Override
        public void onNext(final T item) {
            log.debug("{}.onNext({})", this, item);
            consumer.accept(Objects.requireNonNull(item, "item is null"));
        }

        @Override
        public void onError(final Throwable throwable) {
            log.error("{}.onError({})", this, throwable, throwable);
        }

        @Override
        public void onComplete() {
            log.debug("{}.onComplete()", this);
        }

        private final Consumer<? super T> consumer;

        @Accessors(fluent = true)
        @Getter(AccessLevel.PROTECTED)
        private Flow.Subscription subscription;
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private HelloWorldFlow() {
        throw new AssertionError("instantiation is not allowed");
    }
}
