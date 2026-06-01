package com.github.jinahya.hello.api.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Helpers for {@link java.lang java.lang} — currently two families of utilities:
 * <ul>
 *   <li>Daemon-thread {@code System.in}-watcher factories
 *       ({@code readLinesAndCallWhenTests} / {@code …CloseWhenTests} / {@code …RunWhenTests}) that
 *       start a background daemon thread that reads stdin lines until a caller-supplied
 *       {@link Predicate} matches, then performs the bound action (call a
 *       {@link Callable}, close a {@link Closeable}, or run a {@link Runnable}). Useful for
 *       interactive sample apps that want a "press q to quit" affordance.</li>
 *   <li>{@link #trimByCodepoints(String, Charset, int)} — a binary-search trim that returns the
 *       longest prefix of a string whose encoded byte length under a given {@link Charset} does
 *       not exceed a budget. Codepoint-aware, so it never truncates inside a surrogate pair.</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaLangUtils {

    private static final System.Logger log = System.getLogger(JavaLangUtils.class.getName());

    static final Map<Class<?>, Class<?>> WRAPPER_CLASSES = Map.of(
            boolean.class, Boolean.class,
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            char.class, Character.class,
            float.class, Float.class,
            double.class, Double.class,
            void.class, Void.class
    );

    static boolean isPrimitive(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        return WRAPPER_CLASSES.containsKey(clazz);
    }

    static Class<?> getWrapperType(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        if (!isPrimitive(clazz)) {
            throw new IllegalArgumentException("not a primitive type: " + clazz);
        }
        return WRAPPER_CLASSES.get(clazz);
    }

    static final Map<Class<?>, Class<?>> PRIMITIVE_CLASSES =
            WRAPPER_CLASSES.entrySet()
                    .stream()
                    .collect(Collectors.toUnmodifiableMap(Map.Entry::getValue, Map.Entry::getKey));

    static boolean isWrapper(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        return PRIMITIVE_CLASSES.containsKey(clazz);
    }

    /**
     * Starts a new {@link Thread#isDaemon() daemon} thread which continuously reads lines from
     * {@link System#in}, calls specified callable when a line tests with the specified predicate,
     * otherwise accepts each line to, if a non-null specified,  specified consumer.
     *
     * @param predicate the predicate to test.
     * @param callable  the callable to be called when a line passes {@code predicate}.
     * @param consumer  the consumer be accepted with lines not pass; may be {@code null}.
     */
    public static void readLinesAndCallWhenTests(final Predicate<? super String> predicate,
                                                 final Callable<Void> callable,
                                                 final Consumer<? super String> consumer) {
        Objects.requireNonNull(predicate, "predicate is null");
        Objects.requireNonNull(callable, "callable is null");
        Thread.ofPlatform().daemon().start(() -> {
            final var reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                for (String line; (line = reader.readLine()) != null; ) {
                    if (predicate.test(line.strip())) {
                        break;
                    }
                    if (consumer != null) {
                        consumer.accept(line);
                    }
                }
            } catch (IOException ioe) {
                log.log(System.Logger.Level.ERROR, "failed to read line", ioe);
            }
            try {
                callable.call();
            } catch (final InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (final Exception e) {
                log.log(System.Logger.Level.ERROR, () -> "failed to call " + callable, e);
            }
        });
    }

    /**
     * Starts a new {@link Thread#isDaemon() daemon} thread which continuously reads lines from
     * {@link System#in}, calls specified callable when a line tests with the specified predicate.
     *
     * @param predicate the predicate to test.
     * @param callable  the callable to be called when a line passes the {@code predicate}.
     */
    public static void readLinesAndCallWhenTests(final Predicate<? super String> predicate,
                                                 final Callable<Void> callable) {
        readLinesAndCallWhenTests(predicate, callable, null);
    }

    /**
     * Starts a new {@link Thread#isDaemon() daemon} thread which continuously reads lines from
     * {@link System#in}, {@link Closeable#close() closes} specified closeable when a line tests
     * with the specified predicate, otherwise accepts each line to, if a non-null specified,
     * specified consumer.
     *
     * @param predicate the predicate to test.
     * @param closeable the closeable to be closed when a line passes {@code predicate}.
     * @param consumer  the consumer be accepted with lines not pass; may be {@code null}.
     */
    public static void readLinesAndCloseWhenTests(final Predicate<? super String> predicate,
                                                  final Closeable closeable,
                                                  final Consumer<? super String> consumer) {
        Objects.requireNonNull(closeable, "closeable is null");
        readLinesAndCallWhenTests(
                predicate,
                () -> {
                    closeable.close();
                    return null;
                },
                consumer
        );
    }

    /**
     * Starts a new {@link Thread#isDaemon() daemon} thread which continuously reads lines from
     * {@link System#in}, {@link Closeable#close() closes} specified closeable when a line tests
     * with the specified predicate.
     *
     * @param predicate the predicate to test.
     * @param closeable the closeable to be closed when a line passes {@code predicate}.
     */
    public static void readLinesAndCloseWhenTests(final Predicate<? super String> predicate,
                                                  final Closeable closeable) {
        readLinesAndCloseWhenTests(
                predicate,
                closeable,
                l -> {
                    // does nothing
                }
        );
    }

    /**
     * Starts a new {@link Thread#isDaemon() daemon} thread which continuously reads lines from
     * {@link System#in}, {@link Runnable#run() runs} specified runnable when a line tests with
     * specified predicate, otherwise accepts each line to, if a non-null specified,  specified
     * consumer.
     *
     * @param predicate the predicate to test.
     * @param runnable  the runnable to run when a line passes {@code predicate}.
     * @param consumer  the consumer be accepted with lines not pass; may be {@code null}.
     */
    public static void readLinesAndRunWhenTests(final Predicate<? super String> predicate,
                                                final Runnable runnable,
                                                final Consumer<? super String> consumer) {
        Objects.requireNonNull(runnable, "runnable is null");
        readLinesAndCallWhenTests(
                predicate,
                () -> {
                    runnable.run();
                    return null;
                },
                consumer
        );
    }

    /**
     * Starts a new {@link Thread#isDaemon() daemon} thread which continuously reads lines from
     * {@link System#in}, {@link Runnable#run() runs} specified runnable when a line tests with
     * specified predicate.
     *
     * @param predicate the predicate to test.
     * @param runnable  the runnable to run when a line passes {@code predicate}.
     */
    public static void readLinesAndRunWhenTests(final Predicate<? super String> predicate,
                                                final Runnable runnable) {
        readLinesAndRunWhenTests(
                predicate,
                runnable,
                l -> {
                    // does nothing
                }
        );
    }

    private static int[] trimByCodepoints(final int[] codePoints, int from, int to,
                                          final Charset charset, final int bytes) {
        if (from == to) {
            return Arrays.copyOfRange(codePoints, 0, to);
        }
        var length = new String(codePoints, 0, to).getBytes(charset).length;
        if (length == bytes) {
            return Arrays.copyOfRange(codePoints, 0, to);
        }
        if (length > bytes) {
            if ((to = from + ((to - from) >> 1)) == from) {
                return Arrays.copyOfRange(codePoints, 0, from);
            }
            return trimByCodepoints(codePoints, from, to, charset, bytes);
        }
        if ((to = (from = to) + ((codePoints.length - to) >> 1)) == from) {
            return Arrays.copyOfRange(codePoints, 0, from);
        }
        return trimByCodepoints(codePoints, from, to, charset, bytes);
    }

    /**
     * Returns the longest codepoint-aligned prefix of {@code string} whose encoded length under
     * {@code charset} is at most {@code bytes}. The result never splits a surrogate pair, since
     * trimming is performed at codepoint boundaries.
     *
     * @param string  the string to trim; must not be {@code null}.
     * @param charset the charset under which the byte budget is measured; must not be
     *                {@code null}.
     * @param bytes   the maximum allowed byte length of the encoded result; must be positive.
     * @return a trimmed string whose {@code charset}-encoded byte length does not exceed
     * {@code bytes}; never {@code null}.
     * @throws NullPointerException     if {@code string} or {@code charset} is {@code null}.
     * @throws IllegalArgumentException if {@code bytes} is not positive.
     */
    public static String trimByCodepoints(final String string, final Charset charset,
                                          final int bytes) {
        Objects.requireNonNull(string, "string is null");
        Objects.requireNonNull(charset, "charset is null");
        if (bytes <= 0) {
            throw new IllegalArgumentException("bytes(" + bytes + ") is not positive");
        }
        var codePoints = string.codePoints().toArray();
        codePoints = trimByCodepoints(codePoints, 0, codePoints.length, charset, bytes);
        return new String(codePoints, 0, codePoints.length);
    }

    // ---------------------------------------------------------------------------------------------
    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaLangUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
