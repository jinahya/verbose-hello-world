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

import java.time.*;
import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
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
@DisplayName("byte publisher")
@Slf4j
class HelloWorld_Byte_Publisher_Test extends HelloWorld__Publisher_Test<Byte> {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // ---------------------------------------------------------------------------------------------
    HelloWorld_Byte_Publisher_Test() {
        super(HelloWorldBytePublisher::new);
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("""
            should emit exactly <12> elements and <onComplete>
            when the subscriber calls <request(12)>""")
    @Test
    void __singleExactly12() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var subscriber = spy(new Flow.Subscriber<Byte>() {
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }
            @Override public void onNext(final Byte item) { }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(publisher -> {
            publisher.subscribe(subscriber);
            verify(subscriber, timeout(TIMEOUT.toMillis()).times(1)).onComplete();
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
        } // @formatter:on
    }

    @DisplayName("""
            should give each subscriber <min(n, 12)> elements and <onComplete>
            when <n >= 12>, given multiple subscribers each requesting <n> in <[1, 24)>""")
    @Test
    void __multiRandom1To24() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var count = ThreadLocalRandom.current().nextInt(2, 5);
        final var demands = new int[count];
        final var subscribers = new ArrayList<Flow.Subscriber<Byte>>(count);
        for (int i = 0; i < count; i++) {
            final var n = ThreadLocalRandom.current().nextInt(1, HelloWorld.BYTES << 1); // [1, 24)
            demands[i] = n;
            subscribers.add(spy(new Flow.Subscriber<>() {
                @Override public void onSubscribe(final Flow.Subscription subscription) {
                    subscription.request(n);
                }
                @Override public void onNext(final Byte item) { }
                @Override public void onError(final Throwable throwable) { }
                @Override public void onComplete() { }
            }));
        }
        log.debug("count: {}", count);
        log.debug("demands: {}", demands);
        // ------------------------------------------------------------------------------------ when
        applyPublisher(p -> {
            for (final var subscriber : subscribers) {
                p.subscribe(subscriber);
            }
            for (int i = 0; i < count; i++) {
                final var expectedNext = Math.min(demands[i], HelloWorld.BYTES);
                final var expectedComplete = demands[i] >= HelloWorld.BYTES ? 1 : 0;
                verify(subscribers.get(i), timeout(TIMEOUT.toMillis()).times(expectedNext))
                        .onNext(any());
                if (expectedComplete > 0) {
                    verify(subscribers.get(i), timeout(TIMEOUT.toMillis()).times(expectedComplete))
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

    @DisplayName("""
            should signal <onError> with no <onNext> and no <onComplete>
            when <service.set> throws""")
    @Test
    void __serviceThrows() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var error = new RuntimeException("simulated set(byte[]) failure");
        doThrow(error).when(service()).set(any(byte[].class));
        final var subscriber = spy(new Flow.Subscriber<Byte>() {
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }
            @Override public void onNext(final Byte item) { }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(publisher -> {
            publisher.subscribe(subscriber);
            verify(subscriber, timeout(TIMEOUT.toMillis()).times(1)).onError(notNull());
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

    @DisplayName("""
            should deliver <onError> via <SubmissionPublisher>
            and let <subscribe> return normally
            when <subscriber.onSubscribe> throws""")
    @Test
    void __onSubscribeThrows() throws Exception { // @formatter:off
        // ----------------------------------------------------------------------------------- given
        final var error = new RuntimeException("simulated onSubscribe failure");
        final var subscriber = spy(new Flow.Subscriber<Byte>() {
            @Override public void onSubscribe(final Flow.Subscription subscription) {
                throw error;
            }
            @Override public void onNext(final Byte item) { }
            @Override public void onError(final Throwable throwable) { }
            @Override public void onComplete() { }
        });
        // ------------------------------------------------------------------------------------ when
        applyPublisher(publisher -> {
            publisher.subscribe(subscriber);                  // returns normally
            verify(subscriber, timeout(TIMEOUT.toMillis()).times(1)).onError(notNull());
            return null;
        });
        // ------------------------------------------------------------------------------------ then
        verify(subscriber, times(1)).onSubscribe(notNull());
        verify(subscriber, never()).onNext(any());
        verify(subscriber, never()).onComplete(); // @formatter:on
    }
}
