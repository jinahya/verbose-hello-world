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

import javax.crypto.Cipher;
import javax.crypto.Mac;
import java.io.ByteArrayInputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.net.URLConnection;
import java.net.http.HttpRequest;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.BitSet;
import java.util.Objects;
import java.util.SequencedCollection;
import java.util.SequencedMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.jar.JarOutputStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.Checksum;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        "java:S4274", // assert ...
        "UnicodeInCode" // https://errorprone.info/bugpattern/UnicodeInCode
})
public interface HelloWorld {

    // ----------------------------------------------------------------------------------- constants

    /**
     * The length of the <a href="#hello-world-bytes">hello-world-bytes</a> which is {@value}.
     *
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-9.html#jls-9.3">9.3.
     * Field (Constant) Declarations</a> (The Java® Language Specification)
     */
    /* public static final */
    int BYTES = 12;

    // ----------------------------------------------------------------------------------- java.lang

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> on the specified array starting
     * at the specified index.
     * <p>
     * The elements in the array, on successful return, will be set as follows.
     * <pre>
     *  0  &lt;= index          index + 12  &lt;=   array.length
     *  ↓     ↓                       ↓       ↓
     * | |...|h|e|l|l|o|,| |w|o|r|l|d| |...| |
     * </pre>
     *
     * @param array the array on which bytes are set.
     * @param index the starting index of the {@code array} to which bytes are set.
     * @return the given {@code array}.
     * @throws NullPointerException      if {@code array} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code index} is negative, or ({@code index} plus
     *                                   {@value #BYTES}) is greater than {@code array.length}.
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-9.html#jls-9.4">9.4.
     * Method Declarations </a> (The Java® Language Specification)
     * @see <a href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-10.html#jls-10.4">10.4.
     * Array Access</a> (Java Language Specification)
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
     *                           &lt;= array.length
     * </pre>
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
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
//        if (array == null) {
//            throw new NullPointerException("array is null");
//        }
//        if (array.length < BYTES) {
//            throw new IndexOutOfBoundsException("array.length(" + array.length + ") < " + BYTES);
//        }
//        set(array, 0);
//        return array;
        return null;
    }

    /**
     * Returns an array of {@value #BYTES} bytes on which the <a
     * href="#hello-world-bytes">hello-world-bytes</a> are set.
     * <p>
     * The result array, on successful return, will be set as follows.
     * <pre>
     *  0                       12
     *  ↓                       ↓
     * |h|e|l|l|o|,| |w|o|r|l|d|
     * </pre>
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * final var array = new byte[BYTES];
     * set(array);
     * return array;
     *}
     *
     * @return an array of {@value #BYTES} bytes containing the <a
     * href="#hello-world-bytes">hello-world-bytes</a>.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and returns the result.
     */
    default byte[] byteArray() {
        return set(new byte[BYTES]);
    }

    default String string() {
        return new String(byteArray(), StandardCharsets.US_ASCII);
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
     */
    default <T extends Appendable> T append(final T appendable) throws IOException {
        if (appendable == null) {
            throw new NullPointerException("appendable is null");
        }
//        final var array = new byte[BYTES];
//        set(array);
        for (final var b : set(new byte[BYTES])) {
            appendable.append((char) b);
        }
        return appendable;
    }

    // --------------------------------------------------------------------------- java.lang.foreign

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> on the specified memory segment
     * starting at offset {@code 0}.
     * <p>
     * The memory segment must have at least {@value #BYTES} bytes available.
     * <pre>
     *  0                       12    &lt;=   segment.byteSize()
     *  ↓                       ↓         ↓
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
     * @param segment the memory segment on which bytes are set.
     * @return the given {@code segment}.
     * @throws NullPointerException      if {@code segment} is {@code null}.
     * @throws IndexOutOfBoundsException if {@code segment.byteSize()} is less than
     *                                   {@value #BYTES}.
     * @apiNote Callers can use {@link MemorySegment#asSlice(long)} to set at a specific offset.
     * @see MemorySegment#asSlice(long)
     * @see #set(byte[])
     * @see MemorySegment#copy(Object, int, MemorySegment, ValueLayout, long, int)
     */
    default <T extends MemorySegment> T copy(final T segment) {
        Objects.requireNonNull(segment, "segment is null");
        if (segment.byteSize() < BYTES) {
            throw new IndexOutOfBoundsException(
                    "byteSize(" + segment.byteSize() + ") < BYTES(" + BYTES + ")"
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
                array.length
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
     */
    default <T extends OutputStream> T write(final T stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        final var array = new byte[BYTES];
        set(new byte[BYTES]);
//        stream.write(array);
        return stream;
    }

    default InputStream inputStream() {
        return new ByteArrayInputStream(byteArray());
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
     */
    default <T extends Writer> T write(final T writer) throws IOException {
        if (writer == null) {
            throw new NullPointerException("writer is null");
        }
//        append(writer);
        return writer;
    }

    default Reader reader() {
        return new InputStreamReader(inputStream(), StandardCharsets.US_ASCII);
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
     * @param file    the file to append to
     * @param charset the character set to use for encoding
     * @return the given {@code file}.
     * @throws NullPointerException if {@code file} is {@code null}.
     * @throws NullPointerException if {@code charset} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation creates a new {@link FileWriter} with {@code file},
     * {@code charset}, and {@code true} for
     * {@link FileWriter#FileWriter(File, Charset, boolean) appending mode}, invokes the
     * {@link #write(Writer) write(writer)} method with it, {@link Writer#flush() flushes} and
     * {@link Writer#close() closes} the writer, and returns {@code file}.
     * @see FileWriter#FileWriter(File, Charset, boolean)
     * @see #write(Writer)
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

    default InputStream asInputStream() throws IOException {
        return new ByteArrayInputStream(byteArray());
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
     * packet:       0     &le; offset    &le; offset + length
     *               ↓       ↓           ↓
     * packet.data: | | | | |.|.|.|.|.|.| | | | | | | | | | | | | | | | |
     *
     * Then, on successful return,
     *
     * packet:       0     &le; offset                            &lt; offset + length'
     *               ↓       ↓                                   ↓
     * packet.data: | | | | |.|.|.|.|.|.|h|e|l|l|o|,| |w|o|r|l|d| | | | |
     *                                                (length' = length + 12)
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
     */
    default DatagramPacket append(final DatagramPacket packet) {
        if (packet == null) {
            throw new NullPointerException("packet is null");
        }
        final var data = packet.getData();
        final var offset = packet.getOffset(); // the offset in the data
        final var length = packet.getLength(); // the number of bytes to send from the offset
        if (offset + length + BYTES > data.length) {
            throw new IllegalArgumentException("packet.data is not enough");
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
     * {@link DatagramSocket#isConnected() connected } datagram socket.
     *
     * @param <T>    socket type parameter
     * @param socket the socket through which bytes are sent.
     * @return the given {@code socket}.
     * @throws NullPointerException     if {@code socket} is {@code null}.
     * @throws IllegalArgumentException if the {@code socket} is not
     *                                  {@link DatagramSocket#isConnected() connected}.
     * @throws IOException              if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #append(DatagramPacket) set(packet)} with a
     * datagram packet of {@value #BYTES}-long data array, and
     * {@link DatagramSocket#send(DatagramPacket) sends} the packet through the {@code socket}.
     * @see #append(DatagramPacket)
     * @see DatagramSocket#send(DatagramPacket)
     */
    default <T extends DatagramSocket> T send(final T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("not connected; " + socket);
        }
        final var target = socket.getRemoteSocketAddress();
        assert target != null;
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
     */
    default <T extends Socket> T send(final T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
//        final var stream = socket.getOutputStream();
//        write(stream);
        return socket;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> through the specified url
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
     * @param <T>        url connection type parameter
     * @param connection the url connection through which bytes are sent.
     * @return the given {@code socket}.
     * @throws NullPointerException if {@code socket} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #write(OutputStream)} method with
     * {@link URLConnection#getOutputStream() connection.outputStream}, and returns the
     * {@code connection}.
     * @implNote Note that this method does not {@link Flushable#flush() flush} the
     * {@code connection}'s output stream.
     * @see URLConnection#getOutputStream()
     * @see #write(OutputStream)
     */
    default <T extends URLConnection> T write(final T connection) throws IOException {
        if (connection == null) {
            throw new NullPointerException("connection is null");
        }
        write(connection.getOutputStream());
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
//    @Deprecated(forRemoval = true)
//    @屋上架屋("SSLSocket extends Socket")
//    @SuppressWarnings({"unchecked"})
//    default <T extends SSLSocket> T send(final T socket) throws IOException {
//        return (T) send((Socket) socket);
//    }

    // ------------------------------------------------------------------------------------ java.nio

    /**
     * Puts the <a href="#hello-world-bytes">hello-world-bytes</a> on the specified byte buffer.
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
     *                               (21)
     *
     * Then, on successful return,
     *
     *                                 16                25            32
     *  0                     &lt;= position          &lt;= limit   &lt;= capacity
     *  ↓                               ↓                 ↓             ↓
     * | | | | |h|e|l|l|o|,| |w|o|r|l|d| | | | | | | | | | | | | | | | |
     *                                 |--- remaining ---|
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
     */
    default <T extends ByteBuffer> T put(final T buffer) {
        if (Objects.requireNonNull(buffer, "buffer is null").remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        if (buffer.hasArray()) {
//            final var array = buffer.array();
//            final var index = buffer.arrayOffset() + buffer.position();
//            set(array, index);
//            buffer.position(buffer.position() + BYTES);
        } else {
//            final var array = new byte[BYTES];
//            set(array);
//            buffer.put(array);
        }
        return buffer;
    }

    @SuppressWarnings({"unchecked"})
    default <T extends ByteBuffer> T byteBuffer(final Supplier<? extends T> supplier) {
        Objects.requireNonNull(supplier, "supplier is null");
        return (T) put(Objects.requireNonNull(supplier.get(), "supplier.get() is null")).flip();
    }

    default ByteBuffer byteBuffer() {
        return byteBuffer(() -> ByteBuffer.allocate(BYTES));
    }

    /**
     * Returns a byte buffer of {@value #BYTES} bytes, containing the <a
     * href="#hello-world-bytes">hello-world-bytes</a>, whose {@code position}, {@code limit} is
     * equal to the {@code capacity}.
     * <p>
     * The result buffer's state, on successful return, is as follows.
     * <pre>
     *  0                       12
     *                          position = limit = capacity
     *  ↓                       ↓
     * |h|e|l|l|o|,| |w|o|r|l|d|
     *                         |
     *                         remaining(0)
     * </pre>
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * var buffer = ByteBuffer.allocate(BYTES);
     * put(buffer);
     * return buffer;
     *}
     *
     * @return a byte buffer ready to be drained.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer)} with a byte buffer of
     * {@value #BYTES}, and returns the byte buffer.
     * @apiNote The returned buffer has no remaining. Callers should {@link ByteBuffer#flip() flip}
     * the buffer before reading from it.
     * @see #put(ByteBuffer)
     * @see ByteBuffer#flip()
     */
    default ByteBuffer put() {
        return ByteBuffer.wrap(byteArray());
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
     * @param channel the channel to which bytes are written.
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
     */
    default <T extends WritableByteChannel> T write(final T channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
//        final var buffer = ByteBuffer.allocate(BYTES);
//        put(buffer);
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
     */
    @SuppressWarnings({"unchecked"})
    default <T extends DatagramChannel> T send(final T channel, final SocketAddress target)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(target, "target is null");
        if (channel.isBlocking()) {
            return (T) send(channel.socket(), target).getChannel();
        }
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
//        while (channel.send(buffer, target) == 0) {
//            {
//                final var sndbuf = channel.getOption(StandardSocketOptions.SO_SNDBUF);
//                if (sndbuf == null || sndbuf < BYTES) {
//                    throw new IOException("channel.SNDBUF is not enough: " + sndbuf);
//                }
//            }
//            Thread.onSpinWait();
//        }
        return channel;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified connected
     * datagram channel.
     *
     * @param <T>     channel type parameter
     * @param channel the connected datagram channel to which bytes are written.
     * @return the given {@code channel}.
     * @throws NullPointerException     if {@code channel} is {@code null}.
     * @throws IllegalArgumentException if the {@code channel} is not
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
     */
    @SuppressWarnings({"unchecked"})
    default <T extends DatagramChannel> T write(final T channel) throws IOException {
        if (!Objects.requireNonNull(channel, "channel is null").isConnected()) {
            throw new IllegalArgumentException("not connected: " + channel);
        }
        if (channel.isBlocking()) {
            return (T) send(channel.socket()).getChannel();
        }
        return (T) write((WritableByteChannel) channel);
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return the given {@code channel}.
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
     */
    default <T extends AsynchronousByteChannel> T write(final T channel)
            throws InterruptedException, ExecutionException {
        Objects.requireNonNull(channel, "channel is null");
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
//        while (buffer.hasRemaining()) {
//            final var future = channel.write(buffer);
//            final var written = future.get();
//            assert written > 0;
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
     * @throws InterruptedException if interrupted while executing.
     * @throws ExecutionException   if failed to write bytes.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer) put(buffer)} with a byte
     * buffer of {@value #BYTES} bytes, flips it, and writes the {@code buffer} to {@code channel},
     * while the {@code buffer} {@link ByteBuffer#hasRemaining() has remaining}, by continuously
     * invoking
     * {@link AsynchronousFileChannel#write(ByteBuffer, long) channel.write(buffer, position)}
     * method with the {@code buffer} and {@code position} adjusted by the previous result.
     * @see #put(ByteBuffer)
     * @see AsynchronousFileChannel#write(ByteBuffer, long)
     */
    default <T extends AsynchronousFileChannel> T write(final T channel, long position)
            throws InterruptedException, ExecutionException {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        while (buffer.hasRemaining()) {
            final var future = channel.write(buffer, position);
            final var written = future.get();
            assert written > 0; // why?
            position += written;
        }
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
     *     channel.force(true);
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
     * {@link FileChannel#force(boolean) forces channel including metadata},
     * {@link WritableByteChannel#close() closes} the channel, and returns the {@code path}.
     * @see FileChannel#open(Path, OpenOption...)
     * @see StandardOpenOption#CREATE
     * @see StandardOpenOption#APPEND
     * @see #write(WritableByteChannel)
     * @see FileChannel#force(boolean)
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-14.html#jls-14.20.3">14.20.3.
     * try-with-resources</a> (The Java® Language Specification)
     */
    default <T extends Path> T append(final T path) throws IOException {
        Objects.requireNonNull(path, "path is null");
        final var options = new OpenOption[] {
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        };
        try (var channel = FileChannel.open(path, options)) {
            write((WritableByteChannel) channel);
            channel.force(true);
        }
        return path;
    }

    // ------------------------------------------------------------------------------- java.security

    /**
     * Updates the specified message digest with the <a
     * href="#hello-world-bytes">hello-world-bytes</a>.
     *
     * @param digest the message digest to be updated.
     * @param <T>    message digest type parameter
     * @return the given {@code digest}.
     * @throws NullPointerException if {@code digest} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, {@link MessageDigest#update(byte[]) updates} the {@code digest}
     * with the array, and returns the {@code digest}.
     * @see #set(byte[])
     * @see MessageDigest#update(byte[])
     */
    default <T extends MessageDigest> T update(final T digest) {
        Objects.requireNonNull(digest, "digest is null");
        final var array = new byte[BYTES];
        set(array);
//        digest.update(array);
        return digest;
    }

    /**
     * Updates the specified signature with the <a href="#hello-world-bytes">hello-world-bytes</a>.
     *
     * @param signature the signature to be updated.
     * @param <T>       signature type parameter
     * @return the given {@code signature}.
     * @throws NullPointerException if {@code signature} is {@code null}.
     * @throws SignatureException   if the signature is not initialized properly.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and {@link Signature#update(byte[]) updates} the {@code signature}
     * with the array.
     * @see #set(byte[])
     * @see Signature#update(byte[])
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
     *                                  marker in the SQL statement; if a database access error
     *                                  occurs; or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, wraps the array in a {@link ByteArrayInputStream}, invokes
     * {@link PreparedStatement#setAsciiStream(int, InputStream) setAsciiStream(parameterIndex,
     * stream)} method on the {@code preparedStatement} with the {@code parameterIndex} and the
     * stream, {@link InputStream#close() closes} the stream, and returns the
     * {@code preparedStatement}.
     * @see #set(byte[])
     * @see PreparedStatement#setAsciiStream(int, InputStream)
     */
    default <T extends PreparedStatement> T setAsciiStream(final T preparedStatement,
                                                           final int parameterIndex)
            throws IOException, SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var buf = new byte[BYTES];
        set(buf);
        try (var x = new ByteArrayInputStream(buf)) {
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
     *                                  marker in the SQL statement; if a database access error
     *                                  occurs; or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, wraps the array in a {@link ByteArrayInputStream}, invokes
     * {@link PreparedStatement#setBinaryStream(int, InputStream) setBinaryStream(parameterIndex,
     * stream)} method on the {@code preparedStatement} with the {@code parameterIndex} and the
     * stream, {@link InputStream#close() closes} the stream, and returns the
     * {@code preparedStatement}.
     * @see #set(byte[])
     * @see PreparedStatement#setBinaryStream(int, InputStream)
     */
    default <T extends PreparedStatement> T setBinaryStream(final T preparedStatement,
                                                            final int parameterIndex)
            throws IOException, SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var buf = new byte[BYTES];
        set(buf);
        try (var x = new ByteArrayInputStream(buf)) {
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
     *                                  marker in the SQL statement; if a database access error
     *                                  occurs; or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, invokes
     * {@link PreparedStatement#setBytes(int, byte[]) setBytes(parameterIndex, array)} method on the
     * {@code preparedStatement} with the {@code parameterIndex} and the array, and returns the
     * {@code preparedStatement}.
     * @see #set(byte[])
     * @see PreparedStatement#setBytes(int, byte[])
     */
    default <T extends PreparedStatement> T setBytes(final T preparedStatement,
                                                     final int parameterIndex)
            throws SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var x = new byte[BYTES];
        set(x);
        preparedStatement.setBytes(parameterIndex, x);
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
     *                                  marker in the SQL statement; if a database access error
     *                                  occurs; or if this method is called on a closed
     *                                  {@link PreparedStatement}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, wraps the array in an {@link InputStreamReader} decoded as
     * {@link StandardCharsets#US_ASCII US_ASCII}, invokes
     * {@link PreparedStatement#setCharacterStream(int, Reader) setCharacterStream(parameterIndex,
     * reader)} method on the {@code preparedStatement} with the {@code parameterIndex} and the
     * reader, {@link Reader#close() closes} the reader, and returns the {@code preparedStatement}.
     * @see #set(byte[])
     * @see PreparedStatement#setCharacterStream(int, Reader)
     */
    default <T extends PreparedStatement> T setCharacterStream(final T preparedStatement,
                                                               final int parameterIndex)
            throws IOException, SQLException {
        Objects.requireNonNull(preparedStatement, "preparedStatement is null");
        if (parameterIndex < 1) {
            throw new IllegalArgumentException("non-positive parameterIndex: " + parameterIndex);
        }
        final var buf = new byte[BYTES];
        set(buf);
        try (var reader = new InputStreamReader(new ByteArrayInputStream(buf),
                                                StandardCharsets.US_ASCII)) {
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
     */
    default <T extends Blob> T setBytes(final T blob, final long pos) throws SQLException {
        Objects.requireNonNull(blob, "blob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        final var bytes = new byte[BYTES];
        set(bytes);
        blob.setBytes(pos, bytes);
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
     */
    default <T extends Clob> T setString(final T clob, final long pos) throws SQLException {
        Objects.requireNonNull(clob, "clob is null");
        if (pos < 1L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        final var array = new byte[BYTES];
        set(array);
        final var str = new String(array, StandardCharsets.UTF_8);
        clob.setString(pos, str);
        return clob;
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
     * @param bitset the bit set into which the bits are set.
     * @param index  the starting index in the bit set.
     * @param <T>    bit set type parameter
     * @return the given {@code bitset}.
     * @throws NullPointerException     when the {@code bitset} is {@code null}.
     * @throws IllegalArgumentException when the {@code index} is negative.
     * @implSpec The default implementation invokes the {@link #set(byte[]) set(array)} method, and
     * sets each bit of the result into the given {@code bitset} in little-endian bit order.
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

//    /**
//     * Collects each of the <a href="#hello-world-bytes">hello-world-bytes</a>, boxed as
//     * {@link Byte}, into the specified collection.
//     *
//     * @param <T>        collection type parameter
//     * @param collection the collection into which each byte is collected.
//     * @return the given {@code collection}.
//     * @throws NullPointerException if {@code collection} is {@code null}.
//     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
//     * of {@value #BYTES} bytes, and {@link Collection#add(Object) adds} each byte in the array,
//     * boxed as {@link Byte}, to the {@code collection}.
//     * @see #set(byte[])
//     * @see Collection#add(Object)
//     */
//    default <T extends Collection<? super Byte>> T collect(final T collection) {
//        Objects.requireNonNull(collection, "collection is null");
//        final var array = new byte[BYTES];
//        set(array);
//        for (final var b : array) {
//            collection.add(b);
//        }
//        return collection;
//    }

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
     * {@link Byte}, and {@link SequencedCollection#add(Object) adds} the result to the
     * {@code collection}.
     * @see #set(byte[])
     * @see Function#apply(Object)
     * @see SequencedCollection#add(Object)
     */
    default <T extends SequencedCollection<? super U>, U>
    T add(final T collection, final Function<? super Byte, ? extends U> mapper) {
        Objects.requireNonNull(collection, "collection is null");
        Objects.requireNonNull(mapper, "mapper is null");
        final var array = new byte[BYTES];
        set(array);
        for (final var b : array) {
            collection.add(mapper.apply(b));
        }
        return collection;
    }

    /**
     * Collects each of the <a href="#hello-world-bytes">hello-world-bytes</a>, mapped by the
     * specified mapper, into the specified collection.
     * <p>
     * Each byte is applied to the {@code mapper} as an {@code int}. Because every
     * <a href="#hello-world-bytes">hello-world-byte</a> is non-negative (within
     * {@code [0x20..0x77]}), the widening conversion preserves the byte's numeric value with no
     * masking required.
     *
     * @param <T>        collection type parameter
     * @param <R>        element type parameter
     * @param collection the collection into which each mapped value is collected.
     * @param mapper     the function applied to each byte, widened to an {@code int}, to produce
     *                   the value to be collected.
     * @return the given {@code collection}.
     * @throws NullPointerException if either {@code collection} or {@code mapper} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, applies the {@code mapper} to each byte in the array, widened to an
     * {@code int}, and {@link SequencedCollection#add(Object) adds} the result to the
     * {@code collection}.
     * @see #set(byte[])
     * @see IntFunction#apply(int)
     * @see SequencedCollection#add(Object)
     */
    default <T extends SequencedCollection<? super R>, R>
    T add(final T collection, final IntFunction<? extends R> mapper) {
        Objects.requireNonNull(collection, "collection is null");
        Objects.requireNonNull(mapper, "mapper is null");
        final var array = new byte[BYTES];
        set(array);
        for (final var b : array) {
//            collection.add(mapper.apply(b & 0xFF));
            collection.add(mapper.apply(b));
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
     * @param keyMapper   the function applied to each byte index, in
     *                    {@code [0, }{@value #BYTES}{@code )}, to produce the key.
     * @param valueMapper the function applied to each byte, boxed as {@link Byte}, to produce the
     *                    value.
     * @return the given {@code map}.
     * @throws NullPointerException if any of {@code map}, {@code keyMapper}, or {@code valueMapper}
     *                              is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and, for each byte index {@code i} in
     * {@code [0, }{@value #BYTES}{@code )}, {@link SequencedMap#put(Object, Object) puts}
     * {@code (keyMapper.apply(i), valueMapper.apply(array[i]))} into the {@code map}.
     * @see #set(byte[])
     * @see IntFunction#apply(int)
     * @see Function#apply(Object)
     * @see SequencedMap#put(Object, Object)
     */
    default <T extends SequencedMap<? super K, ? super V>, K, V> T put(
            final T map,
            final IntFunction<? extends K> keyMapper,
            final Function<? super Byte, ? extends V> valueMapper) {
        Objects.requireNonNull(map, "map is null");
        Objects.requireNonNull(keyMapper, "keyMapper is null");
        Objects.requireNonNull(valueMapper, "valueMapper is null");
        final var array = new byte[BYTES];
        set(array);
        for (var i = 0; i < array.length; i++) {
            map.put(keyMapper.apply(i), valueMapper.apply(array[i]));
        }
        return map;
    }

    // -------------------------------------------------------------------------- java.util.function

    /**
     * Accepts each of the <a href="#hello-world-bytes">hello-world-bytes</a>, boxed as
     * {@link Byte}, to the specified consumer.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(consumer, "consumer is null");
     * final var array = new byte[BYTES];
     * set(array);
     * for (final var b : array) { // @highlight region
     *     consumer.accept(b);
     * } // @end
     * return consumer;
     *}
     *
     * @param <T>      consumer type parameter
     * @param consumer the consumer to which each byte is accepted.
     * @return the given {@code consumer}.
     * @throws NullPointerException if {@code consumer} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and {@link Consumer#accept(Object) accepts} each byte in the array,
     * boxed as {@link Byte}, to the {@code consumer}.
     * @see #set(byte[])
     * @see Consumer#accept(Object)
     */
    default <T extends Consumer<? super Byte>> T accept(final T consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        final var array = new byte[BYTES];
        set(array);
        for (final var b : array) {
            consumer.accept(b);
        }
        return consumer;
    }

    default <T extends IntConsumer> T accept(final T consumer) {
        Objects.requireNonNull(consumer, "consumer is null");
        final var array = new byte[BYTES];
        set(array);
        for (final var b : array) {
            consumer.accept(b);
        }
        return consumer;
    }

    // ------------------------------------------------------------------------------- java.util.jar
    @屋上架屋("JarOutputStream extends ZipOutputStream")
    @Deprecated(forRemoval = true)
    default <T extends JarOutputStream> T write(final T stream) throws IOException {
        final var result = write((ZipOutputStream) stream);
        assert result == stream;
        return stream;
    }

    @屋上架屋("JarOutputStream extends ZipOutputStream")
    @Deprecated(forRemoval = true)
    default <T extends JarOutputStream> T put(final T stream, final String name)
            throws IOException {
        final var result = put((ZipOutputStream) stream, name);
        assert result == stream;
        return stream;
    }

    // ------------------------------------------------------------------------------- java.util.zip
    default <T extends Checksum> T update(final T checksum) {
        Objects.requireNonNull(checksum, "checksum is null");
        final var array = new byte[BYTES];
        set(array);
        checksum.update(array);
        return checksum;
    }

    default <T extends Deflater> T input(final T deflater) {
        Objects.requireNonNull(deflater, "deflater is null");
        final var array = new byte[BYTES];
        set(array);
        deflater.setInput(array);
        return deflater;
    }

    /**
     * Puts an entry of the specified name, whose content is the <a
     * href="#hello-world-bytes">hello-world-bytes</a>, to the specified zip output stream.
     *
     * @param stream the zip output stream to which the entry is put.
     * @param name   the name of the entry to put.
     * @param <T>    zip output stream type parameter
     * @return given {@code stream}.
     * @throws IOException if an I/O error occurs.
     */
    default <T extends ZipOutputStream> T put(final T stream, final String name)
            throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        Objects.requireNonNull(name, "name is null");
        final var entry = new ZipEntry(name);
        stream.putNextEntry(entry);
        write(stream);
        stream.closeEntry();
        return stream;
    }

    // ---------------------------------------------------------------------------- java.util.stream

    /**
     * Accepts each of the <a href="#hello-world-bytes">hello-world-bytes</a>, boxed as
     * {@link Byte}, to the specified stream builder.
     *
     * @param <T>     stream builder type parameter
     * @param builder the stream builder to which each byte is accepted.
     * @return the given {@code builder}.
     * @throws NullPointerException if {@code builder} is {@code null}.
     * @implSpec Default implementation invokes {@link #accept(Consumer) accept(consumer)} method
     * with the {@code builder} cast as a {@link Consumer}, and returns the {@code builder}.
     * @see #accept(Consumer)
     * @see Stream.Builder
     * @deprecated Invoke {@link #accept(Consumer)} with the {@code builder}.
     */
    @屋上架屋("Stream.Builder<T> extends Consumer<T>")
    @Deprecated(forRemoval = true)
    default <T extends Stream.Builder<? super Byte>> T add(final T builder) {
        return (T) accept((Consumer<? super Byte>) builder);
    }

    @屋上架屋("IntStream.Builder extends IntConsumer")
    @Deprecated(forRemoval = true)
    default <T extends IntStream.Builder> T add(final T builder) {
        return (T) accept((IntConsumer) builder);
    }

    // -------------------------------------------------------------------------------- javax.crypto

    /**
     * Updates the specified cipher with the <a href="#hello-world-bytes">hello-world-bytes</a>, and
     * accepts the result to the specified consumer.
     *
     * @param cipher   the cipher to be updated.
     * @param consumer the consumer to accept the result of
     *                 {@link Cipher#update(byte[]) cipher.update(array)}.
     * @param <T>      cipher type parameter
     * @return the given {@code cipher}.
     * @throws NullPointerException if either {@code cipher} or {@code consumer} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, {@link Cipher#update(byte[]) updates} the {@code cipher} with the
     * array, and {@link Consumer#accept(Object) accepts} the result to the {@code consumer}.
     * @see #set(byte[])
     * @see Cipher#update(byte[])
     * @see Consumer#accept(Object)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
     */
    default <T extends Cipher> T update(final T cipher, final Consumer<? super byte[]> consumer) {
        Objects.requireNonNull(cipher, "cipher is null");
        Objects.requireNonNull(consumer, "consumer is null");
        final var array = new byte[BYTES];
        set(array);
        final var result = cipher.update(array);
        consumer.accept(result);
        return cipher;
    }

    /**
     * Updates the specified MAC with the <a href="#hello-world-bytes">hello-world-bytes</a>.
     *
     * @param mac the MAC to be updated.
     * @param <T> MAC type parameter
     * @return the given {@code mac}.
     * @throws NullPointerException if {@code mac} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and invokes {@link Mac#update(byte[])} method, on the {@code mac},
     * with the array.
     * @see #set(byte[])
     * @see Mac#update(byte[])
     */
    default <T extends Mac> T update(final T mac) {
        Objects.requireNonNull(mac, "mac is null");
        final var array = set(new byte[BYTES]);
        mac.update(array);
        return mac;
    }
}
