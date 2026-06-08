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

import lombok.extern.slf4j.*;
import org.mockito.*;
import org.mockito.invocation.*;
import org.reactivestreams.*;

import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.api.HelloWorldBookUtils.*;
import static org.mockito.Mockito.*;

/**
 * Test-scoped Mockito utilities grouped by purpose.
 * <ul>
 *   <li><b>Predicates / assertions</b> —
 *       {@link #requireMock(Object) requireMock(...)} and
 *       {@link #requireNotMock(Object) requireNotMock(...)} guard helpers that should only run
 *       against (or away from) a Mockito mock; throw {@link IllegalArgumentException} otherwise.
 *       Both come in a generic {@code public} form and a more constrained
 *       {@code <T extends HelloWorld>} package-private form for call sites that want to keep
 *       the {@link HelloWorld} bound.</li>
 *   <li><b>Logging spy (mock + delegate via {@code defaultAnswer})</b> —
 *       {@link #loggingSpy(Class, Object) loggingSpy(...)} creates a {@link Mockito#mock(Class)
 *       Mockito mock} of an interface whose {@code defaultAnswer} both logs the call at
 *       {@code DEBUG} and reflectively forwards it to a real delegate. The returned object is a
 *       full Mockito mock (so {@link Mockito#verify(Object) verify} / {@link Mockito#inOrder(Object...)
 *       inOrder} / {@link ArgumentCaptor ArgumentCaptor} work on it), but because forwarding goes
 *       through reflection on the delegate, <em>internal</em> {@code this.foo(...)} calls inside
 *       interface default methods bypass the proxy and are not seen by {@code verify}. Use this
 *       when you don't need to verify nested-call chains. Typed convenience overloads exist for
 *       {@link Subscriber}, {@link Flow.Subscriber}, and {@code <T extends HelloWorld>}.</li>
 *   <li><b>Logging spied instance (true Mockito spy + {@link org.mockito.listeners.InvocationListener
 *       InvocationListener})</b> —
 *       {@link #loggingSpiedInstance(Object) loggingSpiedInstance(...)} produces a Mockito spy of a
 *       concrete real instance with an {@code InvocationListener} that logs every non-{@link Object}
 *       method invocation. Unlike {@code loggingSpy(...)}, this preserves true spy semantics:
 *       internal {@code this.foo(...)} calls from the interface's default-method bodies still go
 *       through the spy proxy, so {@link Mockito#verify(Object) verify} sees them. Use this when
 *       a contract test needs to assert delegation chains (e.g.
 *       {@code verify(service, times(1)).write(same(channel), same(attachment), notNull())}).</li>
 * </ul>
 * <p>
 * Members are ordered <em>callees-first</em>: utilities used by others (the generic
 * {@code requireMock} / {@code requireNotMock} and the base
 * {@link #loggingSpy(Class, Object) loggingSpy(Class, T)}) appear above their callers, and earlier
 * sections never depend on later ones.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
@SuppressWarnings({
        "java:S101"
})
public final class Mockito__TestUtils {

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
        if (!Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is not a mock: " + object);
        }
        return object;
    }

    /**
     * Asserts that the specified object is <em>not</em> a
     * {@linkplain Mockito#mock(Class) Mockito mock} and returns it; throws
     * {@link IllegalArgumentException} otherwise. Used by helpers that exercise a real
     * implementation and would be misled by a mock — for example,
     * {@link #loggingSpiedInstance(Object) loggingSpiedInstance(...)} expects a vanilla instance to
     * spy on, not an already-mocked one.
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
        if (Mockito.mockingDetails(object).isMock()) {
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
        if (!Mockito.mockingDetails(object).isMock()) {
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
        if (Mockito.mockingDetails(object).isMock()) {
            throw new IllegalArgumentException("is a mock: " + object);
        }
        return object;
    }

    // ----------------------------------------------------- LOGGING SPY (mock + delegate via defaultAnswer)

    /**
     * Returns a {@link Mockito#mock(Class) Mockito mock} of {@code clazz} whose
     * {@code defaultAnswer} both logs the invocation at {@code DEBUG} and reflectively forwards it
     * to {@code delegate}. Every call on the returned object is recorded for
     * {@link Mockito#verify(Object) verify} / {@link Mockito#inOrder(Object...) inOrder} /
     * {@link ArgumentCaptor ArgumentCaptor}, so the returned object behaves as a fully-verifiable
     * Mockito mock.
     * <p>
     * <b>Caveat — internal calls bypass the mock.</b> Forwarding is performed by
     * {@code invocation.getMethod().invoke(delegate, args)}, so recursive {@code this.foo(...)}
     * calls emitted by interface default-method bodies reach {@code delegate} directly and are
     * <em>not</em> recorded on the returned mock. Use
     * {@link #loggingSpiedInstance(Object) loggingSpiedInstance(...)} instead when a contract test
     * needs to verify nested-call delegation chains.
     *
     * @param clazz    the interface to mock; must not be {@code null} and must be an interface.
     * @param delegate the real instance to which calls are ultimately delivered; must not be
     *                 {@code null}.
     * @param <T>      the interface type.
     * @return a verifiable, logged mock of {@code clazz}; never {@code null}.
     * @throws NullPointerException     if either {@code clazz} or {@code delegate} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code clazz} is not an interface.
     * @see #loggingSpiedInstance(Object)
     */
    @SuppressWarnings("unchecked")
    static <T> T loggingSpy(final Class<? super T> clazz, final T delegate) {
        Objects.requireNonNull(delegate, "delegate is null");
        return (T) mock(
                clazz,
                withSettings().defaultAnswer(invocation -> {
                    if (invocation.getMethod().getDeclaringClass() == clazz) {
                        log.debug("{}.{}({})",
                                  toHashcodeString(invocation.getMock()),
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
     * {@link Mockito__TestUtils#loggingSpy(Class, Object) loggingSpy(Subscriber.class, delegate)}.
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
     * The {@link Flow.Subscriber} counterpart of
     * {@link Mockito__TestUtils#loggingSpy(Subscriber)}.
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

    /**
     * The {@link HelloWorld}-typed convenience for
     * {@link #loggingSpy(Class, Object) loggingSpy(HelloWorld.class, delegate)}.
     *
     * @param delegate the real {@link HelloWorld} (or subtype) to which calls are ultimately
     *                 delivered; must not be {@code null}.
     * @param <T>      the {@link HelloWorld} subtype.
     * @return a verifiable, logged mock of {@link HelloWorld}; never {@code null}.
     * @throws NullPointerException if {@code delegate} is {@code null}.
     * @see #loggingSpy(Class, Object)
     */
    static <T extends HelloWorld> T loggingSpy(final T delegate) {
        return loggingSpy(HelloWorld.class, delegate);
    }

    // --------------------------------------------------- LOGGING SPIED INSTANCE (real spy + InvocationListener)

    /**
     * Returns a {@linkplain Mockito#spy(Object) Mockito spy} of the given real instance whose every
     * non-{@link Object} method invocation is logged at {@code DEBUG} via an
     * {@link org.mockito.listeners.InvocationListener InvocationListener}. Unlike
     * {@link #loggingSpy(Class, Object) loggingSpy(...)}, this preserves <em>true</em> spy
     * semantics — internal {@code this.foo(...)} calls from interface default-method bodies still
     * route through the spy proxy — so {@link Mockito#verify(Object) verify} sees nested call
     * chains and a contract test can assert delegation patterns such as
     * {@code verify(service, times(1)).write(same(channel), same(attachment), notNull())}.
     * <p>
     * The {@link org.mockito.listeners.InvocationListener InvocationListener} fires on every
     * invocation — including stubbed ones — so stubbed calls are logged just like unstubbed ones.
     * <p>
     * {@code realInstance} must be a real (non-mock) object: this method calls
     * {@link #requireNotMock(Object) requireNotMock(realInstance)} first and throws
     * {@link IllegalArgumentException} if a Mockito mock is passed in.
     *
     * @param realInstance the real instance to spy on; must not be {@code null} and must not be a
     *                     Mockito mock.
     * @param <T>          the runtime type of {@code realInstance}.
     * @return a Mockito spy of {@code realInstance} that logs every non-{@link Object} method
     * invocation; never {@code null}.
     * @throws NullPointerException     if {@code realInstance} is {@code null}.
     * @throws IllegalArgumentException if {@code realInstance} is a Mockito mock.
     * @see #loggingSpy(Class, Object)
     * @see #requireNotMock(Object)
     */
    @SuppressWarnings("unchecked")
    static <T> T loggingSpiedInstance(final T realInstance) {
        requireNotMock(realInstance);
        final Class<T> clazz = (Class<T>) realInstance.getClass();
        return mock(clazz, withSettings()
                .spiedInstance(realInstance)
                .defaultAnswer(CALLS_REAL_METHODS)
                .invocationListeners(report -> {
                    final var inv = (Invocation) report.getInvocation();
                    if (inv.getMethod().getDeclaringClass() != Object.class) {
                        log.debug("{}.{}({})",
                                  toHashcodeString(inv.getMock()),
                                  inv.getMethod().getName(),
                                  argsString(inv.getArguments()));
                    }
                }));
    }

    // ---------------------------------------------------------------------------------------------
    private Mockito__TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
