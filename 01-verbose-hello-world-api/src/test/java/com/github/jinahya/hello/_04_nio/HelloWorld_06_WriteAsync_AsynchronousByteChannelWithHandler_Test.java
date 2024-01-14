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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousByteChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * A class for testing
 * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler, Object)
 * writeAsync(channel, handler, attachment)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, handler, attachment)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_06_WriteAsync_AsynchronousByteChannelWithHandler_Test extends _HelloWorldTest {

    /**
     * Verifies that the
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler, Object)
     * writeAsync(channel, handler, attachment)} method throws a {@link NullPointerException} when
     * the {@code channel} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the channel argument is null"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = (AsynchronousByteChannel) null;
        final var handler = Mockito.mock(CompletionHandler.class);
        final var attachment = (Void) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.writeAsync(channel, handler, attachment)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler, Object)
     * writeAsync(channel, handler, attachment)} method throws a {@link NullPointerException} when
     * the {@code handler} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the handler argument is null"""
    )
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var handler = (CompletionHandler<AsynchronousByteChannel, Void>) null;
        final var attachment = (Void) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.writeAsync(channel, handler, attachment)
        );
    }

    /**
     * Verifies that the
     * {@link HelloWorld#writeAsync(AsynchronousByteChannel, CompletionHandler, Object)
     * writeAsync(channel, handler, attachment)} method invokes
     * {@link HelloWorld#put(ByteBuffer) put(buffer)} method with a byte buffer of
     * {@value HelloWorld#BYTES} bytes, continuously invokes
     * {@link AsynchronousByteChannel#write(ByteBuffer, Object, CompletionHandler)
     * channel.write(buffer, attachment, a-handler)}, and eventually invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(Object, Object)
     * handler.completed(channel, attachment)}.
     */
    @DisplayName("""
            -> put(buffer[12])
            -> write the buffer to channel while the buffer has remaining
            """
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        BDDMockito.willAnswer(i -> {
                    final var buffer = i.getArgument(0, ByteBuffer.class);
                    buffer.position(buffer.position() + HelloWorld.BYTES);
                    return buffer;
                })
                .given(service)
                .put(ArgumentMatchers.argThat(b -> b != null && b.remaining() >= HelloWorld.BYTES));
        final var channel = Mockito.mock(AsynchronousByteChannel.class);
        final var writtenSoFar = new LongAdder();
        _HelloWorldTestUtils.write_invokeHandlerCompleted(channel, writtenSoFar);
        final var handler = Mockito.mock(CompletionHandler.class);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        // ------------------------------------------------------------------------------------ when
        service.writeAsync(channel, handler, attachment);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).put(bufferCaptor().capture());
        final var buffer = bufferCaptor().getValue();
        Assertions.assertNotNull(buffer);
        Assertions.assertEquals(HelloWorld.BYTES, buffer.capacity());
        // TODO: verify, handler.completed(channel, attachment) invoked, once, within some time.
        // TODO: verify, channel.write(buffer, attachment, a-handler) invoked, at least once.
        // TODO: assert, writtenSoFar.intValue() is equal to BYTES
        // TODO: assert, buffer ha no remaining
    }
}
