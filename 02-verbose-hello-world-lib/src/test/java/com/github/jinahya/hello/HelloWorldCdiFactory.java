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
import org.junit.jupiter.api.Assertions;

/**
 * A provider produces {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldCdiFactory {

    /**
     * Produces an instance of {@link HelloWorld} qualified with
     * {@link HelloWorldDiConstants#_NAME_DEMO}.
     *
     * @param injectionPoint the injection point to be injected
     * @return an instance of {@link HelloWorld}.
     */
    @Named(HelloWorldDiConstants._NAME_DEMO)
    @Produces
    HelloWorld produceNamedDemo(final InjectionPoint injectionPoint) {
        final var bean = new HelloWorldDemo();
        log.debug("producing {} for {}", bean, injectionPoint);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link HelloWorldDiConstants#_NAME_IMPL}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeNamedDemo(
            @Named(HelloWorldDiConstants._NAME_DEMO) @Disposes final HelloWorld bean) {
        log.debug("disposing {}", bean);
        Assertions.assertTrue(bean instanceof HelloWorldDemo);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces an instance of {@link HelloWorld} qualified with
     * {@link HelloWorldDiConstants#_NAME_IMPL}.
     *
     * @param injectionPoint the injection point to be injected.
     * @return an instance of {@link HelloWorldImpl}
     */
    @Named(HelloWorldDiConstants._NAME_IMPL)
    @Produces
    HelloWorld produceNamedImpl(final InjectionPoint injectionPoint) {
        final var bean = new HelloWorldImpl();
        log.debug("producing {} for {}", bean, injectionPoint);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link HelloWorldDiConstants#_NAME_IMPL}.
     *
     * @param bean the {@link HelloWorld} instance to dispose.
     */
    void disposeNamedImpl(
            @Named(HelloWorldDiConstants._NAME_IMPL) @Disposes final HelloWorld bean) {
        log.debug("disposing {}", bean);
        Assertions.assertTrue(bean instanceof HelloWorldImpl);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces an instance of {@link HelloWorld} qualified with {@link _QualifiedDemo}.
     *
     * @param injectionPoint the injection point
     * @return an instance of {@link HelloWorld}
     */
    @_QualifiedDemo
    @Produces
    HelloWorld produceQualifiedDemo(final InjectionPoint injectionPoint) {
        final var bean = new HelloWorldDemo();
        log.debug("producing {} for {}", bean, injectionPoint);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with {@link _QualifiedDemo}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedDemo(@_QualifiedDemo @Disposes final HelloWorld bean) {
        log.debug("disposing {}", bean);
        Assertions.assertTrue(bean instanceof HelloWorldDemo);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces an instance of {@link HelloWorld} qualified with {@link _QualifiedImpl}.
     *
     * @param injectionPoint the injection point
     * @return an instance of {@link HelloWorld}
     */
    @_QualifiedImpl
    @Produces
    HelloWorld produceQualifiedImpl(final InjectionPoint injectionPoint) {
        final var bean = new HelloWorldImpl();
        log.debug("producing {} for {}", bean, injectionPoint);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with {@link _QualifiedImpl}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedImpl(@_QualifiedImpl @Disposes final HelloWorld bean) {
        log.debug("disposing {}", bean);
        Assertions.assertTrue(bean instanceof HelloWorldImpl);
    }
}
