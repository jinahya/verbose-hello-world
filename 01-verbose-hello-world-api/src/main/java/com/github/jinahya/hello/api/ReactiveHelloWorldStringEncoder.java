package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A single-use {@link Processor} that subscribes to an upstream {@code byte[]} publisher and
 * republishes each emitted {@code byte[]} as a {@link String} decoded in
 * {@link StandardCharsets#US_ASCII US-ASCII}.
 * <p>
 * Demand is forwarded <strong>1:1</strong> — each downstream {@code request(n)} is forwarded as
 * {@code upstream.request(n)}; each upstream {@code onNext(byte[])} produces one downstream
 * {@code onNext(String)}.
 * <p>
 * One downstream subscriber per instance — {@link ReactiveHelloWorldStringPublisher} creates a
 * fresh encoder per {@link Publisher#subscribe(Subscriber) subscribe} so that multiple downstream
 * subscribers each get their own independent stream. Because the upstream
 * {@link ReactiveHelloWorldArrayPublisher} is single-threaded, downstream signals inherit that
 * serialization for free (Rule 1.3) — no internal lock is needed. Terminal sites ({@code onError},
 * {@code onComplete}) are gated by {@code terminated.compareAndSet(false, true)} so that at most
 * one terminal ever reaches downstream (Rule 1.7).
 * <p>
 * <strong>Didactic scope.</strong> This class is written to <em>introduce</em> the Reactive
 * Streams workflow, not to be a hardened implementation. {@code request(n &le; 0)} is guarded by an
 * {@code assert} rather than routed to {@code onError}, and exceptions thrown by upstream signals
 * or by the downstream subscriber are <em>not</em> caught — they propagate out of the calling
 * thread. A production-grade processor would handle both as terminal {@code onError} signals.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldStringPublisher
 * @see ReactiveHelloWorldArrayPublisher
 */
final class ReactiveHelloWorldStringEncoder implements Processor<byte[], String> {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance wrapping the specified upstream byte-array publisher.
     *
     * @param publisher the upstream {@link Publisher} of {@code byte[]} that supplies the bytes for
     *                  each decoded string.
     * @throws NullPointerException if the {@code publisher} is {@code null}.
     */
    ReactiveHelloWorldStringEncoder(final Publisher<? extends byte[]> publisher) {
        super();
        this.publisher = Objects.requireNonNull(publisher, "publisher is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void subscribe(final Subscriber<? super String> s) {
        Objects.requireNonNull(s, "s is null");
        subscriber = s;
        publisher.subscribe(this);   // upstream synchronously calls onSubscribe
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onSubscribe(final Subscription s) { // @formatter:off
        this.subscription = s;
        subscriber.onSubscribe(new Subscription() {
            @Override public void request(final long n) {
                if (terminated.get()) { return; }
                assert n > 0L;
                subscription.request(n);
            }
            @Override public void cancel() {
                terminated.set(true);
                subscription.cancel();
            }
        }); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void onNext(final byte[] element) {
        if (terminated.get()) {
            return;
        }
        subscriber.onNext(new String(element, StandardCharsets.US_ASCII));
    }

    @Override
    public void onError(final Throwable t) {
        if (terminated.compareAndSet(false, true)) {
            subscriber.onError(t);
        }
    }

    @Override
    public void onComplete() {
        if (terminated.compareAndSet(false, true)) {
            subscriber.onComplete();
        }
    }

    // ---------------------------------------------------------------------------------------------
    private final Publisher<? extends byte[]> publisher;

    private final AtomicBoolean terminated = new AtomicBoolean();

    private @Nullable Subscription subscription;

    private @Nullable Subscriber<? super String> subscriber;
}
