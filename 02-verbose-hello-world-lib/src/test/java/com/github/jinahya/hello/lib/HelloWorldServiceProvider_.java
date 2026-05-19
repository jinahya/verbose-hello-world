package com.github.jinahya.hello.lib;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.spi.HelloWorldServiceProvider;

import java.util.Objects;

abstract class HelloWorldServiceProvider_ implements HelloWorldServiceProvider {

    HelloWorldServiceProvider_(final Class<? extends HelloWorld> clazz) {
        super();
        this.clazz = Objects.requireNonNull(clazz, "clazz is null");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '@' + String.format("%08x", hashCode());
    }

    @Override
    public HelloWorld getService() {
        var result = service;
        if (result == null) {
            try {
                final var constructor = clazz.getDeclaredConstructor();
                if (!constructor.canAccess(this)) {
                    constructor.setAccessible(true);
                }
                result = service = constructor.newInstance();
            } catch (final ReflectiveOperationException roe) {
                throw new RuntimeException("failed initialize " + clazz, roe);
            }
        }
        return result;
    }

    // ---------------------------------------------------------------------------------------------
    private final Class<? extends HelloWorld> clazz;

    private volatile HelloWorld service;
}
