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

import jakarta.inject.Named;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.github.jinahya.hello.HelloWorldDiTest._NAMED_DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest._NAMED_IMPL;

/**
 * A configuration for providing {@link HelloWorld} beans.
 */
@Configuration
class HelloWorldDiSpringConfiguration {

    @Named(_NAMED_DEMO)
    @Bean
    HelloWorld namedDemo() {
        return new HelloWorldDemo();
    }

    @Named(_NAMED_IMPL)
    @Bean
    HelloWorld namedImpl() {
        return new HelloWorldImpl();
    }

    @_QualifiedDemo
    @Bean
    HelloWorld qualifiedDemo() {
        return new HelloWorldDemo();
    }

    @_QualifiedImpl
    @Bean
    HelloWorld qualifiedImpl() {
        return new HelloWorldImpl();
    }
}
