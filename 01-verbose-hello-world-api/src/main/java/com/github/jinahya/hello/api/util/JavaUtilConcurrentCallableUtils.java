package com.github.jinahya.hello.api.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Helpers that adapt {@link Callable java.util.concurrent.Callable} into call sites that disallow
 * checked exceptions — either by passing the exception to a caller-supplied {@link Consumer}, or by
 * wrapping it (as {@link UncheckedIOException} for {@link IOException}, otherwise
 * {@link RuntimeException}).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class JavaUtilConcurrentCallableUtils {

    /**
     * Calls {@code callable.call()} and, on any thrown {@link Exception}, passes it to
     * {@code consumer} instead of rethrowing.
     *
     * @param callable the callable to invoke; must not be {@code null}.
     * @param consumer the consumer to receive any thrown {@link Exception}; must not be
     *                 {@code null}.
     * @throws NullPointerException if either argument is {@code null}.
     */
    public static void callUnchecked(final Callable<?> callable,
                                     final Consumer<? super Exception> consumer) {
        Objects.requireNonNull(callable, "callable is null");
        Objects.requireNonNull(consumer, "consumer is null");
        try {
            callable.call();
        } catch (final Exception e) {
            consumer.accept(e);
        }
    }

    /**
     * Calls {@code callable.call()} and returns its result, wrapping any thrown checked
     * {@link Exception} so the caller does not have to declare it: {@link IOException} →
     * {@link UncheckedIOException}, anything else → a {@link RuntimeException} whose {@code cause}
     * is the original exception.
     *
     * @param callable the callable to invoke; must not be {@code null}.
     * @param <V>      the result type.
     * @return the value returned by {@code callable.call()}.
     * @throws NullPointerException if {@code callable} is {@code null}.
     * @throws UncheckedIOException if {@code callable} threw an {@link IOException}.
     * @throws RuntimeException     if {@code callable} threw any other {@link Exception}; the cause
     *                              is the original exception.
     */
    public static <V> V callUnchecked(final Callable<V> callable) {
        Objects.requireNonNull(callable, "callable is null");
        try {
            return callable.call();
        } catch (final Exception e) {
            if (e instanceof IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
            throw new RuntimeException("failed to call " + callable, e);
        }
    }

    private JavaUtilConcurrentCallableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
