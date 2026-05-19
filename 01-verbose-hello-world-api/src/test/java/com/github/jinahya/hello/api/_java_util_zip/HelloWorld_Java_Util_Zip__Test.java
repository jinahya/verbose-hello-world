package com.github.jinahya.hello.api._java_util_zip;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Java_Util_Zip__Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        HelloWorldTestUtils.write_stream_will_write_actual_hello_world_bytes(service());
    }

    @Nested
    class ZipOutputStream_Test {

        @Test
        void __ZipInputStream() throws IOException {
            // ------------------------------------------------------------------------------- given
            // -------------------------------------------------------------------------------- when
            try (var baos = new ByteArrayOutputStream();
                 var zos = new ZipOutputStream(baos)) {
                zos.putNextEntry(new ZipEntry("hello-world.txt"));
                service().write(zos);
                zos.closeEntry();
                zos.flush();
                // ---------------------------------------------------------------------------- then
                try (var bais = new ByteArrayInputStream(baos.toByteArray());
                     var zis = new ZipInputStream(bais)) {
                    final var entry = zis.getNextEntry();
                    assert entry != null;
                    Assertions.assertArrayEquals(
                            HelloWorldTestUtils.hello_world_byte_array(),
                            zis.readAllBytes()
                    );
                }
            }
        }

        @Test
        void __ZipFile() throws IOException {
            // ----------------------------------------------------------------------------- given
            final var tempFile = File.createTempFile("tmp", null, tempDir);
            final var entryName = "hello-world.bin";
            // ------------------------------------------------------------------------------ when
            try (var fos = new FileOutputStream(tempFile);
                 var zos = new ZipOutputStream(fos)) {
                zos.putNextEntry(new ZipEntry(entryName));
                service().write(zos);
                zos.closeEntry();
                zos.flush();
            }
            // ------------------------------------------------------------------------------ then
            try (var zipFile = new ZipFile(tempFile)) {
                final var entry = zipFile.getEntry(entryName);
                assert entry != null;
                try (var in = zipFile.getInputStream(entry)) {
                    Assertions.assertArrayEquals(
                            HelloWorldTestUtils.hello_world_byte_array(),
                            in.readAllBytes()
                    );
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class DeflaterOutputStream_Test {

        @MethodSource(
                "com.github.jinahya.hello.api._java_util_zip.HelloWorld_SetInput_Deflater__Test#levelStream"
        )
        @ParameterizedTest
        void __(final int level) throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var baos = new ByteArrayOutputStream();
            // -------------------------------------------------------------------------------- when
            try (var deflater = new Deflater(level);
                 var stream = new DeflaterOutputStream(baos, deflater)) {
                service.write(stream);
            }
            final var bytes = baos.toByteArray();
            System.out.printf("%2d (%2d) %s%n", level, bytes.length,
                              Base64.getEncoder().encodeToString(bytes));
            // -------------------------------------------------------------------------------- then
            try (var inflater = new InflaterInputStream(
                    new ByteArrayInputStream(baos.toByteArray()))) {
                Assertions.assertArrayEquals(
                        HelloWorldTestUtils.hello_world_byte_array(),
                        inflater.readAllBytes()
                );
            }
        }

        @Test
        void __bytecode() throws IOException {
            // ------------------------------------------------------------------------------- given
            final byte[] input;
            try (var in = HelloWorld.class.getResourceAsStream("HelloWorld.class")) {
                input = in.readAllBytes();
            }
            System.out.printf("input: %d bytes%n", input.length);
            // -------------------------------------------------------------------------------- when
            for (int level = 0; level <= 9; level++) {
                final var baos = new ByteArrayOutputStream();
                try (var deflater = new Deflater(level);
                     var stream = new DeflaterOutputStream(baos, deflater)) {
                    stream.write(input);
                }
                final var compressed = baos.toByteArray();
                System.out.printf("%2d: %6d -> %5d (%5.2f%%)%n",
                                  level, input.length, compressed.length,
                                  100.0 * compressed.length / input.length);
                // ----------------------------------------------------------------------------- then
                try (var inflater = new InflaterInputStream(
                        new ByteArrayInputStream(compressed))) {
                    Assertions.assertArrayEquals(input, inflater.readAllBytes());
                }
            }
        }
    }

    @Nested
    class GZIPOutputStream_Test {

        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var baos = new ByteArrayOutputStream();
            // -------------------------------------------------------------------------------- when
            try (var gzipos = new GZIPOutputStream(baos)) {
                service.write(gzipos);
            }
            final var bytes = baos.toByteArray();
            System.out.printf("(%2d) %s%n", bytes.length,
                              Base64.getEncoder().encodeToString(bytes));
            // -------------------------------------------------------------------------------- then
            try (var gzipis = new GZIPInputStream(
                    new ByteArrayInputStream(baos.toByteArray()))) {
                Assertions.assertArrayEquals(
                        HelloWorldTestUtils.hello_world_byte_array(),
                        gzipis.readAllBytes()
                );
            }
        }
    }
}
