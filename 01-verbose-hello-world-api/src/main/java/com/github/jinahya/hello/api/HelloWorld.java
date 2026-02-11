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

import jakarta.validation.constraints.Positive;
import org.jspecify.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.net.http.HttpRequest;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.zip.Checksum;
import java.util.zip.Deflater;

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

    // ---------------------------------------------------------------------------------- log/logger

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
     *  0                       12    &lt;= array.length
     *  ↓                       ↓        ↓
     * |h|e|l|l|o|,| |w|o|r|l|d| |....| |
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
        if (array == null) {
//            throw new NullPointerException("array is null");
        }
        if (array.length < BYTES) {
//            throw new IndexOutOfBoundsException("array.length(" + array.length + ") < " + BYTES);
        }
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
    default byte[] set() {
        final var array = new byte[BYTES];
        set(array);
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
     */
    default <T extends Appendable> T append(final T appendable) throws IOException {
        if (appendable == null) {
            throw new NullPointerException("appendable is null");
        }
        final var array = new byte[BYTES];
        set(array);
        // append each byte in <array>, cast as <char>, to <appendable>
//        for (final var b : array) {
//            appendable.append((char) b);
//        }
        // return given <appendable>
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
     * {@link OutputStream#write(byte[])} method on {@code stream} with the array, and returns the
     * {@code stream}.
     * @see #set(byte[])
     * @see OutputStream#write(byte[])
     */
    default <T extends OutputStream> T write(final T stream) throws IOException {
        if (stream == null) {
            throw new NullPointerException("stream is null");
        }
        final var array = set(new byte[BYTES]);
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
     *     stream.flush();
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
     */
    default <T extends File> T append(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        // create a new <FileOutputStream> with <file> and <true>
//        try (var stream = new FileOutputStream(file, true)) { // appending mode
//            // invoke <write(stream)> method with it
////            final var result = write(stream);
////            assert result == stream;
//            // flush the <stream>
////            stream.flush();
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
        final var array = set(new byte[BYTES]);
//        output.write(array);
        return output;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified random access
     * file starting at its current file pointer.
     * <p>
     * The default implementation would be as follows.
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
     * @return the given {@code file}.
     * @throws NullPointerException if {@code file} argument is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #set(byte[])} method with an array of
     * {@value #BYTES} bytes, writes the array to the specified random access file by invoking
     * {@link RandomAccessFile#write(byte[])} method on {@code file} with the array, and returns the
     * {@code file}.
     * @see #set(byte[])
     * @see RandomAccessFile#write(byte[])
     */
    default <T extends RandomAccessFile> T write(final T file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file is null");
        }
        final var array = set(new byte[BYTES]);
//        file.write(array);
        return file;
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

    // ------------------------------------------------------------------------------------ java.net

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> in the specified datagram
     * packet.
     * <p>
     * If the packet's data buffer is at least {@value #BYTES} bytes long, the buffer is reused.
     * Otherwise, a new buffer is allocated and set on the packet. In either case, the packet's
     * offset is reset to {@code 0} and its length is set to {@value #BYTES}.
     *
     * @param packet the datagram packet in which bytes are set.
     * @return the given {@code packet}.
     * @throws NullPointerException if {@code packet} is {@code null}.
     * @implSpec Default implementation checks if the packet's data buffer has at least
     * {@value #BYTES} bytes. If so, it invokes {@link #set(byte[])} with the buffer and resets the
     * packet's offset and length via {@link DatagramPacket#setData(byte[], int, int)}. Otherwise,
     * it creates a new array of {@value #BYTES} bytes, invokes {@link #set(byte[])} with it, and
     * sets the array on the packet via {@link DatagramPacket#setData(byte[])}.
     * @see DatagramPacket#getData()
     * @see DatagramPacket#setData(byte[], int, int)
     * @see DatagramPacket#setData(byte[])
     */
    default DatagramPacket set(final DatagramPacket packet) {
        if (packet == null) {
            throw new NullPointerException("packet is null");
        }
        if (packet.getData().length >= BYTES) {
            set(packet.getData());
            packet.setData(packet.getData(), 0, BYTES);
        } else {
            final var array = new byte[BYTES];
            set(array);
            packet.setData(array);
        }
        return packet;
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified datagram
     * packet.
     * <p>
     * The bytes are written starting at ({@link DatagramPacket#getOffset() offset} +
     * {@link DatagramPacket#getLength() length}), and the packet's length is extended by
     * {@value #BYTES}.
     * <pre>
     * Given,
     *
     *          4                   14
     *  0    &lt;= offset           <= offset + length      &lt;= data.length
     *  ↓       ↓                   ↓                       ↓
     * | | | | |e|x|i|s|t|i|n|g|.|.| | | | | | | | | | | | |
     *         |---- length (10) --|
     *
     * Then, on successful return,
     *
     *          4                   14
     *  0    &lt;= offset           <= offset + length        &lt;= data.length
     *  ↓       ↓                   ↓                         ↓
     * | | | | |e|x|i|s|t|i|n|g|.|.|h|e|l|l|o|,| |w|o|r|l|d| |
     *         |--------------- length (22) --------------|
     * </pre>
     *
     * @param packet the datagram packet to which bytes are appended.
     * @return the given {@code packet}.
     * @throws NullPointerException    if {@code packet} is {@code null}.
     * @throws BufferOverflowException if the packet's data buffer does not have at least
     *                                 {@value #BYTES} bytes available after the current content.
     * @implSpec Default implementation {@link ByteBuffer#wrap(byte[], int, int) wraps} the packet's
     * data array as a {@link ByteBuffer} starting at ({@link DatagramPacket#getOffset() offset} +
     * {@link DatagramPacket#getLength() length}) with the remaining space as length, invokes
     * {@link #put(ByteBuffer) put(buffer)} (which throws {@link BufferOverflowException} if
     * insufficient space), and extends the packet's length by {@value #BYTES}.
     * @see DatagramPacket#getData()
     * @see DatagramPacket#getOffset()
     * @see DatagramPacket#getLength()
     * @see DatagramPacket#setLength(int)
     * @see ByteBuffer#wrap(byte[], int, int)
     * @see #put(ByteBuffer)
     */
    default DatagramPacket append(final DatagramPacket packet) {
        Objects.requireNonNull(packet, "packet is null");
        final ByteBuffer buffer;
        {
            final var array = packet.getData();
            final var offset = packet.getOffset() + packet.getLength();
            final var length = array.length - offset;
            buffer = ByteBuffer.wrap(array, offset, length);
        }
        put(buffer);
        packet.setLength(buffer.position() - packet.getOffset());
        return packet;
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
     * @implSpec Default implementation invokes {@link #set(DatagramPacket) set(packet)} with a
     * datagram packet of {@value #BYTES}-long data array, and
     * {@link DatagramSocket#send(DatagramPacket) sends} the packet through the {@code socket}.
     * @see #set(DatagramPacket)
     * @see DatagramSocket#send(DatagramPacket)
     */
    default <T extends DatagramSocket> T send(final T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        if (!socket.isConnected()) {
            throw new IllegalArgumentException("not connected; " + socket);
        }
        final var packet = new DatagramPacket(new byte[BYTES], BYTES);
        set(packet);
        socket.send(packet);
        return socket;
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
     * @implSpec Default implementation invokes {@link #set(DatagramPacket) set(packet)} with a
     * datagram packet of {@value #BYTES}-long data array with the {@code target} address, and
     * {@link DatagramSocket#send(DatagramPacket) sends} it through the {@code socket}.
     * @see #set(DatagramPacket)
     * @see DatagramSocket#send(DatagramPacket)
     */
    default <T extends DatagramSocket> T send(final T socket, final SocketAddress target)
            throws IOException {
        Objects.requireNonNull(socket, "socket is null");
        Objects.requireNonNull(target, "target is null");
        final var packet = new DatagramPacket(new byte[BYTES], BYTES, target);
        set(packet);
        socket.send(packet);
        return socket;
    }

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> through the specified multicast
     * socket.
     *
     * @param <T>    socket type parameter
     * @param socket the multicast socket through which bytes are sent; must be
     *               {@link MulticastSocket#isConnected() connected} to a multicast group address.
     * @return the given {@code socket}.
     * @throws NullPointerException     if {@code socket} is {@code null}.
     * @throws IllegalArgumentException if the {@code socket} is not
     *                                  {@link MulticastSocket#isConnected() connected}.
     * @throws IOException              if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #send(DatagramSocket)} with {@code socket}.
     * @deprecated Invoke {@link #send(DatagramSocket)} with the {@code socket}.
     */
    @屋上架屋("MulticastSocket extends DatagramSocket")
    @Deprecated(forRemoval = true)
    @SuppressWarnings("unchecked")
    default <T extends MulticastSocket> T send(final T socket) throws IOException {
        return (T) send((DatagramSocket) socket);
    }

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified target address
     * via the specified multicast socket.
     *
     * @param <T>    socket type parameter
     * @param socket the multicast socket through which bytes are sent.
     * @param target the target address to which bytes are sent.
     * @return the given {@code socket}.
     * @throws NullPointerException if {@code socket} is {@code null} or {@code target} is
     *                              {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #send(DatagramSocket, SocketAddress)} with
     * {@code socket} and {@code target}.
     * @deprecated Invoke {@link #send(DatagramSocket, SocketAddress)} with {@code socket} and
     * {@code target}.
     */
    @屋上架屋("MulticastSocket extends DatagramSocket")
    @Deprecated(forRemoval = true)
    @SuppressWarnings("unchecked")
    default <T extends MulticastSocket> T send(final T socket, final SocketAddress target)
            throws IOException {
        return (T) send((DatagramSocket) socket, target);
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
     * @see Socket#getOutputStream()
     * @see #write(OutputStream)
     */
    default <T extends Socket> T send(final T socket) throws IOException {
        if (socket == null) {
            throw new NullPointerException("socket is null");
        }
        final var stream = socket.getOutputStream();
//        write(stream);
        return socket;
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
     * {@link HttpRequest.Builder#method(String, HttpRequest.BodyPublisher) builder.method(method, publisher)}
     * with {@code method} and {@link HttpRequest.BodyPublishers#ofByteArray(byte[])
     * BodyPublishers.ofByteArray(array)}.
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
            final var array = buffer.array();
            final var index = buffer.arrayOffset() + buffer.position();
            set(array, index);
            buffer.position(buffer.position() + BYTES);
        } else {
            final var array = new byte[BYTES];
            set(array);
            buffer.put(array);
        }
        return buffer;
    }

    /**
     * Returns a byte buffer of {@value #BYTES} bytes, containing the <a
     * href="#hello-world-bytes">hello-world-bytes</a> which is ready to be drained.
     * <p>
     * The result buffer's state, on successful return, is as follows.
     * <pre>
     *  0                       12
     *  position                limit = capacity
     *  ↓                       ↓
     * |h|e|l|l|o|,| |w|o|r|l|d|
     * |------ remaining ------|
     *              (12)
     * </pre>
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * final var buffer = ByteBuffer.allocate(BYTES);
     * put(buffer);
     * buffer.flip();
     * return buffer;
     *}
     *
     * @return a byte buffer ready to be drained.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer)} with a byte buffer of
     * {@value #BYTES}, and returns the result as {@link ByteBuffer#flip() flipped}.
     * @see #put(ByteBuffer)
     */
    default ByteBuffer put() {
        final var buffer = put(ByteBuffer.allocate(BYTES));
        buffer.flip();
        return buffer;
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified channel.
     * <p>
     * The default implementation would be as follows.
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
        final var buffer = put(ByteBuffer.allocate(BYTES));
        buffer.flip();
        assert buffer.remaining() == BYTES;
        while (buffer.hasRemaining()) {
            final var written = channel.write(buffer);
            assert written >= 0; // why
        }
        return channel;
    }

    // --------------------------------------------------------------------------- java.nio.channels

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified target address
     * via the specified datagram channel.
     *
     * @param <T>     channel type parameter
     * @param channel the datagram channel through which the bytes are sent.
     * @param target  the address to which the bytes are sent.
     * @return the given {@code channel}.
     * @throws NullPointerException if {@code channel} is {@code null} or {@code target} is
     *                              {@code null}.
     * @throws IOException          if an I/O error occurs, or if the datagram was not sent due to
     *                              the OS's send buffer being full.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer) put(buffer)} method with a
     * byte buffer of {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, and
     * {@link DatagramChannel#send(ByteBuffer, SocketAddress) sends} the buffer to {@code target}
     * via the {@code channel}.
     * @see #put(ByteBuffer)
     * @see DatagramChannel#send(ByteBuffer, SocketAddress)
     */
    default <T extends DatagramChannel> T send(final T channel, final SocketAddress target)
            throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        {
            final var sndbuf = channel.getOption(StandardSocketOptions.SO_SNDBUF);
            assert sndbuf == null || sndbuf >= BYTES;
        }
        Objects.requireNonNull(target, "target is null");
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        final var written = channel.send(buffer, target);
        if (written != BYTES) {
            assert written == 0;
            throw new IOException("packet dropped; OS's send buffer is full");
        }
        assert !buffer.hasRemaining();
        assert written == buffer.capacity();
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
     * @throws IOException              if an I/O error occurs, or if the datagram was not sent due
     *                                  to the OS's send buffer being full.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer) put(buffer)} method with a
     * byte buffer of {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, and
     * {@link DatagramChannel#write(ByteBuffer) writes} the buffer to the {@code channel}.
     * @see #put(ByteBuffer)
     * @see DatagramChannel#write(ByteBuffer)
     */
    default <T extends DatagramChannel> T write(final T channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (!channel.isConnected()) {
            throw new IllegalArgumentException("not connected: " + channel);
        }
        {
            final var sndbuf = channel.getOption(StandardSocketOptions.SO_SNDBUF);
            assert sndbuf == null || sndbuf >= BYTES;
        }
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        assert buffer.remaining() == BYTES;
        final var written = channel.write(buffer);
        if (written != BYTES) {
            assert written == 0;
            throw new IOException("packet dropped; OS's send buffer is full");
        }
        assert !buffer.hasRemaining();
        assert written == buffer.capacity();
        return channel;
    }

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified socket
     * channel.
     * <p>
     * The default implementation would be as follows.
     * {@snippet lang = "java":
     * Objects.requireNonNull(channel, "channel is null");
     * write(channel); // @highlight
     * return channel;
     *}
     *
     * @param channel the socket channel to which the <a
     *                href="#hello-world-bytes">hello-world-bytes</a> are sent.
     * @param <T>     socket channel type parameter
     * @return the given {@code channel}.
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

    // --------------------------------------------------------------------------- java.nio.channels

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified channel.
     *
     * @param <T>     channel type parameter
     * @param channel the channel to which bytes are written.
     * @return the given {@code channel}.
     * @throws InterruptedException if interrupted while executing.
     * @throws IOException          if an I/O error occurs.
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
            throws InterruptedException, IOException {
        Objects.requireNonNull(channel, "channel is null");
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        while (buffer.hasRemaining()) {
            try {
                channel.write(buffer).get();
            } catch (final ExecutionException ee) {
                final var cause = ee.getCause();
                if (cause instanceof InterruptedException ie) throw ie;
                if (cause instanceof Error err) throw err;
                if (cause instanceof RuntimeException re) throw re;
                if (cause instanceof IOException ioe) throw ioe;
                throw new RuntimeException("failed to write", cause);
            }
        }
        return channel;
    }

    /**
     * Sends the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified socket
     * channel.
     *
     * @param channel the socket channel to which the <a
     *                href="#hello-world-bytes">hello-world-bytes</a> are sent.
     * @param <T>     socket channel type parameter
     * @return the given {@code channel}.
     * @throws InterruptedException if interrupted while executing.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #write(AsynchronousByteChannel)} method with
     * {@code channel}, and returns the result.
     * @deprecated Invoke directly the {@link #write(AsynchronousByteChannel)} method with
     * {@code channel}.
     */
    @屋上架屋("AsynchronousSocketChannel implements AsynchronousByteChannel")
    @Deprecated(forRemoval = true)
    default <T extends AsynchronousSocketChannel> T send(final T channel)
            throws InterruptedException, IOException {
        return write(channel);
    }

    /**
     * Writes, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to the specified channel, and notifies a completion (or a failure) to the specified handler
     * with the specified attachment.
     * <p>
     * The default implementation would be as follows.
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
     *}
     *
     * @param <T>        channel type parameter
     * @param channel    the channel to which bytes are written.
     * @param attachment the attachment for the {@code handler}; may be {@code null}.
     * @param handler    the completion handler to be notified with a completion (or a failure).
     * @throws NullPointerException if either {@code channel} or {@code handler} is {@code null}.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer) put(buffer)} method with a
     * byte buffer of {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, writes the buffer
     * to the {@code channel} while the buffer has remaining, and notifies a completion (or a
     * failure) to the {@code handler}.
     * @see #put(ByteBuffer)
     * @see AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     * @see <a
     * href="https://docs.oracle.com/javase/specs/jls/se25/html/jls-15.html#jls-15.8.3">15.8.3.
     * this</a> (The Java® Language Specification)
     */
    default <T extends AsynchronousByteChannel, A> void write(
            final T channel,
            @Nullable final A attachment,
            final CompletionHandler<? super T, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        // get the <hello-world-bytes>
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        // keep invoking <channel.write(buffer, attachment, same-handler)>,
        //         while <buffer> has <remaining>
        // and, eventually, invoke <handler.complete(channel, attachment)>
//        channel.write(
//                buffer,                     // <src>
//                null,                       // <attachment>
//                new CompletionHandler<>() { // <handler>
//                    @Override // @formatter:off
//                    public void completed(final Integer result, final Object a) {
//                        assert result > 0; // why?
//                        if (!buffer.hasRemaining()) {
//                            handler.completed(channel, attachment);
//                            return;
//                        }
//                        channel.write(
//                                buffer, // <src>
//                                a,      // <attachment>
//                                this    // <handler>; what does the `this` expression denote?
//                        );
//                    }
//                    @Override public void failed(final Throwable exc, final Object a) {
//                        handler.failed(exc, attachment);
//                    } // @formatter:on
//                }
//        );
    }

    /**
     * Sends, asynchronously, the <a href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a>
     * to the specified channel, and notifies a completion (or a failure) to the specified handler
     * with the specified attachment.
     *
     * @param channel    the channel to which the <a
     *                   href="HelloWorld.html#hello-world-bytes">hello-world-bytes</a> are sent.
     * @param attachment an attachment.
     * @param handler    the handler to be notified with a completion (or a failure).
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @implSpec Default implementation invokes
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler)} method with the given
     * arguments.
     * @deprecated Invoke, directly, the
     * {@link #write(AsynchronousByteChannel, Object, CompletionHandler)} method with
     * {@code channel}, {@code attachment}, and {@code handler}.
     */
    @屋上架屋("AsynchronousSocketChannel implements AsynchronousByteChannel")
    @Deprecated(forRemoval = true)
    default <T extends AsynchronousSocketChannel, A> void send(
            final T channel, final A attachment,
            final CompletionHandler<? super T, ? super A> handler) {
        write(channel, attachment, handler);
    }

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified asynchronous
     * file channel, starting at the given file position.
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
     * @param channel  the asynchronous file channel to which bytes are written.
     * @param position the file position at which the transfer is to begin; must be non-negative.
     * @return the given {@code channel}.
     * @throws InterruptedException if interrupted while executing.
     * @throws IOException          if an I/O error occurs.
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
            throws InterruptedException, IOException {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        while (buffer.hasRemaining()) {
            final var future = channel.write(buffer, position);
            try {
                final var written = future.get();
                assert written > 0; // why?
                position += written;
            } catch (final ExecutionException ee) {
                final var cause = ee.getCause();
                if (cause instanceof InterruptedException ie) throw ie;
                if (cause instanceof Error err) throw err;
                if (cause instanceof RuntimeException re) throw re;
                if (cause instanceof IOException ioe) throw ioe;
                throw new RuntimeException("failed to write", cause);
            }
        }
        return channel;
    }

    /**
     * Writes, asynchronously, the <a href="#hello-world-bytes">hello-world-bytes</a> to the
     * specified channel, starting at the specified position, and notifies a completion (or a
     * failure) to the specified handler.
     *
     * @param <T>        channel type parameter
     * @param <A>        attachment type parameter
     * @param channel    the asynchronous file channel to which bytes are written.
     * @param position   the file position at which the transfer is to begin; must be non-negative.
     * @param attachment an attachment for the {@code handler}; may be {@code null}.
     * @param handler    the handler.
     * @throws NullPointerException     if either {@code channel} or {@code handler} is
     *                                  {@code null}.
     * @throws IllegalArgumentException if {@code position} is negative.
     * @see AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     */
    // @formatter:off
    default <T extends AsynchronousFileChannel, A> void write(
            final T channel,
            final long position,
            final @Nullable A attachment,
            final CompletionHandler<? super T, ? super A> handler) {
        Objects.requireNonNull(channel, "channel is null");
        if (position < 0L) {
            throw new IllegalArgumentException("position(" + position + ") is negative");
        }
        Objects.requireNonNull(handler, "handler is null");
        // get the <hello-world-bytes>
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        // write the <buffer> to the <channel>
        channel.write(
                buffer,                     // <src>
                position,                   // <position>
                position,                   // <attachment>
                new CompletionHandler<>() { // <handler>
                    @Override public void completed(final Integer r, final Long p) {
                        assert r > 0; // why?
                        if (!buffer.hasRemaining()) {
                            handler.completed(channel, attachment);
                            return;
                        }
                        final var position = p + r;
                        channel.write(
                                buffer,   // <src>
                                position, // <position>
                                position, // <attachment>
                                this      // <handler>
                        );
                    }
                    @Override public void failed(final Throwable t, final Long p) {
                        handler.failed(t, attachment);
                    }
                }
        );
    } // @formatter:on

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a> to the end of the specified
     * path to a file, and notifies a completion (or a failure) to the specified handler.
     *
     * @param path       the path to a file to which the bytes are appended.
     * @param attachment an attachment for the handler.
     * @param handler    the handler to be notified with a completion (or a failure).
     * @param <T>        path type parameter
     * @param <A>        attachment type parameter
     * @throws IOException if an I/O error occurs.
     */
    default <T extends Path, A> void append(final T path, @Nullable final A attachment,
                                            final CompletionHandler<? super T, ? super A> handler)
            throws IOException {
        Objects.requireNonNull(path, "path is null");
        Objects.requireNonNull(handler, "handler is null");
        final var options = new OpenOption[] {
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE
        };
        @SuppressWarnings({
                "java:S2095" // Resources should be closed
        })
        final var channel = AsynchronousFileChannel.open(path, options); // no try-with-resources?
        write(channel, channel.size(), attachment, new CompletionHandler<>() { // @formatter:off
            @Override public void completed(final AsynchronousFileChannel r, final A a) {
                assert r == channel;
                try {
                    r.force(true);
                    r.close();
                } catch (final IOException ioe) {
//                    log().error("failed to force/close the channel", ioe);
                    handler.failed(ioe, a);
                    return;
                }
                handler.completed(path, a);
            }
            @Override public void failed(final Throwable t, final A a) {
//                log().error("failed({}, {})", t, a, t);
                try {
                    channel.close();
                } catch (final IOException ioe) {
//                    log().error("failed to close the channel", ioe);
                    handler.failed(ioe, a);
                    return;
                }
                handler.failed(t, a);
            } // @formatter:on
        });
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
        // open a <FileChannel> with <path>,
        //         <StandardOpenOption.CREATE>, and <StandardOpenOption.APPEND>
        // use the try-with-resources statement
//        final var options = new OpenOption[] {
//                StandardOpenOption.CREATE,
//                StandardOpenOption.APPEND
//        };
//        try (var channel = FileChannel.open(path, options)) {
//            // invoke <write(channel)> method with it
////            write(channel);
//            // force changes to both the <file>'s content and metadata
////            channel.force(true);
//        }
        return path;
    }

    // ---------------------------------------------------------------- java.security / javax.crypto

    /**
     * Updates the specified message digest with the <a
     * href="#hello-world-bytes">hello-world-bytes</a>.
     *
     * @param digest the message digest to be updated.
     * @param <T>    message digest type parameter
     * @return the given {@code digest}.
     * @throws NullPointerException if {@code digest} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and {@link MessageDigest#update(byte[]) updates} the {@code digest}
     * with the array.
     * @see #set(byte[])
     * @see MessageDigest#update(byte[])
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

    /**
     * Updates the specified cipher with the <a href="#hello-world-bytes">hello-world-bytes</a>, and
     * accepts the result to the specified consumer.
     *
     * @param cipher   the cipher to be updated.
     * @param consumer the consumer to accept the result of
     *                 {@link Cipher#update(byte[]) cipher.update(array)}.
     * @param <T>      cipher type parameter
     * @return the given {@code cipher}.
     * @throws NullPointerException if {@code cipher} is {@code null} or {@code consumer} is
     *                              {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, {@link Cipher#update(byte[]) updates} the {@code cipher} with the
     * array, and {@link Consumer#accept(Object) accepts} the result to the {@code consumer}.
     * @see #set(byte[])
     * @see Cipher#update(byte[])
     * @see Consumer#accept(Object)
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
     * of {@value #BYTES} bytes, and {@link Mac#update(byte[]) updates} the {@code mac} with the
     * array.
     * @see #set(byte[])
     * @see Mac#update(byte[])
     */
    default <T extends Mac> T update(final T mac) {
        Objects.requireNonNull(mac, "mac is null");
        final var array = new byte[BYTES];
        set(array);
        mac.update(array);
        return mac;
    }

    // ------------------------------------------------------------------------------------ java.sql
    default <T extends Blob> T set(final T blob, @Positive long pos) throws SQLException {
        Objects.requireNonNull(blob, "blob is null");
        if (pos <= 0L) {
            throw new IllegalArgumentException("non-positive pos: " + pos);
        }
        final var array = new byte[BYTES];
        set(array);
        for (int offset = 0; offset < array.length; ) {
            final var written = blob.setBytes(
                    pos,                  // <pos>
                    array,                // <bytes>
                    offset,               // <offset>
                    array.length - offset // <len>
            );
            assert written >= 0;
            offset += written;
            pos += written;
        }
        return blob;
    }

    // ------------------------------------------------------------------------------- java.util.jar

    // ------------------------------------------------------------------------------- java.util.zip
    default <T extends Checksum> T update(final T checksum) {
        Objects.requireNonNull(checksum, "checksum is null");
        final var array = new byte[BYTES];
        set(array);
        checksum.update(array);
        return checksum;
    }

    default <T extends Deflater> T input(final T deflater) {
        final var array = new byte[BYTES];
        set(array);
        deflater.setInput(array);
        return deflater;
    }
}
