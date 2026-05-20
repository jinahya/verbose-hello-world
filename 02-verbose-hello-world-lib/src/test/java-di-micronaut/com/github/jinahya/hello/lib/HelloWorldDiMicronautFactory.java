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
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;

/**
 * A factory for providing {@link HelloWorld} instances via Micronaut Inject.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://micronaut.io/">Micronaut</a>
 */
@io.micronaut.context.annotation.Factory
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiMicronautFactory {

    private static HelloWorld provideNamed(final String name, final HelloWorld bean) {
        log.debug("providing {} for '{}'", bean, name);
        return bean;
    }

    @io.micronaut.context.annotation.Bean
    @Named(HelloWorldDi_Constants._NAME_DEMO)
    @Singleton
    HelloWorld provideNamedDemo() {
        return provideNamed(HelloWorldDi_Constants._NAME_DEMO, new HelloWorldDemo());
    }

    @io.micronaut.context.annotation.Bean
    @Named(HelloWorldDi_Constants._NAME_IMPL)
    @Singleton
    HelloWorld provideNamedImpl() {
        return provideNamed(HelloWorldDi_Constants._NAME_IMPL, new HelloWorldImpl());
    }

    // ---------------------------------------------------------------------------------------------
    private static HelloWorld provideQualified(final HelloWorld bean,
                                               final Class<? extends Annotation> annotationClass) {
        log.debug("providing {} for '{}'", bean, annotationClass.getSimpleName());
        return bean;
    }

    @io.micronaut.context.annotation.Bean
    @HelloWorld_Qualified_Demo
    @Singleton
    HelloWorld provideQualifiedDemo() {
        return provideQualified(new HelloWorldDemo(), HelloWorld_Qualified_Demo.class);
    }

    @io.micronaut.context.annotation.Bean
    @HelloWorld_Qualified_Impl
    @Singleton
    HelloWorld provideQualifiedImpl() {
        return provideQualified(new HelloWorldImpl(), HelloWorld_Qualified_Impl.class);
    }
}
