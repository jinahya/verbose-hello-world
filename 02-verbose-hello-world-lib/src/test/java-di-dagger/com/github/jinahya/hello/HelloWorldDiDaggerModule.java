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

import dagger.Module;
import dagger.Provides;
import jakarta.inject.Named;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Module
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiDaggerModule {

    @Named(HelloWorldDiConstants._NAME_DEMO)
    @Provides
    static HelloWorld provideNamedDemo() {
        return new HelloWorldDemo();
    }

    @Named(HelloWorldDiConstants._NAME_IMPL)
    @Provides
    static HelloWorld provideNamedImpl() {
        return new HelloWorldImpl();
    }

    @_QualifiedDemo
    @Provides
    static HelloWorld provideQualifiedDemo() {
        return new HelloWorldDemo();
    }

    @_QualifiedImpl
    @Provides
    static HelloWorld provideQualifiedImpl() {
        return new HelloWorldImpl();
    }
}
