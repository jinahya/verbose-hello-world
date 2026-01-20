package com.github.jinahya.hello.api;

import java.io.OutputStream;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public interface AsynchronousHelloWorld {

    static AsynchronousHelloWorld from(final HelloWorld underlying, final Executor executor) {
        return new DefaultAsynchronousHelloWorld(underlying, executor);
    }

    static AsynchronousHelloWorld from(final HelloWorld underlying) {
        return from(underlying, Executors.newVirtualThreadPerTaskExecutor());
    }

    // ----------------------------------------------------------------------------------- java.lang
    CompletionStage<byte[]> set(byte[] array, int index);

    default CompletionStage<byte[]> set(final byte[] array) {
        HelloWorldValidator.requireValid(array);
        return set(array, 0);
    }

    <T extends Appendable> CompletionStage<T> append(final T appendable);

    // ------------------------------------------------------------------------------------- java.io
    <T extends OutputStream> CompletionStage<T> write(final T stream);
}
