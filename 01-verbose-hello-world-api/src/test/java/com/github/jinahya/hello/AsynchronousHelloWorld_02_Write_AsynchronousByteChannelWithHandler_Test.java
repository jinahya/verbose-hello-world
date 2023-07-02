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
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.HelloWorldTestUtils.print;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, CompletionHandler, Object)
 * write(channel, handler, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_02_Write_AsynchronousByteChannelWithHandler_Arguments_Test
 */
@DisplayName("write(channel, handler, attachment)")
@Slf4j
class AsynchronousHelloWorld_02_Write_AsynchronousByteChannelWithHandler_Test
        extends AsynchronousHelloWorldTest {

    /**
     * Stubs the {@link HelloWorld#put(ByteBuffer) put(buffer)} method to just return the
     * {@code buffer} as its {@code position} increased by {@value HelloWorld#BYTES}.
     */
    @DisplayName("[stubbing] put(buffer[12]) returns buffer as its position increased by BYTES")
    @org.junit.jupiter.api.BeforeEach
    void stub_ReturnBufferAsItsPositionIncreaseBy12_PutBuffer() {
        willAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            assert buffer != null;
            print(buffer);
            assert buffer.capacity() == BYTES;
            assert buffer.limit() == buffer.capacity();
            assert buffer.remaining() == BYTES;
            buffer.position(buffer.limit());
            return buffer;
        }).given(service()).put(any());
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, CompletionHandler, Object)
     * write(channel, handler, attachment)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a buffer of
     * {@value HelloWorld#BYTES} bytes, writes the buffer to the {@code channel}, and invokes
     * {@link CompletionHandler#completed(Object, Object)} method, on {@code handler}, with
     * {@value HelloWorld#BYTES} and {@code channel}.
     */
    @DisplayName("-> put(buffer[12]) -> handler(12, channel)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Completed_() {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        willAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            assert buffer.hasRemaining();
            var attachment = i.getArgument(1);
            var handler = i.getArgument(2, CompletionHandler.class);
            var written = current().nextInt(buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            handler.completed(written, attachment);
            return null;
        }).given(channel).write(any(), any(), any());
        CompletionHandler<AsynchronousByteChannel, Void> handler = mock(CompletionHandler.class);
        // WHEN
        service.write(channel, handler, null);
        // THEN: once, put(buffer[12]) invoked
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        assertEquals(buffer.capacity(), buffer.limit());
        // THEN: once in timeout, handler.completed(12, channel) invoked
        // THEN: at least once, channel.write(buffer, <whatever>, <whatever>) invoked
        // THEN: 12 bytes are written to the channel
    }
}
