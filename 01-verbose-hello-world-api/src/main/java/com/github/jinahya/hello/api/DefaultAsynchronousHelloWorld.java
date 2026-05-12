package com.github.jinahya.hello.api;

import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * A default implementation of {@link AsynchronousHelloWorld} interface, parameterized by the type
 * of the wrapped {@link HelloWorld} service.
 *
 * @param <T> the type of the wrapped {@link HelloWorld} service.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
final class DefaultAsynchronousHelloWorld<T extends HelloWorld>
        implements AsynchronousHelloWorld<T> {

    // -------------------------------------------------------------------------------- CONSTRUCTORS

    /**
     * Creates a new instance wrapping the specified service.
     *
     * @param service the service of type {@code T} for the
     *                <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>.
     * @throws NullPointerException if {@code service} is {@code null}.
     */
    DefaultAsynchronousHelloWorld(final T service) {
        super();
        this.service = Objects.requireNonNull(service, "service is null");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public <R> CompletionStage<R> applyAsync(final Function<? super T, ? extends R> mapper,
                                             final Executor executor) {
        Objects.requireNonNull(mapper, "mapper is null");
        Objects.requireNonNull(executor, "executor is null");
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

    // ---------------------------------------------------------------------------------------------
    private final T service;
}
