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
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class _HelloWorld_Flow_04_StringPublisher_Test extends __HelloWorld_Flow_Test {

    // ---------------------------------------------------------------------------------------------
    static class StringPublisher implements Flow.Publisher<String> { // @formatter:off
        private StringPublisher(final HelloWorld service) {
            super();
            this.service = Objects.requireNonNull(service, "service is null");
        }
        @Override public String toString() {
            return String.format("[buffer-publisher@%8x]", hashCode());
        }
        @Override
        public void subscribe(final Flow.Subscriber<? super String> subscriber) {
            log.debug("{}.subscribe({})", this, subscriber);
            Objects.requireNonNull(subscriber, "subscriber is null");
            final var processor = new Flow.Processor<ByteBuffer, String>() {
                @Override public String toString() {
                    return String.format("[array-to-buffer-processor@%08x]", hashCode());
                }
                @Override
                public void subscribe(final Flow.Subscriber<? super String> subscriber) {
                    log.debug("{}.subscribe({})", this, subscriber);
                    subscriber.onSubscribe(subscription);
                }
                @Override
                public void onSubscribe(final Flow.Subscription subscription) {
                    log.debug("{}.onSubscribe({})", this, subscription);
                    this.subscription = subscription;
                }
                @Override
                public void onNext(final ByteBuffer item) {
                    log.debug("{}.onNext({})", this, item);
                    subscriber.onNext(StandardCharsets.US_ASCII.decode(item).toString());
                }
                @Override
                public void onError(final Throwable throwable) {
                    log.error("{}.onError({}", this, throwable, throwable);
                    subscriber.onError(throwable);
                }
                @Override
                public void onComplete() {
                    log.debug("{}.onComplete()", this);
                    subscriber.onComplete();
                }
                private Flow.Subscription subscription;
            };
            new _HelloWorld_Flow_03_BufferPublisher_Test.BufferPublisher(service).subscribe(processor);
            processor.subscribe(subscriber);
        }
        final HelloWorld service;
    } // @formatter:on

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var publisher = Mockito.spy(new StringPublisher(service));
        final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES >> 1) + 1;
        final var subscriber = Mockito.spy(new Flow.Subscriber<String>() { // @formatter:off
            @Override public String toString() {
                return String.format("[string-subscriber@%08x]", hashCode());
            }
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                log.debug("{}.onSubscribe({})", this, subscription);
                log.debug("  - requesting {} item(s)...", n);
                subscription.request(n);
            }
            @Override public void onNext(final String item) {
                log.debug("{}.onNext({})", this, item);
            }
            @Override public void onError(final Throwable throwable) {
                log.error("{}.onError({})", this, throwable, throwable);
            }
            @Override public void onComplete() {
                log.debug("{}.onComplete()", this);
            }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ then
        publisher.subscribe(subscriber);
        // ------------------------------------------------------------------------------------ then
        final var subscriptionCaptor = ArgumentCaptor.forClass(Flow.Subscription.class);
        Mockito.verify(subscriber, Mockito.times(1)).onSubscribe(subscriptionCaptor.capture());
        final var subscription = subscriptionCaptor.getValue();
        Mockito.verify(subscription, Mockito.times(1)).request(n);
        {
            final var duration = Duration.ofSeconds(1L);
            log.debug("awaiting for {}", duration);
            Awaitility.await()
                    .timeout(duration.plusMillis(1L))
                    .pollDelay(duration)
                    .untilAsserted(() -> Assertions.assertTrue(true));
        }
        Mockito.verify(subscriber, Mockito.atMost(n)).onNext(ArgumentMatchers.notNull());
        Mockito.verify(subscriber, Mockito.atMost(1)).onComplete();
        log.debug("cancelling the subscription...");
        subscription.cancel();
        {
            final var m = ThreadLocalRandom.current().nextInt(4) + 1;
            log.debug("requesting {} item(s) after the cancellation...", m);
            subscription.request(m);
        }
    }
}
