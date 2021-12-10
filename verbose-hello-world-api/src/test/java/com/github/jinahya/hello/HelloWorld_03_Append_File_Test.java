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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A class for testing {@link HelloWorld#append(File)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_03_Append_File_Arguments_Test
 */
@Slf4j
class HelloWorld_03_Append_File_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} method invokes {@link HelloWorld#write(OutputStream)} method
     * with an {@link java.io.FileOutputStream} and asserts {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes are
     * appended to the end of the {@code file}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file) invokes write(stream) and 12 bytes are appended")
    @Test
    void append_InvokeWriteStream_(@TempDir final File tempDir) throws IOException {
        final File file = File.createTempFile("tmp", null, tempDir);
        final long length = file.length();
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} returns given {@code file} arguments.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file) returns file")
    @Test
    void append_ReturnFile_() throws IOException {
        final File file = Mockito.mock(File.class);
        final File actual = helloWorld().append(file);
        Assertions.assertSame(file, actual);
    }
}
