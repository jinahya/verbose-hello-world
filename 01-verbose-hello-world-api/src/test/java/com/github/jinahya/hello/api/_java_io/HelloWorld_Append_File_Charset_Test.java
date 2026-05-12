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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api._Java_Nio_Charset_TestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HexFormat;
import java.util.stream.Stream;

/**
 * A class for testing {@link HelloWorld#append(File, Charset)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(file, charset)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Append_File_Charset_Test
        extends HelloWorldTest {

    private static Stream<Charset> charsetStream() {
        return _Java_Nio_Charset_TestUtils.charsetStream();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@link HelloWorld#append(File, Charset)} method throws a
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
        final var charset = Charset.defaultCharset();
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(file, charset)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#append(File, Charset)} method throws a
     * {@link NullPointerException} when the {@code charset} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <charset> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_CharsetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var file = Mockito.mock(File.class);
        final var charset = (Charset) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.append(file, charset)
        );
    }

    /**
     * Verifies {@link HelloWorld#append(File, Charset)} method constructs a new {@link FileWriter}
     * with {@code file}, {@code charset}, and {@code true}, invokes
     * {@link HelloWorld#write(Writer)} method with it, flushes/closes the writer, and returns the
     * {@code file}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should create a <new FileOutputStream> as <appending mode>,
            should create a <new OutputStreamWriter> with the stream and charset,
            should invoke <write(writer)> method with it,
            and should <flushes/closes> the writer"""
    )
    @MethodSource("charsetStream")
    @ParameterizedTest
    void __(final Charset charset) throws IOException, NoSuchMethodException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(AdditionalAnswers.returnsFirstArg())
                .when(service)
                .write(ArgumentMatchers.<Writer>any());
        final var file = Mockito.mock(File.class);
        try (var mockConstruction = Mockito.mockConstruction(FileWriter.class, (m, c) -> {
            Assertions.assertEquals(
                    FileWriter.class.getConstructor(File.class, Charset.class, boolean.class),
                    c.constructor()
            );
            final var arguments = c.arguments();
            assert arguments.size() == 3;
            Assertions.assertSame(file, arguments.get(0));
            Assertions.assertSame(charset, arguments.get(1));
            Assertions.assertTrue((Boolean) arguments.get(2));
        })) {
            // -------------------------------------------------------------------------------- when
            final var result = service.append(file, charset);
            // -------------------------------------------------------------------------------- then
            final var constructed = mockConstruction.constructed();
//            Assertions.assertEquals(1, constructed.size());
//            final var writer = constructed.getFirst();
//            Mockito.verify(service, Mockito.times(1)).write(writer);
//            Mockito.verify(writer, Mockito.times(1)).flush();
//            Mockito.verify(writer, Mockito.times(1)).close();
            Assertions.assertSame(file, result);
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

    @MethodSource({"fileAndCharsetArgumentsStream"})
    @ParameterizedTest(name = "[{index}]: {1}")
    void _添足_畵蛇(final File file, final Charset charset) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var decoded = "hello, world";
        final var encoded = decoded.toCharArray();
        Mockito.doAnswer(i -> {
                    final var writer = i.getArgument(0, Writer.class);
                    writer.write(encoded);
                    return writer;
                })
                .when(service)
                .write(ArgumentMatchers.<Writer>any());
        Mockito.doAnswer(i -> {
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
