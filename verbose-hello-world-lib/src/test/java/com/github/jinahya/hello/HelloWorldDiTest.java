package com.github.jinahya.hello;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class HelloWorldDiTest extends AbstractHelloWorldTest {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * An injection qualifier for {@link HelloWorldDemo}.
     */
    static final String DEMO = "demo";

    /**
     * An injection qualifier for {@link HelloWorldImpl}.
     */
    static final String IMPL = "impl";

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    HelloWorld helloWorld() {
        // TODO: implement!
        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Inject
    @Named(DEMO)
    HelloWorld namedDemo;

    @Inject
    @Named(IMPL)
    HelloWorld namedImpl;

    @Inject
    @QualifiedDemo
    HelloWorld qualifiedDemo;

    @Inject
    @QualifiedImpl
    HelloWorld qualifiedImpl;
}
