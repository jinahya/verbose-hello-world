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

import dagger.Provides;
import jakarta.inject.Named;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;

@dagger.Module
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiDaggerModule {

    private static HelloWorld provideNamed(final String name, final HelloWorld bean) {
        log.debug("providing {} for '{}'", bean, name);
        return bean;
    }

    @Named(HelloWorldDiConstants._NAME_DEMO)
    @Provides
    static HelloWorld provideNamedDemo() {
        return provideNamed(HelloWorldDiConstants._NAME_DEMO, new HelloWorldDemo());
    }

    @Named(HelloWorldDiConstants._NAME_IMPL)
    @Provides
    static HelloWorld provideNamedImpl() {
        return provideNamed(HelloWorldDiConstants._NAME_IMPL, new HelloWorldImpl());
    }

    @Named(HelloWorldDiConstants._NAME_WRAP)
    @Provides
    static HelloWorld provideNamedWrap() {
        return provideNamed(HelloWorldDiConstants._NAME_WRAP, new HelloWorldWrap());
    }

    // -----------------------------------------------------------------------------------------------------------------
    private static HelloWorld provideQualified(final HelloWorld bean,
                                               final Class<? extends Annotation> annotationClass) {
        log.debug("providing {} for '{}'", bean, annotationClass.getSimpleName());
        return bean;
    }

    @__QualifiedDemo
    @Provides
    static HelloWorld provideQualifiedDemo() {
        return provideQualified(new HelloWorldDemo(), __QualifiedDemo.class);
    }

    @__QualifiedImpl
    @Provides
    static HelloWorld provideQualifiedImpl() {
        return provideQualified(new HelloWorldImpl(), __QualifiedImpl.class);
    }

    @__QualifiedWrap
    @Provides
    static HelloWorld provideQualifiedWrap() {
        return provideQualified(new HelloWorldWrap(), __QualifiedWrap.class);
    }
}
