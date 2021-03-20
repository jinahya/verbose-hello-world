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

    // ----------------------------------------------------------------------------------------- AsynchronousFileChannel

    /**
     * Writes the {@code hello-world-bytes} to specified channel starting at the given file position. This method
     * invokes {@link #put(ByteBuffer)}} with a byte buffer of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes
     * and, after flips it, writes all remaining bytes of the buffer ot specified asynchronous file channel starting at
     * the given file position.
     *
     * @param channel the channel to be appended.
     * @param <T>     channel type parameter
     * @return specified channel.
     * @throws InterruptedException if interrupted while {@link Future#get() getting} the result from a {@code Future}.
     * @throws ExecutionException   if failed to {@link Future#get() get} the result from a {@code Future}.
     * @see #put(ByteBuffer)
     * @see AsynchronousFileChannel#write(ByteBuffer, long)
     */
    default <T extends AsynchronousFileChannel> T write(final T channel, long position)
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
     * @return specified channel.
     * @throws IOException          if an I/O error occurs.
     * @throws InterruptedException if interrupted while working.
     * @throws ExecutionException   if failed to execute.
     * @see AsynchronousFileChannel#size()
     * @see #write(AsynchronousFileChannel, long)
     */
    default <T extends AsynchronousFileChannel> T append(final T channel)
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
    default <T extends AsynchronousFileChannel> CompletableFuture<T> writeAsync(final T channel, final long position) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") < 0L");
        }
        final CompletableFuture<T> future = new CompletableFuture<>();
        final ByteBuffer buffer = (ByteBuffer) put(allocate(BYTES)).flip();
        channel.write(buffer, position, position, new CompletionHandler<Integer, Long>() {

            @Override
            public void completed(final Integer result, final Long attachment) {
                if (!buffer.hasRemaining()) {
                    future.complete(channel);
                    return;
                }
                final long position = attachment + result;
                channel.write(buffer, position, position, this);
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
    default <T extends AsynchronousFileChannel> CompletableFuture<T> appendAsync(final T channel)
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
    default <T extends AsynchronousByteChannel> T write(final T channel)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        for (final ByteBuffer buffer = (ByteBuffer) put(allocate(BYTES)).flip(); buffer.hasRemaining(); ) {
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
    default <T extends AsynchronousByteChannel> CompletableFuture<T> writeAsync(final T channel) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        final CompletableFuture<T> future = new CompletableFuture<>();
        final ByteBuffer buffer = (ByteBuffer) put(allocate(BYTES)).flip();
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
    default <T extends AsynchronousSocketChannel> T send(final T channel)
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
    default <T extends AsynchronousSocketChannel> CompletableFuture<T> sendAsync(final T channel) {
        return writeAsync(requireNonNull(channel, "channel is null"));
    }
}
