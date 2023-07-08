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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.LongAdder;

/**
 * An interface for writing <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to
 * various targets.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@FunctionalInterface
public interface AsynchronousHelloWorld extends HelloWorld {

    /**
     * Returns a logger for this interface.
     *
     * @return a logger for this interface.
     */
    private Logger log() {
        return LoggerFactory.getLogger(getClass());
    }

    /**
     * Returns a logger for this interface.
     *
     * @return a logger for this interface.
     */
    private System.Logger logger() {
        return System.getLogger(getClass().getName());
    }

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to specified channel using specified executor.
     *
     * @param <T>      channel type parameter
     * @param channel  the channel to which bytes are written.
     * @param executor the executor.
     * @return a future of given {@code channel} that will, some time in the future, write the
     * <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to {@code channel}.
     * @see #put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer)
     */
    default <T extends AsynchronousByteChannel> Future<T> writeAsync(T channel, Executor executor) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(executor, "executor is null");
        Callable<T> callable = () -> {
            // TODO: Write hello-world-bytes to the channel!
            return null;
        };
        FutureTask<T> command = new FutureTask<>(callable);
        executor.execute(command); // Runnable
        return command;            // Future<T>
    }

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to specified channel, and handles completion on specified handler along with specified
     * attachment.
     *
     * @param <T>        channel type parameter
     * @param channel    the channel to which bytes are written.
     * @param handler    the completion handler.
     * @param attachment the attachment for the {@code handler}.
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     */
    default <T extends AsynchronousByteChannel, U> void write(
            T channel, CompletionHandler<? super T, ? super U> handler, final U attachment) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        // TODO: Implement!
    }

    default <T extends AsynchronousByteChannel> CompletableFuture<T> writeCompletable(T channel) {
        Objects.requireNonNull(channel, "channel is null");
        var future = new CompletableFuture<T>();
        // TODO: Implement!
        return future;
    }

    /**
     * Writes, asynchronously, the <a href="#hello-world-bytes">hello-world-bytes</a> to specified
     * channel starting at the given file position, using specified executor.
     *
     * @param <T>      channel type parameter
     * @param channel  the channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @param executor the executor for submitting a task.
     * @return a future of specified channel which, some time in the future, writes the <a
     * href="HelloWorld#hello-world-bytes">hello-world-bytes</a> to {@code channel} starting at
     * {@code position}.
     * @implSpec The default implementation submits, to {@code executor}, a task which invokes
     * {@link #put(ByteBuffer) put(buffer)} with a buffer of {@value HelloWorld#BYTES}, and writes
     * the buffer to {@code channel} starting at {@code position}.
     */
    default <T extends AsynchronousFileChannel> Future<T> write(T channel, long position,
                                                                Executor executor) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(executor, "executor is null");
        Callable<T> callable = () -> {
            var buffer = put(ByteBuffer.allocate(BYTES)).flip();
            // TODO: Implement!
            return channel;
        };
        FutureTask<T> command = new FutureTask<>(callable);
        executor.execute(command);
        return command;
    }

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to specified channel, starting at specified file position, and invokes
     * {@link CompletionHandler#completed(Object, Object) complete(result, attachment)} method, on
     * specified completion handler, with {@value #BYTES} and specified file channel, when all bytes
     * are written.
     *
     * @param <T>      channel type parameter
     * @param channel  the channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @param handler  the completion handler.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer) put(buffer)} method with
     * a byte buffer of {@value #BYTES} bytes, flips it, and writes the buffer to {@code channel} by
     * recursively handling the callback called inside the
     * {@link AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * channel#(src, position, attachment, handler)} method, and eventually invokes
     * {@code handler.completed(12, channel)} when all bytes are written.
     * @see #put(ByteBuffer)
     * @see AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     */
    default <T extends AsynchronousFileChannel> void write(
            T channel, long position, CompletionHandler<Integer, ? super T> handler) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(handler, "handler is null");
        var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        channel.write(
                buffer,
                position,
                new LongAdder(),
                new CompletionHandler<>() {
                    @Override
                    public void completed(Integer result, LongAdder attachment) {
                        attachment.add(result);
                        if (!buffer.hasRemaining()) {
                            handler.completed(attachment.intValue(), channel);
                            return;
                        }
                        channel.write(
                                buffer,
                                position + attachment.longValue(),
                                attachment,
                                this
                        );
                    }

                    @Override
                    public void failed(Throwable exc, LongAdder attachment) {
                        handler.failed(exc, channel);
                    }
                }
        );
    }

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
