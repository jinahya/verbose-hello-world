package com.github.jinahya.hello;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Long.MAX_VALUE;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.AsynchronousFileChannel.open;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousFileChannel, long)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_13_Write_AsynchronousFileChannel_Arguments_Test
 */
@Slf4j
class HelloWorld_13_Write_AsynchronousFileChannel_Test
        extends HelloWorldTest {

    // TODO: Remove this stubbing method when you implemented the put(buffer) method!
    @BeforeEach
    void stubPutBuffer() {
        // https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13
        doAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0);
            buffer.position(buffer.position() + BYTES);
            return buffer;
        }).when(helloWorld()).put(ArgumentMatchers.any());
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long)
     * write(channel, position)} method invokes {@link HelloWorld#put(ByteBuffer)
     * put(buffer)} method and writes the buffer to the {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("write(channel)"
                 + " invokes put(buffer)"
                 + " and writes the buffer to channel")
    @Test
    void write_InvokePutBufferWriteBufferToChannel_()
            throws InterruptedException, ExecutionException {
        final LongAdder writtenSoFar = new LongAdder();
        final AsynchronousFileChannel channel
                = mock(AsynchronousFileChannel.class);
        Mockito.when(channel.write(notNull(), longThat(a -> a >= 0L)))
                .thenAnswer(i -> {
                    final ByteBuffer buffer = i.getArgument(0);
                    final long position = i.getArgument(1);
                    final int written
                            = new Random().nextInt(buffer.remaining() + 1);
                    buffer.position(buffer.position() + written);
                    writtenSoFar.add(written);
                    @SuppressWarnings({"unchecked"})
                    final Future<Integer> future = mock(Future.class);
                    doReturn(written).when(future).get();
                    return future;
                });
        final long position = 0L;
        final AsynchronousFileChannel actual
                = helloWorld().write(channel, position);
        assertSame(channel, actual);
        verify(helloWorld(), times(1)).put(bufferCaptor().capture());
        final ByteBuffer buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        verify(channel, atLeast(1))
                .write(same(buffer), longThat(a -> a >= position));
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long)
     * write(channel, position)} method {@link HelloWorld#BYTES} bytes starting
     * at specified {@code position}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     * @throws IOException          when an I/O error occurs.
     */
    @DisplayName("write(channel)"
                 + " invokes put(buffer)"
                 + " and writes the buffer to channel")
    @Test
    void write_Write12Bytes_(@TempDir final Path tempDir)
            throws InterruptedException, ExecutionException, IOException {
        final Path path = createTempFile(tempDir, null, null);
        final long writePosition = new Random().nextLong() & 1024L;
        try (AsynchronousFileChannel channel = open(path, WRITE)) {
            helloWorld().write(channel, writePosition);
            channel.force(false);
        }
        long readPosition = writePosition;
        try (AsynchronousFileChannel channel = open(path, READ)) {
            final ByteBuffer buffer = allocate(BYTES);
            while (buffer.hasRemaining()) {
                readPosition += channel.read(buffer, readPosition).get();
            }
        }
        assertEquals(writePosition + BYTES, readPosition);
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long)
     * write(channel, position)} method returns the {@code channel} argument.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("write(channel, position) returns channel")
    @Test
    void write_ReturnChannel_()
            throws InterruptedException, ExecutionException {
        final AsynchronousFileChannel channel
                = mock(AsynchronousFileChannel.class);
        Mockito.when(channel.write(notNull(), longThat(a -> a >= 0L)))
                .thenAnswer(i -> {
                    final ByteBuffer buffer = i.getArgument(0);
                    final long position = i.getArgument(1);
                    final int written = buffer.remaining();
                    buffer.position(buffer.position() + written);
                    @SuppressWarnings({"unchecked"})
                    final Future<Integer> future = mock(Future.class);
                    doReturn(written).when(future).get();
                    return future;
                });
        final long position = new Random().nextLong() & MAX_VALUE;
        final AsynchronousFileChannel actual
                = helloWorld().write(channel, position);
        assertSame(channel, actual);
    }
}