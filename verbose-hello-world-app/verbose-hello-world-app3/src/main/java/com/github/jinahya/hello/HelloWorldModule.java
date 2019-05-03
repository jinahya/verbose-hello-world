package com.github.jinahya.hello;

import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * A module for injecting instances of {@link HelloWorld}.
 */
class HelloWorldModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * The fully qualified name of the {@code HelloWorldMain} class.
     */
    private static final String HELLO_WORLD_IMPL_NAME = "com.github.jinahya.hello.HelloWorldImpl";

    /**
     * The class found with {@link #HELLO_WORLD_IMPL_NAME}.
     */
    static final Class<?> HELLO_WORLD_IMPL_CLASS;

    static {
        try {
            HELLO_WORLD_IMPL_CLASS = Class.forName(HELLO_WORLD_IMPL_NAME);
        } catch (final ClassNotFoundException cnfe) {
            throw new InstantiationError(cnfe.getMessage());
        }
    }

    @Override
    protected void configure() {
        // TODO: implement!
    }
}
