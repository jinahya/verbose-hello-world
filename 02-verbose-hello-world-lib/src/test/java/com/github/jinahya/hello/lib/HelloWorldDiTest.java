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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Stream;

/**
 * An abstract class for testing {@link HelloWorld} implementations using Dependency Injection.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://jcp.org/en/jsr/detail?id=330">JSR 330: Dependency Injection for Java</a>
 */
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
abstract class HelloWorldDiTest extends __HelloWorld__Test {

    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + String.format("%08x", hashCode());
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    Stream<HelloWorld> services() {
        return Stream.of(
                namedDemo,
                namedImpl,
                qualifiedDemo,
                qualifiedImpl
        );
    }

    // ---------------------------------------------------------------------------------------------
    @Named(HelloWorldDiConstants._NAME_DEMO)
    @jakarta.inject.Inject
    protected HelloWorld namedDemo;

    @Named(HelloWorldDiConstants._NAME_IMPL)
    @jakarta.inject.Inject
    protected HelloWorld namedImpl;

    // ---------------------------------------------------------------------------------------------
    @__QualifiedDemo
    @jakarta.inject.Inject
    protected HelloWorld qualifiedDemo;

    @__QualifiedImpl
    @jakarta.inject.Inject
    protected HelloWorld qualifiedImpl;
}
