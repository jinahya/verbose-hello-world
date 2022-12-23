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

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * An interface for writing <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to
 * various targets.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@FunctionalInterface
public interface AsynchronousHelloWorld extends HelloWorld {

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to specified channel using specified executor.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return a future of given {@code channel} that will, some time in the future, write the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to {@code channel}.
     * @see #put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer)
     */
    default <T extends AsynchronousByteChannel> Future<T> write(T channel, Executor executor) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(executor, "executor is null");
        var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        Callable<T> callable = () -> {
            // TODO: Implement!
            return channel;
        };
        FutureTask<T> command = new FutureTask<>(callable);
        executor.execute(command);
        return command;
    }

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to specified channel, and invokes specified completion handler when all bytes are written.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @param handler the completion handler.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer) put(buffer)} method with
     * a byte buffer of {@value #BYTES} bytes, flips it, and writes the buffer to {@code channel} by
     * recursively handling the callback called inside the
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler) channel#(src,
     * attachment, handler)} method, and eventually invokes {@code handler} when all bytes are
     * written.
     * @see #put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     */
    default <T extends AsynchronousByteChannel> void write(
            T channel, CompletionHandler<Integer, ? super T> handler) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        // TODO: Implement!
    }

    /**
     * Returns a completable future which writes the <a
     * href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return a completable future of {@code channel}.
     */
    default <T extends AsynchronousByteChannel> CompletableFuture<T> write(T channel) {
        Objects.requireNonNull(channel, "channel is null");
        return new CompletableFuture<T>().completeAsync(() -> {
            // TODO: Implement!
            return null;
        });
    }

    /**
     * Returns a completable future which writes the <a
     * href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return a completable future of {@code channel}.
     */
    default <T extends AsynchronousByteChannel> CompletableFuture<T> writeCompletableAsync(
            T channel) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        var future = new CompletableFuture<T>();
        var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        // TODO: Implement!
        future.complete(channel); // TODO: Remove!!!
        return future;
    }

    /**
     * Writes, synchronously, the <a href="#hello-world-bytes">hello-world-bytes</a> to specified
     * channel, starting at the given file position.
     *
     * @param <T>      channel type parameter
     * @param channel  the channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @return given {@code channel}.
     * @throws InterruptedException if interrupted while working.
     * @throws ExecutionException   if failed to operate.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer)} method with a byte
     * buffer of {@value #BYTES} bytes, flips it, and writes the buffer to {@code channel} by
     * repeatedly getting the result of
     * {@link AsynchronousFileChannel#write(ByteBuffer, long) channel.write(buffer)} method while
     * the buffer has remaining.
     * @see #put(ByteBuffer)
     * @see AsynchronousFileChannel#write(ByteBuffer, long)
     */
    default <T extends AsynchronousFileChannel> T write(T channel, long position)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        for (var buffer = put(ByteBuffer.allocate(BYTES)).flip(); buffer.hasRemaining(); ) {
            var future = channel.write(buffer, position); // <1>
            int written = future.get();                   // <2>
            position += written;                          // <3>
        }
        return channel;
    }

    /**
     * Writes, asynchronously, the <a href="#hello-world-bytes">hello-world-bytes</a> to specified
     * channel starting at the given file position.
     *
     * @param <T>      channel type parameter
     * @param channel  the channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @param executor an executor service for submitting a task.
     * @return A future representing the result of the operation.
     * @implSpec The default implementation submits, to specified executor, a task which simply
     * returns the result of {@link #write(AsynchronousFileChannel, long) #write(channel, position)}
     * method.
     * @see #write(AsynchronousFileChannel, long)
     */
    default <T extends AsynchronousFileChannel> Future<T> writeAsync(
            T channel, long position, ExecutorService executor) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        if (executor == null) {
            throw new NullPointerException("executor is null");
        }
        return executor.submit(() -> write(channel, position));
    }

    /**
     * Writes, asynchronously, the <a href="#hello-world-bytes">hello-world-bytes</a> to specified
     * asynchronous file channel, starting at the given file position.
     *
     * @param <T>      channel type parameter
     * @param channel  the asynchronous file channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @return A completable future representing the result of the operation.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer)} method with a byte
     * buffer of {@value #BYTES} bytes, flips it, and writes the buffer to {@code channel} using
     * {@link AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)} method
     * while the buffer has remaining.
     * @see #write(AsynchronousFileChannel, long)
     */
    default <T extends AsynchronousFileChannel> CompletableFuture<T> writeCompletable(
            T channel, long position) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        var future = new CompletableFuture<T>();
        channel.write(buffer,                     // src
                      position,                   // position
                      position,                   // attachment
                      new CompletionHandler<>() { // handler
                          @Override
                          public void completed(Integer result, Long attachment) {
                              if (!buffer.hasRemaining()) { // <1>
                                  future.complete(channel); // <2>
                                  return;
                              }
                              attachment += result;                   // <1>
                              channel.write(buffer,     // src        // <2>
                                            attachment, // position   // <3>
                                            attachment, // attachment // <4>
                                            this);      // handler    // <5>
                          }

                          @Override
                          public void failed(Throwable exc, Long attachment) {
                              future.completeExceptionally(exc);
                          }
                      });
        return future;
    }
}
