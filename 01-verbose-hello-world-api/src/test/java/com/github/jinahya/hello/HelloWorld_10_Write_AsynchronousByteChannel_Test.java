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
import org.mockito.ArgumentMatchers;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.LongAdder;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(AsynchronousByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_10_Write_AsynchronousByteChannel_Arguments_Test
 */
@Slf4j
class HelloWorld_10_Write_AsynchronousByteChannel_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method and writes the buffer to the
     * {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("write(channel)"
                 + " invokes put(buffer)"
                 + " and writes the buffer to channel")
    @Test
    void write_InvokePutBufferWriteBufferToChannel_()
            throws InterruptedException, ExecutionException {
        var service = helloWorld();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        lenient().when(channel.write(ArgumentMatchers.notNull())).thenAnswer(i -> {
            var buffer = i.getArgument(0, ByteBuffer.class);
            var written = new Random().nextInt(buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            var future = mock(Future.class);
            doReturn(written).when(future).get();
            return future;
        });
        service.write(channel);
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(HelloWorld.BYTES, buffer.capacity());
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#write(AsynchronousByteChannel) write(channel)} method returns the
     * {@code channel} argument.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("write(channel) invokes put(buffer) writes the buffer to channel")
    @Test
    void write_ReturnChannel_() throws InterruptedException, ExecutionException {
        var service = helloWorld();
        var channel = mock(AsynchronousByteChannel.class);
        lenient()
                .when(channel.write(any(ByteBuffer.class)))
                .thenAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    var written = new Random().nextInt(buffer.remaining() + 1);
                    buffer.position(buffer.position() + written);
                    return completedFuture(written);
                });
        var actual = service.write(channel);
        assertSame(channel, actual);
    }
}
