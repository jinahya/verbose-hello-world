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
 * Subscription-level tests for {@link HelloWorldBytePublisher} against the
 * {@link Flow.Publisher Flow.Publisher} contract.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld / byte Publisher")
@Slf4j
@SuppressWarnings({"rawtypes"})
class HelloWorld_Byte_Publisher_Test extends HelloWorld__Publisher_Test<Byte> {

    /**
     * Maximum time, in milliseconds, to wait for subscriber interactions.
     */
    private static final long TIMEOUT = TimeUnit.SECONDS.toMillis(10L);

    // ---------------------------------------------------------------------------------------------
    private static MockedConstruction<SubmissionPublisher> SUBMISSION_PUBLISHER_CONSTRUCTION;

    // ---------------------------------------------------------------------------------------------
    HelloWorld_Byte_Publisher_Test() {
        super(s -> loggingPublisher(new HelloWorldBytePublisher(s)));
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

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the publisher emits exactly {@value HelloWorld#BYTES} elements followed by
     * {@code onComplete} when a single subscriber requests unbounded demand.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("exactly 12 + onComplete / request(12)")
    @Test
    void __exactly12() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingByteSubscriber(new Flow.Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }
            @Override public void onNext(final Byte item) { }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            p.subscribe(subscriber);
            verify(subscriber, timeout(TIMEOUT).times(1)).onComplete();
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(Byte.class);
        inOrder.verify(subscriber, times(HelloWorld.BYTES)).onNext(elementCaptor.capture());
        inOrder.verify(subscriber, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
        final var expected = hello_world_byte_array();
        final var elements = elementCaptor.getAllValues();
        assertEquals(expected.length, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            assertEquals(expected[i], elements.get(i));
        }
    }

    /**
     * Verifies that, given multiple subscribers each requesting {@code n} in {@code [1, 24)}, every
     * subscriber receives {@code min(n, 12)} elements and an {@code onComplete} when
     * {@code n >= 12}.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("multiple subscribers / min(n, 12) + onComplete when n >= 12")
    @Test
    void __multiRandom1To24() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var count = ThreadLocalRandom.current().nextInt(2, 5);
        final var demands = new int[count];
        final var subscribers = new ArrayList<Flow.Subscriber<Byte>>(count);
        for (int i = 0; i < count; i++) {
            final var n = ThreadLocalRandom.current().nextInt(1, HelloWorld.BYTES << 1); // [1, 24)
            demands[i] = n;
            subscribers.add(loggingByteSubscriber(new Flow.Subscriber<>() {
                @Override public void onSubscribe(final Flow.Subscription subscription) {
                    subscription.request(n);
                }
                @Override public void onNext(final Byte item) { }
                @Override public void onError(final Throwable throwable) { }
                @Override public void onComplete() { }
            }));
        }
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            for (final var subscriber : subscribers) {
                p.subscribe(subscriber);
            }
            for (int i = 0; i < count; i++) {
                final var expectedNext = Math.min(demands[i], HelloWorld.BYTES);
                final var expectedComplete = demands[i] >= HelloWorld.BYTES ? 1 : 0;
                verify(subscribers.get(i), timeout(TIMEOUT).times(expectedNext))
                        .onNext(any());
                if (expectedComplete > 0) {
                    verify(subscribers.get(i), timeout(TIMEOUT).times(expectedComplete))
                            .onComplete();
                }
            }
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        final var expected = hello_world_byte_array();
        for (int i = 0; i < count; i++) {
            final var subscriber = subscribers.get(i);
            final var expectedNext = Math.min(demands[i], HelloWorld.BYTES);
            final var expectedComplete = demands[i] >= HelloWorld.BYTES ? 1 : 0;
            verify(subscriber, times(1)).onSubscribe(notNull());
            verify(subscriber, never()).onError(any());
            verify(subscriber, times(expectedComplete)).onComplete();
            final var elementCaptor = forClass(Byte.class);
            verify(subscriber, times(expectedNext)).onNext(elementCaptor.capture());
            final var elements = elementCaptor.getAllValues();
            assertEquals(expectedNext, elements.size());
            for (int j = 0; j < elements.size(); j++) {
                assertEquals(expected[j], elements.get(j));
            }
        } // @formatter:on
    }

    /**
     * Verifies that the publisher stops emission after the subscriber {@linkplain
     * Flow.Subscription#cancel() cancels} mid-stream — at most {@value HelloWorld#BYTES} elements
     * arrive, with neither {@code onComplete} nor {@code onError}.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("stop emitting / cancel mid-stream")
    @Test
    void __cancelMidStream() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var cancelAt = 6;
        final var subscriber = loggingByteSubscriber(new Flow.Subscriber<>() {
            private Flow.Subscription subscription;
            private int received;
            @Override public void onSubscribe(final Flow.Subscription s) {
                subscription = s;
                s.request(Long.MAX_VALUE);
            }
            @Override public void onNext(final Byte item) {
                if (++received == cancelAt) subscription.cancel();
            }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            p.subscribe(subscriber);
            verify(subscriber, timeout(TIMEOUT).atLeast(cancelAt)).onNext(any());
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, after(500L).never()).onComplete();
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, never()).onError(any());
        verify(subscriber, atMost(HelloWorld.BYTES)).onNext(any()); // @formatter:on
    }

    /**
     * Verifies that the publisher signals {@code onError} (with no {@code onNext} and no
     * {@code onComplete}) when {@code service.set} throws.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("onError / service.set throws")
    @Test
    void __serviceThrows() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var error = new RuntimeException("simulated set(byte[]) failure");
        doThrow(error).when(service()).set(any(byte[].class));
        final var subscriber = loggingByteSubscriber(new Flow.Subscriber<>() {
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }
            @Override public void onNext(final Byte item) { }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            p.subscribe(subscriber);
            verify(subscriber, timeout(TIMEOUT).times(1)).onError(any());
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

    /**
     * Verifies that, when {@code subscriber.onSubscribe} throws, the publisher delivers
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
        final var subscriber = loggingByteSubscriber(new Flow.Subscriber<>() {
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                throw error;
            }
            @Override public void onNext(final Byte item) { }
            @Override public void onError(final Throwable throwable) { }
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
