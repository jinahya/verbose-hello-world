package com.github.jinahya.hello;

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
import java.nio.channels.CompletionHandler;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Just a revisited implementation.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
interface HelloWorldRevisited extends HelloWorld {

    @Override
    default byte[] set(final byte[] array) {
        return set(array, 0);
    }

    @Override
    default <T extends Appendable> T append(final T appendable) throws IOException {
        for (final var b : set(new byte[BYTES])) {
            appendable.append((char) b);
        }
        return appendable;
    }

    @Override
    default <T extends OutputStream> T write(final T stream) throws IOException {
        stream.write(set(new byte[BYTES]));
        return stream;
    }

    @Override
    default <T extends File> T append(final T file) throws IOException {
        try (var stream = new FileOutputStream(file, true)) {
            write(stream).flush();
        }
        return file;
    }

    @Override
    default <T extends DataOutput> T write(final T output) throws IOException {
        output.write(set(new byte[BYTES]));
        return output;
    }

    @Override
    default <T extends RandomAccessFile> T write(final T file) throws IOException {
        file.write(set(new byte[BYTES]));
        return file;
    }

    @Override
    default <T extends Writer> T write(final T writer) throws IOException {
        return append(writer);
    }

    @Override
    default <T extends Socket> T send(final T socket) throws IOException {
        return HelloWorld.super.send(socket);
    }

    @Override
    default <T extends ByteBuffer> T put(final T buffer) {
        if (Objects.requireNonNull(buffer, "buffer is null").remaining() < BYTES) {
            throw new BufferOverflowException();
        }
        if (buffer.hasArray()) {
            set(buffer.array(), (buffer.arrayOffset() + buffer.position()));
            buffer.position(buffer.position() + HelloWorld.BYTES);
        } else {
            buffer.put(set(new byte[BYTES]));
        }
        return buffer;
    }

    @Override
    default <T extends WritableByteChannel> T write(final T channel) throws IOException {
        Objects.requireNonNull(channel, "channel is null");
        for (final var b = put(ByteBuffer.allocate(BYTES)).flip(); b.hasRemaining(); ) {
            channel.write(b);
        }
        return channel;
    }

    @Override
    default <T extends Path> T append(final T path) throws IOException {
        return HelloWorld.super.append(path);
    }

    @Override
    default <T extends AsynchronousByteChannel> T write(final T channel)
            throws InterruptedException, ExecutionException {
        Objects.requireNonNull(channel, "channel is null");
        for (final var b = put(ByteBuffer.allocate(BYTES)).flip(); b.hasRemaining(); ) {
            channel.write(b).get();
        }
        return channel;
    }

    @Override
    default <T extends AsynchronousByteChannel, A> void write(
            final T channel, final A attachment, final CompletionHandler<? super T, ? super A> handler) { // @formatter:off
        Objects.requireNonNull(channel, "channel is null");
        Objects.requireNonNull(handler, "handler is null");
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        channel.write(buffer, attachment, new CompletionHandler<>() {
            @Override public void completed(final Integer result, final A attachment) {
                if (!buffer.hasRemaining()) {
                    handler.completed(channel, attachment);
                    return;
                }
                channel.write(buffer, attachment, this);
            }
            @Override public void failed(final Throwable exc, final A attachment) {
                handler.failed(exc, attachment);
            }
        }); // @formatter:on
    }

    @Override
    default <T extends AsynchronousFileChannel> T write(final T channel, long position)
            throws InterruptedException, ExecutionException {
        for (final var b = put(ByteBuffer.allocate(BYTES)).flip(); b.hasRemaining(); ) {
            position += channel.write(b, position).get();
        }
        return channel;
    }

    @Override
    default <T extends AsynchronousFileChannel, A> void write(
            final T channel, final long position,
            final A attachment, final CompletionHandler<? super T, ? super A> handler) { // @formatter:off
        final var buffer = put(ByteBuffer.allocate(BYTES)).flip();
        channel.write(buffer, position, new AtomicLong(position), new CompletionHandler<>() {
            @Override
            public void completed(final Integer result, final AtomicLong cursor) {
                if (!buffer.hasRemaining()) {
                    handler.completed(channel, attachment);
                    return;
                }
                channel.write(buffer, cursor.addAndGet(result), cursor, this);
            }
            @Override
            public void failed(final Throwable exc, final AtomicLong cursor) {
                handler.failed(exc, attachment);
            }
        }); // @formatter:on
    }
}
