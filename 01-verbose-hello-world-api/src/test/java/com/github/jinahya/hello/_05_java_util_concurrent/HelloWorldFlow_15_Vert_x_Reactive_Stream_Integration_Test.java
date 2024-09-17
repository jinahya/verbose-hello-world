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
import io.vertx.ext.reactivestreams.ReactiveReadStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.FlowAdapters;

import java.time.Duration;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

@Disabled("failing with github aciton")
@DisplayName("MultiCreate")
@Slf4j
class HelloWorldFlow_15_Vert_x_Reactive_Stream_Integration_Test extends _HelloWorldFlowTest {

    @Test
    void __ReactiveReadStream() {
        final var service = service();
        final var publisher = Mockito.spy(
                new HelloWorldFlow.HelloWorldPublisher.OfByte(service, EXECUTOR)
        );
        final var reference = new AtomicReference<Flow.Subscription>();
        // intercept, <publisher.subscribe(subscriber)> to wrap the <subscriber> as a spy
        BDDMockito.willAnswer(i -> {
            final var subscriber = Mockito.spy(i.getArgument(0, Flow.Subscriber.class));
            // intercept, <subscriber.onSubscribe(subscription)> to store the <subscription>
            BDDMockito.willAnswer(j -> {
                final var subscription = Mockito.spy(j.getArgument(0, Flow.Subscription.class));
                log.debug("onSubscribe({})", subscription);
                reference.set(subscription);
                return j.callRealMethod();
            }).given(subscriber).onSubscribe(ArgumentMatchers.notNull());
            // log, on <onNext(item)>
            BDDMockito.willAnswer(j -> {
                final var item = j.getArgument(0, Byte.class);
                log.debug("onNext({})", item);
                return j.callRealMethod();
            }).given(subscriber).onNext(ArgumentMatchers.notNull());
            // log, on <onComplete()>
            BDDMockito.willAnswer(j -> {
                log.debug("onComplete()");
                return j.callRealMethod();
            }).given(subscriber).onComplete();
            // log, on <onError(throwable)>
            BDDMockito.willAnswer(j -> {
                final var throwable = j.getArgument(0, Throwable.class);
                log.debug("onError({})", throwable);
                return j.callRealMethod();
            }).given(subscriber).onComplete();
            i.getArguments()[0] = subscriber;
            return i.callRealMethod();
        }).given(publisher).subscribe(ArgumentMatchers.notNull());
        // prepare a stream
        final org.reactivestreams.Subscriber<Byte> stream = ReactiveReadStream.readStream();
        // convert
        final var subscriber = FlowAdapters.toFlowSubscriber(stream);
        // subscribe
        publisher.subscribe(subscriber);
        // request
        final var subscription = reference.get();
        subscription.request(
                ThreadLocalRandom.current().nextLong(HelloWorld.BYTES << 1) + 1L
        );
        AwaitilityTestUtils.awaitFor(Duration.ofSeconds(8L));
    }
}
