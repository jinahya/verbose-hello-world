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
import static com.github.jinahya.hello.miscellaneous._Org_Mockito__TestUtils.OfReactiveStream.*;
import static java.util.Arrays.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Subscription-level tests for {@link ReactiveHelloWorldBytePublisher} — verifies the Reactive
 * Streams 1.0 contract that this didactic publisher actively upholds (demand, completion, the
 * single-terminal-signal rule (1.7), cancellation, reentrant-{@code request} rules (3.2 / 3.3),
 * multi-subscription (1.11), …) using a {@link Mockito#spy(Object) spied} {@link Subscriber}.
 * <p>
 * The publisher trusts well-behaved externals and does <em>not</em> defend against
 * {@code request(n ≤ 0)} (Rule 3.9), subscriber-thrown signals (Rule 1.4 surface), or duplicate
 * subscription (Rule 1.10) — those tests are intentionally absent.
 * <p>
 * The constructor passes
 * {@link ReactiveHelloWorldBytePublisher#ReactiveHelloWorldBytePublisher(HelloWorld) new
 * ReactiveHelloWorldBytePublisher(service)} (as a method reference) to
 * {@link ReactiveHelloWorld__PublisherTest super}, which builds the mock {@link HelloWorld} service
 * and the publisher. The mock is stubbed by the inherited {@code @BeforeEach} hook in the base
 * class — see {@link ReactiveHelloWorld__PublisherTest#stubService()}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorld__PublisherTest
 * @see ReactiveHelloWorldBytePublisher
 */
@DisplayName("byte publisher")
@Slf4j
class ReactiveHelloWorld_Byte_PublisherTest extends ReactiveHelloWorld__PublisherTest<Byte> {

    /**
     * Maximum time to wait, in milliseconds, for an expected asynchronous subscriber interaction.
     */
    private static final long TIMEOUT = TimeUnit.SECONDS.toMillis(10L);

    /**
     * Quiescence window, in milliseconds, during which no further subscriber interaction is
     * expected to arrive.
     */
    private static final long QUIESCE = 500L;

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorld_Byte_PublisherTest() {
        super(s -> loggingPublisher(new ReactiveHelloWorldBytePublisher(s)));
    }

    // ------------------------------------------------------------------------- demand / completion

    /**
     * Verifies that the publisher emits exactly {@value HelloWorld#BYTES} elements followed by
     * {@code onComplete} when the subscriber calls {@code request(12)}.
     */
    @DisplayName(
            "should emit <12> elements and <onComplete> when the subscriber calls <request(12)>")
    @Test
    void __exactly12() {
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingByteSubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) {
                s.request(HelloWorld.BYTES);
            }
            @Override public void onNext(final Byte t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        verify(subscriber, timeout(TIMEOUT).times(1)).onComplete();
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
     * Verifies that the publisher emits {@code n} elements with no {@code onComplete} signal when
     * the subscriber calls {@code request(n)} with {@code n} in {@code [1, 12)}.
     */
    @DisplayName("""
            should emit <n> elements with no <onComplete>
            when the subscriber calls <request(n)> with <n> in <[1, 12)>""")
    @Test
    void __randomLessThan12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, HelloWorld.BYTES);
        final var subscriber = loggingByteSubscriber(new Subscriber<Byte>() {
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final Byte t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        verify(subscriber, timeout(TIMEOUT).times(n)).onNext(any());
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        final var elementCaptor = forClass(Byte.class);
        inOrder.verify(subscriber, times(n)).onNext(elementCaptor.capture());
        inOrder.verifyNoMoreInteractions();
        final var expected = copyOf(hello_world_byte_array(), n);
        final var elements = elementCaptor.getAllValues();
        assertEquals(expected.length, elements.size());
        for (int i = 0; i < elements.size(); i++) {
            assertEquals(expected[i], elements.get(i));
        } // @formatter:on
    }

    /**
     * Verifies that the publisher caps emission at {@value HelloWorld#BYTES} elements followed by a
     * single {@code onComplete} when the subscriber calls {@code request(n)} with {@code n > 12} —
     * Rule 1.2 (a publisher MUST NOT signal more {@code onNext} signals than the cumulative
     * demand).
     */
    @DisplayName("""
            should emit <12> elements and <onComplete>
            when the subscriber calls <request(n)> with <n > 12>""")
    @Test
    void __requestMoreThan12() {
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextLong(HelloWorld.BYTES + 1L, 1024L);
        final var subscriber = loggingByteSubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final Byte t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        verify(subscriber, timeout(TIMEOUT).times(1)).onComplete();
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
     * Verifies that demand accumulates across multiple {@code request(n)} calls — two synchronous
     * {@code request(5)} and {@code request(7)} from {@code onSubscribe} together unlock all
     * {@value HelloWorld#BYTES} elements followed by {@code onComplete}.
     */
    @DisplayName("""
            should accumulate demand across multiple <request(n)> calls
            (request(5) + request(7) → 12 elements and <onComplete>)""")
    @Test
    void __cumulativeDemand() {
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingByteSubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) {
                s.request(5L);
                s.request(7L);
            }
            @Override public void onNext(final Byte t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        verify(subscriber, timeout(TIMEOUT).times(1)).onComplete();
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        inOrder.verify(subscriber, times(HelloWorld.BYTES)).onNext(any());
        inOrder.verify(subscriber, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions(); // @formatter:on
    }

    /**
     * Verifies that reentrant {@code request(1)} calls issued from inside {@code onNext} are
     * accepted and accumulated without unbounded recursion — Rules 3.2 / 3.3. The subscriber pulls
     * one byte at a time and ultimately receives all {@value HelloWorld#BYTES} bytes followed by
     * {@code onComplete}.
     */
    @DisplayName("""
            should accept reentrant <request(1)> calls from inside <onNext>
            (Rules 3.2 / 3.3) and emit all <12> elements and <onComplete>""")
    @Test
    void __oneAtATime() {
        // ----------------------------------------------------------------------------------- given
        final var subRef = new AtomicReference<Subscription>();
        final var subscriber = loggingByteSubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) {
                subRef.set(s);
                s.request(1L);
            }
            @Override public void onNext(final Byte t) { subRef.get().request(1L); }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        verify(subscriber, timeout(TIMEOUT).times(1)).onComplete();
        // ------------------------------------------------------------------------------------ then
        final var inOrder = inOrder(subscriber);
        inOrder.verify(subscriber, times(1)).onSubscribe(notNull());
        inOrder.verify(subscriber, times(HelloWorld.BYTES)).onNext(any());
        inOrder.verify(subscriber, times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    // -------------------------------------------------------------------------------- cancellation

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
        final var subscriber = loggingByteSubscriber(new Subscriber<Byte>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) { s.cancel(); }
            @Override public void onNext(final Byte t) { }
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
        final var subscriber = loggingByteSubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) {
                s.cancel();
                s.cancel();
                s.cancel();
            }
            @Override public void onNext(final Byte t) { }
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
     * Verifies that a {@code cancel} issued from inside {@code onComplete} (Rule 3.6 — cancel after
     * a terminal signal is a no-op) is benign — the stream still delivers all
     * {@value HelloWorld#BYTES} elements followed by exactly one {@code onComplete} and no extra
     * signals.
     */
    @DisplayName("should ignore a <cancel> issued from inside <onComplete> (Rule 3.6)")
    @Test
    void __cancelAfterComplete() {
        // ----------------------------------------------------------------------------------- given
        final var subRef = new AtomicReference<Subscription>();
        final var subscriber = loggingByteSubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) {
                subRef.set(s);
                s.request(HelloWorld.BYTES);
            }
            @Override public void onNext(final Byte t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { subRef.get().cancel(); }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        verify(subscriber, timeout(TIMEOUT).times(1)).onComplete();
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, after(QUIESCE).times(1)).onComplete();
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, times(HelloWorld.BYTES)).onNext(any());
        verify(subscriber, never()).onError(any()); // @formatter:on
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
        final var lock = new ReentrantLock();
        final var terminated = new AtomicBoolean();
        final var requester = new AtomicReference<Thread>();
        final var canceller = new AtomicReference<Thread>();
        final var subscriber = loggingByteSubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) {
                requester.set(Thread.ofPlatform().daemon().start(() -> {
                    for (var i = 0; i < HelloWorld.BYTES; i++) {
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
            @Override public void onNext(final Byte t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        canceller.get().join(Duration.ofSeconds(20L).toMillis());
        requester.get().join(Duration.ofSeconds(20L).toMillis());
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, atMost(HelloWorld.BYTES)).onNext(any());
        verify(subscriber, never()).onError(any());
        verify(subscriber, never()).onComplete();
    }

    // ------------------------------------------------------------------------- multi-subscriber (Rule 1.11)

    /**
     * Verifies that the publisher serves two sequential subscribers independently — each one
     * receives its own fresh {@value HelloWorld#BYTES}-element stream followed by
     * {@code onComplete}, satisfying Rule 1.11 (a publisher MAY support multi-subscription).
     */
    @DisplayName("should serve sequential subscribers independently (Rule 1.11)")
    @Test
    void __multipleSubscribers() {
        // ----------------------------------------------------------------------------------- given
        final var subscriber1 = loggingByteSubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) { s.request(HelloWorld.BYTES); }
            @Override public void onNext(final Byte t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        final var subscriber2 = loggingByteSubscriber(new Subscriber<>() { // @formatter:off
            @Override public void onSubscribe(final Subscription s) { s.request(HelloWorld.BYTES); }
            @Override public void onNext(final Byte t) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        }); // @formatter:on
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber1);
        publisher().subscribe(subscriber2);
        verify(subscriber1, timeout(TIMEOUT).times(1)).onComplete();
        verify(subscriber2, timeout(TIMEOUT).times(1)).onComplete();
        // ------------------------------------------------------------------------------------ then
        verify(subscriber1, times(1)).onSubscribe(notNull());
        verify(subscriber1, times(HelloWorld.BYTES)).onNext(any());
        verify(subscriber1, never()).onError(any());
        verify(subscriber2, times(1)).onSubscribe(notNull());
        verify(subscriber2, times(HelloWorld.BYTES)).onNext(any());
        verify(subscriber2, never()).onError(any());
    }

    // ----------------------------------------------------------------------------- null subscriber

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
