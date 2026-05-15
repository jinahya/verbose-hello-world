package com.github.jinahya.hello.api;

import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A package-private {@link Publisher} of {@link String} elements — each decoded in
 * {@link StandardCharsets#US_ASCII US-ASCII} from a {@code byte[]} obtained from the upstream
 * byte-array publisher.
 * <p>
 * Unlike {@link ReactiveHelloWorldArrayPublisher} (which aggregates {@value HelloWorld#BYTES} bytes
 * into one array via a worker-per-demand pattern), this publisher is a strict <strong>1:1
 * transform</strong>: each upstream {@code onNext(byte[])} produces exactly one downstream
 * {@code onNext(String)}. Each {@link #subscribe(Subscriber) subscribe} call creates a fresh
 * single-use {@link Processor} ({@link StringEncoder}) that wraps the upstream byte-array publisher
 * and forwards demand 1:1.
 * <p>
 * <strong>Threading.</strong> No producer thread of its own; the {@link StringEncoder} runs on
 * whichever thread the upstream emits on. Per-subscriber state (terminated flag, lock,
 * subscriptions) lives inside the encoder instance.
 * <p>
 * <strong>Signal serialization (Rules 1.3 / 1.7).</strong> All downstream signals — upstream's
 * {@code onNext}/{@code onError}/{@code onComplete} translated through the encoder, plus
 * {@code onError} from {@link Subscription#request(long) request(n &le; 0)} on the caller's thread
 * — are wrapped in the same {@link ReentrantLock} inside the encoder, satisfying <a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.3">Rule
 * 1.3</a>. Terminal sites additionally CAS the {@code terminated} flag, satisfying <a
 * href="https://github.com/reactive-streams/reactive-streams-jvm/blob/master/README.md#1.7">Rule
 * 1.7</a> — at most one terminal ever fires.
 * <p>
 * <strong>Lifetime.</strong> The stream's natural lifetime mirrors the upstream's — if upstream
 * errors, downstream errors; if upstream completes, downstream completes. When stacked on top of
 * the open-ended {@link ReactiveHelloWorldArrayPublisher}, this publisher is open-ended too;
 * downstream {@code cancel()} (or {@code request(n &le; 0)}) is the only way it stops.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorldPublishers#ofStrings(HelloWorld)
 * @see ReactiveHelloWorldArrayPublisher
 * @see Processor
 */
final class ReactiveHelloWorldStringPublisher implements Publisher<String> {

    private static final System.Logger logger = System.getLogger(
            MethodHandles.lookup().lookupClass().getName()
    );

    // ---------------------------------------------------------------------------------------------

    /**
     * A single-use {@link Processor} that subscribes to an upstream {@code byte[]} publisher and
     * republishes each {@code byte[]} as a {@link String} decoded in
     * {@link StandardCharsets#US_ASCII US-ASCII}.
     * <p>
     * Expected wiring (typical of any {@link Processor}):
     * <pre>{@code
     *     final var encoder = new StringEncoder();
     *     upstream.subscribe(encoder);          // upstream calls encoder.onSubscribe(...)
     *     encoder.subscribe(downstream);        // downstream gets its Subscription via onSubscribe
     * }</pre>
     * Demand is forwarded <strong>1:1</strong> — each downstream {@code request(n)} is forwarded as
     * {@code upstream.request(n)}; each upstream {@code onNext(byte[])} produces one downstream
     * {@code onNext(String)}.
     * <p>
     * One downstream subscriber per instance. Downstream signals are serialized through an internal
     * {@link ReentrantLock} (Rule 1.3), and terminal sites are gated by
     * {@code terminated.compareAndSet(false, true)} so that at most one of
     * {@code onError}/{@code onComplete} ever fires (Rule 1.7).
     */
    private static final class StringEncoder implements Processor<byte[], String> {

        // ---------------------------------------------------------------------------- CONSTRUCTORS

        /**
         * Creates a new instance wrapping the specified upstream byte-array publisher.
         *
         * @param publisher the upstream {@link Publisher} of {@code byte[]} that supplies the bytes
         *                  for each decoded string.
         * @throws NullPointerException if the {@code publisher} is {@code null}.
         */
        StringEncoder(final Publisher<? extends byte[]> publisher) {
            super();
            this.upstreamPublisher = Objects.requireNonNull(publisher, "publisher is null");
        }

        // ------------------------------------------------------------------------ java.lang.Object

        @Override
        public String toString() {
            return super.toString().substring(getClass().getPackageName().length() + 1);
        }

        // -----------------------------------------------------------------------------------------
        @Override
        public void subscribe(final Subscriber<? super String> s) {
            logger.log(System.Logger.Level.DEBUG, "subscribe({0}) / {1}", s, this);
            Objects.requireNonNull(s, "s is null");
            downstreamSubscriber = s;
            upstreamPublisher.subscribe(this);   // upstream synchronously calls onSubscribe
        }

        // -----------------------------------------------------------------------------------------
        @Override
        public void onSubscribe(final Subscription s) { // @formatter:off
            logger.log(System.Logger.Level.DEBUG, "onSubscribe({0}) / {1}", s, this);
            this.upstreamSubscription = s;
            downstreamSubscriber.onSubscribe(new Subscription() {
                @Override public String toString() {
                    return super.toString().substring(getClass().getPackageName().length() + 1);
                }
                @Override public void request(final long n) {
                    logger.log(System.Logger.Level.DEBUG, "request({0}) / {1}", n, this);
                    if (terminated.get()) { return; }
                    if (n <= 0L) {
                        if (terminated.compareAndSet(false, true)) {
                            try {
                                downstreamSubscriber.onError(new IllegalArgumentException(
                                        "n(" + n + ") is not positive"
                                ));
                            } catch (final Throwable st) { }
                            upstreamSubscription.cancel();
                        }
                        return;
                    }
                    upstreamSubscription.request(n);                         // 1:1 passthrough
                }
                @Override public void cancel() {
                    logger.log(System.Logger.Level.DEBUG, "cancel() / {0}", this);
                    terminated.set(true);
                    upstreamSubscription.cancel();
                }
            }); // @formatter:on
        }

        @Override
        public void onNext(final byte[] element) { // @formatter:off
            logger.log(System.Logger.Level.DEBUG, "onNext({0}) / {1}",
                       IntStream.range(0, element.length)
                               .mapToObj(i -> String.format("%02x'%c'", element[i], element[i]))
                               .collect(Collectors.joining(" ", "[", "]")), this);
            if (terminated.get()) { return; }
            try {
                downstreamSubscriber.onNext(new String(element, StandardCharsets.US_ASCII));
            } catch (final Throwable t) {
                terminated.set(true);
                upstreamSubscription.cancel();
            } // @formatter:on
        }

        @Override
        public void onError(final Throwable t) { // @formatter:off
            logger.log(System.Logger.Level.DEBUG, "onError({0}) / {1}", t, this);
            if (terminated.compareAndSet(false, true)) {
                try { downstreamSubscriber.onError(t); } catch (final Throwable st) { }
            } // @formatter:on
        }

        @Override
        public void onComplete() { // @formatter:off
            logger.log(System.Logger.Level.DEBUG, "onComplete() / {0}", this);
            if (terminated.compareAndSet(false, true)) {
                try { downstreamSubscriber.onComplete(); } catch (final Throwable st) { }
            } // @formatter:on
        }

        // ------------------------------------------------------------------------------------------
        private final Publisher<? extends byte[]> upstreamPublisher;

        private Subscription upstreamSubscription;

        private final AtomicBoolean terminated = new AtomicBoolean();

        private Subscriber<? super String> downstreamSubscriber;
    }

    // ---------------------------------------------------------------------------------------------
    static ReactiveHelloWorldStringPublisher from(final HelloWorld service) {
        return new ReactiveHelloWorldStringPublisher(
                ReactiveHelloWorldArrayPublisher.from(service));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance wrapping the specified upstream byte-array publisher.
     *
     * @param publisher the upstream {@link Publisher} of {@code byte[]} that supplies the bytes for
     *                  each decoded string.
     * @throws NullPointerException if the {@code publisher} is {@code null}.
     */
    ReactiveHelloWorldStringPublisher(final ReactiveHelloWorldArrayPublisher publisher) {
        super();
        this.publisher = Objects.requireNonNull(publisher, "publisher is null");
    }

    // ---------------------------------------------------------------------------- java.lang.Object
    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + Objects.hash(this);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     *
     * @throws NullPointerException if the {@code subscriber} is {@code null}.
     * @implSpec The default implementation creates a fresh {@link StringEncoder} {@link Processor}
     * wrapping the upstream byte-array publisher and subscribes the given {@code subscriber} to it.
     * Demand is forwarded 1:1 from downstream to upstream, and each upstream {@code onNext(byte[])}
     * produces one downstream {@code onNext(String)} decoded in
     * {@link StandardCharsets#US_ASCII US-ASCII}.
     */
    @Override
    public void subscribe(final Subscriber<? super String> subscriber) {
        logger.log(System.Logger.Level.DEBUG, "subscribe({0}) / {1}", subscriber, this);
        Objects.requireNonNull(subscriber, "subscriber is null");
        new StringEncoder(publisher).subscribe(subscriber);
    }

    // ---------------------------------------------------------------------------------------------
    private final ReactiveHelloWorldArrayPublisher publisher;
}
