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

import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * A class for testing
 * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler) write(channel, handler)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_22_WriteAsync_AsynchronousByteChannelWithHandler_Arguments_Test
 */
@DisplayName("write(channel, handler)")
@Slf4j
class HelloWorld_22_WriteAsync_AsynchronousByteChannelWithHandler_Test
        extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        stubPutBufferToReturnTheBufferAsItsPositionIncreasedBy12();
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler) writeAsync(channel,
     * handler)} method invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, null)}.
     */
    @DisplayName("(channel, handler) -> handler.completed(channel, null)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Completed_() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        stubToComplete(channel, writtenSoFar);
        CompletionHandler<AsynchronousByteChannel, Object> handler = mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ WHEN
        service.writeAsync(channel, handler);
        // ------------------------------------------------------------------------------------ THEN
        verify(handler, timeout(SECONDS.toMillis(8L)).times(1)).completed(channel, null);
        assertEquals(BYTES, writtenSoFar.intValue());
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler) writeAsync(channel,
     * handler)} method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exe, null)}.
     */
    @DisplayName("(channel, handler) -> handler.failed(exe, null)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Failed_() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        stubToFail(channel);
        CompletionHandler<AsynchronousByteChannel, Object> handler = mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ WHEN
        service.writeAsync(channel, handler);
        // ------------------------------------------------------------------------------------ THEN
        verify(handler, timeout(SECONDS.toMillis(8L)).times(1)).failed(notNull(), isNull());
    }
}