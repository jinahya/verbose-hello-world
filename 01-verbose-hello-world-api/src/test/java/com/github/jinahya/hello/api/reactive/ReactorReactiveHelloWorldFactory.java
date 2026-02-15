package com.github.jinahya.hello.api.reactive;

import com.github.jinahya.hello.api.HelloWorld;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

/**
 * An example implementation of {@link ReactiveHelloWorldFactory} using Project Reactor.
 * <p>
 * This demonstrates how to implement {@link ReactiveHelloWorldFactory} using Reactor's
 * {@link Flux}. Reactor's {@code Flux} implements {@link org.reactivestreams.Publisher}, so it can
 * be returned directly from the interface methods.
 * <p>
 * This is an example implementation for testing and educational purposes.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class ReactorReactiveHelloWorldFactory
        extends AbstractReactiveHelloWorld {

    // ---------------------------------------------------------------------------------------------

    /**
     * Creates a new instance with the specified service and scheduler.
     *
     * @param service   the underlying {@link HelloWorld} service
     * @param scheduler the scheduler for publishing items
     */
    public ReactorReactiveHelloWorldFactory(final HelloWorld service, final Scheduler scheduler) {
        super(service);
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Publisher<Byte> newOctetPublisher() {
        final var array = service.set(new byte[HelloWorld.BYTES], 0);
        return Flux.fromStream(IntStream.range(0, array.length).mapToObj(i -> array[i]))
                .publishOn(scheduler)
                .doOnNext(b -> log.debug("publishing byte: 0x{}('{}')",
                                         String.format("%02x", b), (char) b.byteValue()));
    }

    @Override
    public Publisher<byte[]> newArrayPublisher() {
        return Flux.create(sink -> {
            final var pending = new AtomicLong(0);
            sink.onRequest(n -> {
                pending.addAndGet(n); // no overflow handled
                for (long i = 0; i < n && !sink.isCancelled(); i++) {
                    Flux.from(newOctetPublisher())
                            .collectList()
                            .map(ReactiveHelloWorldFactoryUtils::toByteArray)
                            .publishOn(scheduler)
                            .subscribe(
                                    array -> {
                                        log.debug("publishing array: {}", array);
                                        sink.next(array);
                                        final var remaining = pending.decrementAndGet();
                                        if (remaining == 0 && !sink.isCancelled()) {
                                            sink.complete();
                                        }
                                    },
                                    sink::error
                            );
                }
            });
        });
    }

    @Override
    public Publisher<String> newStringPublisher() {
        return Flux.create(sink -> {
            final var pending = new AtomicLong(0);
            sink.onRequest(n -> {
                pending.addAndGet(n); // no overflow handled
                for (long i = 0; i < n && !sink.isCancelled(); i++) {
                    Flux.from(newArrayPublisher())
                            .next()
                            .map(array -> StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(array))
                                    .toString())
                            .publishOn(scheduler)
                            .subscribe(
                                    string -> {
                                        log.debug("publishing string: {}", string);
                                        sink.next(string);
                                        final var remaining = pending.decrementAndGet();
                                        if (remaining == 0 && !sink.isCancelled()) {
                                            sink.complete();
                                        }
                                    },
                                    sink::error
                            );
                }
            });
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Converts a byte array to a Byte array for Flux.fromArray().
     *
     * @param bytes the byte array
     * @return a Byte array
     */
    private static Byte[] convertToByteArray(final byte[] bytes) {
        final var result = new Byte[bytes.length];
        for (var i = 0; i < bytes.length; i++) {
            result[i] = bytes[i];
        }
        return result;
    }

    // ---------------------------------------------------------------------------------------------
    private final Scheduler scheduler;
}
