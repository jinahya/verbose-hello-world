package com.github.jinahya.hello;

import org.slf4j.Logger;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A class for testing {@link HelloWorldImpl}.
 */
public class HelloWorldImplTest extends AbstractHelloWorldTest {

    private static final Logger logger = getLogger(lookup().lookupClass());

    @Override
    HelloWorld helloWorld() {
        return new HelloWorldImpl();
    }
}
