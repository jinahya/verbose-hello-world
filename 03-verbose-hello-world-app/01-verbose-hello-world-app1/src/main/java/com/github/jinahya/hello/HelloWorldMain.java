package com.github.jinahya.hello;

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

import java.io.IOException;
import java.io.OutputStream;

/**
 * A program whose {@link #main(String[])} method prints {@code hello, world} to
 * {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldMain {

    /**
     * The main method of this program which prints {@code hello, world} to {@link System#out}
     * followed by a platform-specific line separator.
     *
     * @param args an array of command line arguments
     * @throws IOException if an I/O error occurs.
     * @see HelloWorldImpl
     * @see HelloWorld#write(OutputStream)
     * @see System#lineSeparator()
     */
    public static void main(String... args) throws IOException {
        // TODO: Create a instance of HelloWorldImpl
        // TODO: Print the 'hello, world' to System.out using the instance
        // TODO: Print the System.lineSeparator() to System.out
    }

    /**
     * Creates a new instance, which is not possible.
     */
    private HelloWorldMain() {
        throw new AssertionError("instantiation is not allowed");
    }
}
