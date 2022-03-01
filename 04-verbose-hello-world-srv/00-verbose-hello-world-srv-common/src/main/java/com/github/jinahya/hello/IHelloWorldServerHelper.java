package com.github.jinahya.hello;

import java.util.ServiceLoader;

class IHelloWorldServerHelper {

    private static HelloWorld service;

    static HelloWorld helloWorld() {
        if (service == null) {
            service = ServiceLoader.load(HelloWorld.class).iterator().next();
        }
        return service;
    }

    private IHelloWorldServerHelper() {
        throw new AssertionError("instantiation is not allowed");
    }
}
