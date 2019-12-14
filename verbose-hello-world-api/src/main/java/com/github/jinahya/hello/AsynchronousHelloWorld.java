package com.github.jinahya.hello;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import static java.nio.ByteBuffer.allocate;

/**
 * An extended hello world interface for asynchronous operations.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
interface AsynchronousHelloWorld extends HelloWorld {

    // -----------------------------------------------------------------------------------------------------------------
    default ByteBuffer put() {
        final ByteBuffer buffer = allocate(BYTES); // position = 0, limit,capacity = 12
        put(buffer); // position -> 12
        buffer.flip(); // limit -> position(12), position -> 0
        return buffer;
    }

    // ----------------------------------------------------------------------------------------- AsynchronousFileChannel
    default <T extends AsynchronousFileChannel> @NotNull T write(@NotNull final T channel, long position)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") < 0L");
        }
        for (final ByteBuffer buffer = put(); buffer.hasRemaining(); ) {
            final Future<Integer> future = channel.write(buffer, position);
            position += future.get();
        }
        return channel;
    }

    default <T extends AsynchronousFileChannel> @NotNull T append(@NotNull final T channel)
            throws IOException, InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        return write(channel, channel.size());
    }

    default <T extends AsynchronousFileChannel> @NotNull CompletableFuture<T> writeAsync(@NotNull final T channel,
                                                                                         final long position) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") < 0L");
        }
        final CompletableFuture<T> future = new CompletableFuture<>();
        final ByteBuffer buffer = put();
        channel.write(buffer, position, position, new CompletionHandler<Integer, Long>() {

            @Override
            public void completed(final Integer result, final Long attachment) {
                if (!buffer.hasRemaining()) {
                    future.complete(channel);
                    return;
                }
                final long newPosition = attachment + result;
                channel.write(buffer, newPosition, newPosition, this);
            }

            @Override
            public void failed(final Throwable exc, final Long attachment) {
                future.completeExceptionally(exc);
            }
        });
        return future;
    }

    default <T extends AsynchronousFileChannel> @NotNull CompletableFuture<T> appendAsync(@NotNull final T channel)
            throws IOException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        return writeAsync(channel, channel.size());
    }

    // ----------------------------------------------------------------------------------------- AsynchronousByteChannel
    default <T extends AsynchronousByteChannel> @NotNull T write(@NotNull final T channel)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        for (final ByteBuffer buffer = put(); buffer.hasRemaining(); ) {
            final Future<Integer> future = channel.write(buffer);
            final int written = future.get();
        }
        return channel;
    }

    default <T extends AsynchronousByteChannel> CompletableFuture<T> writeAsync(@NotNull final T channel) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        final CompletableFuture<T> future = new CompletableFuture<>();
        final ByteBuffer buffer = put();
        channel.write(buffer, null, new CompletionHandler<Integer, Void>() {
            @Override
            public void completed(final Integer result, final Void attachment) {
                if (!buffer.hasRemaining()) {
                    future.complete(channel);
                    return;
                }
                channel.write(buffer, null, this);
            }

            @Override
            public void failed(final Throwable exc, final Void attachment) {
                future.completeExceptionally(exc);
            }
        });
        return future;
    }

    // --------------------------------------------------------------------------------------- AsynchronousSocketChannel
    default <T extends AsynchronousSocketChannel> @NotNull T send(@NotNull final T channel)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        return write(channel);
    }

    default <T extends AsynchronousSocketChannel> CompletableFuture<T> sendAsync(@NotNull final T channel) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        return writeAsync(channel);
    }
}
