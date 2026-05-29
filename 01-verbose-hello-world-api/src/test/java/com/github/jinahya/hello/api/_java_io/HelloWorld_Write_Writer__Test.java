package com.github.jinahya.hello.api._java_io;

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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.stream.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static java.io.File.*;
import static org.junit.jupiter.api.Assertions.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_Writer__Test extends HelloWorld__Test {

    @TempDir
    private static File tempDir;

    private static Stream<Charset> charsetStream() {
        return _Java_Nio_Charset_TestUtils.charsetStream();
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        write_writer_writes_hello_world_string(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class OutputStreamWriter_Test {

        private static Stream<Charset> charsetStream() {
            return HelloWorld_Write_Writer__Test.charsetStream();
        }

        @MethodSource({"charsetStream"})
        @ParameterizedTest
        void __(final Charset charset) throws IOException {
            try (var in = new ByteArrayOutputStream();
                 var writer = new OutputStreamWriter(in, charset)) {
                service().write(writer).flush();
                final var buf = in.toByteArray();
                log.debug("charset: {} ({})", String.format("%14s", charset), buf.length);
                try (var out = new ByteArrayInputStream(buf);
                     var reader = new InputStreamReader(out, charset)) {
                    final var string = reader.readAllAsString();
                    assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING, string);
                }
            }
        }

        @Test
        void __() throws IOException {
            try (var baos = new ByteArrayOutputStream();
                 var writer = new OutputStreamWriter(baos, StandardCharsets.US_ASCII)) {
                writer.write(HelloWorld__TestConstants.HELLO_WORLD_STRING);
                writer.flush();
                try (var bais = new ByteArrayInputStream(baos.toByteArray());
                     final var reader = new InputStreamReader(bais, StandardCharsets.US_ASCII)) {
                    final var string = reader.readAllAsString();
                    assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING, string);
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class FileWriter_Test {

        private static Stream<Charset> charsetStream() {
            return HelloWorld_Write_Writer__Test.charsetStream();
        }

        @MethodSource({"charsetStream"})
        @ParameterizedTest
        void __(final Charset charset) throws IOException {
            final var file = createTempFile("tmp", null, tempDir);
            try (var writer = new FileWriter(file, charset)) {
                service().write(writer).flush();
            }
            log.debug("charset: {} ({})", String.format("%14s", charset), file.length());
            try (var reader = new FileReader(file, charset)) {
                final var string = reader.readAllAsString();
                assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING, string);
            }
        }

        @Test
        void __() throws IOException {
            final var file = createTempFile("tmp", null, tempDir);
            try (var writer = new FileWriter(file, StandardCharsets.US_ASCII, true)) {
                writer.write(HelloWorld__TestConstants.HELLO_WORLD_STRING);
                writer.flush();
            }
            assertEquals(HelloWorld.BYTES, file.length());
            assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING,
                         Files.readString(file.toPath()));
        }
    }
}
