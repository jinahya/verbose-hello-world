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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link AsynchronousHelloWorld#writeSync2(AsynchronousByteChannel)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_02_WriteSync2_AsynchronousByteChannel_Arguments_Test
 */
@Slf4j
class AsynchronousHelloWorld_02_WriteSync2_AsynchronousByteChannel_Testa
        extends AsynchronousHelloWorldTest {

    /**
     * Asserts {@link AsynchronousHelloWorld#writeSync2(AsynchronousByteChannel)}
     * writeSync2(channel)} method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method,
     * and writes the buffer to the {@code channel}.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeSync2(channel)"
                 + " invokes put(buffer)"
                 + ", and writes the buffer to channel")
    @Test
    @SuppressWarnings({"unchecked"})
    void _PutBufferWriteBuffer_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        lenient().
                doAnswer(i -> {
                    var buffer = i.getArgument(0, ByteBuffer.class);
                    var attachment = i.getArgument(1);
                    var handler = i.getArgument(2, CompletionHandler.class);
                    var written = current().nextInt(buffer.remaining());
                    writtenSoFar.add(written);
                    buffer.position(buffer.position() + written);
                    handler.completed(written, attachment);
                    return null;
                })
                .when(channel)
//                .write(any(ByteBuffer.class), any(), any(CompletionHandler.class));
                .write(any(), any(), any());
        // WHEN
        service.writeSync2(channel);
        // THEN: put(buffer[12]) invoked
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        // THEN: channel.write(buffer, attachment, handler) invoked at least once
        // THEN: 12 bytes are written to the channel
    }

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#writeSync2(AsynchronousByteChannel) writeSync2(channel)} method
     * returns the {@code channel} argument.
     *
     * @throws InterruptedException if interrupted while testing.
     * @throws ExecutionException   if failed to execute.
     */
    @DisplayName("writeSync2(channel) returns channel")
    @Test
    @SuppressWarnings({"unchecked"})
    void _ReturnChannel_() throws InterruptedException, ExecutionException {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        lenient().
                doAnswer(i -> {
                    var buffer = i.getArgument(0);
                    var attachment = i.getArgument(1);
                    var handler = i.getArgument(2, CompletionHandler.class);
                    if (current().nextBoolean()) {
                        handler.completed(null, attachment);
                    } else {
                        handler.failed(new Throwable(), attachment);
                    }
                    return null;
                })
                .when(channel)
                .write(any(ByteBuffer.class), any(), any(CompletionHandler.class));
        // WHEN
        var actual = service.writeSync2(channel);
        // THEN
        assertSame(channel, actual);
    }
}