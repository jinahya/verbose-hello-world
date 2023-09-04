package com.github.jinahya.hello.util;

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

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.TemporalAmount;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.LongFunction;
import java.util.function.Predicate;

/**
 * Utilities for {@link java.lang} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class HelloWorldLangUtils {

    private static final Method CLOSE;

    static {
        try {
            CLOSE = AutoCloseable.class.getMethod("close");
        } catch (NoSuchMethodException nsme) {
            throw new InstantiationError(nsme.toString());
        }
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends AutoCloseable> T uncloseableProxy(
            final T closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        return (T) Proxy.newProxyInstance(
                closeable.getClass().getClassLoader(),
                new Class<?>[] {AutoCloseable.class},
                (proxy, method, args) -> {
                    if (CLOSE.equals(method)) {
                        throw new UnsupportedOperationException(
                                "unable to close");
                    }
                    return method.invoke(closeable);
                }
        );
    }

    /**
     * Starts a new {@link Thread#isDaemon() daemon} thread which continuously reads lines from
     * {@link System#in}, calls specified callable when a line tests with specified predicate,
     * otherwise, accepts each line to specified consumer.
     *
     * @param predicate the predicate to test.
     * @param callable  the callable to be called when a line passes {@code predicate}.
     * @param consumer  the consumer be accepted with lines not pass.
     */
    public static void readLinesAndCallWhenTests(final Predicate<? super String> predicate,
                                                 final Callable<Void> callable,
                                                 final Consumer<? super String> consumer) {
        Objects.requireNonNull(predicate, "predicate is null");
        Objects.requireNonNull(callable, "callable is null");
        Objects.requireNonNull(consumer, "consumer is null");
        var thread = new Thread(() -> {
            var reader = new BufferedReader(new InputStreamReader(System.in));
            try {
                for (String line; (line = reader.readLine()) != null; ) {
                    if (predicate.test(line.strip())) {
                        break;
                    }
                    consumer.accept(line);
                }
            } catch (IOException ioe) {
                log.error("failed to read line", ioe);
            }
            try {
                callable.call();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("failed to call {}", callable, e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

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

    // @formatter:off
    private interface Stopwatch<C> {
        C start();
        <T extends TemporalAmount> T stop(C carrier, LongFunction<? extends T> mapper);
    }
    // @formatter:on

    // @formatter:off
    private enum Stopwatch1 implements Stopwatch<Void> {
        INSTANCE() {
            @Override
            public Void start() {
                START_NANOS1.set(System.nanoTime());
                return null;
            }
            @Override
            public <T extends TemporalAmount> T stop(Void carrier,
                                                     LongFunction<? extends T> mapper) {
                try {
                    return mapper.apply(System.nanoTime() - START_NANOS1.get());
                } finally {
                    START_NANOS1.remove();
                }
            }
        };
        private static final ThreadLocal<Long> START_NANOS1 =
                ThreadLocal.withInitial(System::nanoTime);
    }
    // @formatter:on

    static void startStopWatch1() {
        Stopwatch1.INSTANCE.start();
    }

    static Duration stopStopWatch1() {
        return Stopwatch1.INSTANCE.stop(null, Duration::ofNanos);
    }

    //    private static final ScopedValue<Long> START_NANOS = ScopedValue.newInstance();
//
//    public static ScopedValue.Carrier<Long> startStopwatch() {
//        return ScopedValue.where(START_NANOS, System.nanoTime());
//    }
//
//    public static Duration stopStopwatch(ScopedValue.Carrier<Long> carrier) {
//        Objects.requireNonNull(carrier, "carrier is null");
//        return carrier.call(v -> Duration.ofNanos(System.nanoTime() - v));
//    }

    /**
     * Starts a stopwatch bound to current thread.
     */
    public static void startStopWatch() {
        startStopWatch1();
    }

    /**
     * Returns a duration elapsed since {@link #startStopWatch()} method invoked.
     */
    public static Duration stopStopWatch() {
        return stopStopWatch1();
    }

    private HelloWorldLangUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
