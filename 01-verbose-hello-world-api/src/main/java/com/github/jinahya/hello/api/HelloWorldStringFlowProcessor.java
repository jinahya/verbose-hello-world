package com.github.jinahya.hello.api;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * A {@link Flow.Processor} that subscribes to an upstream {@code byte[]} publisher (as a
 * {@link Flow.Subscriber Subscriber&lt;byte[]&gt;}) and republishes each {@code byte[]} downstream
 * as a {@link StandardCharsets#US_ASCII US-ASCII} {@link String} (as a
 * {@link Flow.Publisher Publisher&lt;String&gt;}).
 * <p>
 * The publishing side is provided by extending {@link SubmissionPublisher} — multicast, so every
 * downstream subscriber sees every emitted string. Upstream {@code onError}/{@code onComplete} are
 * forwarded to all downstream subscribers via
 * {@link SubmissionPublisher#closeExceptionally(Throwable) closeExceptionally(t)} /
 * {@link SubmissionPublisher#close() close()}. The processor signals unbounded demand
 * ({@link Long#MAX_VALUE}) to the upstream on {@code onSubscribe}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldStringFlowProcessor
        implements Flow.Processor<byte[], String> {

    private static final System.Logger logger = System.getLogger(
            MethodHandles.lookup().lookupClass().getName()
    );

    // ---------------------------------------------------------------------------------------------
    HelloWorldStringFlowProcessor() {
        super();
    }

    // ---------------------------------------------------------------------- Flow.Publisher<String>
    @Override
    public void subscribe(final Flow.Subscriber<? super String> subscriber) {
    }

    // --------------------------------------------------------------------- Flow.Subscriber<byte[]>
    @Override
    public void onSubscribe(final Flow.Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(final byte[] item) {
    }

    @Override
    public void onError(final Throwable throwable) {
    }

    @Override
    public void onComplete() {
    }
}
