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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldFlow;
import com.github.jinahya.hello.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow;

@DisplayName("JdkFlowAdapter")
@Slf4j
class HelloWorldFlow_13_Reactor_JdkFlowAdapter_Test extends _HelloWorldFlowTest {

    // -----------------------------------------------------------------------------------------------------------------

    @DisplayName("flowPublisherToFlux(publisher)")
    @Nested
    class FlowPublisherToFluxTest {

        @Test
        @SuppressWarnings({"unchecked"})
        void _byte() {
            // ------------------------------------------------------------------------------- given
            interface AlienService { // @formatter:off
                default void doSome(final Flux<? extends Byte> flux) {
                    final var disposable = flux
                            .doOnNext(i -> log.debug("item: {}", String.format("%02x", i)))
                            .map(i -> (char) i.byteValue())
                            .reduce(new StringBuilder(), StringBuilder::append)
                            .doOnNext(v -> log.debug("item: {}", v))
                            .subscribe();
                    HelloWorldTestUtils.awaitForOneSecond();
                    disposable.dispose();
                }
            } // @formatter:on
            final var service = service();
            final var publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfByte(service, EXECUTOR)
            );
            // intercept, publisher.subscribe(subscriber) to wrap the subscriber as a spy
            BDDMockito.willAnswer(i -> {
                final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
                // intercept, subscriber.onSubscribe(subscription) to wrap the subscription as a spy
                BDDMockito.willAnswer(j -> {
                    final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                    j.getArguments()[0] = subscription;
                    return j.callRealMethod();
                }).given(subscriber).onSubscribe(ArgumentMatchers.any());
                i.getArguments()[0] = subscriber;
                return i.callRealMethod();
            }).given(publisher).subscribe(ArgumentMatchers.any());
            // -------------------------------------------------------------------------------- when
            new AlienService() {
            }.doSome(JdkFlowAdapter.flowPublisherToFlux(publisher));
            // -------------------------------------------------------------------------------- then
            final var subscriberCaptor = ArgumentCaptor.forClass(Flow.Subscriber.class);
            // verify, publisher.subscribe(subscriber) invoked, once
            Mockito.verify(publisher, Mockito.times(1)).subscribe(subscriberCaptor.capture());
            final var subscriber = subscriberCaptor.getValue();
            final var subscriptionCaptor = ArgumentCaptor.forClass(Flow.Subscription.class);
            // verify, subscriber.onSubscribe(subscription) invoked, once
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
            final var subscription = subscriptionCaptor.getValue();
            // verify, subscription.request(Long.MAX_VALUE) invoked, once
            Mockito.verify(subscription, Mockito.times(1)).request(Long.MAX_VALUE);
            // verify, subscriber.onNext(nonnull) invoked, HelloWorld.BYTES-times
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscriber, Mockito.times(HelloWorld.BYTES))
                        .onNext(ArgumentMatchers.notNull());
            });
            // verify, subscriber.onComplete() invoked, once
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscriber, Mockito.times(1)).onComplete();
            });
            // verify, subscription.cancel() invoked, once
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscription, Mockito.never()).cancel(); // do we have log for this?
            });
        }

        @Test
        @SuppressWarnings({"unchecked"})
        void _array() {
            // ------------------------------------------------------------------------------- given
            interface AlienService { // @formatter:off
                default void doSome(final Flux<? extends byte[]> flux) {
                    final var disposable = flux
                            .doOnNext(i -> log.debug("item: {}", i))
                            .map(i -> new String(i, StandardCharsets.US_ASCII))
                            .doOnNext(v -> log.debug("item: {}", v))
                            .take(3L)
                            .subscribe();
                    HelloWorldTestUtils.awaitForOneSecond();
                    disposable.dispose();
                }
            } // @formatter:on
            final var service = service();
            final var publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfArray(service, EXECUTOR)
            );
            // DONE: intercept, publisher.subscribe(subscriber) to wrap the subscriber as a spy
            BDDMockito.willAnswer(i -> {
                final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
                // DONE: intercept, subscriber.onSubscribe(subscription) to wrap the subscription as a spy
                BDDMockito.willAnswer(j -> {
                    final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                    j.getArguments()[0] = subscription;
                    return j.callRealMethod();
                }).given(subscriber).onSubscribe(ArgumentMatchers.any());
                i.getArguments()[0] = subscriber;
                return i.callRealMethod();
            }).given(publisher).subscribe(ArgumentMatchers.any());
            // -------------------------------------------------------------------------------- when
            new AlienService() {
            }.doSome(JdkFlowAdapter.flowPublisherToFlux(publisher));
            // -------------------------------------------------------------------------------- then
            // DONE: verify, publisher.subscribe(subscriber) invoked, once
            final var subscriberCaptor = ArgumentCaptor.forClass(Flow.Subscriber.class);
            Mockito.verify(publisher, Mockito.times(1)).subscribe(subscriberCaptor.capture());
            final var subscriber = subscriberCaptor.getValue();
            // DONE: verify, subscriber.onSubscribe(subscription) invoked, once
            final var subscriptionCaptor = ArgumentCaptor.forClass(Flow.Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
            final var subscription = subscriptionCaptor.getValue();
            // DONE: verify, subscription.request(n) invoked, once
            final var nCaptor = ArgumentCaptor.forClass(long.class);
            Mockito.verify(subscription, Mockito.times(1)).request(nCaptor.capture());
            final var n = Math.toIntExact(nCaptor.getValue());
            // DONE: verify, subscriber.onNext(item) invoked, n-times
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscriber, Mockito.times(n)).onNext(ArgumentMatchers.notNull());
            });
            // DONE: verify, subscription.cancel() invoked, once
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscription, Mockito.times(1)).cancel();
            });
        }

        @Test
        @SuppressWarnings({"unchecked"})
        void _buffer() {
            // ------------------------------------------------------------------------------- given
            interface AlienService { // @formatter:off
                default void doSome(final Flux<? extends ByteBuffer> flux) {
                    final var disposable = flux
                            .doOnNext(i -> log.debug("item: {}", i))
                            .map(StandardCharsets.US_ASCII::decode)
                            .doOnNext(v -> log.debug("item: {}", v))
                            .take(3L)
                            .subscribe();
                    HelloWorldTestUtils.awaitForOneSecond();
                    disposable.dispose();
                }
            } // @formatter:on
            final var service = service();
            final var publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfBuffer(service, EXECUTOR)
            );
            // DONE: intercept, publisher.subscribe(subscriber) to wrap the subscriber as a spy
            BDDMockito.willAnswer(i -> {
                final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
                // DONE: intercept, subscriber.onSubscribe(subscription)
                //                  to wrap the subscription as a spy
                BDDMockito.willAnswer(j -> {
                    final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                    j.getArguments()[0] = subscription;
                    return j.callRealMethod();
                }).given(subscriber).onSubscribe(ArgumentMatchers.any());
                i.getArguments()[0] = subscriber;
                return i.callRealMethod();
            }).given(publisher).subscribe(ArgumentMatchers.any());
            // -------------------------------------------------------------------------------- when
            new AlienService() {
            }.doSome(JdkFlowAdapter.flowPublisherToFlux(publisher));
            // -------------------------------------------------------------------------------- then
            // DONE: verify, publisher.subscribe(subscriber) invoked, once
            final var subscriberCaptor = ArgumentCaptor.forClass(Flow.Subscriber.class);
            Mockito.verify(publisher, Mockito.times(1)).subscribe(subscriberCaptor.capture());
            final var subscriber = subscriberCaptor.getValue();
            // DONE: verify, subscriber.onSubscribe(subscription) invoked, once
            final var subscriptionCaptor = ArgumentCaptor.forClass(Flow.Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
            final var subscription = subscriptionCaptor.getValue();
            // DONE: verify, subscription.request(n) invoked, once
            final var nCaptor = ArgumentCaptor.forClass(long.class);
            Mockito.verify(subscription, Mockito.times(1)).request(nCaptor.capture());
            final var n = Math.toIntExact(nCaptor.getValue());
            // DONE: verify, subscriber.onNext(item) invoked, n-times
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscriber, Mockito.times(n)).onNext(ArgumentMatchers.notNull());
            });
            // DONE: verify, subscription.cancel() invoked, once
            Awaitility.await().untilAsserted(() -> {
                Mockito.verify(subscription, Mockito.times(1)).cancel();
            });
        }

        @Test
        void _string() {
            // TODO: test!
        }
    }
}
