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
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, CompletionHandler) method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see AsynchronousHelloWorld_02_Write_AsynchronousByteChannelWithHandler_Arguments_Test
 */
@DisplayName("write(AsynchronousByteChannel, CompletionHandler) arguments")
@Slf4j
class AsynchronousHelloWorld_02_Write_AsynchronousByteChannelWithHandler_Test
        extends AsynchronousHelloWorldTest {

    /**
     * Asserts
     * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, CompletionHandler)
     * write(channel, handler)} method invokes {@link HelloWorld#put(ByteBuffer) put(buffer)} method
     * with a buffer of {@value HelloWorld#BYTES} bytes, writes the buffer to the {@code channel},
     * and invokes {@link CompletionHandler#completed(Object, Object)} method, on {@code handler},
     * with {@value HelloWorld#BYTES} and {@code channel}.
     */
    @DisplayName("put(buffer[12]) -> handler(12, channel)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Completed_() {
        // GIVEN
        var service = service();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        doAnswer(i -> {
            var buffer = i.getArgument(0, ByteBuffer.class);
            assert buffer.hasRemaining();
            var attachment = i.getArgument(1);
            var handler = i.getArgument(2, CompletionHandler.class);
            var written = current().nextInt(buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            handler.completed(written, attachment);
            return null;
        }).when(channel).write(any(), any(), any());
        CompletionHandler<Integer, AsynchronousByteChannel> handler = mock(CompletionHandler.class);
        // WHEN
        service.write(channel, handler);
        // THEN: once, put(buffer[12]) invoked
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertEquals(BYTES, buffer.capacity());
        // THEN: once with timeout, handler.completed(12, channel) invoked
        // THEN: at least once, channel.write(buffer, attachment, handler) invoked
        // THEN: 12 bytes are written to the channel
    }
}
