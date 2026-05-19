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
import com.github.jinahya.hello.api.HelloWorldUtils;
import com.github.jinahya.hello.lib.HelloWorldImpl;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A program whose {@link #main()} method obtains a {@link HelloWorld} by directly instantiating
 * {@link HelloWorldImpl} and prints {@code hello, world} to {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldImpl
 */
class HelloWorldMain {

    /**
     * Instantiates a {@link HelloWorldImpl} directly, formats the result with
     * {@link HelloWorldUtils#string(HelloWorld)}, and prints the resulting string followed by a
     * system-dependent line separator via {@link IO#println(Object)}.
     */
    static void main() {
        IO.println(
                HelloWorldUtils.string(
                        new HelloWorldImpl()
                )
        );
    }

    /**
     * Suppresses external instantiation; always throws an {@link AssertionError}.
     */
    private HelloWorldMain() {
        throw new AssertionError("instantiation is not allowed");
    }
}
