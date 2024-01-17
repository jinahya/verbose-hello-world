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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;

@DisplayName("HelloWorldFlow.HelloWorldPublisher.OfByte")
@Slf4j
class HelloWorldFlow_01_HelloWorldPublisher_OfByte_Test extends _HelloWorldFlowTest {

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var publisher = Mockito.spy(
                new HelloWorldFlow.HelloWorldPublisher.OfByte(service, EXECUTOR)
        );
        final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES) + HelloWorld.BYTES;
        final var subscriber = Mockito.spy(new HelloWorldFlow.HelloWorldSubscriber.OfByte() {
            @Override
            public void onSubscribe(final Flow.Subscription subscription) {
                super.onSubscribe(subscription);
                subscription.request(n);
            }
        });
        // intercept, subscriber.onSubscribe(subscription), to wrap the subscription as a spy
        BDDMockito.willAnswer(i -> {
            i.getRawArguments()[0] = Mockito.spy(i.getArgument(0, Flow.Subscription.class));
            return i.callRealMethod();
        }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
        // ------------------------------------------------------------------------------------ when
        publisher.subscribe(subscriber);
        // ------------------------------------------------------------------------------------ then
        // verify, subscriber.onSubscribe(subscription) invoked, once
        final var subscriptionCaptor = ArgumentCaptor.forClass(Flow.Subscription.class);
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
        final var subscription = subscriptionCaptor.getValue();
        // verify, subscription.request(n) invoked, once
        Mockito.verify(subscription, Mockito.times(1)).request(n);
        // await, for a second
        _HelloWorldTestUtils.awaitForOneSecond();
        // verify, subscriber.onNext(item) invoked, at most HelloWorld.BYTES times
        Mockito.verify(subscriber, Mockito.atMost(HelloWorld.BYTES))
                .onNext(ArgumentMatchers.notNull());
        // verify, subscriber.onComplete() invoked, once
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }
}
