package com.github.jinahya.hello.api;

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

import com.github.jinahya.hello.api.util.*;
import lombok.extern.slf4j.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.reactivestreams.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorldBookUtils.*;
import static org.mockito.Mockito.*;

/**
 * Utilities for Mockito.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class MockitoTestUtils {

    // ---------------------------------------------------------------------------------------------

    /**
     * Asserts specified object is a mock.
     *
     * @param object the object to test
     * @param <T>    object's type parameter
     * @return given {@code object}.
     */
    public static <T> T requireMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (!Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is not a mock: " + object);
        }
        return object;
    }

    public static <T> T requireNotMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is a mock: " + object);
        }
        return object;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Asserts specified object is a mock.
     *
     * @param object the object to test
     * @param <T>    object's type parameter
     * @return given {@code object}.
     */
    static <T extends HelloWorld> T requireMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (!Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is not a mock: " + object);
        }
        return object;
    }

    static <T extends HelloWorld> T requireNotMock(final T object) {
        Objects.requireNonNull(object, "object is null");
        if (Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is a mock: " + object);
        }
        return object;
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private MockitoTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }

    /**
     * Returns a {@link Mockito#mock(Class) Mockito mock} of {@code clazz} whose default answer
     * {@linkplain AdditionalAnswers#delegatesTo(Object) delegates} to
     * {@link HelloWorldBookUtils#loggingProxy(Class, Object) loggingProxy(clazz, delegate)} — every
     * call on the returned object is recorded for {@link Mockito#verify(Object) verify} /
     * {@link Mockito#inOrder(Object...) inOrder} / {@link ArgumentCaptor ArgumentCaptor}, is logged
     * at {@code DEBUG}, and then forwarded to {@code delegate}.
     *
     * @param clazz    the interface to mock; must not be {@code null} and must be an interface.
     * @param delegate the real instance to which calls are ultimately delivered; must not be
     *                 {@code null}.
     * @param <T>      the interface type.
     * @return a verifiable, logged proxy of {@code clazz}; never {@code null}.
     * @throws NullPointerException     if either {@code clazz} or {@code delegate} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code clazz} is not an interface.
     * @see HelloWorldBookUtils#loggingProxy(Class, Object)
     */
    @SuppressWarnings("unchecked")
    static <T> T loggingSpy(final Class<? super T> clazz, final T delegate) {
        Objects.requireNonNull(delegate, "delegate is null");
        return (T) mock(
                clazz,
                withSettings().defaultAnswer(invocation -> {
                    if (invocation.getMethod().getDeclaringClass() == clazz) {
                        log.debug("{}.{}({})",
                                  toHascodeString(invocation.getMock()),
                                  invocation.getMethod().getName(),
                                  argsString(invocation.getArguments()));
                    }
                    return invocation.getMethod().invoke(delegate, invocation.getArguments());
                })
        );
    }

    /**
     * Returns a Mockito-verifiable {@link Subscriber} that, on every received signal, logs the call
     * and forwards it to {@code delegate}. Equivalent to
     * {@link MockitoTestUtils#loggingSpy(Class, Object) loggingSpy(Subscriber.class, delegate)}.
     *
     * @param delegate the real {@link Subscriber} to which calls are ultimately delivered; must not
     *                 be {@code null}.
     * @param <T>      the element type.
     * @return a verifiable, logged subscriber; never {@code null}.
     * @throws NullPointerException if {@code delegate} is {@code null}.
     */
    static <T> Subscriber<T> loggingSpy(final Subscriber<T> delegate) {
        return loggingSpy(Subscriber.class, delegate);
    }

    /**
     * Returns a Mockito spy of the given real instance whose every invocation is logged at
     * {@code DEBUG} via an {@link org.mockito.listeners.InvocationListener InvocationListener} and
     * — unlike {@link MockitoTestUtils#loggingSpy(Class, Object) loggingSpy(...)} — preserves true
     * spy semantics: internal {@code this.foo(...)} calls from interface default methods still go
     * through the mock proxy, so {@link Mockito#verify(Object) verify} sees them.
     *
     * @param realInstance the real instance to spy on; must not be {@code null}.
     * @param <T>          the runtime type of {@code realInstance}.
     * @return a Mockito spy of {@code realInstance} that logs every non-{@link Object} method
     * invocation; never {@code null}.
     * @throws NullPointerException if {@code realInstance} is {@code null}.
     */
    @SuppressWarnings("unchecked")
    static <T> T loggingSpiedInstance(final T realInstance) {
//        Objects.requireNonNull(realInstance, "realInstance is null");
        requireNotMock(realInstance);
        final Class<T> clazz = (Class<T>) realInstance.getClass();
        return mock(clazz, withSettings()
                .spiedInstance(realInstance)
                .defaultAnswer(CALLS_REAL_METHODS)
                .invocationListeners(report -> {
                    final var inv = (Invocation) report.getInvocation();
                    if (inv.getMethod().getDeclaringClass() != Object.class) {
                        log.debug("{}.{}({})",
                                  toHascodeString(inv.getMock()),
                                  inv.getMethod().getName(),
                                  argsString(inv.getArguments()));
                    }
                }));
    }

    static <T extends HelloWorld> T loggingSpy(final T delegate) {
        return loggingSpy(HelloWorld.class, delegate);
    }

    /**
     * The {@link Flow.Subscriber} counterpart of {@link MockitoTestUtils#loggingSpy(Subscriber)}.
     *
     * @param delegate the real {@link Flow.Subscriber} to which calls are ultimately delivered;
     *                 must not be {@code null}.
     * @param <T>      the element type.
     * @return a verifiable, logged Flow subscriber; never {@code null}.
     * @throws NullPointerException if {@code delegate} is {@code null}.
     */
    static <T> Flow.Subscriber<T> loggingSpy(final Flow.Subscriber<T> delegate) {
        return loggingSpy(Flow.Subscriber.class, delegate);
    }
}
