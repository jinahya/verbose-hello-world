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

import jakarta.enterprise.inject.*;
import jakarta.enterprise.inject.se.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.extension.*;

import java.util.*;

/**
 * A test instance factory for {@link HelloWorldCdiSe__Test} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldCdiSe_TestInstanceFactory
        implements TestInstanceFactory, BeforeAllCallback, AfterAllCallback,
                   TestInstancePreDestroyCallback {

    private static final ExtensionContext.Namespace NS =
            ExtensionContext.Namespace.create(HelloWorldCdiSe_TestInstanceFactory.class);

    private static final String KEY_CONTAINER = "container";

    /**
     * Maps each test instance to its CDI {@link Instance.Handle} so that
     * {@link #preDestroyTestInstance(ExtensionContext)} can close it. Keyed by identity because two
     * test instances of the same test class are equal under {@code equals}.
     */
    private static final Map<Object, Instance.Handle<?>> HANDLES =
            Collections.synchronizedMap(new IdentityHashMap<>());

    // ---------------------------------------------------------------------------------------------
    private HelloWorldCdiSe_TestInstanceFactory() {
        super();
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public void beforeAll(final ExtensionContext context) {
        final var testClass = context.getRequiredTestClass();
        final var initializer = SeContainerInitializer.newInstance()
                .addBeanClasses(HelloWorldCdi_Producer.class, testClass);
        final var container = initializer.initialize();
        log.debug("container initialized: {}", container);
        context.getStore(NS).put(KEY_CONTAINER, container);
    }

    @Override
    public void afterAll(final ExtensionContext context) {
        final var container = context.getStore(NS).remove(KEY_CONTAINER, SeContainer.class);
        if (container != null) {
            log.debug("closing container: {}", container);
            container.close();
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public Object createTestInstance(final TestInstanceFactoryContext factoryContext,
                                     final ExtensionContext extensionContext)
            throws TestInstantiationException {
        final var testClass = factoryContext.getTestClass();
        final var container = extensionContext.getStore(NS).get(KEY_CONTAINER, SeContainer.class);
        final var handle = container.select(testClass).getHandle();
        final var testInstance = handle.get();
        HANDLES.put(testInstance, handle);
        log.debug("creating test instance: {}", testInstance);
        return testInstance;
    }

    @Override
    public void preDestroyTestInstance(final ExtensionContext context) {
        context.getTestInstance().ifPresent(testInstance -> {
            final var handle = HANDLES.remove(testInstance);
            if (handle != null) {
                log.debug("destroying test instance: {}", testInstance);
                handle.close();
            }
        });
    }
}
