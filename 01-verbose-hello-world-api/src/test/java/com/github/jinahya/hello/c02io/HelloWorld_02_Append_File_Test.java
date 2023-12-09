package com.github.jinahya.hello.c02io;

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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction.MockInitializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.io.File.createTempFile;
import static java.util.concurrent.ThreadLocalRandom.current;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;

/**
 * A class for testing {@link HelloWorld#append(File) append(file)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_02_Append_File_Arguments_Test
 */
@DisplayName("append(file)")
@Slf4j
class HelloWorld_02_Append_File_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach()
            throws IOException {
        _stub_WriteStream_ToWrite12BytesAndReturnTheStream();
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
    void _InvokeWriteWithFileOutputStreamWithAppendingMode_()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var file = mock(File.class);
        MockInitializer<FileOutputStream> initializer = (m, c) -> {
            // new FileOutputStream(file, true)
            assertEquals(FileOutputStream.class.getConstructor(File.class, boolean.class),
                         c.constructor());
            var arguments = c.arguments();
            assert arguments.size() == 2;
            assertEquals(file, arguments.get(0));
            assertTrue((boolean) arguments.get(1));
            assertEquals(1, c.getCount());
//            doNothing().when(m).write(any(byte[].class));
        };
        try (var construction = mockConstruction(FileOutputStream.class, initializer)) {
            // -------------------------------------------------------------------------------- when
            var result = service.append(file);
            // ---------------------------------------------------------------------------------THEN
            var constructed = construction.constructed();
            // TODO: Assert, constructed.size() is equal to 1.
            // TODO: Verify, service.write(constructed.get(0)) invoked, once.
            // TODO: Verify, constructed.get(0).flush() invoked, once.
            // TODO: Verify, constructed.get(0).close() invoked, once.
            assertSame(file, result);
        }
    }

    /**
     * Asserts {@link HelloWorld#append(File) append(file)} method appends {@value HelloWorld#BYTES}
     * bytes to the end of the {@code file}.
     *
     * @param tempDir a temporary directory to test with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("(file) -> 12 bytes are appended to the file")
    @Test
    void _12BytesAppended_(@TempDir File tempDir)
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var file = createTempFile("tmp", null, tempDir);
        if (current().nextBoolean()) {
            // TODO: (Optional) Write some bytes to the file
        }
        var length = file.length();
        // ------------------------------------------------------------------------------------ when
        var result = service.append(file);
        // ------------------------------------------------------------------------------------ then
        // TODO: Assert, 12 bytes are appended to the end of the file.
        assertSame(file, result);
    }
}
