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

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Executor) write(channel,
 * position, executor)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_04_Write_AsynchronousFileChannelWithExecutor_Arguments_Test
 */
@DisplayName("write(channel, position, executor")
@Slf4j
class AsynchronousHelloWorld_04_Write_AsynchronousFileChannelWithExecutor_Test
        extends AsynchronousHelloWorldTest {

    /**
     * Stubs {@link HelloWorld#put(ByteBuffer) put(buffer)} method to just return the {@code buffer}
     * as its {@link ByteBuffer#position() position} increased by {@value HelloWorld#BYTES}.
     */
    @DisplayName("[stubbing] put(buffer) returns buffer as its position increased by 12")
    @BeforeEach
    void stub_IncreaseBufferPositionBy12_PutBuffer() {
        // GIVEN
        var service = service();
        // WHEN/THEN
        doAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            buffer.position(buffer.position() + BYTES);
            return buffer;
        }).when(service).put(any());
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Executor) write(channel,
     * position, executor)} method returns a future of {@code channel} which invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to the {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("-> put(buffer[12]) -> channel.write(buffer, ge(position))+")
    @Test
    void __() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousFileChannel.class);
        var writtenSoFar = new LongAdder();
        var firstPositionRef = new AtomicReference<Long>();
        when(channel.write(any(), anyLong())).thenAnswer(i -> {
            ByteBuffer src = i.getArgument(0);
            assert src != null && src.hasRemaining();
            long position = i.getArgument(1);
            Long firstPosition = firstPositionRef.getAndSet(position);
            assert firstPosition == null || position == firstPosition + src.position();
            var written = current().nextInt(src.remaining() + 1);
            src.position(src.position() + written);
            writtenSoFar.add(written);
            var future = mock(Future.class);
            when(future.get()).thenReturn(written);
            return future;
        });
        var executor = mock(Executor.class);
        doAnswer(i -> {
            Runnable runnable = i.getArgument(0);
            new Thread(runnable).start();
            return null;
        }).when(executor).execute(any());
        var position = 0L;
        // WHEN
        var future = service.write(channel, position, executor);
        // THEN: once, executor.execute(Runnable)
        verify(executor, times(1)).execute(notNull());
        var result = future.get();
        // THEN: put(buffer[12]) invoked
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        // THEN: at least once, channel.write(buffer, >= position) invoked
        // TODO: Verify, at least once, channel.write(buffer, position) invoked
        // THEN: 12 bytes are written
        // TODO: Asserts writtenSoFar.intValue() is equal to BYTES
        // THEN: result is same as channel
        assertSame(channel, result);
    }
}
