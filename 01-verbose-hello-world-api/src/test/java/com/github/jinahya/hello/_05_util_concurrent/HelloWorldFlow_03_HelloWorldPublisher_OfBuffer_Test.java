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

import com.github.jinahya.hello.HelloWorldFlow;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;

@DisplayName("HelloWorldFlow.HelloWorldPublisher.OfBuffer")
@Slf4j
class HelloWorldFlow_03_HelloWorldPublisher_OfBuffer_Test extends _HelloWorldFlowTest {

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var publisher = new HelloWorldFlow.HelloWorldPublisher.OfBuffer(service, EXECUTOR);
        final var subscriber = new HelloWorldFlow.HelloWorldSubscriber.OfBuffer() { // @formatter:off
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
            private int n = ThreadLocalRandom.current().nextInt(1, 4);
            private int i = 0;
        }; // @formatter:on
        // ------------------------------------------------------------------------------- when/then
        publisher.subscribe(subscriber);
    }
}
