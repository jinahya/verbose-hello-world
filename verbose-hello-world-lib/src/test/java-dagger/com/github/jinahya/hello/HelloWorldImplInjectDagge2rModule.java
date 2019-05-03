package com.github.jinahya.hello;

import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

import static java.util.concurrent.ThreadLocalRandom.current;

@Module
class HelloWorldImplInjectDagge2rModule {

    @Provides
    static HelloWorld provideHelloWorld() {
        return current().nextBoolean() ? new HelloWorldImpl() : new HelloWorldDemo();
    }

    @Named(HelloWorldImplInjectTest.QUALIFIER_IMPL)
    @Provides
    static HelloWorld providesNmedImpl() {
        return new HelloWorldImpl();
    }

    @Named(HelloWorldImplInjectTest.QUALIFIER_DEMO)
    @Provides
    static HelloWorld providesNamedDemo() {
        return new HelloWorldDemo();
    }

    @ImplQualifier
    @Provides
    static HelloWorld providesQualifiedImpl() {
        return new HelloWorldImpl();
    }

    @DemoQualifier
    @Provides
    static HelloWorld providesQualifiedDemo() {
        return new HelloWorldDemo();
    }
}
