package com.github.jinahya.hello.app1_;

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
import java.io.OutputStream;

/**
 * A program whose {@link #main()} method prints {@code hello, world} to {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
public class HelloWorldMain {

    /**
     * The main method of this program which prints {@code hello, world} to {@link System#out}
     * followed by a system-dependent line separator.
     *
     * @throws IOException if an I/O error occurs.
     * @see HelloWorldImpl
     * @see HelloWorld#write(OutputStream)
     * @see System#lineSeparator()
     */
    static void main() throws IOException {
        final var service = new HelloWorldImpl();
        final var array = service.set(new byte[HelloWorld.BYTES]);
        System.out.write(array);
        System.out.println();
    }

    /**
     * Creates a new instance, which is not possible.
     */
    HelloWorldMain() {
        super();
    }
}
