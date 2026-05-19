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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * A test class which injects {@link HelloWorld} instances using Avaje Inject.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://avaje.io/inject/">Avaje Inject</a>
 */
//@io.avaje.inject.test.InjectTest
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldDiAvajeTest extends HelloWorldDiTest {

    @BeforeEach
    void _beforeEach() {
        beanScope = io.avaje.inject.BeanScope.builder().modules(new LibModule()).build();
        namedDemo = beanScope.get(HelloWorld.class, HelloWorldDiConstants._NAME_DEMO);
        namedImpl = beanScope.get(HelloWorld.class, HelloWorldDiConstants._NAME_IMPL);
        qualifiedDemo = beanScope.get(HelloWorld.class, __QualifiedDemo.class.getSimpleName());
        qualifiedImpl = beanScope.get(HelloWorld.class, __QualifiedImpl.class.getSimpleName());
    }

    @AfterEach
    void _afterEach() {
        if (beanScope != null) {
            beanScope.close();
            beanScope = null;
        }
    }

    private io.avaje.inject.BeanScope beanScope;
}
