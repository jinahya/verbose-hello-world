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
import com.github.jinahya.hello.api.HelloWorldUtils;

import java.util.ServiceLoader;

/**
 * A program whose {@link #main()} method obtains a {@link HelloWorld} via the
 * {@link ServiceLoader Service Provider Interface} and prints {@code hello, world} to
 * {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see ServiceLoader#load(Class)
 */
class HelloWorldMain {

    /**
     * Loads the first registered {@link HelloWorld} provider through {@link ServiceLoader}, formats
     * it with {@link HelloWorldUtils#string(HelloWorld)}, and prints the resulting string followed
     * by a system-dependent line separator via {@link IO#println(Object)}.
     *
     * @see ServiceLoader#load(Class)
     */
    public static void main() {
        IO.println(
                HelloWorldUtils.string(
                        ServiceLoader.load(HelloWorld.class).iterator().next()
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
