package com.github.jinahya.hello;

import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

import static com.github.jinahya.hello.HelloWorldDiTest.DEMO;
import static com.github.jinahya.hello.HelloWorldDiTest.IMPL;

@Module
class HelloWorldDiDaggerModule {

    @Provides
    @Named(DEMO)
    static HelloWorld provideNamedDemo() {
        return new HelloWorldDemo();
    }

    @Provides
    @Named(IMPL)
    static HelloWorld provideNamedImpl() {
        return new HelloWorldImpl();
    }

    @Provides
    @QualifiedDemo
    static HelloWorld provideQualifiedDemo() {
        return new HelloWorldDemo();
    }

    @Provides
    @QualifiedImpl
    static HelloWorld provideQualifiedImpl() {
        return new HelloWorldImpl();
    }
}
