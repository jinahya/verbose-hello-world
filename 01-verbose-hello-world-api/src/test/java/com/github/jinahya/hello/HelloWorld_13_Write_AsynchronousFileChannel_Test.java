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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static java.lang.Long.MAX_VALUE;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.AsynchronousFileChannel.open;
import static java.nio.file.Files.createTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousFileChannel, long)} method.
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
        var service = service();
        // https://www.javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html#13
        doAnswer(i -> {
            var buffer = i.getArgument(0, ByteBuffer.class);
            buffer.position(buffer.position() + HelloWorld.BYTES);
            return buffer;
        }).when(service).put(any());
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method and writes the buffer to
     * the {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("write(channel)"
                 + " invokes put(buffer)"
                 + ", and writes the buffer to channel")
    @Test
    void write_InvokePutBufferWriteBufferToChannel_()
            throws InterruptedException, ExecutionException {
        var service = service();
        var writtenSoFar = new LongAdder();
        var channel = mock(AsynchronousFileChannel.class);
        when(channel.write(notNull(), longThat(a -> a >= 0L))).thenAnswer(i -> {
            var buffer = i.getArgument(0, ByteBuffer.class);
            var position = i.getArgument(1);
            var written = new Random().nextInt(buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            var future = mock(Future.class);
            doReturn(written).when(future).get();
            return future;
        });
        var position = 0L;
        var actual = service.write(channel, position);
        assertSame(channel, actual);
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(HelloWorld.BYTES, buffer.capacity());
        verify(channel, atLeast(1)).write(same(buffer), longThat(a -> a >= position));
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method {@link HelloWorld#BYTES} bytes starting at specified {@code position}.
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
    void write_Write12Bytes_(@TempDir Path tempDir)
            throws InterruptedException, ExecutionException, IOException {
        var service = service();
        var path = createTempFile(tempDir, null, null);
        long writePosition = new Random().nextLong() & 1024L;
        try (var channel = open(path, StandardOpenOption.WRITE)) {
            service.write(channel, writePosition);
            channel.force(false);
        }
        long readPosition = writePosition;
        try (var channel = open(path, StandardOpenOption.READ)) {
            for (var buffer = allocate(HelloWorld.BYTES); buffer.hasRemaining(); ) {
                readPosition += channel.read(buffer, readPosition).get();
            }
        }
        assertEquals(writePosition + HelloWorld.BYTES, readPosition);
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method returns the {@code channel} argument.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("write(channel, position) returns channel")
    @Test
    void write_ReturnChannel_()
            throws InterruptedException, ExecutionException {
        var service = service();
        var channel = mock(AsynchronousFileChannel.class);
        when(channel.write(notNull(), longThat(a -> a >= 0L))).thenAnswer(i -> {
            var buffer = i.getArgument(0, ByteBuffer.class);
            var position = i.getArgument(1);
            int written = buffer.remaining();
            buffer.position(buffer.position() + written);
            @SuppressWarnings({"unchecked"})
            Future<Integer> future = mock(Future.class);
            doReturn(written).when(future).get();
            return future;
        });
        var position = new Random().nextLong() & MAX_VALUE;
        var actual = service.write(channel, position);
        assertSame(channel, actual);
    }
}
