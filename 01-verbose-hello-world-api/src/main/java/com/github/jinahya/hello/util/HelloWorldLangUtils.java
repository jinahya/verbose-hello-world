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
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
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
    public static <T extends AutoCloseable> T uncloseableProxy(final T closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        return (T) Proxy.newProxyInstance(
                closeable.getClass().getClassLoader(),
                new Class<?>[] {AutoCloseable.class},
                (proxy, method, args) -> {
                    if (CLOSE.equals(method)) {
                        throw new UnsupportedOperationException("unable to close");
                    }
                    return method.invoke(closeable);
                }
        );
    }

    /**
     * Starts a new {@link Thread#isDaemon() daemon} thread which continuously reads lines from
     * {@link System#in} while specified predicate tests, calls specified callable when it reads a
     * line equals (ignoring the case) to specified string, otherwise, accepts lines to specified
     * consumer.
     *
     * @param predicate the predicate to be tested while reading lines.
     * @param string    the string to match.
     * @param callable  the callable to be called when reads {@code string}.
     * @param consumer  the consumer be accepted with read lines other than {@code string}
     */
    public static void callWhenRead(final Predicate<Void> predicate, String string,
                                    Callable<Void> callable, Consumer<? super String> consumer) {
        Objects.requireNonNull(predicate, "predicate is null");
        Objects.requireNonNull(string, "string is null");
        Objects.requireNonNull(callable, "callable is null");
        Objects.requireNonNull(consumer, "consumer is null");
        var thread = new Thread(() -> {
            var r = new BufferedReader(new InputStreamReader(System.in));
            try {
                for (String l; ((l = r.readLine()) != null) && predicate.test(null); ) {
                    if (l.strip().equalsIgnoreCase(string)) {
                        try {
                            callable.call();
                        } catch (InterruptedException ie) {
                            log.info("interrupted while calling {}", callable, ie);
                            Thread.currentThread().interrupt();
                        } catch (Exception e) {
                            log.error("failed to call {}", callable, e);
                        }
                        break;
                    }
                    consumer.accept(l);
                }
            } catch (IOException ioe) {
                log.error("failed to read line", ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public static byte[] trim(final String string, final Charset charset, final int length) {
        Objects.requireNonNull(string, "string is null");
        Objects.requireNonNull(charset, "charset is null");
        if (length < 0) {
            throw new IllegalArgumentException("length(" + length + ") is not positive");
        }
        var bytes = string.getBytes(charset);
        if (bytes.length <= length) {
            return bytes;
        }
        var codePoints = string.codePoints().toArray();
        return trim(new String(codePoints, 0, codePoints.length - 1), charset, length);
    }

    private HelloWorldLangUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
