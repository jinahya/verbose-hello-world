package com.github.jinahya.hello;

import java.util.ServiceLoader;

/**
 * A class for helping {@link HelloWorldServer} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class HelloWorldServerHelper {

    private static volatile HelloWorld service;

    /**
     * Loads an instance of {@link HelloWorld} interface.
     *
     * @return an instance of {@link HelloWorld} interface.
     */
    static HelloWorld service() {
        var result = service;
        if (result == null) {
            service = result = ServiceLoader.load(HelloWorld.class).iterator().next();
        }
        return result;
    }

    private HelloWorldServerHelper() {
        throw new AssertionError("instantiation is not allowed");
    }
}
