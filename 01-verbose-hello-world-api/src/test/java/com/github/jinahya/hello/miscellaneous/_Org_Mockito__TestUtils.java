package com.github.jinahya.hello.miscellaneous;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import com.github.jinahya.hello.api.*;
import lombok.extern.slf4j.*;
import org.mockito.*;
import org.mockito.mock.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

import static org.mockito.Mockito.*;

/**
 * Test-scoped Mockito predicate helpers — {@link #requireMock(Object) requireMock(...)} and
 * {@link #requireNotMock(Object) requireNotMock(...)} guard helpers that should only run against
 * (or away from) a Mockito mock; throw {@link IllegalArgumentException} otherwise. Both come in a
 * generic {@code public} form and a more constrained {@code <T extends HelloWorld>} package-private
 * form for call sites that want to keep the {@link HelloWorld} bound.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class _Org_Mockito__TestUtils {

    // -------------------------------------------------------------------------- PREDICATES / ASSERTIONS

    /**
     * Asserts that the specified object is a {@linkplain Mockito#mock(Class) Mockito mock} and
     * returns it; throws {@link IllegalArgumentException} otherwise. Used by helpers that only make
     * sense against a mock (e.g. stubbing helpers in {@code HelloWorldTestUtils}).
     *
     * @param object the object to check; must not be {@code null}.
     * @param <T>    the type of {@code object}.
     * @return the given {@code object}, unchanged.
     * @throws NullPointerException     if {@code object} is {@code null}.
     * @throws IllegalArgumentException if {@code object} is not a Mockito mock.
     * @see #requireNotMock(Object)
     */
    public static <T> T requireMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (!mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is not a mock: " + object);
        }
        return object;
    }

    /**
     * Asserts that the specified object is <em>not</em> a
     * {@linkplain Mockito#mock(Class) Mockito mock} and returns it; throws
     * {@link IllegalArgumentException} otherwise. Used by helpers that exercise a real
     * implementation and would be misled by a mock.
     *
     * @param object the object to check; must not be {@code null}.
     * @param <T>    the type of {@code object}.
     * @return the given {@code object}, unchanged.
     * @throws NullPointerException     if {@code object} is {@code null}.
     * @throws IllegalArgumentException if {@code object} is a Mockito mock.
     * @see #requireMock(Object)
     */
    public static <T> T requireNotMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is a mock: " + object);
        }
        return object;
    }

    /**
     * Like {@link _Java_Lang_TestUtils#toSimplifedString(Object)}, but if {@code object} is a
     * Mockito spy (or a spy of a spy …) the chain is walked down to the innermost
     * {@linkplain MockCreationSettings#getSpiedInstance() spied instance}, and that instance's
     * identity hash is rendered. Every wrapper layer of the same logical entity therefore prints
     * as the same {@code @<hex>}. For non-mocks and for mocks with no {@code spiedInstance} (e.g.
     * those created by {@link Mockito#mockConstruction(Class) mockConstruction}), the object's own
     * identity hash is used.
     *
     * @param object the object to render; may be {@code null}.
     * @return {@code "null"} if {@code object} is {@code null}; otherwise an {@code @<hex>} string.
     */
    public static String toSimplifedString(final Object object) {
        if (object == null) {
            return "null";
        }
        Object target = object;
        while (mockingDetails(target).isMock()) {
            final Object spied = mockingDetails(target)
                    .getMockCreationSettings()
                    .getSpiedInstance();
            if (spied == null || spied == target) {
                break;
            }
            target = spied;
        }
        return String.format("@%08x", System.identityHashCode(target));
    }

    private static String formatByte(final byte v) {
        return String.format("0x%02x", Byte.toUnsignedInt(v));
    }

    private static String formatArray(final byte[] v) {
        return IntStream.range(0, v.length)
                .mapToObj(i -> formatByte(v[i]))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private static <T> CharSequence print(
            final T value, final Function<? super T, ? extends CharSequence> formatter) {
        if (value == null) {
            return "null";
        }
        try {
            return formatter.apply(value);
        } catch (final RuntimeException re) {
            return String.valueOf(value);
        }
    }

    /**
     * Builds a {@linkplain Mockito#spy(Object) Mockito spy} of the specified {@code realInstance}
     * with {@link Mockito#CALLS_REAL_METHODS CALLS_REAL_METHODS} as the default answer — the shared
     * baseline used by every {@code logging*} helper below. The returned spy has the same static
     * type as the input.
     *
     * @param realInstance the real, non-mock instance to spy on.
     * @param <T>          the runtime type of {@code realInstance}.
     * @return a spy of {@code realInstance}; never {@code null}.
     * @throws NullPointerException     if {@code realInstance} is {@code null}.
     * @throws IllegalArgumentException if {@code realInstance} is already a Mockito mock.
     */
    @SuppressWarnings("unchecked")
    private static <T> T loggingSpyOf(final T realInstance) {
        requireNotMock(realInstance);
        final var clazz = (Class<T>) realInstance.getClass();
        return mock(clazz, withSettings()
                .spiedInstance(realInstance)
                .defaultAnswer(CALLS_REAL_METHODS));
    }

    // ai: should be analogues to OfReactiveStream
    public static final class OfFlow {

        @SuppressWarnings({"unchecked", "rawtypes"})
        public static <P extends Flow.Publisher<?>> P loggingPublisher(final P realInstance) {
            final P spy = loggingSpyOf(realInstance);
            final var mock = toSimplifedString(spy);
            doAnswer(i -> {
                log.debug("{}.subscribe({})", mock, toSimplifedString(i.getArgument(0)));
                return i.callRealMethod();
            }).when((Flow.Publisher) spy).subscribe(any());
            return spy;
        }

        public static Flow.Subscription loggingSubscription(
                final Flow.Subscription realInstance) {
            final var spy = loggingSpyOf(realInstance);
            final var mock = toSimplifedString(spy);
            doAnswer(i -> {
                log.debug("{}.request({})", mock, i.<Long>getArgument(0));
                return i.callRealMethod();
            }).when(spy).request(anyLong());
            doAnswer(i -> {
                log.debug("{}.cancel()", mock);
                return i.callRealMethod();
            }).when(spy).cancel();
            return spy;
        }

        public static <T> Flow.Subscriber<T> loggingSubscriber(
                final Flow.Subscriber<? super T> realInstance) {
            return loggingSubscriber(realInstance, Objects::toString);
        }

        public static <T> Flow.Subscriber<T> loggingSubscriber(
                final Consumer<? super Flow.Subscription> consumer) {
            Objects.requireNonNull(consumer, "consumer is null");
            return loggingSubscriber(new Flow.Subscriber<T>() { // @formatter:off
                @Override public void onSubscribe(final Flow.Subscription subscription) {
                    consumer.accept(subscription);
                }
                @Override public void onNext(final T item) { }
                @Override public void onError(final Throwable throwable) { }
                @Override public void onComplete() { } // @formatter:on
            });
        }

        public static <T> Flow.Subscriber<T> loggingSubscriberRequestsOnSubscribe(final long n) {
            if (n <= 0L) {
                throw new IllegalArgumentException("n(" + n + ") <= 0L");
            }
            return loggingSubscriber(s -> s.request(n));
        }

        @SuppressWarnings("unchecked")
        public static <T> Flow.Subscriber<T> loggingSubscriber(
                final Flow.Subscriber<? super T> realInstance,
                final Function<? super T, ? extends CharSequence> itemFormatter) {
            final Flow.Subscriber<T> spy = (Flow.Subscriber<T>) loggingSpyOf(realInstance);
            final var mock = toSimplifedString(spy);
            doAnswer(i -> {
                log.debug("{}.onSubscribe({})", mock, toSimplifedString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).onSubscribe(any());
            doAnswer(i -> {
                log.debug("{}.onNext({})", mock, print(i.getArgument(0), itemFormatter));
                return i.callRealMethod();
            }).when(spy).onNext(any());
            doAnswer(i -> {
                final var thrown = i.<Throwable>getArgument(0);
//                log.debug("{}.onError({})", mock, thrown, thrown);
                log.debug("{}.onError({})", mock, thrown.getMessage());
                return i.callRealMethod();
            }).when(spy).onError(any());
            doAnswer(i -> {
                log.debug("{}.onComplete()", mock);
                return i.callRealMethod();
            }).when(spy).onComplete();
            return spy;
        }

        @SuppressWarnings("unchecked")
        public static <T, R> Flow.Processor<T, R> loggingProcessor(
                final Flow.Processor<? super T, ? extends R> realInstance,
                final Function<? super T, ? extends CharSequence> itemFormatter) {
            final Flow.Processor<T, R> spy = (Flow.Processor<T, R>) loggingSpyOf(realInstance);
            final var mock = toSimplifedString(spy);
            doAnswer(i -> {
                log.debug("{}.subscribe({})", mock, toSimplifedString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).subscribe(any());
            doAnswer(i -> {
                log.debug("{}.onSubscribe({})", mock, toSimplifedString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).onSubscribe(any());
            doAnswer(i -> {
                log.debug("{}.onNext({})", mock, print(i.getArgument(0), itemFormatter));
                return i.callRealMethod();
            }).when(spy).onNext(any());
            doAnswer(i -> {
                final var thrown = i.<Throwable>getArgument(0);
//                log.debug("{}.onError({})", mock, thrown, thrown);
                log.debug("{}.onError({})", mock, thrown.getMessage());
                return i.callRealMethod();
            }).when(spy).onError(any());
            doAnswer(i -> {
                log.debug("{}.onComplete()", mock);
                return i.callRealMethod();
            }).when(spy).onComplete();
            return spy;
        }

        public static Flow.Subscriber<Byte> loggingByteSubscriber(
                final Flow.Subscriber<? super Byte> realInstance) {
            return loggingSubscriber(realInstance, _Org_Mockito__TestUtils::formatByte);
        }

        public static Flow.Subscriber<byte[]> loggingArraySubscriber(
                final Flow.Subscriber<? super byte[]> realInstance) {
            return loggingSubscriber(realInstance, _Org_Mockito__TestUtils::formatArray);
        }

        public static <R> Flow.Processor<Byte, R> loggingByteProcessor(
                final Flow.Processor<? super Byte, ? extends R> realInstance) {
            return loggingProcessor(realInstance, _Org_Mockito__TestUtils::formatByte);
        }

        public static <R> Flow.Processor<byte[], R> loggingArrayProcessor(
                final Flow.Processor<? super byte[], ? extends R> realInstance) {
            return loggingProcessor(realInstance, _Org_Mockito__TestUtils::formatArray);
        }

        private OfFlow() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // ai: should be analogues to OfFlow
    public static final class OfReactiveStream {

        @SuppressWarnings("rawtypes")
        public static <P extends org.reactivestreams.Publisher<?>> P loggingPublisher(
                final P realInstance) {
            final P spy = loggingSpyOf(realInstance);
            final var mock = toSimplifedString(spy);
            doAnswer(i -> {
                log.debug("{}.subscribe({})", mock, toSimplifedString(i.getArgument(0)));
                return i.callRealMethod();
            }).when((org.reactivestreams.Publisher) spy).subscribe(any());
            return spy;
        }

        public static org.reactivestreams.Subscription loggingSubscription(
                final org.reactivestreams.Subscription realInstance) {
            final var spy = loggingSpyOf(realInstance);
            final var mock = toSimplifedString(spy);
            doAnswer(i -> {
                log.debug("{}.request({})", mock, i.<Long>getArgument(0));
                return i.callRealMethod();
            }).when(spy).request(anyLong());
            doAnswer(i -> {
                log.debug("{}.cancel()", mock);
                return i.callRealMethod();
            }).when(spy).cancel();
            return spy;
        }

        public static <T> org.reactivestreams.Subscriber<T> loggingSubscriber(
                final org.reactivestreams.Subscriber<? super T> realInstance) {
            return loggingSubscriber(realInstance, Objects::toString);
        }

        public static <T> org.reactivestreams.Subscriber<T> loggingSubscriber(
                final Consumer<? super org.reactivestreams.Subscription> consumer) {
            Objects.requireNonNull(consumer, "consumer is null");
            return loggingSubscriber(new org.reactivestreams.Subscriber<T>() { // @formatter:off
                @Override public void onSubscribe(final org.reactivestreams.Subscription subscription) {
                    consumer.accept(subscription);
                }
                @Override public void onNext(final T item) { }
                @Override public void onError(final Throwable throwable) { }
                @Override public void onComplete() { } // @formatter:on
            });
        }

        public static <T> org.reactivestreams.Subscriber<T> loggingSubscriberRequestsOnSubscribe(
                final long n) {
            if (n <= 0L) {
                throw new IllegalArgumentException("n(" + n + ") <= 0L");
            }
            return loggingSubscriber(s -> s.request(n));
        }

        @SuppressWarnings("unchecked")
        public static <T> org.reactivestreams.Subscriber<T> loggingSubscriber(
                final org.reactivestreams.Subscriber<? super T> realInstance,
                final Function<? super T, ? extends CharSequence> itemFormatter) {
            final org.reactivestreams.Subscriber<T> spy =
                    (org.reactivestreams.Subscriber<T>) loggingSpyOf(realInstance);
            final var mock = toSimplifedString(spy);
            doAnswer(i -> {
                log.debug("{}.onSubscribe({})", mock, toSimplifedString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).onSubscribe(any());
            doAnswer(i -> {
                log.debug("{}.onNext({})", mock, print(i.getArgument(0), itemFormatter));
                return i.callRealMethod();
            }).when(spy).onNext(any());
            doAnswer(i -> {
                final var thrown = i.<Throwable>getArgument(0);
//                log.debug("{}.onError({})", mock, thrown, thrown);
                log.debug("{}.onError({})", mock, thrown.getMessage());
                return i.callRealMethod();
            }).when(spy).onError(any());
            doAnswer(i -> {
                log.debug("{}.onComplete()", mock);
                return i.callRealMethod();
            }).when(spy).onComplete();
            return spy;
        }

        @SuppressWarnings("unchecked")
        public static <T, R> org.reactivestreams.Processor<T, R> loggingProcessor(
                final org.reactivestreams.Processor<? super T, ? extends R> realInstance,
                final Function<? super T, ? extends CharSequence> itemFormatter) {
            final org.reactivestreams.Processor<T, R> spy =
                    (org.reactivestreams.Processor<T, R>) loggingSpyOf(realInstance);
            final var mock = toSimplifedString(spy);
            doAnswer(i -> {
                log.debug("{}.subscribe({})", mock, toSimplifedString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).subscribe(any());
            doAnswer(i -> {
                log.debug("{}.onSubscribe({})", mock, toSimplifedString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).onSubscribe(any());
            doAnswer(i -> {
                log.debug("{}.onNext({})", mock, print(i.getArgument(0), itemFormatter));
                return i.callRealMethod();
            }).when(spy).onNext(any());
            doAnswer(i -> {
                final var thrown = i.<Throwable>getArgument(0);
//                log.debug("{}.onError({})", mock, thrown);
                log.debug("{}.onError({})", mock, thrown.getMessage());
                return i.callRealMethod();
            }).when(spy).onError(any());
            doAnswer(i -> {
                log.debug("{}.onComplete()", mock);
                return i.callRealMethod();
            }).when(spy).onComplete();
            return spy;
        }

        public static org.reactivestreams.Subscriber<Byte> loggingByteSubscriber(
                final org.reactivestreams.Subscriber<? super Byte> realInstance) {
            return loggingSubscriber(realInstance, _Org_Mockito__TestUtils::formatByte);
        }

        public static org.reactivestreams.Subscriber<byte[]> loggingArraySubscriber(
                final org.reactivestreams.Subscriber<? super byte[]> realInstance) {
            return loggingSubscriber(realInstance, _Org_Mockito__TestUtils::formatArray);
        }

        public static <R> org.reactivestreams.Processor<Byte, R> loggingByteProcessor(
                final org.reactivestreams.Processor<? super Byte, ? extends R> realInstance) {
            return loggingProcessor(realInstance, _Org_Mockito__TestUtils::formatByte);
        }

        public static <R> org.reactivestreams.Processor<byte[], R> loggingArrayProcessor(
                final org.reactivestreams.Processor<? super byte[], ? extends R> realInstance) {
            return loggingProcessor(realInstance, _Org_Mockito__TestUtils::formatArray);
        }

        private OfReactiveStream() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    private _Org_Mockito__TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
