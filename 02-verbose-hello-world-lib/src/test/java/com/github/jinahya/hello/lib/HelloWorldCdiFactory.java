package com.github.jinahya.hello.lib;

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

import com.github.jinahya.hello.api.HelloWorld;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

/**
 * A provider produces {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldCdiFactory {

    private static void logProducing(
            final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint,
            final Object bean) {
        log.debug("producing [{}]\tfor [{}]", bean, injectionPoint.getMember().getName());
    }

    private static void logDisposing(final Object bean) {
        log.debug("disposing [{}]", bean);
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Produces an instance of {@link HelloWorld} qualified with
     * {@link HelloWorldDiConstants#_NAME_DEMO}.
     *
     * @param injectionPoint the injection point to be injected
     * @return an instance of {@link HelloWorld}.
     */
    @jakarta.inject.Named(HelloWorldDiConstants._NAME_DEMO)
    @jakarta.enterprise.inject.Produces
    HelloWorld produceNamedDemo(final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        final var bean = new HelloWorldDemo();
        logProducing(injectionPoint, bean);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link HelloWorldDiConstants#_NAME_IMPL}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeNamedDemo(
            @jakarta.inject.Named(HelloWorldDiConstants._NAME_DEMO)
            @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        logDisposing(bean);
        Assertions.assertInstanceOf(HelloWorldDemo.class, bean);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces an instance of {@link HelloWorld} qualified with
     * {@link HelloWorldDiConstants#_NAME_IMPL}.
     *
     * @param injectionPoint the injection point to be injected.
     * @return an instance of {@link HelloWorldImpl}
     */
    @jakarta.inject.Named(HelloWorldDiConstants._NAME_IMPL)
    @jakarta.enterprise.inject.Produces
    HelloWorld produceNamedImpl(final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        final var bean = new HelloWorldImpl();
        logProducing(injectionPoint, bean);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link HelloWorldDiConstants#_NAME_IMPL}.
     *
     * @param bean the {@link HelloWorld} instance to dispose.
     */
    void disposeNamedImpl(
            @jakarta.inject.Named(HelloWorldDiConstants._NAME_IMPL)
            @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        logDisposing(bean);
        Assertions.assertInstanceOf(HelloWorldImpl.class, bean);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces an instance of {@link HelloWorld} qualified with {@link __QualifiedDemo}.
     *
     * @param injectionPoint the injection point
     * @return an instance of {@link HelloWorld}
     */
    @__QualifiedDemo
    @jakarta.enterprise.inject.Produces
    HelloWorld produceQualifiedDemo(
            final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        final var bean = new HelloWorldDemo();
        logProducing(injectionPoint, bean);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with {@link __QualifiedDemo}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedDemo(
            @__QualifiedDemo @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        logDisposing(bean);
        Assertions.assertInstanceOf(HelloWorldDemo.class, bean);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces an instance of {@link HelloWorld} qualified with {@link __QualifiedImpl}.
     *
     * @param injectionPoint the injection point
     * @return an instance of {@link HelloWorld}
     */
    @__QualifiedImpl
    @jakarta.enterprise.inject.Produces
    HelloWorld produceQualifiedImpl(
            final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        final var bean = new HelloWorldImpl();
        logProducing(injectionPoint, bean);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with {@link __QualifiedImpl}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedImpl(
            @__QualifiedImpl @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        logDisposing(bean);
        Assertions.assertInstanceOf(HelloWorldImpl.class, bean);
    }
}
