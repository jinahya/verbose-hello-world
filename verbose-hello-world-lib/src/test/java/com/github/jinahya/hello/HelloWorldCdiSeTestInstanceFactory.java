package com.github.jinahya.hello;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstanceFactory;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import java.lang.invoke.MethodHandles;

class HelloWorldCdiSeTestInstanceFactory implements TestInstanceFactory {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public Object createTestInstance(final TestInstanceFactoryContext testInstanceFactoryContext,
                                     final ExtensionContext extensionContext)
            throws TestInstantiationException {
        final Class<?> testClass = testInstanceFactoryContext.getTestClass();
        final SeContainerInitializer initializer = SeContainerInitializer.newInstance()
//                .disableDiscovery()
                .addBeanClasses(HelloWorldCdiFactory.class, testClass);
        try (SeContainer container = initializer.initialize()) {
            return container.select(testClass).get();
        }
    }
}
