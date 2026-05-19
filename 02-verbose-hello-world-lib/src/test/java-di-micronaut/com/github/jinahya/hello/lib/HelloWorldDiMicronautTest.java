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
 * A test class which injects {@link HelloWorld} instances using Micronaut Inject.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://micronaut.io/">Micronaut</a>
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@io.micronaut.context.annotation.Prototype
@Slf4j
class HelloWorldDiMicronautTest extends HelloWorldDiTest {

    @BeforeEach
    void _beforeEach() {
        beanContext = io.micronaut.context.BeanContext.run();
        beanContext.inject(this);
    }

    @AfterEach
    void _afterEach() {
        if (beanContext != null) {
            beanContext.close();
            beanContext = null;
        }
    }

    private io.micronaut.context.BeanContext beanContext;
}
