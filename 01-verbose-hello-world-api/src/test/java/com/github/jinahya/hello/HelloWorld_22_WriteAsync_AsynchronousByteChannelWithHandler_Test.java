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
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing
 * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler, Object)
 * writeAsync(channel, handler, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_22_WriteAsync_AsynchronousByteChannelWithHandler_Arguments_Test
 */
@DisplayName("write(channel, handler, attachment)")
@Slf4j
class HelloWorld_22_WriteAsync_AsynchronousByteChannelWithHandler_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        _stub_PutBuffer_ToReturnTheBuffer_AsItsPositionIncreasedBy12();
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler, Object)
     * writeAsync(channel, handler, attachment)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer[12])}, continuously invokes
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     * channel.write(buffer, attachment, a-handler)}, and eventually invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(Object, Object)
     * handler.completed(channel, attachment)}.
     */
    @DisplayName("""
            (channel, handler, attachment)
            -> put(buffer[12])
            -> handler.completed(channel, attachment)""")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Completed_() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        _stub_ToComplete(channel, writtenSoFar);
        CompletionHandler<AsynchronousByteChannel, Object> handler = mock(CompletionHandler.class);
        var attachment = current().nextBoolean() ? null : new Object();
        // ------------------------------------------------------------------------------------ WHEN
        service.writeAsync(channel, handler, attachment);
        // ------------------------------------------------------------------------------------ THEN
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertNotNull(buffer);
        assertEquals(BYTES, buffer.capacity());
        // TODO: Verify, handler.completed(channel, attachment) invoked, once within some time.
        // TODO: Verify, channel.write(buffer, attachment, handler) invoked, at least once.
        // TODO: Assert, writtenSoFar.intValue() is equal to BYTES
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler, Object)
     * writeAsync(channel, handler, attachment)} method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)}.
     */
    @DisplayName("""
            (channel, handler, attachment)
            -> put(buffer[12])
            -> handler.failed(exc, attachment)""")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Failed_() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        var exc = mock(Throwable.class);
        _stub_ToFail(channel, exc);
        CompletionHandler<AsynchronousByteChannel, Object> handler = mock(CompletionHandler.class);
        var attachment = current().nextBoolean() ? null : new Object();
        // ------------------------------------------------------------------------------------ WHEN
        service.writeAsync(channel, handler, attachment);
        // ------------------------------------------------------------------------------------ THEN
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        // TODO: Verify, handler.failed(exc, attachment) invoked, once within some time.
    }
}
