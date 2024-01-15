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
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Interrelated classes for establishing flow-controlled components based on interfaces defined in
 * {@link Flow} class.
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
    public abstract static class HelloWorldPublisher<T> // @formatter:off
            implements Flow.Publisher<T> {
        /**
         * A publisher publishes <em>hello-world-bytes</em>.
         * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
         * */
        public static class OfByte extends HelloWorldPublisher<Byte> {
            /**
             * Creates a new instance.
             * @param service  a service for the <em>hello-world-bytes</em>.
             * @param executor an executor service for publishing items asynchronously.
             */
            public OfByte(final HelloWorld service, final ExecutorService executor) {
                super(service, executor);
            }
            @Override public String toString() {
                return String.format("[byte-publisher@%1$08x]", hashCode());
            }
            @Override public void subscribe(final Flow.Subscriber<? super Byte> subscriber) {
                log.debug("{}.subscribe({})", this, subscriber);
                Objects.requireNonNull(subscriber, "subscriber is null");
                final var buffer = service.put(ByteBuffer.allocate(HelloWorld.BYTES)).limit(0);
                final var latch = new CountDownLatch(1);
                final var future = executor.submit(() -> {
                    try {
                        latch.await();
                    } catch (final InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    while (!Thread.currentThread().isInterrupted()) {
                        synchronized (buffer) {
                            while (!buffer.hasRemaining()) {
                                try {
                                    buffer.wait();
                                } catch (final InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                            subscriber.onNext(buffer.get());
                            if (buffer.position() == buffer.capacity()) {
                                subscriber.onComplete();
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                });
                subscriber.onSubscribe(new Flow.Subscription() {
                    @Override public String toString() {
                        return String.format("[subscription-for-%1$s]", subscriber);
                    }
                    @Override public void request(final long n) {
                        log.debug("{}.request({})", this, n);
                        if (n <= 0L) {
                            future.cancel(true);
                            subscriber.onError(new IllegalArgumentException("n(" + n + ") <= 0L"));
                        }
                        if (future.isDone() || future.isCancelled()) {
                            return;
                        }
                        synchronized (buffer) {
                            buffer.limit((int) Math.min(buffer.capacity(), buffer.limit() + n));
                            buffer.notifyAll();
                        }
                    }
                    @Override public void cancel() {
                        log.debug("{}.cancel()", this);
                        future.cancel(true);
                    }
                });
                latch.countDown();
            }
        }
        /**
         * A publisher publishes arrays of the <em>hello-world-bytes</em>.
         * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
         */
        public static class OfArray extends HelloWorldPublisher<byte[]> {
            /**
             * Creates a new instance with specified service and executor.
             * @param service the service for the <em>hello-world-bytes</em>.
             * @param executor the executor for publishing items asynchronously.
             */
            public OfArray(final HelloWorld service, final ExecutorService executor) {
                super(service, executor);
            }
            @Override public String toString() {
                return String.format("[array-publisher@%1$08x]", hashCode());
            }
            @Override public void subscribe(final Flow.Subscriber<? super byte[]> subscriber) {
                Objects.requireNonNull(subscriber, "subscriber is null");
                final var summed = new AtomicLong();
                final var lock = new ReentrantLock();
                final var requested = lock.newCondition();
                final var latch = new CountDownLatch(1);
                final var future = executor.submit(() -> {
                    try {
                        latch.await();
                    } catch (final InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    final var executorForBytes = Executors.newSingleThreadExecutor(
                            Thread.ofVirtual().name("byte-publisher-", 0L).factory()
                    );
                    while (!Thread.currentThread().isInterrupted()) {
                        lock.lock();
                        try {
                            while (summed.get() == 0) {
                                try {
                                    requested.await();
                                } catch (final InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                            }
                            summed.decrementAndGet();
                        } finally {
                            lock.unlock();
                        }
                        new OfByte(service, executorForBytes).subscribe(new Flow.Subscriber<>() {
                            @Override public String toString() {
                                return String.format("[bytes-subscriber@%08x]", hashCode());
                            }
                            @Override public void onSubscribe(final Flow.Subscription subscription) {
                                log.debug("{}.onSubscribe({})", this, subscription);
                                subscription.request(array.length);
                            }
                            @Override public void onNext(final Byte item) {
                                log.debug("{}.onNext({})", this, item);
                                array[index++] = item;
                            }
                            @Override public void onError(final Throwable throwable) {
                                log.error("{}.onError({})", this, throwable, throwable);
                                Thread.currentThread().interrupt();
                                subscriber.onError(throwable);
                            }
                            @Override public void onComplete() {
                                log.debug("{}.onComplete()", this);
                                assert index == array.length;
                                subscriber.onNext(array);
                            }
                            private final byte[] array = new byte[HelloWorld.BYTES];
                            private int index = 0;
                        });
                    }
                });
                subscriber.onSubscribe(new Flow.Subscription() {
                    @Override public String toString() {
                        return String.format("[subscription-for-%s]", subscriber);
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
                            if (summed.addAndGet(n) < 0L) {
                                summed.set(Long.MAX_VALUE);
                            }
                            requested.signal();
                        } finally {
                            lock.unlock();
                        }
                    }
                    @Override public void cancel() {
                        log.debug("{}.cancel()", this);
                        future.cancel(true);
                    }
                });
                latch.countDown();
            }
        }
        /**
         * A publisher publishes byte buffers of the <em>hello-world-bytes</em>.
         * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
         */
        public static class OfBuffer extends HelloWorldPublisher<ByteBuffer> {
            /**
             * Creates a new instance with specified service and executor.
             * @param service the service for the <em>hello-world-bytes</em>.
             * @param executor the executor for publishing items asynchronously.
             */
            public OfBuffer(final HelloWorld service, final ExecutorService executor) {
                super(service, executor);
            }
            @Override public String toString() {
                return String.format("[buffer-publisher@%1$08x]", hashCode());
            }
            @Override public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
                super.subscribe(subscriber);
                final var processor = new Flow.Processor<byte[], ByteBuffer>() {
                    @Override public String toString() {
                        return String.format("[array-to-buffer-processor@%08x]", hashCode());
                    }
                    @Override
                    public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
                        log.debug("{}.subscribe({})", this, subscriber);
                        subscriber.onSubscribe(new Flow.Subscription() {
                            @Override public String toString() {
                                return String.format("[subscription-for-%1$s@%2$08x]", subscriber, hashCode());
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
                    @Override public void onNext(final byte[] item) {
                        log.debug("{}.onNext({})", this, item);
                        subscriber.onNext(ByteBuffer.wrap(item));
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
                };
                new OfArray(service, executor).subscribe(processor);
                processor.subscribe(subscriber);
            }
        }
        /**
         * A publisher publishes byte buffers of the <em>hello-world-bytes</em>.
         * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
         */
        public static class OfString extends HelloWorldPublisher<String> {
            /**
             * Creates a new instance with specified service and executor.
             * @param service the service for the <em>hello-world-bytes</em>.
             * @param executor the executor for publishing items asynchronously.
             */
            public OfString(final HelloWorld service, final ExecutorService executor) {
                super(service, executor);
            }
            @Override public String toString() {
                return String.format("[string-publisher@%1$08x]", hashCode());
            }
            @Override public void subscribe(final Flow.Subscriber<? super String> subscriber) {
                super.subscribe(subscriber);
                final var processor = new Flow.Processor<ByteBuffer, String>() {
                    @Override public String toString() {
                        return String.format("[buffer-to-string-processor@%08x]", hashCode());
                    }
                    @Override
                    public void subscribe(final Flow.Subscriber<? super String> subscriber) {
                        log.debug("{}.subscribe({})", this, subscriber);
                        subscriber.onSubscribe(new Flow.Subscription() {
                            @Override public String toString() {
                                return String.format("[subscription-for-%1$s@%2$08x]", subscriber, hashCode());
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
                };
                new OfBuffer(service, executor).subscribe(processor);
                processor.subscribe(subscriber);
            }
        }
        private HelloWorldPublisher(final HelloWorld service, final ExecutorService executor) {
            super();
            this.service = Objects.requireNonNull(service, "service is null");
            this.executor = Objects.requireNonNull(executor, "executor is null");
        }
        @Override public void subscribe(final Flow.Subscriber<? super T> subscriber) {
            log.debug("subscribe({})", subscriber);
        }
        final HelloWorld service;
        final ExecutorService executor;
    } // @formatter:on

    /**
     * An abstract class for subscribing to {@link HelloWorldPublisher}.
     *
     * @param <T> item type parameter.
     */
    @Slf4j
    public abstract static class HelloWorldSubscriber<T> // @formatter:off
            implements Flow.Subscriber<T> {
        /** A subscriber for {@link HelloWorldPublisher.OfByte}. */
        public static class OfByte extends HelloWorldSubscriber<Byte> {
            /** Creates a new instance. */
            public OfByte() { super(); }
            @Override public String toString() {
                return String.format("[bytes-subscriber@%1$08x]", hashCode());
            }
        }
        /** A subscriber for {@link HelloWorldPublisher.OfArray}. */
        public static class OfArray extends HelloWorldSubscriber<byte[]> {
            /** Creates a new instance. */
            public OfArray() { super(); }
            @Override public String toString() {
                return String.format("[array-subscriber@%1$08x]", hashCode());
            }
        }
        /** A subscriber for {@link HelloWorldPublisher.OfBuffer}. */
        public static class OfBuffer extends HelloWorldSubscriber<ByteBuffer> {
            /** Creates a new instance. */
            public OfBuffer() { super(); }
            @Override public String toString() {
                return String.format("[buffer-subscriber@%1$08x]", hashCode());
            }
        }
        /** A subscriber for {@link HelloWorldPublisher.OfString}. */
        public static class OfString extends HelloWorldSubscriber<String> {
            /** Creates a new instance. */
            public OfString() { super(); }
            @Override public String toString() {
                return String.format("[string-subscriber@%1$08x]", hashCode());
            }
        }
        private HelloWorldSubscriber() { super(); }
        @Override public void onSubscribe(final Flow.Subscription subscription) {
            log.debug("{}.onSubscribe({})", this, subscription);
            if (this.subscription != null) {
                this.subscription.cancel();
            }
            this.subscription = subscription;
        }
        @Override public void onNext(final T item) {
            log.debug("{}.onNext({})", this, item);
        }
        @Override public void onError(final Throwable throwable) {
            log.error("{}.onError({})", this, throwable, throwable);
        }
        @Override public void onComplete() {
            log.debug("{}.onComplete()", this);
        }
        @Accessors(fluent = true)
        @Getter(AccessLevel.PROTECTED)
        private Flow.Subscription subscription;
    } // @formatter:on

    // ---------------------------------------------------------------------------------------------

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private HelloWorldFlow() {
        throw new AssertionError("instantiation is not allowed");
    }
}
