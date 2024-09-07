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
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;

@DisplayName("HelloWorldFlow.HelloWorldPublisher.OfBuffer")
@Slf4j
class HelloWorldFlow_03_HelloWorldPublisher_OfBuffer_Test extends _HelloWorldFlowTest {

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var publisher = Mockito.spy(
                new HelloWorldFlow.HelloWorldPublisher.OfBuffer(service, EXECUTOR)
        );
        final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES >> 1) + 1;
        final var subscriber = Mockito.spy(
                new HelloWorldFlow.HelloWorldSubscriber.OfBuffer() { // @formatter:off
                    @Override public void onSubscribe(final Flow.Subscription subscription) {
                        super.onSubscribe(subscription);
                        subscription.request(n);
                    }
                    @Override public void onNext(final ByteBuffer item) {
                        super.onNext(item);
                        if (++i == n) {
                            subscription().cancel();
                        }
                    }
                    private int i = 0;
                } // @formatter:on
        );
        // intercept, subscriber.onSubscribe(subscription), to wrap the subscription as a spy
        BDDMockito.willAnswer(i -> {
            i.getRawArguments()[0] = Mockito.spy(i.getArgument(0, Flow.Subscription.class));
            return i.callRealMethod();
        }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
        // ------------------------------------------------------------------------------- when/then
        publisher.subscribe(subscriber);
        // ------------------------------------------------------------------------------------ then
        // verify, subscriber.onSubscribe(subscription) invoked, once
        final var subscriptionCaptor = ArgumentCaptor.forClass(Flow.Subscription.class);
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
        final var subscription = subscriptionCaptor.getValue();
        // verify, subscription.request(n) invoked, once
        Mockito.verify(subscription, Mockito.times(1)).request(n);
        // verify, subscriber.onNext(item) invoked, n-times
        Awaitility.await().atMost(Duration.ofSeconds(8L)).untilAsserted(() -> {
            Mockito.verify(subscriber, Mockito.times(n)).onNext(ArgumentMatchers.notNull());
        });
        // verify, subscription.cancel() invoked, once
        Awaitility.await().atMost(Duration.ofSeconds(4L)).untilAsserted(() -> {
            Mockito.verify(subscription, Mockito.times(1)).cancel();
        });
    }
}
