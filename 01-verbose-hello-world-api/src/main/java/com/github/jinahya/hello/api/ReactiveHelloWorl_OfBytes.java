package com.github.jinahya.hello.api;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

non-sealed class ReactiveHelloWorl_OfBytes
        implements ReactiveHelloWorld.OfBytes {

    ReactiveHelloWorl_OfBytes(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    @Override
    public Subscription subscribe(final Subscriber<? super Byte> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        final var array = service.set();
        final var index = new AtomicInteger();
        final var demand = new AtomicLong();
        final var terminated = new AtomicBoolean();
        final var lock = new Object();
        final var thread = Thread.ofVirtual().start(() -> {
            while (!Thread.currentThread().isInterrupted() && index.get() < array.length) {
                synchronized (lock) {
                    while (demand.get() == 0L) {
                        try {
                            lock.wait();
                        } catch (final InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
                assert demand.get() > 0L;
                demand.decrementAndGet();
                final var item = array[index.getAndIncrement()];
                try {
                    subscriber.onNext(item);
                } catch (final Throwable t) {
                    Thread.currentThread().interrupt();
                }
            }
            if (!Thread.currentThread().isInterrupted()) {
                assert index.get() == array.length;
                if (terminated.compareAndSet(false, true)) {
                    subscriber.onComplete();
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
                    thread.interrupt();
                    return;
                }
                demand.accumulateAndGet(n, (cur, inc) -> {
                    try {
                        return Math.addExact(cur, inc);
                    } catch (final ArithmeticException ae) {
                        return Long.MAX_VALUE;
                    }
                });
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
            @Override public void cancel() {
                thread.interrupt();
            } // @formatter:on
        };
        try {
            subscriber.onSubscribe(subscription);
        } catch (final Throwable t) {
            subscription.cancel();
        }
        return subscription;
    }

    private final HelloWorld service;
}
