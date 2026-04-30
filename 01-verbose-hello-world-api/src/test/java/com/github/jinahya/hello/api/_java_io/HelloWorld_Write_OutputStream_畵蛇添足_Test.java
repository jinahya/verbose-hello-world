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
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@畵蛇添足
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_OutputStream_畵蛇添足_Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        HelloWorldTestUtils.write_outputstream_writes_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class ByteArrayOutputStreamTest {

        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            // -------------------------------------------------------------------------------- when
            try (var baos = new ByteArrayOutputStream(HelloWorld.BYTES)) {
                service.write(baos).flush();
                Assertions.assertEquals(HelloWorld.BYTES, baos.size());
                // ---------------------------------------------------------------------------- then
                try (var bais = new ByteArrayInputStream(baos.toByteArray())) {
                    final var bytes = bais.readNBytes(HelloWorld.BYTES);
                    assert bytes.length == HelloWorld.BYTES;
                    log.debug("read: {}", new String(bytes, StandardCharsets.US_ASCII));
                }
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class FileOutputStreamTest {

        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var file = File.createTempFile("tmp", null, tempDir);
            // -------------------------------------------------------------------------------- when
            try (var stream = new FileOutputStream(file)) {
                service.write(stream).flush();
            }
            Assertions.assertEquals(HelloWorld.BYTES, file.length());
            // -------------------------------------------------------------------------------- then
            try (var stream = new FileInputStream(file)) {
                final var bytes = stream.readNBytes(HelloWorld.BYTES);
                assert bytes.length == HelloWorld.BYTES;
                log.debug("read: {}", new String(bytes, StandardCharsets.US_ASCII));
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class PipeOutputStreamTest {

        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = service();
            final var file = File.createTempFile("tmp", null, tempDir);
            // -------------------------------------------------------------------------------- when
            try (var stream = new FileOutputStream(file)) {
                service.write(stream).flush();
            }
            Assertions.assertEquals(HelloWorld.BYTES, file.length());
            // -------------------------------------------------------------------------------- then
            try (var stream = new FileInputStream(file)) {
                final var bytes = stream.readNBytes(HelloWorld.BYTES);
                assert bytes.length == HelloWorld.BYTES;
                log.debug("read: {}", new String(bytes, StandardCharsets.US_ASCII));
            }
        }
    }

    @畵蛇添足
    @Test
    void __DeflatorOutputStream() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils
                .write_stream_will_write_actual_hello_world_bytes(service());
        // -------------------------------------------------------------------------------- compress
        final var baos = new ByteArrayOutputStream();
        try (var zipos = new GZIPOutputStream(baos)) {
            service.write(zipos);
            zipos.flush();
            zipos.finish(); // maybe redundant; DeflatorOutputStream#close() does this
        }
        final var compressed = baos.toByteArray();
        log.debug("  compressed: {} ({})", HexFormat.of().formatHex(compressed), compressed.length);
        // ------------------------------------------------------------------------------ decompress
        try (var gzipis = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            final var decompressed = gzipis.readAllBytes();
            log.debug("decompressed: {} ({})", HexFormat.of().formatHex(decompressed),
                      decompressed.length);
            Assertions.assertArrayEquals(hello_world_byte_array(), decompressed);
        }
    }

    @畵蛇添足
    @Test
    void __GZIPOutputStream() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils
                .write_stream_will_write_actual_hello_world_bytes(service());
        // -------------------------------------------------------------------------------- compress
        final var baos = new ByteArrayOutputStream();
        try (var zipos = new GZIPOutputStream(baos)) {
            service.write(zipos);
            zipos.flush();
            zipos.finish(); // maybe redundant; DeflatorOutputStream#close() does this
        }
        final var compressed = baos.toByteArray();
        log.debug("  compressed: {} ({})", HexFormat.of().formatHex(compressed), compressed.length);
        // ------------------------------------------------------------------------------ decompress
        try (var gzipis = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            final var decompressed = gzipis.readAllBytes();
            log.debug("decompressed: {} ({})", HexFormat.of().formatHex(decompressed),
                      decompressed.length);
            Assertions.assertArrayEquals(hello_world_byte_array(), decompressed);
        }
    }
}
