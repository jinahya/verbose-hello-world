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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A configuration for providing {@link HelloWorld} beans.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@org.springframework.context.annotation.Configuration
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiSpringConfiguration {

    @Named(HelloWorldDi_Constants._NAME_DEMO)
    @org.springframework.context.annotation.Bean
    HelloWorld namedDemo() {
        return new HelloWorldDemo();
    }

    @Named(HelloWorldDi_Constants._NAME_IMPL)
    @org.springframework.context.annotation.Bean
    HelloWorld namedImpl() {
        return new HelloWorldImpl();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @HelloWorld_Qualified_Demo
    @org.springframework.context.annotation.Bean
    HelloWorld qualifiedDemo() {
        return new HelloWorldDemo();
    }

    @HelloWorld_Qualified_Impl
    @org.springframework.context.annotation.Bean
    HelloWorld qualifiedImpl() {
        return new HelloWorldImpl();
    }
}
