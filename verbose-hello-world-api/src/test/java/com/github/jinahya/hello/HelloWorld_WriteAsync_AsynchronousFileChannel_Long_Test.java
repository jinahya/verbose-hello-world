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
import org.mockito.ArgumentCaptor;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.LongAdder;

import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#writeAsync(AsynchronousFileChannel, long)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_WriteAsync_AsynchronousFileChannel_Long_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousFileChannel, long)} method throws a {@link NullPointerException}
     * when {@code channel} argument is {@code null}.
     */
    @DisplayName("writeAsync(channel, position) throws NullPointerException when channel is null")
    @Test
    void writeAsync_NullPointerException_ChannelIsNull() {
        assertThrows(NullPointerException.class,
                     () -> helloWorld.writeAsync((AsynchronousFileChannel) null, 0L));
    }

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousFileChannel, long)} method throws an {@link
     * IllegalArgumentException} when {@code position} argument is negative.
     */
    @DisplayName("writeAsync(channel, position) throws IllegalArgumentException when position is negative")
    @Test
    void writeAsync_IllegalArgumentException_PositionIsNegative() {
        assertThrows(NullPointerException.class,
                     () -> helloWorld.writeAsync((AsynchronousFileChannel) null, 0L, mock(ExecutorService.class)));
    }

    /**
     * Asserts {@link HelloWorld#writeAsync(AsynchronousFileChannel, long)} method invokes {@link
     * HelloWorld#put(ByteBuffer)} and writes the buffer to {@code channel}.
     */
    @DisplayName("write(channel, long) invokes put(buffer) writes the buffer to channel")
    @Test
    void writeAsync_InvokePutBufferWriteBufferToChannel_() {
        final AsynchronousFileChannel channel = mock(AsynchronousFileChannel.class);
        final LongAdder writtenSoFar = new LongAdder();
        doAnswer(i -> {
            final ByteBuffer src = i.getArgument(0);
            final long position = i.getArgument(1);
            final Object attachment = i.getArgument(2);
            final CompletionHandler<Integer, Object> handler = i.getArgument(3);
            final int written = current().nextInt(0, src.remaining() + 1);
            src.position(src.position() + written);
            writtenSoFar.add(written);
            handler.completed(written, attachment);
            return null;
        }).when(channel).write(any(ByteBuffer.class), anyLong(), any(), any());
        final CompletableFuture<Void> future = helloWorld.writeAsync(channel, 0L);
        final ArgumentCaptor<ByteBuffer> bufferCaptor = forClass(ByteBuffer.class);
        verify(helloWorld, times(1)).put(bufferCaptor.capture());
        final ByteBuffer buffer = bufferCaptor.getValue();
    }
}
