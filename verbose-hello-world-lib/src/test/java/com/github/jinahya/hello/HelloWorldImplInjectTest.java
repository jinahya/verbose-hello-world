package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

class HelloWorldImplInjectTest extends AbstractHelloWorldImplTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    HelloWorld helloWorld() {
        // TODO: implement!
        return null;
    }
}
