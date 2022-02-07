package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-lib
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

import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;

import static com.github.jinahya.hello.HelloWorldDiTest.DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest.IMPL;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A provider produces {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldCdiFactory {

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point
     * annotated with {@link Named} whose {@link Named#value() value} equals to
     * {@link HelloWorldDiTest#DEMO}.
     *
     * @param injectionPoint the injection point to be injected
     * @return an instance of {@link HelloWorld}.
     */
    @javax.inject.Named(DEMO)
    @javax.enterprise.inject.Produces
    @Named(DEMO)
    @Produces
    HelloWorld produceNamedDemo(final InjectionPoint injectionPoint) {
        final var helloWorld = new HelloWorldDemo();
        log.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for those
     * injection points which each annotated with {@link Named} whose {@link
     * Named#value() value} equals to {@link HelloWorldDiTest#DEMO}.
     *
     * @param helloWorld the {@link HelloWorld} instance to dispose
     */
    void disposeNamedDemo(
            @javax.inject.Named(DEMO)
            @javax.enterprise.inject.Disposes
            @Named(DEMO) @Disposes final HelloWorld helloWorld) {
        log.debug("disposing {}", helloWorld);
        assertTrue(helloWorld instanceof HelloWorldDemo);
    }

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point
     * {@link Named named} with {@link HelloWorldDiTest#IMPL}.
     *
     * @param injectionPoint the injection point to be injected.
     * @return an instance of {@link HelloWorldImpl}
     */
    @javax.inject.Named(IMPL)
    @javax.enterprise.inject.Produces
    @Named(IMPL)
    @Produces
    HelloWorld produceNamedImpl(final InjectionPoint injectionPoint) {
        final var helloWorld = new HelloWorldImpl();
        log.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for injection
     * points {@link Named named} with {@link HelloWorldDiTest#IMPL}.
     *
     * @param helloWorld the {@link HelloWorld} instance to dispose.
     */
    void disposeNamedImpl(
            @javax.inject.Named(IMPL)
            @javax.enterprise.inject.Disposes
            @Named(IMPL) @Disposes final HelloWorld helloWorld) {
        log.debug("disposing {}", helloWorld);
        assertTrue(helloWorld instanceof HelloWorldImpl);
    }

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point
     * annotated with {@link QualifiedDemo}.
     *
     * @param injectionPoint the injection point
     * @return an instance of {@link HelloWorld}
     */
    @javax.enterprise.inject.Produces
    @Produces
    @QualifiedDemo
    HelloWorld produceQualifiedDemo(final InjectionPoint injectionPoint) {
        final var helloWorld = new HelloWorldDemo();
        log.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for those
     * injection points which each annoatated with {@link QualifiedDemo}.
     *
     * @param helloWorld the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedDemo(
            @javax.enterprise.inject.Disposes
            @Disposes @QualifiedDemo final HelloWorld helloWorld) {
        log.debug("disposing {}", helloWorld);
        assertTrue(helloWorld instanceof HelloWorldDemo);
    }

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point
     * annotated with {@link QualifiedImpl}
     *
     * @param injectionPoint the injection point
     * @return an instance of {@link HelloWorld}
     */
    @javax.enterprise.inject.Produces
    @Produces
    @QualifiedImpl
    HelloWorld produceQualifiedImpl(final InjectionPoint injectionPoint) {
        final var helloWorld = new HelloWorldImpl();
        log.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for those
     * injection points which each annotated with {@link QualifiedImpl}.
     *
     * @param helloWorld the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedImpl(
            @javax.enterprise.inject.Disposes
            @Disposes @QualifiedImpl final HelloWorld helloWorld) {
        log.debug("disposing {}", helloWorld);
        assertTrue(helloWorld instanceof HelloWorldImpl);
    }
}
