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
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.FlowAdapters;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
class HelloWorldFlow_12_RxJava extends _HelloWorldFlowTest {

    @Test
    @SuppressWarnings({"unchecked"})
    void __byte() {
        // ----------------------------------------------------------------------------------- given
        interface AlienService { // @formatter:off
            AtomicLong countRef = new AtomicLong();
            default void doWith(final Observable<? extends Byte> observable) {
                Objects.requireNonNull(observable, "observable is null");
                final var count = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES << 1) + 1;
                log.debug("count: {}", count);
                countRef.set(count);
                final var disposable = observable
                        .take(count)
                        .doOnNext(i -> log.debug("item: {}", i))
                        .subscribe();
                HelloWorldTestUtils.awaitForOneSecond();
                disposable.dispose();
            }
            default void doWith(final Flowable<? extends Byte> flowable) {
                Objects.requireNonNull(flowable, "flowable is null");
                doWith(flowable.toObservable());
            }
        } // @formatter:on
        final var service = service();
        final Flow.Publisher<Byte> publisher = Mockito.spy(
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
        // ------------------------------------------------------------------------------------ when
        final var reactiveStreamsPublisher = FlowAdapters.toPublisher(publisher);
        final var observable = ThreadLocalRandom.current().nextBoolean()
                               ? Observable.fromPublisher(reactiveStreamsPublisher)
                               : Flowable.fromPublisher(reactiveStreamsPublisher).toObservable();
        new AlienService() {
        }.doWith(observable);
        HelloWorldTestUtils.awaitForOneSecond();
        // ------------------------------------------------------------------------------------ then
        // verify, publisher.subscribe(subscriber) invoked, once
        final var subscriberCaptor = ArgumentCaptor.forClass(Flow.Subscriber.class);
        Mockito.verify(publisher, Mockito.times(1)).subscribe(subscriberCaptor.capture());
        final var subscriber = subscriberCaptor.getValue();
        // verify subscriber.onSubscribe(subscription) invoked, once
        final var subscriptionCaptor = ArgumentCaptor.forClass(Flow.Subscription.class);
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
        final var subscription = subscriptionCaptor.getValue();
        // verify subscription.request(Long.MAX_VALUE) invoked, once
        Mockito.verify(subscription, Mockito.times(1)).request(Long.MAX_VALUE);
        // verify, subscriber.onNext(item) invoked, at most HelloWorld.BYTES
        Mockito.verify(subscriber, Mockito.atMost(HelloWorld.BYTES))
                .onNext(ArgumentMatchers.notNull());
        if (AlienService.countRef.get() >= HelloWorld.BYTES) {
            // verify, subscriber.onComplete() invoked, once
            Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        } else {
            // verify, subscriber.onComplete() invoked, never
            Mockito.verify(subscriber, Mockito.never()).onComplete();
        }
        // verify, subscription.cancel() invoked, once
        Mockito.verify(subscription, Mockito.times(1)).cancel();
    }
}
