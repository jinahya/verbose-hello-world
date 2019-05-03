package com.github.jinahya.hello;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static com.github.jinahya.hello.HelloWorldImplInjectTest.QUALIFIER_DEMO;
import static com.github.jinahya.hello.HelloWorldImplInjectTest.QUALIFIER_IMPL;
import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A binder for injecting {@link HelloWorld}s.
 */
class HelloWorldImplInjectHk2Binder extends AbstractBinder {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static class ImplQualifierLiteral extends AnnotationLiteral<ImplQualifier> implements ImplQualifier {

    }

    private static class DemoQualifierLiteral extends AnnotationLiteral<DemoQualifier> implements DemoQualifier {

    }

    @Override
    protected void configure() {
        if (current().nextBoolean()) {
            bind(HelloWorldImpl.class).to(HelloWorld.class);
        } else {
            bind(HelloWorldDemo.class).to(HelloWorld.class);
        }
        bind(HelloWorldImpl.class).named(QUALIFIER_IMPL).to(HelloWorld.class);
        bind(HelloWorldDemo.class).named(QUALIFIER_DEMO).to(HelloWorld.class);
        bind(HelloWorldImpl.class).qualifiedBy(new ImplQualifierLiteral()).to(HelloWorld.class);
        bind(HelloWorldImpl.class).qualifiedBy(new DemoQualifierLiteral()).to(HelloWorld.class);
    }
}
