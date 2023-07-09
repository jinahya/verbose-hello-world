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

import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
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
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An interface for generating <a href="#hello-world-bytes">hello-world-bytes</a> to various
 * targets.
 * <p>
 * All methods defined in this interface are thead-safe.
 *
 * <h2 id="hello-world-bytes">hello-world-bytes</h2>
 * A sequence of {@value #BYTES} bytes, representing the "{@code hello, world}" string encoded in
 * {@link java.nio.charset.StandardCharsets#US_ASCII US_ASCII} character set, which consists of
 * {@code 0x68('h')} followed by {@code 0x65('e')}, {@code 0x6C('l')}, {@code 0x6C('l')},
 * {@code 0x6F('o')}, {@code 0x2C(',')}, {@code 0x20(' ')}, {@code 0x77('w')}, {@code 0x6F('o')},
 * {@code 0x72('r')}, {@code 0x6C('l')}, and {@code 0x64('d')}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@FunctionalInterface
public interface HelloWorld {

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
     * The length of the <a href="#hello-world-bytes">hello-world-bytes</a> which is {@value}.
     */
    public static final /* redundant */
            int BYTES = 12;

    /**
     * Sets <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at
     * specified position.
     * <p>
     * The elements in the array, on successful return, will be set as follows.
     * <pre>
     *   0    &lt;= index                                  index + 12    &lt;= array.length
     *   ↓       ↓                                               ↓       ↓
     * |   |...|'h'|'e'|'l'|'l'|'o'|','|' '|'w'|'o'|'r'|'l'|'d'|...|   |
     * </pre>
     *
     * @param array the array on which bytes are set.
     * @param index the starting index of the {@code array}.
     * @return given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code index} is negative or {@code array.length} is
     *                                   less than or equal to ({@code index} + {@value #BYTES}).
     */
    public /* redundant */
    abstract /* discouraged */
    byte[] set(byte[] array, int index);

    /**
     * Sets <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at
     * {@code 0}.
     *
     * @param array the array on which bytes are set.
     * @return given {@code array}.
     * @throws NullPointerException           if {@code array} is {@code null}.
     * @throws ArrayIndexOutOfBoundsException if {@code array.length} is less than {@value #BYTES}.
     * @implSpec The default implementation invokes {@link #set(byte[], int) set(array, index)}
     * method with {@code array} and {@code 0}, and returns the {@code array}.
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
     * array of {@value #BYTES} bytes, writes the array to {@code stream} by invoking
     * {@link OutputStream#write(byte[])} method on {@code stream} with the array, and returns the
     * {@code stream}.
     * @see #set(byte[])
     * @see OutputStream#write(byte[])
     */
    default <T extends OutputStream> T write(T stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        // TODO: create an array of 12 bytes
        // TODO: invoke set(byte[]) method with the array
        // TODO: write the byte to the stream
        return null;
    }

    /**
     * Appends <a href="#hello-world-bytes">hello-world-bytes</a> to the end of specified file.
     *
     * @param <T>  file type parameter
     * @param file the file to which bytes are appended.
     * @return given {@code file}.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation creates a {@link FileOutputStream} from the {@code file}
     * as {@link FileOutputStream#FileOutputStream(File, boolean) appending mode}, invokes
     * {@link #write(OutputStream)} method with the stream, and returns the {@code file}.
     * @see java.io.FileOutputStream#FileOutputStream(File, boolean)
     * @see #write(OutputStream)
     */
    default <T extends File> T append(T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // TODO: Construct a FileOutputStream with file in appending mode
        // TODO: Invoke write(stream) method with it
        // TODO: Flush the stream
        // TODO: Close the stream
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
     * @implSpec The default implementation invokes {@link #write(OutputStream)} method with
     * {@link Socket#getOutputStream() socket.outputStream}, and returns the {@code socket}.
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
     * @param <T>  output type parameter
     * @param data the data output to which bytes are written.
     * @return given {@code data}.
     * @throws NullPointerException if {@code data} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #set(byte[])} method with an array of
     * {@value #BYTES} bytes, writes the array to specified data output by invoking
     * {@link DataOutput#write(byte[])} method on {@code data} with the {@code array}, and returns
     * the {@code data}.
     * @see #set(byte[])
     * @see DataOutput#write(byte[])
     */
    default <T extends DataOutput> T write(T data) throws IOException {
        if (data == null) {
            throw new NullPointerException("data is null");
        }
        var array = new byte[BYTES];
        set(array);
        // TODO: Implement!
        return data;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified random access file
     * starting at its current file pointer.
     *
     * @param <T>  random access file type parameter
     * @param file the random access file to which bytes are written.
     * @return given {@code file}.
     * @throws NullPointerException if {@code file} argument is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #set(byte[])} method with an array of
     * {@value #BYTES} bytes, writes the array to specified random access file by invoking
     * {@link DataOutput#write(byte[])} method on {@code file} with the array, and returns the
     * {@code file}.
     * @see #set(byte[])
     * @see RandomAccessFile#write(byte[])
     */
    default <T extends RandomAccessFile> T write(T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        var array = set(new byte[BYTES]);
        // TODO: Implement!
        return file;
    }

    /**
     * Puts <a href="#hello-world-bytes">hello-world-bytes</a> on specified byte buffer.
     * <p>
     * The buffer's position, on successful return, is incremented by {@value #BYTES}.
     * <pre>
     * Given,
     *   0    &lt;= position                                                 &lt;= limit    &lt;= capacity
     *   ↓       ↓                                                           ↓           ↓
     * |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |   |
     *           |------------------------ remaining ------------------------|
     *
     * Then, on successful return,
     *   0                                                    &lt;= position &lt;= limit    &lt;= capacity
     *   ↓                                                       ↓           ↓           ↓
     * |   |   |'h'|'e'|'l'|'l'|'o'|','|' '|'w'|'o'|'r'|'l'|'d'|   |   |   |   |   |   |
     *                                                           |-remaining-|
     * </pre>
     *
     * @param <T>    buffer type parameter
     * @param buffer the byte buffer on which bytes are put.
     * @return given {@code buffer} whose {@link ByteBuffer#position() position} increased by
     * {@value BYTES}.
     * @throws NullPointerException    if {@code buffer} is {@code null}.
     * @throws BufferOverflowException if {@link ByteBuffer#remaining() buffer.remaining} is less
     *                                 than {@value #BYTES}.
     * @implSpec The default implementation, if {@code buffer}
     * {@link ByteBuffer#hasArray() has a backing-array}, invokes
     * {@link #set(byte[], int) #set(array, index)} method with the buffer's
     * {@link ByteBuffer#array() backing-array} and ({@code buffer.arrayOffset} +
     * {@code buffer.position}), and then manually increments the buffer"s position by
     * {@value #BYTES}. Otherwise, this method invokes {@link #set(byte[]) #set(array)} method with
     * an array of {@value #BYTES} bytes, and puts the array on the buffer by invoking
     * {@link ByteBuffer#put(byte[])} method on {@code buffer} with the array.
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
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return given {@code channel}.
     * @throws NullPointerException if {@code channel} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer)} method with a buffer of
     * {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, writes the buffer to
     * {@code channel} by continuously invoking
     * {@link WritableByteChannel#write(ByteBuffer) channel.write(buffer)} while the buffer has
     * remaining, and returns the {@code channel}.
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
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to a path supplied by specified
     * supplier, passing a channel, applied to specified function with the {@code path}, to
     * {@link #write(WritableByteChannel) write(channel)} method, and eventually accepting the
     * channel to specified consumer.
     *
     * @param <T>     path type parameter
     * @param <U>     channel type parameter
     * @param locator a supplier for locating the path to write.
     * @param opener  a function for opening a channel from the {@code path} supplied from
     *                {@code locator}.
     * @param closer  a consumer for closing the channel opened by {@code opener}.
     * @return the {@code path} to which bytes have been written.
     * @throws NullPointerException if either {@code locator}, {@code opener}, or {@code closer} is
     *                              {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @see #write(WritableByteChannel)
     */
    default <T extends Path, U extends WritableByteChannel> T write(
            Supplier<? extends T> locator, Function<? super T, ? extends U> opener,
            Consumer<? super U> closer)
            throws IOException {
        Objects.requireNonNull(locator, "locator is null");
        Objects.requireNonNull(opener, "opener is null");
        Objects.requireNonNull(closer, "closer is null");
        T path = Objects.requireNonNull(locator.get(), "locator supplied null");
        U channel = Objects.requireNonNull(opener.apply(path), "opener applied null");
        try {
            write(channel);
        } finally {
            closer.accept(channel);
        }
        return path;
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the end of specified path
     * to a file. The length of the file, on successful return, is increased by {@value #BYTES}.
     *
     * @param <T>  path type parameter
     * @param path the path a file to which bytes are appended.
     * @return given {@code path}.
     * @throws NullPointerException if {@code path} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation opens a {@link FileChannel} from {@code path} in
     * {@link StandardOpenOption#APPEND appending mode}, invokes
     * {@link #write(WritableByteChannel) write(channel)} method with it, and returns the
     * {@code path}.
     * @see FileChannel#open(Path, OpenOption...)
     * @see #write(WritableByteChannel)
     */
    default <T extends Path> T append(T path) throws IOException {
        Objects.requireNonNull(path, "path is null");
        // TODO: Implement!
        return path;
    }

    /**
     * Writes the <a href="hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return given {@code channel}.
     * @throws InterruptedException if interrupted while executing.
     * @throws ExecutionException   if failed to execute.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer) put(buffer)} method with
     * a byte buffer of {@value #BYTES} bytes, flips it, and writes the buffer to the
     * {@code channel} by, continuously while the {@code buffer}
     * {@link ByteBuffer#hasRemaining() has remaining}, invoking
     * {@link AsynchronousByteChannel#write(ByteBuffer)} method with the {@code buffer}.
     * @see #put(ByteBuffer)
     * @deprecated Use {@link #writeAsync(AsynchronousByteChannel, CompletionHandler, Object)}
     */
    @Deprecated
    default <T extends AsynchronousByteChannel> T write(T channel)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        while (buffer.hasRemaining()) {
            var future = channel.write(buffer);
            var written = future.get();
        }
        return channel;
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
     * @deprecated Use {@link #writeAsync(AsynchronousByteChannel, CompletionHandler, Object)}
     */
    @Deprecated
    default <T extends AsynchronousByteChannel> Future<T> writeAsync(T channel, Executor executor) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(executor, "executor is null");
        Callable<T> callable = () -> {
            return write(channel);
        };
        FutureTask<T> command = new FutureTask<>(callable);
        executor.execute(command); // Runnable
        return command;            // Future<T>
    }

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to specified channel, and handles completion on specified handler with specified attachment.
     *
     * @param <C>        channel type parameter
     * @param channel    the channel to which bytes are written.
     * @param handler    the completion handler.
     * @param attachment the attachment.
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     */
    default <C extends AsynchronousByteChannel, A> void writeAsync(
            C channel, CompletionHandler<? super C, ? super A> handler, A attachment) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        channel.write(
                buffer,                     // src
                attachment,                 // attachment
                new CompletionHandler<>() { // handler
                    @Override
                    public void completed(Integer result, A attachment) {
                        if (!buffer.hasRemaining()) {
                            handler.completed(channel, attachment);
                            return;
                        }
                        channel.write(
                                buffer,     // src
                                attachment, // attachment
                                this        // handler
                        );
                    }

                    @Override
                    public void failed(Throwable exe, A attachment) {
                        handler.failed(exe, attachment);
                    }
                }
        );
    }

    /**
     * Returns a completable future of specified channel which writes the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return a completable future.
     */
    default <T extends AsynchronousByteChannel> CompletableFuture<T> writeCompletable(T channel) {
        Objects.requireNonNull(channel, "channel is null");
        var future = new CompletableFuture<T>();
        writeAsync(
                channel,                    // channel
                new CompletionHandler<>() { // handler
                    @Override
                    public void completed(T result, Object attachment) {
                        future.complete(result);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        future.completeExceptionally(exc);
                    }
                },
                null                        // attachment
        );
        return future;
    }

    /**
     * Writes the <a href="hello-world-bytes">hello-world-bytes</a> to specified file channel,
     * starting at given file position.
     *
     * @param <T>      channel type parameter
     * @param channel  the file channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @return given {@code channel}.
     * @throws InterruptedException if interrupted while executing.
     * @throws ExecutionException   if failed to execute.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer) put(buffer)} with a byte
     * buffer of {@value #BYTES} bytes, flips it, and writes the {@code buffer} to {@code channel},
     * while the {@code buffer} {@link ByteBuffer#hasRemaining() has remaining}, by continuously
     * invoking
     * {@link AsynchronousFileChannel#write(ByteBuffer, long) channel.write(buffer, position)}
     * method with the {@code buffer} and {@code position} adjusted with the result of previous
     * result.
     * @see #put(ByteBuffer)
     * @see AsynchronousFileChannel#write(ByteBuffer, long)
     */
    default <T extends AsynchronousFileChannel> T write(T channel, long position)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") < 0L");
        }
        var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        while (buffer.hasRemaining()) {
            var future = channel.write(buffer, position);
            position += future.get();
        }
        return channel;
    }

    /**
     * Writes, asynchronously, the <a href="hello-world-bytes">hello-world-bytes</a> to specified
     * file channel, starting at given file position.
     *
     * @param <T>      channel type parameter
     * @param channel  the file channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @param executor an executor.
     * @return a future of {@code channel}.
     * @see #write(AsynchronousFileChannel, long)
     */
    default <T extends AsynchronousFileChannel> Future<T> writeAsync(T channel, long position,
                                                                     Executor executor) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") < 0L");
        }
        Objects.requireNonNull(executor, "executor is null");
        FutureTask<T> command = new FutureTask<>(() -> write(channel, position));
        executor.execute(command); // Runnable  <- RunnableFuture<V> <- FutureTask<V>
        return command;            // Future<V> <- RunnableFuture<V> <- FutureTask<V>
    }

    @SuppressWarnings({
            "java:S117" // attachment_
    })
    private <T extends AsynchronousFileChannel, A> void writeAsync1(
            T channel, long position, CompletionHandler<? super T, ? super A> handler,
            A attachment) {
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        final var finalPosition = position + BYTES;
        channel.write(
                buffer,                     // <src>
                position,                   // <position>
                position,                   // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(Integer result, Long attachment_) {
                        if ((attachment_ = attachment_ + result) == finalPosition) {
                            handler.completed(channel, attachment);
                            return;
                        }
                        channel.write(
                                buffer,      // <src>
                                attachment_, // <position>
                                attachment_, // <attachment>
                                this         // <handler>
                        );
                    }

                    @Override
                    public void failed(Throwable exc, Long attachment_) {
                        handler.failed(exc, attachment);
                    }
                }
        );
    }

    @SuppressWarnings({
            "java:S117" // attachment_
    })
    private <T extends AsynchronousFileChannel, A> void writeAsync2(
            T channel, long position, CompletionHandler<? super T, ? super A> handler,
            A attachment) {
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        channel.write(
                buffer,                     // <src>
                position,                   // <position>
                buffer,                     // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(Integer result, ByteBuffer attachment_) {
                        if (!attachment_.hasRemaining()) {
                            handler.completed(channel, attachment);
                            return;
                        }
                        channel.write(
                                buffer,                            // <src>
                                position + attachment_.position(), // <position>
                                attachment_,                       // <attachment>
                                this                               // <handler>
                        );
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment_) {
                        handler.failed(exc, attachment);
                    }
                }
        );
    }

    /**
     * Writes, asynchronously, the <a href="hello-world-bytes">hello-world-bytes</a> to specified
     * channel, starting at specified position, and handles a completion on specified handler.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the file channel to which bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param handler    the handler.
     * @param attachment an attachment.
     * @throws NullPointerException  either {@code channel} or {@code handler} is {@code null}.
     * @throws IllegalStateException if {@code position} is negative.
     * @see AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     */
    @SuppressWarnings({
            "java:S117" // attachment_
    })
    default <T extends AsynchronousFileChannel, A> void writeAsync(
            T channel, long position, CompletionHandler<? super T, ? super A> handler,
            A attachment) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") < 0L");
        }
        Objects.requireNonNull(handler, "handler is null");
        writeAsync1(channel, position, handler, attachment);
    }

    @SuppressWarnings({
            "java:S4274" // assert
    })
    private <T extends AsynchronousFileChannel> CompletableFuture<T> writeCompletable1(
            T channel, long position) {
        var future = new CompletableFuture<T>();
        writeAsync(
                channel,                    // <channel>
                position,                   // <position>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(T result, Object attachment) {
                        future.complete(result);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        future.completeExceptionally(exc);
                    }
                },
                null                        // <attachment>
        );
        return future;
    }

    @SuppressWarnings({
            "java:S4274" // assert
    })
    private <T extends AsynchronousFileChannel> CompletableFuture<T> writeCompletable2(
            T channel, long position) {
        var future = new CompletableFuture<T>();
        writeAsync(
                channel,                    // <channel>
                position,                   // <position>
                new CompletionHandler<>() { // <handler>
                    @Override
                    public void completed(T result, T attachment) {
                        assert result == channel;
                        assert attachment == channel;
                        future.complete(attachment);
                    }

                    @Override
                    public void failed(Throwable exc, T attachment) {
                        assert exc != null;
                        assert attachment == channel;
                        future.completeExceptionally(exc);
                    }
                },
                channel                     // <attachment>
        );
        return future;
    }

    /**
     * Returns a completable future of specified channel which writes the <a
     * href="#hello-world-bytes">hello-world-bytes</a> to the channel starting at specified
     * position.
     *
     * @param channel  the channel to which bytes are written.
     * @param position the starting position to which the bytes are transferred; must not be
     *                 negative.
     * @param <T>      channel type parameter
     * @return a completable future of {@code channel}.
     * @throws NullPointerException     when {@code channel} is {@code null}.
     * @throws IllegalArgumentException when {@code position} is negative.
     */
    default <T extends AsynchronousFileChannel> CompletableFuture<T> writeCompletable(
            T channel, long position) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") < 0L");
        }
        return writeCompletable1(channel, position);
    }

    /**
     * Returns a completable future of specified path which writes the <a
     * href="#hello-world-bytes">hello-world-bytes</a> to the end of the path.
     *
     * @param path the path to the file to which bytes are appended.
     * @param <T>  path type parameter
     * @return a completable future of {@code path}.
     * @throws NullPointerException when {@code channel} is {@code null}.
     */
    @SuppressWarnings({
            "java:S2095" // no try-with-resources
    })
    default <T extends Path> CompletableFuture<T> appendCompletable(T path) throws IOException {
        Objects.requireNonNull(path, "path is null");
        var channel = AsynchronousFileChannel.open(
                path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        return writeCompletable(channel, channel.size())
                .thenApply(c -> {
                    try {
                        channel.force(false);
                        channel.close();
                    } catch (IOException ioe) {
                        throw new UncheckedIOException("unable to force " + channel, ioe);
                    }
                    return path;
                });
    }
}
