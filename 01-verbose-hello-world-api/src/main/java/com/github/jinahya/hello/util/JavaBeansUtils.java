package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-srv-common
 * %%
 * Copyright (C) 2018 - 2022 Jinahya, Inc.
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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A utility class for {@link java.beans} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class JavaBeansUtils {

    public static class PropertyInfo {

        static PropertyInfo of(final PropertyInfo parent, final PropertyDescriptor descriptor,
                               final Integer index, final Object value) {
            Objects.requireNonNull(descriptor, "descriptor is null");
            return new PropertyInfo(
                    parent, descriptor.getName(),
                    descriptor.getPropertyType(),
                    index,
                    value
            );
        }

        PropertyInfo(final PropertyInfo parent, final String name, final Class<?> type,
                     final Integer index, final Object value) {
            super();
            this.parent = parent;
            this.name = Objects.requireNonNull(name, "name is null");
            this.type = type;
            this.index = index;
            this.value = value;
        }

        @Override
        public String toString() {
            return super.toString() + '{' +
                   "parent=" + parent +
                   ",name=" + name +
                   ",type=" + type +
                   ",index=" + index +
                   ",value=" + value +
                   '}';
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof PropertyInfo that)) {
                return false;
            }
            return Objects.equals(parent, that.parent)
                   && Objects.equals(name, that.name)
                   && Objects.equals(type, that.type)
                   && Objects.equals(index, that.index)
                   && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(
                    parent,
                    name,
                    type,
                    index,
                    value
            );
        }

        public final int getDepth() {
            return Optional.ofNullable(parent)
                    .stream()
                    .mapToInt(PropertyInfo::getDepth)
                    .map(v -> v + 1)
                    .findAny()
                    .orElse(0);
        }

        private boolean isCyclic() {
            for (var p = parent; p != null; p = p.parent) {
                if (p.value == value) {
                    return true;
                }
            }
            return false;
        }

        private final PropertyInfo parent;

        final String name;

        final Class<?> type;

        final Integer index;

        final Object value;
    }

    public interface PropertyInfoHolder extends Supplier<PropertyInfo> {

        static PropertyInfoHolder of(final PropertyInfo value) {
            Objects.requireNonNull(value, "value is null");
            return () -> value;
        }
    }

    private static <T> boolean check(final Class<T> type, final T value) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(value, "value is null");
        if (type.isPrimitive()
            || JavaLangUtils.isWrapper(type)
            || (type.isArray() && type.componentType().isPrimitive())
            || type == String.class) {
            return false;
        }
        return true;
    }

    private static <T> boolean checkHelper(final Class<T> type, final Object value) {
        Objects.requireNonNull(type, "type is null");
        if (JavaLangUtils.isPrimitive(type)) {
            return checkHelper(JavaLangUtils.getWrapperType(type), value);
        }
        return check(type, type.cast(value));
    }

    private static boolean check(final Object value) {
        if (value == null) {
            return false;
        }
        return checkHelper(value.getClass(), value);
    }

    private static <H extends PropertyInfoHolder, T> void acceptEachProperty(
            final H parent,
            final Class<? super T> clazz, final T object,
            final Function<? super H,
                    ? extends Function<? super PropertyInfo,
                            ? extends H>> function)
            throws IntrospectionException, ReflectiveOperationException {
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(object, "object is null");
        Objects.requireNonNull(function, "function is null");
        for (var descriptor : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
            final var reader = descriptor.getReadMethod();
            if (reader == null || reader.getDeclaringClass() != clazz) {
                continue;
            }
            final var type = descriptor.getPropertyType();
            final var name = descriptor.getName();
            var value = reader.invoke(object);
            final var childInfo = new PropertyInfo(
                    Optional.ofNullable(parent).map(PropertyInfoHolder::get).orElse(null),
                    name,
                    type,
                    null,
                    value
            );
            final var childHolder = function.apply(parent).apply(childInfo);
            if (value == null || childHolder == null || !checkHelper(type, value)) {
                continue;
            }
            if (value instanceof Enumeration<?> e) {
                value = Collections.list(e);
            }
            if (value instanceof Stream<?> s) {
                value = s.toList();
            }
            if (value instanceof Iterable<?> i) {
                var j = 0;
                for (final Object v : i) {
                    final var grandchildInfo = new PropertyInfo(
                            childInfo,
                            name,
                            Optional.ofNullable(v).map(Object::getClass).orElse(null),
                            j++,
                            value
                    );
                    final var grandchildHolder = function.apply(childHolder).apply(grandchildInfo);
                    if (v == null || grandchildHolder == null || !check(value)) {
                        continue;
                    }
                    acceptEachProperty(grandchildHolder, v, function);
                }
                continue;
            }
            if (childInfo.isCyclic()) {
                continue;
            }
            acceptEachProperty(childHolder, value, function);
        }
    }

    private static <H extends PropertyInfoHolder, T> void acceptEachPropertyHelper(
            final H parent, final Class<T> clazz, final Object object,
            final Function<? super H,
                    ? extends Function<? super PropertyInfo,
                            ? extends H>> function)
            throws IntrospectionException, ReflectiveOperationException {
        Objects.requireNonNull(clazz, "clazz is null");
        acceptEachProperty(parent, clazz, clazz.cast(object), function);
    }

    public static <H extends PropertyInfoHolder> void acceptEachProperty(
            final H parent, final Object bean,
            final Function<? super H,
                    ? extends Function<? super PropertyInfo,
                            ? extends H>> function)
            throws IntrospectionException, ReflectiveOperationException {
        Objects.requireNonNull(bean, "bean is null");
        acceptEachPropertyHelper(parent, bean.getClass(), bean, function);
    }

    private JavaBeansUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
