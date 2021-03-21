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

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * A class for testing {@link HelloWorld#writeAsync(AsynchronousByteChannel, ExecutorService)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_WriteAsyncAsynchronousByteChannelExecutorServiceTest extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousByteChannel, ExecutorService)} throws a {@link
     * NullPointerException} when {@code channel} argument is {@code null}.
     */
    @DisplayName("writeSync(channel, service) throws NullPointerException when channel is null")
    @Test
    void writeAsync_NullPointerException_ChannelIsNull() {
        assertThrows(NullPointerException.class,
                     () -> helloWorld.writeAsync((AsynchronousByteChannel) null, mock(ExecutorService.class)));
    }

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousByteChannel, ExecutorService)} throws a {@link
     * NullPointerException} when {@code service} argument is {@code null}.
     */
    @DisplayName("writeSync(channel, service) throws NullPointerException when channel is null")
    @Test
    void writeAsync_NullPointerException_ServiceIsNull() {
        assertThrows(NullPointerException.class,
                     () -> helloWorld.writeAsync(mock(AsynchronousByteChannel.class), null));
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel)} invokes {@link HelloWorld#put(ByteBuffer)} and
     * writes the buffer to {@code channel}.
     *
     * @throws ExecutionException   if failed to work.
     * @throws InterruptedException if interrupted while executing.
     */
    @DisplayName("write(channel, service) invokes put(buffer) writes the buffer to channel")
    @Test
    void writeAsync_InvokePutBufferWriteBufferToChannel_() throws ExecutionException, InterruptedException {
        final AsynchronousByteChannel channel = mock(AsynchronousByteChannel.class);
        final LongAdder writtenSoFar = new LongAdder();
        when(channel.write(any(ByteBuffer.class))).thenAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0, ByteBuffer.class);
            final int written = current().nextInt(0, buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            @SuppressWarnings({"unchecked"})
            final Future<Integer> future = mock(Future.class);
            when(future.get()).thenReturn(written);
            return future;
        });
        doAnswer(i -> {
            for (final ByteBuffer b = allocate(HelloWorld.BYTES); b.hasRemaining(); ) {
                channel.write(b);
            }
            return null;
        })
                .when(helloWorld)
                .write(channel);
        final Future<Void> future = helloWorld.writeAsync(channel, newSingleThreadExecutor());
        assertNotNull(future);
        final Void got = future.get();
        assertNull(got);
        assertEquals(HelloWorld.BYTES, writtenSoFar.sum());
    }

    @Test
    void writeAsync_InterruptedException_() throws ExecutionException, InterruptedException {
        final AsynchronousByteChannel channel = mock(AsynchronousByteChannel.class);
        final LongAdder writtenSoFar = new LongAdder();
        when(channel.write(any(ByteBuffer.class))).thenAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0, ByteBuffer.class);
            final int written = current().nextInt(0, buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            @SuppressWarnings({"unchecked"})
            final Future<Integer> future = mock(Future.class);
            when(future.get()).thenThrow(new InterruptedException());
            return future;
        });
        doAnswer(i -> {
            for (final ByteBuffer b = allocate(HelloWorld.BYTES); b.hasRemaining(); ) {
                channel.write(b).get();
            }
            return null;
        })
                .when(helloWorld)
                .write(channel);
        final Future<Void> future = helloWorld.writeAsync(channel, newSingleThreadExecutor());
        assertNotNull(future);
        final InterruptedException ie = assertThrows(InterruptedException.class, future::get);
    }

    @Test
    void writeAsync_ExecutionException_() throws ExecutionException, InterruptedException {
        final AsynchronousByteChannel channel = mock(AsynchronousByteChannel.class);
        final LongAdder writtenSoFar = new LongAdder();
        when(channel.write(any(ByteBuffer.class))).thenAnswer(i -> {
            final ByteBuffer buffer = i.getArgument(0, ByteBuffer.class);
            final int written = current().nextInt(0, buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            @SuppressWarnings({"unchecked"})
            final Future<Integer> future = mock(Future.class);
            when(future.get()).thenThrow(new ExecutionException(new RuntimeException()));
            return future;
        });
        doAnswer(i -> {
            for (final ByteBuffer b = allocate(HelloWorld.BYTES); b.hasRemaining(); ) {
                channel.write(b).get();
            }
            return null;
        })
                .when(helloWorld)
                .write(channel);
        final Future<Void> future = helloWorld.writeAsync(channel, newSingleThreadExecutor());
        assertNotNull(future);
        final ExecutionException ee = assertThrows(ExecutionException.class, future::get);
    }
}
