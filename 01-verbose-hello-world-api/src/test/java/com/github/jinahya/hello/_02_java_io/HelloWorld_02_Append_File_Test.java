package com.github.jinahya.hello._02_java_io;

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
import com.github.jinahya.hello.HelloWorldTest;
import com.github.jinahya.hello.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
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
class HelloWorld_02_Append_File_Test extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#append(File) append(file)} method throws a
     * {@link NullPointerException} when the {@code file} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <file> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_FileIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = (File) null;
        // ------------------------------------------------------------------------------- when/then
        // assert, <service.append(file)> will throw a <NullPointerException>
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(file)
        );
    }

    /**
     * Verifies {@link HelloWorld#append(File) append(file)} method constructs a new
     * {@link FileOutputStream} with {@code file} and {@code true}, invokes
     * {@link HelloWorld#write(OutputStream) write(stream)} method, flushes/closes the stream, and
     * returns the {@code file}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should create a <new FileOutputStream> as <appending mode>
            and invoke <write(stream)> method with it
            and <flushes/closes> the stream"""
    )
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.write(stream)> will just return the <stream>
        Mockito.doAnswer(i -> i.getArgument(0, OutputStream.class))
                .when(service)
                .write(ArgumentMatchers.any(OutputStream.class));
        final var file = Mockito.mock(File.class);
        // mock, constructions of <FileOutputStream>
        final var contexts = new LinkedHashMap<FileOutputStream, MockedConstruction.Context>();
        try (var mock = Mockito.mockConstruction(FileOutputStream.class, contexts::put)) {
            // -------------------------------------------------------------------------------- when
            final var result = service.append(file);
            // ---------------------------------------------------------------------------------then
            // verify, <new FileOutputStream(file, true)<stream>> invoked, once

            // verify, <service.write(<stream>)> invoked, once

            // verify, <<stream>.flush()> invoked, once

            // verify, <<stream>.close()> invoked, once

            // assert, <result> is same as <file>
            Assertions.assertSame(file, result);
        }
    }

    @畵蛇添足("testing with an existing file doesn't add any value")
    @DisplayName("<file>'s length should be increased by <12>")
    @Test
    void _添足_畵蛇(@TempDir final File dir) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // stub, <service.append(file)> will append <12> bytes, and will return the <file>
        Mockito.doAnswer(i -> {
                    var file = i.getArgument(0, File.class);
                    try (var stream = new FileOutputStream(file, true)) {
                        stream.write(new byte[HelloWorld.BYTES]);
                        stream.flush();
                    }
                    return file;
                })
                .when(service)
                .append(ArgumentMatchers.<File>argThat(File::isFile));
        final var file = File.createTempFile("tmp", null, dir);
        final var length = file.length();
        // ------------------------------------------------------------------------------------ when
        final var result = service.append(file);
        // ------------------------------------------------------------------------------------ then
        // assert, <file>'s <length> increased by <12>
        Assertions.assertEquals(
                length + HelloWorld.BYTES,
                file.length()
        );
        // assert, <result> is same as <file>
        Assertions.assertSame(file, result);
    }
}
