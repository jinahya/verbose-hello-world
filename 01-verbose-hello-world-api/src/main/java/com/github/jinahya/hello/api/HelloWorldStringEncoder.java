package com.github.jinahya.hello.api;

import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A single-use Reactive Streams {@link Processor} that decodes each upstream {@code byte[]} as a
 * {@link StandardCharsets#US_ASCII US-ASCII} {@link String} and forwards it downstream with
 * <strong>1:1</strong> demand pass-through — each downstream {@code request(n)} becomes one
 * {@code upstream.request(n)}, and each upstream {@code onNext(byte[])} becomes one
 * {@code downstream.onNext(String)}.
 * <p>
 * Typical wiring:
 * <pre>{@code
 *     final var encoder = new HelloWorldStringEncoder();
 *     upstream.subscribe(encoder);       // upstream calls encoder.onSubscribe(upstreamSubscription)
 *     encoder.subscribe(downstream);     // downstream gets its Subscription via onSubscribe
 * }</pre>
 * Or, for {@link java.util.concurrent.Flow} upstream/downstream, adapt via {@link FlowAdapters}:
 * <pre>{@code
 *     final var flow = HelloWorldStringEncoder.ofFlow();
 *     flowUpstream.subscribe(flow);
 *     flow.subscribe(flowDownstream);
 * }</pre>
 * <p>
 * One downstream subscriber per instance. Terminal sites ({@code onError}, {@code onComplete}, an
 * {@code onNext} that throws) are gated by {@code terminated.compareAndSet(false, true)} so that at
 * most one terminal ever reaches downstream (Rule 1.7).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class HelloWorldStringEncoder implements Processor<byte[], String> {

    // -----------------------------------------------------------------------------------------
    @Override
    public void subscribe(final Subscriber<? super String> s) {
        Objects.requireNonNull(s, "s is null");
        this.downstream = s;
        s.onSubscribe(new Subscription() {
            @Override public void request(final long n) {
                if (terminated.get()) { return; }
                assert n > 0L : "n(" + n + ") is not positive";
                upstream.request(n);
            }
            @Override public void cancel() {
                if (terminated.compareAndSet(false, true)) {
                    upstream.cancel();
                }
            }
        });
    }

    // -----------------------------------------------------------------------------------------
    @Override
    public void onSubscribe(final Subscription s) {
        this.upstream = s;
    }

    @Override
    public void onNext(final byte[] item) {
        if (terminated.get()) { return; }
        try {
            downstream.onNext(new String(item, StandardCharsets.US_ASCII));
        } catch (final Throwable t) {
            if (terminated.compareAndSet(false, true)) {
                upstream.cancel();
            }
        }
    }

    @Override
    public void onError(final Throwable t) {
        if (terminated.compareAndSet(false, true)) {
            try { downstream.onError(t); } catch (final Throwable st) { }
        }
    }

    @Override
    public void onComplete() {
        if (terminated.compareAndSet(false, true)) {
            try { downstream.onComplete(); } catch (final Throwable st) { }
        }
    }

    // -----------------------------------------------------------------------------------------
    private Subscription upstream;

    private Subscriber<? super String> downstream;

    private final AtomicBoolean terminated = new AtomicBoolean();

    /**
     * Wraps a new {@link HelloWorldStringEncoder} as a {@link Flow.Processor} via
     * {@link FlowAdapters#toFlowProcessor(Processor)}. Convenience for the {@code java.util.concurrent.Flow}
     * side.
     *
     * @return a fresh, single-use {@link Flow.Processor} backed by a new {@link HelloWorldStringEncoder}.
     */
    static Flow.Processor<byte[], String> ofFlow() {
        return FlowAdapters.toFlowProcessor(new HelloWorldStringEncoder());
    }
}
