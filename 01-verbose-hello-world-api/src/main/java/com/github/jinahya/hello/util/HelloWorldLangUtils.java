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
import java.io.UncheckedIOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

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
     * {@link System#in}, and (when it reads {@code "quit!"}) closes specified closeable.
     *
     * @param closeable the object to close.
     */
    public static void readQuitAndClose(Closeable closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        var thread = new Thread(() -> {
            var r = new BufferedReader(new InputStreamReader(System.in));
            try {
                for (String l; (l = r.readLine()) != null; ) {
                    if (l.strip().equalsIgnoreCase("quit!")) {
                        break;
                    }
                }
                closeable.close();
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private HelloWorldLangUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
