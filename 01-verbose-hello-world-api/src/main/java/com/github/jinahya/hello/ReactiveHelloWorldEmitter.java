package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
class ReactiveHelloWorldEmitter<T> extends Thread {

    ReactiveHelloWorldEmitter(Flow.Subscriber<? super T> subscriber,
                              Supplier<? extends T> supplier, BlockingQueue<Long> queue) {
        super();
        this.subscriber = Objects.requireNonNull(subscriber, "subscriber is null");
        this.supplier = Objects.requireNonNull(supplier, "supplier is null");
        this.queue = Objects.requireNonNull(queue, "queue is null");
    }

    @Override
    public void run() {
        for (Long n; !isInterrupted(); ) {
            try {
                n = queue.poll(1L, TimeUnit.SECONDS);
                if (n == null) {
                    continue;
                }
            } catch (InterruptedException ie) {
                log.info("interrupted while polling the queue", ie);
                interrupt();
                continue;
            }
            for (long i = 0; i < n && !isInterrupted(); i++) {
                subscriber.onNext(supplier.get());
            }
            subscriber.onComplete();
        }
    }

    private final Flow.Subscriber<? super T> subscriber;

    private final Supplier<? extends T> supplier;

    private final BlockingQueue<Long> queue;
}
