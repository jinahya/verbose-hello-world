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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static org.mockito.quality.Strictness.LENIENT;

/**
 * A class for unit-testing {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@MockitoSettings(strictness = LENIENT)
@ExtendWith({MockitoExtension.class})
@Slf4j
class HelloWorld_AppendFileTest extends AbstractHelloWorldTest {

    /**
     * Asserts {@link HelloWorld#append(File)} method throws a {@link NullPointerException} when {@code file} argument
     * is {@code null}.
     */
    @DisplayName("append(file) throws a NullPointerException when the file is null")
    @Test
    void appendFile_NullPointerException_FileIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> helloWorld.append((File) null));
    }

    /**
     * Asserts {@link HelloWorld#append(File)} method invokes {@link HelloWorld#write(OutputStream)} method.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file) invokes write(stream)")
    @Test
    void appendFile_InvokeWriteStream_(final @TempDir File tempDir) throws IOException {
        final File file = File.createTempFile("tmp", null, tempDir);
    }

    /**
     * Asserts {@link HelloWorld#append(File)} method returns given file.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(file) returns given file")
    @Test
    void appendFile_ReturnFile_(final @TempDir File tempDir) throws IOException {
        final File file = File.createTempFile("tmp", null, tempDir);
    }
}
