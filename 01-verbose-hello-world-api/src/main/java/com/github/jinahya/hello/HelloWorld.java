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

import com.github.jinahya.hello.util.JavaNioByteBufferUtils;
import org.slf4j.Logger;

import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.Function;

/**
 * An interface for generating <a href="#hello-world-bytes">hello-world-bytes</a> to various
 * targets.
 * <p>
 * All methods defined in this interface are thread-safe.
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
@SuppressWarnings({
        "java:S112",  // Generic exceptions should never be thrown
        "java:S1168", // Empty arrays and collections should be returned instead of null
        "java:S1481", // Unused local variables should be removed
        "java:S1854", // Unused assignments should be removed
        "java:S1865", // useless assignments
        "java:S4274"  // assert ...
})
public interface HelloWorld {

    // ---------------------------------------------------------------------------------- log/logger

    /**
     * Returns a logger for this interface.
     *
     * @return a logger for this interface.
     */
    private Logger log() {
        return HelloWorldLoggers.log();
    }

    /**
     * Returns a logger for this interface.
     *
     * @return a logger for this interface.
     */
    private System.Logger logger() {
        return HelloWorldLoggers.logger();
    }

    // ----------------------------------------------------------------------------------- constants

    /**
     * The length of the <a href="#hello-world-bytes">hello-world-bytes</a> which is {@value}.
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.3">9.3.
     * Field (Constant) Declarations</a> (The Java® Language Specification / Java SE 21 Edition)
     */
    public static final // redundant
            int BYTES = 12;

    // ----------------------------------------------------------------------------------- java.lang

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> on specified array starting at
     * specified index.
     * <p>
     * The elements in the array, on successful return, will be set as follows.
     * <pre>
     *  0  &lt;= index            index+12    &lt;= array.length
     *  ↓     ↓                       ↓       ↓
     * | |...|h|e|l|l|o|,| |w|o|r|l|d| |...| |
     * </pre>
     *
     * @param array the array on which bytes are set.
     * @param index the starting index of the {@code array} to which bytes are set.
     * @return given {@code array}.
     * @throws NullPointerException           if {@code array} is {@code null}.
     * @throws ArrayIndexOutOfBoundsException if {@code index} is negative, or {@code array.length}
     *                                        is less than ({@code index} +
     *                                        {@link #BYTES}({@value #BYTES})).
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-9.html#jls-9.4">9.4.
     * Method Declarations </a> (The Java® Language Specification / Java SE 21 Edition)
     */
    public   // redundant
    abstract // discouraged
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
     * <p>
     * Default implementation would look like,
     * {@snippet :
     * if (array == null) {
     *     throw new NullPointerException("array is null");
     * }
     * if (array.length < BYTES) {
     *     throw new IndexOutOfBoundsException("array.length(" + array.length +") < " + BYTES);
     * }
     * set(array, 0); // @highlight
     * return array;
     *}
     *
     * @param array the array on which bytes are set.
     * @return given {@code array}.
     * @throws NullPointerException           if {@code array} is {@code null}.
     * @throws ArrayIndexOutOfBoundsException if {@code array.length} is less than
     *                                        {@link #BYTES}({@value #BYTES}).
     * @implSpec The default implementation invokes {@link #set(byte[], int) set(array, index)}
     * method with {@code array} and {@code 0}, and returns the {@code array}.
     * @see #set(byte[], int)
     */
    default byte[] set(final byte[] array) {
        return null;
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to specified appendable.
     * <p>
     * Default implementation would look like,
     * {@snippet :
     * if (appendable == null) {
     *     throw new NullPointerException("appendable is null");
     * }
     * final var array = new byte[BYTES];
     * set(array);
     * for (final var b : array) { // @highlight region
     *     appendable.append((char) b);
     * } // @end
     * return appendable;
     *}
     *
     * @param <T>        appendable type parameter
     * @param appendable the appendable to which bytes are appended.
     * @return given {@code appendable}.
     * @throws NullPointerException if {@code appendable} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #set(byte[]) set(array)} method with an
     * array of {@value #BYTES} bytes, and {@link Appendable#append(char) appends} each byte in the
     * array, as a {@code char}, to {@code appendable}.
     * @see #set(byte[])
     * @see Appendable#append(char)
     */
    default <T extends Appendable> T append(final T appendable) throws IOException {
        if (appendable == null) {
            throw new NullPointerException("appendable is null");
        }
        final var array = new byte[BYTES];
        set(array);
        // append each byte in <array> to <appendable>

        // return given <appendable>
        return appendable;
    }

    // ------------------------------------------------------------------------------------- java.io

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified output stream.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * if (stream == null) {
     *     throw new NullPointerException("stream is null");
     * }
     * final var array = set(new byte[BYTES]);
     * stream.write(array); // @highlight
     * return stream;
     *}
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
    default <T extends OutputStream> T write(final T stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        // get the hello, world bytes
        final var array = set(new byte[BYTES]);
        // write the array to the <stream>

        // return the stream
        return stream;
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the end of specified file.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * if (file == null) {
     *     throw new NullPointerException("file is null");
     * }
     * try (var stream = new FileOutputStream(file, true)) { // @highlight region
     *     write(stream);
     *     stream.flush();
     * } // @end
     * return file;
     *}
     *
     * @param <T>  file type parameter
     * @param file the file to which bytes are appended.
     * @return given {@code file}.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation creates a new {@link FileOutputStream} with
     * {@code file}, in {@link FileOutputStream#FileOutputStream(File, boolean) appending mode},
     * invokes the {@link #write(OutputStream) write(stream)} method with it,
     * {@link OutputStream#flush() flushes} and {@link OutputStream#close() closes} the stream, and
     * returns {@code file}.
     * @see java.io.FileOutputStream#FileOutputStream(File, boolean)
     * @see #write(OutputStream)
     */
    default <T extends File> T append(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // create a new <FileOutputStream> with <file> and <true>
        // invoke <write(stream)> method with it
        // <flush> (and <close>) the stream
        return file;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified data output.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * if (output == null) {
     *     throw new NullPointerException("output is null");
     * }
     * final var array = new byte[BYTES];
     * set(array);
     * output.write(array); // @highlight
     * return output;
     *}
     *
     * @param <T>    data output type parameter
     * @param output the data output to which bytes are written.
     * @return given {@code output}.
     * @throws NullPointerException if {@code output} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #set(byte[])} method with an array of
     * {@value #BYTES} bytes, writes the array to {@code output} by invoking
     * {@link DataOutput#write(byte[])} method on {@code output} with the array, and returns
     * {@code output}.
     * @see #set(byte[])
     * @see DataOutput#write(byte[])
     */
    default <T extends DataOutput> T write(final T output) throws IOException {
        if (output == null) {
            throw new NullPointerException("output is null");
        }
        // get the hello-world-bytes
        final var array = set(new byte[BYTES]);
        // invoke <output.write(array)>

        // return the <output>
        return output;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified random access file
     * starting at its current file pointer.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * if (file == null) {
     *     throw new NullPointerException("file is null");
     * }
     * final var array = new byte[BYTES];
     * set(array);
     * file.write(array); // @highlight
     * return file;
     *}
     *
     * @param <T>  random access file type parameter
     * @param file the random access file to which bytes are written.
     * @return given {@code file}.
     * @throws NullPointerException if {@code file} argument is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #set(byte[])} method with an array of
     * {@value #BYTES} bytes, writes the array to specified random access file by invoking
     * {@link RandomAccessFile#write(byte[])} method on {@code file} with the array, and returns the
     * {@code file}.
     * @see #set(byte[])
     * @see RandomAccessFile#write(byte[])
     */
    default <T extends RandomAccessFile> T write(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // get the hello-world-bytes
        final var array = set(new byte[BYTES]);
        // invoke <file.write(array)>

        // return the <file>
        return file;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified writer.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * if (writer == null) {
     *     throw new NullPointerException("writer is null");
     * }
     * return append(writer); // @highlight
     *}
     *
     * @param <T>    writer type parameter
     * @param writer the writer to which bytes are written.
     * @return given {@code writer}.
     * @throws NullPointerException if {@code writer} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #append(Appendable) append(appendable)}
     * method with {@code writer}, and returns the {@code writer}.
     * @see #append(Appendable)
     */
    default <T extends Writer> T write(final T writer) throws IOException {
        if (writer == null) {
            throw new NullPointerException("writer is null");
        }
        // invoke <append(writer)>

        // return the <writer>
        return writer;
    }

    // ------------------------------------------------------------------------------------ java.net

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> through specified socket.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * if (socket == null) {
     *     throw new NullPointerException("socket is null");
     * }
     * write(socket.getOutputStream()); // @highlight
     * return socket;
     *}
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
    default <T extends Socket> T send(final T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        final var stream = socket.getOutputStream();
        // invoke <write(stream)>

        // return the <socket>
        return socket;
    }

    // ------------------------------------------------------------------------------------ java.nio

    /**
     * Puts the <a href="#hello-world-bytes">hello-world-bytes</a> on specified byte buffer.
     * <p>
     * The buffer's position, on successful return, is incremented by {@value #BYTES}.
     * <pre>
     * Given,
     *
     *          4                                        25            32
     *  0    &lt;= position                           &lt;= limit   &lt;= capacity
     *  ↓       ↓                                         ↓             ↓
     * | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | |
     *         |--------------- remaining ---------------|
     *                                 21
     *
     * Then, on successful return,
     *
     *                                 16                25            32
     *  0                     &lt;= position          &lt;= limit   &lt;= capacity
     *  ↓                               ↓                 ↓             ↓
     * | | | | |h|e|l|l|o|,| |w|o|r|l|d| | | | | | | | | | | | | | | | |
     *                                 |--- remaining ---|
     *                                              9
     * </pre>
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * Objects.requireNonNull(buffer, "buffer is null");
     * if (buffer.remaining() < BYTES) {
     *     throw new BufferOverflowException();
     * }
     * if (buffer.hasArray()) {
     *     var array = buffer.array();
     *     var index = buffer.arrayOffset() + buffer.position();
     *     var position = buffer.position();
     *     set(array, index); // @highlight region
     *     assert buffer.position() == position;
     *     buffer.position(buffer.position() + BYTES); // @end
     * } else {
     *     var array = new byte[BYTES];
     *     set(array);
     *     var position = buffer.position();
     *     buffer.put(array); // @highlight
     *     assert buffer.position() == position + array.length;
     * }
     * return buffer;
     *}
     *
     * @param <T>    buffer type parameter
     * @param buffer the byte buffer on which bytes are put.
     * @return given {@code buffer}.
     * @throws NullPointerException    if {@code buffer} is {@code null}.
     * @throws BufferOverflowException if {@link ByteBuffer#remaining() buffer.remaining} is less
     *                                 than {@value #BYTES}.
     * @implSpec The default implementation, if {@code buffer}
     * {@link ByteBuffer#hasArray() has a backing-array}, invokes
     * {@link #set(byte[], int) #set(array, index)} method with the
     * {@link ByteBuffer#array() buffer.array()} and
     * ({@link ByteBuffer#arrayOffset() buffer.arrayOffset()}
     * + {@link ByteBuffer#position() buffer.position()}), and then manually increments the buffer"s
     * position by {@value #BYTES}. Otherwise, this method invokes {@link #set(byte[]) #set(array)}
     * method with an array of {@value #BYTES} bytes, and puts the {@code array} on the
     * {@code buffer} by invoking {@link ByteBuffer#put(byte[])} method, on {@code buffer}, with the
     * array.
     * @see ByteBuffer#hasArray()
     * @see ByteBuffer#array()
     * @see ByteBuffer#arrayOffset()
     * @see ByteBuffer#position()
     * @see ByteBuffer#position(int)
     * @see #set(byte[], int)
     * @see ByteBuffer#put(byte[])
     */
    default <T extends ByteBuffer> T put(final T buffer) {
        if (Objects.requireNonNull(buffer, "buffer is null").remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        if (buffer.hasArray()) {
            // invoke <set(buffer.array(), (buffer.arrayOffset() + buffer.position())>

            // increase <buffer.position> by <BYTES>

        } else {
            // get the hello-world-bytes
            final var array = set(new byte[BYTES]);
            // invoke <buffer.put(array)>

        }
        // return given <buffer>
        return buffer;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to specified channel.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * Objects.requireNonNull(channel, "channel is null");
     * final var buffer = put(ByteBuffer.allocate(BYTES));
     * buffer.flip(); // @highlight
     * while (buffer.hasRemaining()) { // @highlight region
     *     final var written = channel.write(buffer);
     *     assert written >= 0; // why?
     * } // @end
     * return channel;
     *}
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return given {@code channel}.
     * @throws NullPointerException if {@code channel} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer)} method with a buffer of
     * {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, writes the buffer to
     * {@code channel}, by continuously invoking
     * {@link WritableByteChannel#write(ByteBuffer) channel.write(buffer)} while the buffer has
     * remaining, and returns the {@code channel}.
     * @see #put(ByteBuffer)
     * @see ByteBuffer#flip()
     * @see ByteBuffer#hasRemaining()
     * @see WritableByteChannel#write(ByteBuffer)
     */
    default <T extends WritableByteChannel> T write(final T channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        // get the hello-world-bytes
        final var buffer = put(ByteBuffer.allocate(BYTES));
        JavaNioByteBufferUtils.print(buffer);
        // flip the <buffer>
//        buffer.flip(); // limit -> position, position -> zero
        JavaNioByteBufferUtils.print(buffer);
        // invoke <channel.write(buffer)> while <buffer> has <remaining>
//        while (buffer.hasRemaining()) {
//            final var written = channel.write(buffer);
//            assert written >= 0; // why
//            JavaNioByteBufferUtils.print(buffer);
//        }
        // return given <channel>
        return channel;
    }

    /**
     * Sends the <a href="hello-world-bytes">hello-world-bytes</a> to specified socket channel.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * return write(Objects.requireNonNull(channel, "channel is null")); // @highlight
     *}
     *
     * @param channel the socket channel to which the <a
     *                href="hello-world-bytes">hello-world-bytes</a> be sent.
     * @param <T>     socket channel type parameter
     * @return given {@code channel}.
     * @throws IOException if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #write(WritableByteChannel)} method with
     * {@code channel}, and returns the result.
     * @deprecated Invoke {@link #write(WritableByteChannel)} method with the {@code channel}.
     */
    @屋上架屋("SocketChannel implements WritableByteChannel")
    @Deprecated(forRemoval = true)
    default <T extends SocketChannel> T send(final T channel) throws IOException {
        return write(channel);
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the end of specified path
     * to a file. The {@link java.nio.file.Files#size(Path) size} of the {@code path}, on successful
     * return, is increased by {@value #BYTES}.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * Objects.requireNonNull(path, "path is null");
     * try (var channel = FileChannel.open(path, StandardOpenOption.CREATE, // @highlight region
     *                                     StandardOpenOption.APPEND)) {
     *     write(channel);
     *     channel.force(true);
     * } // @end
     * return path;
     *}
     *
     * @param <T>  path type parameter
     * @param path the path a file to which bytes are appended.
     * @return given {@code path}.
     * @throws NullPointerException if {@code path} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec The default implementation opens a {@link FileChannel} from {@code path} with
     * {@link StandardOpenOption#CREATE CREATE} and {@link StandardOpenOption#APPEND APPEND},
     * invokes {@link #write(WritableByteChannel) write(channel)} method with it,
     * {@link FileChannel#force(boolean) forces channel including metadata},
     * {@link WritableByteChannel#close() closes} the channel, and returns the {@code path}.
     * @see FileChannel#open(Path, OpenOption...)
     * @see StandardOpenOption#CREATE
     * @see StandardOpenOption#APPEND
     * @see #write(WritableByteChannel)
     * @see FileChannel#force(boolean)
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jls/se21/html/jls-14.html#jls-14.20.3">14.20.3.
     * try-with-resources</a> (The Java® Language Specification /  Java SE 21 Edition)
     */
    default <T extends Path> T append(final T path) throws IOException {
        Objects.requireNonNull(path, "path is null");
        // open a <FileChannel> with <path>,
        //         <StandardOpenOption.CREATE>, and <StandardOpenOption.APPEND>
        // use the try-with-resources statement
//        try (var channel = FileChannel.open(path, StandardOpenOption.CREATE,
//                                           StandardOpenOption.APPEND)) {
//            // invoke <write(channel)> method with it
////            write(channel);
//            // force changes to both the <file>'s content and metadata
////            channel.force(true);
//        }
        return path;
    }

    /**
     * Writes the <a href="hello-world-bytes">hello-world-bytes</a> to specified channel.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * Objects.requireNonNull(channel, "channel is null");
     * final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
     * while (buffer.hasRemaining()) { // @highlight region
     *     final var written = channel.write(buffer).get();
     *     assert written > 0; // why?
     * } // @end
     * return channel;
     * }
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return given {@code channel}.
     * @throws InterruptedException if interrupted while executing.
     * @throws ExecutionException   if failed to execute.
     * @implSpec The default implementation invokes {@link #put(ByteBuffer) put(buffer)} method with
     * a byte buffer of {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, and writes the
     * buffer to the {@code channel} by, while the {@code buffer}
     * {@link ByteBuffer#hasRemaining() has remaining}, continuously invoking and
     * {@link Future#get() getting the result} of {@link AsynchronousByteChannel#write(ByteBuffer)}
     * method on {@code channel} with the {@code buffer}.
     * @see #put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer)
     */
    default <T extends AsynchronousByteChannel> T write(final T channel)
            throws InterruptedException, ExecutionException {
        Objects.requireNonNull(channel, "channel is null");
        // get the <hello-world-bytes>
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        JavaNioByteBufferUtils.print(buffer);
        // write <buffer> to <channel> while <buffer> has <remaining>
        while (buffer.hasRemaining()) {
            final var future = channel.write(buffer);
            final var result = future.get();
            assert result > 0; // why?
        }
        // return the <channel>
        return channel;
    }

    /**
     * Sends the <a href="hello-world-bytes">hello-world-bytes</a> to specified asynchronous socket
     * channel.
     *
     * @param channel the asynchronous socket channel to which the <a
     *                href="hello-world-bytes">hello-world-bytes</a> be sent.
     * @param <T>     asynchronous socket channel type parameter
     * @return given {@code channel}.
     * @throws InterruptedException interrupted while executing.
     * @throws ExecutionException   when failed to execute.
     * @implSpec Default implementation invokes {@link #write(AsynchronousByteChannel)} method with
     * {@code channel} and returns the result.
     * @deprecated Use {@link #write(AsynchronousByteChannel)} method.
     */
    @屋上架屋("AsynchronousSocketChannel implements AsynchronousByteChannel")
    @Deprecated(forRemoval = true)
    default <T extends AsynchronousSocketChannel> T send(final T channel)
            throws InterruptedException, ExecutionException {
        return write(channel);
    }

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to specified channel, and notifies a completion (or a failure) to specified handler with
     * specified attachment.
     * <p>
     * Default implementation would look like,
     * {@snippet lang = "java":
     * Objects.requireNonNull(channel, "channel is null");
     * Objects.requireNonNull(handler, "handler is null");
     * final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
     * channel.write( // @highlight region
     *         buffer,                                    // <src>
     *         null,                                      // <attachment>
     *         new CompletionHandler<Integer, Object>() { // <handler>
     *                 @Override
     *                 public void completed(final Integer result, final Object a) {
     *                     if (!buffer.hasRemaining()) {
     *                         handler.completed(channel, attachment);
     *                         return;
     *                     }
     *                     channel.write(
     *                             buffer, // <src>
     *                             a,      // <attachment>
     *                             this    // <handler>
     *                     );
     *                 }
     *                 @Override
     *                 public void failed(final Throwable exc, final Object a) {
     *                     handler.failed(exc, attachment);
     *                 }
     *         }
     * ); // @end
     * }
     *
     * @param <T>        channel type parameter
     * @param channel    the channel to which bytes are written.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler.
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     */
    default <T extends AsynchronousByteChannel, A> void write(
            final T channel, final A attachment,
            final CompletionHandler<? super T, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        // get the <hello, world> bytes
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        // keep invoking <channel.write(buffer, attachment, a-handler)>,
        //         while <buffer> has <remaining>
        // and, eventually, invoke <handler.complete(channel, attachment)>

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
     *                        p(3)
     *                        ↓
     * &lt;buffer&gt;:       |h|e|l|l|o|,| |w|o|r|l|d|
     *
     *                       &lt;position&gt; + 3
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
    default <T extends AsynchronousFileChannel> T write(final T channel, long position)
            throws InterruptedException, ExecutionException {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        // get the hello-world-bytes
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        while (buffer.hasRemaining()) {
            final var future = channel.write(buffer, position);
            final var written = future.get();
            assert written > 0; // why?
            position += written;
        }
        return channel;
    }

    @屋上架屋
    default <T extends Path, R> R append(final T path,
                                         final Function<? super T, ? extends R> function)
            throws IOException, InterruptedException, ExecutionException {
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(function, "function is null");
        final var options = new StandardOpenOption[] {
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        };
        try (var channel = AsynchronousFileChannel.open(path, options)) {
            write(channel, channel.size());
        }
        return function.apply(path);
    }

    /**
     * Writes, asynchronously, the <a href="hello-world-bytes">hello-world-bytes</a> to specified
     * channel, starting at specified position, and notifies a completion (or a failure) to the
     * handler.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the file channel to which bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param attachment an attachment for the {@code handler}; may be {@code null}.
     * @param handler    the handler.
     * @throws NullPointerException  if either {@code channel} or {@code handler} is {@code null}.
     * @throws IllegalStateException if {@code position} is negative.
     * @see AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     */
    // @formatter:off
    default <T extends AsynchronousFileChannel, A> void write(
            final T channel, final long position, final A attachment,
            final CompletionHandler<? super T, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(handler, "handler is null");
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        final var accumulator = new LongAccumulator(Long::sum, position);
        channel.write(
                buffer,                     // <src>
                accumulator.get(),          // <position>
                attachment,                 // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override public void completed(final Integer r, final A a) {
                        log().debug("written: {}", r);
                        assert r > 0; // why?
                        accumulator.accumulate(r);
                        if (!buffer.hasRemaining()) {
                            handler.completed(channel, a);
                            return;
                        }
                        channel.write(
                                buffer,            // <src>
                                accumulator.get(), // <position>
                                a,                 // <attachment>
                                this               // <handler>
                        );
                    }
                    @Override public void failed(final Throwable t, final A a) {
                        handler.failed(t, a);
                    }
                }
        );
    } // @formatter:on

    @屋上架屋
    default <T extends Path, A> void append(final T path, final A attachment,
                                            final CompletionHandler<? super T, ? super A> handler)
            throws IOException {
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(handler, "handler is null");
        final var options = new StandardOpenOption[] {
                StandardOpenOption.CREATE, StandardOpenOption.APPEND
        };
        @SuppressWarnings({
                "java:S2095" // Resources should be closed
        })
        final var channel = AsynchronousFileChannel.open(path, options); // @formatter:off
        write(channel, channel.size(), attachment, new CompletionHandler<>() {
            @Override
            public void completed(final AsynchronousFileChannel result, final A attachment) {
                assert result == channel;
                try {
                    result.close();
                } catch (final IOException ioe) {
                    throw new RuntimeException("failed to close channel", ioe);
                }
                handler.completed(path, attachment);
            }
            @Override public void failed(final Throwable exc, final A attachment) {
                try {
                    channel.close();
                } catch (final IOException ioe) {
                    throw new RuntimeException("failed to close channel", ioe);
                }
                handler.failed(exc, attachment);
            }
        }); // @formatter:on
    }
}
