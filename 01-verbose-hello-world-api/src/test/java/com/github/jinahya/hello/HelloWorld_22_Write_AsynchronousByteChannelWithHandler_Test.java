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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing
 * {@link HelloWorld#write(AsynchronousByteChannel, CompletionHandler, Object) write(channel,
 * handler, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_22_Write_AsynchronousByteChannelWithHandler_Arguments_Test
 */
@DisplayName("write(channel, handler, attachment)")
@Slf4j
class HelloWorld_22_Write_AsynchronousByteChannelWithHandler_Test
        extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        stubPutBufferToIncreasePositionBy12();
    }

    /**
     * Asserts
     * {@link HelloWorld#write(AsynchronousByteChannel, CompletionHandler, Object) write(channel,
     * handler, attachment)} method invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}.
     */
    @DisplayName("(channel, handler, attachment) -> handler.completed(channel, attachment)")
    @Test
    @SuppressWarnings({"unchecked"})
    void _Completed_() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var channel = mock(AsynchronousByteChannel.class);
        var writtenSoFar = new LongAdder();
        willAnswer(i -> {
            ByteBuffer buffer = i.getArgument(0);
            assert buffer.hasRemaining();
            var attachment = i.getArgument(1);
            var handler = i.getArgument(2, CompletionHandler.class);
            var written = current().nextInt(1, buffer.remaining() + 1);
            buffer.position(buffer.position() + written);
            writtenSoFar.add(written);
            handler.completed(written, attachment);
            return null;
        }).given(channel).write(any(), any(), any());
        CompletionHandler<AsynchronousByteChannel, Object> handler = mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ WHEN
        service.write(channel, handler);
        // ------------------------------------------------------------------------------------ THEN
        // TODO: Verify handler.completed(channel, null) invoked, once, in a handful seconds
        // TODO: Verify 12 bytes has been written to the channel
    }
}
