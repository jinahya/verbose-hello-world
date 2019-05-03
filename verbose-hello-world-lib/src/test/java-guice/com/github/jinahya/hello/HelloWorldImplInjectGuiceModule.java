package com.github.jinahya.hello;

import com.google.inject.AbstractModule;

import static com.github.jinahya.hello.HelloWorldImplInjectTest.QUALIFIER_DEMO;
import static com.github.jinahya.hello.HelloWorldImplInjectTest.QUALIFIER_IMPL;
import static com.google.inject.name.Names.named;
import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A Guice module for injecting {@link HelloWorld} instances.
 */
class HelloWorldImplInjectGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HelloWorld.class).to(current().nextBoolean() ? HelloWorldImpl.class : HelloWorldDemo.class);
        bind(HelloWorld.class).annotatedWith(named(QUALIFIER_IMPL)).to(HelloWorldImpl.class);
        bind(HelloWorld.class).annotatedWith(named(QUALIFIER_DEMO)).to(HelloWorldDemo.class);
        bind((HelloWorld.class)).annotatedWith(ImplQualifier.class).to(HelloWorldImpl.class);
        bind((HelloWorld.class)).annotatedWith(DemoQualifier.class).to(HelloWorldDemo.class);
    }
}
