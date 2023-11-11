package com.github.jinahya.hello.c04nio;

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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.atomic.LongAdder;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.lang.Long.MAX_VALUE;
import static java.util.concurrent.ThreadLocalRandom.current;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * A class for testing
 * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, CompletionHandler, Object)
 * write(channel, position, handler, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_63_WriteAsync_AsynchronousFileChannelWithHandler_Arguments_Test
 */
@DisplayName("writeAsync(channel, position, handler, attachment)")
@Slf4j
class HelloWorld_63_WriteAsync_AsynchronousFileChannelWithHandler_Test extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        _stub_PutBuffer_ToReturnTheBuffer_AsItsPositionIncreasedBy12();
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, CompletionHandler, Object)
     * writeAsync(channel, position, handler, attachment)} method invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}.
     */
    @DisplayName("""
            (channel, position, handler, attachment)
            -> handler.completed(channel, attachment)""")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Completed_() {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        var writtenSoFar = new LongAdder();
        _stub_ToComplete(channel, writtenSoFar);
        var position = 0L;
        var handler = mock(CompletionHandler.class);
        var attachment = current().nextBoolean() ? null : new Object();
        // ------------------------------------------------------------------------------------ when
        service.writeAsync(channel, position, handler, attachment);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(SECONDS.toMillis(8L)).times(1)).completed(channel, attachment);
        assertEquals(BYTES, writtenSoFar.intValue());
    }

    /**
     * Asserts
     * {@link HelloWorld#writeAsync(AsynchronousFileChannel, long, CompletionHandler, Object)
     * writeAsync(channel, handler, attachment)} method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)}.
     */
    @DisplayName(
            "(channel, position, handler, attachment) -> handler.failed(exc, attachment)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Failed_() {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var channel = mock(AsynchronousFileChannel.class);
        var exc = _stub_ToFail(channel, mock(Throwable.class));
        var position = current().nextLong(MAX_VALUE - BYTES);
        var handler = mock(CompletionHandler.class);
        var attachment = current().nextBoolean() ? null : new Object();
        // ------------------------------------------------------------------------------------ when
        service.writeAsync(channel, position, handler, attachment);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(SECONDS.toMillis(8L)).times(1)).failed(same(exc), same(attachment));
    }
}
