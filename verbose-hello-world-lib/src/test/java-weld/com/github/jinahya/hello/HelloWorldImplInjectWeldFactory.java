package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import java.lang.invoke.MethodHandles;

import static com.github.jinahya.hello.HelloWorldImplInjectTest.DEMO;
import static com.github.jinahya.hello.HelloWorldImplInjectTest.IMPL;

class HelloWorldImplInjectWeldFactory {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Produces
    @Named(IMPL)
    HelloWorld produceNamedImpl(final InjectionPoint injectionPoint) {
        final HelloWorld helloWorld = new HelloWorldImpl();
        logger.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    void disposeNamedImpl(@Disposes @Named(IMPL) final HelloWorld helloWorld) {
        logger.debug("disposing {}", helloWorld);
    }

    @Produces
    @Named(DEMO)
    HelloWorld produceNamedDemo(final InjectionPoint injectionPoint) {
        final HelloWorld helloWorld = new HelloWorldDemo();
        logger.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    void disposeNamedDemo(@Disposes @Named(DEMO) final HelloWorld helloWorld) {
        logger.debug("disposing {}", helloWorld);
    }

    @Produces
    @QualifiedImpl
    HelloWorld produceQualifiedImpl(final InjectionPoint injectionPoint) {
        final HelloWorld helloWorld = new HelloWorldImpl();
        logger.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    void disposeQualifiedImpl(@Disposes @QualifiedImpl final HelloWorld helloWorld) {
        logger.debug("disposing {}", helloWorld);
    }

    @Produces
    @QualifiedDemo
    HelloWorld produceQualifiedDemo(final InjectionPoint injectionPoint) {
        final HelloWorld helloWorld = new HelloWorldDemo();
        logger.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    void disposeQualifiedDemo(@Disposes @QualifiedDemo final HelloWorld helloWorld) {
        logger.debug("disposing {}", helloWorld);
    }
}
