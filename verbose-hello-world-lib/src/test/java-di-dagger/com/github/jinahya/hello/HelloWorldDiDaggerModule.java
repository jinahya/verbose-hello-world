package com.github.jinahya.hello;

import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

import static com.github.jinahya.hello.HelloWorldDiTest.DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest.IMPL;

@Module
class HelloWorldDiDaggerModule {

    @Named(DEMO)
    @Provides
    static HelloWorld provideNamedDemo() {
        return new HelloWorldDemo();
    }

    @Named(IMPL)
    @Provides
    static HelloWorld provideNamedImpl() {
        return new HelloWorldImpl();
    }

    @QualifiedDemo
    @Provides
    static HelloWorld provideQualifiedDemo() {
        return new HelloWorldDemo();
    }

    @QualifiedImpl
    @Provides
    static HelloWorld provideQualifiedImpl() {
        return new HelloWorldImpl();
    }
}
