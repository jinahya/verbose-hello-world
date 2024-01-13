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
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public final class HelloWorldFlow {

    private abstract static class HelloWorldPublisher<T> // @formatter:off
            implements Flow.Publisher<T> {
        private HelloWorldPublisher(final HelloWorld service, final ExecutorService executor) {
            super();
            this.service = Objects.requireNonNull(service, "service is null");
            this.executor = Objects.requireNonNull(executor, "executor is null");
        }
        final HelloWorld service;
        final ExecutorService executor;
    } // @formatter:on

    private static final class BytePublisher // @formatter:off
            extends HelloWorldPublisher<Byte> {
        private BytePublisher(final HelloWorld service, final ExecutorService executor) {
            super(service, executor);
        }
        @Override
        public void subscribe(final Flow.Subscriber<? super Byte> subscriber) {
            Objects.requireNonNull(subscriber, "subscriber is null");
            Objects.requireNonNull(executor, "executor is null");
            final var buffer = service.put(ByteBuffer.allocate(HelloWorld.BYTES)).limit(0);
            assert buffer.capacity() == HelloWorld.BYTES;
            assert buffer.position() == 0;
            assert buffer.limit() == 0;
            assert buffer.remaining() == 0;
            final var future = executor.submit(() -> {
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
                @Override public void request(final long n) {
                    if (n <= 0L) {
                        cancelInternal();
                        subscriber.onError(
                                new IllegalArgumentException("n(" + n +") is not positive")
                        );
                    }
                    if (future.isCancelled() || future.isDone()) { return; }
                    synchronized (buffer) {
                        buffer.limit((int) Math.min(buffer.capacity(), buffer.limit() + n));
                        buffer.notifyAll();
                    }
                }
                private void cancelInternal() { future.cancel(true); }
                @Override public void cancel() { cancelInternal(); }
            });
        }
    } // @formatter:on

    private static final class ArrayPublisher // @formatter:off
            extends HelloWorldPublisher<byte[]> {
        private ArrayPublisher(final HelloWorld service, final ExecutorService executor) {
            super(service, executor);
        }
        @Override
        public void subscribe(final Flow.Subscriber<? super byte[]> subscriber) {
            Objects.requireNonNull(subscriber, "subscriber is null");
            Objects.requireNonNull(executor, "executor is null");
            final var summed = new AtomicLong();
            final var lock = new ReentrantLock();
            final var requested = lock.newCondition();
            final var future = executor.submit(() -> {
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
                    new BytePublisher(service, executorForBytes).subscribe(new Flow.Subscriber<>() {
                        @Override public void onSubscribe(final Flow.Subscription subscription) {
                            subscription.request(array.length);
                        }
                        @Override public void onNext(final Byte item) { array[index++] = item; }
                        @Override public void onError(final Throwable throwable) {
                            Thread.currentThread().interrupt();                               // <1>
                            subscriber.onError(throwable);
                        }
                        @Override public void onComplete() {
                            assert index == array.length;
                            subscriber.onNext(array);
                        }
                        private final byte[] array = new byte[HelloWorld.BYTES];
                        private int index = 0;
                    });
                }
            });
            subscriber.onSubscribe(new Flow.Subscription() {
                @Override public void request(final long n) {
                    if (n <= 0L) {
                        cancelInternal();
                        subscriber.onError(
                                new IllegalArgumentException( "n(" + n +") is not positive")
                        );
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
                private void cancelInternal() { future.cancel(true); }
                @Override public void cancel() { cancelInternal(); }
            });
        }
    } // @formatter:on

    private static final class BufferPublisher // @formatter:off
            extends HelloWorldPublisher<ByteBuffer> {
        private BufferPublisher(final HelloWorld service, final ExecutorService executor) {
            super(service, executor);
        }
        @Override public void subscribe(final Flow.Subscriber<? super ByteBuffer> subscriber) {
            final var processor = new Flow.Processor<byte[], ByteBuffer>() {
                @Override public void subscribe(
                        final Flow.Subscriber<? super ByteBuffer> subscriber) {
                    subscriber.onSubscribe(new Flow.Subscription() {
                        @Override public void request(final long n) { subscription.request(n); }
                        @Override public void cancel() { subscription.cancel(); }
                    });
                }
                @Override public void onSubscribe(final Flow.Subscription subscription) {
                    this.subscription = subscription;
                }
                @Override public void onNext(final byte[] item) {
                    subscriber.onNext(ByteBuffer.wrap(item));
                }
                @Override public void onError(final Throwable throwable) {
                    subscriber.onError(throwable);
                }
                @Override public void onComplete() { subscriber.onComplete(); }
                private Flow.Subscription subscription;
            };
            new ArrayPublisher(service, executor).subscribe(processor);
            processor.subscribe(subscriber);
        }
    } // @formatter:on

    // ---------------------------------------------------------------------------------------------

    /**
     * Returns a new publisher publishes the {@code hello, world} bytes.
     *
     * @param service  an instance of {@link HelloWorld} interface.
     * @param executor an executor for publishing items asynchronously.
     * @return a new publisher.
     */
    public static Flow.Publisher<Byte> newPublisherForBytes(final HelloWorld service,
                                                            final ExecutorService executor) {
        return new BytePublisher(service, executor);
    }

    /**
     * Returns a new publisher publishes arrays of the {@code hello, world} bytes.
     *
     * @param service  an instance of {@link HelloWorld} interface.
     * @param executor an executor for publishing items asynchronously.
     * @return a new publisher.
     */
    public static Flow.Publisher<byte[]> newPublisherForArrays(final HelloWorld service,
                                                               final ExecutorService executor) {
        return new ArrayPublisher(service, executor);
    }

    /**
     * Returns a new publisher publishes buffers of the {@code hello, world} bytes.
     *
     * @param service  an instance of {@link HelloWorld} interface.
     * @param executor an executor for publishing items asynchronously.
     * @return a new publisher.
     */
    public static Flow.Publisher<ByteBuffer> newPublisherForBuffers(
            final HelloWorld service, final ExecutorService executor) {
        return new BufferPublisher(service, executor);
    }

    // ---------------------------------------------------------------------------------------------

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private HelloWorldFlow() {
        throw new AssertionError("instantiation is not allowed");
    }
}
