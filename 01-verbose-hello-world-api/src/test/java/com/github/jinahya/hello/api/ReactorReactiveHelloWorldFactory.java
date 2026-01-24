package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.nio.ByteBuffer;
import java.util.Objects;
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
public final class ReactorReactiveHelloWorldFactory extends AbstractReactiveHelloWorld {

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
    public Publisher<Byte> newBytePublisher() {
        final var array = service.set(new byte[HelloWorld.BYTES], 0);
        return Flux.fromStream(IntStream.range(0, array.length).mapToObj(i -> array[i]))
                .publishOn(scheduler)
                .doOnNext(b -> log.debug("publishing byte: 0x{}('{}')",
                                         String.format("%02x", b), (char) b.byteValue()));
    }

    @Override
    public Publisher<byte[]> newArrayPublisher() {
        if (false) {
            // FIRST ATTEMPT - using repeat() - FAILED because:
            // - repeat() makes Flux infinite, never completes
            // - Tests expect completion after requested items
            return Flux.defer(() -> Flux.from(newBytePublisher())
                            .collectList()
                            .map(l -> {
                                final var array = new byte[l.size()];
                                for (var i = 0; i < array.length; i++) {
                                    array[i] = l.get(i);
                                }
                                return array;
                            }))
                    .repeat()
                    .publishOn(scheduler)
                    .doOnNext(bytes -> log.debug("publishing array: {}", bytes));
        }
        if (false) {
            // SECOND ATTEMPT - using Flux.create with reused Mono - FAILED because:
            // 1. Reused the same Mono instance for all requests (Mono only completes once)
            // 2. Incorrect completion logic (only checked last item of single request)
            // 3. Race conditions with currentRequested tracking
            return Flux.create(sink -> {
                final var singleArrayMono = Flux.from(newBytePublisher())
                        .collectList()
                        .map(l -> {
                            final var array = new byte[l.size()];
                            for (var i = 0; i < array.length; i++) {
                                array[i] = l.get(i);
                            }
                            return array;
                        })
                        .publishOn(scheduler);
                final var requested = new java.util.concurrent.atomic.AtomicLong(0);
                final var completed = new java.util.concurrent.atomic.AtomicLong(0);
                sink.onRequest(n -> {
                    final var currentRequested = requested.addAndGet(n);
                    for (long i = 0; i < n && !sink.isCancelled(); i++) {
                        final var index = i;
                        singleArrayMono.subscribe(
                                array -> {
                                    log.debug("publishing array: {}", array);
                                    sink.next(array);
                                    final var done = completed.incrementAndGet();
                                    if (done == currentRequested) {
                                        sink.complete();
                                    }
                                },
                                sink::error
                        );
                    }
                });
            });
        }
        return Flux.create(sink -> {
            final var pending = new java.util.concurrent.atomic.AtomicLong(0);
            sink.onRequest(n -> {
                final var currentPending = pending.addAndGet(n);
                for (long i = 0; i < n && !sink.isCancelled(); i++) {
                    Flux.from(newBytePublisher())
                            .collectList()
                            .map(l -> {
                                final var array = new byte[l.size()];
                                for (var j = 0; j < array.length; j++) {
                                    array[j] = l.get(j);
                                }
                                return array;
                            })
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
            final var pending = new java.util.concurrent.atomic.AtomicLong(0);
            sink.onRequest(n -> {
                final var currentPending = pending.addAndGet(n);
                for (long i = 0; i < n && !sink.isCancelled(); i++) {
                    Flux.from(newArrayPublisher())
                            .next()
                            .map(array -> java.nio.charset.StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(array)).toString())
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
