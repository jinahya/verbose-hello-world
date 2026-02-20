package com.github.jinahya.hello.api;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

non-sealed class ReactiveHelloWorld_OfArrays
        implements ReactiveHelloWorld.OfArrays {

    ReactiveHelloWorld_OfArrays(final ReactiveHelloWorld<Byte> ofBytes) {
        super();
        this.ofBytes = Objects.requireNonNull(ofBytes, "ofBytes is null");
    }

    @Override
    public Subscription subscribe(final Subscriber<? super byte[]> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        final var executor = Executors.newVirtualThreadPerTaskExecutor();
        final var queue = new LinkedBlockingQueue<Future<byte[]>>();
        final var terminated = new AtomicBoolean();
        final var consumer = Thread.ofVirtual().start(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                final Future<byte[]> future;
                try {
                    future = queue.take();
                } catch (final InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                final byte[] array;
                try {
                    array = future.get();
                } catch (final InterruptedException | CancellationException ice) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (final ExecutionException ee) {
                    if (terminated.compareAndSet(false, true)) {
                        subscriber.onError(ee.getCause());
                    }
                    Thread.currentThread().interrupt();
                    break;
                }
                try {
                    subscriber.onNext(array);
                } catch (final Throwable t) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        final var subscription = new Subscription() { // @formatter:off
            @Override public void request(final long n) {
                if (n <= 0L) {
                    if (terminated.compareAndSet(false, true)) {
                        subscriber.onError(
                                new IllegalArgumentException("n(" + n + ") is not positive")
                        );
                    }
                    consumer.interrupt();
                    executor.shutdownNow();
                    return;
                }
                for (var i = 0L; i < n; i++) {
                    try {
                        queue.add(executor.submit(() -> {
                            final var array = new byte[HelloWorld.BYTES];
                            final var index = new AtomicInteger();
                            final var latch = new CountDownLatch(1);
                            final var error = new AtomicReference<Throwable>();
                            ofBytes.subscribe(new Subscriber<Byte>() {
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
                            });
                            latch.await();
                            final var t = error.get();
                            if (t != null) {
                                if (t instanceof RuntimeException re) throw re;
                                if (t instanceof Error e) throw e;
                                throw new Exception(t);
                            }
                            return array;
                        }));
                    } catch (final java.util.concurrent.RejectedExecutionException ree) {
                        break;
                    }
                }
            }
            @Override public void cancel() {
                consumer.interrupt();
                executor.shutdownNow();
            } // @formatter:on
        };
        try {
            subscriber.onSubscribe(subscription);
        } catch (final Throwable t) {
            subscription.cancel();
        }
        return subscription;
    }

    private final ReactiveHelloWorld<Byte> ofBytes;
}
