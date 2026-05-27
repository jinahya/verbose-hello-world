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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.nio.*;
import java.nio.channels.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(channel, attachment, handler)")
@Slf4j
class AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Handler_Test
        extends AsynchronousHelloWorld__Test<HelloWorld> {

    AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Handler_Test() {
        super(HelloWorld.class);
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("should throw NullPointerException when channel is null")
    @Test
    @SuppressWarnings({"rawtypes"})
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var channel = (AsynchronousByteChannel) null;
        final CompletionHandler handler = mock(CompletionHandler.class);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                NullPointerException.class,
                () -> asynchronousService.write(channel, null, handler));
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code handler}
     * argument is {@code null}.
     */
    @DisplayName("should throw NullPointerException when handler is null")
    @Test
    @SuppressWarnings({"rawtypes"})
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final CompletionHandler handler = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(
                NullPointerException.class,
                () -> asynchronousService.write(channel, null, handler)
        );
    }

    /**
     * Verifies that the method writes all {@value HelloWorld#BYTES} bytes to the {@code channel}
     * across one or more partial writes, and finally invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}.
     */
    @DisplayName("""
            should write all <hello-world-bytes> across partial writes,
            and invoke <handler.completed(channel, attachment)>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() {
        // ----------------------------------------------------------------------------------- given
        put_buffer_will_increase_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final var threadReference = new AtomicReference<Thread>();
        final var bufferPositions = new CopyOnWriteArrayList<Integer>();
        doAnswer(i -> {
            assert threadReference.get() == null;
            final var src = i.getArgument(0, ByteBuffer.class);
            assert src != null;
            assert src.capacity() == HelloWorld.BYTES;
            assert src.limit() == HelloWorld.BYTES;
            assert src.remaining() > 0;
            bufferPositions.add(src.position());
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            threadReference.set(Thread.ofVirtual().unstarted(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                threadReference.set(null);
                handler.completed(n, attachment);
            }));
            threadReference.get().start();
            return null;
        }).when(channel).write(any(), any(), any());
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(channel, attachment);
        verify(handler, never()).failed(any(), any());
        final var buffer = put_buffer12_invoked_once(synchronousService());
        verify(channel, atLeastOnce()).write(same(buffer), same(attachment), notNull());
        assertFalse(bufferPositions.isEmpty());
        assertEquals(0, bufferPositions.getFirst());
        for (int i = 1; i < bufferPositions.size(); i++) {
            assertTrue(bufferPositions.get(i) > bufferPositions.get(i - 1));
        }
        assertFalse(buffer.hasRemaining());
    }

    /**
     * Verifies that the method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)} when the
     * {@code channel} fails — possibly synchronously on the first invocation, or asynchronously
     * after one or more partial writes have already been acknowledged.
     */
    @DisplayName("""
            should invoke <handler.failed(exc, attachment)>
            when the <channel> fails on or after partial writes"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() {
        // ----------------------------------------------------------------------------------- given
        put_buffer_will_increase_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final var exc = new RuntimeException("simulated write failure");
        doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var a = i.getArgument(1);
            final var h = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().start(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                if (!src.hasRemaining() || ThreadLocalRandom.current().nextBoolean()) {
                    h.failed(exc, a);
                    return;
                }
                h.completed(n, a);
            });
            return null;
        }).when(channel).write(
                ArgumentMatchers.argThat(b -> b != null && b.hasRemaining()), // <src>
                any(),                                       // <attachment>
                notNull()                                    // <handler>
        );
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = (CompletionHandler<AsynchronousByteChannel, Object>)
                mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .failed(exc, attachment);
        verify(handler, never())
                .completed(any(), any());
    }
}
