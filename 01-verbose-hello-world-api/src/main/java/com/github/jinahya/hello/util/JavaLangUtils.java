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
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Utilities for {@link java.lang} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class JavaLangUtils {

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

    public static Set<Class<?>> getAllPrimitiveTypes() {
        return WRAPPER_CLASSES.keySet();
    }

    public static boolean isPrimitive(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        return WRAPPER_CLASSES.containsKey(clazz);
    }

    public static Class<?> getWrapperType(final Class<?> clazz) {
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

    public static Set<Class<?>> getAllWrapperTypes() {
        return PRIMITIVE_CLASSES.keySet();
    }

    public static boolean isWrapper(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz is null");
        return PRIMITIVE_CLASSES.containsKey(clazz);
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
        var thread = new Thread(() -> {
            var reader = new BufferedReader(new InputStreamReader(System.in));
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

    public static final class Ints {

        public static int requireGreaterThanOrEqualTo(final int value, final int against) {
            if (value < against) {
                throw new IllegalArgumentException("value(" + value + ") < " + against);
            }
            return value;
        }

        public static int requireGreaterThan(final int value, final int against) {
            if (requireGreaterThanOrEqualTo(value, against) == against) {
                throw new IllegalArgumentException("value(" + value + ") == " + against);
            }
            return value;
        }

        public static int requireLessThanOrEqualTo(final int value, final int against) {
            if (value > against) {
                throw new IllegalArgumentException("value(" + value + ") > " + against);
            }
            return value;
        }

        public static int requireLessThan(final int value, final int against) {
            if (requireLessThanOrEqualTo(value, against) == against) {
                throw new IllegalArgumentException("value(" + value + ") == " + against);
            }
            return value;
        }

        public static int requireNonPositive(final int value) {
            if (value > 0) {
                throw new IllegalArgumentException("value(" + value + ") > 0");
            }
            return value;
        }

        public static int requireNonNegative(final int value, final String message) {
            if (value < 0) {
                throw new IllegalArgumentException(message);
            }
            return value;
        }

        public static int requireNonNegative(final int value) {
            return requireNonNegative(value, "value(" + value + ") < 0");
        }

        public static int requireNegative(final int value) {
            if (requireNonPositive(value) == 0) {
                throw new IllegalArgumentException("value(" + value + ") == 0");
            }
            return value;
        }

        public static int requirePositive(final int value) {
            if (requireNonNegative(value) == 0) {
                throw new IllegalArgumentException("value(" + value + ") == 0");
            }
            return value;
        }

        private Ints() {
            throw new IllegalArgumentException("");
        }
    }

    private JavaLangUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
