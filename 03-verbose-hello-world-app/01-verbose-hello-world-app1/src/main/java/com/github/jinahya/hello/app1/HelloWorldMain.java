package com.github.jinahya.hello.app1;

/*-
 * #%L
 * verbose-hello-world-app1
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
import com.github.jinahya.hello.lib.HelloWorldImpl;

import java.io.IOException;

/**
 * A program whose {@link #main()} method obtains a {@link HelloWorld} by directly instantiating
 * {@link HelloWorldImpl} and prints {@code hello, world} to {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldImpl
 */
@SuppressWarnings({
        "java:S106" // Standard outputs should not be used directly to log anything
})
class HelloWorldMain {

    /**
     * Instantiates a {@link HelloWorldImpl} directly, writes {@code hello, world} to
     * {@link System#out} via {@link HelloWorld#write(java.io.OutputStream) write(stream)}, and
     * terminates the line with {@link java.io.PrintStream#println() println()}.
     *
     * @throws IOException if an I/O error occurs while writing to {@link System#out}.
     */
    static void main() throws IOException {
        new HelloWorldImpl()
                .write(System.out)
                .println();
    }

    /**
     * Suppresses external instantiation; always throws an {@link AssertionError}.
     */
    private HelloWorldMain() {
        throw new AssertionError("instantiation is not allowed");
    }
}
