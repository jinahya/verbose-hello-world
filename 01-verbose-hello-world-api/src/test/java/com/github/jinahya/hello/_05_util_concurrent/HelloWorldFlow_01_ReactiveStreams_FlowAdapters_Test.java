package com.github.jinahya.hello._05_util_concurrent;

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldFlow;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Subscription;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Tests {@link FlowAdapters} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldFlow_01_ReactiveStreams_FlowAdapters_Test extends _HelloWorldTest {

    /**
     * A simple subscriber which requests {@value HelloWorld#BYTES} items on its
     * {@link org.reactivestreams.Subscriber#onSubscribe(Subscription) onSubscribe(subscription)}
     * method and {@link Subscription#cancel() cancels} the subscription on the
     * {@value HelloWorld#BYTES}th item.
     *
     * @param <T> item type parameter
     */
    private static final class SimpleSubscriber<T> // @formatter:off
            implements org.reactivestreams.Subscriber<T> {
        private SimpleSubscriber() { super(); }
        @Override public void onSubscribe(final org.reactivestreams.Subscription s) {
            log.debug("onSubscribe({})", s);
            this.s = s;
            i = 0;
            this.s.request(HelloWorld.BYTES);
        }
        @Override public void onNext(final T t) {
            log.debug("onNext({})", t);
            if (++i == HelloWorld.BYTES) {
                this.s.cancel();
            }
        }
        @Override public void onError(final Throwable t) {
            log.error("onError({})", t, t);
        }
        @Override public void onComplete() {
            log.debug("onComplete()");
        }
        private org.reactivestreams.Subscription s;
        private int i;
    } // @formatter:on

    // ---------------------------------------------------------------------------------------------
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(
            Thread.ofVirtual().name("publisher-", 0L).factory()
    );

    @AfterAll
    static void closeTheExecutor() {
        EXECUTOR.close();
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void beforeEach() {
        putBuffer_willReturnTheBuffer(b -> {
            if (b == null || !b.hasRemaining()) {
                return;
            }
            IntStream.range(0, b.remaining()).forEach(i -> b.put((byte) i));
        });
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("toPublisher(flowPublisher)")
    @Nested
    class ToPublisherTest {

        @Test
        void __bytes() {
            final var flowPublisher = HelloWorldFlow.newPublisherForBytes(service(), EXECUTOR);
            final var reactiveStreamsPublisher = FlowAdapters.toPublisher(flowPublisher);
            final var reactiveStreamsSubscriber = new SimpleSubscriber<Byte>();
            reactiveStreamsPublisher.subscribe(reactiveStreamsSubscriber);
        }

        @Test
        void __array() {
            final var flowPublisher = HelloWorldFlow.newPublisherForArrays(service(), EXECUTOR);
            final var reactiveStreamsPublisher = FlowAdapters.toPublisher(flowPublisher);
            final var reactiveStreamsSubscriber = new SimpleSubscriber<byte[]>();
            reactiveStreamsPublisher.subscribe(reactiveStreamsSubscriber);
        }

        @Test
        void __buffers() {
            final var flowPublisher = HelloWorldFlow.newPublisherForBuffers(service(), EXECUTOR);
            final var reactiveStreamsPublisher = FlowAdapters.toPublisher(flowPublisher);
            final var reactiveStreamsSubscriber = new SimpleSubscriber<ByteBuffer>();
            reactiveStreamsPublisher.subscribe(reactiveStreamsSubscriber);
        }
    }

    @DisplayName("toFlowPublisher(reactiveStreamsSubscriber)")
    @Nested
    class ToFlowSubscriberTest {

        @Test
        void __bytes() {
            final var reactiveStreamsSubscriber = new SimpleSubscriber<Byte>();
            final var flowSubscriber = FlowAdapters.toFlowSubscriber(reactiveStreamsSubscriber);
            final var flowPublisher = HelloWorldFlow.newPublisherForBytes(service(), EXECUTOR);
            flowPublisher.subscribe(flowSubscriber);
        }

        @Test
        void __arrays() {
            final var reactiveStreamsSubscriber = new SimpleSubscriber<byte[]>();
            final var flowSubscriber = FlowAdapters.toFlowSubscriber(reactiveStreamsSubscriber);
            final var flowPublisher = HelloWorldFlow.newPublisherForArrays(service(), EXECUTOR);
            flowPublisher.subscribe(flowSubscriber);
        }

        @Test
        void __buffers() {
            final var reactiveStreamsSubscriber = new SimpleSubscriber<ByteBuffer>();
            final var flowSubscriber = FlowAdapters.toFlowSubscriber(reactiveStreamsSubscriber);
            final var flowPublisher = HelloWorldFlow.newPublisherForBuffers(service(), EXECUTOR);
            flowPublisher.subscribe(flowSubscriber);
        }
    }
}
