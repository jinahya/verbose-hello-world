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
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
class HelloWorld_01_Flow_BytePublisher_Test extends _HelloWorldTest {

    // ---------------------------------------------------------------------------------------------
    static class BytePublisher implements Flow.Publisher<Byte> { // @formatter:off
        BytePublisher(final HelloWorld service) {
            super();
            this.service = Objects.requireNonNull(service, "service is null");
        }
        @Override public String toString() {
            return String.format("[byte-publisher@%08x]", hashCode());
        }
        @Override public void subscribe(final Flow.Subscriber<? super Byte> subscriber) {
            log.debug("{}.subscribe({})", this, subscriber);
            Objects.requireNonNull(subscriber, "subscriber is null");
            final var buffer = service.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip().limit(0);
            final var latch = new CountDownLatch(1);
            final var thread = Thread.ofVirtual().name("byte-publisher").start(() -> {
                try {
                    latch.await();
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                while (!Thread.currentThread().isInterrupted()) {
                    synchronized (buffer) {
                        while (!buffer.hasRemaining()) {
                            try {
                                buffer.wait();
                            } catch (final InterruptedException ie) {
                                Thread.currentThread().interrupt();
                                return;
                            }
                        }
                        subscriber.onNext(buffer.get());
                        if (buffer.position() == buffer.capacity()) {
                            subscriber.onComplete();
                        }
                    }
                }
            });
            subscriber.onSubscribe(Mockito.spy(new Flow.Subscription() {
                @Override public String toString() {
                    return String.format("[byte-subscription-for-%s@%08x]", subscriber, hashCode());
                }
                @Override public void request(final long n) {
                    log.debug("{}.request({})", this, n);
                    if (n <= 0L) {
                        cancel();
                        subscriber.onError( new IllegalArgumentException("n(" + n + ") < 0L"));
                        return;
                    }
                    if (!thread.isAlive()) {
                        return;
                    }
                    synchronized (buffer) {
                        buffer.limit(
                                buffer.limit() +
                                (int) Math.min((long) buffer.capacity() - buffer.limit(), n)
                        );
                        buffer.notifyAll();
                    }
                }
                @Override public void cancel() {
                    log.debug("{}.cancel()", this);
                    thread.interrupt();
                }
            }));
            latch.countDown();
        }
        final HelloWorld service;
    } // @formatter:on

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> {
            final var buffer = i.getArgument(0, ByteBuffer.class);
            if (buffer != null) {
                for (int j = 0; j < HelloWorld.BYTES && buffer.hasRemaining(); j++) {
                    buffer.put((byte) j);
                }
            }
            return buffer;
        }).given(service).put(ArgumentMatchers.any(ByteBuffer.class));
        final var publisher = Mockito.spy(new BytePublisher(service));
        final var n = ThreadLocalRandom.current().nextInt(HelloWorld.BYTES << 1) + 1;
        final var subscriber = Mockito.spy(new Flow.Subscriber<Byte>() { // @formatter:off
            @Override public String toString() {
                return String.format("[byte-subscriber@%08x]", hashCode());
            }
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                log.debug("{}.onSubscribe({})", this, subscription);
                log.debug("requesting {} item(s)...", n);
                subscription.request(n);
            }
            @Override public void onNext(final Byte item) {
                log.debug("{}.onNext({})", this, String.format("%1$02x", item));
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
        {
            final var r = HelloWorld.BYTES - n;
            if (r > 0) {
                log.debug("requesting {} more item(s)", r);
                subscription.request(r);
                {
                    final var duration = Duration.ofSeconds(1L);
                    log.debug("awaiting for {}", duration);
                    Awaitility.await()
                            .timeout(duration.plusMillis(1L))
                            .pollDelay(duration)
                            .untilAsserted(() -> Assertions.assertTrue(true));
                }
                Mockito.verify(subscriber, Mockito.times(1)).onComplete();
            }
        }
        log.debug("cancelling the subscription...");
        subscription.cancel();
        {
            final var m = ThreadLocalRandom.current().nextInt(4) + 1;
            log.debug("requesting {} item(s) after the cancellation...", m);
            subscription.request(m);
        }
    }
}
