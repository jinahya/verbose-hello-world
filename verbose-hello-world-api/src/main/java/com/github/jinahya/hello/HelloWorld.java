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
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * An interface for generating <a href="#hello-world-bytes">hello-world-bytes</a> to various targets.
 *
 * <h2 id="hello-world-bytes">hello-world-bytes</h2>
 * A sequence of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes, representing the "{@code hello, world}"
 * string encoded in {@link java.nio.charset.StandardCharsets#US_ASCII US-ASCII} character set, which consists of {@code
 * 0x68('h')} followed by {@code 0x65('e')}, {@code 0x6C('l')}, {@code 0x6C('l')}, {@code 0x6F('o')}, {@code 0x2C(',')},
 * {@code 0x20(' ')}, {@code 0x77('w')}, {@code 0x6F('o')}, {@code 0x72('r')}, {@code 0x6C('l')}, and {@code
 * 0x64('d')}.
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
     * Sets <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at specified position.
     * <p>
     * The elements in the array, on successful return, will be set as follows.
     * <blockquote><pre>{@code
     *   0                                                                    array.length
     *   ↓                                                                    ↓
     * |   |...|'h'|'e'|'l'|'l'|'o'|','|' '|'w'|'o'|'r'|'l'|'d'|   |...|   |
     *           ↑                                                ↑
     *      0 <= index                                            (index + BYTES) <= array.length
     * }</pre></blockquote>
     *
     * @param array the array on which bytes are set.
     * @param index the starting index of the {@code array}.
     * @return given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code index} is negative or ({@code index} + {@link #BYTES}) is greater
     *                                   than {@code array.length}.
     */
    byte[] set(byte[] array, int index);

    /**
     * Sets <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at {@code 0}.
     *
     * @param array the array on which bytes are set.
     * @return given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code array.length} is less than {@link #BYTES}.
     * @implSpec The implementation in this class invokes {@link #set(byte[], int)} method with specified {@code array}
     * and {@code 0}.
     * @see #set(byte[], int)
     */
    default byte[] set(final byte[] array) {
        // TODO: Implement!
        return null;
    }

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> to specified output stream.
     *
     * @param stream the output stream to which bytes are written.
     * @throws NullPointerException if {@code stream} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class invokes {@link #set(byte[])} method with an array of {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the array to specified {@code stream} using {@link
     * OutputStream#write(byte[])} method.
     * @see #set(byte[])
     * @see OutputStream#write(byte[])
     */
    default <T extends OutputStream> T write(final T stream) throws IOException {
        // TODO: implement!
        return null;
    }

    /**
     * Appends <a href="#hello-world-bytes">hello-world-bytes</a> to the end of specified file.
     *
     * @param file the file to which bytes are appended.
     * @return given {@code file}.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class creates a {@link FileOutputStream} from {@code file} as append mode
     * and invokes {@link #write(OutputStream)} method with the stream.
     * @see java.io.FileOutputStream#FileOutputStream(File, boolean)
     * @see #write(OutputStream)
     */
    default <T extends File> T append(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // TODO: Implement!
        return file;
    }

    /**
     * Sends <a href="#hello-world-bytes">hello-world-bytes</a> through specified socket.
     *
     * @param socket the socket through which bytes are sent.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class invokes {@link #write(OutputStream)} method with {@link
     * Socket#getOutputStream() socket.outputStream}.
     * @see Socket#getOutputStream()
     * @see #write(OutputStream)
     */
    default <T extends Socket> T send(final T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        // TODO: Implement!
        return socket;
    }

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> to specified data output.
     *
     * @param data the data output to which bytes are written.
     * @return given {@code data}.
     * @throws NullPointerException if {@code data} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class invokes {@link #set(byte[])} with an array of {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the array to specified data output using {@link
     * DataOutput#write(byte[])} method.
     * @see #set(byte[])
     * @see DataOutput#write(byte[])
     */
    default <T extends DataOutput> T write(final T data) throws IOException {
        Objects.requireNonNull(data, "data is null");
        final byte[] array = set(new byte[BYTES]);
        // TODO: Implement!
        return data;
    }

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> starting at the current file pointer of specified
     * random access file.
     *
     * @param file the random access file to which bytes are written.
     * @throws NullPointerException if {@code file} argument is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class invokes {@link #set(byte[])} with an array of {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the array to specified random access file using
     * {@link RandomAccessFile#write(byte[])} method.
     * @see #set(byte[])
     * @see RandomAccessFile#write(byte[])
     */
    default <T extends RandomAccessFile> T write(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        final byte[] array = new byte[BYTES];
        set(array);
        file.write(array);
        return file;
    }

    /**
     * Puts <a href="#hello-world-bytes">hello-world-bytes</a> on specified byte buffer. The buffer's position, on
     * successful return, is incremented by {@value com.github.jinahya.hello.HelloWorld#BYTES}.
     * <pre>
     * Given,
     *           |------------------------ remaining ------------------------|
     *   0    <= position                                                 <= limit    <= capacity
     *   ↓       ↓                                                           ↓           ↓
     *   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *
     * On successful return,
     *   0                                                    <= position <= limit    <= capacity
     *   ↓                                                       ↓           ↓           ↓
     *   |   |   |'h'|'e'|'l'|'l'|'o'|','|' '|'w'|'o'|'r'|'l'|'d'|   |   |   |   |   |   |
     *                                                           |-remaining-|
     * </pre>
     *
     * @param buffer the byte buffer on which bytes are put.
     * @return given {@code buffer}.
     * @throws NullPointerException    if {@code buffer} is {@code null}.
     * @throws BufferOverflowException if {@link ByteBuffer#remaining() buffer.remaining} is less than {@value
     *                                 com.github.jinahya.hello.HelloWorld#BYTES}.
     * @implSpec The implementation in this class, if specified buffer {@link ByteBuffer#hasArray() has a
     * backing-array}, invokes {@link #set(byte[], int)} with the buffer's {@link ByteBuffer#array() backing-array} and
     * ({@link ByteBuffer#arrayOffset() buffer.arrayOffset} + {@link ByteBuffer#position() buffer.position}) and then
     * manually increments the buffer's {@link ByteBuffer#position(int) position} by {@value
     * com.github.jinahya.hello.HelloWorld#BYTES}. Otherwise, this method invokes {@link #set(byte[])} method with an
     * array of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and puts the array on the buffer using {@link
     * ByteBuffer#put(byte[])} method which increments the {@code position} by itself.
     * @see ByteBuffer#hasArray()
     * @see #set(byte[], int)
     * @see #set(byte[])
     * @see ByteBuffer#put(byte[])
     */
    default <T extends ByteBuffer> T put(final T buffer) {
        if (Objects.requireNonNull(buffer, "buffer is null").remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        // TODO: Implement!
        return buffer;
    }

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @throws NullPointerException if {@code channel} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer)} method with a buffer of {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the buffer to {@code channel}.
     * @see #put(ByteBuffer)
     * @see WritableByteChannel#write(ByteBuffer)
     */
    default <T extends WritableByteChannel> T write(final T channel) throws IOException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        // TODO: Implement!
        return channel;
    }

    /**
     * Appends <a href="#hello-world-bytes">hello-world-bytes</a> to the end of specified path. The size of specified
     * path, on successful return, is increased by {@value com.github.jinahya.hello.HelloWorld#BYTES}.
     *
     * @param path the path to which bytes are appended.
     * @return given {@code path}.
     * @throws NullPointerException if {@code path} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class opens a {@link FileChannel}, from specified path, as append mode and
     * invokes {@link #write(WritableByteChannel)} method with it.
     * @see FileChannel#open(Path, OpenOption...)
     * @see #write(WritableByteChannel)
     */
    default <T extends Path> T append(final T path) throws IOException {
        if (path == null) {
            throw new NullPointerException("path is null");
        }
        // TODO: Implement!
        return path;
    }

    // ----------------------------------------------------------------------------------------- AsynchronousByteChannel

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @return A future representing the result of the operation.
     * @throws InterruptedException if interrupted while working.
     * @throws ExecutionException   if failed to operate.
     * @implSpec The default implementation in this interface invokes {@link #put(ByteBuffer) put(buffer)} method with a
     * byte buffer of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and write the buffer to specified
     * channel
     */
    default <T extends AsynchronousByteChannel> T write(final T channel)
            throws InterruptedException, ExecutionException {
        Objects.requireNonNull(channel, "channel is null");
        final ByteBuffer buffer = (ByteBuffer) put(ByteBuffer.allocate(BYTES)).flip();
        // TODO: Implement!
        return channel;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel using specified executor
     * service.
     *
     * @param channel the channel to which bytes are written.
     * @param service the executor service to which a task is submitted.
     * @return A future representing the result of the operation.
     * @implSpec The default implementation in this interface submits a task which simply invokes {@link
     * #write(AsynchronousByteChannel) write(channel)} method.
     */
    default <T extends AsynchronousByteChannel> Future<T> writeAsync(final T channel, final ExecutorService service) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(service, "service is null");
        return service.submit(() -> { // <1>
            // TODO: Implement!
            return channel;
        });
    }

    /**
     * Writes, asynchronously, the <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel using
     * specified service.
     *
     * @param channel the channel to which bytes are written.
     * @return a completable future of {@code channel}.
     */
    default <T extends AsynchronousByteChannel> CompletableFuture<T> writeCompletable(final T channel) {
        Objects.requireNonNull(channel, "channel is null");
        final CompletableFuture<T> future = new CompletableFuture<>();                 // <1>
        final ByteBuffer buffer = (ByteBuffer) put(ByteBuffer.allocate(BYTES)).flip(); // <2>
        // TODO: Implement!
        future.complete(channel);                                                      // <3> // TODO: Remove!
        return future;                                                                 // <4>
    }

    // ----------------------------------------------------------------------------------------- AsynchronousFileChannel

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel, starting at the given file
     * position.
     *
     * @param channel  the channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @return given {@code channel}.
     * @throws InterruptedException if interrupted while working.
     * @throws ExecutionException   if failed to operate.
     * @implSpec The default implementation in this interface invokes {@link #put(ByteBuffer) put(buffer)} method with a
     * byte buffer of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and write the buffer to specified
     * channel
     */
    default <T extends AsynchronousFileChannel> T write(final T channel, long position)
            throws InterruptedException, ExecutionException {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        final ByteBuffer buffer = (ByteBuffer) put(ByteBuffer.allocate(BYTES)).flip();
        while (buffer.hasRemaining()) {
            final Future<Integer> future = channel.write(buffer, position);
            final int written = future.get();
            position += written;
        }
        return channel;
    }

    /**
     * Writes, asynchronously, the <a href="#hello-world-bytes">hello-world-bytes</a> to specified asynchronous file
     * channel, starting at the given file position.
     *
     * @param channel  the channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @param service  an executor service for submitting a task.
     * @return A future representing the result of the operation.
     */
    default <T extends AsynchronousFileChannel> Future<T> writeAsync(final T channel, final long position,
                                                                     final ExecutorService service) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(service, "service is null");
        return service.submit(() -> write(channel, position));
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified asynchronous file channel, starting at
     * the given file position, using specified executor service.
     *
     * @param <T>      channel type parameter
     * @param channel  the asynchronous file channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @return A completable future representing the result of the operation.
     */
    default <T extends AsynchronousFileChannel> CompletableFuture<T> writeCompletable(final T channel,
                                                                                      final long position) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        final CompletableFuture<T> future = new CompletableFuture<>();
        final ByteBuffer buffer = (ByteBuffer) put(ByteBuffer.allocate(BYTES)).flip();
        channel.write(buffer, position, position, new CompletionHandler<Integer, Long>() {
            @Override
            public void completed(final Integer result, Long attachment) {
                if (!buffer.hasRemaining()) {
                    future.complete(channel);
                    return;
                }
                attachment += result;
                channel.write(buffer, attachment, attachment, this);
            }

            @Override
            public void failed(final Throwable exc, final Long attachment) {
                future.completeExceptionally(exc);
            }
        });
        return future;
    }
}
