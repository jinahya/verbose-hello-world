package com.github.jinahya.hello;

import java.util.Optional;

/**
 * An implementation of {@link HelloWorldProvider} for providing instances of {@link HelloWorldImpl}.
 */
class HelloWorldProviderImpl implements HelloWorldProvider {

    @Override
    public Optional<HelloWorld> provideHelloWorld() {
        return Optional.of(new HelloWorldImpl());
    }
}
