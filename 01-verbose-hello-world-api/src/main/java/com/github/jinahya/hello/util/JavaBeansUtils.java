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
import org.apache.commons.lang3.ClassUtils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.net.Inet6Address;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A utility class for {@link java.beans} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public class JavaBeansUtils {

    public static class PropertyValue {

        private static String name(final String name, final Integer index) {
            if (index == null) {
                return name;
            }
            return Objects.requireNonNull(name, "name is null") + '[' + index + ']';
        }

        private static Class<?> type(final Class<?> type, final Object value) {
            if (value == null) {
                return type;
            }
            return Objects.requireNonNull(value, "value is null").getClass();
        }

        public static PropertyValue of(final PropertyDescriptor descriptor, final Integer index,
                                       final Object value) {
            return new PropertyValue(
                    Objects.requireNonNull(descriptor, "descriptor is null"),
                    name(descriptor.getName(), index),
                    type(descriptor.getPropertyType(), value),
                    value);
        }

        public static PropertyValue of(final PropertyDescriptor descriptor, final Object value) {
            return of(descriptor, null, value);
        }

        public static PropertyValue of(final String name, final Integer index, final Class<?> type,
                                       final Object value) {
            return new PropertyValue(null, name(name, index), type(type, value), value);
        }

        public static PropertyValue of(final String name, final Class<?> type, final Object value) {
            return of(name, null, type, value);
        }

        private PropertyValue(final PropertyDescriptor descriptor, final String name,
                              final Class<?> type, final Object value) {
            super();
            this.descriptor = descriptor;
            this.name = Objects.requireNonNull(name, "name is null");
            this.type = Objects.requireNonNull(type, "type is null");
            this.value = value;
        }

        @Override
        public String toString() {
            return "PropertyValue{" +
                   "descriptor=" + descriptor +
                   ", name='" + name + '\'' +
                   ", type=" + type +
                   ", value=" + value +
                   '}';
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (!(object instanceof PropertyValue that)) return false;
            return Objects.equals(descriptor, that.descriptor)
                   && Objects.equals(name, that.name)
                   && Objects.equals(type, that.type)
                   && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(descriptor, name, type, value);
        }

        final PropertyDescriptor descriptor;

        final String name;

        final Class<?> type;

        final Object value;
    }

    public static <R, T> void acceptEachProperty(
            final R parent,
            final Class<? super T> clazz, final T object,
            final Function<? super R,
                    ? extends Function<? super String,
                            ? extends Function<? super PropertyValue,
                                    ? extends R>>> function1,
            final Function<? super R,
                    ? extends Function<? super String,
                            ? extends IntFunction<
                                    ? extends Function<
                                            ? super PropertyValue, ? extends R>>>> function2)
            throws IntrospectionException, ReflectiveOperationException {
        Objects.requireNonNull(parent, "parent is null");
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(object, "object is null");
        Objects.requireNonNull(function1, "function1 is null");
        Objects.requireNonNull(function2, "function2 is null");
        for (var descriptor : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
            if (clazz == Inet6Address.class) {
                log.debug("Inet6Address.d: {}", descriptor);
            }
            var reader = descriptor.getReadMethod();
            if (reader == null || reader.getDeclaringClass() != clazz) {
                continue;
            }
            final var type = descriptor.getPropertyType();
            final var name = descriptor.getName();
            var value = reader.invoke(object);
            final R child = function1.apply(parent).apply(name)
                    .apply(PropertyValue.of(descriptor, value));
            if (value == null) {
                continue;
            }
            if (type.isPrimitive()) {
                continue;
            }
            if (type.isArray() && type.componentType().isPrimitive()) {
                continue;
            }
            if (value instanceof Enumeration<?> e) {
                value = Collections.list(e);
            }
            if (value instanceof Iterable<?> i) {
                value = StreamSupport.stream(i.spliterator(), false);
            }
            if (value instanceof Stream<?> s) {
                var i = 0;
                for (final var iterator = s.iterator(); iterator.hasNext(); ) {
                    final var next = iterator.next();
                    final var propertyValue = PropertyValue.of(descriptor, i, next);
                    final var grandchild = function2.apply(child).apply(name).apply(i)
                            .apply(propertyValue);
                    if (grandchild != null) {
                        acceptEachProperty(grandchild, next, function1, function2);
                    }
                    i++;
                }
                continue;
            }
            if (ClassUtils.isPrimitiveWrapper(type)) {
                continue;
            }
            if (type == String.class) {
                continue;
            }
        }
    }

    private static <R, T> void acceptEachPropertyHelper(
            final R parent, final Class<T> clazz, final Object object,
            final Function<? super R,
                    ? extends Function<? super String,
                            ? extends Function<? super PropertyValue,
                                    ? extends R>>> function1,
            final Function<? super R,
                    ? extends Function<? super String,
                            ? extends IntFunction<
                                    ? extends Function<? super PropertyValue,
                                            ? extends R>>>> function2)
            throws IntrospectionException, ReflectiveOperationException {
        Objects.requireNonNull(clazz, "clazz is null");
        acceptEachProperty(parent, clazz, clazz.cast(object), function1, function2);
    }

    public static <R> void acceptEachProperty(
            final R parent, final Object object,
            final Function<? super R,
                    ? extends Function<? super String,
                            ? extends Function<? super PropertyValue,
                                    ? extends R>>> function1,
            final Function<? super R,
                    ? extends Function<? super String,
                            ? extends IntFunction<
                                    ? extends Function<? super PropertyValue,
                                            ? extends R>>>> function2)
            throws IntrospectionException, ReflectiveOperationException {
        Objects.requireNonNull(object, "object is null");
        acceptEachPropertyHelper(parent, object.getClass(), object, function1, function2);
    }

    private JavaBeansUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
