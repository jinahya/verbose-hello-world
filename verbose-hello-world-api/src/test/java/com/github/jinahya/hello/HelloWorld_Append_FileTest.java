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
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A class for testing {@link HelloWorld#append(File)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@Slf4j
class HelloWorld_Append_FileTest extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#append(File)} method throws a {@link NullPointerException} when {@code file} argument
     * is {@code null}.
     */
    @DisplayName("append(file) throws a NullPointerException when the file is null")
    @Test
    void appendFile_NullPointerException_FileIsNull() {
        assertThrows(NullPointerException.class, () -> helloWorld.append((File) null));
    }

    /**
     * Asserts {@link HelloWorld#append(File)} method invokes {@link HelloWorld#write(OutputStream)} method and asserts
     * {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes are written to the {@code file}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file) invokes write(stream) and 12 bytes are written")
    @Test
    void appendFile_InvokeWriteStream_(final @TempDir File tempDir) throws IOException {
        final File file = createTempFile("tmp", null, tempDir);
        final long length = file.length();
    }
}
