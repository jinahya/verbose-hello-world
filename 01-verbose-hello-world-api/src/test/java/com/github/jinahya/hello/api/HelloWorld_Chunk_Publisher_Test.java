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

import com.github.jinahya.hello.miscellaneous._org_mockito.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._java_lang.__Java_Lang_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._java_util_concurrent._Java_Util_Concurrent_SubmissionPublisher_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._org_mockito._Org_Mockito__TestUtils.OfFlow.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Subscription-level tests for {@link HelloWorldChunkPublisher} against the
 * {@link Flow.Publisher Flow.Publisher} contract. {@code HelloWorldChunkPublisher} is functionally
 * equivalent to {@link HelloWorldArrayPublisher} from a subscriber's point of view — both emit an
 * open-ended stream of fresh {@code byte[HelloWorld.BYTES]} snapshots — but assembles each
 * {@code byte[]} by consuming one full subscription of an internal {@link HelloWorldBytePublisher}
 * (chunking 12 {@code Byte}s into one array) rather than calling
 * {@link HelloWorld#set(byte[]) service.set(...)} directly. The tests therefore mirror the
 * {@link HelloWorld_Array_Publisher_Test array-publisher tests} — same expectations on demand,
 * cancellation, multi-subscriber isolation, and error routing — verifying that the chunking layer
 * preserves the outer contract.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld / chunk Publisher")
@Slf4j
@SuppressWarnings({"rawtypes"})
class HelloWorld_Chunk_Publisher_Test extends HelloWorld__Publisher_Test<byte[]> {

    /**
     * Maximum time, in milliseconds, to wait for subscriber interactions.
     */
    private static final long TIMEOUT = TimeUnit.SECONDS.toMillis(10L);

    // ---------------------------------------------------------------------------------------------
    private static MockedConstruction<SubmissionPublisher> SUBMISSION_PUBLISHER_CONSTRUCTION;

    /**
     * A test-only subclass of {@link HelloWorldBytePublisher} that logs its
     * {@link #subscribe(Flow.Subscriber) subscribe} invocation and wraps the incoming subscriber
     * with {@link _Org_Mockito__TestUtils.OfFlow#loggingByteSubscriber loggingByteSubscriber(...)}
     * before delegating to {@code super.subscribe(...)} — so the upstream byte-publisher's full
     * signal exchange surfaces in logs without touching the SUT's own body.
     */
    private static final class LoggingByteFixture extends HelloWorldBytePublisher {

        LoggingByteFixture(final HelloWorld service) {
            super(service);
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super Byte> s) {
            log.debug("{}.subscribe({})", toSimplifedString(this), toSimplifedString(s));
            super.subscribe(loggingByteSubscriber(s));
        }
    }

    /**
     * A test-only subclass of {@link HelloWorldBytePublisher} that ignores the service and emits
     * only the first {@value #LENGTH} bytes of the {@code hello-world-bytes} before completing —
     * an upstream which completes short of {@value HelloWorld#BYTES} bytes.
     */
    private static final class ShortByteFixture extends HelloWorldBytePublisher {

        private static final int LENGTH = 5;

        ShortByteFixture(final HelloWorld service) {
            super(service);
        }

        @Override
        public void subscribe(final Flow.Subscriber<? super Byte> s) {
            final var publisher = new SubmissionPublisher<Byte>();
            publisher.subscribe(s);
            final var bytes = hello_world_byte_array();
            for (int i = 0; i < LENGTH; i++) {
                publisher.submit(bytes[i]);
            }
            publisher.close();
        }
    }

    /**
     * Builds a {@link HelloWorldChunkPublisher} whose upstream is a {@link LoggingByteFixture} —
     * yields full inner byte-stream logging without modifying the SUT.
     */
    private static HelloWorldChunkPublisher withLoggingUpstream(final HelloWorld service) {
        return new HelloWorldChunkPublisher(new LoggingByteFixture(service));
    }

    // ---------------------------------------------------------------------------------------------
    HelloWorld_Chunk_Publisher_Test() {
        super(s -> loggingPublisher(withLoggingUpstream(s)));
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
     * Verifies that the publisher emits exactly {@code 1} array when a single subscriber calls
     * {@code request(1)} and {@linkplain Flow.Subscription#cancel() cancels} after receiving it. No
     * {@code onComplete} fires because this publisher is open-ended (Rule 3.12).
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("exactly 1 / request(1) + cancel")
    @Test
    void __exactly1() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingArraySubscriber(new Flow.Subscriber<>() { // @formatter:off
            private Flow.Subscription subscription;
            @Override public void onSubscribe(final Flow.Subscription s) {
                subscription = s;
                s.request(1L);
            }
            @Override public void onNext(final byte[] item) { subscription.cancel(); }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        });  // @formatter:on
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
        assertArrayEquals(hello_world_byte_array(), elementCaptor.getValue());
    }

    /**
     * Verifies that the publisher emits exactly {@code 2} arrays when a single subscriber calls
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
     * receives its own {@code n} arrays independently.
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
     * {@code service.set} throws. The failure originates inside the inner
     * {@link HelloWorldBytePublisher}'s producer task and is routed through the chunking layer's
     * {@link CompletableFuture} unwrap so the original cause reaches the outer subscriber.
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
     * {@code onError} via the outer {@link SubmissionPublisher} and lets {@code subscribe} return
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

    /**
     * Verifies that the
     * {@link HelloWorldChunkPublisher#HelloWorldChunkPublisher(HelloWorldBytePublisher)
     * HelloWorldChunkPublisher(upstream)} constructor throws a {@link NullPointerException} when
     * the {@code upstream} argument is {@code null}.
     */
    @DisplayName("new(upstream) / NullPointerException / upstream is null")
    @Test
    void _ThrowNullPointerException_UpstreamIsNull() {
        assertThrows(NullPointerException.class,
                     () -> new HelloWorldChunkPublisher((HelloWorldBytePublisher) null));
    }

    /**
     * Verifies that the inner byte subscriptions run on the executor of the
     * {@link HelloWorldBytePublisher} given to the constructor.
     */
    @DisplayName("upstream executor / byte subscriptions run on it")
    @Test
    void __upstreamExecutor() {
        // ----------------------------------------------------------------------------------- given
        final var executor = mock(Executor.class,
                                  AdditionalAnswers.delegatesTo(ForkJoinPool.commonPool()));
        final var publisher = new HelloWorldChunkPublisher(
                new HelloWorldBytePublisher(service(), executor));
        final var subscriber = loggingArraySubscriber(new Flow.Subscriber<>() { // @formatter:off
            private Flow.Subscription subscription;
            @Override public void onSubscribe(final Flow.Subscription s) {
                subscription = s;
                s.request(1L);
            }
            @Override public void onNext(final byte[] item) { subscription.cancel(); }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher.subscribe(subscriber);
        // ------------------------------------------------------------------------------------ then
        final var elementCaptor = forClass(byte[].class);
        verify(subscriber, timeout(TIMEOUT).times(1)).onNext(elementCaptor.capture());
        assertArrayEquals(hello_world_byte_array(), elementCaptor.getValue());
        verify(executor, atLeastOnce()).execute(notNull());
        verify(subscriber, never()).onError(any());
    }

    /**
     * Verifies that the publisher signals {@code onError} with an {@link IllegalStateException},
     * and no {@code onNext}, when the upstream completes before emitting
     * {@value HelloWorld#BYTES} bytes.
     */
    @DisplayName("onError / upstream completes short")
    @Test
    void _OnError_UpstreamCompletesShort() {
        // ----------------------------------------------------------------------------------- given
        final var publisher = new HelloWorldChunkPublisher(new ShortByteFixture(service()));
        final var subscriber = loggingArraySubscriber(new Flow.Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }
            @Override public void onNext(final byte[] item) { }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher.subscribe(subscriber);
        // ------------------------------------------------------------------------------------ then
        final var errorCaptor = forClass(Throwable.class);
        verify(subscriber, timeout(TIMEOUT).times(1)).onError(errorCaptor.capture());
        verify(subscriber, never()).onNext(any());
        verify(subscriber, never()).onComplete();
        assertInstanceOf(IllegalStateException.class, errorCaptor.getValue());
    }
}
