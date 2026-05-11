package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * A default implementation of {@link AsynchronousHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class DefaultAsynchronousHelloWorld
        implements AsynchronousHelloWorld {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance with the specified service.
     *
     * @param service the service for the <em>hello-world-bytes</em>.
     */
    DefaultAsynchronousHelloWorld(final HelloWorld service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public <R> CompletionStage<R> applyAsync(
            final Function<? super HelloWorld, ? extends R> mapper,
            final Executor executor) {
        Objects.requireNonNull(mapper, "mapper is null");
//        Objects.requireNonNull(executor, "executor is null");
        return CompletableFuture.supplyAsync(
                () -> mapper.apply(service),
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
    @SuppressWarnings({"java:S117"})
    public <T extends AsynchronousByteChannel, A>
    void write(final T channel,
               @Nullable final A attachment,
               final CompletionHandler<? super T, ? super A> handler) { // @formatter:off
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        final var src = service.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip();
        channel.write(
                src,                        // <src>
                attachment,                 // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(final Integer result, final A attachment) {
                        if (!src.hasRemaining()) {
                            handler.completed(channel, attachment);
                            return;
                        }
                        channel.write(src, attachment, this);
                    }
                    @Override
                    public void failed(final Throwable exc, final A attachment) {
                        handler.failed(exc, attachment);
                    }
                }
        ); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    @SuppressWarnings({"java:S117"})
    public <T extends AsynchronousFileChannel, A>
    void write(final T channel,
               final long position,
               final @Nullable A attachment,
               final CompletionHandler<? super T, ? super A> handler) { // @formatter:off
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(handler, "handler is null");
        final var buffer = service.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip();
        channel.write(
                buffer,                     // <src>
                position,                   // <position>
                position,                   // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(final Integer result, Long p_) {
                        assert result > 0; // why?
                        if (!buffer.hasRemaining()) {
                            handler.completed(channel, attachment);
                            return;
                        }
                        channel.write(
                                buffer,       // <src>
                                p_ += result, // <position>
                                p_,           // <attachment>
                                this          // <handler>
                        );
                    }
                    @Override
                    public void failed(final Throwable t, final Long p_) {
                        handler.failed(t, attachment);
                    }
                }
        ); // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;
}
