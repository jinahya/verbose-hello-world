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

import static com.github.jinahya.hello.api.HelloWorldBookTestUtils.*;
import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
import static com.github.jinahya.hello.api.ReactiveHelloWorld__PublisherTestUtils.*;
import static java.util.Arrays.*;
import static org.awaitility.Awaitility.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Subscription-level tests for {@link ReactiveHelloWorldBytePublisher} — verifies the Reactive
 * Streams 1.0 contract (demand, completion, the single-terminal-signal rule (1.7), cancellation, …)
 * using a {@link Mockito#spy(Object) spied} {@link Subscriber} wrapped in a logging proxy via
 * {@link HelloWorldBookUtils#loggingProxy(Class, Object)}.
 * <p>
 * The constructor passes
 * {@link ReactiveHelloWorldBytePublisher#ReactiveHelloWorldBytePublisher(HelloWorld) new
 * ReactiveHelloWorldBytePublisher(service)} (as a method reference) to
 * {@link ReactiveHelloWorld__PublisherTest super}, which builds the mock {@link HelloWorld} service
 * and the logging-wrapped publisher. The mock is stubbed by the inherited {@code @BeforeEach} hook
 * in the base class — see {@link ReactiveHelloWorld__PublisherTest#stubService()}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ReactiveHelloWorld__PublisherTest
 * @see ReactiveHelloWorldBytePublisher
 */
@Slf4j
class ReactiveHelloWorld_Byte_PublisherTest
        extends ReactiveHelloWorld__PublisherTest<Byte> {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // ---------------------------------------------------------------------------------------------
    ReactiveHelloWorld_Byte_PublisherTest() {
        super(ReactiveHelloWorldBytePublisher::new);
    }

    // ---------------------------------------------------------------------------------------------
    @Test
    @DisplayName("request(12) → 12 elements, onComplete")
    void __exactly12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var subscriber = loggingSpy(new Subscriber<Byte>() {
            @Override public void onSubscribe(final Subscription s) {
                s.request(HelloWorld.BYTES);
            }
            @Override public void onNext(final Byte b) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(1)).onComplete());
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
        } // @formatter:on
    }

    @Test
    @DisplayName("request(n), n ∈ [1, 12) → n elements, no onComplete")
    void __randomLessThan12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextInt(1, HelloWorld.BYTES);
        final var subscriber = loggingSpy(new Subscriber<Byte>() {
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final Byte b) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(n)).onNext(any()));
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

    @Test
    @DisplayName("request(n), n > 12 → 12 elements, onComplete")
    void __requestMoreThan12() { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var n = ThreadLocalRandom.current().nextLong(HelloWorld.BYTES + 1L, 1024L);
        final var subscriber = loggingSpy(new Subscriber<Byte>() {
            @Override public void onSubscribe(final Subscription s) { s.request(n); }
            @Override public void onNext(final Byte b) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        await().atMost(TIMEOUT).untilAsserted(() -> verify(subscriber, times(1)).onComplete());
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
        } // @formatter:on
    }

    @DisplayName("request(1) repeatedly with concurrent cancel → no onError, no onComplete")
    @Test
    void __cancel() throws InterruptedException { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var lock = new ReentrantLock();
        final var terminated = new AtomicBoolean();
        final var requester = new AtomicReference<Thread>();
        final var canceller = new AtomicReference<Thread>();
        final var subscriber = loggingSpy(new Subscriber<Byte>() {
            @Override public void onSubscribe(final Subscription s) {
                requester.set(Thread.ofVirtual().start(() -> {
                    for (var i = 0; i < HelloWorld.BYTES; i++) {
                        sleep(Duration.ofSeconds(1L));
                        lock.lock();
                        try { if (terminated.get()) { break; } s.request(1L);
                        } finally { lock.unlock(); }
                    }
                }));
                canceller.set(Thread.ofVirtual().start(() -> {
                    sleep(3L, 6L);
                    lock.lock();
                    try { s.cancel(); terminated.set(true); } finally { lock.unlock(); }
                }));
            }
            @Override public void onNext(final Byte b) { }
            @Override public void onError(final Throwable t) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        publisher().subscribe(subscriber);
        canceller.get().join(Duration.ofSeconds(20L).toMillis());
        requester.get().join(Duration.ofSeconds(20L).toMillis());
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, atMost(HelloWorld.BYTES)).onNext(any());
        verify(subscriber, never()).onError(any());
        verify(subscriber, never()).onComplete(); // @formatter:on
    }
}
