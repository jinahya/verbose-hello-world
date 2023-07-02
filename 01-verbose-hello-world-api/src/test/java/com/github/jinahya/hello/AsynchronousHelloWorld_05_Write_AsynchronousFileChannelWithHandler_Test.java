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
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Executor) write(channel,
 * position, handler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_05_Write_AsynchronousFileChannelWithHandler_Arguments_Test
 */
@DisplayName("write(channel, position, handler)")
@Slf4j
class AsynchronousHelloWorld_05_Write_AsynchronousFileChannelWithHandler_Test
        extends _AsynchronousHelloWorldTest {

    /**
     * Stubs {@link HelloWorld#put(ByteBuffer) put(buffer)} method to just return the {@code buffer}
     * as its {@link ByteBuffer#position() position} increased by {@value HelloWorld#BYTES}.
     */
    @DisplayName("[stubbing] put(buffer) returns buffer as its position increased by 12")
    @org.junit.jupiter.api.BeforeEach
    void stub_ReturnBufferAsItsPositionIncreasedBy12_PutBuffer() {
        // GIVEN
        var service = serviceInstance();
        // WHEN/THEN
        doAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            assert buffer != null;
            assert buffer.capacity() == BYTES;
            assert buffer.remaining() == buffer.capacity();
            buffer.position(buffer.limit());
            return buffer;
        }).when(service).put(any());
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, CompletionHandler)
     * write(channel, position, handler)} method invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(12, channel)} when every
     * {@link AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * channel.write(s, p, a, h)} invocation invokes
     * {@link CompletionHandler#completed(Object, Object) h.completed(result, a)}.
     */
    @DisplayName("-> handler.completed(12, channel)")
    @Test
    void _Completed_() {
        // GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        var writtenSoFar = new LongAdder();
        var firstPosition = new AtomicReference<Long>();
        doAnswer(i -> {
            ByteBuffer src = i.getArgument(0);
            assert src != null && src.hasRemaining();
            long position = i.getArgument(1);
            firstPosition.compareAndSet(null, position);
            assert position == firstPosition.get() + src.position();
            var attachment = i.getArgument(2);
            CompletionHandler<Integer, Object> handler = i.getArgument(3);
            var written = current().nextInt(src.remaining() + 1);
            src.position(src.position() + written);
            handler.completed(written, attachment);
            writtenSoFar.add(written);
            return null;
        }).when(channel).write(any(), anyLong(), any(), any());
        CompletionHandler<Integer, AsynchronousFileChannel> handler = mock(CompletionHandler.class);
        var position = 0L;
        // WHEN
        service.write(channel, position, handler);
        // THEN: once, put(buffer[12]) invoked
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        // THEN: once, in time, handler.completed(12, channel) invoked.
        verify(handler, timeout(SECONDS.toMillis(4L)).times(1)).completed(BYTES, channel);
        // THEN: at least once, channel.write(buffer, >= position, <whatever>, <whatever>) invoked
        verify(channel, atLeastOnce()).write(same(buffer), anyLong(), any(), any());
        // THEN: 12 bytes written to the channel
        assertEquals(BYTES, writtenSoFar.intValue());
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, CompletionHandler)
     * write(channel, position, handler)} method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, channel)} when
     * {@link AsynchronousFileChannel#write(ByteBuffer, long, Object, CompletionHandler)
     * channel.write(s, p, a, h)} invokes {@code h.failed(exc, channel)}.
     */
    @DisplayName("-> handler.failed(exc, channel)")
    @Test
    void _Failed_() {
        // GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        var exc = mock(Throwable.class);
        doAnswer(i -> {
            var attachment = i.getArgument(2);
            CompletionHandler<Integer, Object> handler = i.getArgument(3);
            handler.failed(exc, attachment);
            return null;
        }).when(channel).write(any(), anyLong(), any(), any());
        CompletionHandler<Integer, AsynchronousFileChannel> handler = mock(CompletionHandler.class);
        var position = 0L;
        // WHEN
        service.write(channel, position, handler);
        // THEN: once, put(buffer[12]) invoked
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        // THEN: once, in time, handler.failed(exc, channel) invoked.
        verify(handler, timeout(SECONDS.toMillis(4L)).times(1)).failed(exc, channel);
    }
}
