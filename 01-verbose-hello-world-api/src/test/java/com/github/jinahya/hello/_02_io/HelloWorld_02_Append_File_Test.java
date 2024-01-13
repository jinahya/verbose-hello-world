package com.github.jinahya.hello._02_io;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;

/**
 * A class for testing {@link HelloWorld#append(File) append(file)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(file)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_02_Append_File_Test extends _HelloWorldTest {

    /**
     * Verifies {@link HelloWorld#append(File) append(file)} method throws a
     * {@link NullPointerException} when the {@code file} argument is {@code null}.
     */
    @DisplayName("[file == null] -> NullPointerException")
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

    @BeforeEach
    void beforeEach() throws IOException {
        writeStream_willReturnStream();
    }

    /**
     * Verifies {@link HelloWorld#append(File) append(file)} method constructs a new
     * {@link FileOutputStream} with {@code file} and {@code true}, invokes
     * {@link HelloWorld#write(OutputStream) write(stream)} method, flushes/closes the stream, and
     * returns the {@code file}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("-> write(new FileOutputStream(file, true))")
    @Test
    void _InvokeWriteWithFileOutputStreamOfAppendingMode_()
            throws IOException, NoSuchMethodException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = Mockito.mock(File.class);
        final var contexts = new LinkedHashMap<FileOutputStream, MockedConstruction.Context>();
        try (var mock = Mockito.mockConstruction(FileOutputStream.class, contexts::put)) {
            // -------------------------------------------------------------------------------- when
            final var result = service.append(file);
            // ---------------------------------------------------------------------------------then
            // TODO: assert, FileOutputStream(file, true) invoked, once
            // TODO: verify, service.write(stream) invoked, once
            // TODO: verify, stream.flush() invoked, once
            // TODO: verify, stream.close() invoked, once
            Assertions.assertSame(file, result);
        }
    }
}
