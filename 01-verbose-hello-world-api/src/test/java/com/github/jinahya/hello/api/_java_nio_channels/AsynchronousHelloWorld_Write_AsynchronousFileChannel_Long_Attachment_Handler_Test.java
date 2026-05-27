package com.github.jinahya.hello.api._java_nio_channels;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousFileChannel, long, Object, CompletionHandler)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, position, attachment, handler)")
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment_Handler_Test
        extends AsynchronousHelloWorld__Test<HelloWorld> {

    AsynchronousHelloWorld_Write_AsynchronousFileChannel_Long_Attachment_Handler_Test() {
        super(HelloWorld.class);
    }

    @DisplayName("should throw NullPointerException when channel is null")
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_ChannelIsNull() {
        final var asynchronousService = asynchronousService();
        final var channel = (AsynchronousFileChannel) null;
        final var position = 0L;
        final var handler = (CompletionHandler<AsynchronousFileChannel, Object>)
                Mockito.mock(CompletionHandler.class);
        Assertions.assertThrows(
                NullPointerException.class,
                () -> asynchronousService.write(channel, position, null, handler)
        );
    }

    @DisplayName("should throw IllegalArgumentException when position is negative")
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowIllegalArgumentException_PositionIsNegative() {
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var position = ThreadLocalRandom.current().nextLong() | Long.MIN_VALUE;
        final var handler = (CompletionHandler<AsynchronousFileChannel, Object>)
                Mockito.mock(CompletionHandler.class);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> asynchronousService.write(channel, position, null, handler)
        );
    }

    @DisplayName("should throw NullPointerException when handler is null")
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var position = 0L;
        final var handler = (CompletionHandler<AsynchronousFileChannel, Object>) null;
        Assertions.assertThrows(
                NullPointerException.class,
                () -> asynchronousService.write(channel, position, null, handler)
        );
    }

    @DisplayName("""
            should write all <hello-world-bytes> across partial writes,
            and invoke <handler.completed(channel, attachment)>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() {
        // ----------------------------------------------------------------------------------- given
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var written = new LongAdder();
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2);
            final var handler = i.getArgument(3, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                written.add(n);
                handler.completed(n, attachment);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(),
                ArgumentMatchers.notNull()
        );
        final var position = ThreadLocalRandom.current().nextLong(1024L);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = (CompletionHandler<AsynchronousFileChannel, Object>)
                Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.write(channel, position, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(channel, attachment);
        Mockito.verify(handler, Mockito.never())
                .failed(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assertions.assertEquals(HelloWorld.BYTES, written.intValue());
    }

    @DisplayName("""
            should invoke <handler.failed(exc, attachment)>
            when the <channel> fails on or after partial writes"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() {
        // ----------------------------------------------------------------------------------- given
        HelloWorldTestUtils.put_buffer_will_increase_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var channel = Mockito.mock(AsynchronousFileChannel.class);
        final var exc = new RuntimeException("simulated write failure");
        Mockito.doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var position = i.getArgument(1, Long.class);
            final var attachment = i.getArgument(2);
            final var handler = i.getArgument(3, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                if (!src.hasRemaining() || ThreadLocalRandom.current().nextBoolean()) {
                    handler.failed(exc, attachment);
                    return;
                }
                handler.completed(n, attachment);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()),
                ArgumentMatchers.anyLong(),
                ArgumentMatchers.any(),
                ArgumentMatchers.notNull()
        );
        final var position = ThreadLocalRandom.current().nextLong(1024L);
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = (CompletionHandler<AsynchronousFileChannel, Object>)
                Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.write(channel, position, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(handler, Mockito.timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .failed(exc, attachment);
        Mockito.verify(handler, Mockito.never())
                .completed(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}
