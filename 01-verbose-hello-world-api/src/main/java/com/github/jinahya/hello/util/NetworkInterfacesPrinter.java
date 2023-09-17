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

import java.net.NetworkInterface;

/**
 * A class prints properties of all {@link NetworkInterface}s.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://www.baeldung.com/java-network-interfaces">Working with
 * Network Interfaces in Java (Bealdung)</a>
 */
@Slf4j
class NetworkInterfacesPrinter {

    private static final String INDENT = "  ";

    private static void indent(final int depth) {
        if (depth == 0) {
            return;
        }
        System.out.printf("%1$s", INDENT);
        indent(depth - 1);
    }

    public static void main(String... args) throws Exception {
        int index = 0;
        for (final var e = NetworkInterface.getNetworkInterfaces(); e.hasMoreElements(); ) {
            final var networkInterface = e.nextElement();
            System.out.printf("networkInterface[%1$d]: %2$s%n", index++, networkInterface);
            JavaBeansUtils.acceptEachProperty(
                    JavaBeansUtils.PropertyInfoHolder.of(new JavaBeansUtils.PropertyInfo(
                            null,
                            "networkInterface",
                            NetworkInterface.class,
                            index,
                            networkInterface
                    )),
                    networkInterface,
                    p -> i -> {
                        indent(i.getDepth() + 1);
                        if (i.index == null) {
                            System.out.printf("%1$s: %2$s\t%3$s%n", i.name, i.value,
                                              i.type.getName());
                        } else {
                            System.out.printf("%1$s[%2$d]: %3$s\t%4$s%n", i.name, i.index, i.value,
                                              i.type.getName());
                        }
                        return JavaBeansUtils.PropertyInfoHolder.of(i);
                    }
            );
        }
    }
}
