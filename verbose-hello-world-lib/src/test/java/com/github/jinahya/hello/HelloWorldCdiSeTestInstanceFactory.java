package com.github.jinahya.hello;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

/**
 * A test instance factory for {@link HelloWorldCdiSeTest} class.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorldCdiSeTestInstanceFactory implements TestInstanceFactory {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public Object createTestInstance(final TestInstanceFactoryContext testInstanceFactoryContext,
                                     final ExtensionContext extensionContext)
            throws TestInstantiationException {
        final Class<?> testClass = testInstanceFactoryContext.getTestClass();
        final SeContainerInitializer seContainerInitializer = SeContainerInitializer.newInstance()
                .addBeanClasses(HelloWorldCdiFactory.class, testClass);
        try (SeContainer container = seContainerInitializer.initialize()) {
            return container.select(testClass).get();
        }
    }
}
