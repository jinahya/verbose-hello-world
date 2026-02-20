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
import com.github.jinahya.hello.api._java_nio._Java_Nio_TestUtils;
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
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

    @TempDir
    private static File tempDir;

    private static Stream<Charset> charsetStream() {
        return _Java_Nio_TestUtils.charsetStream();
    }

    private static Stream<Arguments> fileAndCharsetArgumentsStream() {
        return charsetStream().map(
                c -> {
                    try {
                        return Arguments.of(File.createTempFile("tmp", "txt", tempDir), c);
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
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
     * Verifies {@link HelloWorld#append(File, Charset)} method constructs a new
     * {@link FileOutputStream} with {@code file} and {@code true}, constructs a new
     * {@link OutputStreamWriter} with the {@code stream} and the {@code charset}, invokes
     * {@link HelloWorld#write(OutputStreamWriter)} method with it, flushes/closes the writer, and
     * returns the {@code file}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should create a <new FileOutputStream> as <appending mode>,
            should create a <new OutputStreamWriter> with the stream and charset,
            should invoke <write(writer)> method with it,
            and should <flushes/closes> the writer"""
    )
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> i.getArgument(0))       // <1>
                .when(service)
                .write(ArgumentMatchers.<OutputStreamWriter>notNull());
        final var file = Mockito.mock(File.class);    // <2>
        final var charset = Charset.defaultCharset(); // <3>
        try (var c1 = Mockito.mockConstruction(FileOutputStream.class, (m, c) -> {   // <1>
            final var arguments = c.arguments();
            Assertions.assertEquals(2, arguments.size());
            Assertions.assertSame(file, arguments.get(0));
            Assertions.assertTrue((Boolean) arguments.get(1));
        });
             var c2 = Mockito.mockConstruction(OutputStreamWriter.class, (m, c) -> { // <2>
                 final var arguments = c.arguments();
                 Assertions.assertEquals(2, arguments.size());
                 Assertions.assertSame(c1.constructed().getFirst(), arguments.get(0));
                 Assertions.assertSame(charset, arguments.get(1));
             })) {
            // -------------------------------------------------------------------------------- when
            final var result = service.append(file, charset);
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(1, c1.constructed().size()); // <1>
            Assertions.assertEquals(1, c2.constructed().size()); // <2>
            final var writer = c2.constructed().getFirst();          // <1>
            Mockito.verify(service, Mockito.times(1)).write(writer); // <2>
            Mockito.verify(writer, Mockito.times(1)).flush();        // <3>
            Mockito.verify(writer, Mockito.times(1)).close();        // <4>
            Assertions.assertSame(file, result);
        }
    }

//    private byte[] getFirst4Bytes(final File file) throws IOException {
//        assert file.length() >= 4;
//        try (var fis = new FileInputStream(file)) {
//            return fis.readNBytes(4);
//        }
//    }

    @MethodSource({"fileAndCharsetArgumentsStream"})
    @ParameterizedTest(name = "[{index}]: {1}")
    void _添足_畵蛇(final File file, final Charset charset) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> {
                    final var writer = i.getArgument(0, OutputStreamWriter.class);
                    writer.write(hello_world_char_array());
                    return writer;
                })
                .when(service)
                .write(ArgumentMatchers.<OutputStreamWriter>notNull());
        // ------------------------------------------------------------------------------------ when
        service.append(file, charset);
        // ------------------------------------------------------------------------------------ then
        final var length = file.length();
        Assertions.assertTrue(length >= HelloWorld.BYTES);
//        log.debug("{}: {} {}", String.format("%14s", charset.name()), length,
//                  HexFormat.of().formatHex(getFirst4Bytes(file)));
        log.debug("{}: {} {}", String.format("%14s", charset.name()), length,
                  HexFormat.of().formatHex(Files.readAllBytes(file.toPath())));
        ;
    }
}
