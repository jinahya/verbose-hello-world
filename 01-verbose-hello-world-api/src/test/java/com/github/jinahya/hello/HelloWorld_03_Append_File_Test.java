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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

/**
 * A class for testing {@link HelloWorld#append(File)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_03_Append_File_Arguments_Test
 */
@DisplayName("append(file)")
@Slf4j
class HelloWorld_03_Append_File_Test extends HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#write(OutputStream) write(stream)} method to write
     * {@value HelloWorld#BYTES} bytes to the {@code stream}, and returns the {@code stream}.
     */
    @DisplayName("[stubbing] write(stream) writes 12 bytes and returns stream")
    @org.junit.jupiter.api.BeforeEach
    void _Write12BytesReturnArray_WriteStream() throws IOException {
        doAnswer(i -> {
            OutputStream stream = i.getArgument(0); // <1>
            for (int j = 0; j < BYTES; j++) {       // <2>
                stream.write(0);                    // <3>
            }
            return stream;
        }).when(service()).write(any(OutputStream.class));
    }

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} method invokes
     * {@link HelloWorld#write(OutputStream) write(stream)} method with an instance of
     * {@link java.io.FileOutputStream}, and asserts {@value HelloWorld#BYTES} bytes are appended to
     * the {@code file}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("invokes write(FileOutputStream)"
                 + ", 12 bytes are appended to the file")
    @Test
    void _InvokeWriteStreamAnd12BytesAppended_(@TempDir File tempDir) throws IOException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: File
        var file = createTempFile("tmp", null, tempDir);
        var length = file.length();
        // WHEN
        service.append(file);
        // THEN: once, service.write(FileOutputStream) invoked
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
    void _ReturnFile_(@TempDir File tempDir) throws IOException {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: File
        var file = createTempFile("tmp", null, tempDir);
        // WHEN
        var actual = service.append(file);
        // THEN
        assertSame(file, actual);
    }
}
