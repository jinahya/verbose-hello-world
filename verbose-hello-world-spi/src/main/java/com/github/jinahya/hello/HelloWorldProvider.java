package com.github.jinahya.hello;

import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An interface for hello world provider.
 */
public interface HelloWorldProvider {

    /**
     * Returns a stream of hello world providers.
     *
     * @return a stream of hello world providers
     */
    static Stream<HelloWorldProvider> providerStream() {
        final Iterable<HelloWorldProvider> loader = ServiceLoader.load(HelloWorldProvider.class);
        final Spliterator<HelloWorldProvider> spliterator = loader.spliterator();
        return StreamSupport.stream(spliterator, false);
    }

    /**
     * Find any available hello world provider and returns the value of {@link #getAvailable()}.
     *
     * @return an available instance of {@link HelloWorld}.
     */
    static HelloWorld findAnyAvailableAndGetOrElseThrow() {
        return providerStream()
                .filter(HelloWorldProvider::isAvailable)
                .findAny().orElseThrow(() -> new RuntimeException("no available services"))
                .getAvailable();
    }

    /**
     * Returns a boolean flag for availability of provider. The {@code default} {@code isAvailable} method of {@code
     * HelloWorldProvider} class returns {@code true}.
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
