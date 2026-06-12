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

import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Java_Util_Concurrent_SubmissionPublisher_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Org_Mockito__TestUtils.OfFlow.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Subscription-level tests for {@link HelloWorldArrayPublisher} against the
 * {@link Flow.Publisher Flow.Publisher} contract.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("array publisher")
@Slf4j
@SuppressWarnings({"rawtypes"})
class HelloWorld_Array_Publisher_Test extends HelloWorld__Publisher_Test<byte[]> {

    /**
     * Maximum time, in milliseconds, to wait for subscriber interactions.
     */
    private static final long TIMEOUT = TimeUnit.SECONDS.toMillis(10L);

    // ---------------------------------------------------------------------------------------------
    private static MockedConstruction<SubmissionPublisher> SUBMISSION_PUBLISHER_CONSTRUCTION;

    // ---------------------------------------------------------------------------------------------
    HelloWorld_Array_Publisher_Test() {
        super(s -> loggingPublisher(new HelloWorldArrayPublisher(s)));
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeAll
    static void __stubSubmissionPublisherMockConstruct() {
        SUBMISSION_PUBLISHER_CONSTRUCTION = loggingMockConstruction();
    }

    @AfterAll
    static void __closeSubmissionPublisherMockConstruct() {
        SUBMISSION_PUBLISHER_CONSTRUCTION.close();
    }

    /**
     * Verifies that the publisher emits at least {@code n} elements when a single subscriber calls
     * {@code request(n)} with {@code n > 0}.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName(
            "should emit at least <n> elements when the subscriber calls <request(n)> with <n > 0>")
    @Test
    void __singleRandom() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 10);
        log.debug("n: {}", n);
        final var subscriber = loggingArraySubscriber(new Flow.Subscriber<>() {
            private Flow.Subscription subscription;
            private int received;
            @Override public void onSubscribe(final Flow.Subscription s) {
                subscription = s;
                s.request(n);
            }
            @Override public void onNext(final byte[] item) {
                if (++received == n) subscription.cancel();
            }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            p.subscribe(subscriber);
            verify(subscriber, timeout(TIMEOUT).times(n)).onNext(any());
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

    /**
     * Verifies that, with multiple subscribers each calling {@code request(n)}, every subscriber
     * receives its own {@code n} elements independently.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("""
            should give each subscriber its own <n> elements
            when multiple subscribers each call <request(n)>""")
    @Test
    void __multiRandom() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var count = ThreadLocalRandom.current().nextInt(2, 5);
        final var demands = new int[count];
        final var subscribers = new ArrayList<Flow.Subscriber<byte[]>>(count);
        for (int i = 0; i < count; i++) {
            final var n = ThreadLocalRandom.current().nextInt(1, 10);
            demands[i] = n;
            subscribers.add(loggingArraySubscriber(new Flow.Subscriber<>() {
                private Flow.Subscription subscription;
                private int received;
                @Override public void onSubscribe(final Flow.Subscription s) {
                    subscription = s;
                    s.request(n);
                }
                @Override public void onNext(final byte[] item) {
                    if (++received == n) subscription.cancel();
                }
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
            for (int i = 0; i < count; i++) {
                verify(subscribers.get(i), timeout(TIMEOUT).times(demands[i]))
                        .onNext(any());
            }
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

    /**
     * Verifies that the publisher signals {@code onError} (and no further signals) when
     * {@code service.set} throws.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("should signal <onError> with no further signals when <service.set> throws")
    @Test
    void __serviceThrows() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var error = new RuntimeException("simulated set(byte[]) failure");
        doThrow(error).when(service()).set(any(byte[].class));
        final var subscriber = loggingArraySubscriber(new Flow.Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }
            @Override public void onNext(final byte[] item) { }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        applyPublisher(publisher -> {
            publisher.subscribe(subscriber);
            verify(subscriber, timeout(TIMEOUT).times(1)).onError(notNull());
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        final var errorCaptor = forClass(Throwable.class);
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, never()).onNext(any());
        verify(subscriber, times(1)).onError(errorCaptor.capture());
        verify(subscriber, never()).onComplete();
        assertSame(error, errorCaptor.getValue());
    }
}
