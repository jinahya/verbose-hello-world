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
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.nio.ByteBuffer.allocate;

/**
 * An interface for generating <a href="#hello-world-bytes">hello-world-bytes</a> to various targets.
 *
 * <h2 id="hello-world-bytes">hello-world-bytes</h2>
 * A sequence of {@value #BYTES} bytes, representing the "{@code hello, world}" string encoded in {@link
 * java.nio.charset.StandardCharsets#US_ASCII US-ASCII} character set, which consists of {@code 0x68('h')} followed by
 * {@code 0x65('e')}, {@code 0x6C('l')}, {@code 0x6C('l')}, {@code 0x6F('o')}, {@code 0x2C(',')}, {@code 0x20(' ')},
 * {@code 0x77('w')}, {@code 0x6F('o')}, {@code 0x72('r')}, {@code 0x6C('l')}, and {@code 0x64('d')}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@FunctionalInterface
public interface HelloWorld {

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * The length of the <a href="#hello-world-bytes">hello-world-bytes</a>. The value is {@value}.
     *
     * @see <a href="#hello-world-bytes">hello-world-bytes</a>
     */
    int BYTES = 12;

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Sets <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at specified position and
     * returns the array.
     * <p>
     * The elements in the array, on successful return, will be set as follows.
     * <blockquote><pre>{@code
     *   0                                                               array.length
     *   |                                                               |
     * |   |...|'h'|'e'|'l'|'l'|'o'|','|' '|'w'|'o'|'r'|'l'|'d'|   |...|
     *           |                                               |
     *      0 <= index                                           (index + BYTES) <= array.length
     * }</pre></blockquote>
     *
     * @param array the array on which bytes are set.
     * @param index the starting index of the {@code array}.
     * @return given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code index} is negative or ({@code index} + {@value
     *                                   com.github.jinahya.hello.HelloWorld#BYTES}) is greater than {@code
     *                                   array.length}.
     */
    byte[] set(byte[] array, int index);

    /**
     * Sets <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at {@code 0} and returns the
     * array.
     *
     * @param array the array on which bytes are set.
     * @return given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code array.length} is less than {@link #BYTES}.
     * @implSpec The implementation in this class invokes {@link #set(byte[], int)} method with specified {@code array}
     * and {@code 0} and returns the result.
     * @see #set(byte[], int)
     */
    default byte[] set(final byte[] array) {
        return null;
    }

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> to specified output stream and returns the output
     * stream.
     *
     * @param stream the output stream to which bytes are written.
     * @param <T>    output stream type parameter
     * @return given {@code stream}.
     * @throws NullPointerException if {@code stream} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class invokes {@link #set(byte[])} method with an array of {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the array to specified {@code stream} using {@link
     * OutputStream#write(byte[])} method.
     * @see #set(byte[])
     * @see OutputStream#write(byte[])
     */
    default <T extends OutputStream> T write(final T stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        return null;
    }

    /**
     * Appends <a href="#hello-world-bytes">hello-world-bytes</a> to specified file and returns the file.
     * <p>
     * This method creates an instance of {@link FileOutputStream}, in {@link FileOutputStream#FileOutputStream(File,
     * boolean) append mode}, from specified file, invokes {@link #write(OutputStream)} method with it, and returns the
     * file.
     * <blockquote><pre>{@code
     * OutputStream stream = new FileOutputStream(file, true); // in append mode
     * try {
     *     write(stream);
     *     stream.flush();
     * } finally {
     *     stream.close();
     * }
     * }</pre></blockquote>
     *
     * @param file the file to which bytes are appended.
     * @param <T>  file type parameter
     * @return given {@code file}.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class creates a {@link FileOutputStream} from {@code file} as append mode,
     * invokes {@link #write(OutputStream)} method with the stream and returns the {@code file}.
     * @see java.io.FileOutputStream#FileOutputStream(File, boolean)
     * @see #write(OutputStream)
     */
    default <T extends File> T append(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        return null;
    }

    /**
     * Sends <a href="#hello-world-bytes">hello-world-bytes</a> through specified socket.
     *
     * @param socket the socket to which bytes are sent.
     * @param <T>    socket type parameter
     * @return given {@code socket}.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class invokes {@link #write(OutputStream)} method with {@link
     * Socket#getOutputStream() socket.outputStream} and returns {@code socket}.
     * @see Socket#getOutputStream()
     * @see #write(OutputStream)
     */
    default <T extends Socket> T send(final T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> to specified data output and returns the data output.
     *
     * @param data the data output to which bytes are written.
     * @param <T>  data output type parameter
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
        if (data == null) {
            throw new NullPointerException("data is null");
        }
        return null;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> starting at the current file pointer of specified
     * random access file and returns the random access file.
     *
     * @param file the random access file to which bytes are written.
     * @param <T>  random access file type parameter
     * @return given {@code file}.
     * @throws NullPointerException if {@code randomAccessFile} argument is {@code null}.
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
        // TODO: implement!
        return file;
    }

    /**
     * Appends <a href="#hello-world-bytes">hello-world-bytes</a> at the end of specified random access file and returns
     * the random access file.
     *
     * @param file the random access file to which bytes are written.
     * @param <T>  random access file type parameter
     * @return given {@code file}.
     * @throws NullPointerException if {@code file} argument is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class {@link RandomAccessFile#seek(long) moves the file-pointer} to {@link
     * RandomAccessFile#length() the end} of specified random access file and invokes {@link #write(RandomAccessFile)}
     * method with the file.
     * @see RandomAccessFile#length()
     * @see RandomAccessFile#seek(long)
     * @see #write(RandomAccessFile)
     */
    default <T extends RandomAccessFile> T append(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // TODO: implement!
        return file;
    }

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Puts <a href="#hello-world-bytes">hello-world-bytes</a> on specified byte buffer. The buffer's position, on
     * successful return, is incremented by {@value com.github.jinahya.hello.HelloWorld#BYTES}.
     *
     * @param buffer the byte buffer on which bytes are put.
     * @param <T>    byte buffer type parameter
     * @return given {@code buffer}.
     * @throws NullPointerException    if {@code buffer} is {@code null}.
     * @throws BufferOverflowException if {@link ByteBuffer#remaining() buffer.remaining} is less than {@value
     *                                 com.github.jinahya.hello.HelloWorld#BYTES}.
     * @implSpec The implementation in this class, if specified buffer {@link ByteBuffer#hasArray() has a
     * backing-array}, invokes {@link #set(byte[], int)} with the buffer's {@link ByteBuffer#array() backing array} and
     * ({@link ByteBuffer#arrayOffset() buffer.arrayOffset} + {@link ByteBuffer#position() buffer.position}) and then
     * manually increments the buffer's {@link ByteBuffer#position(int) position} by {@value
     * com.github.jinahya.hello.HelloWorld#BYTES}. Otherwise, this method invokes {@link #set(byte[])} method with an
     * array of {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes and puts the array on the buffer using {@link
     * ByteBuffer#put(byte[])} method which increments the {@code position} by itself.
     * @see #set(byte[], int)
     * @see #set(byte[])
     * @see ByteBuffer#put(byte[])
     */
    default <T extends ByteBuffer> T put(final T buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer is null");
        }
        return buffer;
    }

    /**
     * Writes <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     * <p>
     * This method invokes {@link #put(ByteBuffer)} method with a newly allocated byte buffer of {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and, {@link ByteBuffer#flip() flips} it, writes all remaining
     * bytes in the buffer to specified channel using {@link WritableByteChannel#write(ByteBuffer)} method.
     * <blockquote><pre>{@code
     * ByteBuffer buffer = ByteBuffer.allocate(BYTES); // position = 0, limit,capacity = 12
     * put(buffer); // position -> 12
     * buffer.flip(); // limit -> 12(position), position -> 0
     * while (buffer.hasRemaining()) { // not all remaining bytes may be written at once
     *     channel.write(buffer);
     * }
     * }</pre></blockquote>
     *
     * @param channel the channel to which bytes are written.
     * @param <T>     channel type parameter
     * @return given {@code channel}.
     * @throws NullPointerException if {@code channel} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The implementation in this class invokes {@link #put(ByteBuffer)} with a buffer of {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the buffer to {@code channel}.
     * @see #put(ByteBuffer)
     * @see WritableByteChannel#write(ByteBuffer)
     */
    default <T extends WritableByteChannel> T write(final T channel) throws IOException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        return channel;
    }

    /**
     * Appends <a href="#hello-world-bytes">hello-world-bytes</a> to the end of specified path and returns the path. The
     * size of specified path, on successful return, increases by {@value com.github.jinahya.hello.HelloWorld#BYTES}.
     *
     * @param path the path to which bytes are appended.
     * @param <T>  path type parameter
     * @return given {path}.
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
        return path;
    }

    /**
     * Writes, synchronously, the <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @param <T>     channel type parameter
     * @return A future representing the result of the operation.
     * @throws InterruptedException if interrupted while working.
     * @throws ExecutionException   if failed to execute.
     * @implSpec The implementation in this class invokes {@link #put(ByteBuffer)} method with a byte buffer of {@value
     * com.github.jinahya.hello.HelloWorld#BYTES} bytes and writes the buffer to specified channel using {@link
     * AsynchronousByteChannel#write(ByteBuffer)} method.
     */
    default <T extends AsynchronousByteChannel> T writeSync(final T channel)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        final ByteBuffer buffer = allocate(BYTES);
        put(buffer);
        buffer.flip();
        // TODO: implement!
        return channel;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @param <T>     channel type parameter
     * @return A future representing the result of the operation.
     * @throws InterruptedException if interrupted while working.
     * @throws ExecutionException   if failed to execute.
     * @see #writeCompletable(AsynchronousByteChannel)
     */
    default <T extends AsynchronousByteChannel> Future<Void> write(final T channel)
            throws InterruptedException, ExecutionException {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        final ByteBuffer buffer = allocate(BYTES);
        put(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
            final Future<Integer> future = channel.write(buffer);
            final int written = future.get();
        }
        return null;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     *
     * @param channel the channel to which bytes are written.
     * @param <T>     channel type parameter
     * @return a completable future.
     * @see #write(AsynchronousByteChannel)
     */
    default <T extends AsynchronousByteChannel> CompletableFuture<T> writeCompletable(final T channel) {
        if (channel == null) {
            throw new NullPointerException("channel is null");
        }
        final ByteBuffer buffer = allocate(BYTES);
        put(buffer);
        buffer.flip();
        return null;
    }
}
