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
import javax.crypto.Mac;
import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.util.BitSet;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.zip.Checksum;
import java.util.zip.Deflater;

final class DefaultHelloWorld
        implements HelloWorld {

    private static final byte[] ARRAY = "hello, world".getBytes(StandardCharsets.US_ASCII);

    // ---------------------------------------------------------------------------------------------
    private static final class InstanceHolder {

        private static final HelloWorld INSTANCE = new DefaultHelloWorld();

        private InstanceHolder() {
            throw new AssertionError("instantiation is not allowed");
        }
    }

    static HelloWorld getInstance() {
        return InstanceHolder.INSTANCE;
    }

    // ---------------------------------------------------------------------------------------------
    private DefaultHelloWorld() {
        throw new AssertionError("instantiation is not allowed");
    }

    // ---------------------------------------------------------------------------------------------
    @Override
    public byte[] set(final byte[] array, int index) {
        Objects.requireNonNull(array, "array is null");
        if (index < 0) {
            throw new IllegalArgumentException("index is negative: " + index);
        }
        if (index + BYTES > array.length) {
            throw new IllegalArgumentException(
                    "index(" + index + " + " + BYTES + ") > array.length(" + array.length + ")"
            );
        }
        System.arraycopy(
                ARRAY,
                0,
                array,
                index,
                ARRAY.length
        );
        return array;
    }

    @Override
    public byte[] set(final byte[] array) {
        return set(array, 0);
    }

    @Override
    public byte[] byteArray() {
        return set(new byte[BYTES]);
    }

    // ----------------------------------------------------------------------------------- java.lang
    @Override
    public <T extends Appendable> T append(final T appendable) throws IOException {
        for (final var b : byteArray()) {
            appendable.append((char) b);
        }
        return appendable;
    }

    // ------------------------------------------------------------------------------------- java.io
    @Override
    public <T extends DataOutput> T write(final T output) throws IOException {
        output.write(byteArray());
        return output;
    }

    @Override
    public <T extends OutputStream> T write(final T stream) throws IOException {
        stream.write(byteArray());
        return stream;
    }

    @Override
    public <T extends Writer> T write(final T writer) throws IOException {
        return append(writer);
    }

    @Override
    public <T extends File> T append(final T file) throws IOException {
        try (var stream = new FileOutputStream(file, true)) {
            write(stream).flush();
        }
        return file;
    }

    @Override
    public <T extends File> T append(final T file, final Charset charset) throws IOException {
        try (var writer = new OutputStreamWriter(new FileOutputStream(file, true), charset)) {
            write((OutputStreamWriter) writer).flush();
        }
        return file;
    }

    @SuppressWarnings("removal")
    @Deprecated(forRemoval = true)
    public <T extends PrintStream> T write(final T stream) throws IOException {
        return HelloWorld.super.write(stream);
    }

    // --------------------------------------------------------------------------- java.lang.foreign
    @Override
    public <T extends MemorySegment> T copy(final T segment) {
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
    public DatagramPacket append(final DatagramPacket packet) {
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
    public <T extends Socket> T send(final T socket) throws IOException {
        write(socket.getOutputStream());
        return socket;
    }

    @Override
    public <T extends DatagramSocket> T send(final T socket) throws IOException {
        socket.send(append(new DatagramPacket(new byte[BYTES], BYTES)));
        return socket;
    }

    @Override
    public <T extends DatagramSocket> T send(final T socket, final SocketAddress target)
            throws IOException {
        socket.send(append(new DatagramPacket(new byte[BYTES], BYTES, target)));
        return socket;
    }

    // ------------------------------------------------------------------------------- java.net.http
    @SuppressWarnings({"unchecked"})
    @Override
    public <T extends HttpRequest.Builder> T method(final T builder, final String method) {
        return (T) builder.method(method, HttpRequest.BodyPublishers.ofByteArray(byteArray()));
    }

    // ------------------------------------------------------------------------------------ java.nio
    @Override
    public <T extends ByteBuffer> T put(final T buffer) {
        if (buffer.hasArray()) {
            set(buffer.array(), (buffer.arrayOffset() + buffer.position()));
            buffer.position(buffer.position() + BYTES);
        } else {
            buffer.put(byteArray());
        }
        return buffer;
    }

    @Override
    public ByteBuffer byteBuffer() {
        return put(ByteBuffer.allocate(BYTES));
    }

    // --------------------------------------------------------------------------- java.nio.channels
    @Override
    public <T extends AsynchronousByteChannel> T write(final T channel)
            throws InterruptedException, ExecutionException {
        for (final var b = byteBuffer().flip(); b.hasRemaining(); ) {
            channel.write(b).get();
        }
        return channel;
    }

    @Override
    public <T extends AsynchronousFileChannel> T write(final T channel, long position)
            throws InterruptedException, ExecutionException {
        for (final var b = byteBuffer().flip(); b.hasRemaining(); ) {
            final var future = channel.write(b, position);
            position += future.get();
        }
        return channel;
    }

    @Override
    public <T extends DatagramChannel> T send(final T channel, final SocketAddress target)
            throws IOException {
        if (channel.send(byteBuffer().flip(), target) != BYTES) {
            throw new IOException("packet dropped; OS's send buffer is full");
        }
        return channel;
    }

    @Override
    public <T extends DatagramChannel> T write(final T channel) throws IOException {
        if (channel.write(byteBuffer().flip()) != BYTES) {
            throw new IOException("packet dropped; OS's send buffer is full");
        }
        return channel;
    }

    @Override
    public <T extends WritableByteChannel> T write(final T channel) throws IOException {
        for (final var b = byteBuffer().flip(); b.hasRemaining(); ) {
            channel.write(b);
        }
        return channel;
    }

    // ------------------------------------------------------------------------------- java.nio.file
    @Override
    public <T extends Path> T append(final T path) throws IOException {
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
    public <T extends MessageDigest> T update(final T digest) {
        digest.update(byteArray());
        return digest;
    }

    @Override
    public <T extends Signature> T update(final T signature) throws SignatureException {
        signature.update(byteArray());
        return signature;
    }

    // ------------------------------------------------------------------------------------ java.sql

    // ----------------------------------------------------------------------------------- java.util
    @Override
    public <T extends BitSet> T set(final T bitset, final int index) {
        return HelloWorld.super.set(bitset, index);
    }

    // -------------------------------------------------------------------------- java.util.function
    @Override
    public <T extends Consumer<? super Byte>> T accept(final T consumer) {
        for (final var b : byteArray()) {
            consumer.accept(b);
        }
        return consumer;
    }

    // ------------------------------------------------------------------------------- java.util.jar

    // ---------------------------------------------------------------------------- java.util.stream

    // ------------------------------------------------------------------------------- java.util.zip
    @Override
    public <T extends Checksum> T update(final T checksum) {
        checksum.update(byteArray());
        return checksum;
    }

    @Override
    public <T extends Deflater> T setInput(final T deflater) {
        deflater.setInput(byteArray());
        return deflater;
    }

    // -------------------------------------------------------------------------------- javax.crypto
    @Override
    public <T extends Cipher> T update(final T cipher, final Consumer<? super byte[]> consumer) {
        consumer.accept(cipher.update(byteArray()));
        return cipher;
    }

    @Override
    public <T extends Mac> T update(final T mac) {
        mac.update(byteArray());
        return mac;
    }

    // ------------------------------------------------------------------------------- javax.net.ssl
}
