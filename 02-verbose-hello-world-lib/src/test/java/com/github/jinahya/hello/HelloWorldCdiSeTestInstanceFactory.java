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

import jakarta.enterprise.inject.se.SeContainerInitializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;

/**
 * A test instance factory for {@link HelloWorldCdiSeTest} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldCdiSeTestInstanceFactory
        implements TestInstanceFactory {

    @Override
    public Object createTestInstance(final TestInstanceFactoryContext factoryContext,
                                     final ExtensionContext extensionContext)
            throws TestInstantiationException {
        final var testClass = factoryContext.getTestClass();
        log.debug("testClass: {}", testClass);
        final var containerInitializer = SeContainerInitializer.newInstance()
                .addBeanClasses(HelloWorldCdiFactory.class, testClass);
        log.debug("containerInitializer: {}", containerInitializer);
        try (var container = containerInitializer.initialize()) {
            log.debug("container: {}", container);
            return container.select(testClass).get();
        }
    }
}
