package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

class DefaultAsynchronousHelloWorld implements AsynchronousHelloWorld {

    private static final System.Logger logger =
            System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // ---------------------------------------------------------------------------------------------
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

    @Override
    public <T extends Writer> CompletionStage<T> write(final T writer) {
        Objects.requireNonNull(writer, "writer is null");
        return CompletableFuture.supplyAsync(
                () -> execute(() -> service.write(writer)),
                executor
        );
    }

    // ------------------------------------------------------------------------------------- more java.io

    @Override
    public <T extends java.io.DataOutput> CompletionStage<T> write(final T output) {
        Objects.requireNonNull(output, "output is null");
        return CompletableFuture.supplyAsync(
                () -> execute(() -> service.write(output)),
                executor
        );
    }

    @Override
    public <T extends java.io.RandomAccessFile> CompletionStage<T> write(final T file) {
        Objects.requireNonNull(file, "file is null");
        return CompletableFuture.supplyAsync(
                () -> execute(() -> service.write(file)),
                executor
        );
    }

    // ------------------------------------------------------------------------------------- java.net

    @Override
    public <T extends java.net.Socket> CompletionStage<T> send(final T socket) {
        Objects.requireNonNull(socket, "socket is null");
        return CompletableFuture.supplyAsync(
                () -> execute(() -> service.send(socket)),
                executor
        );
    }

    // ------------------------------------------------------------------------------------- java.nio

    @Override
    public <T extends java.nio.ByteBuffer> CompletionStage<T> put(final T buffer) {
        Objects.requireNonNull(buffer, "buffer is null");
        return CompletableFuture.supplyAsync(
                () -> service.put(buffer),
                executor
        );
    }

    @Override
    public <T extends java.nio.channels.WritableByteChannel> CompletionStage<T> write(
            final T channel) {
        Objects.requireNonNull(channel, "channel is null");
        return CompletableFuture.supplyAsync(
                () -> execute(() -> service.write(channel)),
                executor
        );
    }

    @Override
    public <T extends java.nio.file.Path> CompletionStage<T> append(final T path) {
        Objects.requireNonNull(path, "path is null");
        return CompletableFuture.supplyAsync(
                () -> execute(() -> service.append(path)),
                executor
        );
    }

    // --------------------------------------------------------------------------- java.nio.channels
    @Override
    public <T extends AsynchronousByteChannel, A> void write(
            final T channel,
            final @Nullable A attachment,
            final CompletionHandler<? super T, ? super A> handler) { // @formatter:off
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        final var buffer = service.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip();
        channel.write(
                buffer,                     // <src>
                attachment,                 // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(final Integer result, final A attachment) {
                        logger.log(System.Logger.Level.DEBUG, "completed({0}, {1})", result,
                                   attachment);
                        if (!buffer.hasRemaining()) {               // <1>
                            handler.completed(channel, attachment); // <2>
                            return;                                 // <3>
                        }
                        channel.write(                              // <4>
                                buffer,     // <src>
                                attachment, // <attachment>
                                this        // <handler>            // <5>
                        );
                    }
                    @Override
                    public void failed(final Throwable exc, final A attachment) {
                        handler.failed(exc, attachment); // <1>
                    }
                }
        ); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;

    private final Executor executor;
}
