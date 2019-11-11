package com.github.jinahya.hello;

import com.google.inject.AbstractModule;

import static com.github.jinahya.hello.HelloWorldDiTest.DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest.IMPL;
import static com.google.inject.name.Names.named;

/**
 * A Guice module for injecting {@link HelloWorld} instances.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorldDiGuiceModule extends AbstractModule {

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    protected void configure() {
        bind(HelloWorld.class).annotatedWith(named(DEMO)).to(HelloWorldDemo.class);
        bind(HelloWorld.class).annotatedWith(named(IMPL)).to(HelloWorldImpl.class);
        bind((HelloWorld.class)).annotatedWith(QualifiedDemo.class).to(HelloWorldDemo.class);
        bind((HelloWorld.class)).annotatedWith(QualifiedImpl.class).to(HelloWorldImpl.class);
    }
}
