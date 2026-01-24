package com.github.jinahya.hello.api.reactive;

import akka.actor.ActorSystem;
import akka.stream.javadsl.AsPublisher;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.github.jinahya.hello.api.HelloWorld;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * An example implementation of {@link ReactiveHelloWorldFactory} using Akka Streams.
 * <p>
 * This demonstrates how to implement {@link ReactiveHelloWorldFactory} using Akka Streams'
 * {@link Source}. Akka Streams' {@code Source} can be converted to a Reactive Streams
 * {@link Publisher} using {@link Sink#asPublisher(AsPublisher)}.
 * <p>
 * This is an example implementation for testing and educational purposes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class AkkaReactiveHelloWorldFactory extends AbstractReactiveHelloWorld {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with the specified service and actor system.
     *
     * @param service     the underlying {@link HelloWorld} service
     * @param actorSystem the actor system for materializing streams
     */
    public AkkaReactiveHelloWorldFactory(final HelloWorld service, final ActorSystem actorSystem) {
        super(service);
        this.actorSystem = Objects.requireNonNull(actorSystem, "actorSystem is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Publisher<Byte> newOctetPublisher() {
        final var array = service.set(new byte[HelloWorld.BYTES], 0);
        return Source.from(IntStream.range(0, array.length).mapToObj(i -> array[i]).toList())
                .map(b -> {
                    log.debug("publishing byte: 0x{}('{}')",
                              String.format("%02x", b), (char) b.byteValue());
                    return b;
                })
                .runWith(Sink.asPublisher(akka.stream.javadsl.AsPublisher.WITHOUT_FANOUT),
                         akka.stream.Materializer.matFromSystem(actorSystem));
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
                            newOctetPublisher().subscribe(
                                    new Subscriber<Byte>() {
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
                                            if (AbstractReactiveHelloWorldPublisher.isNotCancelled(
                                                    canc)) {
                                                subscriber.onError(t);
                                            }
                                        }

                                        @Override
                                        public void onComplete() {
                                            if (AbstractReactiveHelloWorldPublisher.isNotCancelled(
                                                    canc)) {
                                                final var array
                                                        = ReactiveHelloWorldFactoryUtils.toByteArray(
                                                        bytes);
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
                            newArrayPublisher().subscribe(
                                    new Subscriber<byte[]>() {
                                        private Subscription subscription;

                                        @Override
                                        public void onSubscribe(final Subscription s) {
                                            this.subscription = s;
                                            s.request(1); // Request one array
                                        }

                                        @Override
                                        public void onNext(final byte[] array) {
                                            if (AbstractReactiveHelloWorldPublisher.isNotCancelled(
                                                    canc)) {
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
                                            if (AbstractReactiveHelloWorldPublisher.isNotCancelled(
                                                    canc)) {
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
    private final ActorSystem actorSystem;
}
