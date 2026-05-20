package com.github.jinahya.hello.lib.util;

/*-
 * #%L
 * verbose-hello-world-lib
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

import java.util.Objects;
import java.util.Optional;

/**
 * Utilities for {@link Object}, primarily for rendering an instance as a stable
 * {@code <simple-name>@<identity-hex>} string suitable for log lines.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public final class JavaLangObjectUtils {

    /**
     * Returns a string of the form {@code <simple-name>@<identity-hex>} for the given object, or
     * {@code "null"} when {@code obj} is {@code null}. The simple-name component is taken from the
     * runtime class of {@code obj} when available, falling back to {@code cls.getSimpleName()} for
     * anonymous classes whose own simple name is empty.
     *
     * @param cls the reference type used both for the fallback simple name and for null-safety;
     *            never {@code null}
     * @param obj the object to render; may be {@code null}
     * @param <T> the reference type
     * @return a stable per-instance label
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
     * Renders {@code obj} via {@link #toSimpleString(Class, Object)} using its own runtime class as
     * the reference type.
     *
     * @param obj the object to render; must not be {@code null}
     * @return {@code toSimpleString(obj.getClass(), obj)}
     */
    public static String toSimpleString(final Object obj) {
        return toSimpleStringHelper(obj.getClass(), obj);
    }

    private JavaLangObjectUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
