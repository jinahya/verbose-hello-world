package com.github.jinahya.hello.app3_;

/*-
 * #%L
 * verbose-hello-world-app3
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
import com.google.inject.Guice;
import jakarta.inject.Inject;

/**
 * A program whose {@link #main()} method obtains a {@link HelloWorld} through
 * <a href="https://github.com/google/guice">Guice</a> dependency injection and prints
 * {@code hello, world} to {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorldModule
 */
class HelloWorldMain {

    /**
     * Creates a Guice {@link com.google.inject.Injector Injector} configured by
     * {@link HelloWorldModule}, injects {@link #service} into a fresh instance of this class via
     * {@link com.google.inject.Injector#injectMembers(Object)}, formats the injected service with
     * {@link HelloWorldUtils#string(HelloWorld)}, and prints the resulting string followed by a
     * system-dependent line separator via {@link IO#println(Object)}.
     */
    static void main() {
        final var injector = Guice.createInjector(new HelloWorldModule());
        final var instance = new HelloWorldMain();
        injector.injectMembers(instance);
        IO.println(HelloWorldUtils.string(instance.service));
    }

    /**
     * Suppresses external instantiation; only {@link #main()} constructs an instance through this
     * private constructor for Guice member injection.
     */
    private HelloWorldMain() {
        super();
    }

    /**
     * A {@link HelloWorld} bound by {@link HelloWorldModule}, injected by Guice.
     */
    @Inject
    HelloWorld service;
}
