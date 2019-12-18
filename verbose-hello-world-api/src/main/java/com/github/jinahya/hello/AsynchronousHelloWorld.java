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
import static java.util.Objects.requireNonNull;

/**
 * An extended hello world interface for asynchronous operations.
 *
 * @author Jin Kwon &lt;jinahya_at_gmail.com&gt;
 */
interface AsynchronousHelloWorld extends HelloWorld {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Returns a byte buffer contains the {@code hello-world-bytes}. The result buffer's {@code position} is {@code 0}
     * and the {@code limit} is {@value com.github.jinahya.hello.HelloWorld#BYTES}. This method invokes {@link
     * #put(ByteBuffer)} with a newly allocated byte buffer of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes
     * and returns the buffer after {@link ByteBuffer#flip() flips} it.
     *
     * @return a byte buffer contains the {@code hello-world-bytes}.
     * @see HelloWorld#put(ByteBuffer)
     */
    default ByteBuffer put() {
        return (ByteBuffer) put(allocate(BYTES)).flip();
    }

    // ----------------------------------------------------------------------------------------- AsynchronousFileChannel

    /**
     * Writes the {@code hello-world-bytes} to specified channel starting at the given file position. This method
     * invokes {@link #put()} and writes all remaining bytes of the result buffer starting at the given file position.
     *
     * @param channel the channel to be appended.
     * @param <T>     channel type parameter
     * @return specified channel.
     * @throws InterruptedException if interrupted while {@link Future#get() getting} the result from a {@code Future}.
     * @throws ExecutionException   if failed to {@link Future#get() get} the result from a {@code Future}.
     * @see #put()
     * @see AsynchronousFileChannel#write(ByteBuffer, long)
     */
    default <T extends AsynchronousFileChannel> @NotNull T write(@NotNull final T channel, long position)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") < 0L");
        }
        for (final ByteBuffer buffer = (ByteBuffer) put(allocate(BYTES)).flip(); buffer.hasRemaining(); ) {
            position += channel.write(buffer, position).get();
        }
        return channel;
    }

    /**
     * Appends the {@code hello-world-bytes} to the end of specified channel. This method invokes {@link
     * #write(AsynchronousFileChannel, long)} method with given {@code channel} and the current {@link
     * AsynchronousFileChannel#size() size} of the channel and returns the result.
     *
     * @param channel the channel to be appended.
     * @param <T>     channel type parameter
     * @return specified channel
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while working.
     * @throws ExecutionException   if failed to execute.
     * @see AsynchronousFileChannel#size()
     * @see #write(AsynchronousFileChannel, long)
     */
    default <T extends AsynchronousFileChannel> @NotNull T append(@NotNull final T channel)
            throws IOException, InterruptedException, ExecutionException {
        return write(requireNonNull(channel, "channel is null"), channel.size());
    }

    /**
     * Writes, asynchronously, the {@code hello-world-bytes} to specified channel starting at given file position.
     *
     * @param channel  the channel to which bytes are written.
     * @param position the file position to start with writing the bytes.
     * @param <T>      channel type parameter
     * @return a completable future of specified channel.
     */
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

    /**
     * Appends, asynchronously, the {@code hello-world-bytes} to specified channel starting at the end of the file.
     *
     * @param channel the channel to which bytes are written.
     * @param <T>     channel type parameter
     * @return a completable future of specified channel.
     */
    default <T extends AsynchronousFileChannel> @NotNull CompletableFuture<T> appendAsync(@NotNull final T channel)
            throws IOException {
        return writeAsync(requireNonNull(channel, "channel is null"), channel.size());
    }

    // ----------------------------------------------------------------------------------------- AsynchronousByteChannel

    /**
     * Writes the {@code hello-world-bytes} to specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @param <T>     channel type parameter
     * @return given channel.
     * @throws InterruptedException if interrupted while working.
     * @throws ExecutionException   if failed to execute.
     * @see #writeAsync(AsynchronousByteChannel)
     */
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

    /**
     * Writes, asynchronously, the {@code hello-world-bytes} to specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @param <T>     channel type parameter
     * @return a completable future of given channel.
     * @see #write(AsynchronousByteChannel)
     */
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

    /**
     * Sends the {@code hello-world-bytes} to specified channel. This method simply invokes {@link
     * #write(AsynchronousByteChannel)} method with specified channel and returns the result.
     *
     * @param channel the channel to which bytes are written.
     * @param <T>     channel type parameter
     * @return given channel.
     * @throws InterruptedException if interrupted while working.
     * @throws ExecutionException   if failed to execute.
     * @see #write(AsynchronousByteChannel)
     */
    default <T extends AsynchronousSocketChannel> @NotNull T send(@NotNull final T channel)
            throws InterruptedException, ExecutionException {
        return write(requireNonNull(channel, "channel is null"));
    }

    /**
     * Sends, asynchronously, the {@code hello-world-bytes} to specified channel. This method simply invokes {@link
     * #writeAsync(AsynchronousByteChannel)} method with specified channel and returns the result.
     *
     * @param channel the channel to which bytes are written.
     * @param <T>     channel type parameter
     * @return given channel.
     * @see #write(AsynchronousByteChannel)
     */
    default <T extends AsynchronousSocketChannel> CompletableFuture<T> sendAsync(@NotNull final T channel) {
        return writeAsync(requireNonNull(channel, "channel is null"));
    }
}
