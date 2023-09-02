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

import java.lang.reflect.Modifier;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Utilities for {@link java.net} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
public final class HelloWorldNetUtils {

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

    public static void printSocketOptions(Object socket) throws ReflectiveOperationException {
        Objects.requireNonNull(socket, "socket is null");
        var method = socket.getClass().getMethod("getOption", SocketOption.class);
        acceptEachStandardSocketOption(so -> {
            var name = so.name();
            var type = so.type();
            Object value;
            try {
                value = method.invoke(socket, so);
            } catch (ReflectiveOperationException roe) {
                value = "[ERROR] " +
                        Optional.ofNullable(roe.getCause()).map(Throwable::getMessage).orElse(null);
            }
            System.out.printf("%1$s%n", so);
            System.out.printf("\ttype:\t%1$s%n", type);
            System.out.printf("\tvalue:\t%1$s%n", value);
        });
    }

    private HelloWorldNetUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
