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
import com.github.jinahya.hello._HelloWorldTestUtils;
import io.smallrye.mutiny.Multi;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Flow;

@DisplayName("MultiCreate")
@Slf4j
class HelloWorldFlow_14_Mutiny_MultiCreate_Test extends _HelloWorldFlowTest {

    @DisplayName("publisher(Flow.Publisher)")
    @Nested
    class PublisherTest {

        @Test
        @SuppressWarnings({"unchecked"})
        void __byte() {
            // ------------------------------------------------------------------------------- given
            interface AlienService { // @formatter:off
                default void doSome(final Multi<? extends Byte> multi) {
                    final var cancellable = multi
                            .onItem().invoke(i -> {
                                log.debug("item: {}", (char) i.byteValue());
                            })
                            .map(i -> (char) i.byteValue())
                            .collect().in(StringBuilder::new, StringBuilder::append)
                            .subscribe()
                            .with(i -> {
                                log.debug("item: {}", i);
                            });
                    _HelloWorldTestUtils.awaitForOneSecond();
                    cancellable.cancel(); // do we have log for this?
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
            }.doSome(Multi.createFrom().publisher(publisher));
            _HelloWorldTestUtils.awaitForOneSecond();
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
            Mockito.verify(subscriber, Mockito.times(HelloWorld.BYTES))
                    .onNext(ArgumentMatchers.notNull());
            // verify, subscriber.onComplete() invoked, once
            Mockito.verify(subscriber, Mockito.times(1)).onComplete();
            // verify, subscription.cancel() invoked, once
            Mockito.verify(subscription, Mockito.never()).cancel(); // do we have log for this?
        }

        @Test
        @SuppressWarnings({"unchecked"})
        void __array() {
            interface AlienService { // @formatter:off
                default void doSome(final Multi<? extends byte[]> multi) {
                    final var cancellable = multi
                            .select().first(3)
                            .onItem().invoke(i -> {
                                log.debug("item: {}", i);
                            })
                            .map(i -> new String(i, StandardCharsets.US_ASCII))
                            .subscribe().with(i -> {
                                log.debug("item: {}", i);
                            });
                    _HelloWorldTestUtils.awaitForOneSecond();
                    // not cancelling the cancellable!
                }
            } // @formatter:on
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfArray(service, EXECUTOR)
            );
            BDDMockito.willAnswer(i -> {
                final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
                BDDMockito.willAnswer(j -> {
                    final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                    j.getArguments()[0] = subscription;
                    return j.callRealMethod();
                }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
                i.getArguments()[0] = subscriber;
                return i.callRealMethod();
            }).given(publisher).subscribe(ArgumentMatchers.notNull());
            final var multi = Multi.createFrom().publisher(publisher);
            // -------------------------------------------------------------------------------- when
            new AlienService() {
            }.doSome(multi);
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
            // verify, subscription.cancel() invoked, once
            Mockito.verify(subscription, Mockito.times(1)).cancel(); // why?
        }

        @Test
        @SuppressWarnings({"unchecked"})
        void __buffer() {
            interface AlienService { // @formatter:off
                default void doSome(final Multi<? extends ByteBuffer> multi) {
                    final var cancellable = multi
                            .select().first(3)
                            .onItem().invoke(i -> {
                                log.debug("item: {}", i);
                            })
                            .map(StandardCharsets.US_ASCII::decode)
                            .subscribe().with(i -> {
                                log.debug("item: {}", i);
                            });
                    _HelloWorldTestUtils.awaitForOneSecond();
                    // not cancelling the cancellable
                }
            } // @formatter:on
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var publisher = Mockito.spy(
                    new HelloWorldFlow.HelloWorldPublisher.OfBuffer(service, EXECUTOR)
            );
            BDDMockito.willAnswer(i -> {
                final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
                BDDMockito.willAnswer(j -> {
                    final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                    j.getArguments()[0] = subscription;
                    return j.callRealMethod();
                }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
                i.getArguments()[0] = subscriber;
                return i.callRealMethod();
            }).given(publisher).subscribe(ArgumentMatchers.notNull());
            final var multi = Multi.createFrom().publisher(publisher);
            // -------------------------------------------------------------------------------- when
            new AlienService() {
            }.doSome(multi);
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
            // verify, subscription.cancel() invoked, once
            Mockito.verify(subscription, Mockito.times(1)).cancel(); // why?
        }

        @Test
        void __string() {
            // TODO: test!
        }
    }
}
