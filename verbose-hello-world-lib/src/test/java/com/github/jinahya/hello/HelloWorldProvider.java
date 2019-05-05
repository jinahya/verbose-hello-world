package com.github.jinahya.hello;

import java.util.Optional;

/**
 * An interface for hello world providers.
 */
interface HelloWorldProvider {

    /**
     * Returns an optional of {@link HelloWorld}.
     *
     * @return an optional of {@link HelloWorld}.
     */
    Optional<HelloWorld> provideHelloWorld();
}
