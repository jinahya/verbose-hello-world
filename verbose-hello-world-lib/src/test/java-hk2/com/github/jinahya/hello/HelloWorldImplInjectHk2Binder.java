package com.github.jinahya.hello;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

class HelloWorldImplInjectHk2Binder extends AbstractBinder {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    protected void configure() {
        bind(HelloWorldImpl.class).to(HelloWorld.class);
    }
}
