package com.github.jinahya.hello;

import java.util.ServiceLoader;

final class IHelloWorldServerHelper {

    private static HelloWorld service;

    static HelloWorld service() {
        if (service == null) {
            service = ServiceLoader.load(HelloWorld.class).iterator().next();
        }
        return service;
    }

    private IHelloWorldServerHelper() {
        throw new AssertionError("instantiation is not allowed");
    }
}
