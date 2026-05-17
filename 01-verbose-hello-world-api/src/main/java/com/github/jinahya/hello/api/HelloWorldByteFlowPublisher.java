package com.github.jinahya.hello.api;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * A {@link Flow.Publisher} of individual {@link Byte} elements — one per byte of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
 * <p>
 * Each call to {@link #subscribe(Flow.Subscriber) subscribe} allocates a fresh, single-use
 * {@link SubmissionPublisher} that delivers all {@value HelloWorld#BYTES} bytes — from {@code 'h'}
 * to {@code 'd'} — to the given subscriber, then {@link SubmissionPublisher#close() closes} it
 * (yielding {@code onComplete}). The {@code SubmissionPublisher}'s multicast buffer is unsuitable
 * here, since each subscriber must see the full sequence from the first byte regardless of when it
 * subscribes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldByteFlowPublisher implements Flow.Publisher<Byte> {

    HelloWorldByteFlowPublisher(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    @Override
    public void subscribe(final Flow.Subscriber<? super Byte> subscriber) {
        final var inner = new SubmissionPublisher<Byte>();
        inner.subscribe(subscriber);
        Thread.ofVirtual().start(() -> {
            try (inner) {
                for (final var b : HelloWorldUtils.array(service)) {
                    inner.submit(b);
                }
            } catch (final Throwable t) {
                inner.closeExceptionally(t);
            }
        });
    }

    private final HelloWorld service;
}
