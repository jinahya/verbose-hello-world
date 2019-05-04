package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.invoke.MethodHandles;

abstract class HelloWorldImplInjectTest extends AbstractHelloWorldImplTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * An injection qualifier for {@link HelloWorldImpl}.
     */
    static final String IMPL = "impl";

    /**
     * An injection qualifier for {@link HelloWorldDemo}.
     */
    static final String DEMO = "demo";

    @Override
    HelloWorld helloWorld() {
        // TODO: implement!
        return null;
    }

    @Inject
    @Named(IMPL)
    HelloWorld namedImpl;

    @Inject
    @Named(DEMO)
    HelloWorld namedDemo;

    @Inject
    @QualifiedImpl
    HelloWorld qualifiedImpl;

    @Inject
    @QualifiedDemo
    HelloWorld qualifiedDemo;
}
