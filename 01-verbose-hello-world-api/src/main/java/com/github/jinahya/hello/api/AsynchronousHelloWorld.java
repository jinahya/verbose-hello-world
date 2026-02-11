package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;

public interface AsynchronousHelloWorld {

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Creates a new instance wrapping the specified service.
     *
     * @param service the service to wrap.
     * @return a new instance wrapping the {@code service}.
     * @throws NullPointerException if {@code service} is {@code null}.
     */
    static AsynchronousHelloWorld from(final HelloWorld service) {
        return new DefaultAsynchronousHelloWorld(service);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the specified mapper to the specified target asynchronously using the specified
     * executor, and returns the result as a {@link CompletionStage}.
     * <p>
     * Example usage:
     * {@snippet lang = "java":
     * var asyncHelloWorld = AsynchronousHelloWorld.from(helloWorld);
     * asyncHelloWorld.applyAsync(outputStream, HelloWorld::write, executor)
     *     .thenAccept(stream -> System.out.println("written"));
     *}
     *
     * @param <T>      target type parameter
     * @param target   the target to be passed to the {@code mapper}.
     * @param mapper   the mapper to apply; receives a {@link HelloWorld} instance and the
     *                 {@code target}, and returns a result.
     * @param executor the executor to use for async execution.
     * @return a {@link CompletionStage} representing the async operation.
     * @throws NullPointerException if {@code mapper} or {@code executor} is {@code null}.
     */
    <T> CompletionStage<T> applyAsync(T target,
                                      BiFunction<? super HelloWorld, ? super T, ? extends T> mapper,
                                      Executor executor);

    /**
     * Applies the specified mapper to the specified target asynchronously using
     * {@link ForkJoinPool#commonPool()}, and returns the result as a {@link CompletionStage}.
     *
     * @param <T>    target type parameter
     * @param target the target to be passed to the {@code mapper}.
     * @param mapper the mapper to apply; receives a {@link HelloWorld} instance and the
     *               {@code target}, and returns a result.
     * @return a {@link CompletionStage} representing the async operation.
     * @throws NullPointerException if {@code mapper} is {@code null}.
     * @implSpec Default implementation invokes {@link #applyAsync(Object, BiFunction, Executor)}
     * with the {@code target}, {@code mapper}, and {@link ForkJoinPool#commonPool()}.
     */
    default <T> CompletionStage<T> applyAsync(
            final T target,
            final BiFunction<? super HelloWorld, ? super T, ? extends T> mapper) {
        return applyAsync(target, mapper, ForkJoinPool.commonPool());
    }

    // ------------------------------------------------------------------------------- java.net.http
    CompletableFuture<WebSocket> send(WebSocket socket, boolean last);

    <T extends WebSocket> CompletableFuture<T> ping(T socket);

    <T extends WebSocket> CompletableFuture<T> pong(T socket);

    // --------------------------------------------------------------------------- java.nio.channels

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * channel, and then, notifies a completion (or a failure) to the specified handler with the
     * specified attachment.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(channel, "channel is null");
     * Objects.requireNonNull(handler, "handler is null");
     * final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
     * channel.write( // @highlight region
     *         buffer,                                    // <src>
     *         null,                                      // <attachment>
     *         new CompletionHandler<Integer, Object>() { // <handler>
     *                 @Override
     *                 public void completed(final Integer result, final Object a) {
     *                     if (!buffer.hasRemaining()) {
     *                         handler.completed(channel, attachment);
     *                         return;
     *                     }
     *                     channel.write(
     *                             buffer, // <src>
     *                             a,      // <attachment>
     *                             this    // <handler>
     *                     );
     *                 }
     *                 @Override
     *                 public void failed(final Throwable exc, final Object a) {
     *                     handler.failed(exc, attachment);
     *                 }
     *         }
     * ); // @end
     *}
     *
     * @param <T>        channel type parameter
     * @param channel    the channel to which bytes are written.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException either {@code channel} or {@code handler} is {@code null}.
     * @see HelloWorld#put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     */
    <T extends AsynchronousByteChannel, A>

    void write(
            final T channel,
            @Nullable final A attachment,
            final CompletionHandler<? super T, ? super A> handler);

    /**
     * Sends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * socket channel, and then, notifies a completion (or a failure) to the specified handler with
     * the specified attachment.
     *
     * @param channel    the channel to which the <a
     *                   href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> is sent.
     * @param attachment an attachment.
     * @param handler    the handler to be notified with a completion (or a failure).
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @implSpec Default implementation invokes
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler)} method with
     * {@code channel}, {@code attachment}, and {@code handler}.
     * @deprecated Invoke, directly, the
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler)} method with
     * {@code channel}, {@code attachment}, and {@code handler}.
     */
    @屋上架屋("AsynchronousSocketChannel implements AsynchronousByteChannel")
    @Deprecated(forRemoval = true)
    default <T extends AsynchronousSocketChannel, A> void send(
            final T channel, final A attachment,
            final CompletionHandler<? super T, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        write(channel, attachment, handler);
    }

    /**
     * Writes, asynchronously, the <a href="#hello-world-bytes">hello-world-bytes</a> to the
     * specified channel, starting at the specified position, and notifies a completion (or a
     * failure) to the specified handler.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the asynchronous file channel to which bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param attachment an attachment for the {@code handler}; may be {@code null}.
     * @param handler    the handler.
     * @throws NullPointerException     if either {@code channel} or {@code handler} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code position} is negative.
     * @see AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     */
    // @formatter:off
    <T extends AsynchronousFileChannel, A> void write(
            final T channel,
            final long position,
            final @Nullable A attachment,
            final CompletionHandler<? super T, ? super A> handler);

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the end of the specified
     * path to a file, and notifies a completion (or a failure) to the specified handler.
     *
     * @param path       the path to a file to which the bytes are appended.
     * @param attachment an attachment for the handler.
     * @param handler    the handler to be notified with a completion (or a failure).
     * @param <T>        path type parameter
     * @param <A>        attachment type parameter
     * @throws IOException if an I/O error occurs.
     */
    default <T extends Path, A> void append(final T path, @Nullable final A attachment,
                                            final CompletionHandler<? super T, ? super A> handler)
            throws IOException {
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(handler, "handler is null");
        final var options = new OpenOption[] {
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
        };
        @SuppressWarnings({
                "java:S2095" // Resources should be closed
        })
        final var channel = AsynchronousFileChannel.open(path, options); // no try-with-resources?
        write(channel, channel.size(), attachment, new CompletionHandler<>() { // @formatter:off
            @Override public void completed(final AsynchronousFileChannel r, final A a) {
                assert r == channel;
                try {
                    r.force(true);
                    r.close();
                } catch (final IOException ioe) {
                    handler.failed(ioe, a);
                    return;
                }
                handler.completed(path, a);
            }
            @Override public void failed(final Throwable t, final A a) {
                try {
                    channel.close();
                } catch (final IOException ioe) {
                    handler.failed(ioe, a);
                    return;
                }
                handler.failed(t, a);
            } // @formatter:on
        });
    }
}
