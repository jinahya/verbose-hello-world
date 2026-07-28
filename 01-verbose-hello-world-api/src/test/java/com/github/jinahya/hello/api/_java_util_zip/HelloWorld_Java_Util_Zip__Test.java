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

import com.github.jinahya.hello.api.*;
import com.github.jinahya.hello.api.annotations.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;

import java.io.*;
import java.util.zip.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for exploring {@link HelloWorld#write(OutputStream) write(stream)} method with real
 * {@code java.util.zip} streams ({@link ZipOutputStream}, {@link DeflaterOutputStream},
 * {@link GZIPOutputStream}).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideNameFromPublishing
@DisplayName("java.util.zip")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Java_Util_Zip__Test extends HelloWorld__Test {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        set_array_sets_hello_world_bytes(service());
        write_stream_writes_hello_world_bytes(service());
    }

    @DisplayName("ZipOutputStream")
    @Nested
    class ZipOutputStream_Test {

        /**
         * Verifies that a {@code "hello, world"} entry written through
         * {@link HelloWorld#write(OutputStream) write(stream)} into a {@link ZipOutputStream}
         * round-trips through {@link ZipInputStream}.
         */
        @DisplayName("ZipInputStream")
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
                    assertArrayEquals(
                            hello_world_byte_array(),
                            zis.readAllBytes()
                    );
                }
            }
        }

        /**
         * Verifies that a {@code "hello, world"} entry written through
         * {@link HelloWorld#write(OutputStream) write(stream)} into a file-backed
         * {@link ZipOutputStream} round-trips through {@link ZipFile}.
         */
        @DisplayName("ZipFile")
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
                    assertArrayEquals(
                            hello_world_byte_array(),
                            in.readAllBytes()
                    );
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
//    @DisplayName("DeflaterOutputStream")
//    @Nested
//    class DeflaterOutputStream_Test {
//
//        /**
//         * Verifies that {@code "hello, world"} round-trips through a {@link DeflaterOutputStream}
//         * at the given compression {@code level} and back through an {@link InflaterInputStream}.
//         *
//         * @param level a deflater compression level.
//         */
//        @DisplayName("""
//                should round-trip <hello, world>
//                through <DeflaterOutputStream> at the given <level>""")
//        @MethodSource(
//                "com.github.jinahya.hello.api._java_util_zip.HelloWorld_SetInput_Deflater__Test#levelStream"
//        )
//        @ParameterizedTest
//        void __(final int level) throws IOException {
//            // ------------------------------------------------------------------------------- given
//            final var service = service();
//            ;
//            // -------------------------------------------------------------------------------- when
//            try (final var baos = new ByteArrayOutputStream();
//                 var deflater = new Deflater(level);
//                 var stream = new DeflaterOutputStream(baos, deflater)) {
//                service.write(stream);
//                stream.finish();
//                stream.flush();
//                final var bytes = baos.toByteArray();
//                System.out.printf("%2d (%2d) %s%n", level, bytes.length,
//                                  HexFormat.of().formatHex(bytes));
//                // ---------------------------------------------------------------------------- then
//                try (var inflater = new InflaterInputStream(new ByteArrayInputStream(bytes))) {
//                    assertArrayEquals(hello_world_byte_array(), inflater.readAllBytes());
//                }
//            }
//        }
//
//        /**
//         * Verifies that the {@link HelloWorld} class bytecode round-trips through a
//         * {@link DeflaterOutputStream} across all compression levels and prints the resulting
//         * ratios.
//         */
//        @DisplayName("""
//                should round-trip the <HelloWorld.class> bytecode
//                through <DeflaterOutputStream> across all levels""")
//        @Test
//        void __bytecode() throws IOException {
//            // ------------------------------------------------------------------------------- given
//            final byte[] input;
//            try (var in = HelloWorld.class.getResourceAsStream("HelloWorld.class")) {
//                input = in.readAllBytes();
//            }
//            System.out.printf("input: %d bytes%n", input.length);
//            // -------------------------------------------------------------------------------- when
//            for (int level = 0; level <= 9; level++) {
//                final var baos = new ByteArrayOutputStream();
//                try (var deflater = new Deflater(level);
//                     var stream = new DeflaterOutputStream(baos, deflater)) {
//                    stream.write(input);
//                    stream.flush();
//                }
//                final var compressed = baos.toByteArray();
//                System.out.printf("%2d: %6d -> %5d (%5.2f%%)%n",
//                                  level, input.length, compressed.length,
//                                  100.0 * compressed.length / input.length);
//                // ----------------------------------------------------------------------------- then
//                try (var inflater = new InflaterInputStream(
//                        new ByteArrayInputStream(compressed))) {
//                    assertArrayEquals(input, inflater.readAllBytes());
//                }
//            }
//        }
//    }
//
//    @DisplayName("GZIPOutputStream")
//    @Nested
//    class GZIPOutputStream_Test {
//
//        /**
//         * Verifies that {@code "hello, world"} round-trips through a {@link GZIPOutputStream} and
//         * back through a {@link GZIPInputStream}.
//         */
//        @DisplayName(
//                "should round-trip <hello, world> through <GZIPOutputStream> and <GZIPInputStream>")
//        @Test
//        void __() throws IOException {
//            // ------------------------------------------------------------------------------- given
//            final var service = service();
//            // -------------------------------------------------------------------------------- when
//            try (final var baos = new ByteArrayOutputStream();
//                 var gzipos = new GZIPOutputStream(baos)) {
//                service.write(gzipos);
//                gzipos.finish();
//                gzipos.flush();
//                final var bytes = baos.toByteArray();
//                System.out.printf("(%2d) %s%n", bytes.length,
//                                  HexFormat.of().formatHex(bytes));
//                // ---------------------------------------------------------------------------- then
//                try (var gzipis = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
//                    assertArrayEquals(hello_world_byte_array(), gzipis.readAllBytes());
//                }
//            }
//        }
//    }
}
