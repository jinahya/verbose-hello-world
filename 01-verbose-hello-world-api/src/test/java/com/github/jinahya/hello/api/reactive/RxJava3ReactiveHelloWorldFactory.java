package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
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
 * An example implementation of {@link ReactiveHelloWorldFactory} using RxJava 3.
 * <p>
 * This demonstrates how to implement {@link ReactiveHelloWorldFactory} using RxJava 3's
 * {@link Flowable}. RxJava 3's {@code Flowable} implements {@link org.reactivestreams.Publisher},
 * so it can be returned directly from the interface methods.
 * <p>
 * This is an example implementation for testing and educational purposes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class RxJava3ReactiveHelloWorldFactory
        extends AbstractReactiveHelloWorldFactory {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with the specified service and scheduler.
     *
     * @param service   the underlying {@link HelloWorld} service
     * @param scheduler the scheduler for publishing items
     */
    public RxJava3ReactiveHelloWorldFactory(final HelloWorld service, final Scheduler scheduler) {
        super(service);
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Publisher<Byte> newOctetPublisher() {
        final var array = service.set(new byte[HelloWorld.BYTES], 0);
        return Flowable.fromStream(IntStream.range(0, array.length).mapToObj(i -> array[i]))
                .observeOn(scheduler)
                .doOnNext(b -> log.debug("publishing byte: 0x{}('{}')",
                                         String.format("%02x", b), (char) b.byteValue()));
    }

    @Override
    public Publisher<byte[]> newArrayPublisher() {
        // Implement custom Publisher to handle multiple requests similar to Reactor
        // RxJava 3's Flowable.create() with BackpressureStrategy doesn't support
        // setRequestHandler(), so we implement a custom Publisher
        return Flowable.fromPublisher(new AbstractReactiveHelloWorldPublisher<byte[]>() {
            @Override
            protected Subscription createSubscription(
                    final Subscriber<? super byte[]> subscriber,
                    final AtomicLong pending,
                    final AtomicBoolean cancelled
            ) {
                return AbstractReactiveHelloWorldPublisher.createStandardSubscription(
                        subscriber, pending, cancelled,
                        (sub, pend, canc) -> {
                            Flowable.fromPublisher(newOctetPublisher())
                                    .toList()
                                    .map(ReactiveHelloWorldFactoryUtils::toByteArray)
                                    .observeOn(scheduler)
                                    .subscribe(
                                            array -> {
                                                if (AbstractReactiveHelloWorldPublisher.isNotCancelled(
                                                        canc)) {
                                                    log.debug("publishing array: {}", array);
                                                    subscriber.onNext(array);
                                                    pend.decrementAndGet();
                                                    AbstractReactiveHelloWorldPublisher.completeIfNoPending(
                                                            subscriber, pend, canc);
                                                }
                                            },
                                            error -> {
                                                if (AbstractReactiveHelloWorldPublisher.isNotCancelled(
                                                        canc)) {
                                                    subscriber.onError(error);
                                                }
                                            }
                                    );
                        }
                );
            }
        });
    }

    @Override
    public Publisher<String> newStringPublisher() {
        // Implement custom Publisher to handle multiple requests similar to Reactor
        return Flowable.fromPublisher(new AbstractReactiveHelloWorldPublisher<String>() {
            @Override
            protected Subscription createSubscription(
                    final Subscriber<? super String> subscriber,
                    final AtomicLong pending,
                    final AtomicBoolean cancelled
            ) {
                return AbstractReactiveHelloWorldPublisher.createStandardSubscription(
                        subscriber, pending, cancelled,
                        (sub, pend, canc) -> {
                            Flowable.fromPublisher(newArrayPublisher())
                                    .firstElement()
                                    .map(array -> StandardCharsets.US_ASCII.decode(
                                                    ByteBuffer.wrap(array))
                                            .toString())
                                    .observeOn(scheduler)
                                    .subscribe(
                                            string -> {
                                                if (AbstractReactiveHelloWorldPublisher.isNotCancelled(
                                                        canc)) {
                                                    log.debug("publishing string: {}", string);
                                                    subscriber.onNext(string);
                                                    pend.decrementAndGet();
                                                    AbstractReactiveHelloWorldPublisher.completeIfNoPending(
                                                            subscriber, pend, canc);
                                                }
                                            },
                                            error -> {
                                                if (AbstractReactiveHelloWorldPublisher.isNotCancelled(
                                                        canc)) {
                                                    subscriber.onError(error);
                                                }
                                            }
                                    );
                        }
                );
            }
        });
    }

    // ---------------------------------------------------------------------------------------------
    private final Scheduler scheduler;
}
