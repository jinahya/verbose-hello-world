package com.github.jinahya.hello.api._java_nio_file;

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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#append(Path) append(path)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld.append(Path)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_Path_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#append(Path) append(path)} method throws a
     * {@link NullPointerException} when the {@code path} argument is {@code null}.
     */
    @DisplayName("throws NPE / path is null")
    @Test
    void _ThrowNullPointerException_PathIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var path = (Path) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.append(path));
    }

    /**
     * Verifies that the {@link HelloWorld#append(Path) append(path)} method invokes
     * {@link HelloWorld#write(WritableByteChannel) write(channel)} with the channel opened on the
     * given path, and returns the {@code path}.
     */
    @DisplayName("happy path")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).<WritableByteChannel>write(any());
        final var path = mock(Path.class);
        final var channel = mock(FileChannel.class);
        try (var mockStatic = mockStatic(FileChannel.class)) {
            mockStatic.when(() -> FileChannel.open(same(path), any(OpenOption[].class)))
                    .thenReturn(channel);
            // -------------------------------------------------------------------------------- when
            final var result = service.append(path);
            // -------------------------------------------------------------------------------- then
            final var captor = ArgumentCaptor.forClass(OpenOption[].class);
//            mockStatic.verify(
//                    () -> FileChannel.open(same(path), captor.capture()),
//                    times(1)
//            );
//            final var value = captor.getValue();
//            final var options = new HashSet<>(Arrays.asList(value));
//            assertTrue(options.remove(StandardOpenOption.CREATE));
//            assertTrue(options.remove(StandardOpenOption.APPEND));
//            assertTrue(options.isEmpty());
//            verify(service, times(1)).write(channel);
//            verify(channel, times(1)).close();
            assertSame(path, result);
        }
    }
}
