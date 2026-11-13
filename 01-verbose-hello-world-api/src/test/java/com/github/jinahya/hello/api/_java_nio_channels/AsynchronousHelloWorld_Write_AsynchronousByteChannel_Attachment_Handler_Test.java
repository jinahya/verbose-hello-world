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

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#write(AsynchronousByteChannel, Object, CompletionHandler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("AsynchronousHelloWorld.write(AsynchronousByteChannel, attachment, handler)")
@Slf4j
abstract class AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Handler_Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    /**
     * Maximum time in milliseconds to wait for handler callbacks.
     */
    private static final long TIMEOUT = TimeUnit.SECONDS.toMillis(10);

    // ---------------------------------------------------------------------------------------------
    AsynchronousHelloWorld_Write_AsynchronousByteChannel_Attachment_Handler_Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code channel}
     * argument is {@code null}.
     */
    @DisplayName("throws NPE / channel is null")
    @Test
    void _ThrowNullPointerException_ChannelIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var channel = (AsynchronousByteChannel) null;
        final var handler = mock(CompletionHandler.class);
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.write(channel, null, handler));
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code handler}
     * argument is {@code null}.
     */
    @DisplayName("throws NPE / handler is null")
    @Test
    @SuppressWarnings({"rawtypes"})
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final CompletionHandler h = null;
        // ----------------------------------------------------------------------------- when / then
        assertThrows(NullPointerException.class, () -> service.write(channel, null, h));
    }

    /**
     * Verifies that the method writes all {@value HelloWorld#BYTES} bytes to the {@code channel}
     * across one or more partial writes, and finally invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}.
     */
    @DisplayName("completed")
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() {
        // ----------------------------------------------------------------------------------- given
        put_buffer12_increases_buffer_position_by_12(synchronousService());
        final var service = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final var threadReference = new AtomicReference<Thread>();
        final var bufferPositions = new CopyOnWriteArrayList<Integer>();
        doAnswer(i -> {
            assert threadReference.get() == null;
            final var src = i.getArgument(0, ByteBuffer.class);
            assert src != null;
            assert src.capacity() == HelloWorld.BYTES;
            assert src.limit() == HelloWorld.BYTES;
            assert src.hasRemaining();
            bufferPositions.add(src.position());
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            threadReference.set(Thread.ofPlatform().daemon().unstarted(() -> {
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
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TIMEOUT).times(1)).completed(channel, attachment);
        verifyNoMoreInteractions(handler);
        final var buffer = put_buffer12_invoked_once(synchronousService());
        verify(channel, atLeastOnce()).write(same(buffer), same(attachment), notNull());
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
    @DisplayName("failed")
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() {
        // ----------------------------------------------------------------------------------- given
        put_buffer12_increases_buffer_position_by_12(synchronousService());
        final var service = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final var exc = new RuntimeException("simulated write failure");
        final var threadReference = new AtomicReference<Thread>();
        final var bufferPositions = new CopyOnWriteArrayList<Integer>();
        doAnswer(i -> {
            assert threadReference.get() == null;
            final var src = i.getArgument(0, ByteBuffer.class);
            assert src != null;
            assert src.capacity() == HelloWorld.BYTES;
            assert src.limit() == HelloWorld.BYTES;
            assert src.hasRemaining();
            bufferPositions.add(src.position());
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            threadReference.set(Thread.ofPlatform().daemon().unstarted(() -> {
                final var n = ThreadLocalRandom.current().nextInt(src.remaining()) + 1;
                src.position(src.position() + n);
                threadReference.set(null);
                if (!src.hasRemaining() || ThreadLocalRandom.current().nextBoolean()) {
                    handler.failed(exc, attachment);
                    return;
                }
                handler.completed(n, attachment);
            }));
            threadReference.get().start();
            return null;
        }).when(channel).write(any(), any(), any());
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TIMEOUT).times(1)).failed(exc, attachment);
        verifyNoMoreInteractions(handler);
        final var buffer = put_buffer12_invoked_once(synchronousService());
        verify(channel, atLeastOnce()).write(same(buffer), same(attachment), notNull());
        assertEquals(0, bufferPositions.getFirst());
        for (int i = 1; i < bufferPositions.size(); i++) {
            assertTrue(bufferPositions.get(i) > bufferPositions.get(i - 1));
        }
//        assertFalse(buffer.hasRemaining()); // should be remained as commented-out
    }

    /**
     * Verifies that the method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)} when the
     * first {@code channel.write} invocation throws a {@link RuntimeException} synchronously.
     */
    @DisplayName("failed / first write throws")
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed_FirstWriteThrows() {
        // ----------------------------------------------------------------------------------- given
        put_buffer12_increases_buffer_position_by_12(synchronousService());
        final var service = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final var exc = new WritePendingException();
        doThrow(exc).when(channel).write(any(), any(), any());
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TIMEOUT).times(1)).failed(exc, attachment);
        verifyNoMoreInteractions(handler);
        verify(channel, times(1)).write(any(), any(), any());
    }

    /**
     * Verifies that the method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)} when a
     * subsequent {@code channel.write} invocation, initiated for the remaining bytes, throws a
     * {@link RuntimeException} synchronously.
     */
    @DisplayName("failed / subsequent write throws")
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed_SubsequentWriteThrows() {
        // ----------------------------------------------------------------------------------- given
        put_buffer12_increases_buffer_position_by_12(synchronousService());
        final var service = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final var exc = new WritePendingException();
        final var invocations = new AtomicInteger();
        doAnswer(i -> {
            if (invocations.getAndIncrement() > 0) {
                throw exc;
            }
            final var src = i.getArgument(0, ByteBuffer.class);
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            Thread.ofPlatform().daemon().start(() -> {
                src.position(src.position() + 1);
                handler.completed(1, attachment);
            });
            return null;
        }).when(channel).write(any(), any(), any());
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TIMEOUT).times(1)).failed(exc, attachment);
        verifyNoMoreInteractions(handler);
        verify(channel, times(2)).write(any(), any(), any());
    }

    /**
     * Verifies that the method keeps initiating {@code channel.write} for the remaining bytes when
     * the {@code channel} writes exactly {@code 3}, {@code 4}, and {@code 5} bytes in turn, and
     * finally invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}.
     */
    @DisplayName("completed / partial writes of 3, 4, and 5 bytes")
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed_PartialWrites() {
        // ----------------------------------------------------------------------------------- given
        put_buffer12_increases_buffer_position_by_12(synchronousService());
        final var service = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final var counts = new int[] {3, 4, 5};
        final var invocations = new AtomicInteger();
        final var bufferPositions = new CopyOnWriteArrayList<Integer>();
        doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var attachment = i.getArgument(1);
            final var handler = i.getArgument(2, CompletionHandler.class);
            bufferPositions.add(src.position());
            final var n = counts[invocations.getAndIncrement()];
            Thread.ofPlatform().daemon().start(() -> {
                src.position(src.position() + n);
                handler.completed(n, attachment);
            });
            return null;
        }).when(channel).write(any(), any(), any());
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        service.write(channel, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TIMEOUT).times(1)).completed(channel, attachment);
        verifyNoMoreInteractions(handler);
        final var buffer = put_buffer12_invoked_once(synchronousService());
        verify(channel, times(3)).write(same(buffer), same(attachment), notNull());
        assertEquals(List.of(0, 3, 7), bufferPositions);
        assertFalse(buffer.hasRemaining());
    }

    /**
     * Verifies that the method notifies
     * {@link CompletionHandler#completed(Object, Object) handler.completed(channel, attachment)}
     * exactly once, and never notifies a failure, when the {@code channel} completes inline and
     * the {@code handler.completed} itself throws a {@link RuntimeException}.
     */
    @DisplayName("completed once / handler.completed throws on an inline completion")
    @Test
    @SuppressWarnings({"unchecked"})
    void __completedOnce_HandlerThrowsOnInlineCompletion() {
        // ----------------------------------------------------------------------------------- given
        put_buffer12_increases_buffer_position_by_12(synchronousService());
        final var service = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var n = src.remaining();
            src.position(src.limit());
            i.getArgument(2, CompletionHandler.class).completed(n, i.getArgument(1));
            return null;
        }).when(channel).write(any(), any(), any());
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = mock(CompletionHandler.class);
        final var exc = new RuntimeException("simulated handler failure");
        doThrow(exc).when(handler).completed(any(), any());
        // ------------------------------------------------------------------------------------ when
        try {
            service.write(channel, attachment, handler);
        } catch (final RuntimeException e) {
            assertSame(exc, e);
        }
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TIMEOUT).times(1)).completed(channel, attachment);
        verify(handler, after(500L).never()).failed(any(), any());
    }

    /**
     * Verifies that the method notifies
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)} exactly
     * once when the {@code channel} fails inline and the {@code handler.failed} itself throws a
     * {@link RuntimeException}.
     */
    @DisplayName("failed once / handler.failed throws on an inline failure")
    @Test
    @SuppressWarnings({"unchecked"})
    void __failedOnce_HandlerThrowsOnInlineFailure() {
        // ----------------------------------------------------------------------------------- given
        put_buffer12_increases_buffer_position_by_12(synchronousService());
        final var service = asynchronousService();
        final var channel = mock(AsynchronousByteChannel.class);
        final var failure = new IOException("simulated write failure");
        doAnswer(i -> {
            i.getArgument(2, CompletionHandler.class).failed(failure, i.getArgument(1));
            return null;
        }).when(channel).write(any(), any(), any());
        final var attachment = ThreadLocalRandom.current().nextBoolean() ? null : new Object();
        final var handler = mock(CompletionHandler.class);
        final var exc = new RuntimeException("simulated handler failure");
        doThrow(exc).when(handler).failed(any(), any());
        // ------------------------------------------------------------------------------------ when
        try {
            service.write(channel, attachment, handler);
        } catch (final RuntimeException e) {
            assertSame(exc, e);
        }
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TIMEOUT).times(1)).failed(failure, attachment);
        verify(handler, after(500L).times(1)).failed(any(), any());
        verify(handler, never()).completed(any(), any());
    }
}
