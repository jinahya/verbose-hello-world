package com.github.jinahya.hello.api;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

class DefaultAsynchronousHelloWorld implements AsynchronousHelloWorld {

    DefaultAsynchronousHelloWorld(final HelloWorld delegate) {
        super();
        this.delegate = Objects.requireNonNull(delegate, "delegate is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public CompletableFuture<byte[]> set(final byte[] array) {
        HelloWorldValidator.requireValid(array);
        return CompletableFuture.supplyAsync(() -> delegate.set(array));
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld delegate;
}
