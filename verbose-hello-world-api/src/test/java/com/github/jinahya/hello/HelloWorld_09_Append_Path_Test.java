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

import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A class for testing {@link HelloWorld#append(Path)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_09_Append_Path_Arguments_Test
 */
@Slf4j
class HelloWorld_09_Append_Path_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#append(Path) append(path)} method invokes {@link HelloWorld#write(WritableByteChannel)
     * write(channel)} method and asserts {@value com.github.jinahya.hello.HelloWorld#BYTES} bytes are appended to the
     * {@code path}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(path) invokes write(channel), 12 bytes are appended")
    @Test
    void append_InvokeWriteChannel12BytesWritten_(final @TempDir Path tempDir) throws IOException {
        final Path path = Files.createTempFile(tempDir, null, null);
        final long size = Files.size(path);
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#append(Path) append(path)} method returns the {@code path} argument.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("append(path) returns path")
    @Test
    void append_ReturnPath_() throws IOException {
        final Path expected = Mockito.mock(Path.class);
        final Path actual = helloWorld().append(expected);
        Assertions.assertSame(expected, actual);
    }
}
