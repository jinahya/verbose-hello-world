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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration for providing {@link HelloWorld} beans.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Configuration
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiSpringConfiguration {

    @Named(HelloWorldDiConstants._NAME_DEMO)
    @Bean
    HelloWorld namedDemo() {
        return new HelloWorldDemo();
    }

    @Named(HelloWorldDiConstants._NAME_IMPL)
    @Bean
    HelloWorld namedImpl() {
        return new HelloWorldImpl();
    }

    @__QualifiedDemo
    @Bean
    HelloWorld qualifiedDemo() {
        return new HelloWorldDemo();
    }

    @__QualifiedImpl
    @Bean
    HelloWorld qualifiedImpl() {
        return new HelloWorldImpl();
    }
}
