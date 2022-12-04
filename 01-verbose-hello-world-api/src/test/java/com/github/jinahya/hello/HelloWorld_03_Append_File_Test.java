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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * A class for testing {@link HelloWorld#append(File)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_03_Append_File_Arguments_Test
 */
@Slf4j
class HelloWorld_03_Append_File_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} method invokes
     * {@link HelloWorld#write(OutputStream) write(stream)} method with an instance of
     * {@link java.io.FileOutputStream} and asserts
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes are appended to the {@code file}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file)"
                 + " invokes write(FileOutputStream)"
                 + " 12 bytes are appended to the file")
    @Test
    void append_InvokeWriteStreamAnd12BytesAppended_(@TempDir File tempDir) throws IOException {
        var service = helloWorld();
        var file = createTempFile("tmp", null, tempDir);
        var length = file.length();
        // TODO: Invoke service.append(file).
        // TODO: Verify service invoked write(OutputStream) once.
        // TODO: Verify file.length increased by 12.
    }

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} returns given {@code file} arguments.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file) returns file")
    @Test
    void append_ReturnFile_(@TempDir File tempDir) throws IOException {
        var service = helloWorld();
        var file = createTempFile("tmp", null, tempDir);
        var actual = helloWorld().append(file);
        assertSame(file, actual);
    }
}
