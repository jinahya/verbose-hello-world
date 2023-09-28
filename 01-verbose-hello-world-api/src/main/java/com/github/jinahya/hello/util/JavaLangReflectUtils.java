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

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Utilities for {@link java.lang.reflect} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class JavaLangReflectUtils {

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

    private static final Set<AutoCloseable> CLOSED_CLOSEABLES =
            Collections.newSetFromMap(new WeakHashMap<>());

    @SuppressWarnings({"unchecked"})
    public static <T extends AutoCloseable> T nonIdempotentCloseableProxy(final T closeable) {
        Objects.requireNonNull(closeable, "closeable is null");
        return (T) Proxy.newProxyInstance(
                closeable.getClass().getClassLoader(),
                new Class<?>[] {AutoCloseable.class},
                (proxy, method, args) -> {
                    if (CLOSE.equals(method)) {
                        synchronized (CLOSED_CLOSEABLES) {
                            if (CLOSED_CLOSEABLES.contains(closeable)) {
                                throw new IllegalStateException("already closed: " + closeable);
                            }
                            CLOSED_CLOSEABLES.add(closeable);
                        }
                    }
                    return method.invoke(closeable);
                }
        );
    }

    private JavaLangReflectUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
