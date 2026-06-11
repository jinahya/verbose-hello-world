package com.github.jinahya.hello.api._java_nio_file;

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
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#append(Path, Object, CompletionHandler) append(path, attachment,
 * handler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(path, attachment, handler)")
@Slf4j
abstract class AsynchronousHelloWorld_Append_Path_Attachment_Handler_Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    private static final long TIMEOUT = TimeUnit.SECONDS.toMillis(8L);

    AsynchronousHelloWorld_Append_Path_Attachment_Handler_Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code path} argument
     * is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <path> argument is <null>")
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_PathIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var path = (Path) null;
        final var handler = mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                NullPointerException.class,
                () -> asynchronousService.append(path, null, handler)
        );
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code handler}
     * argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <handler> argument is <null>")
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var path = mock(Path.class);
        final var handler = (CompletionHandler<Path, Object>) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                NullPointerException.class,
                () -> asynchronousService.append(path, null, handler)
        );
    }

    /**
     * Verifies that the method opens the {@code path} as an {@link AsynchronousFileChannel}, writes
     * the {@value HelloWorld#BYTES} bytes starting at {@link AsynchronousFileChannel#size()
     * channel.size()}, closes the channel, and invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(path, attachment)}.
     */
    @DisplayName("""
            should open <path>, write at <channel.size()>, close,
            and invoke <handler.completed(path, attachment)>""")
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() throws Exception {
        // ----------------------------------------------------------------------------------- given
        put_buffer12_increases_buffer_position_by_12(synchronousService());
        final var asynchronousService = asynchronousService();
        final var path = mock(Path.class);
        final var channel = mock(AsynchronousFileChannel.class);
        final var initialSize = ThreadLocalRandom.current().nextLong(1024L);
        doReturn(initialSize).when(channel).size();
        doAnswer(i -> {
            final var src = i.getArgument(0, ByteBuffer.class);
            final var att = i.getArgument(2);
            final CompletionHandler innerHandler = i.getArgument(3, CompletionHandler.class);
            final var n = src.remaining();
            src.position(src.position() + n);
            innerHandler.completed(n, att);
            return null;
        }).when(channel).write(any(), anyLong(), any(), any());
        final var attachment = new Object();
        final var handler = mock(CompletionHandler.class);
        try (var mockStatic = mockStatic(AsynchronousFileChannel.class)) {
            mockStatic.when(() -> AsynchronousFileChannel.open(same(path), any(OpenOption[].class)))
                    .thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            asynchronousService.append(path, attachment, handler);
            // -------------------------------------------------------------------------------- then
            verify(handler, timeout(TIMEOUT).times(1)).completed(path, attachment);
        }
        verify(channel, times(1)).size();
        verify(channel, times(1)).close();
        verify(handler, never()).failed(any(), any());
    }

    /**
     * Verifies that when {@link AsynchronousFileChannel#open(Path, java.nio.file.OpenOption...)
     * AsynchronousFileChannel.open(path, ...)} throws an {@link IOException}, the method invokes
     * {@link CompletionHandler#failed(Throwable, Object) handler.failed(exc, attachment)}.
     */
    @DisplayName("""
            should invoke <handler.failed(exc, attachment)>
            when <AsynchronousFileChannel.open(path, ...)> throws""")
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var path = mock(Path.class);
        final var exc = new IOException("simulated open failure");
        final var attachment = new Object();
        final var handler = mock(CompletionHandler.class);
        try (var mockStatic = mockStatic(AsynchronousFileChannel.class)) {
            mockStatic.when(() -> AsynchronousFileChannel.open(same(path), any(OpenOption[].class)))
                    .thenThrow(exc);
            // -------------------------------------------------------------------------------- when
            asynchronousService.append(path, attachment, handler);
            // -------------------------------------------------------------------------------- then
            verify(handler, times(1)).failed(exc, attachment);
        }
        verify(handler, never()).completed(any(), any());
    }
}
