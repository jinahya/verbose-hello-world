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
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HexFormat;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A class for testing {@link HelloWorld#write(ZipOutputStream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(stream)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_ZipOutputStream_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#write(ZipOutputStream)} method throws a
     * {@link NullPointerException} when the {@code stream} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <stream> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_StreamIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var stream = (ZipOutputStream) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(stream)
        );
    }

    /**
     * Asserts {@link HelloWorld#write(ZipOutputStream)} method invokes
     * {@link HelloWorld#write(DeflaterOutputStream)} method with {@code stream}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should invoke <write((DeflatorOutputStream) stream)>"""
    )
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .write(ArgumentMatchers.<DeflaterOutputStream>notNull());
        final var stream = Mockito.mock(ZipOutputStream.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(stream);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).write((DeflaterOutputStream) stream);
        Assertions.assertSame(stream, result);
    }

    /**
     * .
     *
     * @throws IOException if an I/O error occurs.
     */
    @畵蛇添足
    @ValueSource(ints = {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9})
    // DEFAULT_COMPRESSION through BEST_COMPRESSION
    @ParameterizedTest
    void _添足_畵蛇(final int level) throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_will_set_actual_hello_world_bytes();
        Mockito.doAnswer(i -> {
            final var stream = i.getArgument(0, OutputStream.class);
            stream.write(service.set(new byte[HelloWorld.BYTES]));
            return stream;
        }).when(service).write(ArgumentMatchers.<OutputStream>any());
        final var bytes = hello_world_byte_array();
        // ------------------------------------------------------------------------------ compress
        final var baos = new ByteArrayOutputStream();
        try (var zos = new ZipOutputStream(baos)) {
            zos.setMethod(ZipEntry.DEFLATED);
            zos.setLevel(level);
            zos.putNextEntry(new ZipEntry("hello.txt"));
            service.write(zos);
            zos.closeEntry();
        }
        final var compressed = baos.toByteArray();
        log.debug("DEFLATED, level: {}, compressed: {} bytes, hex: {}",
                  level, compressed.length, HexFormat.of().formatHex(compressed));
        // ---------------------------------------------------------------------------- decompress
        try (var zis = new ZipInputStream(new ByteArrayInputStream(compressed))) {
            final var entry = zis.getNextEntry();
            Assertions.assertNotNull(entry);
            Assertions.assertEquals("hello.txt", entry.getName());
            final var decompressed = zis.readAllBytes();
            log.debug("decompressed: {}", HexFormat.of().formatHex(decompressed));
            Assertions.assertArrayEquals(bytes, decompressed);
        }
    }

    /**
     * .
     *
     * @throws IOException if an I/O error occurs.
     */
    @畵蛇添足
    @Test
    void _添足_畵蛇() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_will_set_actual_hello_world_bytes();
        Mockito.doAnswer(i -> {
            final var stream = i.getArgument(0, OutputStream.class);
            stream.write(service.set(new byte[HelloWorld.BYTES]));
            return stream;
        }).when(service).write(ArgumentMatchers.<OutputStream>any());
        final var bytes = hello_world_byte_array();
        // ------------------------------------------------------------------------------ compress
        final var baos = new ByteArrayOutputStream();
        final var name = "hello.txt";
        try (var zos = new ZipOutputStream(baos)) {
            zos.setMethod(ZipEntry.STORED);
            final var entry = new ZipEntry(name);
            {
                entry.setSize(bytes.length);
                entry.setCompressedSize(bytes.length);
                final var crc = new CRC32();
                crc.update(bytes);
                entry.setCrc(crc.getValue());
            }
            zos.putNextEntry(entry);
            service.write(zos);
            zos.closeEntry();
        }
        final var compressed = baos.toByteArray();
        log.debug("STORED, compressed: {} bytes, hex: {}",
                  compressed.length, HexFormat.of().formatHex(compressed));
        // ---------------------------------------------------------------------------- decompress
        try (var zis = new ZipInputStream(new ByteArrayInputStream(compressed))) {
            final var entry = zis.getNextEntry();
            Assertions.assertNotNull(entry);
            Assertions.assertEquals(name, entry.getName());
            final var decompressed = zis.readAllBytes();
            log.debug("decompressed: {}", HexFormat.of().formatHex(decompressed));
            Assertions.assertArrayEquals(bytes, decompressed);
        }
    }
}
