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
import com.github.jinahya.hello.miscellaneous.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static com.github.jinahya.hello.api.HelloWorld__TestConstants.*;
import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static java.io.File.*;
import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class exercising {@link HelloWorld#write(Writer) write(writer)} against various real
 * {@link Writer} implementations and charsets.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(writer)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_Writer__Test extends HelloWorld__Test {

    /**
     * The {@link TempDir} shared by tests in this class.
     */
    @TempDir
    private static File tempDir;

    private static Stream<Charset> charsetStream() {
        return _Java_Nio_Charset_TestUtils.charsetStream();
    }

    private static void print(final Charset charset, final byte[] bytes) {
        final var n = Math.min(4, bytes.length);
        final var hex = HexFormat.of().withDelimiter(" ").formatHex(bytes, 0, n);
        log.debug("charset: {} ({}) [{}]", String.format("%14s", charset), bytes.length, hex);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs the service so that {@code write(writer)} writes the {@code hello-world-string} before
     * each test.
     *
     * @throws IOException if an I/O error occurs while stubbing.
     */
    @BeforeEach
    void __stubService() throws IOException {
        write_writer_writes_hello_world_string(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("output stream writer")
    @Nested
    class OutputStreamWriter_Test {

        private static Stream<Charset> charsetStream() {
            return HelloWorld_Write_Writer__Test.charsetStream();
        }

        /**
         * Verifies that the method writes the {@code hello-world-string} through an
         * {@link OutputStreamWriter} configured with the given {@code charset}.
         *
         * @param charset the {@link Charset} under test.
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("""
                should write <hello-world-string> through an
                <OutputStreamWriter> with the <charset>""")
        @MethodSource({"charsetStream"})
        @ParameterizedTest
        void __(final Charset charset) throws IOException {
            try (var in = new ByteArrayOutputStream();
                 var writer = new OutputStreamWriter(in, charset)) {
                service().write(writer).flush();
                final var buf = in.toByteArray();
                print(charset, buf);
                try (var out = new ByteArrayInputStream(buf);
                     var reader = new InputStreamReader(out, charset)) {
                    final var string = reader.readAllAsString();
                    assertEquals(HELLO_WORLD_STRING, string);
                }
            }
        }

        /**
         * Verifies that the {@code hello-world-string} round-trips through an
         * {@link OutputStreamWriter} / {@link InputStreamReader} pair in {@code US_ASCII}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("should round-trip <hello-world-string> through an <OutputStreamWriter>")
        @Test
        void __() throws IOException {
            try (var baos = new ByteArrayOutputStream();
                 var writer = new OutputStreamWriter(baos, US_ASCII)) {
                writer.write(HELLO_WORLD_STRING);
                writer.flush();
                try (var bais = new ByteArrayInputStream(baos.toByteArray());
                     final var reader = new InputStreamReader(bais, US_ASCII)) {
                    final var string = reader.readAllAsString();
                    assertEquals(HELLO_WORLD_STRING, string);
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("file writer")
    @Nested
    class FileWriter_Test {

        private static Stream<Charset> charsetStream() {
            return HelloWorld_Write_Writer__Test.charsetStream();
        }

        /**
         * Verifies that the method writes the {@code hello-world-string} through a
         * {@link FileWriter} configured with the given {@code charset}.
         *
         * @param charset the {@link Charset} under test.
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("should write <hello-world-string> through a <FileWriter> with the <charset>")
        @MethodSource({"charsetStream"})
        @ParameterizedTest
        void __(final Charset charset) throws IOException {
            final var file = createTempFile("tmp", null, tempDir);
            try (var writer = new FileWriter(file, charset)) {
                service().write(writer).flush();
            }
            print(charset, Files.readAllBytes(file.toPath()));
            try (var reader = new FileReader(file, charset)) {
                final var string = reader.readAllAsString();
                assertEquals(HELLO_WORLD_STRING, string);
            }
        }

        /**
         * Verifies that the {@code hello-world-string} round-trips through a {@link FileWriter} in
         * appending mode using {@code US_ASCII}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("should round-trip <hello-world-string> through a <FileWriter>")
        @Test
        void __() throws IOException {
            final var file = createTempFile("tmp", null, tempDir);
            try (var writer = new FileWriter(file, US_ASCII, true)) {
                writer.write(HELLO_WORLD_STRING);
                writer.flush();
            }
            assertEquals(HelloWorld.BYTES, file.length());
            assertEquals(HELLO_WORLD_STRING, Files.readString(file.toPath()));
        }

        @Nested
        class Append_Test {

            void __() throws IOException {
                final var tempFile = File.createTempFile("tmp", null, tempDir);
                try (var writer = new FileWriter(tempFile)) {
                    service().write(writer).flush();
                }
                assertEquals(HelloWorld.BYTES, tempFile.length());
            }
        }
    }
}
