package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

class DefaultAsynchronousHelloWorld implements AsynchronousHelloWorld {

    private static final System.Logger logger =
            System.getLogger(MethodHandles.lookup().lookupClass().getName());

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    DefaultAsynchronousHelloWorld(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public <T> CompletionStage<T> applyAsync(
            final T target,
            final BiFunction<? super HelloWorld, ? super T, ? extends T> mapper,
            final Executor executor) {
        Objects.requireNonNull(mapper, "mapper is null");
        Objects.requireNonNull(executor, "executor is null");
        return CompletableFuture.supplyAsync(
                () -> mapper.apply(service, target),
                executor
        );
    }

    // ------------------------------------------------------------------------------- java.net.http
    @Override
    public CompletableFuture<WebSocket> sendBinary(final WebSocket socket, final boolean last) {
        Objects.requireNonNull(socket, "socket is null");
        final var buffer = ByteBuffer.allocate(HelloWorld.BYTES);
        service.put(buffer);
        buffer.flip();
        return socket.sendBinary(buffer, last);
    }

    @Override
    public CompletableFuture<WebSocket> sendPing(WebSocket socket) {
        return null;
    }

    @Override
    public CompletableFuture<WebSocket> sendPong(WebSocket socket) {
        return null;
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

    @Override
    // @formatter:off
    public <T extends AsynchronousFileChannel, A> void write(
            final T channel,
            final long position,
            final @Nullable A attachment,
            final CompletionHandler<? super T, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(handler, "handler is null");
        // get the <hello-world-bytes>
        final var buffer = service.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip();
        // write the <buffer> to the <channel>
        channel.write(
                buffer,                     // <src>
                position,                   // <position>
                position,                   // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override public void completed(final Integer r, final Long p) {
                        assert r > 0; // why?
                        if (!buffer.hasRemaining()) {
                            handler.completed(channel, attachment);
                            return;
                        }
                        final var position = p + r;
                        channel.write(
                                buffer,   // <src>
                                position, // <position>
                                position, // <attachment>
                                this      // <handler>
                        );
                    }
                    @Override public void failed(final Throwable t, final Long p) {
                        handler.failed(t, attachment);
                    }
                }
        );
    } // @formatter:on

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;
}
