package com.github.jinahya.hello;

import com.google.inject.AbstractModule;

class HelloWorldImplInjectGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(HelloWorld.class).to(HelloWorldImpl.class);
    }
}
