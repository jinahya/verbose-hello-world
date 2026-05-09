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
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;

/**
 * A default implementation of {@link AsynchronousHelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class DefaultAsynchronousHelloWorld
        implements AsynchronousHelloWorld {

    /**
     * Drains the specified, already-prepared {@link ByteBuffer} to the specified asynchronous byte
     * channel by recursively invoking
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler) channel.write}
     * until the {@code buffer} has no remaining bytes, then notifies
     * {@code handler.completed(channel, attachment)} on success or
     * {@code handler.failed(exc, attachment)} on failure.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param buffer     the buffer to drain; must already be flipped and contain the bytes to
     *                   write.
     * @param channel    the channel to write to.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified.
     */
    @SuppressWarnings({"java:S117"})
    private static <T extends AsynchronousByteChannel, A>
    void write(final ByteBuffer buffer,
               final T channel,
               final @Nullable A attachment,
               final CompletionHandler<? super T, ? super A> handler) {
        channel.write( // @formatter:off
                buffer,                     // <src>
                attachment,                 // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(final Integer result, final A attachment) {
                        if (!buffer.hasRemaining()) {
                            handler.completed(channel, attachment);
                            return;
                        }
                        channel.write(buffer, attachment, this);
                    }
                    @Override
                    public void failed(final Throwable exc, final A attachment) {
                        handler.failed(exc, attachment);
                    }
                } // @formatter:on
        );
    }

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
        Objects.requireNonNull(executor, "executor is null");
        return CompletableFuture.supplyAsync(
                () -> mapper.apply(service),
                executor
        );
    }

    @Override
    public <R> CompletionStage<R> applyAsync(
            final Function<? super HelloWorld, ? extends R> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return applyAsync(mapper, ForkJoinPool.commonPool());
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
    public <T extends AsynchronousByteChannel, A>
    void write(final T channel,
               @Nullable final A attachment,
               final CompletionHandler<? super T, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        final var buffer = service.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip();
        write(buffer, channel, attachment, handler);
    }

    @Deprecated(forRemoval = true)
    @Override
    public <T extends AsynchronousByteChannel, A>
    void writeOn(final T channel,
                 final @Nullable A attachment,
                 final CompletionHandler<? super T, ? super A> handler,
                 final Executor executor) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        Objects.requireNonNull(executor, "executor is null");
        applyAsync(
                s -> s.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip(),
                executor
        ).thenAcceptAsync(
                buffer -> write(buffer, channel, attachment, handler),
                executor
        ).exceptionally(t -> {
            handler.failed(t, attachment);
            return null;
        });
    }

//    @Override
//    public <T extends AsynchronousByteChannel, A>
//    void write(final Executor executor,
//               final T channel,
//               @Nullable final A attachment,
//               final CompletionHandler<? super T, ? super A> handler) {
//        Objects.requireNonNull(executor, "executor is null");
//        Objects.requireNonNull(channel, "channel is null");
//        Objects.requireNonNull(handler, "handler is null");
//        applyAsync(
//                s -> s.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip(),
//                executor
//        ).thenAcceptAsync(
//                b -> {
//                    channel.write( // @formatter:on
//                            b,                          // <src>
//                            attachment,                 // <attachment>
//                            new CompletionHandler<>() { // <handler>
//                                @Override
//                                public void completed(final Integer result, final A attachment) {
//                                    if (!b.hasRemaining()) {
//                                        handler.completed(channel, attachment);
//                                        return;
//                                    }
//                                    channel.write(b, attachment, this);
//                                }
//
//                                @Override
//                                public void failed(final Throwable exc, final A attachment) {
//                                    handler.failed(exc, attachment);
//                                }
//                            } // @formatter:on
//                    );
//                },
//                executor
//        ).exceptionally(t -> {
//            handler.failed(t, attachment);
//            return null;
//        });
//    }

    @Override
    @SuppressWarnings({"java:S117"})
    public <T extends AsynchronousFileChannel, A>
    void write(final T channel,
               final long position,
               final @Nullable A attachment,
               final CompletionHandler<? super T, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(handler, "handler is null");
        final var buffer = service.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip();
        channel.write( // @formatter:off
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
                } // @formater:on
        );
    } // @formatter:on

//    @Override
//    public <T extends AsynchronousFileChannel, A>
//    void write(final Executor executor,
//               final T channel,
//               final long position,
//               final @Nullable A attachment,
//               final CompletionHandler<? super T, ? super A> handler) {
//        Objects.requireNonNull(executor, "executor is null");
//        Objects.requireNonNull(channel, "channel is null");
//        if (position < 0L) {
//            throw new IllegalArgumentException("position(" + position + ") is negative");
//        }
//        Objects.requireNonNull(handler, "handler is null");
//        applyAsync(
//                HelloWorld::byteBuffer,
//                executor
//        ).thenAcceptAsync(b -> {
//            channel.write( // @formatter:off
//                    b,                          // <src>
//                    position,                   // <position>
//                    position,                   // <attachment>
//                    new CompletionHandler<>() { // <handler>
//                        @Override
//                        public void completed(final Integer result, final Long p) {
//                            if (!b.hasRemaining()) {
//                                handler.completed(channel, attachment);
//                                return;
//                            }
//                            final var next = p + result;
//                            channel.write(b, next, next, this);
//                        }
//                        @Override
//                        public void failed(final Throwable t, final Long p) {
//                            handler.failed(t, attachment);
//                        }
//                    } // @formatter:on
//            );
//        }, executor).exceptionally(t -> {
//            handler.failed(t, attachment);
//            return null;
//        });
//    }

    // ---------------------------------------------------------------------------------------------
    private final HelloWorld service;
}
