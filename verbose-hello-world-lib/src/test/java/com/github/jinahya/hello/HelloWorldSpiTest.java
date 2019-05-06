package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * A class for testing {@link HelloWorldImpl} using Service Provider Interface.
 */
class HelloWorldSpiTest extends AbstractHelloWorldTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    HelloWorld helloWorld() {
        // TODO: implement!
        return null;
    }
}
