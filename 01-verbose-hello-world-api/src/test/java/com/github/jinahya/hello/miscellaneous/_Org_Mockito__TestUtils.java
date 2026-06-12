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
import org.mockito.invocation.*;

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

    // ---------------------------------------------------------------------------------------------

    /**
     * The {@link HelloWorld}-bounded counterpart of {@link #requireMock(Object) requireMock(T)};
     * preserves the {@code <T extends HelloWorld>} bound at the call site for stubbing helpers that
     * want to keep their {@link HelloWorld} return type.
     *
     * @param object the {@link HelloWorld}-typed object to check; must not be {@code null}.
     * @param <T>    the {@link HelloWorld} subtype of {@code object}.
     * @return the given {@code object}, unchanged.
     * @throws NullPointerException     if {@code object} is {@code null}.
     * @throws IllegalArgumentException if {@code object} is not a Mockito mock.
     * @see #requireMock(Object)
     */
    static <T extends HelloWorld> T requireMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (!mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is not a mock: " + object);
        }
        return object;
    }

    /**
     * The {@link HelloWorld}-bounded counterpart of
     * {@link #requireNotMock(Object) requireNotMock(T)}; preserves the
     * {@code <T extends HelloWorld>} bound at the call site for helpers that want to exercise a
     * real {@link HelloWorld} rather than a mock.
     *
     * @param object the {@link HelloWorld}-typed object to check; must not be {@code null}.
     * @param <T>    the {@link HelloWorld} subtype of {@code object}.
     * @return the given {@code object}, unchanged.
     * @throws NullPointerException     if {@code object} is {@code null}.
     * @throws IllegalArgumentException if {@code object} is a Mockito mock.
     * @see #requireNotMock(Object)
     */
    static <T extends HelloWorld> T requireNotMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is a mock: " + object);
        }
        return object;
    }

    //    /**
//     * Returns a {@linkplain Mockito#spy(Object) Mockito spy} of the given real instance whose every
//     * non-{@link Object} method invocation is logged at {@code DEBUG} via an
//     * {@link org.mockito.listeners.InvocationListener InvocationListener}. Unlike
//     * {@link #loggingSpy(Class, Object) loggingSpy(...)}, this preserves <em>true</em> spy
//     * semantics — internal {@code this.foo(...)} calls from interface default-method bodies still
//     * route through the spy proxy — so {@link Mockito#verify(Object) verify} sees nested call
//     * chains and a contract test can assert delegation patterns such as
//     * {@code verify(service, times(1)).write(same(channel), same(attachment), notNull())}.
//     * <p>
//     * The {@link org.mockito.listeners.InvocationListener InvocationListener} fires on every
//     * invocation — including stubbed ones — so stubbed calls are logged just like unstubbed ones.
//     * <p>
//     * {@code realInstance} must be a real (non-mock) object: this method calls
//     * {@link #requireNotMock(Object) requireNotMock(realInstance)} first and throws
//     * {@link IllegalArgumentException} if a Mockito mock is passed in.
//     *
//     * @param realInstance the real instance to spy on; must not be {@code null} and must not be a
//     *                     Mockito mock.
//     * @param <T>          the runtime type of {@code realInstance}.
//     * @return a Mockito spy of {@code realInstance} that logs every non-{@link Object} method
//     * invocation; never {@code null}.
//     * @throws NullPointerException     if {@code realInstance} is {@code null}.
//     * @throws IllegalArgumentException if {@code realInstance} is a Mockito mock.
//     * @see #loggingSpy(Class, Object)
//     * @see #requireNotMock(Object)
//     */
    @SuppressWarnings("unchecked")
    public static <T> T loggingSpiedInstance(final T realInstance) {
        requireNotMock(realInstance);
        final Class<T> clazz = (Class<T>) realInstance.getClass();
        return mock(clazz, withSettings()
                .spiedInstance(realInstance)
                .defaultAnswer(CALLS_REAL_METHODS)
                .invocationListeners(report -> {
                    final var inv = (Invocation) report.getInvocation();
                    if (inv.getMethod().getDeclaringClass() == Flow.Subscriber.class) {
                        log.debug("xxxxxx");
//                        log.debug("{}.{}({})",
//                                  toHashcodeString(inv.getMock()),
//                                  inv.getMethod().getName(),
//                                  argsString(inv.getArguments()));
                    } else {
                        log.debug("yyyyyyy");
                    }
                }));
    }

    private static String formatByte(final byte v) {
        return String.format("0x%02x", Byte.toUnsignedInt(v));
    }

    private static String formatArray(final byte[] v) {
        return IntStream.range(0, v.length)
                .mapToObj(i -> formatByte(v[i]))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private static String toIdentityString(final Object object) {
        if (object == null) {
            return "null";
        }
        return _Java_Lang_TestUtils.toSimplifedString(object);
    }

    private static <T> CharSequence format(
            final Function<? super T, ? extends CharSequence> formatter, final T value) {
        if (value == null) {
            return "null";
        }
        try {
            return formatter.apply(value);
        } catch (final RuntimeException re) {
            return String.valueOf(value);
        }
    }

    public static final class OfFlow {

        @SuppressWarnings("unchecked")
        public static <T> Flow.Publisher<T> loggingPublisher(
                final Flow.Publisher<? extends T> realInstance) {
            requireNotMock(realInstance);
            final var clazz = realInstance.getClass();
            final var spy = (Flow.Publisher<T>) mock(clazz, withSettings()
                    .spiedInstance(realInstance)
                    .defaultAnswer(CALLS_REAL_METHODS));
            final var mock = toIdentityString(spy);
            doAnswer(i -> {
                log.debug("{}.subscribe({})", mock, toIdentityString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).subscribe(any());
            return spy;
        }

        public static Flow.Subscription loggingSubscription(
                final Flow.Subscription realInstance) {
            requireNotMock(realInstance);
            final var clazz = realInstance.getClass();
            final var spy = (Flow.Subscription) mock(clazz, withSettings()
                    .spiedInstance(realInstance)
                    .defaultAnswer(CALLS_REAL_METHODS));
            final var mock = toIdentityString(spy);
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

        @SuppressWarnings("unchecked")
        public static <T> Flow.Subscriber<T> loggingSubscriber(
                final Flow.Subscriber<? super T> realInstance,
                final Function<? super T, ? extends CharSequence> itemFormatter) {
            requireNotMock(realInstance);
            final var clazz = realInstance.getClass();
            final var spy = (Flow.Subscriber<T>) mock(clazz, withSettings()
                    .spiedInstance(realInstance)
                    .defaultAnswer(CALLS_REAL_METHODS));
            final var mock = toIdentityString(spy);
            doAnswer(i -> {
                log.debug("{}.onSubscribe({})", mock, toIdentityString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).onSubscribe(any());
            doAnswer(i -> {
                log.debug("{}.onNext({})", mock, format(itemFormatter, i.getArgument(0)));
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
            requireNotMock(realInstance);
            final var clazz = realInstance.getClass();
            final var spy = (Flow.Processor<T, R>) mock(clazz, withSettings()
                    .spiedInstance(realInstance)
                    .defaultAnswer(CALLS_REAL_METHODS));
            final var mock = toIdentityString(spy);
            doAnswer(i -> {
                log.debug("{}.subscribe({})", mock, toIdentityString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).subscribe(any());
            doAnswer(i -> {
                log.debug("{}.onSubscribe({})", mock, toIdentityString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).onSubscribe(any());
            doAnswer(i -> {
                log.debug("{}.onNext({})", mock, format(itemFormatter, i.getArgument(0)));
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

    public static final class OfReactiveStream {

        @SuppressWarnings("unchecked")
        public static <T> org.reactivestreams.Publisher<T> loggingPublisher(
                final org.reactivestreams.Publisher<? extends T> realInstance) {
            requireNotMock(realInstance);
            final var clazz = realInstance.getClass();
            final var spy = (org.reactivestreams.Publisher<T>) mock(clazz, withSettings()
                    .spiedInstance(realInstance)
                    .defaultAnswer(CALLS_REAL_METHODS));
            final var mock = toIdentityString(spy);
            doAnswer(i -> {
                log.debug("{}.subscribe({})", mock, toIdentityString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).subscribe(any());
            return spy;
        }

        public static org.reactivestreams.Subscription loggingSubscription(
                final org.reactivestreams.Subscription realInstance) {
            requireNotMock(realInstance);
            final var clazz = realInstance.getClass();
            final var spy = (org.reactivestreams.Subscription) mock(clazz, withSettings()
                    .spiedInstance(realInstance)
                    .defaultAnswer(CALLS_REAL_METHODS));
            final var mock = toIdentityString(spy);
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

        @SuppressWarnings("unchecked")
        public static <T> org.reactivestreams.Subscriber<T> loggingSubscriber(
                final org.reactivestreams.Subscriber<? super T> realInstance,
                final Function<? super T, ? extends CharSequence> itemFormatter) {
            requireNotMock(realInstance);
            final var clazz = realInstance.getClass();
            final var spy = (org.reactivestreams.Subscriber<T>) mock(clazz, withSettings()
                    .spiedInstance(realInstance)
                    .defaultAnswer(CALLS_REAL_METHODS));
            final var mock = toIdentityString(spy);
            doAnswer(i -> {
                log.debug("{}.onSubscribe({})", mock, toIdentityString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).onSubscribe(any());
            doAnswer(i -> {
                log.debug("{}.onNext({})", mock, format(itemFormatter, i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).onNext(any());
            doAnswer(i -> {
                final var thrown = i.<Throwable>getArgument(0);
                log.debug("{}.onError({})", mock, thrown, thrown);
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
            requireNotMock(realInstance);
            final var clazz = realInstance.getClass();
            final var spy = (org.reactivestreams.Processor<T, R>) mock(clazz, withSettings()
                    .spiedInstance(realInstance)
                    .defaultAnswer(CALLS_REAL_METHODS));
            final var mock = toIdentityString(spy);
            doAnswer(i -> {
                log.debug("{}.subscribe({})", mock, toIdentityString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).subscribe(any());
            doAnswer(i -> {
                log.debug("{}.onSubscribe({})", mock, toIdentityString(i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).onSubscribe(any());
            doAnswer(i -> {
                log.debug("{}.onNext({})", mock, format(itemFormatter, i.getArgument(0)));
                return i.callRealMethod();
            }).when(spy).onNext(any());
            doAnswer(i -> {
                final var thrown = i.<Throwable>getArgument(0);
                log.debug("{}.onError({})", mock, thrown, thrown);
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
