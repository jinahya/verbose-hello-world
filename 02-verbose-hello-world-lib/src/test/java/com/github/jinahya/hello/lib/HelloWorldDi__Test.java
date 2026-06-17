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

import com.github.jinahya.hello.api.*;
import jakarta.inject.*;
import jakarta.inject.Named;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.util.stream.*;

import static com.github.jinahya.hello.lib.HelloWorldDi_Constants.*;

/**
 * An abstract class for testing {@link HelloWorld} implementations using Dependency Injection.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://jcp.org/en/jsr/detail?id=330">JSR 330: Dependency Injection for Java</a>
 */
@DisplayName("HelloWorldDi")
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
abstract class HelloWorldDi__Test extends HelloWorld__Test {

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
    @Named(_DEMO)
    @Inject
    protected HelloWorld namedDemo;

    @Named(_IMPL)
    @Inject
    protected HelloWorld namedImpl;

    // ---------------------------------------------------------------------------------------------
    @_Demo
    @Inject
    protected HelloWorld qualifiedDemo;

    @_Impl
    @Inject
    protected HelloWorld qualifiedImpl;
}
