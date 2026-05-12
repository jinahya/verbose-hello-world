package com.github.jinahya.hello.api;

import org.jspecify.annotations.Nullable;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Path;
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
 * Each instance wraps a service of type {@code T} (a {@link HelloWorld} subtype) that supplies the
 * bytes. All channel and path operations come as a matched pair:
 * <ul>
 *   <li>a {@link CompletionHandler}-based variant — {@code void method(Executor, target..., A,
 *       handler)} — that notifies completion via the handler;</li>
 *   <li>a {@link CompletionStage}-based variant — {@code CompletionStage<A> method(Executor,
 *       target..., A)} — that completes the returned stage with the {@code attachment}.</li>
 * </ul>
 * Both variants share the same implementation strategy: dispatch the work onto the supplied
 * {@link Executor} via {@link #applyAsync(Function, Executor)}. The two variants differ only in
 * how the result is delivered.
 * <p>
 * Separately, the {@link java.net.http.WebSocket} convenience methods —
 * {@link #sendBinary(WebSocket, boolean) sendBinary}, {@link #sendPing(WebSocket) sendPing}, and
 * {@link #sendPong(WebSocket) sendPong} — return a {@link CompletableFuture} of the same socket.
 * They do not take an executor; dispatch and completion notification are managed by the
 * underlying {@link WebSocket}'s {@link java.net.http.HttpClient} infrastructure.
 *
 * @param <T> the type of the wrapped {@link HelloWorld} service.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld
 */
public interface AsynchronousHelloWorld<T extends HelloWorld> {

    // ---------------------------------------------------------------------- STATIC_FACTORY_METHODS

    /**
     * Creates a new instance wrapping the specified service.
     *
     * @param <T>     the type of the wrapped {@link HelloWorld} service.
     * @param service the service to wrap.
     * @return a new instance wrapping the {@code service}.
     * @throws NullPointerException if {@code service} is {@code null}.
     */
    static <T extends HelloWorld> AsynchronousHelloWorld<T> from(final T service) {
        return new DefaultAsynchronousHelloWorld<>(service);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the specified mapper to the wrapped service of type {@code T} asynchronously on the
     * specified executor, and returns the result as a {@link CompletionStage}.
     *
     * @param <R>      result type parameter.
     * @param mapper   the mapper to apply; receives the wrapped service of type {@code T} and
     *                 returns a result.
     * @param executor the executor on which the {@code mapper} is dispatched.
     * @return a {@link CompletionStage} that completes with the value produced by the
     * {@code mapper}, or completes exceptionally if the {@code mapper} throws.
     * @throws NullPointerException if either {@code mapper} or {@code executor} is {@code null}.
     */
    <R> CompletionStage<R> applyAsync(Function<? super T, ? extends R> mapper, Executor executor);

    // ------------------------------------------------------------------------------- java.net.http

    /**
     * Sends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * WebSocket as a binary data message, and returns a {@link CompletableFuture} that completes
     * with the {@code socket} once the message has been sent.
     *
     * @param socket the WebSocket to which the
     *               <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> are sent.
     * @param last   {@code true} if this is the last fragment of the binary message; {@code false}
     *               otherwise.
     * @return a {@link CompletableFuture} that, on success, completes with the {@code socket} — or,
     * on failure, completes exceptionally.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @apiNote No executor is taken; dispatch and completion notification are managed by the
     * underlying {@link WebSocket}'s {@link java.net.http.HttpClient} infrastructure.
     * @see HelloWorld#put(ByteBuffer)
     * @see WebSocket#sendBinary(ByteBuffer, boolean)
     */
    CompletableFuture<WebSocket> sendBinary(WebSocket socket, boolean last);

    /**
     * Sends a {@code Ping} control frame, with the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as the application data,
     * to the specified WebSocket, and returns a {@link CompletableFuture} that completes with the
     * {@code socket} once the frame has been sent.
     *
     * @param socket the WebSocket to which the {@code Ping} frame is sent.
     * @return a {@link CompletableFuture} that, on success, completes with the {@code socket} — or,
     * on failure, completes exceptionally.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @apiNote The {@value HelloWorld#BYTES}-byte payload is well within the
     * <a href="https://www.rfc-editor.org/rfc/rfc6455#section-5.5">125-byte limit</a> for
     * WebSocket control-frame application data. No executor is taken; dispatch and completion
     * notification are managed by the underlying {@link WebSocket}'s
     * {@link java.net.http.HttpClient} infrastructure.
     * @see HelloWorld#put(ByteBuffer)
     * @see WebSocket#sendPing(ByteBuffer)
     */
    CompletableFuture<WebSocket> sendPing(WebSocket socket);

    /**
     * Sends a {@code Pong} control frame, with the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as the application data,
     * to the specified WebSocket, and returns a {@link CompletableFuture} that completes with the
     * {@code socket} once the frame has been sent.
     *
     * @param socket the WebSocket to which the {@code Pong} frame is sent.
     * @return a {@link CompletableFuture} that, on success, completes with the {@code socket} — or,
     * on failure, completes exceptionally.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @apiNote The {@value HelloWorld#BYTES}-byte payload is well within the
     * <a href="https://www.rfc-editor.org/rfc/rfc6455#section-5.5">125-byte limit</a> for
     * WebSocket control-frame application data. No executor is taken; dispatch and completion
     * notification are managed by the underlying {@link WebSocket}'s
     * {@link java.net.http.HttpClient} infrastructure.
     * @see HelloWorld#put(ByteBuffer)
     * @see WebSocket#sendPong(ByteBuffer)
     */
    CompletableFuture<WebSocket> sendPong(WebSocket socket);

    // --------------------------------------------------------------------------- java.nio.channels

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous byte channel, preparing the buffer on the specified executor, and notifies a
     * completion (or a failure) to the specified handler with the specified attachment.
     *
     * @param <C>        channel type parameter
     * @param <A>        attachment type parameter
     * @param executor   the executor on which the synchronous {@link HelloWorld#put(ByteBuffer)}
     *                   buffer preparation is dispatched.
     * @param channel    the asynchronous byte channel to which the bytes are written.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException if any of {@code executor}, {@code channel}, or {@code handler}
     *                              is {@code null}.
     * @implSpec The default implementation invokes {@link #applyAsync(Function, Executor)} with a
     * mapper that prepares a {@value HelloWorld#BYTES}-byte source buffer via
     * {@link HelloWorld#put(ByteBuffer)} on the wrapped service; the resulting stage's
     * {@link CompletionStage#whenComplete(java.util.function.BiConsumer) whenComplete} starts the
     * recursive
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler) channel.write}
     * loop that, on the final completion, notifies {@code handler.completed(channel, attachment)} —
     * or, on failure, notifies {@code handler.failed(exc, attachment)}.
     * @see HelloWorld#put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     * @see #applyAsync(Function, Executor)
     */
    default <C extends AsynchronousByteChannel, A>
    void write(final Executor executor, final C channel, @Nullable final A attachment,
               final CompletionHandler<? super C, ? super A> handler) { // @formatter:off
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        applyAsync(
                s -> s.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip(),
                executor
        ).whenComplete((b, t) -> {
            if (t != null) {
                handler.failed(t, attachment);
                return;
            }
            channel.write(b, attachment, new CompletionHandler<>() {
                @Override public void completed(final Integer result, final A attachment) {
                    if (b.hasRemaining()) {
                        channel.write(b, attachment, this);
                        return;
                    }
                    handler.completed(channel, attachment);
                }
                @Override public void failed(final Throwable exc, final A attachment) {
                    handler.failed(exc, attachment);
                }
            });
        }); // @formatter:off
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous byte channel on the specified executor, and returns a {@link CompletionStage}
     * that completes with the specified attachment.
     *
     * @param <A>        attachment type parameter; the stage's payload is of this type.
     * @param executor   the executor on which the synchronous
     *                   {@link HelloWorld#write(AsynchronousByteChannel)} call is dispatched.
     * @param channel    the asynchronous byte channel to which the bytes are written.
     * @param attachment the value with which the returned stage completes; may be {@code null}.
     * @return a {@link CompletionStage} that, on success, completes with the {@code attachment} —
     * or, on failure, completes exceptionally.
     * @throws NullPointerException if either {@code executor} or {@code channel} is {@code null}.
     * @implSpec The default implementation invokes
     * {@link #write(Executor, AsynchronousByteChannel, Object, CompletionHandler)} with a
     * {@link CompletionHandler} that completes the returned stage with the {@code attachment} on
     * success, or completes it exceptionally on failure.
     * @see #write(Executor, AsynchronousByteChannel, Object, CompletionHandler)
     */
    default <A>
    CompletionStage<A> write(final Executor executor, final AsynchronousByteChannel channel,
                             final @Nullable A attachment) { // @formatter:off
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(channel, "channel is null");
        final var future = new CompletableFuture<A>();
        write(executor, channel, attachment, new CompletionHandler<>() {
            @Override
            public void completed(final AsynchronousByteChannel result, final A attachment) {
                future.complete(attachment);
            }
            @Override
            public void failed(final Throwable exc, final A attachment) {
                future.completeExceptionally(exc);
            }
        });
        return future; // @formatter:on
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous file channel starting at the specified position, preparing the buffer on the
     * specified executor, and notifies a completion (or a failure) to the specified handler with
     * the specified attachment.
     *
     * @param <C>        channel type parameter
     * @param <A>        attachment type parameter
     * @param executor   the executor on which the synchronous {@link HelloWorld#put(ByteBuffer)}
     *                   buffer preparation is dispatched.
     * @param channel    the asynchronous file channel to which the bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException     if any of {@code executor}, {@code channel}, or
     *                                  {@code handler} is {@code null}.
     * @throws IllegalArgumentException if {@code position} is negative.
     * @implSpec The default implementation invokes {@link #applyAsync(Function, Executor)} with a
     * mapper that prepares a {@value HelloWorld#BYTES}-byte source buffer via
     * {@link HelloWorld#put(ByteBuffer)} on the wrapped service; the resulting stage's
     * {@link CompletionStage#whenComplete(java.util.function.BiConsumer) whenComplete} starts the
     * recursive
     * {@link AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * channel.write} loop that, on the final completion, notifies
     * {@code handler.completed(channel, attachment)} — or, on failure, notifies
     * {@code handler.failed(exc, attachment)}.
     * @see HelloWorld#put(ByteBuffer)
     * @see AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * @see #applyAsync(Function, Executor)
     */
    default <C extends AsynchronousFileChannel, A>
    void write(final Executor executor, final C channel, final long position,
               final @Nullable A attachment,
               final CompletionHandler<? super C, ? super A> handler) { // @formatter:off
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(handler, "handler is null");
        applyAsync(
                s -> s.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip(),
                executor
        ).whenComplete((buffer, t) -> {
            if (t != null) {
                handler.failed(t, attachment);
                return;
            }
            channel.write(buffer, position, position, new CompletionHandler<>() {
                @Override
                public void completed(final Integer result, Long position_) {
                    if (buffer.hasRemaining()) {
                        position_ += result;
                        channel.write(buffer, position_, position_, this);
                        return;
                    }
                    handler.completed(channel, attachment);
                }

                @Override
                public void failed(final Throwable exc, final Long position_) {
                    handler.failed(exc, attachment);
                }
            });
        }); // @formatter:on
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous file channel starting at the specified position on the specified executor, and
     * returns a {@link CompletionStage} that completes with the specified attachment.
     *
     * @param <A>        attachment type parameter; the stage's payload is of this type.
     * @param executor   the executor on which the synchronous
     *                   {@link HelloWorld#write(AsynchronousFileChannel, long)} call is
     *                   dispatched.
     * @param channel    the asynchronous file channel to which the bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param attachment the value with which the returned stage completes; may be {@code null}.
     * @return a {@link CompletionStage} that, on success, completes with the {@code attachment} —
     * or, on failure, completes exceptionally.
     * @throws NullPointerException     if either {@code executor} or {@code channel} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code position} is negative.
     * @implSpec The default implementation invokes
     * {@link #write(Executor, AsynchronousFileChannel, long, Object, CompletionHandler)} with a
     * {@link CompletionHandler} that completes the returned stage with the {@code attachment} on
     * success, or completes it exceptionally on failure.
     * @see #write(Executor, AsynchronousFileChannel, long, Object, CompletionHandler)
     */
    default <A>
    CompletionStage<A> write(final Executor executor, final AsynchronousFileChannel channel,
                             final long position, final @Nullable A attachment) { // @formatter:off
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        final var future = new CompletableFuture<A>();
        write(executor, channel, position, attachment,
              new CompletionHandler<>() {
                  @Override
                  public void completed(final AsynchronousFileChannel result, final A attachment) {
                      future.complete(attachment);
                  }
                  @Override public void failed(final Throwable exc, final A attachment) {
                      future.completeExceptionally(exc);
                  }
              });
        return future; // @formatter:on
    }

    // ------------------------------------------------------------------------------- java.nio.file

    /**
     * Appends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the end of
     * the file at the specified path on the specified executor, and notifies a completion (or a
     * failure) to the specified handler with the specified attachment.
     *
     * @param <P>        path type parameter
     * @param <A>        attachment type parameter
     * @param executor   the executor on which the synchronous {@link HelloWorld#append(Path)} call
     *                   is dispatched.
     * @param path       the path to the file to which the bytes are appended.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException if any of {@code executor}, {@code path}, or {@code handler} is
     *                              {@code null}.
     * @implSpec The default implementation invokes {@link #applyAsync(Function, Executor)} with a
     * mapper that calls {@link HelloWorld#append(Path)} on the wrapped service and, on success,
     * notifies {@code handler.completed(path, attachment)} — or, on any thrown {@link Exception}
     * (checked or unchecked), notifies {@code handler.failed(exc, attachment)}.
     * @see HelloWorld#append(Path)
     * @see #applyAsync(Function, Executor)
     */
    default <P extends Path, A>
    void append(final Executor executor, final P path, final @Nullable A attachment,
                final CompletionHandler<? super P, ? super A> handler) { // @formatter:off
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(handler, "handler is null");
        applyAsync(
                s -> {
                    try {
                        s.append(path);
                    } catch (final Exception e) {
                        handler.failed(e, attachment);
                        return null;
                    }
                    handler.completed(path, attachment);
                    return null;
                },
                executor
        ); // @formatter:on
    }

    /**
     * Appends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the end of
     * the file at the specified path on the specified executor, and returns a
     * {@link CompletionStage} that completes with the specified attachment.
     *
     * @param <A>        attachment type parameter; the stage's payload is of this type.
     * @param executor   the executor on which the synchronous {@link HelloWorld#append(Path)} call
     *                   is dispatched.
     * @param path       the path to the file to which the bytes are appended.
     * @param attachment the value with which the returned stage completes; may be {@code null}.
     * @return a {@link CompletionStage} that, on success, completes with the {@code attachment} —
     * or, on failure, completes exceptionally.
     * @throws NullPointerException if either {@code executor} or {@code path} is {@code null}.
     * @implSpec The default implementation invokes
     * {@link #append(Executor, Path, Object, CompletionHandler)} with a {@link CompletionHandler}
     * that completes the returned stage with the {@code attachment} on success, or completes it
     * exceptionally on failure.
     * @see #append(Executor, Path, Object, CompletionHandler)
     */
    default <A>
    CompletionStage<A> append(final Executor executor, final Path path,
                              final @Nullable A attachment) { // @formatter:off
        Objects.requireNonNull(executor, "executor is null");
        Objects.requireNonNull(path, "path is null");
        final var future = new CompletableFuture<A>();
        append(executor, path, attachment, new CompletionHandler<>() {
            @Override public void completed(final Path result, final A attachment) {
                future.complete(attachment);
            }
            @Override public void failed(final Throwable exc, final A attachment) {
                future.completeExceptionally(exc);
            }
        });
        return future; // @formatter:on
    }
}
