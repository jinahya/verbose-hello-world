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

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.Mac;
import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.BitSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;
import java.util.zip.Checksum;
import java.util.zip.Deflater;
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
    default <T extends Writer> T write(final T writer) throws IOException {
        return append(writer);
    }

    @Override
    default <T extends File> T append(final T file) throws IOException {
        try (var stream = new FileOutputStream(file, true)) {
            write(stream).flush();
        }
        return file;
    }

    @Override
    default <T extends File> T append(final T file, final Charset charset) throws IOException {
        try (var writer = new OutputStreamWriter(new FileOutputStream(file, true), charset)) {
            write((OutputStreamWriter) writer).flush();
        }
        return file;
    }


    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    default <T extends PrintStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    // ----------------------------------------------------------------------------------- java.lang
    @Override
    default byte[] set(final byte[] array, final int index) {
        assert array != null;
        assert index >= 0;
        assert index + BYTES <= array.length;
        final var src = HelloWorldUtils.hello_world_bytes();
        System.arraycopy(src, 0, array, index, src.length);
        return array;
    }

    @Override
    default byte[] set(final byte[] array) {
        return set(array, 0);
    }

    @Override
    default byte[] set() {
        return set(new byte[BYTES]);
    }

    @Override
    default <T extends Appendable> T append(final T appendable) throws IOException {
        for (final var b : set()) {
            appendable.append((char) b);
        }
        return appendable;
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

//    @Override
//    default DatagramPacket set(final DatagramPacket packet) {
//        if (packet.getData().length >= BYTES) {
//            set(packet.getData());
//            packet.setData(packet.getData(), 0, BYTES);
//        } else {
//            packet.setData(set());
//        }
//        return packet;
//    }

    @Override
    default <T extends Socket> T send(final T socket) throws IOException {
        write(socket.getOutputStream());
        return socket;
    }

    @Override
    default <T extends DatagramSocket> T send(final T socket) throws IOException {
        socket.send(append(new DatagramPacket(new byte[BYTES], BYTES)));
        return socket;
    }

    @Override
    default <T extends DatagramSocket> T send(final T socket, final SocketAddress target)
            throws IOException {
        socket.send(append(new DatagramPacket(new byte[BYTES], BYTES, target)));
        return socket;
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

    @Override
    default ByteBuffer put() {
        return put(ByteBuffer.allocate(BYTES));
    }

    // --------------------------------------------------------------------------- java.nio.channels
    @Override
    default <T extends AsynchronousByteChannel> T write(final T channel)
            throws InterruptedException, ExecutionException {
        for (final var b = put().flip(); b.hasRemaining(); ) {
            channel.write(b).get();
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
    default <T extends DatagramChannel> T write(final T channel) throws IOException {
        if (channel.write(put().flip()) != BYTES) {
            throw new IOException("packet dropped; OS's send buffer is full");
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

    // ------------------------------------------------------------------------------- java.security
    @Override
    default <T extends MessageDigest> T update(final T digest) {
        digest.update(set());
        return digest;
    }

    @Override
    default <T extends Signature> T update(final T signature) throws SignatureException {
        signature.update(set());
        return signature;
    }

    // ------------------------------------------------------------------------------------ java.sql

    // ----------------------------------------------------------------------------------- java.util
    @Override
    default <T extends BitSet> T set(final T bitset, final int index) {
        return HelloWorld.super.set(bitset, index);
    }

    // -------------------------------------------------------------------------- java.util.function
    @Override
    default <T extends Consumer<? super Byte>> T accept(final T consumer) {
        for (final var b : set()) {
            consumer.accept(b);
        }
        return consumer;
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

    // ---------------------------------------------------------------------------- java.util.stream
    @Override
    default <T extends Stream.Builder<? super Byte>> T add(final T builder) {
        return HelloWorld.super.add(builder);
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

    @Override
    default <T extends ZipOutputStream> T put(final T stream, final String name)
            throws IOException {
        return HelloWorld.super.put(stream, name);
    }

    // -------------------------------------------------------------------------------- javax.crypto
    @Override
    default <T extends Cipher> T update(final T cipher, final Consumer<? super byte[]> consumer) {
        consumer.accept(cipher.update(set()));
        return cipher;
    }

    @Deprecated(forRemoval = true)
    @Override
    @SuppressWarnings({"removal", "unchecked"})
    default <T extends CipherOutputStream> T write(final T stream) throws IOException {
        return (T) write((FilterOutputStream) stream);
    }

    @Override
    default <T extends Mac> T update(final T mac) {
        mac.update(set());
        return mac;
    }

    // ------------------------------------------------------------------------------- javax.net.ssl
//    @Deprecated(forRemoval = true)
//    @Override
//    default <T extends SSLSocket> T send(final T socket) throws IOException {
//        return HelloWorld.super.send(socket);
//    }
}
