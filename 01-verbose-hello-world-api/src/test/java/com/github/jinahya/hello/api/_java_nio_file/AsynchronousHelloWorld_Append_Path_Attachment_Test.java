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
 * A class for testing {@link AsynchronousHelloWorld#append(Path, Object) append(path, attachment)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("AsynchronousHelloWorld.append(Path, Attachment)")
@Slf4j
abstract class AsynchronousHelloWorld_Append_Path_Attachment_Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    private static final long TIMEOUT_SECONDS = 8L;

    AsynchronousHelloWorld_Append_Path_Attachment_Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code path} argument
     * is {@code null}.
     */
    @DisplayName("throws NPE / path is null")
    @Test
    void _ThrowNullPointerException_PathIsNull() {
        final var asynchronousService = asynchronousService();
        final var path = (Path) null;
        assertThrows(NullPointerException.class, () -> asynchronousService.append(path, null));
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes with
     * the supplied {@code attachment} once the asynchronous append succeeds.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("happy path / completed")
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
        try (var mockStatic = mockStatic(AsynchronousFileChannel.class)) {
            mockStatic.when(() -> AsynchronousFileChannel.open(same(path), any(OpenOption[].class)))
                    .thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            final var future = asynchronousService.append(path, attachment);
            // -------------------------------------------------------------------------------- then
            assertSame(attachment,
                       future.toCompletableFuture().get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
        }
        verify(channel, times(1)).close();
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes
     * exceptionally when
     * {@link AsynchronousFileChannel#open(Path, java.nio.file.OpenOption...)
     * AsynchronousFileChannel.open(path, ...)} throws.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("failed / open throws")
    @Test
    void __failed() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var path = mock(Path.class);
        final var exc = new IOException("simulated open failure");
        final var attachment = new Object();
        try (var mockStatic = mockStatic(AsynchronousFileChannel.class)) {
            mockStatic.when(() -> AsynchronousFileChannel.open(same(path), any(OpenOption[].class)))
                    .thenThrow(exc);
            // -------------------------------------------------------------------------------- when
            final var future = asynchronousService.append(path, attachment);
            // -------------------------------------------------------------------------------- then
            final var cause = assertThrows(
                    ExecutionException.class,
                    () -> future.toCompletableFuture().get(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            ).getCause();
            assertSame(exc, cause);
        }
    }
}
