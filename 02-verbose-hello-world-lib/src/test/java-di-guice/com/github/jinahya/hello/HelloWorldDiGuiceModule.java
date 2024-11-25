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

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;

/**
 * A module for injecting {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiGuiceModule extends AbstractModule {

    private void bindNamed(final String name, final Class<? extends HelloWorld> serviceClass) {
        log.debug("binding {} annotated with '{}' to {}", HelloWorld.class.getSimpleName(),
                  name, serviceClass.getSimpleName());
        bind(HelloWorld.class)
                .annotatedWith(Names.named(name))
                .to(serviceClass);
    }

    private void bindAnnotated(final Class<? extends Annotation> annotationClass,
                               final Class<? extends HelloWorld> serviceClass) {
        log.debug("binding {} annotated with @{} to {}", HelloWorld.class.getSimpleName(),
                  annotationClass.getSimpleName(), serviceClass.getSimpleName());
        bind(HelloWorld.class)
                .annotatedWith(annotationClass)
                .to(serviceClass);
    }

    @Override
    protected void configure() {
        // -----------------------------------------------------------------------------------------
        bindNamed(HelloWorldDiConstants._NAME_DEMO, HelloWorldDemo.class);
        bindNamed(HelloWorldDiConstants._NAME_IMPL, HelloWorldImpl.class);
        bindNamed(HelloWorldDiConstants._NAME_WRAP, HelloWorldWrap.class);
        // -----------------------------------------------------------------------------------------
        bindAnnotated(__QualifiedDemo.class, HelloWorldDemo.class);
        bindAnnotated(__QualifiedImpl.class, HelloWorldImpl.class);
        bindAnnotated(__QualifiedWrap.class, HelloWorldWrap.class);
        // -----------------------------------------------------------------------------------------
        bindAnnotated(___BindingQualifiedDemo.class, HelloWorldDemo.class);
        bindAnnotated(___BindingQualifiedImpl.class, HelloWorldImpl.class);
        bindAnnotated(___BindingQualifiedWrap.class, HelloWorldWrap.class);
    }
}
