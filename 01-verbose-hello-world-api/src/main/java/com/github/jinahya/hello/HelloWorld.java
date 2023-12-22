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

import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.io.Writer;
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
import java.util.concurrent.atomic.LongAccumulator;

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
        return HelloWorldLoggers.log(getClass());
    }

    /**
     * Returns a logger for this interface.
     *
     * @return a logger for this interface.
     */
    private System.Logger logger() {
        return HelloWorldLoggers.logger(getClass());
    }

    /**
     * The length of the <a href="#hello-world-bytes">hello-world-bytes</a> which is {@value}.
     */
    public static final /* redundant */
            int BYTES = 12;

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at
     * specified position.
     * <p>
     * The elements in the array, on successful return, will be set as follows.
     * <pre>
     *  0   &lt;= index            index+12     &lt;= array.length
     *  ↓      ↓                       ↓        ↓
     * | |....|h|e|l|l|o|,| |w|o|r|l|d| |....| |
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
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at
     * {@code 0}.
     * <p>
     * The elements in the array, on successful return, will be set as follows.
     * <pre>
     *  0                      12     &lt;= array.length
     *  ↓                       ↓        ↓
     * |h|e|l|l|o|,| |w|o|r|l|d| |....| |
     * </pre>
     *
     * @param array the array on which bytes are set.
     * @return given {@code array}.
     * @throws NullPointerException           if {@code array} is {@code null}.
     * @throws ArrayIndexOutOfBoundsException if {@code array.length} is less than {@link #BYTES}.
     * @implSpec The default implementation invokes {@link #set(byte[], int) set(array, index)}
     * method with {@code array} and {@code 0}, and returns the result.
     * @see #set(byte[], int)
     */
    default byte[] set(byte[] array) {
        // TODO: set 'hello, world' bytes on array.
        return null;
    }

    /**
     * Prints the <a href="#hello-world-bytes">hello-world-bytes</a> on specified chars starting at
     * specified position.
     * <p>
     * The elements in the chars, on successful return, will be set as follows.
     * <pre>
     *   0     &lt;= offset                                  offset+12 &lt;=         chars.length
     *   ↓        ↓                                               ↓            ↓
     * |   |....|'h'|'e'|'l'|'l'|'o'|','|' '|'w'|'o'|'r'|'l'|'d'|   |....|   |
     * </pre>
     *
     * @param chars  the chars on which bytes are set.
     * @param offset the starting offset of the {@code chars}.
     * @return given {@code chars}.
     * @throws NullPointerException           if {@code chars} is {@code null}.
     * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or {@code chars.length}
     *                                        is less than or equal to ({@code offset} +
     *                                        {@value #BYTES}).
     * @implSpec The default implementation invokes {@link #set(byte[]) set(array)} method with an
     * array of {@value #BYTES} bytes, copies each element in returned array into {@code chars}
     * starting at {@code offset}, and returns the {@code chars}.
     * @see #set(byte[])
     */
    default char[] print(char[] chars, int offset) {
        if (chars == null) {
            throw new NullPointerException("chars is null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset(" + offset + ") is negative");
        }
        if (offset + BYTES > chars.length) {
            throw new ArrayIndexOutOfBoundsException(
                    "offset(" + offset + ") + " + BYTES + " > chars.length(" + chars.length + ")"
            );
        }
        final var array = new byte[BYTES];
        set(array);
        for (final var b : array) {
            chars[offset++] = (char) b;
        }
        return chars;
    }

    /**
     * Prints the <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at
     * {@code 0}.
     * <p>
     * The elements in the chars, on successful return, will be set as follows.
     * <pre>
     *   0                                              12 &lt;=         chars.length
     *   ↓                                               ↓            ↓
     * |'h'|'e'|'l'|'l'|'o'|','|' '|'w'|'o'|'r'|'l'|'d'|   |....|   |
     * </pre>
     *
     * @param chars the chars on which characters are set.
     * @return given {@code chars}.
     * @throws NullPointerException           if {@code chars} is {@code null}.
     * @throws ArrayIndexOutOfBoundsException if {@code chars.length} is less than {@link #BYTES}.
     * @implSpec The default implementation invokes {@link #print(char[], int) set(chars, offset)}
     * method with {@code chars} and {@code 0}, and returns the result.
     * @see #print(char[], int)
     */
    default char[] print(char[] chars) {
        if (chars == null) {
            throw new NullPointerException("chars is null");
        }
        if (chars.length < BYTES) {
            throw new ArrayIndexOutOfBoundsException(
                    "chars.length(" + chars.length + ") < " + BYTES
            );
        }
        print(chars, 0);
        return chars;
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to specified appendable.
     *
     * @param appendable the appendable to which chars are appended.
     * @return given {@code appendable}.
     * @throws NullPointerException if {@code appendable} is {@code null}.
     * @implSpec The default implementation invokes {@link #print(char[]) print(chars)} method with
     * an array of {@value #BYTES} characters, appends each character in the array to
     * {@code appendable} using {@link Appendable#append(char)} method, and returns the
     * {@code appendable}.
     * @see #print(char[])
     * @see Appendable#append(char)
     */
    default <T extends Appendable> T append(T appendable)
            throws IOException {
        if (appendable == null) {
            throw new NullPointerException("appendable is null");
        }
        final var chars = new char[BYTES];
        print(chars);
        for (var c : chars) {
            appendable.append(c);
        }
        return appendable;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified output stream.
     *
     * @param <T>    stream type parameter
     * @param stream the output stream to which bytes are written.
     * @return given {@code stream}.
     * @throws NullPointerException if {@code stream} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @apiNote This method does not {@link OutputStream#flush() flush} the {@code stream}.
     * @implSpec The default implementation invokes {@link #set(byte[]) set(array)} method with an
     * array of {@value #BYTES} bytes, writes the array to {@code stream} by invoking
     * {@link OutputStream#write(byte[])} method on {@code stream} with the array, and returns the
     * {@code stream}.
     * @see #set(byte[])
     * @see OutputStream#write(byte[])
     */
    default <T extends OutputStream> T write(T stream)
            throws IOException {
        if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        // TODO: Create an array of 12 bytes
        // TODO: Invoke set(byte[]) method with the array
        // TODO: Write the array to the stream
        return null;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified writer.
     *
     * @param <T>    writer type parameter
     * @param writer the writer to which characters are written.
     * @return given {@code writer}.
     * @throws NullPointerException if {@code writer} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @apiNote This method does not {@link Writer#flush() flush} the {@code writer}.
     * @implSpec The default implementation invokes {@link #append(Appendable) append(appendable)}
     * with {@code writer}, and returns the {@code writer}.
     * @see #append(Appendable)
     * @see #print(char[])
     * @see Writer#write(char[])
     */
    default <T extends Writer> T write(T writer)
            throws IOException {
        if (writer == null) {
            throw new NullPointerException("writer is null");
        }
        append(writer);
        return writer;
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the end of specified file.
     *
     * @param <T>  file type parameter
     * @param file the file to which bytes are appended.
     * @return given {@code file}.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation creates a new {@link FileOutputStream} with
     * {@code file}, as an {@link FileOutputStream#FileOutputStream(File, boolean) appending mode},
     * invokes the {@link #write(OutputStream) write(stream)} method with it,
     * {@link OutputStream#flush() flushes} and {@link OutputStream#close() closes} the stream, and
     * returns {@code file}.
     * @see java.io.FileOutputStream#FileOutputStream(File, boolean)
     * @see #write(OutputStream)
     */
    default <T extends File> T append(T file)
            throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // TODO: Create a new FileOutputStream with file, in appending mode.
        // TODO: Invoke write(stream) method with it.
        // TODO: Flush the stream.
        // TODO: Close the stream.
        return file;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified data output.
     *
     * @param <T>  data output type parameter
     * @param data the data output to which bytes are written.
     * @return given {@code data}.
     * @throws NullPointerException if {@code data} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #set(byte[])} method with an array of
     * {@value #BYTES} bytes, writes the array to {@code data} by invoking
     * {@link DataOutput#write(byte[])} method on {@code data} with the array, and returns
     * {@code data}.
     * @see #set(byte[])
     * @see DataOutput#write(byte[])
     */
    default <T extends DataOutput> T write(T data)
            throws IOException {
        if (data == null) {
            throw new NullPointerException("data is null");
        }
        var array = new byte[BYTES];
        set(array);
        // TODO: Write array to data!
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
    default <T extends RandomAccessFile> T write(T file)
            throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        final var array = new byte[BYTES];
        set(array);
        // TODO: Write array to file!
        return file;
    }

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> through specified socket.
     *
     * @param <T>    socket type parameter
     * @param socket the socket through which bytes are sent.
     * @return given {@code socket}.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #write(OutputStream)} method with
     * {@link Socket#getOutputStream() socket.outputStream}, and returns {@code socket}.
     * @see Socket#getOutputStream()
     * @see #write(OutputStream)
     */
    default <T extends Socket> T send(T socket)
            throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        // TODO: Invoke write(stream) with socket.outputStream.
        return socket;
    }

    /**
     * Puts the <a href="#hello-world-bytes">hello-world-bytes</a> on specified byte buffer.
     * <p>
     * The buffer's position, on successful return, is incremented by {@value #BYTES}.
     * <pre>
     * Given,
     *
     *          4                                        25          31
     *  0    &lt;= position                           &lt;= limit &lt;= capacity
     *  ↓       ↓                                         ↓           ↓
     * | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | |
     *         |--------------- remaining ---------------|
     *                                 21
     *
     * Then, on successful return,
     *
     *                                 16                25          31
     *  0                     &lt;= position          &lt;= limit &lt;= capacity
     *  ↓                               ↓                 ↓           ↓
     * | | | | |h|e|l|l|o|,| |w|o|r|l|d| | | | | | | | | | | | | | | |
     *                                 |--- remaining ---|
     *                                              9
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
        Objects.requireNonNull(buffer, "buffer is null");
        if (buffer.remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        if (buffer.hasArray()) {
            // TODO: Invoke set(buffer.array(), (buffer.arrayOffset() + buffer.position())
            // TODO: Increment buffer.position by 12
        } else {
            // TODO: Invoke set(array[12])
            // TODO: Put the array to the buffer
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
    default <T extends WritableByteChannel> T write(T channel)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        // TODO: Invoke channel.write(buffer), continuously, while buffer.hasRemaining()
        return channel;
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
     * @implSpec The default implementation opens a {@link FileChannel} from {@code path} with
     * {@link StandardOpenOption#CREATE}, {@link StandardOpenOption#WRITE}, and
     * {@link StandardOpenOption#APPEND}, and invokes
     * {@link #write(WritableByteChannel) write(channel)} method with it,
     * {@link FileChannel#force(boolean) forces}/{@link WritableByteChannel#close() closes} the
     * channel, and returns the {@code path}.
     * @see FileChannel#open(Path, OpenOption...)
     * @see StandardOpenOption#CREATE
     * @see StandardOpenOption#WRITE
     * @see StandardOpenOption#APPEND
     * @see #write(WritableByteChannel)
     * @see FileChannel#force(boolean)
     */
    default <T extends Path> T append(T path)
            throws IOException {
        Objects.requireNonNull(path, "path is null");
        // TODO: Open a file channel from the path as an appending mode.
        // TODO: Invoke write(channel) method with it.
        // TODO: Force the channel with true.
        // TODO: Close the channel.
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
     * {@code channel} by, while the {@code buffer} {@link ByteBuffer#hasRemaining() has remaining},
     * continuously invoking {@link AsynchronousByteChannel#write(ByteBuffer)} method with the
     * {@code buffer}.
     * @see #put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer)
     */
    default <T extends AsynchronousByteChannel> T write(T channel)
            throws InterruptedException, ExecutionException {
        Objects.requireNonNull(channel, "channel is null");
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
            // TODO: Invoke channel.write(buffer) which returns a Future<Integer>
            // TODO: Get the result of the future
            break; // TODO: remove!
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
     * @see Executor#execute(Runnable)
     * @see #put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer)
     */
    default <T extends AsynchronousByteChannel> Future<T> writeAsync(
            final T channel, final Executor executor) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(executor, "executor is null");
        final Callable<T> callable = () -> write(channel);
        final var command = new FutureTask<>(callable);
        executor.execute(command); // as Runnable
        return command;            // as Future<T>
    }

    default <C extends AsynchronousByteChannel> void writeAsync(
            final C channel, final CompletionHandler<? super Integer, ? super Void> handler) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        channel.<Void>write(
                buffer,                     // <src>
                null,                       // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override // @formatter:off
                    public void completed(final Integer result, final Void attachment) {
                        if (!buffer.hasRemaining()) {
                            handler.completed(
                                    buffer.position(), // <result>
                                    null               // <attachment>
                            );
                            return;
                        }
                        handler.completed(result, null);
                        channel.write(
                                buffer, // <src>
                                null,   // <attachment>
                                this    // <handler>
                        );
                    }
                    @Override
                    public void failed(final Throwable exc, final Void attachment) {
                        handler.failed(exc, null);
                    } // @formatter:on
                }
        );
    }

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to specified channel, and handles completion on specified handler with specified attachment.
     *
     * @param <C>        channel type parameter
     * @param channel    the channel to which bytes are written.
     * @param handler    the completion handler.
     * @param attachment the attachment; may be {@code null}.
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     */
    default <C extends AsynchronousByteChannel, A> void writeAsync(
            final C channel, final CompletionHandler<? super C, ? super A> handler,
            final A attachment) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        // TODO: Invoke channel.write(buffer, attachment, a-handler)
    }

    /**
     * Returns a completable future of specified channel which writes the <a
     * href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> to the channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return a completable future.
     */
    default <T extends AsynchronousByteChannel> CompletableFuture<T> writeCompletable(
            T channel) {
        Objects.requireNonNull(channel, "channel is null");
        var future = new CompletableFuture<T>();
        // TODO: Invoke, writeAsync(channel, a-handler, null)
        return future;
    }

    /**
     * Writes the <a href="hello-world-bytes">hello-world-bytes</a> to specified file channel,
     * starting at given file position.
     * <pre>
     * Given,
     *
     *                  p(0)
     *                  ↓
     * &lt;buffer&gt;:       |h|e|l|l|o|,| |w|o|r|l|d|
     *
     *                 &lt;position&gt; + 0
     *                  ↓
     * &lt;channel&gt;: ...| | | | | | | | | | | | | | |...
     *
     * Then, in an intermediate state, possibly,
     *
     *                        p(n)
     *                        ↓
     * &lt;buffer&gt;:       |h|e|l|l|o|,| |w|o|r|l|d|
     *
     *                       &lt;position&gt; + n
     *                        ↓
     * &lt;channel&gt;: ...| |h|e|l| | | | | | | | | | |...
     *
     * And, on successful return,
     *
     *                                          p(12)
     *                                          ↓
     * &lt;buffer&gt;:       |h|e|l|l|o|,| |w|o|r|l|d|
     *
     *                                         &lt;position&gt; + 12
     *                                          ↓
     * &lt;channel&gt;: ...| |h|e|l|l|o|,| |w|o|r|l|d| |...
     * </pre>
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
        Objects.requireNonNull(channel, "channel is null");
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
     * @implSpec The default implementation returns a future of {@code channel} which invokes
     * {@link #write(AsynchronousFileChannel, long) write(channel, position)} method with
     * {@code channel}, and {@code position}.
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

    /**
     * Writes, asynchronously, the <a href="hello-world-bytes">hello-world-bytes</a> to specified
     * channel, starting at specified position, and handles a completion on specified handler.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the file channel to which bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param handler    the handler.
     * @param attachment an attachment; may be {@code null}.
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
        var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        var accumulator = new LongAccumulator(Long::sum, position);
        channel.write(
                buffer,                     // <src>
                accumulator.get(),          // <position>
                accumulator,                // <attachment> <1>
                new CompletionHandler<>() { // <handler>
                    @Override // @formatter:off
                    public void completed(Integer result, LongAccumulator attachment_) {
                        if (!buffer.hasRemaining()) {
                            handler.completed(channel, attachment);
                            return;
                        }
                        attachment_.accumulate(result);
                        channel.write(
                                buffer,            // <src>
                                attachment_.get(), // <position>          <3>
                                attachment_,       // <attachment>        <4>
                                this               // <handler>
                        );
                    }
                    @Override
                    public void failed(Throwable exc, LongAccumulator attachment_) {
                        handler.failed(exc, attachment);
                    } // @formatter:on
                }
        );
    }

    /**
     * Returns a completable future of specified channel which writes the <a
     * href="#hello-world-bytes">hello-world-bytes</a> to the channel starting at specified
     * position.
     *
     * @param channel  the channel to which bytes are written.
     * @param position the starting position to which the bytes are transferred; must be
     *                 non-negative.
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
        var future = new CompletableFuture<T>();
        writeAsync(
                channel,                    // <channel>
                position,                   // <position>
                new CompletionHandler<>() { // <handler>
                    @Override // @formatter:off
                    public void completed(T result, Object attachment) {
                        future.complete(result);
                    }
                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        future.completeExceptionally(exc);
                    } // @formatter:on
                },
                null                        // <attachment>
        );
        return future;
    }

    /**
     * Returns a completable future of specified path which writes the <a
     * href="#hello-world-bytes">hello-world-bytes</a> to the end of specified path.
     *
     * @param path the path to the file to which bytes are appended.
     * @param <T>  path type parameter
     * @return a completable future of {@code path}.
     * @throws NullPointerException when {@code channel} is {@code null}.
     */
    @SuppressWarnings({
            "java:S2095" // no try-with-resources
    })
    default <T extends Path> CompletableFuture<T> appendCompletable(T path)
            throws IOException {
        Objects.requireNonNull(path, "path is null");
        var channel = AsynchronousFileChannel.open(
                path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        return writeCompletable(channel, channel.size())
                .thenApply(c -> {
                    try {
                        c.force(true);
                        c.close();
                    } catch (IOException ioe) {
                        throw new UncheckedIOException(
                                "unable to force/close " + c, ioe);
                    }
                    return path;
                });
    }
}
