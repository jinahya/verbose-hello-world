package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.jspecify.annotations.*;

import java.io.*;
import java.lang.invoke.*;
import java.net.http.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * An interface for writing the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> asynchronously through various
 * Java I/O APIs.
 * <p>
 * Each instance wraps a service of type {@code T} (a {@link HelloWorld} subtype) that supplies the
 * bytes, and a dispatch strategy of the implementation's choosing on which the synchronous
 * {@link HelloWorld} calls are run — an {@link Executor} for {@link ExecutorHelloWorld}. Every
 * method on this interface uses that strategy — directly via the two shape-paired primitives
 * {@link #applyAsync(Function)} ({@link CompletionStage}-based) and
 * {@link #applyAsync(Function, Object, CompletionHandler)} ({@link CompletionHandler}-based), or
 * indirectly via the default methods built on them.
 * <p>
 * Channel and path operations come as a matched pair:
 * <ul>
 *   <li>a {@link CompletionHandler}-based variant — {@code void method(target..., A, handler)} —
 *       that notifies completion via the handler;</li>
 *   <li>a {@link CompletionStage}-based variant — {@code CompletionStage<A> method(target..., A)}
 *       — that completes the returned stage with the {@code attachment}.</li>
 * </ul>
 * <p>
 * The {@link java.net.http.WebSocket} convenience methods —
 * {@link #sendBinary(WebSocket, boolean) sendBinary}, {@link #sendPing(WebSocket) sendPing}, and
 * {@link #sendPong(WebSocket) sendPong} — return a {@link CompletionStage} of the same socket.
 * The synchronous buffer preparation runs on the instance's dispatch strategy; the actual send is
 * dispatched and completed by the underlying {@link WebSocket}'s {@link HttpClient} infrastructure.
 * <p>
 * For HTTP/2, {@link #sendAsync(Function, HttpClient, HttpResponse.BodyHandler) sendAsync}
 * prepares an {@link HttpRequest.BodyPublisher} of the
 * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>, hands it to a caller-supplied
 * {@link Function} that returns a fully-built {@link HttpRequest}, then sends the request through
 * an {@link HttpClient}, returning a {@link CompletionStage} of the {@link HttpResponse}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld
 */
public interface AsynchronousHelloWorld<T extends HelloWorld> {

    private static System.Logger log() {
        return System.getLogger(MethodHandles.lookup().lookupClass().getName());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Applies the specified mapper to the wrapped {@link HelloWorld} service asynchronously on the
     * instance's dispatch strategy, and returns the result as a {@link CompletionStage}.
     *
     * @param <R>    result type parameter.
     * @param mapper the mapper to apply; receives the wrapped {@link HelloWorld} service and
     *               returns a result.
     * @return a {@link CompletionStage} that completes with the value produced by the
     * {@code mapper}, or completes exceptionally if the {@code mapper} throws.
     * @throws NullPointerException if {@code mapper} is {@code null}.
     * @apiNote This method is the {@link CompletionStage}-based primitive on which every
     * stage-based default method in this interface — {@code write(...)} (no-handler overload),
     * {@code append(...)} (no-handler overload), {@code sendBinary}, {@code sendPing},
     * {@code sendPong}, and {@code sendAsync} — is built. Its {@link CompletionHandler}-based
     * counterpart is
     * {@link #applyAsync(Function, Object, CompletionHandler) applyAsync(mapper, attachment,
     * handler)}.
     */
    <R> CompletionStage<R> applyAsync(Function<? super T, ? extends R> mapper);

    /**
     * Applies the specified mapper to the wrapped {@link HelloWorld} service asynchronously on the
     * instance's dispatch strategy, and notifies the specified handler with the result and the
     * specified attachment.
     *
     * @param <R>        result type parameter.
     * @param <A>        attachment type parameter.
     * @param mapper     the mapper to apply; receives the wrapped {@link HelloWorld} service and
     *                   returns a result.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with the result (or a failure) and
     *                   the {@code attachment}.
     * @throws NullPointerException if either {@code mapper} or {@code handler} is {@code null}.
     * @apiNote This method is the {@link CompletionHandler}-based primitive on which every
     * handler-based default method in this interface — {@code write(..., handler)},
     * {@code append(..., handler)}, and any future {@code CompletionHandler}-based variant — is
     * built. Its {@link CompletionStage}-based counterpart is {@link #applyAsync(Function)}.
     */
    <R, A> void applyAsync(Function<? super T, ? extends R> mapper,
                           @Nullable A attachment,
                           CompletionHandler<? super R, ? super A> handler);

    // ------------------------------------------------------------------------------- java.net.http

    /**
     * Sends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * WebSocket as a binary data message, and returns a {@link CompletionStage} that completes with
     * the {@code socket} once the message has been sent.
     *
     * @param socket the WebSocket to which the
     *               <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> are sent.
     * @param last   {@code true} if this is the last fragment of the binary message; {@code false}
     *               otherwise.
     * @return a {@link CompletionStage} that, on success, completes with the {@code socket} — or,
     * on failure, completes exceptionally.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @implSpec The default implementation invokes {@link #applyAsync(Function)} with a mapper that
     * prepares a {@value HelloWorld#BYTES}-byte source buffer via
     * {@link HelloWorld#put(ByteBuffer)} on the wrapped service; the resulting stage's
     * {@link CompletionStage#thenCompose(Function) thenCompose} hands the buffer to
     * {@link WebSocket#sendBinary(ByteBuffer, boolean) socket.sendBinary(buffer, last)}.
     * @see HelloWorld#put(ByteBuffer)
     * @see WebSocket#sendBinary(ByteBuffer, boolean)
     * @see #applyAsync(Function)
     */
    default CompletionStage<WebSocket> sendBinary(final WebSocket socket, final boolean last) {
        Objects.requireNonNull(socket, "socket is null");
        return applyAsync(
                s -> s.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip()
        ).thenCompose(b -> socket.sendBinary(b, last));
    }

    /**
     * Sends a {@code Ping} control frame, with the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as the application data,
     * to the specified WebSocket, and returns a {@link CompletionStage} that completes with the
     * {@code socket} once the frame has been sent.
     *
     * @param socket the WebSocket to which the {@code Ping} frame is sent.
     * @return a {@link CompletionStage} that, on success, completes with the {@code socket} — or,
     * on failure, completes exceptionally.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @apiNote The {@value HelloWorld#BYTES}-byte payload is well within the
     * <a href="https://www.rfc-editor.org/rfc/rfc6455#section-5.5">125-byte limit</a> for
     * WebSocket control-frame application data.
     * @implSpec The default implementation invokes {@link #applyAsync(Function)} with a mapper that
     * prepares a {@value HelloWorld#BYTES}-byte source buffer via
     * {@link HelloWorld#put(ByteBuffer)} on the wrapped service; the resulting stage's
     * {@link CompletionStage#thenCompose(Function) thenCompose} hands the buffer to
     * {@link WebSocket#sendPing(ByteBuffer) socket.sendPing(buffer)}.
     * @see HelloWorld#put(ByteBuffer)
     * @see WebSocket#sendPing(ByteBuffer)
     * @see #applyAsync(Function)
     */
    default CompletionStage<WebSocket> sendPing(final WebSocket socket) {
        Objects.requireNonNull(socket, "socket is null");
        return applyAsync(
                s -> s.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip()
        ).thenCompose(socket::sendPing);
    }

    /**
     * Sends a {@code Pong} control frame, with the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as the application data,
     * to the specified WebSocket, and returns a {@link CompletionStage} that completes with the
     * {@code socket} once the frame has been sent.
     *
     * @param socket the WebSocket to which the {@code Pong} frame is sent.
     * @return a {@link CompletionStage} that, on success, completes with the {@code socket} — or,
     * on failure, completes exceptionally.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @apiNote The {@value HelloWorld#BYTES}-byte payload is well within the
     * <a href="https://www.rfc-editor.org/rfc/rfc6455#section-5.5">125-byte limit</a> for
     * WebSocket control-frame application data.
     * @implSpec The default implementation invokes {@link #applyAsync(Function)} with a mapper that
     * prepares a {@value HelloWorld#BYTES}-byte source buffer via
     * {@link HelloWorld#put(ByteBuffer)} on the wrapped service; the resulting stage's
     * {@link CompletionStage#thenCompose(Function) thenCompose} hands the buffer to
     * {@link WebSocket#sendPong(ByteBuffer) socket.sendPong(buffer)}.
     * @see HelloWorld#put(ByteBuffer)
     * @see WebSocket#sendPong(ByteBuffer)
     * @see #applyAsync(Function)
     */
    default CompletionStage<WebSocket> sendPong(final WebSocket socket) {
        Objects.requireNonNull(socket, "socket is null");
        return applyAsync(
                s -> s.put(ByteBuffer.allocate(HelloWorld.BYTES)).flip()
        ).thenCompose(socket::sendPong);
    }

    /**
     * Sends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> as the request
     * body via the specified HTTP client, building the request through the caller-supplied
     * function, and returns a {@link CompletionStage} that completes with the HTTP response.
     * <p>
     * The synchronous buffer preparation runs on the instance's executor; the prepared
     * {@link HttpRequest.BodyPublisher} is then handed to the supplied {@code function}, which is
     * expected to produce a fully-built {@link HttpRequest} that attaches the publisher (typically
     * via {@link HttpRequest.Builder#POST(HttpRequest.BodyPublisher) POST},
     * {@link HttpRequest.Builder#PUT(HttpRequest.BodyPublisher) PUT}, or
     * {@link HttpRequest.Builder#method(String, HttpRequest.BodyPublisher) method}) and applies any
     * other configuration. The resulting request is then sent via
     * {@link HttpClient#sendAsync(HttpRequest, HttpResponse.BodyHandler) client.sendAsync}.
     * <p>
     * Typical use:
     * {@snippet lang = "java":
     * service.sendAsync(
     *         p -> HttpRequest.newBuilder(uri).POST(p).build(),
     *         client,
     *         HttpResponse.BodyHandlers.discarding()
     * );
     *}
     *
     * @param <R>      response body type parameter.
     * @param function a function that produces a fully-built {@link HttpRequest} from the prepared
     *                 {@link HttpRequest.BodyPublisher}; expected to construct the builder, set the
     *                 URI, attach the body via {@code POST}/{@code PUT}/{@code method}, and call
     *                 {@code build()}.
     * @param client   the HTTP client through which the request is sent.
     * @param handler  the response body handler.
     * @return a {@link CompletionStage} that, on success, completes with the {@link HttpResponse} —
     * or, on failure, completes exceptionally.
     * @throws NullPointerException if any of {@code function}, {@code client}, or {@code handler}
     *                              is {@code null}.
     * @implSpec The default implementation prepares the body via {@link #applyAsync(Function)} —
     * calling {@link HelloWorld#set(byte[])} on the wrapped service and wrapping the resulting
     * array as an {@link HttpRequest.BodyPublishers#ofByteArray(byte[])} body publisher — then
     * applies the {@code function} to obtain the {@link HttpRequest} and invokes
     * {@link HttpClient#sendAsync(HttpRequest, HttpResponse.BodyHandler) client.sendAsync}.
     * @see HelloWorld#set(byte[])
     * @see HttpRequest.BodyPublishers#ofByteArray(byte[])
     * @see #applyAsync(Function)
     * @see HttpClient#sendAsync(HttpRequest, HttpResponse.BodyHandler)
     */
    default <R> CompletionStage<HttpResponse<R>> sendAsync(
            final Function<? super HttpRequest.BodyPublisher, ? extends HttpRequest> function,
            final HttpClient client,
            final HttpResponse.BodyHandler<R> handler) {
        Objects.requireNonNull(function, "function is null");
        Objects.requireNonNull(client, "client is null");
        Objects.requireNonNull(handler, "handler is null");
        return applyAsync(
                s -> {
                    final var array = new byte[HelloWorld.BYTES];
                    s.set(array);
                    return HttpRequest.BodyPublishers.ofByteArray(array);
                }
        ).thenApply(function).thenCompose(r -> client.sendAsync(r, handler));
    }

    // --------------------------------------------------------------------------- java.nio.channels

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous byte channel, and notifies a completion (or a failure) to the specified handler
     * with the specified attachment.
     *
     * @param <C>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the asynchronous byte channel to which the bytes are written.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException if either {@code channel} or {@code handler} is {@code null}.
     * @implSpec The default implementation invokes {@link #applyAsync(Function)} with a mapper that
     * prepares a {@value HelloWorld#BYTES}-byte source buffer via
     * {@link HelloWorld#put(ByteBuffer)} on the wrapped service; the resulting stage's
     * {@link CompletionStage#whenComplete(java.util.function.BiConsumer) whenComplete} starts the
     * recursive
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler) channel.write}
     * loop that, on the final completion, notifies {@code handler.completed(channel, attachment)} —
     * or, on failure, notifies {@code handler.failed(exc, attachment)}.
     * @see HelloWorld#put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     * @see #applyAsync(Function)
     */
    default <C extends AsynchronousByteChannel, A> void write_(
            final C channel, @Nullable final A attachment,
            final CompletionHandler<? super C, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        applyAsync(HelloWorldUtils::buffer).whenComplete((b, t) -> {
            if (t != null) {
                handler.failed(t, attachment);
                return;
            }
            channel.write(b, attachment, new CompletionHandler<>() { // @formatter:off
                @Override
                public void completed(final Integer result, final A attachment) {
                    if (b.hasRemaining()) {
                        channel.write(b, attachment, this);
                        return;
                    }
                    handler.completed(channel, attachment);
                }
                @Override
                public void failed(final Throwable exc, final A attachment) {
                    handler.failed(exc, attachment);
                } // @formatter:on
            });
        });
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous byte channel, and notifies a completion (or a failure) to the specified handler
     * with the specified attachment.
     *
     * @param <C>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the asynchronous byte channel to which the bytes are written.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException if either {@code channel} or {@code handler} is {@code null}.
     * @implSpec The default implementation invokes
     * {@link #applyAsync(Function, Object, CompletionHandler) applyAsync(mapper, attachment,
     * handler)} with a mapper that prepares a {@value HelloWorld#BYTES}-byte source buffer via
     * {@link HelloWorld#put(ByteBuffer)} on the wrapped service; on the mapper's completion, the
     * inner handler starts the recursive
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler) channel.write}
     * loop that, on the final completion, notifies {@code handler.completed(channel, attachment)} —
     * or, on failure (either of the mapper or of the channel write), notifies
     * {@code handler.failed(exc, attachment)}.
     * @see HelloWorld#put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     * @see #applyAsync(Function, Object, CompletionHandler)
     */
    default <C extends AsynchronousByteChannel, A> void write(
            final C channel, @Nullable final A attachment,
            final CompletionHandler<? super C, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        applyAsync(HelloWorldUtils::buffer, attachment, new CompletionHandler<>() { // @formatter:off
            @Override
            public void completed(final ByteBuffer b, final A attachment) {
                channel.write(b, attachment, new CompletionHandler<>() {
                    @Override
                    public void completed(final Integer result, final A attachment) {
                        if (b.hasRemaining()) {
                            channel.write(b, attachment, this);
                            return;
                        }
                        handler.completed(channel, attachment);
                    }
                    @Override
                    public void failed(final Throwable exc, final A attachment) {
                        handler.failed(exc, attachment);
                    }
                });
            }
            @Override
            public void failed(final Throwable exc, final A attachment) {
                handler.failed(exc, attachment);
            } // @formatter:on
        });
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous byte channel, and returns a {@link CompletionStage} that completes with the
     * specified attachment.
     *
     * @param <A>        attachment type parameter; the stage's payload is of this type.
     * @param channel    the asynchronous byte channel to which the bytes are written.
     * @param attachment the value with which the returned stage completes; may be {@code null}.
     * @return a {@link CompletionStage} that, on success, completes with the {@code attachment} —
     * or, on failure, completes exceptionally.
     * @throws NullPointerException if {@code channel} is {@code null}.
     * @implSpec The default implementation invokes
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler)} with a
     * {@link CompletionHandler} that completes the returned stage with the {@code attachment} on
     * success, or completes it exceptionally on failure.
     * @see #write(AsynchronousByteChannel, Object, CompletionHandler)
     */
    default <A> CompletionStage<A> write(final AsynchronousByteChannel channel,
                                         final @Nullable A attachment) {
        Objects.requireNonNull(channel, "channel is null");
        final var future = new CompletableFuture<A>();
        write(channel, attachment, new CompletionHandler<>() { // @formatter:off
            @Override
            public void completed(final AsynchronousByteChannel result, final A attachment) {
                future.complete(attachment);
            }
            @Override
            public void failed(final Throwable exc, final A attachment) {
                future.completeExceptionally(exc);
            } // @formatter:on
        });
        return future;
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous file channel starting at the specified position, and notifies a completion (or a
     * failure) to the specified handler with the specified attachment.
     *
     * @param <C>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the asynchronous file channel to which the bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException     if either {@code channel} or {@code handler} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code position} is negative.
     * @implSpec The default implementation invokes {@link #applyAsync(Function)} with a mapper that
     * prepares a {@value HelloWorld#BYTES}-byte source buffer via
     * {@link HelloWorld#put(ByteBuffer)} on the wrapped service; the resulting stage's
     * {@link CompletionStage#whenComplete(java.util.function.BiConsumer) whenComplete} starts the
     * recursive
     * {@link AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * channel.write} loop that, on the final completion, notifies
     * {@code handler.completed(channel, attachment)} — or, on failure, notifies
     * {@code handler.failed(exc, attachment)}.
     * @see HelloWorld#put(ByteBuffer)
     * @see AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * @see #applyAsync(Function)
     */
    default <C extends AsynchronousFileChannel, A> void write_(
            final C channel, final long position, final @Nullable A attachment,
            final CompletionHandler<? super C, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(handler, "handler is null");
        applyAsync(HelloWorldUtils::buffer).whenComplete((b, t) -> {
            if (t != null) {
                handler.failed(t, attachment);
                return;
            }
            channel.write(b, position, position, new CompletionHandler<>() { // @formatter:off
                @Override
                public void completed(final Integer result, Long position) {
                    if (b.hasRemaining()) {
                        position += result;
                        channel.write(b, position, position, this);
                        return;
                    }
                    handler.completed(channel, attachment);
                }

                @Override
                public void failed(final Throwable exc, final Long position) {
                    handler.failed(exc, attachment);
                } // @formatter:on
            });
        });
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous file channel starting at the specified position, and notifies a completion (or a
     * failure) to the specified handler with the specified attachment.
     *
     * @param <C>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the asynchronous file channel to which the bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException     if either {@code channel} or {@code handler} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code position} is negative.
     * @implSpec The default implementation invokes
     * {@link #applyAsync(Function, Object, CompletionHandler) applyAsync(mapper, attachment,
     * handler)} with a mapper that prepares a {@value HelloWorld#BYTES}-byte source buffer via
     * {@link HelloWorld#put(ByteBuffer)} on the wrapped service; on the mapper's completion, the
     * inner handler starts the recursive
     * {@link AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * channel.write} loop that, on the final completion, notifies
     * {@code handler.completed(channel, attachment)} — or, on failure (either of the mapper or of
     * the channel write), notifies {@code handler.failed(exc, attachment)}.
     * @see HelloWorld#put(ByteBuffer)
     * @see AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * @see #applyAsync(Function, Object, CompletionHandler)
     */
    default <C extends AsynchronousFileChannel, A> void write(
            final C channel, final long position, final @Nullable A attachment,
            final CompletionHandler<? super C, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(handler, "handler is null");
        applyAsync(HelloWorldUtils::buffer, attachment, new CompletionHandler<>() { // @formatter:off
            @Override
            public void completed(final ByteBuffer b, final A attachment) {
                channel.write(b, position, position, new CompletionHandler<>() {
                    @Override
                    public void completed(final Integer result, Long position) {
                        if (b.hasRemaining()) {
                            position += result;
                            channel.write(b, position, position, this);
                            return;
                        }
                        handler.completed(channel, attachment);
                    }
                    @Override
                    public void failed(final Throwable exc, final Long position) {
                        handler.failed(exc, attachment);
                    }
                });
            }
            @Override
            public void failed(final Throwable exc, final A attachment) {
                handler.failed(exc, attachment);
            } // @formatter:on
        });
    }

    /**
     * Writes the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the specified
     * asynchronous file channel starting at the specified position, and returns a
     * {@link CompletionStage} that completes with the specified attachment.
     *
     * @param <A>        attachment type parameter; the stage's payload is of this type.
     * @param channel    the asynchronous file channel to which the bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param attachment the value with which the returned stage completes; may be {@code null}.
     * @return a {@link CompletionStage} that, on success, completes with the {@code attachment} —
     * or, on failure, completes exceptionally.
     * @throws NullPointerException     if {@code channel} is {@code null}.
     * @throws IllegalArgumentException if {@code position} is negative.
     * @implSpec The default implementation invokes
     * {@link #write(AsynchronousFileChannel, long, Object, CompletionHandler)} with a
     * {@link CompletionHandler} that completes the returned stage with the {@code attachment} on
     * success, or completes it exceptionally on failure.
     * @see #write(AsynchronousFileChannel, long, Object, CompletionHandler)
     */
    default <A> CompletionStage<A> write(final AsynchronousFileChannel channel,
                                         final long position, final @Nullable A attachment) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        final var future = new CompletableFuture<A>();
        write(channel, position, attachment, new CompletionHandler<>() { // @formatter:off
            @Override
            public void completed(final AsynchronousFileChannel result, final A attachment) {
                future.complete(attachment);
            }
            @Override
            public void failed(final Throwable exc, final A attachment) {
                future.completeExceptionally(exc);
            } // @formatter:on
        });
        return future;
    }

    // ------------------------------------------------------------------------------- java.nio.file

    /**
     * Appends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the end of
     * the file at the specified path, and notifies a completion (or a failure) to the specified
     * handler with the specified attachment.
     *
     * @param <P>        path type parameter
     * @param <A>        attachment type parameter
     * @param path       the path to the file to which the bytes are appended.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException if either {@code path} or {@code handler} is {@code null}.
     * @implSpec The default implementation opens the file at the specified {@code path} as an
     * {@link AsynchronousFileChannel} with {@link StandardOpenOption#WRITE WRITE} and
     * {@link StandardOpenOption#CREATE CREATE}, queries its current
     * {@link AsynchronousFileChannel#size() size} as the starting position, then delegates to
     * {@link #write(AsynchronousFileChannel, long, Object, CompletionHandler)} with an inner
     * {@link CompletionHandler} that closes the channel before notifying the outer {@code handler}
     * — {@code handler.completed(path, attachment)} on success, or
     * {@code handler.failed(exc, attachment)} on failure (failures from {@code open} / {@code size}
     * / {@code close} are routed the same way).
     * @apiNote {@link AsynchronousFileChannel} does not support {@link StandardOpenOption#APPEND};
     * the end-of-file position is obtained via {@link AsynchronousFileChannel#size()} and used as
     * the starting write position. With concurrent writers, this is not OS-level atomic append.
     * @see AsynchronousFileChannel#open(Path, java.nio.file.OpenOption...)
     * @see #write(AsynchronousFileChannel, long, Object, CompletionHandler)
     */
    default <P extends Path, A>
    void append(final P path, final @Nullable A attachment,
                final CompletionHandler<? super P, ? super A> handler) {
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(handler, "handler is null");
        final AsynchronousFileChannel channel;
        try {
            channel = AsynchronousFileChannel.open(
                    path, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
        } catch (final IOException e) {
            handler.failed(e, attachment);
            return;
        }
        final long position;
        try {
            position = channel.size();
        } catch (final IOException e) {
            try {
                channel.close();
            } catch (final IOException s) {
                e.addSuppressed(s);
            }
            handler.failed(e, attachment);
            return;
        }
        write(channel, position, attachment, new CompletionHandler<>() { // @formatter:off
            @Override
            public void completed(final AsynchronousFileChannel result, final A attachment) {
                try {
                    channel.close();
                } catch (final IOException e) {
                    handler.failed(e, attachment);
                    return;
                }
                handler.completed(path, attachment);
            }
            @Override
            public void failed(final Throwable exc, final A attachment) {
                try {
                    channel.close();
                } catch (final IOException s) {
                    exc.addSuppressed(s);
                }
                handler.failed(exc, attachment);
            } // @formatter:on
        });
    }

    /**
     * Appends the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the end of
     * the file at the specified path, and returns a {@link CompletionStage} that completes with the
     * specified attachment.
     *
     * @param <A>        attachment type parameter; the stage's payload is of this type.
     * @param path       the path to the file to which the bytes are appended.
     * @param attachment the value with which the returned stage completes; may be {@code null}.
     * @return a {@link CompletionStage} that, on success, completes with the {@code attachment} —
     * or, on failure, completes exceptionally.
     * @throws NullPointerException if {@code path} is {@code null}.
     * @implSpec The default implementation invokes {@link #append(Path, Object, CompletionHandler)}
     * with a {@link CompletionHandler} that completes the returned stage with the
     * {@code attachment} on success, or completes it exceptionally on failure.
     * @see #append(Path, Object, CompletionHandler)
     */
    default <A>
    CompletionStage<A> append(final Path path, final @Nullable A attachment) { // @formatter:off
        Objects.requireNonNull(path, "path is null");
        final var future = new CompletableFuture<A>();
        append(path, attachment, new CompletionHandler<>() {
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
