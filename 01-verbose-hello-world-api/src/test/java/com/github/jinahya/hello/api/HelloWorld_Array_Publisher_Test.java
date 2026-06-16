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
@DisplayName("HelloWorld / array Publisher")
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
        SUBMISSION_PUBLISHER_CONSTRUCTION = loggingSubmissionPublisherConstruction();
    }

    @AfterAll
    static void __closeSubmissionPublisherMockConstruct() {
        SUBMISSION_PUBLISHER_CONSTRUCTION.close();
    }

    /**
     * Verifies that the publisher emits exactly {@code 1} element when a single subscriber calls
     * {@code request(1)} and {@linkplain Flow.Subscription#cancel() cancels} after receiving it. No
     * {@code onComplete} fires because this publisher is open-ended (Rule 3.12).
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("exactly 1 / request(1) + cancel")
    @Test
    void __exactly1() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingArraySubscriber(new Flow.Subscriber<>() {
            private Flow.Subscription subscription;
            @Override public void onSubscribe(final Flow.Subscription s) {
                subscription = s;
                s.request(1L);
            }
            @Override public void onNext(final byte[] item) { subscription.cancel(); }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            p.subscribe(subscriber);
            verify(subscriber, timeout(TIMEOUT).times(1)).onNext(any());
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(byte[].class);
        inOrder.verify(subscriber, times(1)).onNext(elementCaptor.capture());
        verify(subscriber, after(500L).never()).onComplete();
        verify(subscriber, never()).onError(any());
        assertArrayEquals(hello_world_byte_array(), elementCaptor.getValue()); // @formatter:on
    }

    /**
     * Verifies that the publisher emits exactly {@code 2} elements when a single subscriber calls
     * {@code request(2)} and {@linkplain Flow.Subscription#cancel() cancels} after receiving them.
     * No {@code onComplete} fires because this publisher is open-ended (Rule 3.12).
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("exactly 2 / request(2) + cancel")
    @Test
    void __exactly2() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = 2;
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
        verify(subscriber, after(500L).never()).onComplete();
        verify(subscriber, never()).onError(any());
        final var expected = hello_world_byte_array();
        final var elements = elementCaptor.getAllValues();
        assertEquals(n, elements.size());
        for (final var element : elements) {
            assertArrayEquals(expected, element);
        } // @formatter:on
    }

    /**
     * Verifies that the publisher delivers no signals beyond {@code onSubscribe} when the
     * subscriber {@linkplain Flow.Subscription#cancel() cancels} from inside {@code onSubscribe}.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("no further signals / cancel in onSubscribe")
    @Test
    void __cancelInOnSubscribe() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingArraySubscriber(new Flow.Subscriber<>() {
            @Override public void onSubscribe(final Flow.Subscription s) { s.cancel(); }
            @Override public void onNext(final byte[] item) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            p.subscribe(subscriber);
            verify(subscriber, timeout(TIMEOUT).times(1)).onSubscribe(notNull());
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, after(500L).never()).onNext(any());
        verify(subscriber, never()).onError(any());
        verify(subscriber, never()).onComplete(); // @formatter:on
    }

    /**
     * Verifies that, with multiple subscribers each calling {@code request(n)}, every subscriber
     * receives its own {@code n} elements independently.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("multiple subscribers / independent demand")
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
    @DisplayName("onError / service.set throws")
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
        applyPublisher(p -> {
            p.subscribe(subscriber);
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

    /**
     * Verifies that, when the subscriber's {@code onSubscribe} throws, the publisher delivers
     * {@code onError} via the inner {@link SubmissionPublisher} and lets {@code subscribe} return
     * normally.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("onError via SubmissionPublisher / onSubscribe throws")
    @Test
    void __onSubscribeThrows() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var error = new RuntimeException("simulated onSubscribe failure");
        final var subscriber = loggingArraySubscriber(new Flow.Subscriber<>() {
            @Override public void onSubscribe(final Flow.Subscription s) { throw error; }
            @Override public void onNext(final byte[] item) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            p.subscribe(subscriber);                  // returns normally
            verify(subscriber, timeout(TIMEOUT).times(1)).onError(notNull());
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, never()).onNext(any());
        verify(subscriber, never()).onComplete(); // @formatter:on
    }
}
