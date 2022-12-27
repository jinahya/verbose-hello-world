package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Flow;
import java.util.function.Supplier;

@Slf4j
class ReactiveHelloWorldSubscription<T> implements Flow.Subscription {

    ReactiveHelloWorldSubscription(Flow.Subscriber<? super T> subscriber,
                                   Supplier<? extends T> supplier,
                                   BlockingQueue<Long> queue) {
        super();
        this.subscriber = Objects.requireNonNull(subscriber, "subscriber is null");
        this.supplier = Objects.requireNonNull(supplier, "supplier is null");
        this.queue = Objects.requireNonNull(queue, "queue is null");
    }

    @Override
    public synchronized void request(long n) {
        if (canceled) {
            log.warn("already canceled");
            return;
        }
        if (n <= 0L) {
            subscriber.onError(new IllegalArgumentException("n(" + n + ") is not positive"));
            return;
        }
        if (emitter == null) {
            emitter = new ReactiveHelloWorldEmitter<>(subscriber, supplier, queue);
            emitter.start();
        }
        if (!queue.offer(n)) {
            log.error("failed to offer n({}) to the queue", n);
        }
    }

    @Override
    public synchronized void cancel() {
        if (canceled) {
            return;
        }
        if (emitter != null) {
            emitter.interrupt();
        }
        canceled = true;
    }

    private final Flow.Subscriber<? super T> subscriber;

    private final Supplier<? extends T> supplier;

    private final BlockingQueue<Long> queue;

    private volatile ReactiveHelloWorldEmitter<T> emitter;

    private volatile boolean canceled = false;
}
