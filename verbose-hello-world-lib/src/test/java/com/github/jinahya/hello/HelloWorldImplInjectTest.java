package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.invoke.MethodHandles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class HelloWorldImplInjectTest extends AbstractHelloWorldImplTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    HelloWorld helloWorld() {
        assertNotNull(helloWorld);
        return helloWorld;
    }

    @Inject
    private HelloWorld helloWorld;
}
