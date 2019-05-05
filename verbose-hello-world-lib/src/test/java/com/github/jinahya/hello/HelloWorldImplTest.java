package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

/**
 * A class for testing {@link HelloWorldImpl}.
 */
public class HelloWorldImplTest extends AbstractHelloWorldTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    HelloWorld helloWorld() {
        return new HelloWorldImpl();
    }
}
