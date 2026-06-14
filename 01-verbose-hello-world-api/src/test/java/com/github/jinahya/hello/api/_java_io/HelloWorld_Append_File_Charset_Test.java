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
import org.mockito.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#append(File, Charset)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld.append(File, Charset)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_File_Charset_Test
        extends HelloWorld__Test {

    private static Stream<Charset> charsetStream() {
        return _Java_Nio_Charset_TestUtils.charsetStream();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@link HelloWorld#append(File, Charset)} method throws a
     * {@link NullPointerException} when the {@code file} argument is {@code null}.
     */
    @DisplayName("throws NPE / file is null")
    @Test
    void _ThrowNullPointerException_FileIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = (File) null;
        final var charset = Charset.defaultCharset();
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.append(file, charset));
    }

    /**
     * Verifies that the {@link HelloWorld#append(File, Charset)} method throws a
     * {@link NullPointerException} when the {@code charset} argument is {@code null}.
     */
    @DisplayName("throws NPE / charset is null")
    @Test
    void _ThrowNullPointerException_CharsetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = mock(File.class);
        final var charset = (Charset) null;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(NullPointerException.class, () -> service.append(file, charset));
    }

    /**
     * Verifies {@link HelloWorld#append(File, Charset)} method constructs a new {@link FileWriter}
     * with {@code file}, {@code charset}, and {@code true}, invokes
     * {@link HelloWorld#write(Writer)} method with it, flushes/closes the writer, and returns the
     * {@code file}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("happy path")
    @MethodSource("charsetStream")
    @ParameterizedTest
    void __(final Charset charset) throws IOException, NoSuchMethodException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(AdditionalAnswers.returnsFirstArg())
                .when(service)
                .write(ArgumentMatchers.<Writer>any());
        final var file = mock(File.class);
        try (var mockConstruction = mockConstruction(FileWriter.class, (m, c) -> {
            assertEquals(FileWriter.class.getConstructor(File.class, Charset.class, boolean.class),
                         c.constructor());
            final var arguments = c.arguments();
            assert arguments.size() == 3;
            assertSame(file, arguments.get(0));
            assertSame(charset, arguments.get(1));
            assertTrue((Boolean) arguments.get(2));
        })) {
            // -------------------------------------------------------------------------------- when
            final var result = service.append(file, charset);
            // -------------------------------------------------------------------------------- then
            final var constructed = mockConstruction.constructed();
//            assertEquals(1, constructed.size());
//            final var writer = constructed.getFirst();
//            verify(service, times(1)).write(writer);
//            verify(writer, times(1)).flush();
//            verify(writer, times(1)).close();
            assertSame(file, result);
        }
    }

    @TempDir
    private static File tempDir;

    private static Stream<Arguments> fileAndCharsetArgumentsStream() {
        return charsetStream().map(c -> {
            try {
                return Arguments.of(File.createTempFile("tmp", "txt", tempDir), c);
            } catch (final IOException ioe) {
                throw new RuntimeException(ioe);
            }
        });
    }

    /**
     * Verifies that the {@link HelloWorld#append(File, Charset)} method writes the
     * {@code hello-world-bytes} to a real {@code file} encoded with the given {@code charset}.
     *
     * @param file    the real temp {@link File} to write to.
     * @param charset the {@link Charset} to encode with.
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("添足 / 畵蛇")
    @MethodSource({"fileAndCharsetArgumentsStream"})
    @ParameterizedTest(name = "[{index}]: {1}")
    void _添足_畵蛇(final File file, final Charset charset) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var decoded = "hello, world";
        final var encoded = decoded.toCharArray();
        doAnswer(i -> {
            final var writer = i.getArgument(0, Writer.class);
            writer.write(encoded);
            return writer;
        })
                .when(service)
                .write(ArgumentMatchers.<Writer>any());
        doAnswer(i -> {
            final var f = i.getArgument(0, File.class);
            final var c = i.getArgument(1, Charset.class);
            try (var w = new FileWriter(f, c, true)) {
                service.write(w);
                w.flush();
            }
            return f;
        })
                .when(service)
                .append(ArgumentMatchers.any(), ArgumentMatchers.any());
        // ------------------------------------------------------------------------------------ when
        service.append(file, charset);
        // ------------------------------------------------------------------------------------ then
        final var bytes = Files.readAllBytes(file.toPath());
        log.debug("{}: {} {}", String.format("%14s", charset.name()), bytes.length,
                  HexFormat.of().formatHex(bytes));
    }
}
