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

import static com.github.jinahya.hello.lib.HelloWorldDi_Constants._DEMO;
import static com.github.jinahya.hello.lib.HelloWorldDi_Constants._IMPL;

/**
 * A module for injecting {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDi_Guice_Module extends com.google.inject.AbstractModule {

    @Override
    protected void configure() {
        bind(HelloWorld.class)
                .annotatedWith(com.google.inject.name.Names.named(_DEMO))
                .to(HelloWorldDemo.class);
        bind(HelloWorld.class)
                .annotatedWith(com.google.inject.name.Names.named(_IMPL))
                .to(HelloWorldImpl.class);
        bind(HelloWorld.class)
                .annotatedWith(_Demo.class)
                .to(HelloWorldDemo.class);
        bind(HelloWorld.class)
                .annotatedWith(_Impl.class)
                .to(HelloWorldImpl.class);
    }
}
