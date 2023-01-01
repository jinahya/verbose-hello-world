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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.HelloWorldTestUtils.print;
import static java.nio.ByteBuffer.allocate;
import static java.nio.channels.AsynchronousFileChannel.open;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.size;
import static java.nio.file.StandardOpenOption.WRITE;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_11_Write_AsynchronousFileChannel_Arguments_Test
 */
@DisplayName("write(channel, position)")
@Slf4j
class HelloWorld_11_Write_AsynchronousFileChannel_Test extends HelloWorldTest {

    /**
     * Stubs the {@link HelloWorld#put(ByteBuffer) put(buffer)} method to just return the
     * {@code buffer} as its {@code position} is increased by {@value HelloWorld#BYTES}.
     */
    @DisplayName("[stubbing] put(buffer[12]) returns buffer as its position increased by 12")
    @org.junit.jupiter.api.BeforeEach
    void stub_ReturnBufferPositionIncreaseBy12_PutBuffer() {
        doAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            assert buffer != null;
            print(buffer);
            assert buffer.capacity() == BYTES;
            assert buffer.limit() == buffer.capacity();
            assert buffer.remaining() == BYTES;
            buffer.position(buffer.limit());
            return buffer;
        }).when(service()).put(any());
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to specified {@code channel}, starting
     * at {@code position}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("-> put(buffer[12]) -> channel.write(buffer)+")
    @Test
    void _PutBufferWriteBufferToChannel_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousFileChannel.class);
        var writtenSoFar = new LongAdder();
        var firstPosition = new AtomicReference<Long>();
        when(channel.write(any(), anyLong())).thenAnswer(i -> {
            ByteBuffer src = i.getArgument(0);
            assert src.hasRemaining();
            long position = i.getArgument(1);
            firstPosition.compareAndSet(null, position);
            assert position == firstPosition.get() + src.position();
            var written = current().nextInt(src.remaining() + 1);
            src.position(src.position() + written);
            writtenSoFar.add(written);
            var future = mock(Future.class);
            when(future.get()).thenReturn(written);
            return future;
        });
        var position = 0L;
        // WHEN
        service.write(channel, position);
        // THEN: once, put(buffer[12]) invoked
        verify(service).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        // THEN: at least once, channel.write(buffer, >= position) invoked
        // THEN: 12 bytes are written
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method returns {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("returns channel")
    @Test
    void _ReturnChannel_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousFileChannel.class);
        when(channel.write(any(), anyLong())).thenAnswer(i -> {
            ByteBuffer src = i.getArgument(0);
            long position = i.getArgument(1);
            var written = src.remaining();
            src.position(src.position() + written);
            var future = mock(Future.class);
            when(future.get()).thenReturn(written);
            return future;
        });
        var position = 0L;
        // WHEN
        var result = service.write(channel, position);
        // THEN: result is same as channel
        assertSame(channel, result);
    }

    @org.junit.jupiter.api.Disabled("enable when implemented")
    @Test
    @畵蛇添足
    void _12BytesAreWritten_(@TempDir Path dir)
            throws IOException, InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var path = createTempFile(dir, null, null);
        try (var channel = FileChannel.open(path, WRITE)) {
            for (var buffer = allocate(current().nextInt(1024)); buffer.hasRemaining(); ) {
                channel.write(buffer);
            }
            channel.force(false);
        }
        var position = current().nextLong(1024L);
        try (var channel = open(path, WRITE)) {
            service.write(channel, position);
            channel.force(false);
        }
        assertTrue(size(path) >= position + BYTES);
    }
}
