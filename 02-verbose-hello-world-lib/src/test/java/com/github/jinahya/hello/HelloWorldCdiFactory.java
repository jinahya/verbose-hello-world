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

import static com.github.jinahya.hello.HelloWorldDiTest._NAMED_DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest._NAMED_IMPL;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A provider produces {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldCdiFactory {

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point annotated with
     * {@link Named} whose {@link Named#value() value} equals to
     * {@link HelloWorldDiTest#_NAMED_DEMO}.
     *
     * @param injectionPoint the injection point to be injected
     * @return an instance of {@link HelloWorld}.
     */
    @Named(_NAMED_DEMO)
    @Produces
    HelloWorld produceNamedDemo(InjectionPoint injectionPoint) {
        var bean = new HelloWorldDemo();
        log.debug("producing {} for {}", bean, injectionPoint);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for those injection points which each
     * annotated with {@link Named} whose {@link Named#value() value} equals to
     * {@link HelloWorldDiTest#_NAMED_DEMO}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeNamedDemo(@Named(_NAMED_DEMO) @Disposes HelloWorld bean) {
        log.debug("disposing {}", bean);
        assertTrue(bean instanceof HelloWorldDemo);
    }

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point {@link Named named}
     * with {@link HelloWorldDiTest#_NAMED_IMPL}.
     *
     * @param injectionPoint the injection point to be injected.
     * @return an instance of {@link HelloWorldImpl}
     */
    @Named(_NAMED_IMPL)
    @Produces
    HelloWorld produceNamedImpl(InjectionPoint injectionPoint) {
        var bean = new HelloWorldImpl();
        log.debug("producing {} for {}", bean, injectionPoint);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for injection points
     * {@link Named named} with {@link HelloWorldDiTest#_NAMED_IMPL}.
     *
     * @param bean the {@link HelloWorld} instance to dispose.
     */
    void disposeNamedImpl(@Named(_NAMED_IMPL) @Disposes HelloWorld bean) {
        log.debug("disposing {}", bean);
        assertTrue(bean instanceof HelloWorldImpl);
    }

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point annotated with
     * {@link _QualifiedDemo}.
     *
     * @param injectionPoint the injection point
     * @return an instance of {@link HelloWorld}
     */
    @Produces
    @_QualifiedDemo
    HelloWorld produceQualifiedDemo(InjectionPoint injectionPoint) {
        var bean = new HelloWorldDemo();
        log.debug("producing {} for {}", bean, injectionPoint);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for those injection points which each
     * annotated with {@link _QualifiedDemo}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedDemo(@Disposes @_QualifiedDemo HelloWorld bean) {
        log.debug("disposing {}", bean);
        assertTrue(bean instanceof HelloWorldDemo);
    }

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point annotated with
     * {@link _QualifiedImpl}
     *
     * @param injectionPoint the injection point
     * @return an instance of {@link HelloWorld}
     */
    @Produces
    @_QualifiedImpl
    HelloWorld produceQualifiedImpl(InjectionPoint injectionPoint) {
        var bean = new HelloWorldImpl();
        log.debug("producing {} for {}", bean, injectionPoint);
        return bean;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for those injection points which each
     * annotated with {@link _QualifiedImpl}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedImpl(@Disposes @_QualifiedImpl HelloWorld bean) {
        log.debug("disposing {}", bean);
        assertTrue(bean instanceof HelloWorldImpl);
    }
}
