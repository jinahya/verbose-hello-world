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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;

/**
 * A class for testing
 * {@link AsynchronousHelloWorld#append(Path, Object, CompletionHandler) append(path, attachment,
 * handler)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(path, attachment, handler)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class AsynchronousHelloWorld_Append_Path_Attachment_Handler_Test
        extends AsynchronousHelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <path> argument is <null>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void _ThrowNullPointerException_PathIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var path = (Path) null;
        final var handler = (CompletionHandler<Path, Object>)
                Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> asynchronousService.append(path, null, handler)
        );
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <handler> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_HandlerIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var path = Mockito.mock(Path.class);
        final var handler = (CompletionHandler<Path, Object>) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> asynchronousService.append(path, null, handler)
        );
    }

    @DisplayName("""
            should invoke <synchronousService.append(path)>
            and notify <handler.completed(path, attachment)>"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __completed() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var path = Mockito.mock(Path.class);
        Mockito.doReturn(path).when(synchronousService()).append(path);
        final var attachment = new Object();
        final var handler = (CompletionHandler<Path, Object>)
                Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.append(path, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(synchronousService(), Mockito.times(1)).append(path);
        Mockito.verify(handler, Mockito.times(1)).completed(path, attachment);
        Mockito.verify(handler, Mockito.never())
                .failed(Mockito.any(), Mockito.any());
    }

    @DisplayName("""
            should notify <handler.failed(exc, attachment)>
            when <synchronousService.append(path)> throws"""
    )
    @Test
    @SuppressWarnings({"unchecked"})
    void __failed() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var asynchronousService = asynchronousService();
        final var path = Mockito.mock(Path.class);
        final var exc = new IOException("simulated append failure");
        Mockito.doThrow(exc).when(synchronousService()).append(path);
        final var attachment = new Object();
        final var handler = (CompletionHandler<Path, Object>)
                Mockito.mock(CompletionHandler.class);
        // ------------------------------------------------------------------------------------ when
        asynchronousService.append(path, attachment, handler);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(handler, Mockito.times(1)).failed(exc, attachment);
        Mockito.verify(handler, Mockito.never())
                .completed(Mockito.any(), Mockito.any());
    }
}
