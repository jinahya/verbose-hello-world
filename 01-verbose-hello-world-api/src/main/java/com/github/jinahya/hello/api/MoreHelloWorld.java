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

import org.jspecify.annotations.Nullable;

import java.io.DataOutput;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@SuppressWarnings({
        "java:S112",  // Generic exceptions should never be thrown
        "java:S1168", // Empty arrays and collections should be returned instead of null
        "java:S1481", // Unused local variables should be removed
        "java:S1854", // Unused assignments should be removed
        "java:S1865", // useless assignments
        "java:S4274", // assert ...
        "UnicodeInCode" // https://errorprone.info/bugpattern/UnicodeInCode
})
public interface MoreHelloWorld {

    /**
     * The length of the hello-world-bytes which is {@value}.
     */
    int BYTES = 12;

    // ------------------------------------------------------------------------------------- java.net

    /**
     * Writes the hello-world-bytes to the specified datagram socket bound to the specified remote
     * address.
     * <p>
     * The default implementation sends the bytes using a datagram packet.
     * {@snippet lang = "java":
     * Objects.requireNonNull(socket, "socket is null");
     * Objects.requireNonNull(remote, "remote is null");
     * var bytes = new byte[BYTES];
     * set(bytes, 0); // populate the hello-world-bytes
     * var packet = new java.net.DatagramPacket(bytes, BYTES, remote);
     * socket.send(packet);
     * return socket;
     *}
     *
     * @param socket the datagram socket to which bytes are written.
     * @param remote the remote socket address.
     * @return the given {@code socket}.
     * @throws NullPointerException if either {@code socket} or {@code remote} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     */
    default DatagramSocket write(DatagramSocket socket, InetSocketAddress remote)
            throws IOException {
        Objects.requireNonNull(socket, "socket is null");
        Objects.requireNonNull(remote, "remote is null");
        var bytes = new byte[BYTES];
        ((HelloWorld) this).set(bytes, 0);
        var packet = new java.net.DatagramPacket(bytes, BYTES, remote);
        socket.send(packet);
        return socket;
    }

    // ---------------------------------------------------------------------------- java.nio.channels

    /**
     * Writes the hello-world-bytes to the specified gathering byte channel.
     * <p>
     * The default implementation uses multiple buffers to demonstrate gathering writes.
     * {@snippet lang = "java":
     * Objects.requireNonNull(channel, "channel is null");
     * var buffers = new ByteBuffer[] {
     *     ByteBuffer.wrap(new byte[] {0x68, 0x65, 0x6C, 0x6C}),      // "hell"
     *     ByteBuffer.wrap(new byte[] {0x6F, 0x2C, 0x20}),            // "o, "
     *     ByteBuffer.wrap(new byte[] {0x77, 0x6F, 0x72, 0x6C, 0x64}) // "world"
     * };
     * long written = 0L;
     * while (written < BYTES) {
     *     written += channel.write(buffers);
     * }
     * return channel;
     *}
     *
     * @param channel the gathering byte channel to which bytes are written.
     * @return the given {@code channel}.
     * @throws NullPointerException if {@code channel} is {@code null}.
     * @throws IOException          if an I/O error occurs.
     */
    default GatheringByteChannel write(GatheringByteChannel channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        var buffers = new ByteBuffer[]{
                ByteBuffer.wrap(new byte[]{0x68, 0x65, 0x6C, 0x6C}),      // "hell"
                ByteBuffer.wrap(new byte[]{0x6F, 0x2C, 0x20}),            // "o, "
                ByteBuffer.wrap(new byte[]{0x77, 0x6F, 0x72, 0x6C, 0x64}) // "world"
        };
        long written = 0L;
        while (written < BYTES) {
            written += channel.write(buffers);
        }
        return channel;
    }

    // --------------------------------------------------------------------------- java.lang.foreign

    /**
     * Sets the hello-world-bytes on the specified memory segment starting at offset 0.
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
     * var bytes = new byte[BYTES];
     * set(bytes, 0);
     * MemorySegment.copy(bytes, 0, segment, ValueLayout.JAVA_BYTE, 0, BYTES);
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
     * @see MemorySegment#copy(Object, int, MemorySegment, ValueLayout, long, int)
     */
    default MemorySegment set(MemorySegment segment) {
        Objects.requireNonNull(segment, "segment is null");
        if (segment.byteSize() < BYTES) {
            throw new IndexOutOfBoundsException(
                    "byteSize(" + segment.byteSize() + ") < BYTES(" + BYTES + ")"
            );
        }
        var bytes = new byte[BYTES];
        ((HelloWorld) this).set(bytes, 0);
        MemorySegment.copy(bytes, 0, segment, ValueLayout.JAVA_BYTE, 0, BYTES);
        return segment;
    }

}
