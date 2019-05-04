package com.github.jinahya.hello;

import com.google.inject.AbstractModule;

import static com.github.jinahya.hello.HelloWorldImplInjectTest.DEMO;
import static com.github.jinahya.hello.HelloWorldImplInjectTest.IMPL;
import static com.google.inject.name.Names.named;
import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A Guice module for injecting {@link HelloWorld} instances.
 */
class HelloWorldImplInjectGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HelloWorld.class).to(current().nextBoolean() ? HelloWorldImpl.class : HelloWorldDemo.class);
        bind(HelloWorld.class).annotatedWith(named(IMPL)).to(HelloWorldImpl.class);
        bind(HelloWorld.class).annotatedWith(named(DEMO)).to(HelloWorldDemo.class);
        bind((HelloWorld.class)).annotatedWith(QualifiedImpl.class).to(HelloWorldImpl.class);
        bind((HelloWorld.class)).annotatedWith(QualifiedDemo.class).to(HelloWorldDemo.class);
    }
}
