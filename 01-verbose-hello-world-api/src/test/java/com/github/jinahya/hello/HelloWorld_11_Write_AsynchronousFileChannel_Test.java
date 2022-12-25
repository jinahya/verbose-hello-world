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
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.longThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousFileChannel, long)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_11_Write_AsynchronousFileChannel_Arguments_Test
 */
@DisplayName("write(AsynchronousFileChannel, Executor) arguments")
@Slf4j
class HelloWorld_11_Write_AsynchronousFileChannel_Test extends HelloWorldTest {

    @BeforeEach
    @Override
    void stub_PutBuffer_IncreaseBufferPositionBy12() {
        super.stub_PutBuffer_IncreaseBufferPositionBy12();
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
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: AsynchronousByteChannel
        var channel = mock(AsynchronousFileChannel.class);
        var writtenSoFar = new LongAdder();
        lenient().when(channel.write(argThat(b -> b != null && b.hasRemaining()),
                                     longThat(p -> p >= 0L)))
                .thenAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    var position = i.getArgument(1, long.class);
                    var written = current().nextInt(buffer.remaining() + 1);
                    buffer.position(buffer.position() + written);
                    writtenSoFar.add(written);
                    var future = mock(Future.class);
                    lenient().doReturn(written).when(future).get();
                    return future;
                });
        // GIVEN: position
        var position = 0L;
        // WHEN
        service.write(channel, position);
        // THEN: once, put(buffer[12]) invoked
        verify(service).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertNotNull(buffer);
        assertEquals(BYTES, buffer.capacity());
        // THEN: at least once, channel.write(buffer, <position>) invoked
        // THEN: 12 bytes are written
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousFileChannel, long) write(channel, position)}
     * method returns specified {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("returns channel")
    @Test
    void _ReturnChannel_() throws InterruptedException, ExecutionException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: AsynchronousFileChannelChannel
        var channel = mock(AsynchronousFileChannel.class);
        lenient().when(channel.write(argThat(b -> b != null && b.hasRemaining()),
                                     longThat(p -> p >= 0L)))
                .thenAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    var position = i.getArgument(1, long.class);
                    var written = buffer.remaining();
                    buffer.position(buffer.position() + written);
                    var future = mock(Future.class);
                    lenient().doReturn(written).when(future).get();
                    return future;
                });
        // GIVEN: position
        var position = 0L;
        // WHEN
        var result = service.write(channel, position);
        // THEN: result is same as channel
        assertSame(channel, result);
    }
}
