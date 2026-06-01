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
import java.lang.invoke.*;
import java.util.*;
import java.util.function.*;

/**
 * Helpers for {@link Closeable java.io.Closeable} — three flavors of
 * {@link Closeable#close() close()} that all hide the checked {@link IOException}: route the
 * exception to a caller {@link Consumer}, wrap it as a {@link RuntimeException}, or swallow it with
 * an {@code ERROR}-level log line.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaIoCloseableUtils {

    private static final System.Logger logger =
            System.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Closes {@code closeable} and, on any {@link IOException}, passes it to {@code consumer}
     * instead of rethrowing. A non-{@link IOException} {@link Exception} (unlikely but possible if
     * {@code close()} throws an undeclared one) is wrapped in a {@link RuntimeException}.
     *
     * @param closeable the resource to close; must not be {@code null}.
     * @param consumer  the consumer to receive a thrown {@link IOException}; must not be
     *                  {@code null}.
     * @throws NullPointerException if either argument is {@code null}.
     */
    public static void closeUnchecked(final Closeable closeable,
                                      final Consumer<? super IOException> consumer) {
        Objects.requireNonNull(closeable, "closeable is null");
        Objects.requireNonNull(consumer, "consumer is null");
        JavaUtilConcurrentCallableUtils.callUnchecked(
                () -> {
                    closeable.close();
                    return closeable;
                },
                e -> {
                    if (e instanceof IOException ioe) {
                        consumer.accept(ioe);
                        return;
                    }
                    throw new RuntimeException(e);
                }
        );
    }

    /**
     * Closes {@code closeable} and returns it, wrapping any thrown {@link IOException} in an
     * {@link UncheckedIOException} via
     * {@link JavaUtilConcurrentCallableUtils#callUnchecked(java.util.concurrent.Callable)}.
     *
     * @param closeable the resource to close; must not be {@code null}.
     * @param <T>       the concrete {@link Closeable} subtype.
     * @return the given {@code closeable}, after closing.
     * @throws NullPointerException if {@code closeable} is {@code null}.
     * @throws UncheckedIOException if {@code closeable.close()} threw an {@link IOException}.
     */
    public static <T extends Closeable> T closeUnchecked(final T closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        return JavaUtilConcurrentCallableUtils.callUnchecked(() -> {
            closeable.close();
            return closeable;
        });
    }

    /**
     * Closes {@code closeable}, swallowing any {@link IOException} by logging it at {@code ERROR}
     * level. Intended for finalizer-style cleanup paths where rethrowing the failure would mask a
     * more important pending exception.
     *
     * @param closeable the resource to close; must not be {@code null}.
     * @throws NullPointerException if {@code closeable} is {@code null}.
     */
    public static void closeSilently(final Closeable closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        try {
            closeable.close();
        } catch (final IOException ioe) {
            logger.log(System.Logger.Level.ERROR, "failed to close {}", closeable, ioe);
        }
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaIoCloseableUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
