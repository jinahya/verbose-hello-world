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

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api._Java_Nio_Charset_TestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.stream.Stream;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_Writer_畵蛇添足_Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    private static Stream<Charset> charsetStream() {
        return _Java_Nio_Charset_TestUtils.charsetStream();
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        HelloWorldTestUtils.write_writer_writes_hello_world_string(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class OutputStreamWriterTest {

        private static Stream<Charset> charsetStream() {
            return HelloWorld_Write_Writer_畵蛇添足_Test.charsetStream();
        }

        @MethodSource({"charsetStream"})
        @ParameterizedTest
        void __(final Charset cs) throws IOException {
            try (var in = new ByteArrayOutputStream();
                 var writer = new OutputStreamWriter(in, cs)) {
                service().write(writer).flush();
                try (var out = new ByteArrayInputStream(in.toByteArray());
                     var reader = new InputStreamReader(out, cs)) {
                    final var string = reader.readAllAsString();
                    Assertions.assertEquals("hello, world", string);
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class FileWriterTest {

        private static Stream<Charset> charsetStream() {
            return HelloWorld_Write_Writer_畵蛇添足_Test.charsetStream();
        }

        @MethodSource({"charsetStream"})
        @ParameterizedTest
        void __(final Charset charset) throws IOException {
            final var file = File.createTempFile("tmp", null, tempDir);
            try (var writer = new FileWriter(file, charset)) {
                service().write(writer).flush();
            }
            try (var reader = new FileReader(file, charset)) {
                final var string = reader.readAllAsString();
                Assertions.assertEquals("hello, world", string);
            }
        }
    }
}
