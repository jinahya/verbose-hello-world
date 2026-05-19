package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.Flow;
import java.util.concurrent.ThreadLocalRandom;

import static com.github.jinahya.hello.api.HelloWorldBookTestUtils.loggingSpy;
import static com.github.jinahya.hello.api.HelloWorldTestUtils.hello_world_byte_array;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Subscription-level tests for {@link HelloWorldArrayPublisher} against the
 * {@link Flow.Publisher Flow.Publisher} contract.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Array_Publisher_Test extends HelloWorld__Publisher_Test<byte[]> {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // ---------------------------------------------------------------------------------------------
    HelloWorld_Array_Publisher_Test() {
        super(HelloWorldArrayPublisher::new);
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("request(n), n > 0 → at least n elements")
    void __singleRandom() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 10);
        log.debug("n: {}", n);
        final var subscriber = loggingSpy(new Flow.Subscriber<byte[]>() {
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                subscription.request(n);
            }
            @Override public void onNext(final byte[] item) { }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            p.subscribe(subscriber);
            await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(n)).onNext(any()));
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(byte[].class);
        inOrder.verify(subscriber, times(n)).onNext(elementCaptor.capture());
        final var expected = hello_world_byte_array();
        final var elements = elementCaptor.getAllValues();
        assertEquals(n, elements.size());
        for (final var element : elements) {
            assertArrayEquals(expected, element);
        } // @formatter:on
    }

    @Test
    @DisplayName("multiple subscribers, each request(n) → each gets its own n elements")
    void __multiRandom() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var count = ThreadLocalRandom.current().nextInt(2, 5);
        final var demands = new int[count];
        final var subscribers = new ArrayList<Flow.Subscriber<byte[]>>(count);
        for (int i = 0; i < count; i++) {
            final var n = ThreadLocalRandom.current().nextInt(1, 10);
            demands[i] = n;
            subscribers.add(loggingSpy(new Flow.Subscriber<byte[]>() {
                @Override public void onSubscribe(final Flow.Subscription subscription) {
                    subscription.request(n);
                }
                @Override public void onNext(final byte[] item) { }
                @Override public void onError(final Throwable throwable) { }
                @Override public void onComplete() { }
            }));
        }
        log.debug("demands: {}", demands);
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            for (final var subscriber : subscribers) {
                p.subscribe(subscriber);
            }
            await().atMost(TIMEOUT).untilAsserted(() -> {
                for (int i = 0; i < count; i++) {
                    verify(subscribers.get(i), times(demands[i])).onNext(any());
                }
            });
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        final var expected = hello_world_byte_array();
        for (int i = 0; i < count; i++) {
            final var subscriber = subscribers.get(i);
            final var inOrder = inOrder(subscriber);
            inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
            final var elementCaptor = forClass(byte[].class);
            inOrder.verify(subscriber, times(demands[i])).onNext(elementCaptor.capture());
            final var elements = elementCaptor.getAllValues();
            assertEquals(demands[i], elements.size());
            for (final var element : elements) {
                assertArrayEquals(expected, element);
            }
        } // @formatter:on
    }

    @Test
    @DisplayName("service.set throws → subscriber gets onError, no further signals")
    void __serviceThrows() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var error = new RuntimeException("simulated set(byte[]) failure");
        Mockito.doThrow(error).when(service()).set(ArgumentMatchers.any(byte[].class));
        final var subscriber = loggingSpy(new Flow.Subscriber<byte[]>() {
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }
            @Override public void onNext(final byte[] item) { }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(publisher -> {
            publisher.subscribe(subscriber);
            await().atMost(TIMEOUT)
                    .untilAsserted(() -> verify(subscriber, times(1)).onError(notNull()));
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        final var errorCaptor = forClass(Throwable.class);
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, never()).onNext(any());
        verify(subscriber, times(1)).onError(errorCaptor.capture());
        verify(subscriber, never()).onComplete();
        assertSame(error, errorCaptor.getValue()); // @formatter:on
    }
}
