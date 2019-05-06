package com.github.jinahya.hello;

/**
 * An interface for hello world provider.
 */
public interface HelloWorldProvider {

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
