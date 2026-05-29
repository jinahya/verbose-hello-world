package com.github.jinahya.hello.api.util;

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

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

/**
 * Helpers for {@link java.lang.reflect java.lang.reflect} — currently a single multi-interface
 * {@link Proxy}-based wrapper that logs each interface-declared call before delegating.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaLangReflectUtils {

    private static final System.Logger log = System.getLogger(JavaLangReflectUtils.class.getName());

    /**
     * Wraps {@code obj} in a JDK {@link Proxy} implementing every interface in
     * {@code interfaceClasses}; on each interface-declared method invocation logs the call at
     * {@code DEBUG} ({@code <obj>.<method>(<args>)}) and then reflectively forwards to
     * {@code obj}. Methods inherited from {@link Object} pass through without a log line.
     *
     * @param interfaceClasses the interfaces the returned proxy must implement; must not be
     *                         {@code null}.
     * @param obj              the underlying instance every call is forwarded to; must not be
     *                         {@code null} and must implement every class in
     *                         {@code interfaceClasses}.
     * @return a proxy object implementing every interface in {@code interfaceClasses}; never
     * {@code null}.
     * @throws NullPointerException if either argument is {@code null}.
     */
    public static Object loggingProxy(final Set<Class<?>> interfaceClasses, final Object obj) {
        return Proxy.newProxyInstance(
                obj.getClass().getClassLoader(),
                interfaceClasses.toArray(new Class<?>[0]),
                (p, m, a) -> {
                    if (interfaceClasses.contains(m.getDeclaringClass())) {
                        final var s = Optional.ofNullable(a)
                                .filter(v -> v.length > 0)
                                .map(v -> Arrays.stream(v).map(Object::toString)
                                        .collect(Collectors.joining(", ")))
                                .orElse("");
                        log.log(System.Logger.Level.DEBUG, "{0}.{1}({2})", obj, m.getName(), s);
                    }
                    return m.invoke(obj, a);
                }
        );
    }

    private JavaLangReflectUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
