package com.github.jinahya.hello;

import com.google.inject.AbstractModule;
import org.slf4j.Logger;

import static com.github.jinahya.hello.HelloWorldDiTest.DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest.IMPL;
import static com.google.inject.name.Names.named;
import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * A Guice module for injecting {@link HelloWorld} instances.
 */
class HelloWorldDiGuiceModule extends AbstractModule {

    private static final Logger logger = getLogger(lookup().lookupClass());

    @Override
    protected void configure() {
        bind(HelloWorld.class).annotatedWith(named(DEMO)).to(HelloWorldDemo.class);
        bind(HelloWorld.class).annotatedWith(named(IMPL)).to(HelloWorldImpl.class);
        bind((HelloWorld.class)).annotatedWith(QualifiedDemo.class).to(HelloWorldDemo.class);
        bind((HelloWorld.class)).annotatedWith(QualifiedImpl.class).to(HelloWorldImpl.class);
    }
}
