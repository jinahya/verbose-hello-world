package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
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
 *   <li>channel-based writers in two shapes — a {@link CompletionHandler}-based primitive,
 *       {@link #write(AsynchronousByteChannel, Object, CompletionHandler) write(channel,
 *       attachment, handler)}, whose buffer preparation runs on the caller's thread, and an
 *       executor-aware {@link CompletableFuture} variant,
 *       {@link #write(Executor, AsynchronousByteChannel, Object) write(executor, channel,
 *       attachment)}, that dispatches the synchronous buffer preparation onto a caller-provided
 *       {@link Executor} and returns a future of the attachment; plus a
 *       {@link CompletionHandler}-based writer for {@link AsynchronousFileChannel};</li>
 *   <li>an {@link #append(Path, Object, CompletionHandler) append} convenience that opens an
 *       {@link AsynchronousFileChannel} for a {@link Path}, with an executor-aware
 *       {@link CompletableFuture} variant,
 *       {@link #append(Executor, Path, Object) append(executor, path, attachment)}, that
 *       dispatches the blocking open-write-close to a caller-provided {@link Executor} and
 *       returns a future of the attachment.</li>
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
     * <p>
     * <strong>Note:</strong> the buffer preparation step (the synchronous
     * {@link HelloWorld#put(ByteBuffer)} call) is performed on the caller's thread before the
     * asynchronous {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)} is
     * dispatched; the caller's thread is therefore <em>not</em> freed until the buffer is ready.
     * For fully-asynchronous behavior — where the synchronous buffer preparation also runs off the
     * caller's thread — use
     * {@link #write(Executor, AsynchronousByteChannel, Object) write(executor, channel,
     * attachment)} instead.
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
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous byte channel, dispatching the synchronous buffer preparation onto the specified
     * executor, and returns a {@link CompletableFuture} that completes with the specified
     * attachment.
     * <p>
     * This is the fully-asynchronous companion to
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler) write(channel, attachment,
     * handler)}: the synchronous {@link HelloWorld#put(ByteBuffer)} step runs on the supplied
     * {@code executor} (not on the caller's thread), and the subsequent
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler) channel.write}
     * recursion runs on the channel group's own thread pool. The caller's thread is released
     * immediately after this method returns.
     * <p>
     * The returned future tracks the actual I/O completion (not just the dispatch): it completes
     * with the {@code attachment} once the bytes have been written, or completes exceptionally if
     * the buffer preparation or the channel write fails.
     * <p>
     * Passing the {@code channel} itself as the {@code attachment} yields a future of the channel
     * — the same outcome a {@link CompletionHandler}-returning overload would provide — without
     * forcing the caller to choose a callback API.
     *
     * @param <A>        attachment type parameter; the future's payload is of this type.
     * @param executor   the executor on which the synchronous buffer preparation is dispatched.
     * @param channel    the asynchronous byte channel to which the bytes are written.
     * @param attachment the value with which the returned future completes; may be {@code null}.
     * @return a {@link CompletableFuture} that completes with the {@code attachment} when the I/O
     * succeeds, or completes exceptionally if the buffer preparation or the channel write fails.
     * @throws NullPointerException if either {@code executor} or {@code channel} is {@code null}.
     * @implSpec The default implementation dispatches {@code s -> s.put(...).flip()} via
     * {@link #applyAsync(Function, Executor)} on the {@code executor}; the resulting stage's
     * {@link CompletionStage#thenAccept(java.util.function.Consumer) thenAccept} starts the
     * recursive {@code channel.write} loop that, on the final completion, completes the returned
     * future with the {@code attachment} — or, on failure (including any synchronous throw from
     * the preparation stage, captured by {@link CompletionStage#exceptionally}), completes it
     * exceptionally.
     * @see #applyAsync(Function, Executor)
     */
    default <A>
    CompletableFuture<A> write(final Executor executor, final AsynchronousByteChannel channel,
                               final @Nullable A attachment) { // @formatter:off
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(channel, "channel is null");
        final var future = new CompletableFuture<A>();
        applyAsync(
                s -> s.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip(),
                executor
        ).thenAccept(b ->
                channel.write(b, attachment, new CompletionHandler<Integer, A>() {
                    @Override
                    public void completed(final Integer n, final A a) {
                        if (b.hasRemaining()) {
                            channel.write(b, a, this);
                            return;
                        }
                        future.complete(a);
                    }
                    @Override
                    public void failed(final Throwable t, final A a) {
                        future.completeExceptionally(t);
                    }
                })
        ).exceptionally(t -> {
            future.completeExceptionally(t);
            return null;
        });
        return future; // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------
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

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous file channel starting at the specified position, dispatching the synchronous
     * buffer preparation onto the specified executor, and returns a {@link CompletableFuture} that
     * completes with the specified attachment.
     * <p>
     * This is the fully-asynchronous companion to
     * {@link #write(AsynchronousFileChannel, long, Object, CompletionHandler) write(channel,
     * position, attachment, handler)}: the synchronous {@link HelloWorld#put(ByteBuffer)} step runs
     * on the supplied {@code executor} (not on the caller's thread), and the subsequent
     * {@link AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * channel.write} recursion runs on the channel's own thread pool. The caller's thread is
     * released immediately after this method returns.
     * <p>
     * The returned future tracks the actual I/O completion (not just the dispatch): it completes
     * with the {@code attachment} once the bytes have been written, or completes exceptionally if
     * the buffer preparation or the channel write fails.
     *
     * @param <A>        attachment type parameter; the future's payload is of this type.
     * @param executor   the executor on which the synchronous buffer preparation is dispatched.
     * @param channel    the asynchronous file channel to which the bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param attachment the value with which the returned future completes; may be {@code null}.
     * @return a {@link CompletableFuture} that completes with the {@code attachment} when the I/O
     * succeeds, or completes exceptionally if the buffer preparation or the channel write fails.
     * @throws NullPointerException     if either {@code executor} or {@code channel} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code position} is negative.
     * @see #applyAsync(Function, Executor)
     */
    default <A>
    CompletableFuture<A> write(final Executor executor,
                               final AsynchronousFileChannel channel,
                               final long position,
                               final @Nullable A attachment) { // @formatter:off
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        final var future = new CompletableFuture<A>();
        applyAsync(
                s -> s.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip(),
                executor
        ).thenAccept(b ->
                channel.write(b, position, position, new CompletionHandler<Integer, Long>() {
                    @Override
                    public void completed(final Integer n, final Long p) {
                        if (b.hasRemaining()) {
                            final long next = p + n;
                            channel.write(b, next, next, this);
                            return;
                        }
                        future.complete(attachment);
                    }
                    @Override
                    public void failed(final Throwable t, final Long p) {
                        future.completeExceptionally(t);
                    }
                })
        ).exceptionally(t -> {
            future.completeExceptionally(t);
            return null;
        });
        return future; // @formatter:on
    }


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
            throws IOException { // @formatter:off
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(handler, "handler is null");
        final var options = new OpenOption[] {
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
        };
        final var channel = AsynchronousFileChannel.open(path, options);
        try {
            write(channel, channel.size(), attachment, new CompletionHandler<>() {
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
                        t.addSuppressed(ioe);
                    }
                    handler.failed(t, a);
                }
            });
        } catch (final Exception e) {
            try {
                channel.close();
            } catch (final IOException ioe) {
                e.addSuppressed(ioe);
            }
            throw e;
        } // @formatter:on
    }

    /**
     * Appends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the end of
     * the file at the specified path, dispatching the whole open-write-close operation onto the
     * specified executor, and returns a {@link CompletableFuture} that completes with the
     * specified attachment.
     * <p>
     * This is the fully-asynchronous companion to
     * {@link #append(Path, Object, CompletionHandler) append(path, attachment, handler)}: the
     * blocking open, write, and close all run on the supplied {@code executor} (not on the
     * caller's thread); the caller's thread is released immediately after this method returns.
     *
     * @param <A>        attachment type parameter; the future's payload is of this type.
     * @param executor   the executor on which the blocking open-write-close is dispatched.
     * @param path       the path to the file to which the bytes are appended.
     * @param attachment the value with which the returned future completes; may be {@code null}.
     * @return a {@link CompletableFuture} that completes with the {@code attachment} when the
     * append succeeds, or completes exceptionally if the append fails.
     * @throws NullPointerException if either {@code executor} or {@code path} is {@code null}.
     * @implSpec The default implementation invokes {@link #applyAsync(Function, Executor)} with a
     * mapper that calls {@link HelloWorld#append(Path)} on the wrapped service, and returns the
     * {@code attachment}; the stage is converted to a {@link CompletableFuture}.
     * @see #append(Path, Object, CompletionHandler)
     * @see #applyAsync(Function, Executor)
     * @see HelloWorld#append(Path)
     */
    default <A>
    CompletableFuture<A> append(final Executor executor,
                                final Path path,
                                final @Nullable A attachment) {
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(path, "path is null");
        return applyAsync(
                s -> {
                    try {
                        s.append(path);
                        return attachment;
                    } catch (final IOException ioe) {
                        throw new UncheckedIOException(ioe);
                    }
                },
                executor
        ).toCompletableFuture();
    }
}
