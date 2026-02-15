package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;
import io.vertx.core.Vertx;
import io.vertx.ext.reactivestreams.ReactiveWriteStream;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An example implementation of {@link ReactiveHelloWorldFactory} using Eclipse Vert.x.
 * <p>
 * This demonstrates how to implement {@link ReactiveHelloWorldFactory} using Vert.x's reactive
 * streams integration. Vert.x provides {@link ReactiveWriteStream} which implements both Vert.x's
 * {@code WriteStream} and Reactive Streams' {@code Publisher}.
 * <p>
 * This is an example implementation for testing and educational purposes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class VertxReactiveHelloWorldFactory
        extends AbstractReactiveHelloWorld {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with the specified service and Vertx instance.
     *
     * @param service the underlying {@link HelloWorld} service
     * @param vertx   the Vertx instance
     */
    public VertxReactiveHelloWorldFactory(final HelloWorld service, final Vertx vertx) {
        super(service);
        this.vertx = Objects.requireNonNull(vertx, "vertx is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Publisher<Byte> newOctetPublisher() {
        final var array = service.set(new byte[HelloWorld.BYTES], 0);
        // Implement custom Publisher to handle backpressure properly
        // Use Vert.x event loop for async execution
        return new Publisher<Byte>() {
            @Override
            public void subscribe(final Subscriber<? super Byte> subscriber) {
                final var index = new AtomicInteger(0);
                final var cancelled = new AtomicBoolean(false);
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(final long n) {
                        if (cancelled.get()) {
                            return;
                        }
                        // Emit requested bytes synchronously (like default implementation)
                        // Vert.x integration can be shown in array/string publishers
                        final var currentIndex = index.get();
                        // Handle Long.MAX_VALUE properly (treat as "all remaining")
                        final var requestCount = n == Long.MAX_VALUE ? array.length - currentIndex :
                                                 (int) Math.min(n, array.length - currentIndex);
                        final var endIndex = currentIndex + requestCount;
                        for (var i = currentIndex; i < endIndex && !cancelled.get(); i++) {
                            final var b = Byte.valueOf(array[i]);
                            log.debug("publishing byte: 0x{}('{}')",
                                      String.format("%02x", array[i]), (char) array[i]);
                            subscriber.onNext(b);
                        }
                        // Update index
                        index.set(endIndex);
                        // Only complete when we've emitted all bytes
                        if (endIndex >= array.length && !cancelled.get()) {
                            subscriber.onComplete();
                        }
                    }

                    @Override
                    public void cancel() {
                        cancelled.set(true);
                    }
                });
            }
        };
    }

    @Override
    public Publisher<byte[]> newArrayPublisher() {
        // Implement custom Publisher to handle multiple requests similar to Reactor
        return new AbstractReactiveHelloWorldPublisher<byte[]>() {
            @Override
            protected Subscription createSubscription(
                    final Subscriber<? super byte[]> subscriber,
                    final AtomicLong pending,
                    final AtomicBoolean cancelled
            ) {
                return AbstractReactiveHelloWorldPublisher.createStandardSubscription(
                        subscriber, pending, cancelled,
                        (sub, pend, canc) -> {
                            // Subscribe to octet publisher and collect bytes
                            // Note: We use 'subscriber' directly instead of 'sub' to avoid type inference issues
                            newOctetPublisher().subscribe(new Subscriber<Byte>() {
                                private final java.util.List<Byte> bytes
                                        = new java.util.ArrayList<>();

                                private Subscription subscription;

                                @Override
                                public void onSubscribe(final Subscription s) {
                                    this.subscription = s;
                                    s.request(HelloWorld.BYTES);
                                }

                                @Override
                                public void onNext(final Byte item) {
                                    bytes.add(item);
                                }

                                @Override
                                public void onError(final Throwable t) {
                                    if (AbstractReactiveHelloWorldPublisher.isNotCancelled(canc)) {
                                        subscriber.onError(t);
                                    }
                                }

                                @Override
                                public void onComplete() {
                                    if (AbstractReactiveHelloWorldPublisher.isNotCancelled(canc)) {
                                        final var array
                                                = ReactiveHelloWorldFactoryUtils.toByteArray(bytes);
                                        log.debug("publishing array: {}", array);
                                        subscriber.onNext(array);
                                        pend.decrementAndGet();
                                        AbstractReactiveHelloWorldPublisher.completeIfNoPending(
                                                subscriber, pend, canc);
                                    }
                                }
                            });
                        }
                );
            }
        };
    }

    @Override
    public Publisher<String> newStringPublisher() {
        // Implement custom Publisher to handle multiple requests similar to Reactor
        return new AbstractReactiveHelloWorldPublisher<String>() {
            @Override
            protected Subscription createSubscription(
                    final Subscriber<? super String> subscriber,
                    final AtomicLong pending,
                    final AtomicBoolean cancelled
            ) {
                return AbstractReactiveHelloWorldPublisher.createStandardSubscription(
                        subscriber, pending, cancelled,
                        (sub, pend, canc) -> {
                            // Subscribe to array publisher and get first array
                            newArrayPublisher().subscribe(new Subscriber<byte[]>() {
                                private Subscription subscription;

                                @Override
                                public void onSubscribe(final Subscription s) {
                                    this.subscription = s;
                                    s.request(1); // Request one array
                                }

                                @Override
                                public void onNext(final byte[] array) {
                                    if (AbstractReactiveHelloWorldPublisher.isNotCancelled(canc)) {
                                        final var string = StandardCharsets.US_ASCII.decode(
                                                ByteBuffer.wrap(array)
                                        ).toString();
                                        log.debug("publishing string: {}", string);
                                        subscriber.onNext(string);
                                        subscription.cancel(); // We only need one
                                        pend.decrementAndGet();
                                        AbstractReactiveHelloWorldPublisher.completeIfNoPending(
                                                subscriber, pend, canc);
                                    }
                                }

                                @Override
                                public void onError(final Throwable t) {
                                    if (AbstractReactiveHelloWorldPublisher.isNotCancelled(canc)) {
                                        subscriber.onError(t);
                                    }
                                }

                                @Override
                                public void onComplete() {
                                    // Already handled in onNext
                                }
                            });
                        }
                );
            }
        };
    }

    // ---------------------------------------------------------------------------------------------
    private final Vertx vertx;
}
