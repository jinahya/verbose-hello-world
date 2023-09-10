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
import java.lang.reflect.Modifier;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utilities for {@link java.net} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class HelloWorldNetUtils {

    static final String VALUE_OF_UNSUPPORTED_SOCKET_OPTION = "NOT SUPPORTED";

    public static void acceptEachStandardSocketOption(
            final Consumer<? super SocketOption<?>> consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        for (final var field : StandardSocketOptions.class.getFields()) {
            final var modifiers = field.getModifiers();
            assert Modifier.isPublic(modifiers);
            if (!Modifier.isStatic(modifiers)) {
                continue;
            }
            final var type = field.getType();
            if (!SocketOption.class.isAssignableFrom(type)) {
                continue;
            }
            SocketOption<?> value;
            try {
                value = (SocketOption<?>) field.get(null);
            } catch (final IllegalAccessException iae) {
                iae.printStackTrace();
                continue;
            }
            consumer.accept(value);
        }
    }

    public static <T> void acceptSocketOptions(
            final Class<T> clazz, T object,
            final Function<? super SocketOption<?>, ? extends Function<? super Class<?>, Consumer<Object>>> function) {
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(object, "object is null");
        Objects.requireNonNull(function, "function is null");
        Method method;
        try {
            method = clazz.getMethod("getOption", SocketOption.class);
        } catch (final NoSuchMethodException nsme) {
            log.error("no getOption(" + SocketOption.class.getSimpleName() + ") found on " + clazz);
            return;
        }
        if (!method.canAccess(object)) {
            method.setAccessible(true);
        }
        acceptEachStandardSocketOption(so -> {
            var type = so.type();
            Object value;
            try {
                value = method.invoke(object, so);
            } catch (final ReflectiveOperationException roe) {
                value = Optional.ofNullable(roe.getCause())
                        .map(Throwable::getMessage)
                        .map(m -> {
                            if (m.equals("'" + so.name() + "' not supported")) {
                                return VALUE_OF_UNSUPPORTED_SOCKET_OPTION;
                            }
                            return m;
                        })
                        .orElse(null);
            }
            function.apply(so).apply(type).accept(value);
        });
    }

    public static <T> void acceptSocketOptionsHelper(
            final Class<T> clazz, Object object,
            final Function<? super SocketOption<?>, ? extends Function<? super Class<?>, Consumer<Object>>> function) {
        acceptSocketOptions(clazz, clazz.cast(object), function);
    }

    /**
     * Prints socket options of specified object.
     *
     * @param clazz  a class of the object.
     * @param object the object whose socket options are printed.
     * @param <T>    object type parameter
     * @see Socket#getOption(SocketOption)
     * @see ServerSocket#getOption(SocketOption)
     * @see DatagramSocket#getOption(SocketOption)
     * @see SocketChannel#getOption(SocketOption)
     * @see ServerSocketChannel#getOption(SocketOption)
     * @see DatagramChannel#getOption(SocketOption)
     * @see AsynchronousSocketChannel#getOption(SocketOption)
     * @see AsynchronousServerSocketChannel#getOption(SocketOption)
     */
    public static <T> void printSocketOptions(final Class<T> clazz, T object) {
        acceptSocketOptions(clazz, object, o -> t -> v -> {
            System.out.printf("%1$-17s\t%2$-17s %3$s%n", o, t.getSimpleName(), v);
        });
        if (true) {
            return;
        }
        Objects.requireNonNull(object, "object is null");
        Method method;
        try {
            method = clazz.getMethod("getOption", SocketOption.class);
        } catch (final NoSuchMethodException nsme) {
            log.error("no getOption(" + SocketOption.class.getSimpleName() + ") found on " + clazz);
            return;
        }
        if (!method.canAccess(object)) {
            method.setAccessible(true);
        }
        acceptEachStandardSocketOption(so -> {
            var type = so.type();
            Object value;
            try {
                value = method.invoke(object, so);
            } catch (ReflectiveOperationException roe) {
                value = Optional.ofNullable(roe.getCause())
                        .map(Throwable::getMessage)
                        .orElse(null);
            }
            System.out.printf("%1$-17s\t%2$-17s %3$s%n", so, type.getSimpleName(), value);
        });
    }

    private HelloWorldNetUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
