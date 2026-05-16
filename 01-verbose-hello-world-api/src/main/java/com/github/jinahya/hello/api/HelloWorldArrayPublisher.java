package com.github.jinahya.hello.api;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.github.jinahya.hello.api.HelloWorldBookUtils.loggingSubscriber;

/**
 * A {@link Flow.Publisher} of {@code byte[]} elements — each a freshly assembled,
 * {@value HelloWorld#BYTES}-byte snapshot of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, composed on top of a
 * {@link HelloWorldBytePublisher upstream byte publisher} and multicast through a wrapped
 * {@link SubmissionPublisher}.
 * <p>
 * On the first {@link #subscribe(Flow.Subscriber) subscribe}, a single producer virtual thread is
 * lazily started. The thread loops until {@link #close() closed}: for each iteration it subscribes
 * to the upstream byte publisher, accumulates the 12 {@code onNext} bytes into a fresh
 * {@code byte[]}, and on the upstream's {@code onComplete}
 * {@linkplain SubmissionPublisher#submit(Object) submits} the array through the wrapped
 * {@code SubmissionPublisher} (multicast — every downstream subscriber sees every emitted array).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldArrayPublisher implements Flow.Publisher<byte[]>, AutoCloseable {

    private static final System.Logger logger = System.getLogger(
            MethodHandles.lookup().lookupClass().getName()
    );

    // ---------------------------------------------------------------------------------------------
    static HelloWorldArrayPublisher from(final HelloWorld service) {
        return new HelloWorldArrayPublisher(new HelloWorldBytePublisher(service));
    }

    // ---------------------------------------------------------------------------------------------
    HelloWorldArrayPublisher(final HelloWorldBytePublisher source) {
        super();
        this.source = Objects.requireNonNull(source, "source is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void subscribe(final Flow.Subscriber<? super byte[]> subscriber) { // @formatter:off
        inner.subscribe(subscriber);
        if (started.compareAndSet(false, true)) {
            Thread.ofVirtual().start(() -> {
                while (!inner.isClosed()) {
                    final var future = new CompletableFuture<byte[]>();
                    source.subscribe(loggingSubscriber(new Flow.Subscriber<>() {
                        final byte[] array = new byte[HelloWorld.BYTES];
                        int index = 0;
                        @Override public void onSubscribe(final Flow.Subscription subscription) {
                            subscription.request(Long.MAX_VALUE);
                        }
                        @Override public void onNext(final Byte item) {
                            array[index++] = item;
                        }
                        @Override public void onError(final Throwable throwable) {
                            future.completeExceptionally(throwable);
                        }
                        @Override public void onComplete() {
                            future.complete(array);
                        }
                    }));
                    final byte[] array;
                    try {
                        array = future.get();
                    } catch (final ExecutionException ee) {
                        inner.closeExceptionally(ee.getCause());
                        return;
                    } catch (final InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        inner.close();
                        return;
                    }
                    try {
                        inner.submit(array);
                    } catch (final IllegalStateException ise) {
                        assert inner.isClosed();
                        return;
                    }
                }
            });
        } // @formatter:on
    }

    @Override
    public void close() {
        inner.close();
    }

    private final HelloWorldBytePublisher source;

    private final SubmissionPublisher<byte[]> inner = new SubmissionPublisher<>();

    private final AtomicBoolean started = new AtomicBoolean();
}
