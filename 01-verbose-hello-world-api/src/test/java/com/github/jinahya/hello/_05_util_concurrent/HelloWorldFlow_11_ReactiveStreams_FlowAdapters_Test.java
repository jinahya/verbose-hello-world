package com.github.jinahya.hello._05_util_concurrent;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldFlow;
import com.github.jinahya.hello.HelloWorldFlow.HelloWorldPublisher;
import com.github.jinahya.hello._HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Subscription;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Tests {@link FlowAdapters} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldFlow_11_ReactiveStreams_FlowAdapters_Test extends _HelloWorldFlowTest {

    /**
     * A simple subscriber which requests {@value HelloWorld#BYTES} items on its
     * {@link org.reactivestreams.Subscriber#onSubscribe(Subscription) onSubscribe(subscription)}
     * method and {@link Subscription#cancel() cancels} the subscription on the
     * {@value HelloWorld#BYTES}th item.
     *
     * @param <T> item type parameter
     */
    private static final class ReactiveStreamsSubscriber<T> // @formatter:off
            implements org.reactivestreams.Subscriber<T> {
        private ReactiveStreamsSubscriber() { super(); }
        @Override public String toString() {
            return String.format("[reactive-streams-subscriber@%1$08x]", hashCode());
        }
        @Override public void onSubscribe(final org.reactivestreams.Subscription s) {
            log.debug("{}.onSubscribe({})", this, s);
            this.s = Mockito.spy(s);
            i = 0;
            n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES) + 1;
            log.debug("  - requesting {} items...", n);
            this.s.request(n);
        }
        @Override public void onNext(final T t) {
            log.debug("{}.onNext({})", this, t);
            if (++i == n) {
                this.s.cancel();
            }
        }
        @Override public void onError(final Throwable t) {
            log.error("{}.onError({})", this, t, t);
        }
        @Override public void onComplete() {
            log.debug("{}.onComplete()", this);
        }
        private org.reactivestreams.Subscription s;
        private int n;
        private int i;
    } // @formatter:on

    // ---------------------------------------------------------------------------------------------
    @DisplayName("toPublisher(flowPublisher)")
    @Nested
    class ToPublisherTest {

        @Test
        void __byte() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var flowPublisher =
                    new HelloWorldFlow.HelloWorldPublisher.OfByte(service, EXECUTOR);
            final var reactiveStreamsPublisher = FlowAdapters.toPublisher(flowPublisher);
            final var reactiveStreamsSubscriber =
                    Mockito.spy(new ReactiveStreamsSubscriber<Byte>());
            // -------------------------------------------------------------------------------- when
            reactiveStreamsPublisher.subscribe(reactiveStreamsSubscriber);
            // -------------------------------------------------------------------------------- then
            _HelloWorldTestUtils.awaitForOneSecond();
            Mockito.verify(reactiveStreamsSubscriber, Mockito.times(1))
                    .onSubscribe(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.atLeast(1))
                    .request(ArgumentMatchers.longThat(v -> v > 0L));
            Mockito.verify(reactiveStreamsSubscriber, Mockito.atMost(HelloWorld.BYTES))
                    .onNext(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.times(1))
                    .cancel();
        }

        @Test
        void __array() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var flowPublisher = new HelloWorldPublisher.OfArray(service, EXECUTOR);
            final var reactiveStreamsPublisher = FlowAdapters.toPublisher(flowPublisher);
            final var reactiveStreamsSubscriber =
                    Mockito.spy(new ReactiveStreamsSubscriber<byte[]>());
            // -------------------------------------------------------------------------------- when
            reactiveStreamsPublisher.subscribe(reactiveStreamsSubscriber);
            // -------------------------------------------------------------------------------- then
            _HelloWorldTestUtils.awaitForOneSecond();
            Mockito.verify(reactiveStreamsSubscriber, Mockito.times(1))
                    .onSubscribe(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.atLeast(1))
                    .request(ArgumentMatchers.longThat(v -> v > 0L));
            Mockito.verify(reactiveStreamsSubscriber, Mockito.atMost(HelloWorld.BYTES))
                    .onNext(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.times(1))
                    .cancel();
        }

        @Test
        void __buffer() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var flowPublisher = new HelloWorldPublisher.OfBuffer(service, EXECUTOR);
            final var reactiveStreamsPublisher = FlowAdapters.toPublisher(flowPublisher);
            final var reactiveStreamsSubscriber =
                    Mockito.spy(new ReactiveStreamsSubscriber<ByteBuffer>());
            // -------------------------------------------------------------------------------- when
            reactiveStreamsPublisher.subscribe(reactiveStreamsSubscriber);
            // -------------------------------------------------------------------------------- then
            _HelloWorldTestUtils.awaitForOneSecond();
            Mockito.verify(reactiveStreamsSubscriber, Mockito.times(1))
                    .onSubscribe(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.atLeast(1))
                    .request(ArgumentMatchers.longThat(v -> v > 0L));
            Mockito.verify(reactiveStreamsSubscriber, Mockito.atMost(HelloWorld.BYTES))
                    .onNext(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.times(1))
                    .cancel();
        }

        @Test
        void __string() {
            // TODO: Test!
        }
    }

    @DisplayName("toFlowPublisher(reactiveStreamsSubscriber)")
    @Nested
    class ToFlowSubscriberTest {

        @Test
        void __byte() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var reactiveStreamsSubscriber =
                    Mockito.spy(new ReactiveStreamsSubscriber<Byte>());
            final var flowSubscriber = FlowAdapters.toFlowSubscriber(reactiveStreamsSubscriber);
            final var flowPublisher = new HelloWorldPublisher.OfByte(service, EXECUTOR);
            // -------------------------------------------------------------------------------- when
            flowPublisher.subscribe(flowSubscriber);
            _HelloWorldTestUtils.awaitForOneSecond();
            // -------------------------------------------------------------------------------- then
            Mockito.verify(reactiveStreamsSubscriber, Mockito.times(1))
                    .onSubscribe(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.atLeast(1))
                    .request(ArgumentMatchers.longThat(v -> v > 0L));
            Mockito.verify(reactiveStreamsSubscriber, Mockito.atMost(HelloWorld.BYTES))
                    .onNext(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.times(1))
                    .cancel();
        }

        @Test
        void __array() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var reactiveStreamsSubscriber =
                    Mockito.spy(new ReactiveStreamsSubscriber<byte[]>());
            final var flowSubscriber = FlowAdapters.toFlowSubscriber(reactiveStreamsSubscriber);
            final var flowPublisher = new HelloWorldPublisher.OfArray(service, EXECUTOR);
            // -------------------------------------------------------------------------------- when
            flowPublisher.subscribe(flowSubscriber);
            _HelloWorldTestUtils.awaitForOneSecond();
            // -------------------------------------------------------------------------------- then
            Mockito.verify(reactiveStreamsSubscriber, Mockito.times(1))
                    .onSubscribe(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.atLeast(1))
                    .request(ArgumentMatchers.longThat(v -> v > 0L));
            Mockito.verify(reactiveStreamsSubscriber, Mockito.atMost(HelloWorld.BYTES))
                    .onNext(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.times(1))
                    .cancel();
        }

        @Test
        void __buffer() {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var reactiveStreamsSubscriber =
                    Mockito.spy(new ReactiveStreamsSubscriber<ByteBuffer>());
            final var flowSubscriber = FlowAdapters.toFlowSubscriber(reactiveStreamsSubscriber);
            final var flowPublisher = new HelloWorldPublisher.OfBuffer(service, EXECUTOR);
            // -------------------------------------------------------------------------------- when
            flowPublisher.subscribe(flowSubscriber);
            _HelloWorldTestUtils.awaitForOneSecond();
            // -------------------------------------------------------------------------------- then
            Mockito.verify(reactiveStreamsSubscriber, Mockito.times(1))
                    .onSubscribe(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.atLeast(1))
                    .request(ArgumentMatchers.longThat(v -> v > 0L));
            Mockito.verify(reactiveStreamsSubscriber, Mockito.atMost(HelloWorld.BYTES))
                    .onNext(ArgumentMatchers.notNull());
            Mockito.verify(reactiveStreamsSubscriber.s, Mockito.times(1))
                    .cancel();
        }

        @Test
        void __string() {
            // TODO: Test!
        }
    }
}
