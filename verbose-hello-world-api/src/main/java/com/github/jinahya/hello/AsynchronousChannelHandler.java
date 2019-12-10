package com.github.jinahya.hello;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

abstract class AsynchronousChannelHandler<T extends AsynchronousChannel>
        implements CompletionHandler<Integer, ByteBuffer> {

    // -----------------------------------------------------------------------------------------------------------------
    static <T extends AsynchronousFileChannel> AsynchronousChannelHandler<T> newInstance(
            final T channel, final CompletableFuture<T> future) {
        return new AsynchronousChannelHandler<T>(channel, future) {
            @Override
            public void completed(final Integer result, final ByteBuffer attachment) {
                if (!attachment.hasRemaining()) {
                    future.complete(channel);
                    return;
                }
                final long position;
                try {
                    position = channel.size();
                } catch (final IOException ioe) {
                    failed(ioe, attachment);
                    return;
                }
                channel.write(attachment, position, attachment, this);
            }
        };
    }

    static <T extends AsynchronousByteChannel> AsynchronousChannelHandler<T> newInstance(
            final T channel, final CompletableFuture<T> future) {
        return new AsynchronousChannelHandler<T>(channel, future) {
            @Override
            public void completed(final Integer result, final ByteBuffer attachment) {
                if (!attachment.hasRemaining()) {
                    future.complete(channel);
                    return;
                }
                channel.write(attachment, attachment, this);
            }
        };
    }

    // -----------------------------------------------------------------------------------------------------------------
    AsynchronousChannelHandler(final T channel, final CompletableFuture<T> future) {
        super();
        this.channel = requireNonNull(channel, "channel is null");
        this.future = requireNonNull(future, "future is null");
    }

    // -----------------------------------------------------------------------------------------------------------------
    @Override
    public void failed(final Throwable exc, final ByteBuffer attachment) {
        future.completeExceptionally(exc);
    }

    // -----------------------------------------------------------------------------------------------------------------
    protected final T channel;

    protected final CompletableFuture<T> future;
}
