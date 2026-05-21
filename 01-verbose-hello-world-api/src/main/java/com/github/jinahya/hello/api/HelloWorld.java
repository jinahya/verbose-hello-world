package com.github.jinahya.hello.api;

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

import javax.crypto.*;
import java.io.*;
import java.lang.foreign.*;
import java.lang.invoke.*;
import java.net.*;
import java.net.http.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.zip.*;

import static java.nio.charset.StandardCharsets.*;

/**
 * An interface for writing <a href="#hello-world-bytes">hello-world-bytes</a> to various targets.
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
        "java:S4274" // assert ...
})
public interface HelloWorld {

    private static System.Logger log() {
        return System.getLogger(MethodHandles.lookup().lookupClass().getName());
    }

    // ----------------------------------------------------------------------------------- constants

    /**
     * The length of the <a href="#hello-world-bytes">hello-world-bytes</a> which is {@value}.
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-9.html#jls-9.3">9.3.
     * Field (Constant) Declarations</a> (The Java® Language Specification)
     */
    public static final // redundant
            int BYTES = 12;

    // ----------------------------------------------------------------------------------- java.lang

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> on the specified array starting
     * at the specified index.
     * <p>
     * The elements in the array, on successful return, will be set as follows.
     * <pre>
     *  0  &lt;= index          index + 12
     *  ↓     ↓                       ↓
     * | |...|h|e|l|l|o|,| |w|o|r|l|d|...
     *                                   ↑
     *                                   array.length
     * </pre>
     *
     * @param array the array on which bytes are set.
     * @param index the starting index of the {@code array} to which bytes are set.
     * @return the given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code index} is negative, or ({@code index} plus
     *                                   {@value #BYTES}) is greater than {@code array.length}.
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-9.html#jls-9.4">9.4.
     * Method Declarations</a> (The Java® Language Specification)
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-10.html#jls-10.4">10.4.
     * Array Access</a> (The Java® Language Specification)
     */
    public   // redundant
    abstract // discouraged
    byte[] set(byte[] array, int index);

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> on the specified array starting
     * at {@code 0}.
     * <p>
     * The elements in the array, on successful return, will be set as follows.
     * <pre>
     *  0                       12
     *  ↓                       ↓
     * |h|e|l|l|o|,| |w|o|r|l|d|....
     *                              ↑
     *                              array.length
     * </pre>
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * if (array == null) {
     *     throw new NullPointerException("array is null");
     * }
     * if (array.length < BYTES) {
     *     throw new ArrayIndexOutOfBoundsException("array.length(" + array.length + ") < " + BYTES);
     * }
     * set(array, 0); // @highlight
     * return array;
     *}
     *
     * @param array the array on which bytes are set.
     * @return the given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code array.length} is less than
     *                                   {@link #BYTES}({@value #BYTES}).
     * @implSpec Default implementation invokes {@link #set(byte[], int) set(array, index)} method
     * with {@code array} and {@code 0}, and returns the {@code array}.
     * @see #set(byte[], int)
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-10.html#jls-10.4">10.4.
     * Array Access</a> (Java Language Specification)
     */
    default byte[] set(final byte[] array) {
        if (array == null) {
            throw new NullPointerException("array is null");
        }
        if (array.length < BYTES) {
            throw new IndexOutOfBoundsException("array.length(" + array.length + ") < " + BYTES);
        }
        set(array, 0);
        return array;
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified appendable.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
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
     * @return the given {@code appendable}.
     * @throws NullPointerException if {@code appendable} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and {@link Appendable#append(char) appends} each byte in the array,
     * as a {@code char}, to {@code appendable}.
     * @see #set(byte[])
     * @see Appendable#append(char)
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-5.html#jls-5.5">5.5.
     * Casting Contexts</a> (The Java® Language Specification)
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-5.html#jls-5.1.4">5.1.4.
     * Widening and Narrowing Primitive Conversion</a> (The Java® Language Specification)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/Appendable.html">java.lang.Appendable</a>
     */
    default <T extends Appendable> T append(final T appendable) throws IOException {
        if (appendable == null) {
            throw new NullPointerException("appendable is null");
        }
//        final var array = new byte[BYTES];
//        set(array);
//        for (final var b : array) {
//            appendable.append((char) b);
//        }
        return appendable;
    }

    // --------------------------------------------------------------------------- java.lang.foreign

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> on the specified memory segment
     * starting at offset {@code 0}.
     * <p>
     * The memory segment must have at least {@value #BYTES} bytes available.
     * <pre>
     *  0                   1
     *  0                       2    &lt;= segment.byteSize()
     *  ↓                       ↓       ↓
     * |h|e|l|l|o|,| |w|o|r|l|d| |...| |
     * </pre>
     * <p>
     * The default implementation copies bytes using {@link MemorySegment#copy}.
     * {@snippet lang = "java":
     * Objects.requireNonNull(segment, "segment is null");
     * if (segment.byteSize() < BYTES) {
     *     throw new IndexOutOfBoundsException(
     *         "byteSize(" + segment.byteSize() + ") < BYTES(" + BYTES + ")"
     *     );
     * }
     * var array = new byte[BYTES];
     * set(array, 0);
     * MemorySegment.copy(array, 0, segment, ValueLayout.JAVA_BYTE, 0, BYTES);
     * return segment;
     *}
     *
     * @param <T>     memory segment type parameter
     * @param segment the memory segment on which bytes are set.
     * @return the given {@code segment}.
     * @throws NullPointerException      if {@code segment} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code segment.byteSize()} is less than
     *                                   {@value #BYTES}.
     * @apiNote Callers can use {@link MemorySegment#asSlice(long)} to set at a specific offset.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, copies the array into the {@code segment} via
     * {@link MemorySegment#copy(Object, int, MemorySegment, ValueLayout, long, int)}, and returns
     * the {@code segment}.
     * @see MemorySegment#asSlice(long)
     * @see #set(byte[])
     * @see MemorySegment#copy(Object, int, MemorySegment, ValueLayout, long, int)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/lang/foreign/MemorySegment.html">java.lang.foreign.MemorySegment</a>
     */
    default <T extends MemorySegment> T copy(final T segment) {
        Objects.requireNonNull(segment, "segment is null");
        if (segment.byteSize() < BYTES) {
            throw new IndexOutOfBoundsException(
                    "segment.byteSize(" + segment.byteSize() + ") < " + BYTES
            );
        }
        final var array = new byte[BYTES];
        set(array);
        MemorySegment.copy(
                array,
                0,
                segment,
                ValueLayout.JAVA_BYTE,
                0,
                BYTES
        );
        return segment;
    }

    // ------------------------------------------------------------------------------------- java.io

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified output
     * stream.
     * <p>
     * The default implementation would be as follows.
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
     * @return the given {@code stream}.
     * @throws NullPointerException if {@code stream} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @apiNote This method does not {@link OutputStream#flush() flush} the {@code stream}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, writes the array to {@code stream} by invoking
     * {@link OutputStream#write(byte[])} method on the {@code stream} with the array, and returns
     * the {@code stream}.
     * @see #set(byte[])
     * @see OutputStream#write(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/OutputStream.html">java.io.OutputStream</a>
     */
    default <T extends OutputStream> T write(final T stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        final var array = new byte[BYTES];
        set(array);
        stream.write(array);
        return stream;
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the end of the specified
     * file.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * if (file == null) {
     *     throw new NullPointerException("file is null");
     * }
     * try (var stream = new FileOutputStream(file, true)) { // @highlight region
     *     write(stream);
     *     stream.flush(); // maybe redundant, not harmful
     * } // @end
     * return file;
     *}
     *
     * @param <T>  file type parameter
     * @param file the file to which bytes are appended.
     * @return the given {@code file}.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation creates a new {@link FileOutputStream} with {@code file}, in
     * {@link FileOutputStream#FileOutputStream(File, boolean) appending mode}, invokes the
     * {@link #write(OutputStream) write(stream)} method with it,
     * {@link OutputStream#flush() flushes} and {@link OutputStream#close() closes} the stream, and
     * returns {@code file}.
     * @see java.io.FileOutputStream#FileOutputStream(File, boolean)
     * @see #write(OutputStream)
     * @see #append(File, Charset)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/File.html">java.io.File</a>
     */
    default <T extends File> T append(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
//        try (var stream = new FileOutputStream(file, true)) { // appending mode
//            write(stream);
//            stream.flush(); // maybe redundant, not harmful
//        }
        return file;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified data output.
     * <p>
     * The default implementation would be as follows.
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
     * @return the given {@code output}.
     * @throws NullPointerException if {@code output} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #set(byte[])} method with an array of
     * {@value #BYTES} bytes, writes the array to {@code output} by invoking
     * {@link DataOutput#write(byte[])} method on the {@code output} with the {@code array}, and
     * returns the {@code output}.
     * @see #set(byte[])
     * @see DataOutput#write(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/DataOutput.html">java.io.DataOutput</a>
     */
    default <T extends DataOutput> T write(final T output) throws IOException {
        if (output == null) {
            throw new NullPointerException("output is null");
        }
        final var array = new byte[BYTES];
        set(array);
//        output.write(array);
        return output;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified writer.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * if (writer == null) {
     *     throw new NullPointerException("writer is null");
     * }
     * append(writer); // @highlight
     * return writer;
     *}
     *
     * @param <T>    writer type parameter
     * @param writer the writer to which bytes are written.
     * @return the given {@code writer}.
     * @throws NullPointerException if {@code writer} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #append(Appendable) append(appendable)}
     * method with {@code writer}, and returns the {@code writer}.
     * @see #append(Appendable)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/Writer.html">java.io.Writer</a>
     */
    default <T extends Writer> T write(final T writer) throws IOException {
        if (writer == null) {
            throw new NullPointerException("writer is null");
        }
//        append(writer);
        return writer;
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a>, encoded with the specified
     * charset, to the end of the specified file, and returns the file.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * if (file == null) {
     *     throw new NullPointerException("file is null");
     * }
     * if (charset == null) {
     *     throw new NullPointerException("charset is null");
     * }
     * try (var writer = new FileWriter(file, charset, true)) { // @highlight region
     *     write(writer);
     *     writer.flush();
     * } // @end
     * return file;
     *}
     *
     * @param <T>     file type parameter
     * @param file    the file to append to.
     * @param charset the character set to use for encoding.
     * @return the given {@code file}.
     * @throws NullPointerException if {@code file} is {@code null}, or if {@code charset} is
     *                              {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation creates a new {@link FileWriter} with {@code file},
     * {@code charset}, and {@code true} for
     * {@link FileWriter#FileWriter(File, Charset, boolean) appending mode}, invokes the
     * {@link #write(Writer) write(writer)} method with it, {@link Writer#flush() flushes} and
     * {@link Writer#close() closes} the writer, and returns {@code file}.
     * @see FileWriter#FileWriter(File, Charset, boolean)
     * @see #write(Writer)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/io/File.html">java.io.File</a>
     */
    default <T extends File> T append(final T file, final Charset charset) throws IOException {
        Objects.requireNonNull(file, "file is null");
        Objects.requireNonNull(charset, "charset is null");
//        try (var writer = new FileWriter(file, charset, true)) {
//            write(writer);
//            writer.flush();
//        }
        return file;
    }

    // ------------------------------------------------------------------------------------ java.net

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified datagram
     * packet.
     * <p>
     * The bytes are written starting at ({@link DatagramPacket#getOffset() offset} +
     * {@link DatagramPacket#getLength() length}), and the packet's length is increased by
     * {@value #BYTES}.
     * <pre>
     * Given,
     *
     * packet:       0 &le; offset        &le; offset + length1
     *               ↓   ↓               ↓
     * packet.data: | | |.|.|.|.|.|.|.|.| | | | | | | | | | | | | | |
     *                  | -- length1 -- |
     *
     * Then, on successful return,
     *
     * packet:       0 &le; offset                                &lt; offset + length2
     *               ↓   ↓                                       ↓
     * packet.data: | | |.|.|.|.|.|.|.|.|h|e|l|l|o|,| |w|o|r|l|d| | | | |
     *                  | ------ length2 = length1 + 12 ------- |
     * </pre>
     *
     * @param packet the datagram packet to which bytes are appended.
     * @return the given {@code packet}.
     * @throws NullPointerException     if {@code packet} is {@code null}.
     * @throws IllegalArgumentException if the packet's {@link DatagramPacket#getData() data} buffer
     *                                  does not have at least {@value #BYTES} bytes available after
     *                                  {@code offset + length}.
     * @implSpec Default implementation invokes the {@link #set(byte[], int)} method with the
     * {@code packet}'s {@link DatagramPacket#getData() data} buffer and
     * ({@link DatagramPacket#getOffset() packet.offset} +
     * {@link DatagramPacket#getLength() packet.length}) as the index, increments the
     * {@code packet}'s {@link DatagramPacket#getLength() length} by {@value #BYTES}, and returns
     * the {@code packet}.
     * @see DatagramPacket#getData()
     * @see DatagramPacket#getOffset()
     * @see DatagramPacket#getLength()
     * @see DatagramPacket#setLength(int)
     * @see #set(byte[], int)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/DatagramPacket.html">java.net.DatagramPacket</a>
     */
    default DatagramPacket append(final DatagramPacket packet) {
        if (packet == null) {
            throw new NullPointerException("packet is null");
        }
        final var data = packet.getData();
        final var offset = packet.getOffset();
        final var length = packet.getLength();
        if (offset + length + BYTES > data.length) {
            throw new IllegalArgumentException("not enough remaining space in the packet");
        }
//        set(data, offset + length);
//        packet.setLength(packet.getLength() + BYTES);
        return packet;
    }

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified target address
     * via the specified datagram socket.
     *
     * @param <T>    socket type parameter
     * @param socket the datagram socket through which bytes are sent.
     * @param target the target address to which bytes are sent.
     * @return the given {@code socket}.
     * @throws NullPointerException if {@code socket} is {@code null} or {@code target} is
     *                              {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #append(DatagramPacket) append(packet)} with
     * a datagram packet of {@value #BYTES}-long data array with the {@code target} address, and
     * {@link DatagramSocket#send(DatagramPacket) sends} it through the {@code socket}.
     * @see #append(DatagramPacket)
     * @see DatagramSocket#send(DatagramPacket)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/DatagramSocket.html">java.net.DatagramSocket</a>
     */
    default <T extends DatagramSocket> T send(final T socket, final SocketAddress target)
            throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        if (target == null) {
            throw new NullPointerException("target is null");
        }
//        final var packet = new DatagramPacket(new byte[BYTES], 0, target);
//        append(packet);
//        socket.send(packet);
        return socket;
    }

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> through the specified
     * {@link DatagramSocket#isConnected() connected} datagram socket.
     *
     * @param <T>    socket type parameter
     * @param socket the socket through which bytes are sent.
     * @return the given {@code socket}.
     * @throws NullPointerException     if {@code socket} is {@code null}.
     * @throws IllegalArgumentException if {@code socket} is not
     *                                  {@link DatagramSocket#isConnected() connected}.
     * @throws IOException              if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #append(DatagramPacket) append(packet)} with
     * a datagram packet of {@value #BYTES}-long data array, and
     * {@link DatagramSocket#send(DatagramPacket) sends} the packet through the {@code socket}.
     * @see #append(DatagramPacket)
     * @see DatagramSocket#send(DatagramPacket)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/DatagramSocket.html">java.net.DatagramSocket</a>
     */
    default <T extends DatagramSocket> T send(final T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("not connected; " + socket);
        }
        final var target = socket.getRemoteSocketAddress();
//        send(socket, target);
        return socket;
    }

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> through the specified socket.
     * <p>
     * The default implementation would be as follows.
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
     * @return the given {@code socket}.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #write(OutputStream)} method with
     * {@link Socket#getOutputStream() socket.outputStream}, and returns the {@code socket}.
     * @implNote Note that this method does not {@link Flushable#flush() flush} the {@code socket}'s
     * output stream.
     * @see Socket#getOutputStream()
     * @see #write(OutputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/Socket.html">java.net.Socket</a>
     */
    default <T extends Socket> T send(final T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        final var stream = socket.getOutputStream();
//        write(stream);
        return socket;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> through the specified URL
     * connection's output stream.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * if (connection == null) {
     *     throw new NullPointerException("connection is null");
     * }
     * write(connection.getOutputStream()); // @highlight
     * return connection;
     *}
     *
     * @param <T>        URL connection type parameter
     * @param connection the URL connection through which bytes are sent.
     * @return the given {@code connection}.
     * @throws NullPointerException if {@code connection} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #write(OutputStream)} method with
     * {@link URLConnection#getOutputStream() connection.outputStream}, and returns the
     * {@code connection}.
     * @implNote Note that this method does not {@link Flushable#flush() flush} the
     * {@code connection}'s output stream.
     * @see URLConnection#getOutputStream()
     * @see #write(OutputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/net/URLConnection.html">java.net.URLConnection</a>
     */
    default <T extends URLConnection> T write(final T connection) throws IOException {
        if (connection == null) {
            throw new NullPointerException("connection is null");
        }
        final var stream = connection.getOutputStream();
        write(stream);
        return connection;
    }

    // ------------------------------------------------------------------------------- java.net.http

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> as the request body on the
     * specified HTTP request builder with the specified method.
     *
     * @param <T>     builder type parameter
     * @param builder the HTTP request builder on which the body is set.
     * @param method  the HTTP method name (e.g., "POST", "PUT").
     * @return the given {@code builder}.
     * @throws NullPointerException if {@code builder} is {@code null} or {@code method} is
     *                              {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and invokes
     * {@link HttpRequest.Builder#method(String, HttpRequest.BodyPublisher) builder.method(method,
     * publisher)} with {@code method} and
     * {@link HttpRequest.BodyPublishers#ofByteArray(byte[]) BodyPublishers.ofByteArray(array)}.
     * @see #set(byte[])
     * @see HttpRequest.Builder#method(String, HttpRequest.BodyPublisher)
     * @see HttpRequest.BodyPublishers#ofByteArray(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.net.http/java/net/http/HttpRequest.Builder.html">java.net.http.HttpRequest.Builder</a>
     */
    default <T extends HttpRequest.Builder> T method(final T builder, final String method) {
        Objects.requireNonNull(builder, "builder is null");
        Objects.requireNonNull(method, "method is null");
        final var array = new byte[BYTES];
        set(array);
        builder.method(method, HttpRequest.BodyPublishers.ofByteArray(array));
        return builder;
    }

    // -------------------------------------------------------------------------------- java.net.ssl

    // ------------------------------------------------------------------------------------ java.nio

    /**
     * Puts the <a href="#hello-world-bytes">hello-world-bytes</a> on the specified byte buffer.
     * <p>
     * The buffer's position, on successful return, is incremented by {@value #BYTES}.
     * <pre>
     * Given,
     *
     *  0                   1                   2                   3
     *          4                                         5             2
     *  0    &lt;= position                           &lt;= limit   &lt;= capacity
     *  ↓       ↓                                         ↓             ↓
     * | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | | |
     *         | -------------- remaining -------------- |
     *                               (21)
     *
     * Then, on successful return,
     *
     *  0                   1                   2                   3
     *                                  6                 5             2
     *  0                     &lt;= position          &lt;= limit   &lt;= capacity
     *  ↓                               ↓                 ↓             ↓
     * | | | | |h|e|l|l|o|,| |w|o|r|l|d| | | | | | | | | | | | | | | | |
     *                                 | -- remaining -- |
     *                                            (9)
     * </pre>
     * <p>
     * The default implementation would be as follows.
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
     *     assert buffer.position() == position; // still
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
     * @return the given {@code buffer}.
     * @throws NullPointerException    if {@code buffer} is {@code null}.
     * @throws BufferOverflowException if {@link ByteBuffer#remaining() buffer.remaining} is less
     *                                 than {@value #BYTES}.
     * @implSpec Default implementation, if {@code buffer}
     * {@link ByteBuffer#hasArray() has a backing-array}, invokes
     * {@link #set(byte[], int) #set(array, index)} method with the
     * {@link ByteBuffer#array() buffer.array()} and
     * ({@link ByteBuffer#arrayOffset() buffer.arrayOffset()} +
     * {@link ByteBuffer#position() buffer.position()}), and then manually increments the buffer's
     * position by {@value #BYTES}. Otherwise, this method invokes {@link #set(byte[]) set(array)}
     * method with an array of {@value #BYTES} bytes, and puts the {@code array} on the
     * {@code buffer} by invoking {@link ByteBuffer#put(byte[])} method, on {@code buffer}, with the
     * {@code array}.
     * @see ByteBuffer#hasArray()
     * @see ByteBuffer#array()
     * @see ByteBuffer#arrayOffset()
     * @see ByteBuffer#position()
     * @see ByteBuffer#position(int)
     * @see #set(byte[], int)
     * @see ByteBuffer#put(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/ByteBuffer.html">java.nio.ByteBuffer</a>
     */
    default <T extends ByteBuffer> T put(final T buffer) {
        if (Objects.requireNonNull(buffer, "buffer is null").remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        if (buffer.hasArray()) {
            final var array = buffer.array();
            final var index = buffer.arrayOffset() + buffer.position();
//            set(array, index);
//            buffer.position(buffer.position() + BYTES);
        } else {
            final var array = new byte[BYTES];
            set(array);
//            buffer.put(array);
        }
        return buffer;
    }

    // --------------------------------------------------------------------------- java.nio.channels

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified channel.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(channel, "channel is null");
     * var buffer = put(ByteBuffer.allocate(BYTES));
     * buffer.flip(); // @highlight
     * while (buffer.hasRemaining()) { // @highlight region
     *     channel.write(buffer);
     * } // @end
     * return channel;
     *}
     *
     * @param <T>     channel type parameter
     * @param channel the writable byte channel to which bytes are written.
     * @return the given {@code channel}.
     * @throws NullPointerException if {@code channel} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer)} method with a byte buffer
     * of {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, writes the buffer to
     * {@code channel}, by continuously invoking
     * {@link WritableByteChannel#write(ByteBuffer) channel.write(buffer)} while the buffer has
     * remaining, and returns the {@code channel}.
     * @see #put(ByteBuffer)
     * @see ByteBuffer#flip()
     * @see ByteBuffer#hasRemaining()
     * @see WritableByteChannel#write(ByteBuffer)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/channels/WritableByteChannel.html">java.nio.channels.WritableByteChannel</a>
     */
    default <T extends WritableByteChannel> T write(final T channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
//        buffer.flip();
//        while (buffer.hasRemaining()) {
//            channel.write(buffer);
//        }
        return channel;
    }

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified target address
     * via the specified datagram channel.
     *
     * @param <T>     channel type parameter
     * @param channel the datagram channel through which the bytes are sent.
     * @param target  the address to which the bytes are sent.
     * @return the given {@code channel}.
     * @throws NullPointerException if {@code channel} is {@code null}, or if {@code target} is
     *                              {@code null}.
     * @throws RuntimeException     if, on the non-blocking branch, while the implementation is
     *                              waiting for the underlying output buffer to drain,
     *                              {@code channel.SO_SNDBUF} cannot be read (returns {@code null})
     *                              or is observed to be less than {@value #BYTES}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation, if the {@code channel}
     * {@link DatagramChannel#isBlocking() is in blocking mode}, invokes
     * {@link #send(DatagramSocket, SocketAddress) #send(socket, target)} method with the
     * {@link DatagramChannel#socket() channel.socket()} and the {@code target}, and returns the
     * {@link DatagramSocket#getChannel() channel} associated with the resulting socket. Otherwise,
     * this method invokes {@link #put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, and repeatedly invokes
     * {@link DatagramChannel#send(ByteBuffer, SocketAddress) channel.send(buffer, target)} until a
     * non-zero count is returned. While that call returns {@code 0}, the implementation re-reads
     * {@link StandardSocketOptions#SO_SNDBUF SO_SNDBUF} and throws a {@link RuntimeException} if
     * the option cannot be read, or if its value is less than {@value #BYTES} — the loop's progress
     * guarantee depends on a verified {@code SO_SNDBUF >= }{@value #BYTES}.
     * @apiNote The blocking branch routes through {@link DatagramChannel#socket()} and forwards to
     * the legacy {@link DatagramSocket#send(DatagramPacket)} pathway. The non-blocking branch uses
     * the NIO {@link DatagramChannel#send(ByteBuffer, SocketAddress)} pathway directly. Splitting
     * the implementation by {@link DatagramChannel#isBlocking() blocking mode} avoids the
     * {@link java.nio.channels.IllegalBlockingModeException IllegalBlockingModeException} that the
     * bridge socket would otherwise throw on a non-blocking channel. On the non-blocking branch,
     * {@code channel.send} may return {@code 0} when the OS send buffer is temporarily full; the
     * loop then spins, issuing {@link Thread#onSpinWait()} as a hint, until the kernel drains the
     * buffer and the datagram is accepted. Callers that need cooperative back-pressure handling
     * should use a {@link java.nio.channels.Selector Selector} with
     * {@link java.nio.channels.SelectionKey#OP_WRITE OP_WRITE} instead of this method.
     * @see DatagramChannel#isBlocking()
     * @see DatagramChannel#socket()
     * @see DatagramSocket#getChannel()
     * @see #send(DatagramSocket, SocketAddress)
     * @see #put(ByteBuffer)
     * @see DatagramChannel#send(ByteBuffer, SocketAddress)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/channels/DatagramChannel.html">java.nio.channels.DatagramChannel</a>
     */
    default <T extends DatagramChannel> T send(final T channel, final SocketAddress target)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(target, "target is null");
        if (channel.isBlocking()) {
            final var socket = channel.socket();
            send(socket, target);
            return channel;
        }
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.send(buffer, target);
        }
        return channel;
    }

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> through the specified connected
     * datagram channel.
     *
     * @param <T>     channel type parameter
     * @param channel the connected datagram channel through which bytes are sent.
     * @return the given {@code channel}.
     * @throws NullPointerException     if {@code channel} is {@code null}.
     * @throws IllegalArgumentException if {@code channel} is not
     *                                  {@link DatagramChannel#isConnected() connected}.
     * @throws IOException              if an I/O error occurs.
     * @implSpec Default implementation, after verifying that the {@code channel} is
     * {@link DatagramChannel#isConnected() connected}, dispatches as follows: if the
     * {@code channel} {@link DatagramChannel#isBlocking() is in blocking mode}, invokes
     * {@link #send(DatagramSocket) #send(socket)} method with the
     * {@link DatagramChannel#socket() channel.socket()}, and returns the
     * {@link DatagramSocket#getChannel() channel} associated with the resulting socket. Otherwise,
     * this method invokes {@link #write(WritableByteChannel) #write(channel)} method with the
     * {@code channel} widened to a {@link WritableByteChannel}, and returns the result.
     * @apiNote The blocking branch routes through {@link DatagramChannel#socket()} and forwards to
     * the legacy {@link DatagramSocket#send(DatagramPacket)} pathway. The non-blocking branch
     * delegates to the generic {@link WritableByteChannel} pathway, which handles the
     * {@link ByteBuffer}-based I/O. Splitting the implementation by
     * {@link DatagramChannel#isBlocking() blocking mode} avoids the
     * {@link java.nio.channels.IllegalBlockingModeException IllegalBlockingModeException} that the
     * bridge socket would otherwise throw on a non-blocking channel.
     * @see DatagramChannel#isConnected()
     * @see DatagramChannel#isBlocking()
     * @see DatagramChannel#socket()
     * @see DatagramSocket#getChannel()
     * @see #send(DatagramSocket)
     * @see #write(WritableByteChannel)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/channels/DatagramChannel.html">java.nio.channels.DatagramChannel</a>
     */
    default <T extends DatagramChannel> T send(final T channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isConnected()) {
            throw new IllegalArgumentException("not connected: " + channel);
        }
        if (channel.isBlocking()) {
            final var socket = channel.socket();
            send(socket);
            return channel;
        }
        write(channel);
        return channel;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified channel.
     *
     * @param <T>     channel type parameter
     * @param channel the asynchronous byte channel to which bytes are written.
     * @return the given {@code channel}.
     * @throws NullPointerException if {@code channel} is {@code null}.
     * @throws InterruptedException if interrupted while executing.
     * @throws ExecutionException   if failed while writing.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer) put(buffer)} method with a
     * byte buffer of {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, and writes the
     * buffer to the {@code channel} by, while the {@code buffer}
     * {@link ByteBuffer#hasRemaining() has remaining}, continuously invoking and
     * {@link Future#get() getting the result} of {@link AsynchronousByteChannel#write(ByteBuffer)}
     * method on {@code channel} with the {@code buffer}.
     * @see #put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/channels/AsynchronousByteChannel.html">java.nio.channels.AsynchronousByteChannel</a>
     */
    default <T extends AsynchronousByteChannel> T write(final T channel)
            throws InterruptedException, ExecutionException {
        Objects.requireNonNull(channel, "channel is null");
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
//        while (buffer.hasRemaining()) {
//            final var future = channel.write(buffer);
//            final var written = future.get();
//        }
        return channel;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified asynchronous
     * file channel, starting at the given file position.
     * <pre>
     * Given,
     *                  p(0)
     *                  ↓
     * &lt;buffer&gt;:       |h|e|l|l|o|,| |w|o|r|l|d|
     *
     *                 &lt;position&gt; + 0
     *                  ↓
     * &lt;channel&gt;: ...| | | | | | | | | | | | | | |...
     *
     * Then, in an intermediate state, possibly,
     *                        p(3)
     *                        ↓
     * &lt;buffer&gt;:       |h|e|l|l|o|,| |w|o|r|l|d|
     *
     *                       &lt;position&gt; + 3
     *                        ↓
     * &lt;channel&gt;: ...| |h|e|l| | | | | | | | | | |...
     *
     * And, on successful return,
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
     * @param channel  the asynchronous file channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @return the given {@code channel}.
     * @throws NullPointerException     if {@code channel} is {@code null}.
     * @throws IllegalArgumentException if {@code position} is negative.
     * @throws InterruptedException     if interrupted while executing.
     * @throws ExecutionException       if failed to write bytes.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer) put(buffer)} with a byte
     * buffer of {@value #BYTES} bytes, flips it, and writes the {@code buffer} to {@code channel},
     * while the {@code buffer} {@link ByteBuffer#hasRemaining() has remaining}, by continuously
     * invoking
     * {@link AsynchronousFileChannel#write(ByteBuffer, long) channel.write(buffer, position)}
     * method with the {@code buffer} and {@code position} adjusted by the previous result.
     * @see #put(ByteBuffer)
     * @see AsynchronousFileChannel#write(ByteBuffer, long)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/channels/AsynchronousFileChannel.html">java.nio.channels.AsynchronousFileChannel</a>
     */
    default <T extends AsynchronousFileChannel> T write(final T channel, long position)
            throws InterruptedException, ExecutionException {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
//        while (buffer.hasRemaining()) {
//            final var future = channel.write(buffer, position);
//            final var written = future.get();
//            position += written;
//        }
        return channel;
    }

    // ------------------------------------------------------------------------------- java.nio.file

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the end of the specified
     * path to a file. The {@link java.nio.file.Files#size(Path) size} of the file, on successful
     * return, is increased by {@value #BYTES}.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(path, "path is null");
     * try (var channel = FileChannel.open(path, // @highlight region
     *                                     StandardOpenOption.CREATE,
     *                                     StandardOpenOption.APPEND)) {
     *     write(channel);
     * } // @end
     * return path;
     *}
     *
     * @param <T>  path type parameter
     * @param path the path to a file to which bytes are appended.
     * @return the given {@code path}.
     * @throws NullPointerException if {@code path} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation opens a {@link FileChannel} from {@code path} with
     * {@link StandardOpenOption#CREATE CREATE} and {@link StandardOpenOption#APPEND APPEND},
     * invokes {@link #write(WritableByteChannel) write(channel)} method with it,
     * {@link WritableByteChannel#close() closes} the channel, and returns the {@code path}.
     * @see FileChannel#open(Path, OpenOption...)
     * @see StandardOpenOption#CREATE
     * @see StandardOpenOption#APPEND
     * @see #write(WritableByteChannel)
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-14.html#jls-14.20.3">14.20.3.
     * try-with-resources</a> (The Java® Language Specification)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/file/Path.html">java.nio.file.Path</a>
     */
    default <T extends Path> T append(final T path) throws IOException {
        Objects.requireNonNull(path, "path is null");
        final var options = new OpenOption[] {
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        };
//        try (var channel = FileChannel.open(path, options)) {
//            write(channel);
//        }
        return path;
    }

    // ------------------------------------------------------------------------------- java.security

    /**
     * Updates the specified message digest with the <a
     * href="#hello-world-bytes">hello-world-bytes</a>.
     *
     * @param <T>    message digest type parameter
     * @param digest the message digest to be updated.
     * @return the given {@code digest}.
     * @throws NullPointerException if {@code digest} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, {@link MessageDigest#update(byte[]) updates} the {@code digest}
     * with the array, and returns the {@code digest}.
     * @see #set(byte[])
     * @see MessageDigest#update(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/MessageDigest.html">java.security.MessageDigest</a>
     */
    default <T extends MessageDigest> T update(final T digest) {
        Objects.requireNonNull(digest, "digest is null");
        final var array = new byte[BYTES];
        set(array);
        digest.update(array);
        return digest;
    }

    /**
     * Updates the specified signature with the <a href="#hello-world-bytes">hello-world-bytes</a>.
     *
     * @param <T>       signature type parameter
     * @param signature the signature to be updated.
     * @return the given {@code signature}.
     * @throws NullPointerException if {@code signature} is {@code null}.
     * @throws SignatureException   if the signature is not initialized properly.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and {@link Signature#update(byte[]) updates} the {@code signature}
     * with the array.
     * @see #set(byte[])
     * @see Signature#update(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
     */
    default <T extends Signature> T update(final T signature) throws SignatureException {
        Objects.requireNonNull(signature, "signature is null");
        final var array = new byte[BYTES];
        set(array);
        signature.update(array);
        return signature;
    }

    // ------------------------------------------------------------------------------------ java.sql

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a>, as an ASCII stream value, of the
     * designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, wraps the array in a {@link ByteArrayInputStream}, invokes
     * {@link PreparedStatement#setAsciiStream(int, InputStream) setAsciiStream(parameterIndex,
     * stream)} method on the {@code preparedStatement} with the {@code parameterIndex} and the
     * stream, {@link InputStream#close() closes} the stream, and returns the
     * {@code preparedStatement}.
     * @see #set(byte[])
     * @see PreparedStatement#setAsciiStream(int, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setAsciiStream(final T preparedStatement,
                                                           final int parameterIndex)
            throws IOException, SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var array = new byte[BYTES];
        set(array);
        try (var x = new ByteArrayInputStream(array)) {
            preparedStatement.setAsciiStream(parameterIndex, x);
        }
        return preparedStatement;
    }

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a>, as a binary stream value, of the
     * designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, wraps the array in a {@link ByteArrayInputStream}, invokes
     * {@link PreparedStatement#setBinaryStream(int, InputStream) setBinaryStream(parameterIndex,
     * stream)} method on the {@code preparedStatement} with the {@code parameterIndex} and the
     * stream, {@link InputStream#close() closes} the stream, and returns the
     * {@code preparedStatement}.
     * @see #set(byte[])
     * @see PreparedStatement#setBinaryStream(int, InputStream)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setBinaryStream(final T preparedStatement,
                                                            final int parameterIndex)
            throws IOException, SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var array = new byte[BYTES];
        set(array);
        try (var x = new ByteArrayInputStream(array)) {
            preparedStatement.setBinaryStream(parameterIndex, x);
        }
        return preparedStatement;
    }

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> as the value of the designated
     * parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, invokes
     * {@link PreparedStatement#setBytes(int, byte[]) setBytes(parameterIndex, array)} method on the
     * {@code preparedStatement} with the {@code parameterIndex} and the array, and returns the
     * {@code preparedStatement}.
     * @see #set(byte[])
     * @see PreparedStatement#setBytes(int, byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setBytes(final T preparedStatement,
                                                     final int parameterIndex)
            throws SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var array = new byte[BYTES];
        set(array);
        preparedStatement.setBytes(parameterIndex, array);
        return preparedStatement;
    }

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a>, as a character stream value, of
     * the designated parameter on the specified prepared statement.
     *
     * @param <T>               prepared statement type parameter
     * @param preparedStatement the prepared statement on which the value is set.
     * @param parameterIndex    the first parameter is {@code 1}, the second is {@code 2}, ....
     * @return the given {@code preparedStatement}.
     * @throws NullPointerException     if {@code preparedStatement} is {@code null}.
     * @throws IllegalArgumentException if {@code parameterIndex} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if {@code parameterIndex} does not correspond to a parameter
     *                                  marker in the SQL statement, if a database access error
     *                                  occurs, or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, wraps the array in an {@link InputStreamReader} decoded as
     * {@link StandardCharsets#US_ASCII US_ASCII}, invokes
     * {@link PreparedStatement#setCharacterStream(int, Reader) setCharacterStream(parameterIndex,
     * reader)} method on the {@code preparedStatement} with the {@code parameterIndex} and the
     * reader, {@link Reader#close() closes} the reader, and returns the {@code preparedStatement}.
     * @see #set(byte[])
     * @see PreparedStatement#setCharacterStream(int, Reader)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/PreparedStatement.html">java.sql.PreparedStatement</a>
     */
    default <T extends PreparedStatement> T setCharacterStream(final T preparedStatement,
                                                               final int parameterIndex)
            throws IOException, SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var array = new byte[BYTES];
        set(array);
        try (var reader = new InputStreamReader(new ByteArrayInputStream(array),
                                                US_ASCII)) {
            preparedStatement.setCharacterStream(parameterIndex, reader);
        }
        return preparedStatement;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified blob, through
     * a binary stream obtained from the blob, starting at the specified position.
     *
     * @param <T>  blob type parameter
     * @param blob the blob to which the bytes are written.
     * @param pos  the position in the blob at which to start writing; the first byte is at position
     *             {@code 1}.
     * @return the given {@code blob}.
     * @throws NullPointerException     if {@code blob} is {@code null}.
     * @throws IllegalArgumentException if {@code pos} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if there is an error accessing the {@code BLOB} value.
     * @implSpec Default implementation opens an output stream by invoking
     * {@link Blob#setBinaryStream(long) blob.setBinaryStream(pos)} method on the {@code blob} with
     * the {@code pos}, invokes {@link #write(OutputStream) write(stream)} method with the stream,
     * {@link OutputStream#flush() flushes} and {@link OutputStream#close() closes} the stream, and
     * returns the {@code blob}.
     * @see #write(OutputStream)
     * @see Blob#setBinaryStream(long)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Blob.html">java.sql.Blob</a>
     */
    default <T extends Blob> T setBinaryStream(final T blob, final long pos)
            throws SQLException, IOException {
        Objects.requireNonNull(blob, "blob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        try (var stream = blob.setBinaryStream(pos)) {
            write(stream);
            stream.flush();
        }
        return blob;
    }

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> in the specified blob, starting
     * at the specified position.
     *
     * @param <T>  blob type parameter
     * @param blob the blob on which the bytes are set.
     * @param pos  the position in the blob at which to start writing; the first byte is at position
     *             {@code 1}.
     * @return the given {@code blob}.
     * @throws NullPointerException     if {@code blob} is {@code null}.
     * @throws IllegalArgumentException if {@code pos} is not positive.
     * @throws SQLException             if there is an error accessing the {@code BLOB} value.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, invokes
     * {@link Blob#setBytes(long, byte[]) blob.setBytes(pos, array)} method on the {@code blob} with
     * the {@code pos} and the array, and returns the {@code blob}.
     * @see #set(byte[])
     * @see Blob#setBytes(long, byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Blob.html">java.sql.Blob</a>
     */
    default <T extends Blob> T setBytes(final T blob, final long pos) throws SQLException {
        Objects.requireNonNull(blob, "blob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        final var array = new byte[BYTES];
        set(array);
        blob.setBytes(pos, array);
        return blob;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified clob, through
     * an ASCII stream obtained from the clob, starting at the specified position.
     *
     * @param <T>  clob type parameter
     * @param clob the clob to which the bytes are written.
     * @param pos  the position in the clob at which to start writing; the first character is at
     *             position {@code 1}.
     * @return the given {@code clob}.
     * @throws NullPointerException     if {@code clob} is {@code null}.
     * @throws IllegalArgumentException if {@code pos} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if there is an error accessing the {@code CLOB} value.
     * @implSpec Default implementation opens an output stream by invoking
     * {@link Clob#setAsciiStream(long) clob.setAsciiStream(pos)} method on the {@code clob} with
     * the {@code pos}, invokes {@link #write(OutputStream) write(stream)} method with the stream,
     * {@link OutputStream#flush() flushes} and {@link OutputStream#close() closes} the stream, and
     * returns the {@code clob}.
     * @see #write(OutputStream)
     * @see Clob#setAsciiStream(long)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Clob.html">java.sql.Clob</a>
     */
    default <T extends Clob> T setAsciiStream(final T clob, final long pos)
            throws SQLException, IOException {
        Objects.requireNonNull(clob, "clob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        try (var stream = clob.setAsciiStream(pos)) {
            write(stream);
            stream.flush();
        }
        return clob;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified clob, through
     * a character stream obtained from the clob, starting at the specified position.
     *
     * @param <T>  clob type parameter
     * @param clob the clob to which the characters are written.
     * @param pos  the position in the clob at which to start writing; the first character is at
     *             position {@code 1}.
     * @return the given {@code clob}.
     * @throws NullPointerException     if {@code clob} is {@code null}.
     * @throws IllegalArgumentException if {@code pos} is not positive.
     * @throws IOException              if an I/O error occurs.
     * @throws SQLException             if there is an error accessing the {@code CLOB} value.
     * @implSpec Default implementation opens a writer by invoking
     * {@link Clob#setCharacterStream(long) clob.setCharacterStream(pos)} method on the {@code clob}
     * with the {@code pos}, invokes {@link #write(Writer) write(writer)} method with the writer,
     * {@link Writer#flush() flushes} and {@link Writer#close() closes} the writer, and returns the
     * {@code clob}.
     * @see #write(Writer)
     * @see Clob#setCharacterStream(long)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Clob.html">java.sql.Clob</a>
     */
    default <T extends Clob> T setCharacterStream(final T clob, final long pos)
            throws SQLException, IOException {
        Objects.requireNonNull(clob, "clob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        try (var writer = clob.setCharacterStream(pos)) {
            write(writer);
            writer.flush();
        }
        return clob;
    }

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a>, as a string, in the specified
     * clob starting at the specified position.
     *
     * @param <T>  clob type parameter
     * @param clob the clob into which the string is written.
     * @param pos  the position at which writing starts; the first character is at position
     *             {@code 1}.
     * @return the given {@code clob}.
     * @throws NullPointerException     if {@code clob} is {@code null}.
     * @throws IllegalArgumentException if {@code pos} is not positive.
     * @throws SQLException             if there is an error accessing the {@code CLOB} value, or if
     *                                  {@code pos} is greater than the length of the {@code clob}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, decodes the array as an ASCII string, invokes
     * {@link Clob#setString(long, String) clob.setString(pos, string)} method on the {@code clob}
     * with the {@code pos} and the string, and returns the {@code clob}.
     * @see #set(byte[])
     * @see Clob#setString(long, String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.sql/java/sql/Clob.html">java.sql.Clob</a>
     */
    default <T extends Clob> T setString(final T clob, final long pos) throws SQLException {
        Objects.requireNonNull(clob, "clob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        final var array = new byte[BYTES];
        set(array);
        final var string = new String(array, US_ASCII);
        clob.setString(pos, string);
        return clob;
    }

    // ----------------------------------------------------------------------------------- java.text

    /**
     * Sets a string, decoded from the <a href="#hello-world-bytes">hello-world-bytes</a>, as the
     * text to be scanned by the specified break iterator.
     *
     * @param <T>      break iterator type parameter
     * @param iterator the break iterator whose text is set.
     * @return the given {@code iterator}.
     * @throws NullPointerException if {@code iterator} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, decodes the array as a
     * {@link java.nio.charset.StandardCharsets#US_ASCII US-ASCII} string, and invokes
     * {@link BreakIterator#setText(String)} method, on the {@code iterator}, with the string.
     * @see #set(byte[])
     * @see BreakIterator#setText(String)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/text/BreakIterator.html">java.text.BreakIterator</a>
     */
    default <T extends BreakIterator> T setText(final T iterator) {
        Objects.requireNonNull(iterator, "iterator is null");
        final var array = new byte[BYTES];
        set(array);
        final var string = new String(array, US_ASCII);
        iterator.setText(string);
        return iterator;
    }

    // ----------------------------------------------------------------------------------- java.util

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> into the specified bit set,
     * starting at the specified index, in little-endian bit order (LSB first).
     * <pre>
     * 'h' = 0x68 = 0b0110_1000, 'e' = 0x65 = 0b0110_0101, ...
     *
     *      h --->          e --->
     *      index           index + 8
     *      ↓               ↓
     * ... |0|0|0|1|0|1|1|0|1|0|1|0| ...
     *      ↓             ↓
     *      LSB           MSB
     * </pre>
     *
     * @param <T>    bit set type parameter
     * @param bitset the bit set into which the bits are set.
     * @param index  the starting index in the bit set.
     * @return the given {@code bitset}.
     * @throws NullPointerException     if {@code bitset} is {@code null}.
     * @throws IllegalArgumentException if {@code index} is negative.
     * @implSpec The default implementation invokes the {@link #set(byte[]) set(array)} method, and
     * sets each bit of the result into the given {@code bitset} in little-endian bit order.
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/BitSet.html">java.util.BitSet</a>
     */
    default <T extends BitSet> T set(final T bitset, int index) {
        if (bitset == null) {
            throw new NullPointerException("bitset is null");
        }
        if (index < 0) {
            throw new IllegalArgumentException("negative index: " + index);
        }
        final var array = new byte[BYTES];
        set(array);
        for (int b : array) {
            for (var i = 0; i < Byte.SIZE; i++) {
                bitset.set(index++, (b & 1) == 1);
                b >>>= 1;
            }
        }
        return bitset;
    }

    /**
     * Collects each of the <a href="#hello-world-bytes">hello-world-bytes</a>, mapped by the
     * specified mapper, into the specified collection.
     *
     * @param <T>        collection type parameter
     * @param <U>        element type parameter
     * @param collection the collection into which each mapped value is collected.
     * @param mapper     the function applied to each byte, boxed as {@link Byte}, to produce the
     *                   value to be collected.
     * @return the given {@code collection}.
     * @throws NullPointerException if either {@code collection} or {@code mapper} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, applies the {@code mapper} to each byte in the array, boxed as
     * {@link Byte}, and {@link SequencedCollection#addLast(Object) appends} the result to the end
     * of the {@code collection}.
     * @see #set(byte[])
     * @see Function#apply(Object)
     * @see SequencedCollection#addLast(Object)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/SequencedCollection.html">java.util.SequencedCollection</a>
     */
    default <T extends SequencedCollection<? super U>, U>
    T add(final T collection, final Function<? super Byte, ? extends U> mapper) {
        Objects.requireNonNull(collection, "collection is null");
        Objects.requireNonNull(mapper, "mapper is null");
        final var array = new byte[BYTES];
        set(array);
        for (final var b : array) {
            collection.addLast(mapper.apply(b));
        }
        return collection;
    }

    /**
     * Puts each of the <a href="#hello-world-bytes">hello-world-bytes</a>, mapped by the specified
     * value mapper and keyed by the specified key mapper, into the specified sequenced map.
     * <p>
     * For each byte index {@code i} in {@code [0, }{@value #BYTES}{@code )}, the entry
     * {@code (keyMapper.apply(i), valueMapper.apply(array[i]))} is put into the {@code map} in
     * encounter order.
     *
     * @param <T>         map type parameter
     * @param <K>         key type parameter
     * @param <V>         value type parameter
     * @param map         the sequenced map into which each entry is put.
     * @param keyMapper   the function applied to each byte, boxed as {@link Byte}, to produce the
     *                    key.
     * @param valueMapper the function applied to each byte, boxed as {@link Byte}, to produce the
     *                    value.
     * @return the given {@code map}.
     * @throws NullPointerException if any of {@code map}, {@code keyMapper}, or {@code valueMapper}
     *                              is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and, for each byte {@code b} in the array, appends
     * {@code (keyMapper.apply(b), valueMapper.apply(b))} to the end of the {@code map} via
     * {@link SequencedMap#putLast(Object, Object)}.
     * @see #set(byte[])
     * @see Function#apply(Object)
     * @see SequencedMap#putLast(Object, Object)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/SequencedMap.html">java.util.SequencedMap</a>
     */
    default <T extends SequencedMap<? super K, ? super V>, K, V> T put(
            final T map,
            final Function<? super Byte, ? extends K> keyMapper,
            final Function<? super Byte, ? extends V> valueMapper) {
        Objects.requireNonNull(map, "map is null");
        Objects.requireNonNull(keyMapper, "keyMapper is null");
        Objects.requireNonNull(valueMapper, "valueMapper is null");
        final var array = new byte[BYTES];
        set(array);
        for (final var b : array) {
            map.putLast(keyMapper.apply(b), valueMapper.apply(b));
        }
        return map;
    }

    // -------------------------------------------------------------------------- java.util.function

    /**
     * Accepts a value, mapped from each of the <a href="#hello-world-bytes">hello-world-bytes</a>
     * by the specified mapper, to the specified consumer.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(consumer, "consumer is null");
     * Objects.requireNonNull(mapper, "mapper is null");
     * final var array = new byte[BYTES];
     * set(array);
     * for (final var b : array) { // @highlight region
     *     consumer.accept(mapper.apply(b));
     * } // @end
     * return consumer;
     *}
     *
     * @param <T>      consumer type parameter
     * @param <U>      mapped value type parameter
     * @param consumer the consumer to which each mapped value is accepted.
     * @param mapper   the mapper for mapping each byte, boxed as {@link Byte}, to a value of type
     *                 {@code U}.
     * @return the given {@code consumer}.
     * @throws NullPointerException if either {@code consumer} or {@code mapper} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and, for each byte in the array,
     * {@link Consumer#accept(Object) accepts} the result of
     * {@link Function#apply(Object) mapper.apply(b)} to the {@code consumer}.
     * @see #set(byte[])
     * @see Function#apply(Object)
     * @see Consumer#accept(Object)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/function/Consumer.html">java.util.function.Consumer</a>
     */
    default <T extends Consumer<? super U>, U>
    T accept(final T consumer, final Function<? super Byte, ? extends U> mapper) {
        Objects.requireNonNull(consumer, "consumer is null");
        Objects.requireNonNull(mapper, "mapper is null");
        final var array = new byte[BYTES];
        set(array);
        for (final var b : array) {
            consumer.accept(mapper.apply(b));
        }
        return consumer;
    }

    // undocumented, yet
    default <T extends IntConsumer> T accept(final T consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        accept(consumer::accept, Byte::intValue);
        return consumer;
    }

    // ------------------------------------------------------------------------------- java.util.jar

    // ---------------------------------------------------------------------------- java.util.stream
    // undocumented, yet
    default <T extends Stream.Builder<? super U>, U>
    T all(final T builder, final Function<? super Byte, ? extends U> mapper) {
        Objects.requireNonNull(builder, "builder is null");
        Objects.requireNonNull(mapper, "mapper is null");
        accept(builder, mapper);
        return builder;
    }

    // undocumented, yet
    default <T extends IntStream.Builder> T add(final T builder) {
        Objects.requireNonNull(builder, "builder is null");
        accept(builder::add);
        return builder;
    }

    // ------------------------------------------------------------------------------- java.util.zip

    /**
     * Updates the specified checksum with the <a href="#hello-world-bytes">hello-world-bytes</a>.
     *
     * @param <T>      checksum type parameter
     * @param checksum the checksum to be updated.
     * @return the given {@code checksum}.
     * @throws NullPointerException if {@code checksum} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, {@link Checksum#update(byte[]) updates} the {@code checksum} with
     * the array, and returns the {@code checksum}.
     * @see #set(byte[])
     * @see Checksum#update(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/zip/Checksum.html">java.util.zip.Checksum</a>
     */
    default <T extends Checksum> T update(final T checksum) {
        Objects.requireNonNull(checksum, "checksum is null");
        final var array = new byte[BYTES];
        set(array);
        checksum.update(array);
        return checksum;
    }

    /**
     * Sets, as an input data for compression, the <a
     * href="#hello-world-bytes">hello-world-bytes</a> to the specified deflater.
     *
     * @param <T>      deflater type parameter
     * @param deflater the deflater to which the input data is set.
     * @return the given {@code deflater}.
     * @throws NullPointerException if {@code deflater} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, {@link Deflater#setInput(byte[]) sets} the array as the input data
     * of the {@code deflater}, and returns the {@code deflater}.
     * @see #set(byte[])
     * @see Deflater#setInput(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/zip/Deflater.html">java.util.zip.Deflater</a>
     */
    default <T extends Deflater> T setInput(final T deflater) {
        Objects.requireNonNull(deflater, "deflater is null");
        final var array = new byte[BYTES];
        set(array);
        deflater.setInput(array);
        return deflater;
    }

    // -------------------------------------------------------------------------------- javax.crypto

    /**
     * Updates the specified cipher with the <a href="#hello-world-bytes">hello-world-bytes</a>,
     * writing the result into the specified output array starting at the specified offset, and
     * accepts the number of bytes stored to the specified consumer.
     *
     * @param <T>      cipher type parameter
     * @param cipher   the cipher to be updated.
     * @param output   the output array into which the result is written.
     * @param offset   the offset in {@code output} at which the result is stored.
     * @param consumer the consumer to accept the number of bytes stored in {@code output}.
     * @return the given {@code cipher}.
     * @throws NullPointerException if either {@code cipher} or {@code consumer} is {@code null}.
     * @throws ShortBufferException if {@code output} is too small to hold the result.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, invokes
     * {@link Cipher#update(byte[], int, int, byte[], int) cipher.update(array, 0, BYTES, output,
     * outputOffset)} method on the {@code cipher}, and {@link IntConsumer#accept(int) accepts} the
     * result to the {@code consumer}.
     * @see #set(byte[])
     * @see Cipher#update(byte[], int, int, byte[], int)
     * @see IntConsumer#accept(int)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
     */
    default <T extends Cipher> T update(final T cipher, final byte[] output, final int offset,
                                        final IntConsumer consumer)
            throws ShortBufferException {
        Objects.requireNonNull(cipher, "cipher is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var array = new byte[BYTES];
        set(array);
        final var result = cipher.update(array, 0, BYTES, output, offset);
        consumer.accept(result);
        return cipher;
    }

    /**
     * Updates the specified cipher with the <a href="#hello-world-bytes">hello-world-bytes</a>,
     * writing the result into the specified output buffer, and accepts the number of bytes stored
     * to the specified consumer.
     *
     * @param <T>      cipher type parameter
     * @param cipher   the cipher to be updated.
     * @param output   the output buffer into which the result is written.
     * @param consumer the consumer to accept the number of bytes stored in {@code output}.
     * @return the given {@code cipher}.
     * @throws NullPointerException if either {@code cipher} or {@code consumer} is {@code null}.
     * @throws ShortBufferException if {@code output} does not have enough remaining bytes to hold
     *                              the result.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer) put(buffer)} method with a
     * byte buffer of {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, invokes
     * {@link Cipher#update(ByteBuffer, ByteBuffer) cipher.update(buffer, output)} method on the
     * {@code cipher}, and {@link IntConsumer#accept(int) accepts} the result to the
     * {@code consumer}.
     * @see #put(ByteBuffer)
     * @see Cipher#update(ByteBuffer, ByteBuffer)
     * @see IntConsumer#accept(int)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
     */
    default <T extends Cipher> T update(final T cipher, final ByteBuffer output,
                                        final IntConsumer consumer)
            throws ShortBufferException {
        Objects.requireNonNull(cipher, "cipher is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        final var result = cipher.update(buffer, output);
        consumer.accept(result);
        return cipher;
    }

    /**
     * Updates the AAD (Additional Authenticated Data) of the specified cipher with the
     * <a href="#hello-world-bytes">hello-world-bytes</a>.
     *
     * @param <T>    cipher type parameter
     * @param cipher the cipher whose AAD is to be updated.
     * @return the given {@code cipher}.
     * @throws NullPointerException if {@code cipher} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and invokes {@link Cipher#updateAAD(byte[])} method, on the
     * {@code cipher}, with the array.
     * @see #set(byte[])
     * @see Cipher#updateAAD(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
     */
    default <T extends Cipher> T updateAAD(final T cipher) {
        Objects.requireNonNull(cipher, "cipher is null");
        final var array = new byte[BYTES];
        set(array);
        cipher.updateAAD(array);
        return cipher;
    }

    /**
     * Updates the specified MAC with the <a href="#hello-world-bytes">hello-world-bytes</a>.
     *
     * @param <T> MAC type parameter
     * @param mac the MAC to be updated.
     * @return the given {@code mac}.
     * @throws NullPointerException if {@code mac} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and invokes {@link Mac#update(byte[])} method, on the {@code mac},
     * with the array.
     * @see #set(byte[])
     * @see Mac#update(byte[])
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
     */
    default <T extends Mac> T update(final T mac) {
        Objects.requireNonNull(mac, "mac is null");
        final var array = new byte[BYTES];
        set(array);
        mac.update(array);
        return mac;
    }
}
