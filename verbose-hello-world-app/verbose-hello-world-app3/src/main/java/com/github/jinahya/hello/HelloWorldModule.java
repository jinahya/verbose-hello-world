package com.github.jinahya.hello;

import com.google.inject.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

class HelloWorldModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final String NAME = "com.github.jinahya.hello.HelloWorldImpl";

    static final Class<?> HELLO_WORLD_IMPL;

    static {
        try {
            HELLO_WORLD_IMPL = Class.forName(NAME);
            logger.debug("found: {}", HELLO_WORLD_IMPL);
        } catch (final ClassNotFoundException cnfe) {
            throw new InstantiationError(cnfe.getMessage());
        }
    }

    @Override
    protected void configure() {
        // TODO: implement!
    }
}
