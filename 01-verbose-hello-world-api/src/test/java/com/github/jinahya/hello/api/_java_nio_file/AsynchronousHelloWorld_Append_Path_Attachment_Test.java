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
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.function.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link AsynchronousHelloWorld#append(Path, Object) append(path, attachment)}
 * method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(path, attachment)")
@Slf4j
abstract class AsynchronousHelloWorld_Append_Path_Attachment_Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    AsynchronousHelloWorld_Append_Path_Attachment_Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code path} argument
     * is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <path> argument is <null>")
    @Test
    void _ThrowNullPointerException_PathIsNull() {
        final var asynchronousService = asynchronousService();
        final var path = (Path) null;
        assertThrows(NullPointerException.class, () -> asynchronousService.append(path, null));
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes with
     * the supplied {@code attachment} once the synchronous append succeeds.
     *
     * @throws Exception if an error occurs.
     */
    @DisplayName("""
            should complete the returned stage with the <attachment>
            once the synchronous append succeeds""")
    @Test
    void __completed() throws Exception {
        final var asynchronousService = asynchronousService();
        final var path = mock(Path.class);
        doReturn(path).when(synchronousService()).append(path);
        final var attachment = new Object();
        final var future = asynchronousService.append(path, attachment);
        assertSame(attachment, future.toCompletableFuture().get(8L, TimeUnit.SECONDS));
    }

    /**
     * Verifies that the returned {@link java.util.concurrent.CompletionStage stage} completes
     * exceptionally when the synchronous append fails.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName(
            "should complete the returned stage exceptionally when the synchronous append fails")
    @Test
    void __failed() throws IOException {
        final var asynchronousService = asynchronousService();
        final var path = mock(Path.class);
        final var exc = new IOException("simulated append failure");
        doThrow(exc).when(synchronousService()).append(path);
        final var attachment = new Object();
        final var future = asynchronousService.append(path, attachment);
        final var cause = assertThrows(
                ExecutionException.class,
                () -> future.toCompletableFuture().get(8L, TimeUnit.SECONDS)
        ).getCause();
        assertSame(exc, cause);
    }
}
