package com.github.jinahya.hello;

import com.google.inject.AbstractModule;

class HelloWorldModule extends AbstractModule {

    static final Class<?> HELLO_WORLD_IMPL;

    static {
        try {
            HELLO_WORLD_IMPL = Class.forName("com.github.jinahya.hello.HelloWorldImpl");
        } catch (final ClassNotFoundException cnfe) {
            throw new InstantiationError(cnfe.getMessage());
        }
    }

    @Override
    protected void configure() {
        // TODO: implement!
    }
}
