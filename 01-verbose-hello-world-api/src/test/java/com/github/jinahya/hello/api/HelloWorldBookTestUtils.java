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

import lombok.extern.slf4j.Slf4j;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;

import java.util.Objects;
import java.util.concurrent.Flow;

import static com.github.jinahya.hello.api.HelloWorldBookUtils.argsString;
import static com.github.jinahya.hello.api.HelloWorldBookUtils.toHascodeString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

/**
 * Test-scoped helpers that compose {@link HelloWorldBookUtils}' logging proxies with
 * {@link Mockito} so a single value is *both* logged (every call printed at {@code DEBUG} via the
 * JDK dynamic proxy) and verifiable (every call recorded for
 * {@link Mockito#verify(Object) Mockito.verify}, {@link Mockito#inOrder(Object...) inOrder}, and
 * {@link org.mockito.ArgumentCaptor ArgumentCaptor}).
 * <p>
 * The pattern is
 * {@code Mockito.mock(Interface.class, defaultAnswer(delegatesTo(loggingProxy(real))))}: the
 * returned object is the Mockito mock (so verifications work on it directly), and every invocation
 * is forwarded to the logging proxy, which logs and then forwards to {@code real}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
final class HelloWorldBookTestUtils {

    /**
     * Returns a {@link Mockito#mock(Class) Mockito mock} of {@code clazz} whose default answer
     * {@linkplain AdditionalAnswers#delegatesTo(Object) delegates} to
     * {@link HelloWorldBookUtils#loggingProxy(Class, Object) loggingProxy(clazz, delegate)} — every
     * call on the returned object is recorded for {@link Mockito#verify(Object) verify} /
     * {@link Mockito#inOrder(Object...) inOrder} /
     * {@link org.mockito.ArgumentCaptor ArgumentCaptor}, is logged at {@code DEBUG}, and then
     * forwarded to {@code delegate}.
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
    private static <T> T loggingSpy(final Class<? super T> clazz, final T delegate) {
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
     * {@link #loggingSpy(Class, Object) loggingSpy(Subscriber.class, delegate)}.
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
     * The {@link Flow.Subscriber} counterpart of {@link #loggingSpy(Subscriber)}.
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

    private HelloWorldBookTestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
