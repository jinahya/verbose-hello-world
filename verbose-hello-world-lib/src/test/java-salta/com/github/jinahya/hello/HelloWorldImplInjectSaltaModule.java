package com.github.jinahya.hello;

import com.github.ruediste.salta.jsr330.AbstractModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

import static com.github.jinahya.hello.HelloWorldImplInjectTest.QUALIFIER_DEMO;
import static com.github.jinahya.hello.HelloWorldImplInjectTest.QUALIFIER_IMPL;
import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A binder for injecting {@link HelloWorld}s.
 */
class HelloWorldImplInjectSaltaModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    protected void configure() {
        bind(HelloWorld.class).to(current().nextBoolean() ? HelloWorldImpl.class : HelloWorldDemo.class);
        bind(HelloWorld.class).named(QUALIFIER_IMPL).to(HelloWorldImpl.class);
        bind(HelloWorld.class).named(QUALIFIER_DEMO).to(HelloWorldDemo.class);
        bind(HelloWorld.class).annotatedWith(ImplQualifier.class).to(HelloWorldImpl.class);
        bind(HelloWorld.class).annotatedWith(DemoQualifier.class).to(HelloWorldDemo.class);
    }
}
