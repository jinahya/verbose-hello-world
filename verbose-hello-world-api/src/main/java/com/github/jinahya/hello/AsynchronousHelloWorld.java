package com.github.jinahya.hello;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.IntConsumer;

/**
 * An extended hello world interface for asynchronous operations.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
public interface AsynchronousHelloWorld extends HelloWorld {

    // -----------------------------------------------------------------------------------------------------------------
    default <T extends AsynchronousFileChannel> @NotNull T append(@NotNull final T channel)
            throws IOException, InterruptedException, ExecutionException {
        if (false && channel == null) {
            throw new NullPointerException("channel is null");
        }
        for (final ByteBuffer buffer = put(); buffer.hasRemaining(); ) {
            final Future<Integer> future = channel.write(buffer, channel.size());
            final int written = future.get();
        }
        return channel;
    }

    default <T extends AsynchronousFileChannel> @NotNull CompletableFuture<T> appendAsync(@NotNull final T channel)
            throws IOException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        final CompletableFuture<T> future = new CompletableFuture<>();
        final ByteBuffer buffer = put();
        channel.write(buffer, channel.size(), buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(final Integer result, final ByteBuffer attachment) {
                if (!attachment.hasRemaining()) {
                    future.complete(channel);
                    return;
                }
                try {
                    channel.write(attachment, channel.size(), attachment, this);
                } catch (final IOException ioe) {
                    failed(ioe, attachment);
                }
            }

            @Override
            public void failed(final Throwable exc, final ByteBuffer attachment) {
                future.completeExceptionally(exc);
            }
        });
        return future;
    }

    // -----------------------------------------------------------------------------------------------------------------
    default <T extends AsynchronousByteChannel> @NotNull T write(@NotNull final T channel, final IntConsumer consumer)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        for (final ByteBuffer buffer = put(); buffer.hasRemaining(); ) {
            final Future<Integer> future = channel.write(buffer);
            final int written = future.get();
            if (consumer != null) {
                consumer.accept(written);
            }
        }
        return channel;
    }

    default <T extends AsynchronousByteChannel> CompletableFuture<T> writeAsync(@NotNull final T channel) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        final CompletableFuture<T> future = new CompletableFuture<>();
        final ByteBuffer buffer = put();
        final CompletionHandler<Integer, ByteBuffer> handler = new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(final Integer result, final ByteBuffer attachment) {
                if (!attachment.hasRemaining()) {
                    future.complete(channel);
                    return;
                }
                channel.write(attachment, attachment, this);
            }

            @Override
            public void failed(final Throwable exc, final ByteBuffer attachment) {
                future.completeExceptionally(exc);
            }
        };
        channel.write(buffer, null, handler);
        return future;
    }

    default <T extends AsynchronousSocketChannel> @NotNull T send(@NotNull final T channel, final IntConsumer consumer)
            throws InterruptedException, ExecutionException {
        return write(channel, consumer);
    }

    default <T extends AsynchronousSocketChannel> CompletableFuture<T> sendAsync(@NotNull final T channel) {
        return writeAsync(channel);
    }
}
