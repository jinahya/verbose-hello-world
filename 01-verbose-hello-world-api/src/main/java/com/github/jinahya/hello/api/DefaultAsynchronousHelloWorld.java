package com.github.jinahya.hello.api;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

class DefaultAsynchronousHelloWorld implements AsynchronousHelloWorld {

    @FunctionalInterface
    private interface ThrowableSupplier<R, T extends Throwable> {

        R get() throws T;
    }

    private static <R> R execute(final ThrowableSupplier<R, IOException> supplier) {
        try {
            return supplier.get();
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    DefaultAsynchronousHelloWorld(final HelloWorld service, final Executor executor) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
        this.executor = Objects.requireNonNull(executor, "executor is null");
    }

    // ----------------------------------------------------------------------------------- java.lang

    @Override
    public CompletionStage<byte[]> set(final byte[] array, final int index) {
        HelloWorldValidator.requireValid(array, index);
        return CompletableFuture.supplyAsync(
                () -> service.set(array, index), executor
        );
    }

    // TODO: remove; just calls super implementation
    @Override
    public CompletionStage<byte[]> set(final byte[] array) {
        return AsynchronousHelloWorld.super.set(array);
    }

    @Override
    public <T extends Appendable> CompletionStage<T> append(final T appendable) {
        Objects.requireNonNull(appendable, "appendable is null");
        return CompletableFuture.supplyAsync(
                () -> execute(() -> service.append(appendable)),
                executor
        );
    }

    // ------------------------------------------------------------------------------------- java.io
    @Override
    public <T extends OutputStream> CompletionStage<T> write(final T stream) {
        Objects.requireNonNull(stream, "stream is null");
        return CompletableFuture.supplyAsync(
                () -> execute(() -> service.write(stream)),
                executor
        );
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;

    private final Executor executor;
}
