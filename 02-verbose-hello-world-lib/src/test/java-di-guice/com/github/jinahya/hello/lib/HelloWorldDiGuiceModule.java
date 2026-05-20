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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;

;

/**
 * A module for injecting {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiGuiceModule extends com.google.inject.AbstractModule {

    private void bindNamed(final String name, final Class<? extends HelloWorld> serviceClass) {
        log.debug("binding {} annotated with '{}' to {}", HelloWorld.class.getSimpleName(),
                  name, serviceClass.getSimpleName());
        bind(HelloWorld.class)
                .annotatedWith(com.google.inject.name.Names.named(name))
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
        bindNamed(HelloWorldDi_Constants._NAME_DEMO, HelloWorldDemo.class);
        bindNamed(HelloWorldDi_Constants._NAME_IMPL, HelloWorldImpl.class);
        // -----------------------------------------------------------------------------------------
        bindAnnotated(HelloWorld_Qualified_Demo.class, HelloWorldDemo.class);
        bindAnnotated(HelloWorld_Qualified_Impl.class, HelloWorldImpl.class);
    }
}
