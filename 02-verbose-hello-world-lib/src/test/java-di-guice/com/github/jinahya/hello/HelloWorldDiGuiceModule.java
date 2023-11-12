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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.google.inject.name.Names.named;

/**
 * A Guice module for injecting {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiGuiceModule
        extends AbstractModule {

    @Override
    protected void configure() {
        // -----------------------------------------------------------------------------------------
        bind(HelloWorld.class)
                .annotatedWith(named(HelloWorldDiConstants._NAME_DEMO))
                .to(HelloWorldDemo.class);
        bind(HelloWorld.class)
                .annotatedWith(named(HelloWorldDiConstants._NAME_IMPL))
                .to(HelloWorldImpl.class);
        // -----------------------------------------------------------------------------------------
        bind((HelloWorld.class))
                .annotatedWith(_QualifiedDemo.class)
                .to(HelloWorldDemo.class);
        bind((HelloWorld.class))
                .annotatedWith(_QualifiedImpl.class)
                .to(HelloWorldImpl.class);
        // -----------------------------------------------------------------------------------------
        bind((HelloWorld.class))
                .annotatedWith(_BindingQualifiedDemo.class)
                .to(HelloWorldDemo.class);
        bind((HelloWorld.class))
                .annotatedWith(_BindingQualifiedImpl.class)
                .to(HelloWorldImpl.class);
    }
}
