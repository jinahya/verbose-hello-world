package com.github.jinahya.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import java.lang.invoke.MethodHandles;

import static com.github.jinahya.hello.HelloWorldDiTest.DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest.IMPL;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A provider for {@link HelloWorld}.
 */
class HelloWorldCdiFactory {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point annotated with {@link Named} whose
     * {@link Named#value() value} equals to {@link HelloWorldDiTest#DEMO}.
     *
     * @param injectionPoint the injection point to be injected
     * @return an instance of {@link HelloWorld}.
     */
    @Produces
    @Named(DEMO)
    HelloWorld produceNamedDemo(final InjectionPoint injectionPoint) {
        final HelloWorld helloWorld = new HelloWorldDemo();
        logger.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for those injection points which each annotated with
     * {@link Named} whose {@link Named#value() value} equals to {@link HelloWorldDiTest#DEMO}.
     *
     * @param helloWorld the {@link HelloWorld} instance to dispose
     */
    void disposeNamedDemo(@Disposes @Named(DEMO) final HelloWorld helloWorld) {
        logger.debug("disposing {}", helloWorld);
        assertTrue(helloWorld instanceof HelloWorldDemo);
    }

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point annotated with {@link Named} whose
     * {@link Named#value() value} equals to {@link HelloWorldDiTest#IMPL}.
     *
     * @param injectionPoint the injection point to be injected
     * @return an instance of {@link HelloWorld}
     */
    @Produces
    @Named(IMPL)
    HelloWorld produceNamedImpl(final InjectionPoint injectionPoint) {
        final HelloWorld helloWorld = new HelloWorldImpl();
        logger.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for those injection points which each annotated with
     * {@link Named} whose {@link Named#value() value} equals to{@link HelloWorldDiTest#IMPL}.
     *
     * @param helloWorld the {@link HelloWorld} instance to dispose
     */
    void disposeNamedImpl(@Disposes @Named(IMPL) final HelloWorld helloWorld) {
        logger.debug("disposing {}", helloWorld);
        assertTrue(helloWorld instanceof HelloWorldImpl);
    }

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point annotated with {@link QualifiedDemo}.
     *
     * @param injectionPoint the injection point
     * @return an instance of {@link HelloWorld}
     */
    @Produces
    @QualifiedDemo
    HelloWorld produceQualifiedDemo(final InjectionPoint injectionPoint) {
        final HelloWorld helloWorld = new HelloWorldDemo();
        logger.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for those injection points which each annoatated with
     * {@link QualifiedDemo}.
     *
     * @param helloWorld the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedDemo(@Disposes @QualifiedDemo final HelloWorld helloWorld) {
        logger.debug("disposing {}", helloWorld);
        assertTrue(helloWorld instanceof HelloWorldDemo);
    }

    /**
     * Produces an instance of {@link HelloWorld} for specified injection point annotated with {@link QualifiedImpl}
     *
     * @param injectionPoint the injection point
     * @return an instance of {@link HelloWorld}
     */
    @Produces
    @QualifiedImpl
    HelloWorld produceQualifiedImpl(final InjectionPoint injectionPoint) {
        final HelloWorld helloWorld = new HelloWorldImpl();
        logger.debug("producing {} for {}", helloWorld, injectionPoint);
        return helloWorld;
    }

    /**
     * Disposes specified {@link HelloWorld} instance produced for those injection points which each annotated with
     * {@link QualifiedImpl}.
     *
     * @param helloWorld the {@link HelloWorld} instance to dispose
     */
    void disposeQualifiedImpl(@Disposes @QualifiedImpl final HelloWorld helloWorld) {
        logger.debug("disposing {}", helloWorld);
        assertTrue(helloWorld instanceof HelloWorldImpl);
    }
}
