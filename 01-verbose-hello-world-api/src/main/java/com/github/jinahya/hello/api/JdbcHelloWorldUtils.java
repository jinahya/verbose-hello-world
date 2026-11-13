package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import java.nio.charset.*;
import java.util.*;

final class JdbcHelloWorldUtils {

    /**
     * Returns a new {@value JdbcHelloWorldConstants#BYTES}-byte array on which the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> are set, produced by
     * invoking {@link JdbcHelloWorld#set(byte[], int) set(array, 0)} on the specified service.
     *
     * @param service the {@link JdbcHelloWorld} service that produces the bytes.
     * @return a new array of the hello-world-bytes.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    static byte[] array(final JdbcHelloWorld service) {
        Objects.requireNonNull(service, "service is null");
        final var array = new byte[JdbcHelloWorldConstants.BYTES];
        service.set(array, 0);
        return array;
    }

    /**
     * Returns a string of the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * decoded, in {@link StandardCharsets#US_ASCII US-ASCII}, from the
     * {@link #array(JdbcHelloWorld) array(service)}.
     *
     * @param service the {@link JdbcHelloWorld} service that produces the bytes.
     * @return a string of the hello-world-bytes.
     * @throws NullPointerException if the {@code service} is {@code null}.
     */
    static String string(final JdbcHelloWorld service) {
        Objects.requireNonNull(service, "service is null");
        return new String(array(service), StandardCharsets.US_ASCII);
    }

    private JdbcHelloWorldUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
