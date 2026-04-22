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
import javax.crypto.CipherOutputStream;
import javax.crypto.Mac;
import javax.net.ssl.SSLSocket;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedOutputStream;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
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
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.Pipe;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.BitSet;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;
import java.util.zip.Checksum;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
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

    @Deprecated(forRemoval = true)
    @屋上架屋("BufferedOutputStream extends FilterOutputStream")
    default <T extends BufferedOutputStream> T write(final T stream) throws IOException {
        return (T) write((FilterOutputStream) stream);
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("FileOutputStream extends OutputStream")
    default <T extends FileOutputStream> T write(final T stream) throws IOException {
        return (T) write((OutputStream) stream);
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("FilterOutputStream extends OutputStream")
    default <T extends FilterOutputStream> T write(final T stream) throws IOException {
        final var result = write((OutputStream) stream);
        assert result == stream;
        return stream;
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("ObjectOutputStream extends OutputStream")
    default <T extends ObjectOutputStream> T write(final T stream) throws IOException {
        return (T) write((OutputStream) stream);
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("PrintStream extends OutputStream")
    default <T extends PipedOutputStream> T write(final T stream) throws IOException {
        return (T) write((OutputStream) stream);
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

    @Deprecated(forRemoval = true)
    @屋上架屋("DataOutputStream extends FilterOutputStream implements DataOutput")
    @SuppressWarnings({"unchecked"})
    default <T extends DataOutputStream> T write(final T stream) throws IOException {
        return (T) write((DataOutput) stream);
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
        final var array = new byte[BYTES];
        set(array);
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

    @Deprecated(forRemoval = true)
    @屋上架屋("BufferedWriter extends Writer")
    @SuppressWarnings({"unchecked"})
    default <T extends BufferedWriter> T write(final T writer) throws IOException {
        return (T) write((Writer) writer);
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("CharArrayWriter extends Writer")
    @SuppressWarnings({"unchecked"})
    default <T extends CharArrayWriter> T write(final T writer) throws IOException {
        return (T) write((Writer) writer);
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("FilterWriter extends Writer")
    @SuppressWarnings({"unchecked"})
    default <T extends FilterWriter> T write(final T writer) throws IOException {
        final var result = write((Writer) writer);
        assert result == writer;
        return writer;
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("OutputStreamWriter extends Writer")
    default <T extends OutputStreamWriter> T write(final T writer) throws IOException {
        Objects.requireNonNull(writer, "writer is null");
        final var result = write((Writer) writer);
        assert result == writer;
        return writer;
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("PipedWriter extends Writer implements Appendable")

    default <T extends PipedWriter> T write(final T writer) throws IOException {
        Objects.requireNonNull(writer, "writer is null");
        final var result = write((Writer) writer);
        assert result == writer;
        return writer;
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("PrintWriter extends Writer implements Appendable")
    default <T extends PrintWriter> T write(final T writer) throws IOException {
        Objects.requireNonNull(writer, "writer is null");
        final var result = write((Writer) writer);
        assert result == writer;
        return writer;
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("StringWriter extends Writer")
    default <T extends StringWriter> T write(final T writer) throws IOException {
        Objects.requireNonNull(writer, "writer is null");
        final var result = write((Writer) writer);
        assert result == writer;
        return writer;
    }

    /**
     * Appends the <a href="#hello-world-bytes">hello-world-bytes</a>, encoded with the specified
     * charset, to the end of the specified file, and returns the file.
     *
     * @param <T>     file type parameter
     * @param file    the file to append to
     * @param charset the character set to use for encoding
     * @see FileWriter#FileWriter(File, Charset, boolean)
     * @see #write(Writer)
     */
    default <T extends File> T append(final T file, final Charset charset) throws IOException {
        Objects.requireNonNull(file, "file is null");
        Objects.requireNonNull(charset, "charset is null");
        try (var writer = new FileWriter(file, charset, true)) {
            write(writer);
            writer.flush();
        }
        return file;
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
     * @throws NullPointerException      if {@code packet} is {@code null}.
     * @throws IndexOutOfBoundsException if the packet's data buffer does not have at least
     *                                   {@value #BYTES} bytes available after
     *                                   {@code offset + length}.
     * @implSpec Default implementation invokes {@link #set(byte[])} method with an array of
     * {@value #BYTES} bytes, copies the array into the {@code packet}'s
     * {@link DatagramPacket#getData() data} buffer starting at
     * ({@link DatagramPacket#getOffset() packet.offset} +
     * {@link DatagramPacket#getLength() packet.length}), increments the {@code packet}'s
     * {@link DatagramPacket#getLength() length} by {@value #BYTES}, and returns the
     * {@code packet}.
     * @see DatagramPacket#getData()
     * @see DatagramPacket#getOffset()
     * @see DatagramPacket#getLength()
     * @see DatagramPacket#setLength(int)
     * @see #set(byte[])
     */
    default DatagramPacket append(final DatagramPacket packet) {
        Objects.requireNonNull(packet, "packet is null");
        final var array = new byte[BYTES];
        set(array);
        System.arraycopy(
                array,
                0,
                packet.getData(),
                packet.getOffset() + packet.getLength(),
                array.length
        );
        packet.setLength(packet.getLength() + BYTES);
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
    default <T extends MulticastSocket> T send(final T socket)
            throws IOException {
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
    @Deprecated(forRemoval = true)
    @屋上架屋("SSLSocket extends Socket")
    @SuppressWarnings({"unchecked"})
    default <T extends SSLSocket> T send(final T socket) throws IOException {
        return (T) send((Socket) socket);
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
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        return buffer;
    }

    /**
     * Puts the <a href="#hello-world-bytes">hello-world-bytes</a> on the specified char buffer.
     *
     * @param <T>    buffer type parameter
     * @param buffer the char buffer on which bytes are put.
     * @return the given {@code buffer}.
     * @throws NullPointerException    if {@code buffer} is {@code null}.
     * @throws BufferOverflowException if {@link ByteBuffer#remaining() buffer.remaining} is less
     *                                 than {@value #BYTES}.
     * @implSpec Default implementation, invokes {@link #append(Appendable) append(appendable)}
     * method with the {@code buffer}, and returns the result.
     * @see #append(Appendable)
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/nio/CharBuffer.html">java.nio.CharBuffer</a>
     * @deprecated Use {@link #append(Appendable)} method.
     */
    @Deprecated(forRemoval = true)
    @屋上架屋("CharBuffer implements Appendable")
    @SuppressWarnings("unchecked")
    default <T extends CharBuffer> T put(final T buffer) throws IOException {
        Objects.requireNonNull(buffer, "buffer is null");
        if (buffer.remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        final var result = append((Appendable) buffer);
        assert result == buffer;
        return buffer;
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("CharBuffer implements Appendable")
    default <T extends ShortBuffer> T put(final T buffer) throws IOException {
        Objects.requireNonNull(buffer, "buffer is null");
        if (buffer.remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        final var b = ByteBuffer.allocate(BYTES);
        put(b);
        b.flip();
        while (b.hasRemaining()) {
            buffer.put(b.get());
        }
        return buffer;
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("CharBuffer implements Appendable")
    default <T extends IntBuffer> T put(final T buffer) throws IOException {
        Objects.requireNonNull(buffer, "buffer is null");
        if (buffer.remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        final var b = ShortBuffer.allocate(BYTES);
        put(b);
        b.flip();
        while (b.hasRemaining()) {
            buffer.put(b.get());
        }
        return buffer;
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("CharBuffer implements Appendable")
    default <T extends LongBuffer> T put(final T buffer) throws IOException {
        Objects.requireNonNull(buffer, "buffer is null");
        if (buffer.remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        final var b = IntBuffer.allocate(BYTES);
        put(b);
        b.flip();
        while (b.hasRemaining()) {
            buffer.put(b.get());
        }
        return buffer;
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("MappedByteBuffer extends ByteBuffer")
    @SuppressWarnings("unchecked")
    default <T extends MappedByteBuffer> T put(final T buffer) {
        final var result = put((ByteBuffer) buffer);
        assert result == buffer;
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

    /**
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified gathering byte
     * channel.
     * {@snippet lang = "java":
     * Objects.requireNonNull(channel, "channel is null");
     * var buffer = put(ByteBuffer.allocate(BYTES));
     * buffer.flip(); // @highlight
     * final var srcs = new ByteBuffer[] {buffer}; // @highlight
     * for (var r = Arrays.stream(srcs).mapToLong(ByteBuffer::remaining).sum(); r > 0; ) {
     *     r -= channel.write(srcs);
     * }
     * return channel;
     *}
     *
     * @param <T>     channel type parameter
     * @param channel the gathering byte channel to which bytes are written.
     * @return the given {@code channel}.
     * @throws NullPointerException if {@code channel} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     * @implSpec Default implementation invokes {@link #put(ByteBuffer)} method with a byte buffer
     * of {@value #BYTES} bytes, {@link ByteBuffer#flip() flips} it, wraps it in a
     * {@code ByteBuffer} array, and writes the array to the {@code channel} by continuously
     * invoking {@link GatheringByteChannel#write(ByteBuffer[]) channel.write(srcs)} while the total
     * remaining bytes is greater than zero.
     * @see #put(ByteBuffer)
     * @see ByteBuffer#flip()
     * @see GatheringByteChannel#write(ByteBuffer[])
     * @deprecated This method is just for demonstrating the
     * {@link GatheringByteChannel#write(ByteBuffer[])} method; use
     * {@link #write(WritableByteChannel) write(channel)} instead.
     */
    @Deprecated(forRemoval = true)
    @屋上架屋("GatheringByteChannel extends WritableByteChannel")
    default <T extends GatheringByteChannel> T write(final T channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        return (T) write((WritableByteChannel) channel);
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("SeekableByteChannel extends WritableByteChannel")
    default <T extends SeekableByteChannel> T write(final T channel) throws IOException {
        return (T) write((WritableByteChannel) channel);
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("Pipe.SinkChannel extends WritableByteChannel")
    default <T extends Pipe.SinkChannel> T write(final T channel) throws IOException {
        return (T) write((WritableByteChannel) channel);
    }

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
        final var buffer = ByteBuffer.allocate(BYTES);
        put(buffer);
        buffer.flip();
        while (buffer.hasRemaining()) {
            final var future = channel.write(buffer);
            final var written = future.get();
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
     * @throws ExecutionException   if failed while writing.
     * @implSpec Default implementation invokes {@link #write(AsynchronousByteChannel)} method with
     * {@code channel}, and returns the result.
     * @deprecated Invoke directly the {@link #write(AsynchronousByteChannel)} method with
     * {@code channel}.
     */
    @屋上架屋("AsynchronousSocketChannel implements AsynchronousByteChannel")
    @Deprecated(forRemoval = true)
    default <T extends AsynchronousSocketChannel> T send(final T channel)
            throws InterruptedException, ExecutionException {
        return write(channel);
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

    default <T extends DigestOutputStream> T write(final T stream) throws IOException {
        Objects.requireNonNull(stream, "stream is null");
        final var result = write((FilterOutputStream) stream);
        assert result == stream;
        return stream;
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
    default <T extends Blob> T set(final T blob, long pos) throws SQLException {
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
        for (var b : array) {
            for (int i = 0; i < Byte.SIZE; i++) {
                bitset.set(index++, (b & 1) == 1);
                b = (byte) (b >> 1);
            }
        }
        return bitset;
    }

    /**
     * Sets the <a href="#hello-world-bytes">hello-world-bytes</a> into the specified bit set,
     * starting at index {@code 0}.
     *
     * @param bitset the bit set into which the bits are set.
     * @param <T>    bit set type parameter
     * @return the given {@code bitset}.
     * @throws NullPointerException when the {@code bitset} is {@code null}.
     * @implSpec The default implementation invokes the {@link #set(BitSet, int) set(bitset, 0)}
     * method with the given {@code bitset} and {@code 0}, and returns the result.
     */
    default <T extends BitSet> T set(final T bitset) {
        Objects.requireNonNull(bitset, "bitset is null");
        return set(bitset, 0);
    }

    /**
     * Collects each of the <a href="#hello-world-bytes">hello-world-bytes</a>, boxed as
     * {@link Byte}, into the specified collection.
     *
     * @param <T>        collection type parameter
     * @param collection the collection into which each byte is collected.
     * @return the given {@code collection}.
     * @throws NullPointerException if {@code collection} is {@code null}.
     * @implSpec Default implementation invokes {@link #set(byte[]) set(array)} method with an array
     * of {@value #BYTES} bytes, and {@link Collection#add(Object) adds} each byte in the array,
     * boxed as {@link Byte}, to the {@code collection}.
     * @see #set(byte[])
     * @see Collection#add(Object)
     */
    default <T extends Collection<? super Byte>> T collect(final T collection) {
        Objects.requireNonNull(collection, "collection is null");
        final var array = new byte[BYTES];
        set(array);
        for (final var b : array) {
            collection.add(b);
        }
        return collection;
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
        final var array = new byte[BYTES];
        set(array);
        deflater.setInput(array);
        return deflater;
    }

    @Deprecated(forRemoval = true)
    @屋上架屋("DeflatorOutputStream extends FilterOutputStream")
    default <T extends DeflaterOutputStream> T write(final T stream) throws IOException {
        final var result = write((FilterOutputStream) stream);
        assert result == stream;
        return stream;
    }

    /**
     * .
     *
     * @param stream .
     * @param <T>    .
     * @return .
     * @throws IOException if an I/O error occurs.
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/zip/GZIPOutputStream.html">java.util.zip.GZIPOutputStream</a>
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/util/zip/GZIPInputStream.html">java.util.zip.GZIPInputStream</a>
     */
    @Deprecated(forRemoval = true)
    @屋上架屋("GZIPOutputStream extends DeflatorOutputStream")
    default <T extends GZIPOutputStream> T write(final T stream) throws IOException {
        final var result = write((DeflaterOutputStream) stream);
        assert result == stream;
        return stream;
    }

    @屋上架屋("ZipOutputStream extends DeflaterOutputStream")
    @Deprecated(forRemoval = true)
    default <T extends ZipOutputStream> T write(final T stream) throws IOException {
        final var result = write((DeflaterOutputStream) stream);
        assert result == stream;
        return stream;
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
        final var result = accept((Consumer<? super Byte>) builder);
        assert result == builder;
        return builder;
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
     * Writes the <a href="#hello-world-bytes">hello-world-bytes</a> to the specified cipher output
     * stream.
     *
     * @param stream the cipher output stream to which the bytes are written.
     * @param <T>    cipher output stream type parameter
     * @return the given {@code stream}
     * @throws IOException if an I/O error occurs
     * @see #write(FilterOutputStream)
     */
    @Deprecated(forRemoval = true)
    @屋上架屋("CipherOutputStream extends FilterOutputStream")
    default <T extends CipherOutputStream> T write(final T stream) throws IOException {
        final var result = write((FilterOutputStream) stream);
        assert result == stream;
        return stream;
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
        final var array = new byte[BYTES];
        set(array);
        mac.update(array);
        return mac;
    }
}
