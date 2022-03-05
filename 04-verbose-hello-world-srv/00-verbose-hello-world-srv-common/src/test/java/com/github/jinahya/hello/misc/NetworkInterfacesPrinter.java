package com.github.jinahya.hello.misc;

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

import java.lang.reflect.Modifier;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A class for testing {@link NetworkInterface}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.baeldung.com/java-network-interfaces">Working with
 * Network Interfaces in Java (Bealdung)</a>
 */
@Slf4j
class NetworkInterfacesPrinter {

    private static String hexadecimal(byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes is null");
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> String.format("%02x", bytes[i]))
                .collect(Collectors.joining());
    }

    private static <T> void print(Object indent, Class<T> clazz, T object) {
        Objects.requireNonNull(indent, "indent is null");
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(object, "object is null");
        for (var method : clazz.getMethods()) {
            if (method.getDeclaringClass() != clazz) {
                continue;
            }
            if (method.isSynthetic()) {
                continue;
            }
            var modifier = method.getModifiers();
            if (Modifier.isStatic(modifier)) {
                continue;
            }
            if (method.getParameterTypes().length > 0) {
                continue;
            }
            Object value;
            try {
                value = method.invoke(object);
            } catch (ReflectiveOperationException roe) {
                throw new RuntimeException(roe);
            }
            if (value instanceof Enumeration) {
                int i = 0;
                for (var e = (Enumeration<?>) value; e.hasMoreElements(); ) {
                    var n = e.nextElement();
                    System.out.printf("%1$s%2$s[%3$d]: %4$s%n", indent, method.getName(), i++, n);
                    print(indent + "\t", n);
                }
                continue;
            }
            if (value instanceof Collection) {
                int i = 0;
                for (var j = ((Collection<?>) value).iterator(); j.hasNext(); ) {
                    var n = j.next();
                    System.out.printf("%1$s%2$s[%3$d]: %4$s%n", indent, method.getName(), i++, n);
                    print(indent + "\t", n);
                }
                continue;
            }
            if (value instanceof Stream) {
                var i = new AtomicInteger();
                ((Stream<?>) value).forEach(e -> {
                    System.out.printf("%1$s%2$s[%3$d]: %4$s%n", indent, method.getName(),
                                      i.getAndIncrement(), e);
                    print(indent + "\t", e);
                });
                continue;
            }
            if (value instanceof byte[]) {
                value = hexadecimal((byte[]) value);
            }
            System.out.printf("%1$s%2$s: %3$s%n", indent, method.getName(), value);
        }
    }

    private static <T> void printHelper(Object indent, Class<T> clazz, Object object) {
        Objects.requireNonNull(indent, "indent is null");
        Objects.requireNonNull(clazz, "clazz is null");
        print(indent, clazz, clazz.cast(object));
    }

    private static <T> void print(Object indent, T object) {
        Objects.requireNonNull(indent, "indent is null");
        Objects.requireNonNull(object, "object is null");
        printHelper(indent, object.getClass(), object);
    }

    public static void main(String... args) throws SocketException {
        int index = 0;
        for (var e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
            var n = e.nextElement();
            System.out.printf("getNetworkInterfaces[%1$d]: %2$s%n", index++, n);
            print('\t', n);
        }
    }
}
