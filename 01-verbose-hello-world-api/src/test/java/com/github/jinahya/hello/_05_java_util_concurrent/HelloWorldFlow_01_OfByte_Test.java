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

import com.github.jinahya.hello.AwaitilityTestUtils;
import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello.HelloWorldFlow;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorldFlow.HelloWorldPublisher.OfByte} class and
 * {@link HelloWorldFlow.HelloWorldSubscriber.OfByte} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorldFlow.HelloWorldPublisher.OfByte")
@Slf4j
class HelloWorldFlow_01_OfByte_Test extends _HelloWorldFlowTest {

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var publisher = Mockito.spy(
                new HelloWorldFlow.HelloWorldPublisher.OfByte(
                        service,
                        Executors.newVirtualThreadPerTaskExecutor()
                )
        );
        final var subscriber = Mockito.spy(
                new HelloWorldFlow.HelloWorldSubscriber.OfByte() { // @formatter:off
                    @Override public void onSubscribe(final Flow.Subscription subscription) {
                        super.onSubscribe(subscription);
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            subscription().request(1L);
                        }
                    }
                    @Override public void onNext(final Byte item) {
                        super.onNext(item);
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            subscription().request(1L);
                        }
                    }
                } // @formatter:on
        );
        // intercept, <subscriber.onSubscribe(subscription)> to wrap the <subscription> as a spy
        BDDMockito.willAnswer(i -> {
            final var subscription = i.getArgument(0);
            i.getRawArguments()[0] = Mockito.spy(subscription);
            return i.callRealMethod();
        }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
        // ------------------------------------------------------------------------------------ when
        publisher.subscribe(subscriber);
        // ------------------------------------------------------------------------------------ then
        // verify, <subscriber.onSubscribe(subscription)> invoked, once
        final Flow.Subscription subscription;
        {
            final var captor = ArgumentCaptor.forClass(Flow.Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(captor.capture());
            subscription = captor.getValue();
        }
        // request some
        subscription.request(ThreadLocalRandom.current().nextLong(8L) + 1L);
        AwaitilityTestUtils.awaitForOneSecond();
        // cancel
        subscription.cancel();
        // request some, after the cancellation
        subscription.request(ThreadLocalRandom.current().nextLong(8L) + 1L);
    }

    @Test
    void _12_() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var publisher = new HelloWorldFlow.HelloWorldPublisher.OfByte(
                service,
                Executors.newVirtualThreadPerTaskExecutor()
        );
        final var subscriber = Mockito.spy(new HelloWorldFlow.HelloWorldSubscriber.OfByte());
        // ------------------------------------------------------------------------------------ when
        publisher.subscribe(subscriber);
        // ------------------------------------------------------------------------------------ then
        // verify, <subscriber.onSubscribe(subscription)> invoked, once
        final Flow.Subscription subscription;
        {
            final var captor = ArgumentCaptor.forClass(Flow.Subscription.class);
            Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(captor.capture());
            subscription = captor.getValue();
        }
        subscription.request(
                ThreadLocalRandom.current().nextInt(HelloWorld.BYTES, HelloWorld.BYTES << 1)
        );
        // await, for a sec
        AwaitilityTestUtils.awaitForOneSecond();
        // verify, <subscriber.onNext(...)> invoked, at most <12> times
        Mockito.verify(subscriber, Mockito.atMost(HelloWorld.BYTES))
                .onNext(ArgumentMatchers.notNull());
        // cancel
        subscription.cancel();
    }
}
