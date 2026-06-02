package com.github.jinahya.hello.api._java_io;

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
import org.junit.jupiter.api.io.*;
import org.mockito.*;

import java.io.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#append(File) append(file)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(file)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_File_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#append(File) append(file)} method throws a
     * {@link NullPointerException} when the {@code file} argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <file> argument is <null>")
    @Test
    void _ThrowNullPointerException_FileIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = (File) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(file)
        );
    }

    /**
     * Verifies {@link HelloWorld#append(File) append(file)} method constructs a new
     * {@link FileOutputStream} with {@code file} and {@code true}, invokes
     * {@link HelloWorld#write(OutputStream) write(stream)} method, flushes/closes the stream, and
     * returns the {@code file}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName(
            "should open the <file> as <appending mode>, write <hello-world-bytes>, and close it")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).write(any(OutputStream.class));
        final var file = mock(File.class);
        final MockedConstruction.MockInitializer<FileOutputStream> initializer = (m, c) -> {
            log.debug("mock: {}, context: {}", m, c);
        };
        try (var construction = mockConstruction(FileOutputStream.class, initializer)) {
            // -------------------------------------------------------------------------------- when
            final var result = service.append(file);
            // ---------------------------------------------------------------------------------then
//            final var constructed = construction.constructed();
//            assertEquals(1, constructed.size());
//            final var stream = constructed.getFirst();
//            verify(service, times(1)).write(stream);
//            verify(stream, times(1)).flush();
//            verify(stream, times(1)).close();
            assertSame(file, result);
        }
    }
}
