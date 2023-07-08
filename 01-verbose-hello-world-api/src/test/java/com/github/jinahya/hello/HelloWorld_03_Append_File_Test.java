package com.github.jinahya.hello;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

/**
 * A class for testing {@link HelloWorld#append(File)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_03_Append_File_Arguments_Test
 */
@DisplayName("append(file)")
@Slf4j
class HelloWorld_03_Append_File_Test extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() throws IOException {
        doAnswer(i -> {
            var stream = i.getArgument(0, OutputStream.class); // <1>
            for (int j = 0; j < BYTES; j++) {                  // <2>
                stream.write(0);                               // <3>
            }
            return stream;
        }).when(serviceInstance()).write(notNull(OutputStream.class));
    }

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} method invokes
     * {@link HelloWorld#write(OutputStream) write(stream)} method with a value of
     * {@link FileOutputStream#FileOutputStream(File, boolean) new FileOutputStream(file, true)}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("(file) -> write(new FileOutputStream(file, true))")
    @Test
    void _InvokeWriteWithFileOutputStreamWithAppendingMode_() throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var file = mock(File.class);
        MockedConstruction.MockInitializer<FileOutputStream> initializer = (m, c) -> {
            assertSame(FileOutputStream.class.getConstructor(File.class, boolean.class),
                       c.constructor());
            var arguments = c.arguments();
            assertEquals(file, arguments.get(0));
            assertTrue((boolean) arguments.get(1));
            assertEquals(1, c.getCount());
        };
        try (var construction = mockConstruction(FileOutputStream.class, initializer)) {
            // -------------------------------------------------------------------------------- WHEN
            service.append(file);
            // ---------------------------------------------------------------------------------THEN
            var constructed = construction.constructed();
            // TODO: Assert, constructed.size() is equal to 1
            // TODO: Verify, service.write(constructed.get(0)) invoked, once
        }
    }

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} returns given {@code file} arguments.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("returns file")
    @Test
    void _ReturnFile_() throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var file = mock(File.class);
        try (MockedConstruction<FileOutputStream> c = mockConstruction(FileOutputStream.class)) {
            // WHEN
            var actual = service.append(file);
            // THEN
            assertSame(file, actual);
        }
    }

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} method appends {@value HelloWorld#BYTES}
     * bytes to the end of the {@code file}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("12 bytes are appended to the file")
    @Test
    @畵蛇添足
    void _12BytesAppended_(@TempDir File tempDir) throws IOException {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var file = createTempFile("tmp", null, tempDir);
        var length = file.length();
        // WHEN
        service.append(file);
        // THEN: file.length() increased by 12
    }

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} returns given {@code file} arguments.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("returns file")
    @Test
    @畵蛇添足
    void _ReturnFile_(@TempDir File tempDir) throws IOException {
        // GIVEN
        var service = serviceInstance();
        var file = createTempFile("tmp", null, tempDir);
        // WHEN
        var actual = service.append(file);
        // THEN
        assertSame(file, actual);
    }
}
