package com.github.jinahya.hello.api;

import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link Flow.Publisher} of {@link String} elements built on top of a
 * {@link HelloWorldArrayBytePublisher upstream array publisher}, with the {@code byte[] → String}
 * conversion performed by a private {@link Flow.Subscriber} that forwards each decoded string into
 * a wrapped {@link SubmissionPublisher} for multicast delivery.
 * <p>
 * On the first {@link #subscribe(Flow.Subscriber) subscribe}, the inner subscriber attaches itself
 * once to the upstream; subsequent downstream subscribes share the same upstream subscription.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldStringFlowPublisher implements Flow.Publisher<String>, AutoCloseable {

    private static final System.Logger logger = System.getLogger(
            MethodHandles.lookup().lookupClass().getName()
    );

    // ---------------------------------------------------------------------------------------------
    static HelloWorldStringFlowPublisher from(final HelloWorld service) {
        return new HelloWorldStringFlowPublisher(HelloWorldArrayBytePublisher.from(service));
    }

    // ---------------------------------------------------------------------------------------------
    HelloWorldStringFlowPublisher(final HelloWorldArrayBytePublisher source) {
        super();
        this.source = Objects.requireNonNull(source, "source is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void subscribe(final Flow.Subscriber<? super String> subscriber) {
        inner.subscribe(subscriber);
        if (started.compareAndSet(false, true)) {
            source.subscribe(new Flow.Subscriber<>() {
                @Override public void onSubscribe(final Flow.Subscription subscription) {
                    subscription.request(Long.MAX_VALUE);
                }
                @Override public void onNext(final byte[] item) {
                    inner.submit(new String(item, StandardCharsets.US_ASCII));
                }
                @Override public void onError(final Throwable throwable) {
                    inner.closeExceptionally(throwable);
                }
                @Override public void onComplete() {
                    inner.close();
                }
            });
        }
    }

    @Override
    public void close() {
        inner.close();
        source.close();
    }

    private final HelloWorldArrayBytePublisher source;

    private final SubmissionPublisher<String> inner = new SubmissionPublisher<>();

    private final AtomicBoolean started = new AtomicBoolean();
}
