package com.github.jinahya.hello;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

class HelloWorldImplInjectHk2Binder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(HelloWorld.class).to(HelloWorldImpl.class);
    }
}
