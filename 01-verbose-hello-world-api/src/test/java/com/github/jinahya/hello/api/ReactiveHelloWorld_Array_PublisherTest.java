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
import org.reactivestreams.*;

import java.time.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static com.github.jinahya.hello.api.ReactiveHelloWorld__PublisherTestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Java_Lang_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Org_Mockito__TestUtils.OfReactiveStream.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Subscription-level tests for {@link ReactiveHelloWorldArrayPublisher} — verifies the Reactive
 * Streams 1.0 contract that this didactic publisher actively upholds (demand, cancellation, the
 * single-terminal-signal rule (1.7), reentrant-{@code request} rules (3.2 / 3.3), multi-subscription
 * (1.11), …) using a {@link Mockito#spy(Object) spied} {@link Subscriber}.
 * <p>
 * The publisher trusts well-behaved externals and does <em>not</em> defend against
 * {@code request(n ≤ 0)} (Rule 3.9), subscriber-thrown signals (Rule 1.4 surface), or duplicate
 * subscription (Rule 1.10) — those tests are intentionally absent.
 * <p>
 * Unlike {@link ReactiveHelloWorld_Byte_PublisherTest}, this publisher is <em>open-ended</em> — it
 * does not naturally complete; downstream {@code cancel} stops emission without a terminal signal
 * (Rule 3.12). Consequently this test class has no "<em>emits onComplete</em>" cases.
 * <p>
 * The constructor passes a factory that composes
 * {@link ReactiveHelloWorldArrayPublisher new ReactiveHelloWorldArrayPublisher(new
 * ReactiveHelloWorldBytePublisher(service))} to {@link ReactiveHelloWorld__PublisherTest super},
 * which builds the mock {@link HelloWorld} service and the publisher. The mock is stubbed by the
 * inherited {@code @BeforeEach} hook in the base class — see
 * {@link ReactiveHelloWorld__PublisherTest#stubService()}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorld__PublisherTest
 * @see ReactiveHelloWorldArrayPublisher
 */
@DisplayName("array publisher")
@Slf4j
class ReactiveHelloWorld_Array_PublisherTest
        extends ReactiveHelloWorld__PublisherTest<byte[]> {

    /**
     * Maximum time to wait, in milliseconds, for an expected asynchronous subscriber interaction.
     */
    private static final long TIMEOUT = TimeUnit.SECONDS.toMillis(10L);

    /**
     * Quiescence window, in milliseconds, during which no further subscriber interaction is
     * expected to arrive.
     */
    private static final long QUIESCE = 500L;

    /**
     * A test-only subclass of {@link ReactiveHelloWorldBytePublisher} that logs its
     * {@link #subscribe(Subscriber) subscribe} invocation and wraps the incoming subscriber with
     * {@link
     * com.github.jinahya.hello.miscellaneous._Org_Mockito__TestUtils.OfReactiveStream#loggingByteSubscriber
     * loggingByteSubscriber(...)} before delegating to {@code super.subscribe(...)} — so the
     * upstream byte-publisher's full signal exchange surfaces in logs without touching the SUT's
     * own body.
     */
    private static final class LoggingByteFixture extends ReactiveHelloWorldBytePublisher {

        LoggingByteFixture(final HelloWorld service) {
            super(service);
        }

        @Override
        public void subscribe(final Subscriber<? super Byte> s) {
            log.debug("{}.subscribe({})", toSimplifedString(this), toSimplifedString(s));
            super.subscribe(loggingByteSubscriber(s));
        }
    }

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorld_Array_PublisherTest() {
        super(s -> loggingPublisher(
                new ReactiveHelloWorldArrayPublisher(new LoggingByteFixture(s))));
    }

    // -------------------------------------------------------------------------------------- demand

    /**
     * Verifies that the publisher emits exactly one element and no {@code onComplete} signal when
     * the subscriber calls {@code request(1)}.
     */
    @DisplayName(
            "should emit <1> element with no <onComplete> when the subscriber calls <request(1)>")
    @Test
    void __exactly1() {
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingArraySubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) { s.request(1L); }
            @Override public void onNext(final byte[] t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        verify(subscriber, timeout(TIMEOUT).times(1)).onNext(any());
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(byte[].class);
        inOrder.verify(subscriber, times(1)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        assertArrayEquals(hello_world_byte_array(), elementCaptor.getValue()); // @formatter:on
    }

    /**
     * Verifies that the publisher emits exactly {@code n} elements and no {@code onComplete} signal
     * when the subscriber calls {@code request(n)} with {@code n > 0}.
     */
    @DisplayName("""
            should emit <n> elements with no <onComplete>
            when the subscriber calls <request(n)> with <n > 0>""")
    @Test
    void __random() {
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, 8);
        final var subscriber = loggingArraySubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final byte[] t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        verify(subscriber, timeout(TIMEOUT).times(n)).onNext(any());
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(byte[].class);
        inOrder.verify(subscriber, times(n)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        final var elements = elementCaptor.getAllValues();
        assertEquals(n, elements.size());
        for (final var element : elements) {
            assertArrayEquals(hello_world_byte_array(), element);
        }
    }

    /**
     * Verifies that demand accumulates across multiple {@code request(n)} calls — two synchronous
     * {@code request(2)} and {@code request(3)} from {@code onSubscribe} together produce five
     * elements with no {@code onComplete}.
     */
    @DisplayName("""
            should accumulate demand across multiple <request(n)> calls
            (request(2) + request(3) → 5 elements, no <onComplete>)""")
    @Test
    void __cumulativeDemand() {
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingArraySubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) {
                s.request(2L);
                s.request(3L);
            }
            @Override public void onNext(final byte[] t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        verify(subscriber, timeout(TIMEOUT).times(5)).onNext(any());
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        inOrder.verify(subscriber, times(5)).onNext(any());
        inOrder.verifyNoMoreInteractions();
        verify(subscriber, never()).onError(any());
        verify(subscriber, never()).onComplete();
    }

    /**
     * Verifies that reentrant {@code request(1)} calls issued from inside {@code onNext} are
     * accepted and accumulated without unbounded recursion — Rules 3.2 / 3.3. The subscriber pulls
     * one array at a time and stops after receiving five via {@code cancel}.
     */
    @DisplayName("""
            should accept reentrant <request(1)> calls from inside <onNext> (Rules 3.2 / 3.3)
            and emit until <cancel>""")
    @Test
    void __oneAtATime() {
        // ----------------------------------------------------------------------------------- given
        final var target = 5;
        final var subRef = new AtomicReference<Subscription>();
        final var received = new AtomicInteger();
        final var subscriber = loggingArraySubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) {
                subRef.set(s);
                s.request(1L);
            }
            @Override public void onNext(final byte[] t) {
                if (received.incrementAndGet() < target) {
                    subRef.get().request(1L);
                } else {
                    subRef.get().cancel();
                }
            }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        verify(subscriber, timeout(TIMEOUT).times(target)).onNext(any());
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, after(QUIESCE).times(target)).onNext(any());
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, never()).onError(any());
        verify(subscriber, never()).onComplete();
    }

    // ------------------------------------------------------------------------- cancellation

    /**
     * Verifies that {@code cancel} called from inside {@code onSubscribe} (Rule 3.7) suppresses
     * every subsequent signal — no {@code onNext}, {@code onError}, or {@code onComplete} fires.
     */
    @DisplayName("""
            should signal no <onNext>/<onError>/<onComplete>
            when <cancel> is called from inside <onSubscribe> (Rule 3.7)""")
    @Test
    void __cancelInOnSubscribe() {
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingArraySubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) { s.cancel(); }
            @Override public void onNext(final byte[] t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, after(QUIESCE).never()).onNext(any());
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, never()).onError(any());
        verify(subscriber, never()).onComplete();
    }

    /**
     * Verifies that {@code cancel} is idempotent (Rule 3.5) — repeated invocations from inside
     * {@code onSubscribe} cause no exceptions and yield no subscriber signals beyond
     * {@code onSubscribe}.
     */
    @DisplayName("should treat repeated <cancel> calls idempotently (Rule 3.5)")
    @Test
    void __cancelIdempotent() {
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingArraySubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) {
                s.cancel();
                s.cancel();
                s.cancel();
            }
            @Override public void onNext(final byte[] t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });  // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, after(QUIESCE).never()).onNext(any());
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, never()).onError(any());
        verify(subscriber, never()).onComplete();
    }

    /**
     * Verifies that the publisher signals neither {@code onError} nor {@code onComplete} when
     * {@code request(1)} is called repeatedly while a concurrent {@code cancel} arrives.
     *
     * @throws InterruptedException if the joining threads are interrupted.
     */
    @DisplayName("""
            should signal neither <onError> nor <onComplete>
            when <request(1)> is called repeatedly with concurrent <cancel>""")
    @Test
    void __cancel() throws InterruptedException {
        // ----------------------------------------------------------------------------------- given
        final var bound = 8;
        final var lock = new ReentrantLock();
        final var terminated = new AtomicBoolean();
        final var requester = new AtomicReference<Thread>();
        final var canceller = new AtomicReference<Thread>();
        final var subscriber = loggingArraySubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) {
                requester.set(Thread.ofPlatform().daemon().start(() -> {
                    for (var i = 0; i < bound; i++) {
                        sleep(Duration.ofSeconds(1L));
                        lock.lock();
                        try { if (terminated.get()) { break; } s.request(1L);
                        } finally { lock.unlock(); }
                    }
                }));
                canceller.set(Thread.ofPlatform().daemon().start(() -> {
                    sleep(3L, 6L);
                    lock.lock();
                    try { s.cancel(); terminated.set(true); } finally { lock.unlock(); }
                }));
            }
            @Override public void onNext(final byte[] t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        canceller.get().join(Duration.ofSeconds(20L).toMillis());
        requester.get().join(Duration.ofSeconds(20L).toMillis());
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, atMost(bound)).onNext(any());
        verify(subscriber, never()).onError(any());
        verify(subscriber, never()).onComplete();
    }

    // ------------------------------------------------------------------------- multi-subscriber (Rule 1.11)

    /**
     * Verifies that the publisher serves two sequential subscribers independently — each one
     * receives its own fresh stream of three arrays satisfying Rule 1.11 (a publisher MAY support
     * multi-subscription).
     */
    @DisplayName("should serve sequential subscribers independently (Rule 1.11)")
    @Test
    void __multipleSubscribers() {
        // ----------------------------------------------------------------------------------- given
        final var n = 3;
        final var subscriber1 = loggingArraySubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final byte[] t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        final var subscriber2 = loggingArraySubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final byte[] t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber1);
        publisher().subscribe(subscriber2);
        verify(subscriber1, timeout(TIMEOUT).times(n)).onNext(any());
        verify(subscriber2, timeout(TIMEOUT).times(n)).onNext(any());
        // ------------------------------------------------------------------------------------ then
        verify(subscriber1, times(1)).onSubscribe(notNull());
        verify(subscriber1, never()).onError(any());
        verify(subscriber1, never()).onComplete();
        verify(subscriber2, times(1)).onSubscribe(notNull());
        verify(subscriber2, never()).onError(any());
        verify(subscriber2, never()).onComplete();
    }

    // ------------------------------------------------------------------------- null subscriber

    /**
     * Verifies that {@link Publisher#subscribe(Subscriber)} throws a {@link NullPointerException}
     * when invoked with a {@code null} subscriber.
     */
    @DisplayName("should throw <NullPointerException> when <subscribe> is invoked with <null>")
    @Test
    void __subscriberIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var publisher = publisher();
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> publisher.subscribe(null));
    }
}
