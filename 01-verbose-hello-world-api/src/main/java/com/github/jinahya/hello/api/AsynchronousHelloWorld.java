package com.github.jinahya.hello.api;

import jakarta.validation.constraints.PositiveOrZero;
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

/**
 * .
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
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

    /**
     * Creates a new instance.
     *
     * @return a new instance.
     * @see #from(HelloWorld)
     */
    static AsynchronousHelloWorld newInstance() {
        return from(HelloWorldRevisited.newInstance());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the specified mapper to the specified target asynchronously using the specified
     * executor, and returns the result as a {@link CompletionStage}.
     * <p>
     * Example usage:
     * {@snippet lang = "java":
     * var instance = AsynchronousHelloWorld.newInstance();
     * instance.applyAsync(s, HelloWorld::write, executor)
     *         .thenAccept(s -> System.out.println("written"));
     *}
     *
     * @param <T>      target type parameter
     * @param target   the target to be passed to the {@code mapper}.
     * @param mapper   the mapper to apply; receives a {@link HelloWorld} instance and the
     *                 {@code target}, and returns a result.
     * @param executor the executor to use for async execution.
     * @return a {@link CompletionStage} representing the async operation.
     * @throws NullPointerException if {@code target}, {@code mapper} or {@code executor} is
     *                              {@code null}.
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
     * @throws NullPointerException if {@code target} or {@code mapper} is {@code null}.
     * @implSpec Default implementation invokes {@link #applyAsync(Object, BiFunction, Executor)}
     * with the {@code target}, {@code mapper}, and {@link ForkJoinPool#commonPool()}.
     * @see #applyAsync(Object, BiFunction, Executor)
     */
    default <T> CompletionStage<T> applyAsync(
            final T target,
            final BiFunction<? super HelloWorld, ? super T, ? extends T> mapper) {
        return applyAsync(target, mapper, ForkJoinPool.commonPool());
    }

    // ------------------------------------------------------------------------------- java.net.http

    /**
     * .
     *
     * @param socket .
     * @param last   .
     * @return .
     * @see WebSocket#sendBinary(ByteBuffer, boolean)
     */
    CompletableFuture<WebSocket> sendBinary(WebSocket socket, boolean last);

    CompletableFuture<WebSocket> sendPing(WebSocket socket);

    CompletableFuture<WebSocket> sendPong(WebSocket socket);

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
     * var buffer = put(ByteBuffer.allocate(BYTES)).flip();
     * channel.write( // @highlight region
     *         buffer,                                    // <src>
     *         attachment,                                // <attachment>
     *         new CompletionHandler<Integer, Object>() { // <handler>
     *                 @Override
     *                 public void completed(final Integer result, final Object a) {
     *                     if (!buffer.hasRemaining()) {
     *                         handler.completed(channel, a);
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
     *                     handler.failed(exc, a);
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
    void write(final T channel,
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
     * @deprecated The {@link AsynchronousSocketChannel} implements {@link AsynchronousByteChannel}.
     * Use {@link #write(AsynchronousByteChannel, Object, CompletionHandler)} method.
     */
    @Deprecated(forRemoval = true)
    @屋上架屋("AsynchronousSocketChannel implements AsynchronousByteChannel")
    default <T extends AsynchronousSocketChannel, A>
    void send(final T channel, final A attachment,
              final CompletionHandler<? super T, ? super A> handler) {
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
    <T extends AsynchronousFileChannel, A>
    void write(T channel,
               @PositiveOrZero long position,
               @Nullable A attachment,
               CompletionHandler<? super T, ? super A> handler);

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the end of the specified
     * path to a file, and notifies a completion (or a failure) to the specified handler.
     *
     * @param path       the path to a file to which the bytes are appended.
     * @param attachment an attachment for the handler.
     * @param handler    the handler.
     * @param <T>        path type parameter
     * @param <A>        attachment type parameter
     * @throws IOException          if an I/O error occurs.
     * @throws NullPointerException if either {@code path} or {@code handler} is {@code null}.
     * @implSpec The default implementation opens an {@link AsynchronousFileChannel} for the
     * specified {@code path} with {@link StandardOpenOption#CREATE CREATE} and
     * {@link StandardOpenOption#WRITE WRITE} options, and invokes the
     * {@link #write(AsynchronousFileChannel, long, Object, CompletionHandler)} method with the
     * channel, the channel's current {@link AsynchronousFileChannel#size() size} as the position,
     * the {@code attachment}, and a {@link CompletionHandler} that, on completion, forces the
     * channel, closes it, and notifies {@code handler.completed(path, attachment)}, or, on failure,
     * closes the channel and notifies {@code handler.failed(exc, attachment)}.
     * @implNote The reading of the channel's {@link AsynchronousFileChannel#size() size} and the
     * subsequent {@link #write(AsynchronousFileChannel, long, Object, CompletionHandler) write} are
     * not atomic. A concurrent writer may extend the file between the two operations, causing data
     * being overwritten rather than appended.
     */
    default <T extends Path, A>
    void append(final T path, @Nullable final A attachment,
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
                } catch (final IOException ioe) {
                    try {
                        r.close();
                    } catch (final IOException cioe) {
                        ioe.addSuppressed(cioe);
                    }
                    handler.failed(ioe, a);
                    return;
                }
                try {
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
                    ioe.addSuppressed(t);
                    handler.failed(ioe, a);
                    return;
                }
                handler.failed(t, a);
            } // @formatter:on
        });
    }
}
