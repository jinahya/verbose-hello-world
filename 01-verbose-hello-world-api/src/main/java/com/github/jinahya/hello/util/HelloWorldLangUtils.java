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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

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
     * {@link System#in}, and calls specified callable when it reads one equals to specified
     * string.
     *
     * @param callable the object to {@link Callable#call() call}.
     * @param string   the string to match.
     * @param consumer a consumer be accepted with read lines other than {@code string}; may be
     *                 {@code null}.
     */
    public static void callWhenRead(Callable<Void> callable, String string,
                                    Consumer<? super String> consumer) {
        Objects.requireNonNull(callable, "callable is null");
        Objects.requireNonNull(string, "string is null");
        var thread = new Thread(() -> {
            var r = new BufferedReader(new InputStreamReader(System.in));
            try {
                for (String line; (line = r.readLine()) != null; ) {
                    if (line.strip().equalsIgnoreCase(string)) {
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
            } catch (Exception e) {
                log.error("failed to call {}", callable, e);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Starts a new {@link Thread#isDaemon() daemon} thread which continuously reads lines from
     * {@link System#in}, and runs specified runnable when it reads one equals to specified string.
     *
     * @param runnable the object to {@link Runnable#run() run}.
     * @param string   the string to match.
     * @param consumer a consumer be accepted with read lines other than {@code string}; may be
     *                 {@code null}.
     */
    public static void runWhenRead(Runnable runnable, String string,
                                   Consumer<? super String> consumer) {
        Objects.requireNonNull(runnable, "runnable is null");
        callWhenRead(
                () -> {
                    runnable.run();
                    return null;
                },
                string,
                consumer
        );
    }

    /**
     * Starts a new {@link Thread#isDaemon() daemon} thread which continuously reads lines from
     * {@link System#in}, and closes specified closeable when it reads one equals to specified
     * string.
     *
     * @param closeable the object to {@link Closeable#close() close}.
     * @param string    the string to match.
     * @param consumer  a consumer be accepted with read lines other than {@code string}; may be
     *                  {@code null}.
     */
    public static void closeWhenRead(Closeable closeable, String string,
                                     Consumer<? super String> consumer) {
        Objects.requireNonNull(closeable, "closeable is null");
        callWhenRead(
                () -> {
                    closeable.close();
                    return null;
                },
                string,
                consumer
        );
    }

    public static Thread readQuitAndClose(String quit, Closeable closeable,
                                          Consumer<? super String> consumer) {
        Objects.requireNonNull(quit, "quit is null");
        Objects.requireNonNull(closeable, "closeable is null");
        Objects.requireNonNull(consumer, "consumer is null");
        PipedInputStream pis = new PipedInputStream();
        PipedOutputStream pos;
        try {
            pos = new PipedOutputStream(pis);
        } catch (IOException ioe) {
            throw new RuntimeException("failed to create pos with " + pis, ioe);
        }
        InputStream systemIn = System.in;
        var piper = new Thread(() -> {
            while (true) {
                try {
                    pos.write(systemIn.read());
                    pos.flush();
                } catch (IOException ioe) {
                    log.error("failed to read from " + systemIn, ioe);
                    break;
                }
            }
        });
        piper.setDaemon(true);
        piper.start();
        var latch = new CountDownLatch(1);
        var closer = new Thread(() -> {
            System.setIn(pis);
            try {
                latch.countDown();
                try (var reader = new BufferedReader(new InputStreamReader(pis))) {
                    for (String line; (line = reader.readLine()) != null; ) {
                        if (line.strip().equalsIgnoreCase(quit)) {
                            break;
                        }
                        consumer.accept(line);
                    }
                } catch (InterruptedIOException iioe) {
                    Thread.currentThread().interrupt();
                } catch (IOException ioe) {
                    throw new UncheckedIOException(ioe);
                }
            } finally {
                System.setIn(systemIn);
                try {
                    closeable.close();
                } catch (IOException ioe) {
                    log.error("failed to close " + closeable, ioe);
                }
            }
        });
        closer.start();
        try {
            latch.await();
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("interrupted while awaiting latch", ie);
        }
        return closer;
    }

    public static byte[] trim(String string, Charset charset, int length) {
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
