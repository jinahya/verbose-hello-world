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
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link AsynchronousHelloWorld#write(AsynchronousByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_01_Write_AsynchronousByteChannel_Arguments_Test
 */
@Slf4j
class AsynchronousHelloWorld_01_Write_AsynchronousByteChannel_Test
        extends AsynchronousHelloWorldTest {

    /**
     * Asserts {@link AsynchronousHelloWorld#write(AsynchronousByteChannel)}
     * writeSync1(channel)} method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method,
     * and writes the buffer to the {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("write(channel)"
                 + " invokes put(buffer)"
                 + ", and writes the buffer to channel")
    @Test
    void _PutBufferWriteBuffer_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        lenient().when(channel.write(notNull())).thenAnswer(i -> {
            var buffer = i.getArgument(0, ByteBuffer.class);
            var written = new Random().nextInt(buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            var future = mock(Future.class);
            doReturn(written).when(future).get();
            return future;
        });
        // WHEN
        service.write(channel);
        // THEN: put(buffer[12]) invoked
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        // THEN: channel.write(buffer) invoked at least once
        // THEN: 12 bytes are written
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel) writeSync1(channel)} method
     * returns the {@code channel} argument.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("write(channel) returns channel")
    @Test
    void _ReturnChannel_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        lenient()
                .when(channel.write(any(ByteBuffer.class)))
                .thenAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    var written = buffer.remaining();
                    buffer.position(buffer.limit());
                    return completedFuture(written);
                });
        // WHEN
        var actual = service.write(channel);
        // THEN
        assertSame(channel, actual);
    }
}
