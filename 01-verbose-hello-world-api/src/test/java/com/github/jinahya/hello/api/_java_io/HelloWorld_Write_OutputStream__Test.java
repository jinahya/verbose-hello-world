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

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({"java:S101"})
class HelloWorld_Write_OutputStream__Test extends HelloWorld__Test {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        write_outputstream_writes_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @Nested
    class ByteArrayOutputStream_Test {

        @Test
        void __() throws IOException {
            try (var baos = new ByteArrayOutputStream(HelloWorld.BYTES)) {
                service().write(baos).flush();
                assertEquals(HelloWorld.BYTES, baos.size());
                try (var bais = new ByteArrayInputStream(baos.toByteArray())) {
                    final var bytes = bais.readAllBytes();
                    final var string = new String(bytes, StandardCharsets.US_ASCII);
                    assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING, string);
                }
            }
        }
    }

    @Nested
    class FilterOutputStream_Test {

        static class FunnelOutputStream extends FilterOutputStream { // @formatter:off

            public FunnelOutputStream(final OutputStream out) { super(out); }

            @Override public void write(final int b) throws IOException { super.write(b); }

            @Override public final void write(final byte[] b) throws IOException { super.write(b); }

            @Override
            public final void write(final byte[] b, final int off, final int len)
                    throws IOException { super.write(b, off, len); }
        } // @formatter:on

        static class FunnelInputStream extends FilterInputStream { // @formatter:off

            protected FunnelInputStream(final InputStream in) { super(in); }

            @Override public int read() throws IOException { return super.read(); }

            @Override
            public final int read(final byte[] b) throws IOException { return super.read(b); }

            @Override
            public final int read(final byte[] b, int off, final int len) throws IOException {
                Objects.checkFromIndexSize(off, len, b.length);
                if (len == 0) { return 0; }
                var r = read();
                if (r == -1) { return -1; }
                b[off++] = (byte) r;
                var i = 1;
                for (; i < len; i++) {
                    r = read();
                    if (r == -1) { break; }
                    b[off++] = (byte) r;
                }
                return i;
            }

            @Override
            public final long skip(final long n) throws IOException {
                long skipped = 0L;
                while (skipped < n && read() != -1) { skipped++; }
                return skipped;
            }
        }
    } // @formatter:on

    @Nested
    class DataOutputStream_Test {

        @Test
        void __() throws IOException {
            try (var baos = new ByteArrayOutputStream();
                 var dos = new DataOutputStream(baos)) {
                service().write((OutputStream) dos).flush();
                try (var bais = new ByteArrayInputStream(baos.toByteArray());
                     var dis = new DataInputStream(bais)) {
                    final var bytes = dis.readAllBytes();
                    final var string = new String(bytes, StandardCharsets.US_ASCII);
                    assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING, string);
                }
            }
        }
    }

    @Nested
    class FileOutputStreamTest {

        @Test
        void __() throws IOException {
            final var file = File.createTempFile("tmp", null, tempDir);
            try (var stream = new FileOutputStream(file)) {
                service().write(stream).flush();
            }
            assertEquals(HelloWorld.BYTES, file.length());
            try (var stream = new FileInputStream(file)) {
                final var bytes = stream.readNBytes(HelloWorld.BYTES);
                final var string = new String(bytes, StandardCharsets.US_ASCII);
                assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING, string);
            }
        }
    }

    @Nested
    class PipeOutputStream_Test {

        @Test
        void __EnoughPipeSize() throws IOException {
            // ------------------------------------------------------------------------------- given
            try (var pos = new PipedOutputStream();
                 var pis = new PipedInputStream(HelloWorld.BYTES)) {
                pos.connect(pis);
                // ---------------------------------------------------------------------------- when
                service().write(pos).flush();
                // ---------------------------------------------------------------------------- when
                final var bytes = pis.readNBytes(HelloWorld.BYTES);
                final var string = new String(bytes, StandardCharsets.US_ASCII);
                assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING, string);
            }
        }

        @Test
        void __NotEnoughPipeSize() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var pipeSize = ThreadLocalRandom.current().nextInt(1, HelloWorld.BYTES);
            try (var pos = new PipedOutputStream();
                 var pis = new PipedInputStream(pipeSize)) {
                pos.connect(pis);
                // ---------------------------------------------------------------------------- when
                Thread.ofPlatform().start(() -> {
                    try {
                        service().write(pos).flush();
                    } catch (final IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                });
                // ---------------------------------------------------------------------------- when
                final var bytes = pis.readNBytes(HelloWorld.BYTES);
                final var string = new String(bytes, StandardCharsets.US_ASCII);
                assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING, string);
            }
        }
    }

    // ------------------------------------------------------------------------------- java.util.zip
    @Nested
    class JavaUtilZipTest {

        @Test
        void __DeflaterOutputStream() throws IOException {
            try (var baos = new ByteArrayOutputStream();
                 var dos = new DeflaterOutputStream(baos)) {
                service().write(dos).flush();
                dos.finish();
                try (var bais = new ByteArrayInputStream(baos.toByteArray());
                     var iis = new InflaterInputStream(bais)) {
                    final var bytes = iis.readAllBytes();
                    final var string = new String(bytes, StandardCharsets.US_ASCII);
                    assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING, string);
                }
            }
        }

        @Test
        void __GZIPOutputStream() throws IOException {
            // ------------------------------------------------------------------------------- given
            // ---------------------------------------------------------------------------- compress
            final var baos = new ByteArrayOutputStream();
            try (var zipos = new GZIPOutputStream(baos)) {
                service().write(zipos);
                zipos.flush();
                zipos.finish(); // maybe redundant; DeflatorOutputStream#close() does this
            }
            final var compressed = baos.toByteArray();
            log.debug("  compressed: {} ({})", HexFormat.of().formatHex(compressed),
                      compressed.length);
            // -------------------------------------------------------------------------- decompress
            try (var gzipis = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
                final var decompressed = gzipis.readAllBytes();
                log.debug("decompressed: {} ({})", HexFormat.of().formatHex(decompressed),
                          decompressed.length);
                Assertions.assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(),
                                             decompressed);
            }
        }

        @Test
        void __ZipOutputStream() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var file = File.createTempFile("tmp", "", tempDir);
            // ---------------------------------------------------------------------------- compress
            try (var fos = new FileOutputStream(file);
                 var zos = new ZipOutputStream(fos)) {
                final var entry = new ZipEntry("hello.txt");
                zos.putNextEntry(entry);
                service().write(zos);
                zos.closeEntry();
                zos.finish();
                zos.flush();
            }
        }
    }
}
