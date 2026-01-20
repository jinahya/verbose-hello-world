package com.github.jinahya.hello.api;

import java.util.concurrent.CompletableFuture;

public interface AsynchronousHelloWorld {

    static AsynchronousHelloWorld from(final HelloWorld delegate) {
        return new DefaultAsynchronousHelloWorld(delegate);
    }

    // ---------------------------------------------------------------------------------------------
    CompletableFuture<byte[]> set(byte[] array, int index);

    default CompletableFuture<byte[]> set(final byte[] array) {
        HelloWorldValidator.requireValid(array);
        return set(array, 0);
    }
}
