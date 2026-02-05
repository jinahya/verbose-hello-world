package com.github.jinahya.hello.app2_;

/*-
 * #%L
 * verbose-hello-world-app2
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import com.github.jinahya.hello.api.HelloWorld;

import java.nio.charset.StandardCharsets;
import java.util.ServiceLoader;

/**
 * A program whose {@link #main(String[])} method prints {@code hello, world} to
 * {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldMain {

    /**
     * The main method of this program which prints {@code hello, world} to {@link System#out}
     * followed by a system-dependent line separator.
     *
     * @param args an array of command line arguments
     * @see java.util.ServiceLoader#load(Class)
     */
    public static void main(final String... args) {
        final var loader = ServiceLoader.load(HelloWorld.class);
        final var service = loader.iterator().next(); // NoSuchElementException
        final var array = service.set(new byte[HelloWorld.BYTES]);
        final var string = new String(array, StandardCharsets.US_ASCII);
        System.out.printf("%1$s%n", string);
    }

    /**
     * Creates a new instance, which is not possible.
     */
    private HelloWorldMain() {
        throw new AssertionError("instantiation is not allowed");
    }
}
