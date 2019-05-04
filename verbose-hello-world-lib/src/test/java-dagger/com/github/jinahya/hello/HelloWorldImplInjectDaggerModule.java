package com.github.jinahya.hello;

import dagger.Module;
import dagger.Provides;

import javax.inject.Named;

import static com.github.jinahya.hello.HelloWorldImplInjectTest.DEMO;
import static com.github.jinahya.hello.HelloWorldImplInjectTest.IMPL;

@Module
class HelloWorldImplInjectDaggerModule {

    @Provides
    @Named(IMPL)
    static HelloWorld provideNamedImpl() {
        return new HelloWorldImpl();
    }

    @Provides
    @Named(DEMO)
    static HelloWorld provideNamedDemo() {
        return new HelloWorldDemo();
    }

    @Provides
    @QualifiedImpl
    static HelloWorld provideQualifiedImpl() {
        return new HelloWorldImpl();
    }

    @Provides
    @QualifiedDemo
    static HelloWorld provideQualifiedDemo() {
        return new HelloWorldDemo();
    }
}
