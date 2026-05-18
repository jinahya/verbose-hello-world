package com.github.jinahya.hello.api;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link Flow.Publisher} of {@code byte[]} elements — each a freshly assembled,
 * {@value HelloWorld#BYTES}-byte snapshot of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, sourced directly from a
 * {@link HelloWorld} service and multicast through a wrapped {@link SubmissionPublisher}.
 * <p>
 * On the first {@link #subscribe(Flow.Subscriber) subscribe}, a single producer virtual thread is
 * lazily started. The thread loops until {@link #close() closed}: each iteration calls
 * {@link HelloWorldUtils#array(HelloWorld) HelloWorldUtils.array(service)} to obtain a fresh
 * {@code byte[]} and {@linkplain SubmissionPublisher#submit(Object) submits} it through the wrapped
 * {@code SubmissionPublisher} (multicast — every downstream subscriber sees every emitted array).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldArrayPublisher implements Flow.Publisher<byte[]>, AutoCloseable {

    // ---------------------------------------------------------------------------------------------
    static HelloWorldArrayPublisher from(final HelloWorld service) {
        return new HelloWorldArrayPublisher(service);
    }

    // ---------------------------------------------------------------------------------------------
    HelloWorldArrayPublisher(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void subscribe(final Flow.Subscriber<? super byte[]> subscriber) {
        inner.subscribe(subscriber);
        if (started.compareAndSet(false, true)) {
            Thread.ofVirtual().start(() -> {
                while (!inner.isClosed()) {
                    try {
                        final var result = inner.offer(
                                HelloWorldUtils.array(service),
                                100,
                                TimeUnit.MILLISECONDS,
                                null
                        );
                    } catch (final IllegalStateException ise) {
                        assert inner.isClosed();
                        return;
                    }
                }
            });
        }
    }

    @Override
    public void close() {
        inner.close();
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;

    private final SubmissionPublisher<byte[]> inner = new SubmissionPublisher<>();

    private final AtomicBoolean started = new AtomicBoolean();
}
