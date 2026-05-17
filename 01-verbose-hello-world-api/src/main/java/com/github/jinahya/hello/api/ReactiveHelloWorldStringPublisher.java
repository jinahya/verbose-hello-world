package com.github.jinahya.hello.api;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * A package-private {@link Publisher} of {@link String} elements — each decoded in
 * {@link StandardCharsets#US_ASCII US-ASCII} from a {@code byte[]} obtained from an upstream
 * byte-array publisher.
 * <p>
 * Each call to {@link #subscribe(Subscriber) subscribe} allocates a fresh
 * {@link ReactiveHelloWorldStringEncoder} (a single-use {@link Processor}) that wraps the upstream
 * byte-array publisher and forwards demand 1:1. Multiple downstream subscribers therefore each get
 * their own independent stream — there is no sharing, no buffering, and no pacing between
 * subscribers; this matches the cold semantics of {@link ReactiveHelloWorldBytePublisher} and
 * {@link ReactiveHelloWorldArrayPublisher}.
 * <p>
 * <strong>Threading.</strong> No producer thread of its own; each encoder runs on whichever thread
 * the upstream emits on. Per-subscriber state (terminated flag, subscription, subscriber) lives
 * inside the encoder instance.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldArrayPublisher
 * @see ReactiveHelloWorldStringEncoder
 */
final class ReactiveHelloWorldStringPublisher implements Publisher<String> {

    // ---------------------------------------------------------------------------------------------
    static ReactiveHelloWorldStringPublisher from(final HelloWorld service) {
        return new ReactiveHelloWorldStringPublisher(
                ReactiveHelloWorldArrayPublisher.from(service));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance wrapping the specified upstream byte-array publisher.
     *
     * @param publisher the upstream {@link ReactiveHelloWorldArrayPublisher} that supplies the
     *                  bytes for each decoded string.
     * @throws NullPointerException if the {@code publisher} is {@code null}.
     */
    ReactiveHelloWorldStringPublisher(final ReactiveHelloWorldArrayPublisher publisher) {
        super();
        this.publisher = Objects.requireNonNull(publisher, "publisher is null");
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the {@code subscriber} is {@code null}.
     * @implSpec The default implementation creates a fresh {@link ReactiveHelloWorldStringEncoder}
     * wrapping the upstream byte-array publisher and subscribes the given {@code subscriber} to it.
     * Demand is forwarded 1:1 from downstream to upstream, and each upstream {@code onNext(byte[])}
     * produces one downstream {@code onNext(String)} decoded in
     * {@link StandardCharsets#US_ASCII US-ASCII}.
     */
    @Override
    public void subscribe(final Subscriber<? super String> subscriber) {
        Objects.requireNonNull(subscriber, "subscriber is null");
        new ReactiveHelloWorldStringEncoder(publisher).subscribe(subscriber);
    }

    // ---------------------------------------------------------------------------------------------
    private final ReactiveHelloWorldArrayPublisher publisher;
}
