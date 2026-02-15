package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * An example implementation of {@link ReactiveHelloWorldFactory} using SmallRye Mutiny.
 * <p>
 * This demonstrates how to implement {@link ReactiveHelloWorldFactory} using Mutiny's
 * {@link Multi}. Since Mutiny 3 uses {@code java.util.concurrent.Flow.Publisher} internally but we
 * need {@code org.reactivestreams.Publisher}, we create custom Publishers that use Multi
 * internally.
 * <p>
 * This is an example implementation for testing and educational purposes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class MutinyReactiveHelloWorldFactory
        extends AbstractReactiveHelloWorld {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with the specified service.
     *
     * @param service the underlying {@link HelloWorld} service
     */
    public MutinyReactiveHelloWorldFactory(final HelloWorld service) {
        super(service);
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Publisher<Byte> newOctetPublisher() {
        final var array = service.set(new byte[HelloWorld.BYTES], 0);
        // Create Multi and wrap it as org.reactivestreams.Publisher
        final var multi = Multi.createFrom().items(
                        IntStream.range(0, array.length).mapToObj(i -> array[i]).toArray(Byte[]::new)
                )
                .invoke(b -> log.debug("publishing byte: 0x{}('{}')",
                                       String.format("%02x", b), (char) b.byteValue()))
                .emitOn(Infrastructure.getDefaultExecutor()); // Use emitOn for async emission
        // Multi implements Flow.Publisher, convert to org.reactivestreams.Publisher
        return new Publisher<Byte>() {
            @Override
            public void subscribe(final Subscriber<? super Byte> subscriber) {
                // Create adapter from Flow.Subscriber to org.reactivestreams.Subscriber
                final var flowSubscriber = new java.util.concurrent.Flow.Subscriber<Byte>() {
                    private java.util.concurrent.Flow.Subscription flowSubscription;

                    @Override
                    public void onSubscribe(
                            final java.util.concurrent.Flow.Subscription subscription) {
                        this.flowSubscription = subscription;
                        // Call onSubscribe synchronously to satisfy Reactive Streams spec
                        subscriber.onSubscribe(new Subscription() {
                            @Override
                            public void request(final long n) {
                                if (flowSubscription != null) {
                                    flowSubscription.request(n);
                                }
                            }

                            @Override
                            public void cancel() {
                                if (flowSubscription != null) {
                                    flowSubscription.cancel();
                                }
                            }
                        });
                    }

                    @Override
                    public void onNext(final Byte item) {
                        subscriber.onNext(item);
                    }

                    @Override
                    public void onError(final Throwable throwable) {
                        subscriber.onError(throwable);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onComplete();
                    }
                };
                ((java.util.concurrent.Flow.Publisher<Byte>) multi).subscribe(flowSubscriber);
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
}
