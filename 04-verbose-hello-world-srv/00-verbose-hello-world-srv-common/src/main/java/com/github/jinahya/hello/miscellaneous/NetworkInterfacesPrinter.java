package com.github.jinahya.hello.miscellaneous;

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

import java.beans.Introspector;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HexFormat;
import java.util.Objects;
import java.util.Optional;
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

    private static <T> void print(Object indent, Class<T> clazz, T object) throws Exception {
        Objects.requireNonNull(indent, "indent is null");
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(object, "object is null");
        for (var descriptor : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
            var reader = descriptor.getReadMethod();
            if (reader == null || reader.getDeclaringClass() != clazz) {
                continue;
            }
            var name = descriptor.getName();
            Object value;
            try {
                value = reader.invoke(object);
            } catch (ReflectiveOperationException roe) {
                throw new RuntimeException("failed to invoke " + reader + " on " + object, roe);
            }
            if (value instanceof Enumeration<?> e) {
                int i = 0;
                while (e.hasMoreElements()) {
                    var n = e.nextElement();
                    System.out.printf("%1$s%2$s[%3$d]: %4$s%n", indent, name, i++, n);
                    print(indent + "\t", n);
                }
                continue;
            }
            if (value instanceof Collection<?> c) {
                int i = 0;
                for (var e : c) {
                    System.out.printf("%1$s%2$s[%3$d]: %4$s%n", indent, name, i++, e);
                    print(indent + "\t", e);
                }
                continue;
            }
            if (value instanceof Stream<?> s) {
                var i = 0;
                for (var j = s.iterator(); j.hasNext(); ) {
                    var n = j.next();
                    System.out.printf("%1$s%2$s[%3$d]: %4$s%n", indent, name, i++, n);
                    print(indent + "\t", n);
                }
                continue;
            }
            if (value instanceof byte[] b) {
                if (name.toLowerCase().endsWith("address")) {
                    try {
                        value = InetAddress.getByAddress(b);
                    } catch (UnknownHostException uhe) {
                        value = HexFormat.of().formatHex(b);
                    }
                } else {
                    value = HexFormat.of().formatHex(b);
                }
            }
            if (false) {
                var type = Optional.of(descriptor.getPropertyType())
                        .filter(c -> !c.isPrimitive())
                        .map(Class::getName)
                        .map(v -> '(' + v + ')').orElse("");
                System.out.printf("%1$s%2$s: %3$s %4$s%n", indent, name, value, type);
            } else {
                System.out.printf("%1$s%2$s: %3$s%n", indent, name, value);
            }
        }
    }

    private static <T> void printHelper(Object indent, Class<T> clazz, Object object)
            throws Exception {
        Objects.requireNonNull(indent, "indent is null");
        Objects.requireNonNull(clazz, "clazz is null");
        print(indent, clazz, clazz.cast(object));
    }

    private static <T> void print(Object indent, T object) throws Exception {
        Objects.requireNonNull(indent, "indent is null");
        Objects.requireNonNull(object, "object is null");
        printHelper(indent, object.getClass(), object);
    }

    public static void main(String... args) throws Exception {
        int index = 0;
        for (var e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
            var n = e.nextElement();
            System.out.printf("networkInterfaces[%1$d]: %2$s%n", index++, n);
            print('\t', n);
        }
    }
}
