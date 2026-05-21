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
import jakarta.inject.Named;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;

import static com.github.jinahya.hello.lib.HelloWorldDi_Constants._DEMO;
import static com.github.jinahya.hello.lib.HelloWorldDi_Constants._IMPL;

/**
 * A CDI producer holding four {@link HelloWorld} producer / disposer pairs — two qualified by
 * {@link Named &#64;Named} ({@value HelloWorldDi_Constants#_DEMO} /
 * {@value HelloWorldDi_Constants#_IMPL}) and two qualified by custom annotations ({@link _Demo} /
 * {@link _Impl}).
 *
 * <p>Annotated with {@link HelloWorld_Logging} at type level so every produce / dispose call is
 * traced by {@link HelloWorld_LoggingInterceptor}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@HelloWorld_Logging
@Slf4j
class HelloWorldCdi_Producer { // @formatter:off

    /**
     * Produces a new {@link HelloWorldDemo} qualified with
     * {@link HelloWorldDi_Constants#_DEMO}.
     *
     * @param injectionPoint the injection point being satisfied
     * @return a new {@link HelloWorldDemo} instance
     */
    @Named(_DEMO) @jakarta.enterprise.inject.Produces
    HelloWorld produceNamedDemo(final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        return new HelloWorldDemo();
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link HelloWorldDi_Constants#_DEMO}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeNamedDemo(
            @Named(_DEMO) @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        Assertions.assertInstanceOf(HelloWorldDemo.class, bean);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces a new {@link HelloWorldImpl} qualified with
     * {@link HelloWorldDi_Constants#_IMPL}.
     *
     * @param injectionPoint the injection point being satisfied
     * @return a new {@link HelloWorldImpl} instance
     */
    @Named(_IMPL) @jakarta.enterprise.inject.Produces
    HelloWorld produceNamedImpl(final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        return new HelloWorldImpl();
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link HelloWorldDi_Constants#_IMPL}.
     *
     * @param bean the {@link HelloWorld} instance to dispose.
     */
    void disposeNamedImpl(
            @Named(_IMPL) @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        Assertions.assertInstanceOf(HelloWorldImpl.class, bean);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces a new {@link HelloWorldDemo} qualified with {@link _Demo}.
     *
     * @param injectionPoint the injection point being satisfied
     * @return a new {@link HelloWorldDemo} instance
     */
    @_Demo @jakarta.enterprise.inject.Produces
    HelloWorld produceQualifiedDemo(
            final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        return new HelloWorldDemo();
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link _Demo}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedDemo(
            @_Demo @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        Assertions.assertInstanceOf(HelloWorldDemo.class, bean);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces a new {@link HelloWorldImpl} qualified with {@link _Impl}.
     *
     * @param injectionPoint the injection point being satisfied
     * @return a new {@link HelloWorldImpl} instance
     */
    @_Impl @jakarta.enterprise.inject.Produces
    HelloWorld produceQualifiedImpl(
            final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        return new HelloWorldImpl();
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link _Impl}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedImpl(
            @_Impl @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        Assertions.assertInstanceOf(HelloWorldImpl.class, bean);
    } // @formatter:off
}
