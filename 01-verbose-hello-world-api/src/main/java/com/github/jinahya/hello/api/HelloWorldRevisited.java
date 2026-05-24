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

import javax.crypto.*;
import java.io.*;
import java.lang.foreign.*;
import java.net.*;
import java.net.http.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.zip.*;

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

    @Override
    default byte[] set(final byte[] array, final int index) {
        assert array != null;
        assert index >= 0;
        assert index + BYTES <= array.length;
        final var src = "hello, world".getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(src, 0, array, index, src.length);
        return array;
    }

    @Override
    default byte[] set(final byte[] array) {
        return set(array, 0);
    }

    private byte[] byteArray() {
        return set(new byte[BYTES]);
    }

    // ----------------------------------------------------------------------------------- java.lang
    @Override
    default <T extends Appendable> T append(final T appendable) throws IOException {
        for (final var b : byteArray()) {
            appendable.append((char) b);
        }
        return appendable;
    }

    // ------------------------------------------------------------------------------------- java.io
    @Override
    default <T extends DataOutput> T write(final T output) throws IOException {
        output.write(byteArray());
        return output;
    }

    @Override
    default <T extends OutputStream> T write(final T stream) throws IOException {
        stream.write(byteArray());
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
    default <T extends PrintStream> T send(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    // --------------------------------------------------------------------------- java.lang.foreign
    @Override
    default <T extends MemorySegment> T copy(final T segment) {
        final var array = byteArray();
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
        return (T) builder.method(method, HttpRequest.BodyPublishers.ofByteArray(byteArray()));
    }

    // ------------------------------------------------------------------------------------ java.nio
    @Override
    default <T extends ByteBuffer> T put(final T buffer) {
        if (buffer.hasArray()) {
            set(buffer.array(), (buffer.arrayOffset() + buffer.position()));
            buffer.position(buffer.position() + BYTES);
        } else {
            buffer.put(byteArray());
        }
        return buffer;
    }

    private ByteBuffer byteBuffer() {
        return ByteBuffer.wrap(byteArray());
    }

    // --------------------------------------------------------------------------- java.nio.channels
    @Override
    default <T extends AsynchronousByteChannel> T write(final T channel)
            throws InterruptedException, ExecutionException {
        for (final var b = byteBuffer().flip(); b.hasRemaining(); ) {
            channel.write(b).get();
        }
        return channel;
    }

    @Override
    default <T extends AsynchronousFileChannel> T write(final T channel, long position)
            throws InterruptedException, ExecutionException {
        for (final var b = byteBuffer().flip(); b.hasRemaining(); ) {
            final var future = channel.write(b, position);
            position += future.get();
        }
        return channel;
    }

    @Override
    default <T extends DatagramChannel> T send(final T channel, final SocketAddress target)
            throws IOException {
        if (channel.send(byteBuffer().flip(), target) != BYTES) {
            throw new IOException("packet dropped; OS's send buffer is full");
        }
        return channel;
    }

    @Override
    default <T extends DatagramChannel> T send(final T channel) throws IOException {
        if (channel.write(byteBuffer().flip()) != BYTES) {
            throw new IOException("packet dropped; OS's send buffer is full");
        }
        return channel;
    }

    @Override
    default <T extends WritableByteChannel> T write(final T channel) throws IOException {
        for (final var b = byteBuffer().flip(); b.hasRemaining(); ) {
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
        digest.update(byteArray());
        return digest;
    }

    @Override
    default <T extends Signature> T update(final T signature) throws SignatureException {
        signature.update(byteArray());
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
        for (final var b : byteArray()) {
            consumer.accept(b);
        }
        return consumer;
    }

    // ------------------------------------------------------------------------------- java.util.jar

    // ---------------------------------------------------------------------------- java.util.stream

    // ------------------------------------------------------------------------------- java.util.zip
    @Override
    default <T extends Checksum> T update(final T checksum) {
        checksum.update(byteArray());
        return checksum;
    }

    @Override
    default <T extends Deflater> T setInput(final T deflater) {
        deflater.setInput(byteArray());
        return deflater;
    }

    // -------------------------------------------------------------------------------- javax.crypto
    @Override
    default <T extends Cipher> T update(final T cipher, final Consumer<? super byte[]> consumer) {
        consumer.accept(cipher.update(byteArray()));
        return cipher;
    }

    @Override
    default <T extends Mac> T update(final T mac) {
        mac.update(byteArray());
        return mac;
    }

    // ------------------------------------------------------------------------------- javax.net.ssl
//    @Deprecated(forRemoval = true)
//    @Override
//    default <T extends SSLSocket> T send(final T socket) throws IOException {
//        return HelloWorld.super.send(socket);
//    }
}
