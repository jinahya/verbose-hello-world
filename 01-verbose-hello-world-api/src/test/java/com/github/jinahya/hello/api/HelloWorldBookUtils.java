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

import org.jspecify.annotations.*;
import org.reactivestreams.*;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Package-private utilities shared by the {@code ReactiveHelloWorld*} family of tests.
 * <ul>
 *   <li>{@link #toSimplifiedString(String)} — strips both the package prefix and any
 *       enclosing-class prefixes from a class-name-like string; used by overridden
 *       {@link Object#toString() toString()} methods throughout the test sources to produce
 *       readable log output.</li>
 *   <li>{@link #formatByte(byte)} / {@link #formatArray(byte[])} — render a single {@code byte} or
 *       a {@code byte[]} payload as {@code <hex>'<char>'} tokens (e.g. {@code 68'h'},
 *       {@code [68'h' 65'e' …]}); used by the byte / byte-array logging subscribers to produce
 *       human-readable {@code onNext} log lines.</li>
 *   <li>{@link #loggingProxy(Class, Object)} — wraps any interface-typed instance in a JDK
 *       dynamic proxy that {@code DEBUG}-logs every interface method invocation before delegating
 *       to the wrapped instance.</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class HelloWorldBookUtils {

    private static final System.Logger logger = System.getLogger(
            MethodHandles.lookup().lookupClass().getName()
    );

    // ---------------------------------------------------------------------------------------------
    static String toHashcodeString(final Object object) {
        return String.format("@%08x", System.identityHashCode(object));
    }

    static String argsString(final @Nullable Object[] args) {
        if (args == null) {
            return "";
        }
        return Arrays.stream(args)
                .map(HelloWorldBookUtils::format)
                .collect(Collectors.joining(", "));
    }

    /**
     * Renders the given value in a log-friendly form, type-dispatched:
     * <ul>
     *   <li>{@code null} → {@code "null"}</li>
     *   <li>{@link Byte} → {@link #formatByte(byte)} (e.g. {@code 68'h'})</li>
     *   <li>{@code byte[]} → {@link #formatArray(byte[])} (e.g. {@code [68'h' 65'e' …]})</li>
     *   <li>{@link Number} (int, long, …) → its {@code toString}, as is</li>
     *   <li>{@link CharSequence} → the string content, unquoted (identifier-like)</li>
     *   <li>anything else → {@link #toHashcodeString(Object)} (identity hash)</li>
     * </ul>
     */
    static String format(final @Nullable Object value) {
        return switch (value) {
            case null -> "null";
            case Byte b -> formatByte(b);
            case byte[] a -> formatArray(a);
            case Number n -> n.toString();
            case CharSequence s -> s.toString();
            default -> toHashcodeString(value);
        };
    }

    /**
     * Wraps the given {@code delegate} in a {@link Proxy JDK dynamic proxy} that {@code DEBUG}-logs
     * every {@code clazz}-declared method invocation (as {@code @<identity-hex>/<method>(<args>)},
     * e.g. {@code @7e514482/onNext([68'h'])}) before forwarding the call to {@code delegate}. The
     * class name is omitted intentionally because the proxied interface is fixed and clear from the
     * call site; identity hash alone is enough to distinguish concurrent instances in log output.
     * Methods inherited from {@link Object} (e.g. {@link Object#toString() toString},
     * {@link Object#hashCode() hashCode}) pass through without a log line.
     * <p>
     * Typical use is to instrument a Reactive Streams {@code Subscriber} / {@code Subscription} or
     * a {@code Flow.Subscriber} / {@code Flow.Subscription} without writing a manual
     * {@code Logging<X>} wrapper class:
     * <pre>{@code
     *     final Subscriber<byte[]> raw = new Subscriber<>() { … };
     *     final Subscriber<byte[]> logged = loggingProxy(Subscriber.class, raw);
     *     publisher.subscribe(logged);
     * }</pre>
     *
     * @param clazz    the interface to proxy; must not be {@code null} and must be an interface.
     * @param delegate the instance to forward calls to; must not be {@code null}.
     * @param <T>      the interface type.
     * @return a {@code clazz}-typed proxy that logs and delegates; never {@code null}.
     * @throws NullPointerException     if either {@code clazz} or {@code delegate} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code clazz} is not an interface.
     */
    @SuppressWarnings("unchecked")
    static <T> T loggingProxy(final Class<? super T> clazz, final T delegate) {
        if (!Objects.requireNonNull(clazz, "clazz is null").isInterface()) {
            throw new IllegalArgumentException("clazz is not an interface: " + clazz);
        }
        Objects.requireNonNull(delegate, "delegate is null");
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[] {clazz},
                (p, m, args) -> {
                    if (m.getDeclaringClass() == clazz) {
                        logger.log(System.Logger.Level.DEBUG, "{0}.{1}({2})",
                                   toHashcodeString(delegate),
                                   m.getName(),
                                   argsString(args));
                    }
                    return m.invoke(delegate, args);
                }
        );
    }

    static <T> org.reactivestreams.Publisher<T> loggingPublisher(
            final org.reactivestreams.Publisher<T> delegate) {
        return loggingProxy(Publisher.class, delegate);
    }

    static org.reactivestreams.Subscription loggingSubscription(
            final org.reactivestreams.Subscription delegate) {
        return loggingProxy(Subscription.class, delegate);
    }

    static <T> org.reactivestreams.Subscriber<T> loggingSubscriber(
            final org.reactivestreams.Subscriber<T> delegate) {
        return loggingProxy(Subscriber.class, delegate);
    }

    static <T> Flow.Publisher<T> loggingPublisher(final Flow.Publisher<T> delegate) {
        return loggingProxy(Flow.Publisher.class, delegate);
    }

    static Flow.Subscription loggingSubscription(final Flow.Subscription delegate) {
        return loggingProxy(Flow.Subscription.class, delegate);
    }

    static <T> Flow.Subscriber<T> loggingSubscriber(final Flow.Subscriber<T> delegate) {
        return loggingProxy(Flow.Subscriber.class, delegate);
    }

    /**
     * Strips both the package prefix and any enclosing-class prefixes from a class-name-like
     * {@code string}, returning the simple-name segment (plus anything after it, e.g. an
     * {@code @<hash>} suffix on an {@link Object#toString() Object.toString()} result).
     * <ul>
     *   <li>{@code "a.b.c.D"}        &rarr; {@code "D"}</li>
     *   <li>{@code "a.b.c.D$E$F"}    &rarr; {@code "F"}</li>
     *   <li>{@code "a.b.c.D@1f2"}    &rarr; {@code "D@1f2"}</li>
     *   <li>{@code "a.b.c.D$E@1f2"}  &rarr; {@code "E@1f2"}</li>
     *   <li>{@code "D"}              &rarr; {@code "D"}</li>
     * </ul>
     *
     * @param string the string to simplify; must not be {@code null}.
     * @return the substring starting at the byte after the last {@code '.'} or {@code '$'}, or
     * {@code string} itself if neither character is present.
     * @throws NullPointerException if {@code string} is {@code null}.
     */
    static String toSimplifiedString(final String string) {
        Objects.requireNonNull(string, "string is null");
        final var cut = Math.max(string.lastIndexOf('.'), string.lastIndexOf('$'));
        return cut < 0 ? string : string.substring(cut + 1);
    }

    static String formatByte(final byte b) {
        return String.format("%02x'%c'", b, b);
    }

    /**
     * Renders a {@code byte[]} as a bracketed, space-separated sequence of {@code <hex>'<char>'}
     * tokens — e.g. the {@value HelloWorld#BYTES}-byte hello-world payload becomes
     * {@code [68'h' 65'e' 6c'l' 6c'l' 6f'o' 2c',' 20' ' 77'w' 6f'o' 72'r' 6c'l' 64'd']}.
     * <p>
     * Each byte is formatted with {@code %02x'%c'} (lower-case hex, single-quoted ASCII
     * character).
     *
     * @param array the array to format; must not be {@code null}.
     * @return a human-readable representation of {@code array}; never {@code null}.
     * @throws NullPointerException if {@code array} is {@code null}.
     */
    static String formatArray(final byte[] array) {
        return IntStream.range(0, array.length)
                .mapToObj(i -> formatByte(array[i]))
                .collect(Collectors.joining(" ", "[", "]"));
    }

    private HelloWorldBookUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
