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

import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * An interface for generating <a href="#hello-world-bytes">hello-world-bytes</a> to various
 * targets.
 * <p>
 * All methods defined in this interface are thead-safe.
 *
 * <h2 id="hello-world-bytes">hello-world-bytes</h2>
 * A sequence of {@value #BYTES} bytes, representing the "{@code hello, world}" string encoded in
 * {@link java.nio.charset.StandardCharsets#US_ASCII US_ASCII} character set, which consists of
 * {@code 0x68("h")} followed by {@code 0x65("e")}, {@code 0x6C("l")}, {@code 0x6C("l")}, {@code
 * 0x6F("o")}, {@code 0x2C(",")}, {@code 0x20(" ")}, {@code 0x77("w")}, {@code 0x6F("o")}, {@code
 * 0x72("r")}, {@code 0x6C("l")}, and {@code 0x64("d")}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@FunctionalInterface
public interface HelloWorld {

    /**
     * The length of the <a href="#hello-world-bytes">hello-world-bytes</a> which is {@value}.
     */
    int BYTES = 12;

    /**
     * Sets <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at
     * specified position.
     * <p>
     * The elements in the array, on successful return, will be set as follows.
     * <pre>
     *   0    &lt;= index                                  index + 12    &lt;= array.length
     *   ↓       ↓                                               ↓       ↓
     * |   |...|"h"|"e"|"l"|"l"|"o"|","|" "|"w"|"o"|"r"|"l"|"d"|...|   |
     * </pre>
     *
     * @param array the array on which bytes are set.
     * @param index the starting index of the {@code array}.
     * @return given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code index} is negative or ({@code index} + {@value
     *                                   #BYTES}) is greater than {@code array.length}.
     */
    byte[] set(byte[] array, int index);

    /**
     * Sets <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at {@code
     * 0}.
     *
     * @param array the array on which bytes are set.
     * @return given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code array.length} is less than {@link #BYTES}.
     * @implSpec The default implementation invokes {@link #set(byte[], int) set(array, index)}
     * method with specified {@code array} and {@code 0}.
     * @see #set(byte[], int)
     */
    default byte[] set(byte[] array) {
        // TODO: Implement!
        return null;
    }

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> to specified output stream.
     *
     * @param <T>    stream type parameter
     * @param stream the output stream to which bytes are written.
     * @return given {@code stream}.
     * @throws NullPointerException if {@code stream} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #set(byte[]) set(array)} method with an
     * array of {@value #BYTES} bytes and writes the array to {@code stream} by invoking {@link
     * OutputStream#write(byte[]) stream.write(array)} method.
     * @see #set(byte[])
     * @see OutputStream#write(byte[])
     */
    default <T extends OutputStream> T write(T stream) throws IOException {
        // TODO: implement!
        return null;
    }

    /**
     * Appends <a href="#hello-world-bytes">hello-world-bytes</a> to the end of specified file.
     *
     * @param <T>  channel type parameter
     * @param file the file to which bytes are appended.
     * @return given {@code file}.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation creates a {@link FileOutputStream} from the {@code file}
     * as {@link FileOutputStream#FileOutputStream(File, boolean) appending mode} and invokes {@link
     * #write(OutputStream)} method with the stream.
     * @see java.io.FileOutputStream#FileOutputStream(File, boolean)
     * @see #write(OutputStream)
     */
    default <T extends File> T append(T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // TODO: Implement!
        return file;
    }

    /**
     * Sends <a href="#hello-world-bytes">hello-world-bytes</a> through specified socket.
     *
     * @param <T>    socket type parameter
     * @param socket the socket through which bytes are sent.
     * @return given {@code socket}.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #write(OutputStream)} method with what
     * {@link Socket#getOutputStream()} method, invoked on {@code socket}, returns .
     * @see Socket#getOutputStream()
     * @see #write(OutputStream)
     */
    default <T extends Socket> T send(T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        // TODO: Implement!
        return socket;
    }

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> to specified data output.
     *
     * @param <T>  channel type parameter
     * @param data the data output to which bytes are written.
     * @return given {@code data}.
     * @throws NullPointerException if {@code data} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #set(byte[])} method with an array of
     * {@value #BYTES} bytes and writes the array to specified data output by invoking {@link
     * DataOutput#write(byte[])} method on {@code data} with the {@code array}.
     * @see #set(byte[])
     * @see DataOutput#write(byte[])
     */
    default <T extends DataOutput> T write(T data) throws IOException {
        if (data == null) {
            throw new NullPointerException("data is null");
        }
        byte[] array = set(new byte[BYTES]);
        // TODO: Implement!
        return data;
    }

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> to specified random access file
     * starting at its current file pointer.
     *
     * @param <T>  channel type parameter
     * @param file the random access file to which bytes are written.
     * @return given {@code file}.
     * @throws NullPointerException if {@code file} argument is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #set(byte[])} method with an array of
     * {@value #BYTES} bytes and writes the array to specified random access file by invoking {@link
     * DataOutput#write(byte[])} method on {@code file} with the array.
     * @see #set(byte[])
     * @see RandomAccessFile#write(byte[])
     */
    default <T extends RandomAccessFile> T write(T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        byte[] array = new byte[BYTES];
        set(array);
        file.write(array);
        return file;
    }

    /**
     * Puts <a href="#hello-world-bytes">hello-world-bytes</a> on specified byte buffer. The
     * buffer's position, on successful return, is incremented by {@value #BYTES}.
     * <pre>
     * Given,
     *           |------------------------ remaining ------------------------|
     *   0    &lt;= position                                                 &lt;= limit    &lt;= capacity
     *   ↓       ↓                                                           ↓           ↓
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *
     * On successful return,
     *   0                                                    &lt;= position &lt;= limit    &lt;= capacity
     *   ↓                                                       ↓           ↓           ↓
     * |   |   |"h"|"e"|"l"|"l"|"o"|","|" "|"w"|"o"|"r"|"l"|"d"|   |   |   |   |   |   |
     *                                                           |-remaining-|
     * </pre>
     *
     * @param <T>    channel type parameter
     * @param buffer the byte buffer on which bytes are put.
     * @return given {@code buffer}.
     * @throws NullPointerException    if {@code buffer} is {@code null}.
     * @throws BufferOverflowException if {@link ByteBuffer#remaining() buffer.remaining} is less
     *                                 than {@value #BYTES}.
     * @implSpec The default implementation, if specified buffer {@link ByteBuffer#hasArray() has a
     * backing-array}, invokes {@link #set(byte[], int) #set(array, index)} with the buffer"s
     * backing-array and ({@code buffer.arrayOffset} + {@code buffer.position}) and then manually
     * increments the buffer"s position by {@value #BYTES}. Otherwise, this method invokes {@link
     * #set(byte[]) #set(array)} method with an array of {@value #BYTES} bytes and puts the array on
     * the buffer by invoking {@link ByteBuffer#put(byte[])} method on {@code buffer} with the
     * array.
     * @see ByteBuffer#hasArray()
     * @see ByteBuffer#array()
     * @see ByteBuffer#arrayOffset()
     * @see ByteBuffer#position()
     * @see #set(byte[], int)
     * @see #set(byte[])
     * @see ByteBuffer#put(byte[])
     */
    default <T extends ByteBuffer> T put(T buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer is null");
        }
        if (buffer.remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        if (buffer.hasArray()) {
            // TODO: Implement!
        } else {
            // TODO: Implement!
        }
        return buffer;
    }

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return given {@code channel}.
     * @throws NullPointerException if {@code channel} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer)} method with a buffer of
     * {@value #BYTES} bytes, flips the buffer, and writes the buffer to {@code channel} by
     * continuously invoking {@link WritableByteChannel#write(ByteBuffer)}  method on {@code
     * channel} with the buffer while the buffer has remaining.
     * @see #put(ByteBuffer)
     * @see ByteBuffer#flip()
     * @see ByteBuffer#hasRemaining()
     * @see WritableByteChannel#write(ByteBuffer)
     */
    default <T extends WritableByteChannel> T write(T channel) throws IOException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        // TODO: Implement!
        return channel;
    }

    /**
     * Appends <a href="#hello-world-bytes">hello-world-bytes</a> to the end of specified path to a
     * file. The length of the file, on successful return, is increased by {@value #BYTES}.
     *
     * @param <T>  channel type parameter
     * @param path the path a file to which bytes are appended.
     * @return given {@code path}.
     * @throws NullPointerException if {@code path} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation opens a {@link FileChannel} from specified path as
     * {@link java.nio.file.StandardOpenOption#APPEND appending mode} and invokes {@link
     * #write(WritableByteChannel)} method with it.
     * @see FileChannel#open(Path, OpenOption...)
     * @see #write(WritableByteChannel)
     */
    default <T extends Path> T append(T path) throws IOException {
        if (path == null) {
            throw new NullPointerException("path is null");
        }
        // TODO: Implement!
        return path;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return given {@code channel}.
     * @throws InterruptedException if interrupted while working.
     * @throws ExecutionException   if failed to operate.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer)} method with a byte
     * buffer of {@value #BYTES} bytes, flips it, and write the buffer to {@code channel} by
     * repeatedly getting the result of {@link AsynchronousByteChannel#write(ByteBuffer)} method
     * invoked on {@code channel} with the buffer while the buffer has remaining.
     */
    default <T extends AsynchronousByteChannel> T write(T channel)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        // TODO: Implement!
        return channel;
    }

    /**
     * Writes, asynchronously, the <a href="#hello-world-bytes">hello-world-bytes</a> to specified
     * channel using specified executor.
     *
     * @param <T>      channel type parameter
     * @param channel  the channel to which bytes are written.
     * @param executor the executor service to which a task is submitted.
     * @return A future representing the result of the operation.
     * @implSpec The default implementation submits a task, which simply returns the result of
     * {@link #write(AsynchronousByteChannel)} method invoked with {@code channel}, to specified
     * executor.
     */
    default <T extends AsynchronousByteChannel> Future<T> writeAsync(
            T channel, ExecutorService executor) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        if (executor == null) {
            throw new NullPointerException("executor is null");
        }
        return executor.submit(() -> {
            // TODO: Implement!
            return channel;
        });
    }

    /**
     * Returns a completable future which writes the <a href="#hello-world-bytes">hello-world-bytes</a>
     * to specified channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return a completable future of {@code channel}.
     */
    default <T extends AsynchronousByteChannel> CompletableFuture<T> writeCompletable(T channel) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        var future = new CompletableFuture<T>();
        var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        future.complete(channel); // TODO: Replace!!!
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
     * repeatedly getting the result of {@link AsynchronousFileChannel#write(ByteBuffer, long)
     * channel.write(buffer)} while the buffer has remaining.
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
        var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
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
        var future = new CompletableFuture<T>();
        var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        channel.write(buffer,                     // src
                      position,                   // position
                      position,                   // attachment
                      new CompletionHandler<>() { // handler
                          @Override
                          public void completed(Integer result, Long attachment) {
                              if (!buffer.hasRemaining()) {            // <1>
                                  future.complete(channel);            // <2>
                                  return;
                              }
                              attachment += result;                    // <3>
                              channel.write(buffer,     // src         // <4>
                                            attachment, // position
                                            attachment, // attachment
                                            this);      // handler
                          }

                          @Override
                          public void failed(Throwable exc, Long attachment) {
                              future.completeExceptionally(exc);
                          }
                      });
        return future;
    }
}
