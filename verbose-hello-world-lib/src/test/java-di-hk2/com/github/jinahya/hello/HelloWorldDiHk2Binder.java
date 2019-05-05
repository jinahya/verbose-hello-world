package com.github.jinahya.hello;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static com.github.jinahya.hello.HelloWorldDiTest.DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest.IMPL;

/**
 * A binder for injecting {@link HelloWorld}s.
 */
class HelloWorldDiHk2Binder extends AbstractBinder {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static class QualifiedDemo_ extends AnnotationLiteral<QualifiedDemo> implements QualifiedDemo {

        private static final long serialVersionUID = 8947668889394516822L;
    }

    private static class QualifiedImpl_ extends AnnotationLiteral<QualifiedImpl> implements QualifiedImpl {

        private static final long serialVersionUID = 9084623087464727990L;
    }

    @Override
    protected void configure() {
        bind(HelloWorldDemo.class).named(DEMO).to(HelloWorld.class);
        bind(HelloWorldImpl.class).named(IMPL).to(HelloWorld.class);
        bind(HelloWorldImpl.class).qualifiedBy(new QualifiedDemo_()).to(HelloWorld.class);
        bind(HelloWorldImpl.class).qualifiedBy(new QualifiedImpl_()).to(HelloWorld.class);
    }
}
