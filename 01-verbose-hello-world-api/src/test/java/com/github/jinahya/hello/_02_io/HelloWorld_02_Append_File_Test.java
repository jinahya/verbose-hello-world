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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction.MockInitializer;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.assertSame;

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
    void beforeEach() throws IOException {
        writeStream_willWrite12Bytes();
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
    void _InvokeWriteWithFileOutputStreamOfAppendingMode_() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = Mockito.mock(File.class);
        final var initializer = (MockInitializer<FileOutputStream>) (m, c) -> {
            Assertions.assertEquals(
                    FileOutputStream.class.getConstructor(File.class, boolean.class),
                    c.constructor()
            );
            final var arguments = c.arguments();
            Assertions.assertEquals(2, arguments.size());
            Assertions.assertEquals(file, arguments.get(0));
            Assertions.assertTrue((boolean) arguments.get(1));
            Assertions.assertEquals(1, c.getCount());
        };
        try (var construction = Mockito.mockConstruction(FileOutputStream.class, initializer)) {
            // -------------------------------------------------------------------------------- when
            final var result = service.append(file);
            // ---------------------------------------------------------------------------------then
            // TODO: assert, construction.constructed().size() is equal to 1
            // TODO: verify, service.write(construction.constructed().get(0)) invoked, once.
            // TODO: verify, construction.constructed().get(0).flush() invoked, once.
            // TODO: verify, construction.constructed().get(0).close() invoked, once.
            assertSame(file, result);
        }
    }
}
