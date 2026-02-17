package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2024 Jinahya, Inc.
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
import java.io.FilterOutputStream;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedOutputStream;
import java.io.PipedWriter;
import java.io.PrintStream;
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
import java.net.http.HttpRequest;
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
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.jar.JarOutputStream;
import java.util.zip.Checksum;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

/**
 * Just a revisited implementation.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
interface HelloWorldRevisited
        extends HelloWorld {

    static HelloWorld newInstance() {
        return new HelloWorldRevisited() {
        };
    }

    // ------------------------------------------------------------------------------------- java.io
    @Override
    default <T extends DataOutput> T write(final T output) throws IOException {
        output.write(set());
        return output;
    }

    @Override
    default <T extends OutputStream> T write(final T stream) throws IOException {
        stream.write(set());
        return stream;
    }

    @Override
    default <T extends RandomAccessFile> T write(final T file) throws IOException {
        file.write(set());
        return file;
    }

    @Override
    default <T extends Writer> T write(final T writer) throws IOException {
        return append(writer);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends DataOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @Override
    default <T extends File> T append(final T file) throws IOException {
        try (var stream = new FileOutputStream(file, true)) {
            write(stream).flush();
        }
        return file;
    }

    @Override
    default <T extends FileOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @Override
    default <T extends PipedOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @Override
    default <T extends CharArrayWriter> T write(final T writer) throws IOException {
        return HelloWorld.super.write(writer);
    }

    @Override
    default <T extends FilterWriter> T write(final T writer) throws IOException {
        return HelloWorld.super.write(writer);
    }

    @Override
    default <T extends OutputStreamWriter> T write(final T writer) throws IOException {
        return HelloWorld.super.write(writer);
    }

    @Override
    default <T extends PipedWriter> T write(final T writer) throws IOException {
        return HelloWorld.super.write(writer);
    }

    @Override
    default <T extends StringWriter> T write(final T writer) throws IOException {
        return (T) write((Writer) writer);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends FilterOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends ObjectOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends BufferedWriter> T write(final T writer) throws IOException {
        return HelloWorld.super.write(writer);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends PrintWriter> T write(final T writer) throws IOException {
        return HelloWorld.super.write(writer);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends BufferedOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    default <T extends PrintStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    // ----------------------------------------------------------------------------------- java.lang
    @Override
    default byte[] set(final byte[] array, final int index) {
        final var src = HelloWorldUtils.getHelloWorldBytes();
        System.arraycopy(src, 0, array, index, src.length);
        return array;
    }

    @Override
    default byte[] set(final byte[] array) {
        return set(array, 0);
    }

    @Override
    default <T extends Appendable> T append(final T appendable) throws IOException {
        for (final var b : set()) {
            appendable.append((char) b);
        }
        return appendable;
    }

    @Override
    default byte[] set() {
        return set(new byte[BYTES]);
    }

    // --------------------------------------------------------------------------- java.lang.foreign
    @Override
    default <T extends MemorySegment> T copy(final T segment) {
        final var array = set();
        MemorySegment.copy(
                array,                 // <srcArray>
                0,                     // <srcIndex>
                segment,               // <dstSegment>
                ValueLayout.JAVA_BYTE, // <dstLayout>
                0,                     // <dstOffset>
                array.length           // <elementCount>
        );
        return segment;
    }

    // ------------------------------------------------------------------------------------ java.net
    @Override
    default DatagramPacket append(final DatagramPacket packet) {
        final ByteBuffer buffer;
        {
            final var array = packet.getData();
            final var offset = packet.getOffset() + packet.getLength();
            buffer = ByteBuffer.wrap(array, offset, array.length - offset);
        }
        packet.setLength(put(buffer).position() - packet.getOffset());
        return packet;
    }

    @Override
    default DatagramPacket set(final DatagramPacket packet) {
        if (packet.getData().length >= BYTES) {
            set(packet.getData());
            packet.setData(packet.getData(), 0, BYTES);
        } else {
            packet.setData(set());
        }
        return packet;
    }

    @Override
    default <T extends Socket> T send(final T socket) throws IOException {
        write(socket.getOutputStream());
        return socket;
    }

    @Override
    default <T extends DatagramSocket> T send(final T socket) throws IOException {
        socket.send(set(new DatagramPacket(new byte[BYTES], BYTES)));
        return socket;
    }

    @Override
    default <T extends DatagramSocket> T send(final T socket, final SocketAddress target)
            throws IOException {
        socket.send(set(new DatagramPacket(new byte[BYTES], BYTES, target)));
        return socket;
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends MulticastSocket> T send(final T socket) throws IOException {
        return HelloWorld.super.send(socket);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends MulticastSocket> T send(final T socket, final SocketAddress target)
            throws IOException {
        return HelloWorld.super.send(socket, target);
    }

    // ------------------------------------------------------------------------------- java.net.http
    @SuppressWarnings({"unchecked"})
    @Override
    default <T extends HttpRequest.Builder> T method(final T builder, final String method) {
        return (T) builder.method(method, HttpRequest.BodyPublishers.ofByteArray(set()));
    }

    // ------------------------------------------------------------------------------------ java.nio
    @Override
    default <T extends ByteBuffer> T put(final T buffer) {
        if (buffer.hasArray()) {
            set(buffer.array(), (buffer.arrayOffset() + buffer.position()));
            buffer.position(buffer.position() + BYTES);
        } else {
            buffer.put(set());
        }
        return buffer;
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends CharBuffer> T put(final T buffer) throws IOException {
        return HelloWorld.super.put(buffer);
    }

    @Override
    default ByteBuffer put() {
        return put(ByteBuffer.allocate(BYTES));
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends MappedByteBuffer> T put(final T buffer) {
        return HelloWorld.super.put(buffer);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends ShortBuffer> T put(final T buffer) throws IOException {
        return HelloWorld.super.put(buffer);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends IntBuffer> T put(final T buffer) throws IOException {
        return HelloWorld.super.put(buffer);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends LongBuffer> T put(final T buffer) throws IOException {
        return HelloWorld.super.put(buffer);
    }

    // --------------------------------------------------------------------------- java.nio.channels
    @Override
    default <T extends AsynchronousByteChannel> T write(final T channel)
            throws InterruptedException, IOException {
        for (final var b = put().flip(); b.hasRemaining(); ) {
            try {
                channel.write(b).get();
            } catch (final ExecutionException ee) {
                final var cause = ee.getCause();
                if (cause instanceof InterruptedException ie) {
                    throw ie;
                }
                if (cause instanceof Error err) {
                    throw err;
                }
                if (cause instanceof RuntimeException re) {
                    throw re;
                }
                if (cause instanceof IOException ioe) {
                    throw ioe;
                }
                throw new RuntimeException("failed to write", cause);
            }
        }
        return channel;
    }

    @Override
    default <T extends AsynchronousFileChannel> T write(final T channel, long position)
            throws InterruptedException, ExecutionException {
        for (final var b = put().flip(); b.hasRemaining(); ) {
            final var future = channel.write(b, position);
            position += future.get();
        }
        return channel;
    }

    @Override
    default <T extends DatagramChannel> T send(final T channel, final SocketAddress target)
            throws IOException {
        if (channel.send(put().flip(), target) != BYTES) {
            throw new IOException("packet dropped; OS's send buffer is full");
        }
        return channel;
    }

    @Override
    default <T extends DatagramChannel> T write(final T channel)
            throws IOException {
        if (channel.write(put().flip()) != BYTES) {
            throw new IOException("packet dropped; OS's send buffer is full");
        }
        return channel;
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends GatheringByteChannel> T write(final T channel)
            throws IOException {
        final var srcs = new ByteBuffer[] {put().flip()};
        for (var r = Arrays.stream(srcs).mapToLong(ByteBuffer::remaining).sum(); r > 0L; ) {
            r -= channel.write(srcs);
        }
        return channel;
    }

    @Override
    default <T extends WritableByteChannel> T write(final T channel) throws IOException {
        for (final var b = put().flip(); b.hasRemaining(); ) {
            channel.write(b);
        }
        return channel;
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends AsynchronousSocketChannel> T send(final T channel)
            throws InterruptedException, IOException {
        return HelloWorld.super.send(channel);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends Pipe.SinkChannel> T write(final T channel) throws IOException {
        return HelloWorld.super.write(channel);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends SeekableByteChannel> T write(final T channel) throws IOException {
        return HelloWorld.super.write(channel);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends SocketChannel> T send(final T channel) throws IOException {
        return HelloWorld.super.send(channel);
    }

    // ------------------------------------------------------------------------------- java.nio.file
    @Override
    default <T extends Path> T append(final T path) throws IOException {
        final var options = new OpenOption[] {
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        };
        try (var channel = FileChannel.open(path, options)) {
            ((FileChannel) write((WritableByteChannel) channel)).force(true);
        }
        return path;
    }

    // ------------------------------------------------------------------------------ java.security
    @Override
    default <T extends MessageDigest> T update(final T digest) {
        return HelloWorld.super.update(digest);
    }

    @Override
    default <T extends Signature> T update(final T signature)
            throws SignatureException {
        return HelloWorld.super.update(signature);
    }

    // ------------------------------------------------------------------------------------ java.sql
    @Override
    default <T extends Blob> T set(final T blob, @Positive final long pos) throws SQLException {
        return HelloWorld.super.set(blob, pos);
    }

    // ----------------------------------------------------------------------------------- java.util
    @Override
    default <T extends BitSet> T set(final T bitset, final int index) {
        return HelloWorld.super.set(bitset, index);
    }

    // ------------------------------------------------------------------------------- java.util.jar

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends JarOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @Override
    default <T extends JarOutputStream> T put(final T stream, final String name)
            throws IOException {
        return HelloWorld.super.put(stream, name);
    }

    // ------------------------------------------------------------------------------- java.util.zip
    @Override
    default <T extends Checksum> T update(final T checksum) {
        checksum.update(set());
        return checksum;
    }

    @Override
    default <T extends Deflater> T input(final T deflater) {
        deflater.setInput(set());
        return deflater;
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends DeflaterOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends GZIPOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends ZipOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @Override
    default <T extends ZipOutputStream> T put(final T stream, final String name)
            throws IOException {
        return HelloWorld.super.put(stream, name);
    }

    // ------------------------------------------------------------------------------ javax.crypto
    @Override
    default <T extends Cipher> T update(final T cipher, final Consumer<? super byte[]> consumer) {
        return HelloWorld.super.update(cipher, consumer);
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends CipherOutputStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    @Override
    default <T extends Mac> T update(final T mac) {
        mac.update(set());
        return mac;
    }

    // ----------------------------------------------------------------------------- javax.net.ssl
    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    @Override
    default <T extends SSLSocket> T send(final T socket) throws IOException {
        return HelloWorld.super.send(socket);
    }
}
