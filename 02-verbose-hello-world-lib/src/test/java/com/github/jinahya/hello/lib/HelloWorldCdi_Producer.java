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
 * A CDI producer holding four {@link HelloWorld} producer / disposer pairs — two qualified by
 * {@link jakarta.inject.Named &#64;Named} ({@value HelloWorldDi_Constants#_NAME_DEMO} /
 * {@value HelloWorldDi_Constants#_NAME_IMPL}) and two qualified by custom annotations
 * ({@link HelloWorld_Qualified_Demo} / {@link HelloWorld_Qualified_Impl}).
 *
 * <p>Annotated with {@link HelloWorld_Logging} at type level so every produce / dispose call is
 * traced by {@link HelloWorld_LoggingInterceptor}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@HelloWorld_Logging
@Slf4j
class HelloWorldCdi_Producer {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Produces a new {@link HelloWorldDemo} qualified with
     * {@link HelloWorldDi_Constants#_NAME_DEMO}.
     *
     * @param injectionPoint the injection point being satisfied
     * @return a new {@link HelloWorldDemo} instance
     */
    @jakarta.inject.Named(HelloWorldDi_Constants._NAME_DEMO)
    @jakarta.enterprise.inject.Produces
    HelloWorld produceNamedDemo(final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        return new HelloWorldDemo();
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link HelloWorldDi_Constants#_NAME_DEMO}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeNamedDemo(
            @jakarta.inject.Named(HelloWorldDi_Constants._NAME_DEMO)
            @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        Assertions.assertInstanceOf(HelloWorldDemo.class, bean);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces a new {@link HelloWorldImpl} qualified with
     * {@link HelloWorldDi_Constants#_NAME_IMPL}.
     *
     * @param injectionPoint the injection point being satisfied
     * @return a new {@link HelloWorldImpl} instance
     */
    @jakarta.inject.Named(HelloWorldDi_Constants._NAME_IMPL)
    @jakarta.enterprise.inject.Produces
    HelloWorld produceNamedImpl(final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        return new HelloWorldImpl();
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link HelloWorldDi_Constants#_NAME_IMPL}.
     *
     * @param bean the {@link HelloWorld} instance to dispose.
     */
    void disposeNamedImpl(
            @jakarta.inject.Named(HelloWorldDi_Constants._NAME_IMPL)
            @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        Assertions.assertInstanceOf(HelloWorldImpl.class, bean);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces a new {@link HelloWorldDemo} qualified with {@link HelloWorld_Qualified_Demo}.
     *
     * @param injectionPoint the injection point being satisfied
     * @return a new {@link HelloWorldDemo} instance
     */
    @HelloWorld_Qualified_Demo
    @jakarta.enterprise.inject.Produces
    HelloWorld produceQualifiedDemo(
            final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        return new HelloWorldDemo();
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link HelloWorld_Qualified_Demo}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedDemo(
            @HelloWorld_Qualified_Demo @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        Assertions.assertInstanceOf(HelloWorldDemo.class, bean);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Produces a new {@link HelloWorldImpl} qualified with {@link HelloWorld_Qualified_Impl}.
     *
     * @param injectionPoint the injection point being satisfied
     * @return a new {@link HelloWorldImpl} instance
     */
    @HelloWorld_Qualified_Impl
    @jakarta.enterprise.inject.Produces
    HelloWorld produceQualifiedImpl(
            final jakarta.enterprise.inject.spi.InjectionPoint injectionPoint) {
        return new HelloWorldImpl();
    }

    /**
     * Disposes specified {@link HelloWorld} instance qualified with
     * {@link HelloWorld_Qualified_Impl}.
     *
     * @param bean the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedImpl(
            @HelloWorld_Qualified_Impl @jakarta.enterprise.inject.Disposes final HelloWorld bean) {
        Assertions.assertInstanceOf(HelloWorldImpl.class, bean);
    }
}
