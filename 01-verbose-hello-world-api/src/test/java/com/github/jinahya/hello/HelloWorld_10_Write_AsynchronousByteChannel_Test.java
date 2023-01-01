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
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static com.github.jinahya.hello.HelloWorldTestUtils.print;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_10_Write_AsynchronousByteChannel_Arguments_Test
 */
@DisplayName("write(channel)")
@Slf4j
class HelloWorld_10_Write_AsynchronousByteChannel_Test extends HelloWorldTest {

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
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a buffer of
     * {@value HelloWorld#BYTES} bytes, and writes the buffer to specified {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("-> put(buffer[12]) -> channel.write(buffer)+")
    @Test
    void _PutBufferWriteBufferToChannel_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        when(channel.write(any())).thenAnswer(i -> {
            ByteBuffer src = i.getArgument(0);
            assert src != null;
            assert src.hasRemaining();
            var written = current().nextInt(src.remaining() + 1);
            src.position(src.position() + written);
            writtenSoFar.add(written);
            var future = mock(Future.class);
            when(future.get()).thenReturn(written);
            return future;
        });
        // WHEN
        service.write(channel);
        // THEN: once, put(buffer[12]) invoked
        verify(service).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        // THEN: at least once, channel.write(buffer) invoked
        // TODO: Verify channel.write(buffer) invoked, at least once
        // THEN: 12 bytes are written
        // TODO: Assert writtenSoFar.intValue() is euqal to BYTES
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method returns
     * {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("returns channel")
    @Test
    void _ReturnChannel_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        when(channel.write(any())).thenAnswer(i -> {
            ByteBuffer src = i.getArgument(0);
            var written = src.remaining();
            src.position(src.position() + written);
            var future = mock(Future.class);
            when(future.get()).thenReturn(written);
            return future;
        });
        // WHEN
        var result = service.write(channel);
        // THEN: result is same as channel
        assertSame(channel, result);
    }
}
