package com.github.jinahya.hello._05_java_util_concurrent;

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

import com.github.jinahya.hello.HelloWorldFlow;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.FlowAdapters;
import org.reactivestreams.Subscriber;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Tests {@link FlowAdapters} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@SuppressWarnings({"unchecked"})
@Slf4j
class HelloWorldFlow_11_ReactiveStreams_FlowAdapters_Test extends _HelloWorldFlowTest {

    private static final class ReactiveStreamsSubscriber<T>
            implements org.reactivestreams.Subscriber<T> {

        private ReactiveStreamsSubscriber() {
            super();
        }

        @Override
        public String toString() {
            return String.format("[reactivestreams-subscriber@%1$08x]", hashCode());
        }

        @Override
        public void onSubscribe(final org.reactivestreams.Subscription s) {
            log.debug("{}.onSubscribe({})", this, s);
            this.s = s;
            this.s.request(1L);
        }

        @Override
        public void onNext(final T t) {
            log.debug("{}.onNext({})", this, t);
            if (ThreadLocalRandom.current().nextBoolean()) {
                s.request(1L);
            }
            if (ThreadLocalRandom.current().nextBoolean()) {
                s.cancel();
            }
        }

        @Override
        public void onError(final Throwable t) {
            log.error("{}.onError({})", this, t, t);
        }

        @Override
        public void onComplete() {
            log.debug("{}.onComplete()", this);
        }

        private org.reactivestreams.Subscription s;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Class for testing {@link FlowAdapters#toPublisher(Flow.Publisher)} method.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @DisplayName("toPublisher(Flow.Publisher)")
    @Nested
    class ToPublisherTest {

        /**
         * Just an alien service accepts an instance of {@link org.reactivestreams.Publisher}, and
         * {@link org.reactivestreams.Publisher#subscribe(Subscriber) subscribes}, to the
         * {@code publisher}, an instance of
         * {@link ReactiveStreamsSubscriber ReactiveStreamsSubscriber}.
         *
         * @param <T> item type parameter
         */
        private static class AlienService<T>
                implements Consumer<org.reactivestreams.Publisher<? extends T>> {

            @Override
            public void accept(final org.reactivestreams.Publisher<? extends T> publisher) {
                Objects.requireNonNull(publisher, "publisher is null");
                publisher.subscribe(new ReactiveStreamsSubscriber<>());
            }
        }

        @SuppressWarnings({"unchecked"})
        private <T> void __(Flow.Publisher<T> publisher) {
            // ------------------------------------------------------------------------------- given
            publisher = Mockito.spy(publisher);
            // intercept, <publisher.subscribe(subscriber)>, to wrap the <subscriber> as a spy
            BDDMockito.willAnswer(i -> {
                final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
                // intercept, <subscriber.onSubscribe(subscription)>,
                //       to wrap the <subscription> as a spy
                BDDMockito.willAnswer(j -> {
                    final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                    j.getArguments()[0] = subscription;
                    return j.callRealMethod();
                }).given(subscriber).onSubscribe(ArgumentMatchers.any());
                i.getArguments()[0] = subscriber;
                return i.callRealMethod();
            }).given(publisher).subscribe(ArgumentMatchers.any());
            // -------------------------------------------------------------------------------- when
            new AlienService<T>().accept(FlowAdapters.toPublisher(publisher));
            // -------------------------------------------------------------------------------- then
            // verify, <publisher.subscribe(subscriber)> invoked, once
            final Flow.Subscriber<T> subscriber;
            {
                final var captor = ArgumentCaptor.forClass(Flow.Subscriber.class);
                Mockito.verify(publisher, Mockito.times(1)).subscribe(captor.capture());
                subscriber = captor.getValue();
            }
            // verify, <subscriber.onSubscribe(subscription)> invoked, once
            final Flow.Subscription subscription;
            {
                final var captor = ArgumentCaptor.forClass(Flow.Subscription.class);
                Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(captor.capture());
                subscription = captor.getValue();
            }
            // verify, <subscription.request(n)> invoked, at least once
            Mockito.verify(subscription, Mockito.atLeastOnce())
                    .request(ArgumentMatchers.longThat(n -> n > 0L));
            // verify, <subscriber.onComplete()> invoked, at most once
            Mockito.verify(subscriber, Mockito.atMostOnce()).onComplete();
            // verify, <subscriber.onError(throwable)> invoked, at most once
            Mockito.verify(subscriber, Mockito.atMostOnce()).onError(ArgumentMatchers.notNull());
            // cancel
            subscription.cancel();
        }

        @Test
        void __byte() {
            __(
                    new HelloWorldFlow.HelloWorldPublisher.OfByte(
                            service(),
                            Executors.newVirtualThreadPerTaskExecutor()
                    )
            );
        }

        @Test
        void __array() {
            __(
                    new HelloWorldFlow.HelloWorldPublisher.OfArray(
                            service(),
                            Executors.newVirtualThreadPerTaskExecutor()
                    )
            );
        }

        @Test
        void __buffer() {
            __(
                    new HelloWorldFlow.HelloWorldPublisher.OfBuffer(
                            service(),
                            Executors.newVirtualThreadPerTaskExecutor()
                    )
            );
        }

        @Test
        void __string() {
            // ------------------------------------------------------------------------------- given
            __(
                    new HelloWorldFlow.HelloWorldPublisher.OfString(
                            service(),
                            Executors.newVirtualThreadPerTaskExecutor()
                    )
            );
        }
    }

    /**
     * Class for testing {@link FlowAdapters#toFlowPublisher(org.reactivestreams.Publisher)}
     * method.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @DisplayName("toFlowPublisher(Publisher)")
    @Nested
    class ToFlowPublisherTest {

        private interface AlienService<T>
                extends Supplier<org.reactivestreams.Publisher<T>> {

        }
    }

    /**
     * A class for testing {@link FlowAdapters#toSubscriber(Flow.Subscriber)} method.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @DisplayName("toSubscriber(Flow.Subscriber)")
    @Nested
    class ToSubscriberTest {

    }

    /**
     * A class for testing {@link FlowAdapters#toFlowSubscriber(org.reactivestreams.Subscriber)}
     * method.
     *
     * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
     */
    @DisplayName("toFlowSubscriber(Subscriber)")
    @Nested
    class ToFlowSubscriberTest {

        private static class AlienSupplier<T>
                implements Supplier<org.reactivestreams.Subscriber<T>> {

            @Override
            public org.reactivestreams.Subscriber<T> get() {
                return new ReactiveStreamsSubscriber<>();
            }
        }

        <T> void __(Flow.Publisher<T> publisher) {
            // ------------------------------------------------------------------------------- given
            publisher = Mockito.spy(publisher);
            final Flow.Subscriber<T> subscriber = Mockito.spy(
                    FlowAdapters.toFlowSubscriber(new AlienSupplier<T>().get()));
            // intercept, <subscriber.onSubscribe(subscription)>
            //         to wrap the <subscription> as a spy
            BDDMockito.willAnswer(i -> {
                i.getArguments()[0] = Mockito.spy(i.getArgument(0, Flow.Subscription.class));
                return i.callRealMethod();
            }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
            // -------------------------------------------------------------------------------- when
            publisher.subscribe(subscriber);
            // -------------------------------------------------------------------------------- then
            // verify, <subscriber.onSubscribe(subscription)> invoked, once
            final Flow.Subscription subscription;
            {
                final var captor = ArgumentCaptor.forClass(Flow.Subscription.class);
                Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(captor.capture());
                subscription = captor.getValue();
            }
            // verify, <subscription.request(n)> invoked, at least once
            Mockito.verify(subscription, Mockito.atLeastOnce())
                    .request(ArgumentMatchers.longThat(n -> n > 0L));
            // cancel
            subscription.cancel();
        }

        @Test
        void __byte() {
            __(
                    new HelloWorldFlow.HelloWorldPublisher.OfByte(
                            service(),
                            Executors.newVirtualThreadPerTaskExecutor()
                    )
            );
        }

        @Test
        void __array() {
            __(
                    new HelloWorldFlow.HelloWorldPublisher.OfArray(
                            service(),
                            Executors.newVirtualThreadPerTaskExecutor()
                    )
            );
        }

        @Test
        void __buffer() {
            __(
                    new HelloWorldFlow.HelloWorldPublisher.OfBuffer(
                            service(),
                            Executors.newVirtualThreadPerTaskExecutor()
                    )
            );
        }

        @Test
        void __string() {
            __(
                    new HelloWorldFlow.HelloWorldPublisher.OfString(
                            service(),
                            Executors.newVirtualThreadPerTaskExecutor()
                    )
            );
        }
    }
}
