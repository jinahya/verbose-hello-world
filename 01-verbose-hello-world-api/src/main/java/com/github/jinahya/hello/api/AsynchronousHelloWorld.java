package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
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
import java.util.function.Function;

/**
 * An interface for writing the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> asynchronously through various
 * Java I/O APIs.
 * <p>
 * Each instance wraps a {@link HelloWorld} that supplies the bytes, and exposes asynchronous
 * patterns built on top of it:
 * <ul>
 *   <li>a generic dispatcher,
 *       {@link #applyAsync(Function, Executor) applyAsync}, that runs an arbitrary
 *       {@link Function} of the wrapped {@link HelloWorld} on a caller-provided
 *       {@link Executor};</li>
 *   <li>WebSocket helpers — {@link #sendBinary(WebSocket, boolean) sendBinary},
 *       {@link #sendPing(WebSocket) sendPing}, and {@link #sendPong(WebSocket) sendPong} — that
 *       return a {@link CompletableFuture} of the same socket;</li>
 *   <li>channel-based writers using {@link CompletionHandler}, for both
 *       {@link AsynchronousByteChannel} and {@link AsynchronousFileChannel}; both run the buffer
 *       preparation on the caller's thread (callers can wrap the call with
 *       {@link Executor#execute(Runnable)} if they need to dispatch it elsewhere);</li>
 *   <li>an {@link #append(Path, Object, CompletionHandler) append} convenience that opens an
 *       {@link AsynchronousFileChannel} for a {@link Path}, with an executor-aware variant,
 *       {@link #append(Executor, Path, Object, CompletionHandler) append(executor, ...)}, that
 *       dispatches the blocking append to a caller-provided {@link Executor}.</li>
 * </ul>
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld
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

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the specified mapper to the wrapped {@link HelloWorld} asynchronously on a default
     * executor, and returns the result as a {@link CompletionStage}.
     *
     * @param <R>    result type parameter
     * @param mapper the mapper to apply; receives the wrapped {@link HelloWorld} instance and
     *               returns a result.
     * @return a {@link CompletionStage} that completes with the value produced by the
     * {@code mapper}, or completes exceptionally if the {@code mapper} throws.
     * @throws NullPointerException if {@code mapper} is {@code null}.
     * @apiNote The executor on which the {@code mapper} is dispatched is implementation-defined.
     * Callers that need control over the execution context should use
     * {@link #applyAsync(Function, Executor)} with an explicit {@link Executor}.
     * @deprecated Use {@link #applyAsync(Function, Executor)} with an explicit {@link Executor}.
     */
    @Deprecated
    <R> CompletionStage<R> applyAsync(Function<? super HelloWorld, ? extends R> mapper);

    /**
     * Applies the specified mapper to the wrapped {@link HelloWorld} asynchronously on the
     * specified executor, and returns the result as a {@link CompletionStage}.
     *
     * @param <R>      result type parameter
     * @param mapper   the mapper to apply; receives the wrapped {@link HelloWorld} instance and
     *                 returns a result.
     * @param executor the executor on which the {@code mapper} is dispatched.
     * @return a {@link CompletionStage} that completes with the value produced by the
     * {@code mapper}, or completes exceptionally if the {@code mapper} throws.
     * @throws NullPointerException if either {@code mapper} or {@code executor} is {@code null}.
     */
    <R> CompletionStage<R> applyAsync(Function<? super HelloWorld, ? extends R> mapper,
                                      Executor executor);

    // ------------------------------------------------------------------------------- java.net.http

    /**
     * Sends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * WebSocket as a binary message, and returns a {@link CompletableFuture} that completes with
     * the {@code socket} when the send is complete.
     *
     * @param socket the WebSocket to which the
     *               <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> are sent.
     * @param last   {@code true} if this is the last fragment of the binary message; {@code false}
     *               otherwise.
     * @return a {@link CompletableFuture} that completes with the {@code socket} when the send
     * succeeds, or completes exceptionally if the send fails.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @see WebSocket#sendBinary(ByteBuffer, boolean)
     */
    CompletableFuture<WebSocket> sendBinary(WebSocket socket, boolean last);

    /**
     * Sends a Ping frame, with the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as the payload, to the
     * specified WebSocket, and returns a {@link CompletableFuture} that completes with the
     * {@code socket} when the send is complete.
     *
     * @param socket the WebSocket to which the Ping frame is sent.
     * @return a {@link CompletableFuture} that completes with the {@code socket} when the send
     * succeeds, or completes exceptionally if the send fails.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @see WebSocket#sendPing(ByteBuffer)
     */
    CompletableFuture<WebSocket> sendPing(WebSocket socket);

    /**
     * Sends a Pong frame, with the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as the payload, to the
     * specified WebSocket, and returns a {@link CompletableFuture} that completes with the
     * {@code socket} when the send is complete.
     *
     * @param socket the WebSocket to which the Pong frame is sent.
     * @return a {@link CompletableFuture} that completes with the {@code socket} when the send
     * succeeds, or completes exceptionally if the send fails.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @see WebSocket#sendPong(ByteBuffer)
     */
    CompletableFuture<WebSocket> sendPong(WebSocket socket);

    // --------------------------------------------------------------------------- java.nio.channels

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous byte channel, and notifies a completion (or a failure) to the specified handler
     * with the specified attachment.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the asynchronous byte channel to which the bytes are written.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException if either {@code channel} or {@code handler} is {@code null}.
     * @see HelloWorld#put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     */
    <T extends AsynchronousByteChannel, A>
    void write(T channel,
               @Nullable A attachment,
               CompletionHandler<? super T, ? super A> handler);

    /**
     * <strong>Provisional</strong> — slated for removal. Writes the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous byte channel, dispatching the buffer preparation onto the specified executor,
     * and notifies a completion (or a failure) to the specified handler with the specified
     * attachment.
     * <p>
     * This overload exists alongside
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler, Executor)} purely to
     * demonstrate an alternative implementation idiom — the {@link CompletionStage} pipeline
     * (see {@code DefaultAsynchronousHelloWorld}). For real use, prefer the
     * {@link CompletableFuture}-returning overload, which subsumes this one's behaviour and
     * exposes a future the caller can compose with.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the asynchronous byte channel to which the bytes are written.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @param executor   the executor on which the buffer preparation is dispatched.
     * @throws NullPointerException if any of {@code channel}, {@code handler}, or {@code executor}
     *                              is {@code null}.
     * @deprecated Provisional — slated for removal. Use
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler, Executor)
     * write(channel, attachment, handler, executor)} instead, which subsumes this method's
     * behaviour and additionally returns a {@link CompletableFuture} the caller can compose with
     * (callers who don't need the future may simply ignore the return value).
     */
    @Deprecated(forRemoval = true)
    <T extends AsynchronousByteChannel, A>
    void writeOn(final T channel,
                 final @Nullable A attachment,
                 final CompletionHandler<? super T, ? super A> handler,
                 final Executor executor);

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous byte channel, dispatching the buffer preparation onto the specified executor,
     * notifies a completion (or a failure) to the specified handler with the specified attachment,
     * and returns a {@link CompletableFuture} that mirrors the same outcome.
     * <p>
     * The returned future tracks the actual I/O completion (not just the dispatch): it completes
     * with the {@code channel} once the bytes have been written and {@code handler.completed} has
     * been invoked, or completes exceptionally — also routing the throwable to
     * {@code handler.failed} — if the buffer preparation or the channel write fails.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the asynchronous byte channel to which the bytes are written.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @param executor   the executor on which the buffer preparation is dispatched.
     * @return a {@link CompletableFuture} that completes with the {@code channel} when the I/O
     * succeeds, or completes exceptionally if the buffer preparation or the channel write fails.
     * @throws NullPointerException if any of {@code channel}, {@code handler}, or {@code executor}
     *                              is {@code null}.
     * @implSpec The default implementation submits a {@link Runnable} to the {@code executor} that
     * invokes {@link #write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
     * attachment, handler)} with a wrapped {@link CompletionHandler} that mirrors every
     * notification to both the caller-supplied {@code handler} and the returned future.
     */
    default <T extends AsynchronousByteChannel, A>
    CompletableFuture<T> write(final T channel,
                               final @Nullable A attachment,
                               final CompletionHandler<? super T, ? super A> handler,
                               final Executor executor) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        Objects.requireNonNull(executor, "executor is null");
        final var future = new CompletableFuture<T>();
        executor.execute(() -> { // @formatter:off
            try {
                write(channel, attachment, new CompletionHandler<T, A>() {
                    @Override
                    public void completed(final T result, final A a) {
                        try {
                            handler.completed(result, a);
                        } finally {
                            future.complete(result);
                        }
                    }
                    @Override
                    public void failed(final Throwable t, final A a) {
                        try {
                            handler.failed(t, a);
                        } finally {
                            future.completeExceptionally(t);
                        }
                    }
                });
            } catch (final Throwable t) {
                try {
                    handler.failed(t, attachment);
                } finally {
                    future.completeExceptionally(t);
                }
            } // @formatter:on
        });
        return future;
    }

//    /**
//     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
//     * asynchronous byte channel using the specified executor for the buffer preparation, and
//     * notifies a completion (or a failure) to the specified handler with the specified attachment.
//     * <p>
//     * This is the executor-aware variant of
//     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler)}: the allocation and
//     * filling of the {@link ByteBuffer} that holds the
//     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> are dispatched to the
//     * {@code executor} rather than running on the caller's thread. The actual non-blocking I/O
//     * still runs on the {@code channel}'s own thread group, as does the eventual notification of
//     * the {@code handler}.
//     *
//     * @param <T>        channel type parameter
//     * @param <A>        attachment type parameter
//     * @param executor   the executor on which the buffer preparation is dispatched.
//     * @param channel    the asynchronous byte channel to which the bytes are written.
//     * @param attachment the attachment for the {@code handler}; may be {@code null}.
//     * @param handler    the completion handler to be notified with a completion (or a failure).
//     * @throws NullPointerException if any of {@code executor}, {@code channel}, or {@code handler}
//     *                              is {@code null}.
//     * @see #write(AsynchronousByteChannel, Object, CompletionHandler)
//     * @see HelloWorld#put(ByteBuffer)
//     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
//     */
//    <T extends AsynchronousByteChannel, A>
//    void write(final Executor executor, final T channel,
//               @Nullable final A attachment,
//               final CompletionHandler<? super T, ? super A> handler);

    /**
     * Sends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * socket channel, and then notifies a completion (or a failure) to the specified handler with
     * the specified attachment.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the channel to which the
     *                   <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> are
     *                   sent.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the handler to be notified with a completion (or a failure).
     * @throws NullPointerException if either {@code channel} or {@code handler} is {@code null}.
     * @implSpec The default implementation invokes
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler)} with the {@code channel},
     * {@code attachment}, and {@code handler}.
     * @deprecated Since {@link AsynchronousSocketChannel} implements
     * {@link AsynchronousByteChannel}, use
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler)} instead.
     */
    @Deprecated(forRemoval = true)
    @屋上架屋("AsynchronousSocketChannel implements AsynchronousByteChannel")
    default <T extends AsynchronousSocketChannel, A>
    void send(final T channel, @Nullable final A attachment,
              final CompletionHandler<? super T, ? super A> handler) {
        write(channel, attachment, handler);
    }

    /**
     * Writes, asynchronously, the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous file channel, starting at the specified position, and notifies a completion (or
     * a failure) to the specified handler with the specified attachment.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the asynchronous file channel to which the bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException     if either {@code channel} or {@code handler} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code position} is negative.
     * @see HelloWorld#put(ByteBuffer)
     * @see AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     */
    <T extends AsynchronousFileChannel, A>
    void write(T channel,
               long position,
               @Nullable A attachment,
               CompletionHandler<? super T, ? super A> handler);

//    /**
//     * Writes, asynchronously, the
//     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
//     * asynchronous file channel, starting at the specified position, using the specified executor
//     * for the buffer preparation, and notifies a completion (or a failure) to the specified handler
//     * with the specified attachment.
//     * <p>
//     * This is the executor-aware variant of
//     * {@link #write(AsynchronousFileChannel, long, Object, CompletionHandler)}: the allocation and
//     * filling of the {@link ByteBuffer} that holds the
//     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> are dispatched to the
//     * {@code executor} rather than running on the caller's thread. The actual non-blocking I/O
//     * still runs on the {@code channel}'s own thread group, as does the eventual notification of
//     * the {@code handler}.
//     *
//     * @param <T>        channel type parameter
//     * @param <A>        attachment type parameter
//     * @param executor   the executor on which the buffer preparation is dispatched.
//     * @param channel    the asynchronous file channel to which the bytes are written.
//     * @param position   the file position at which the transfer is to begin; must be non-negative.
//     * @param attachment the attachment for the {@code handler}; may be {@code null}.
//     * @param handler    the completion handler to be notified with a completion (or a failure).
//     * @throws NullPointerException     if any of {@code executor}, {@code channel}, or
//     *                                  {@code handler} is {@code null}.
//     * @throws IllegalArgumentException if {@code position} is negative.
//     * @see #write(AsynchronousFileChannel, long, Object, CompletionHandler)
//     * @see HelloWorld#put(ByteBuffer)
//     * @see AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
//     */
//    <T extends AsynchronousFileChannel, A>
//    void write(Executor executor,
//               T channel,
//               long position,
//               @Nullable A attachment,
//               CompletionHandler<? super T, ? super A> handler);

    /**
     * Appends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the end of
     * the file at the specified path, and notifies a completion (or a failure) to the specified
     * handler with the specified attachment.
     *
     * @param <T>        path type parameter
     * @param <A>        attachment type parameter
     * @param path       the path to the file to which the bytes are appended.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws IOException          if an I/O error occurs.
     * @throws NullPointerException if either {@code path} or {@code handler} is {@code null}.
     * @implSpec The default implementation opens an {@link AsynchronousFileChannel} for the
     * specified {@code path} with {@link StandardOpenOption#CREATE CREATE} and
     * {@link StandardOpenOption#WRITE WRITE} options, and invokes
     * {@link #write(AsynchronousFileChannel, long, Object, CompletionHandler)} with the channel,
     * the channel's current {@link AsynchronousFileChannel#size() size} as the position, the
     * {@code attachment}, and a {@link CompletionHandler} that, on completion, forces the channel,
     * closes it, and notifies {@code handler.completed(path, attachment)}, or, on failure, closes
     * the channel and notifies {@code handler.failed(exc, attachment)}.
     * @implNote The reading of the channel's {@link AsynchronousFileChannel#size() size} and the
     * subsequent {@link #write(AsynchronousFileChannel, long, Object, CompletionHandler) write} are
     * not atomic. A concurrent writer may extend the file between the two operations, causing data
     * to be overwritten rather than appended.
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

    /**
     * Appends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the end of
     * the file at the specified path using the specified executor, and notifies a completion (or a
     * failure) to the specified handler with the specified attachment.
     * <p>
     * This is the executor-aware variant of {@link #append(Path, Object, CompletionHandler)}: the
     * blocking append is dispatched to the {@code executor} rather than running on the caller's
     * thread, and the {@code handler} is notified once the dispatched operation completes.
     *
     * @param <T>        path type parameter
     * @param <A>        attachment type parameter
     * @param executor   the executor on which the blocking append is dispatched.
     * @param path       the path to the file to which the bytes are appended.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException if any of {@code executor}, {@code path}, or {@code handler} is
     *                              {@code null}.
     * @implSpec The default implementation invokes {@link #applyAsync(Function, Executor)} with a
     * mapper that calls {@link HelloWorld#append(Path)} on the wrapped service, and the
     * {@code executor}; on success, notifies {@code handler.completed(path, attachment)}; on
     * failure, notifies {@code handler.failed(exc, attachment)}.
     * @see #append(Path, Object, CompletionHandler)
     * @see #applyAsync(Function, Executor)
     * @see HelloWorld#append(Path)
     */
    default <T extends Path, A>
    void append(final Executor executor,
                final T path, @Nullable final A attachment,
                final CompletionHandler<? super T, ? super A> handler) {
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(handler, "handler is null");
        applyAsync(
                s -> {
                    try {
                        return s.append(path);
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                },
                executor
        ).thenAccept(p -> handler.completed(p, attachment))
                .exceptionally(t -> {
                    handler.failed(t, attachment);
                    return null;
                });
    }
}
