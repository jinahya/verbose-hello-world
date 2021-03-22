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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static java.nio.ByteBuffer.allocate;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_WriteAsync_AsynchronousFileChannel_Long_ExecutorService_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)} method throws a {@link
     * NullPointerException} when {@code channel} argument is {@code null}.
     */
    @DisplayName("writeAsync(channel, position, service) throws NullPointerException when channel is null")
    @Test
    void writeAsync_NullPointerException_ChannelIsNull() {
        assertThrows(NullPointerException.class,
                     () -> helloWorld.writeAsync((AsynchronousFileChannel) null, 0L, mock(ExecutorService.class)));
    }

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)} method throws an {@link
     * IllegalArgumentException} when {@code position} argument is negative.
     */
    @DisplayName("writeAsync(channel, position, service) throws IllegalArgumentException when position is negative")
    @Test
    void writeAsync_IllegalArgumentException_PositionIsNegative() {
        assertThrows(NullPointerException.class,
                     () -> helloWorld.writeAsync((AsynchronousFileChannel) null, 0L, mock(ExecutorService.class)));
    }

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)} method throws a {@link
     * NullPointerException} when {@code service} argument is {@code null}.
     */
    @DisplayName("writeAsync(channel, position, service) throws NullPointerException when channel is null")
    @Test
    void writeAsync_NullPointerException_ServiceIsNull() {
        assertThrows(NullPointerException.class,
                     () -> helloWorld.writeAsync(mock(AsynchronousFileChannel.class), 0L, null));
    }

    void stubWriteChannelPositionFor(final AsynchronousFileChannel channel)
            throws IOException, InterruptedException, ExecutionException {
        doAnswer(i -> {
            assertSame(channel, i.getArgument(0, AsynchronousFileChannel.class));
            long position = i.getArgument(1, Long.class);
            for (final ByteBuffer src = allocate(HelloWorld.BYTES); src.hasRemaining(); ) {
                position += channel.write(src, position).get();
            }
            return null;
        })
                .when(helloWorld)
                .write(same(channel), anyLong());
    }

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, ExecutorService)} method invokes {@link
     * HelloWorld#put(ByteBuffer)} and writes the buffer to {@code channel}.
     *
     * @throws IOException          if an I/O error occurs.
     * @throws ExecutionException   if failed to work.
     * @throws InterruptedException if interrupted while executing.
     */
    @DisplayName("write(channel, service) invokes put(buffer) writes the buffer to channel")
    @Test
    void writeAsync_InvokePutBufferWriteBufferToChannel_()
            throws IOException, ExecutionException, InterruptedException {
        final AsynchronousFileChannel channel = mock(AsynchronousFileChannel.class);
        stubWriteChannelPositionFor(channel);
        final LongAdder writtenSoFar = new LongAdder();
        when(channel.write(any(ByteBuffer.class), anyLong())).thenAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0, ByteBuffer.class);
            final int written = current().nextInt(0, buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            @SuppressWarnings({"unchecked"})
            final Future<Integer> future = mock(Future.class);
            when(future.get()).thenReturn(written);
            return future;
        });
        final Future<Void> future = helloWorld.writeAsync(channel, 0L, newSingleThreadExecutor());
        assertNotNull(future);
        final Void got = future.get();
        assertNull(got);
        assertEquals(HelloWorld.BYTES, writtenSoFar.sum());
    }

    @Test
    void writeAsync_InterruptedException_() throws IOException, ExecutionException, InterruptedException {
        final AsynchronousFileChannel channel = mock(AsynchronousFileChannel.class);
        stubWriteChannelPositionFor(channel);
        when(channel.write(any(ByteBuffer.class), anyLong())).thenAnswer(i -> {
            final ByteBuffer src = i.getArgument(0, ByteBuffer.class);
            src.position(src.limit());
            @SuppressWarnings({"unchecked"})
            final Future<Integer> future = mock(Future.class);
            when(future.get()).thenThrow(new InterruptedException());
            return future;
        });
        final Future<Void> future = helloWorld.writeAsync(channel, 0L, newSingleThreadExecutor());
        assertNotNull(future);
        final InterruptedException ie = assertThrows(InterruptedException.class, future::get);
    }

    @Test
    void writeAsync_ExecutionException_() throws IOException, ExecutionException, InterruptedException {
        final AsynchronousFileChannel channel = mock(AsynchronousFileChannel.class);
        stubWriteChannelPositionFor(channel);
        when(channel.write(any(ByteBuffer.class), anyLong())).thenAnswer(i -> {
            final ByteBuffer src = i.getArgument(0, ByteBuffer.class);
            src.position(src.limit());
            @SuppressWarnings({"unchecked"})
            final Future<Integer> future = mock(Future.class);
            when(future.get()).thenThrow(new ExecutionException(new RuntimeException()));
            return future;
        });
        final Future<Void> future = helloWorld.writeAsync(channel, 0L, newSingleThreadExecutor());
        assertNotNull(future);
        final ExecutionException ee = assertThrows(ExecutionException.class, future::get);
    }
}
