package com.github.jinahya.hello.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.time.Duration;
import java.util.ArrayList;

import static com.github.jinahya.hello.api.HelloWorldBookTestUtils.loggingSpy;
import static com.github.jinahya.hello.api.HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link ReactiveHelloWorldByteProcessor} with multiple downstream subscribers
 * attached to a single processor, each requesting a different total ({@code < 12}, {@code == 12},
 * {@code > 12}) and joining the processor with a random sub-second throttle.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class ReactiveHelloWorld_Byte_ProcessorTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(30L);

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorld_Byte_ProcessorTest() {
        super();
        service = Mockito.mock(HelloWorld.class, Mockito.CALLS_REAL_METHODS);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void stubService() {
        set_array_sets_actual_hello_world_bytes(service);
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    void __immediateClose() {
        try (final var processor = new ReactiveHelloWorldByteProcessor(service)) {
        }
    }

    @Test
    void __() { // @formatter:on
        // ----------------------------------------------------------------------------------- given
        final var ds = new int[] {
                HelloWorld.BYTES - 1, HelloWorld.BYTES + 1
        };
        try (final var processor = new ReactiveHelloWorldByteProcessor(service)) {
            final var subscribers = new ArrayList<Subscriber<Byte>>();
            for (final int d : ds) {
                final var subscriber = loggingSpy(new Subscriber<Byte>() {
                    @Override
                    public void onSubscribe(final Subscription s) {
                        Thread.ofVirtual().start(() -> {
                            for (int i = 0; i < d; i++) {
                                s.request(1L);
                            }
                        });
                    }

                    @Override
                    public void onNext(final Byte t) { /* drop */ }

                    @Override
                    public void onError(final Throwable t) { /* ignore */ }

                    @Override
                    public void onComplete() { /* ignore */ }
                });
                subscribers.add(subscriber);
                processor.subscribe(subscriber);
            }
            // -------------------------------------------------------------------------------- then
            await().atMost(TIMEOUT).untilAsserted(() -> {
                for (int i = 0; i < ds.length; i++) {
                    final var sub = subscribers.get(i);
                    if (ds[i] >= HelloWorld.BYTES) {
                        verify(sub, times(1)).onComplete();
                    } else {
                        verify(sub, times(ds[i])).onNext(any());
                    }
                }
            });
            for (int i = 0; i < ds.length; i++) {
                final var subscriber = subscribers.get(i);
                verify(subscriber, times(1)).onSubscribe(notNull());
                verify(subscriber, never()).onError(any());
                if (ds[i] >= HelloWorld.BYTES) {
                    verify(subscriber, atMost(HelloWorld.BYTES)).onNext(any());
                    verify(subscriber, times(1)).onComplete();
                } else {
                    verify(subscriber, times(ds[i])).onNext(any());
                    verify(subscriber, never()).onComplete();
                }
            }
        } // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;
}
