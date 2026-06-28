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
import com.github.jinahya.hello.api.annotations.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.nio.channels.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring
 * {@link AsynchronousHelloWorld#append(Path, Object, CompletionHandler) append(path, attachment,
 * handler)} method with real implementations.
 *
 * @param <T> the subtype of {@link AsynchronousHelloWorld}.
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("AsynchronousHelloWorld.append(Path, Attachment, Handler)")
@Slf4j
@SuppressWarnings({"java:S101"})
abstract class AsynchronousHelloWorld_Append_Path_Attachment_Handler__Test<
        T extends AsynchronousHelloWorld<HelloWorld>
        >
        extends AsynchronousHelloWorld__Test<HelloWorld, T> {

    AsynchronousHelloWorld_Append_Path_Attachment_Handler__Test(
            final Function<? super HelloWorld, ? extends T> initializer) {
        super(HelloWorld.class, initializer);
    }

    /**
     * Verifies that the method appends {@code hello-world-bytes} to a real {@link Path}, and
     * invokes
     * {@link CompletionHandler#completed(Object, Object) handler.completed(path, attachment)}.
     *
     * @param tempDir the temporary directory.
     * @throws Exception if an error occurs.
     */
    @DisplayName("happy path")
    @Test
    @SuppressWarnings({"unchecked"})
    void __(@TempDir final Path tempDir) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        put_buffer12_increases_buffer_position_by_12(synchronousService());
        final var path = Files.createTempFile(tempDir, null, null);
        HelloWorld__TestUtils.writeSome(path);
        final var size = Files.size(path);
        final var attachment = new Object();
        final var handler = mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.append(path, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        verify(handler, timeout(TimeUnit.SECONDS.toMillis(8L)).times(1))
                .completed(path, attachment);
        assertEquals(size + HelloWorld.BYTES, Files.size(path));
    }
}
