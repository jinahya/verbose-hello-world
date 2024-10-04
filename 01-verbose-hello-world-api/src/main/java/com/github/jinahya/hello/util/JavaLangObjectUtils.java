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

import java.util.Objects;
import java.util.Optional;

@Slf4j
public final class JavaLangObjectUtils {

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

    public static String toSimpleString(final Object obj) {
        return toSimpleStringHelper(obj.getClass(), obj);
    }

    @_ExcludeFromCoverage_PrivateConstructor_Obviously
    private JavaLangObjectUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
