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

import java.util.*;

/**
 * Helpers for {@link Object java.lang.Object} — currently a compact {@code SimpleName@hexhash}
 * renderer used by the logging utilities elsewhere in this module.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaLangObjectUtils {

    /**
     * Returns a short, log-friendly string for {@code obj} of the form
     * {@code "<SimpleName>@<hex(hashCode)>"} — falling back to the supplied {@code cls}'s simple
     * name when {@code obj.getClass().getSimpleName()} is blank (anonymous classes).
     *
     * @param cls a class whose simple name is used when {@code obj}'s runtime class lacks one; must
     *            not be {@code null}.
     * @param obj the object to render; may be {@code null}.
     * @param <T> the static type captured by {@code cls}.
     * @return a string of the form {@code SimpleName@hexHash}, or {@code "null"} when {@code obj}
     * is {@code null}; never {@code null}.
     * @throws NullPointerException if {@code cls} is {@code null}.
     */
    public static <T> String toSimpleString(final Class<T> cls, final T obj) {
        Objects.requireNonNull(cls, "cls is null");
        if (obj == null) {
            return Objects.toString(obj);
        }
        final var name = Optional.of(obj.getClass().getSimpleName())
                .filter(v -> !v.isBlank())
                .orElseGet(cls::getSimpleName);
        return String.format("%1$s@%2$08x", name, obj.hashCode());
    }

    private static <T> String toSimpleStringHelper(final Class<T> cls, final Object obj) {
        return toSimpleString(Objects.requireNonNull(cls, "cls is null"), cls.cast(obj));
    }

    /**
     * The single-argument convenience of
     * {@link #toSimpleString(Class, Object) toSimpleString(obj.getClass(), obj)}.
     *
     * @param obj the object to render; must not be {@code null} (use
     *            {@link #toSimpleString(Class, Object)} with a {@code null} {@code obj} when
     *            null-safety is required).
     * @return the string {@code SimpleName@hexHash}; never {@code null}.
     * @throws NullPointerException if {@code obj} is {@code null} (dereferenced for
     *                              {@code getClass()}).
     */
    public static String toSimpleString(final Object obj) {
        return toSimpleStringHelper(obj.getClass(), obj);
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaLangObjectUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
