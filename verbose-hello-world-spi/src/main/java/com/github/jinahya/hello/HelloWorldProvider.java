package com.github.jinahya.hello;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.ServiceLoader.load;

/**
 * An interface for hello world provider.
 */
public interface HelloWorldProvider {

    /**
     * Returns a stream of hello world providers filtered by {@link #isAvailable()}.
     *
     * @return a stream of hello world providers
     */
    static Stream<HelloWorldProvider> availableProviders() {
        return StreamSupport.stream(load(HelloWorldProvider.class).spliterator(), false)
                .filter(HelloWorldProvider::isAvailable);
    }

    static HelloWorld findAnyAvailableAndGetOrElseThrow() {
        return availableProviders().findAny().orElseThrow(() -> new RuntimeException("no available services"))
                .getAvailable();
    }

    /**
     * Returns a boolean flag for availability of provider.
     *
     * @return {@code true} if available, {@code false} otherwise.
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * Provides an instance of {@link HelloWorld}.
     *
     * @return an instance of {@link HelloWorld}.
     */
    HelloWorld getAvailable();
}
