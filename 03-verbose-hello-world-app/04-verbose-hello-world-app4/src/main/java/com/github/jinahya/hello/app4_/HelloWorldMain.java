package com.github.jinahya.hello.app4_;

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
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.CDI;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A program whose {@link #main()} method obtains a {@link HelloWorld} through
 * <a href="https://jakarta.ee/specifications/cdi/">Jakarta CDI</a> and prints
 * {@code hello, world} to {@link System#out}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see SeContainerInitializer#newInstance()
 */
@SuppressWarnings({
        "java:S106",  // Standard outputs should not be used directly to log anything
        "java:S6813"  // Field dependency injection should be avoided
})
class HelloWorldMain {

    static {
        Logger.getLogger("org.jboss.weld").setLevel(Level.WARNING);
    }

    /**
     * Bootstraps a CDI SE container via {@link SeContainerInitializer#initialize()}, selects an
     * instance of this class managed by the container, formats the injected {@link #service} with
     * {@link HelloWorldUtils#string(HelloWorld)}, and prints the resulting string followed by a
     * system-dependent line separator via {@link IO#println(Object)}.
     */
    static void main() {
        try (var container = SeContainerInitializer.newInstance().initialize()) {
            final var instance = CDI.current().select(HelloWorldMain.class).get();
            final var string = HelloWorldUtils.string(instance.service);
            IO.println(string);
        }
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Suppresses external instantiation; the CDI container constructs instances of this class
     * reflectively through this private constructor.
     */
    private HelloWorldMain() {
        super();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * A {@link HelloWorld} qualified with {@link HelloWorldQualifier}, injected by CDI.
     */
    @HelloWorldQualifier
    @jakarta.inject.Inject
    private HelloWorld service;
}
