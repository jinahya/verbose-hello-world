package com.github.jinahya.hello._04_nio;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import com.github.jinahya.hello._HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

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
 * @see HelloWorld_06_WriteAsync_AsynchronousByteChannelWithHandler_Arguments_Test
 */
@DisplayName("write(channel, handler, attachment)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_06_WriteAsync_AsynchronousByteChannelWithHandler_Test extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        putBuffer_WillReturnTheBuffer_AsItsPositionIncreasedBy12();
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
    @DisplayName("-> put(buffer[12]) -> handler.completed(channel, attachment)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Completed_Completes() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = mock(AsynchronousByteChannel.class);
        final var writtenSoFar = new LongAdder();
        _HelloWorldTestUtils.writeWithHandler_completes(channel, writtenSoFar);
        final var handler = mock(CompletionHandler.class);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // ------------------------------------------------------------------------------------ when
        service.writeAsync(channel, handler, attachment);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).put(bufferCaptor().capture());
        var buffer = bufferCaptor().getValue();
        assertNotNull(buffer);
        assertEquals(HelloWorld.BYTES, buffer.capacity());
        // TODO: Verify, handler.completed(channel, attachment) invoked, once, within some time.
        // TODO: Verify, channel.write(buffer, attachment, handler) invoked, at least once.
        // TODO: Assert, writtenSoFar.intValue() is equal to BYTES
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler, Object)
     * writeAsync(channel, handler, attachment)} method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)}.
     */
    @DisplayName("-> handler.failed(exc, attachment)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Failed_Fails() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = mock(AsynchronousByteChannel.class);
        _HelloWorldTestUtils.writeWithHandler_fails(channel);
        final var handler = mock(CompletionHandler.class);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // ------------------------------------------------------------------------------------ when
        service.writeAsync(channel, handler, attachment);
        // ------------------------------------------------------------------------------------ then
        // TODO: Verify, handler.failed(any(), attachment) invoked, once, within some time.
    }
}
