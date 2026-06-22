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
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.stream.*;
import java.util.zip.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring {@link HelloWorld#setInput(Deflater) setInput(deflater)} method with real
 * {@link Deflater} / {@link DeflaterOutputStream} / {@link GZIPOutputStream} round-trips at every
 * compression level.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_HideFromPublishing
@DisplayName("setInput(deflater)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetInput_Deflater__Test extends HelloWorld__Test {

    @TempDir
    private static File tempDir;

    // ---------------------------------------------------------------------------------------------
    static IntStream levelStream() {
        return IntStream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    }

    static Stream<Arguments> levelAndNowrapStream() {
        return levelStream().boxed()
                .flatMap(l -> Stream.of(false, true).map(nr -> Arguments.of(l, nr)));
    }

    static Stream<Arguments> levelNowrapAndSyncFlushStream() {
        return levelStream().boxed()
                .flatMap(l -> Stream.of(false, true)
                        .flatMap(nr -> Stream.of(false, true)
                                .map(sf -> Arguments.of(l, nr, sf))));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code compressed} is a valid zlib-wrapped DEFLATE stream (RFC 1950) of the
     * {@code "hello, world"} input — checks the 2-byte header (CMF + FLG with the modulo-31
     * constraint) and the 4-byte big-endian Adler-32 trailer.
     */
    private static void assertZlibWrapped(final byte[] compressed) {
        assertTrue(compressed.length > 2 + 4); // header(2) + body + adler(4)
        // 2-byte zlib header (RFC 1950 §2.2): CMF + FLG, with (CMF*256 + FLG) % 31 == 0
        final var cmf = compressed[0] & 0xff;
        final var flg = compressed[1] & 0xff;
        assertEquals(Deflater.DEFLATED, cmf & 0x0f); // CM = DEFLATE (8)
        assertTrue((cmf >>> 4) <= 7);                // CINFO ≤ 7 (window ≤ 32 KiB)
        assertEquals(0, (flg & 0x20));               // FDICT not set
        assertEquals(0, (cmf * 256 + flg) % 31);     // FCHECK valid
        // 4-byte Adler-32 trailer over the uncompressed input; big-endian (RFC 1950 §2.2)
        final var checksum = new Adler32();
        checksum.update(hello_world_byte_array());
        final var expectedTrailer = ByteBuffer.allocate(Integer.BYTES)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt((int) checksum.getValue())
                .array();
        assertArrayEquals(
                expectedTrailer,
                Arrays.copyOfRange(compressed, compressed.length - 4, compressed.length));
    }

    /**
     * Verifies that {@code compressed} is a valid gzip member (RFC 1952) of the
     * {@code "hello, world"} input — checks the fixed 10-byte header and the 8-byte little-endian
     * {@code (CRC32, ISIZE)} trailer.
     */
    private static void assertGzipWrapped(final byte[] compressed) {
        assertTrue(compressed.length > 10 + 8); // header(10) + body + trailer(8)
        // 10-byte gzip header (RFC 1952 §2.3.1) — fixed values produced by GZIPOutputStream
        final var expectedHeader = new byte[] {
                (byte) 0x1f,       // [0] ID1: gzip magic byte 1
                (byte) 0x8b,       // [1] ID2: gzip magic byte 2
                Deflater.DEFLATED, // [2] CM:  compression method = DEFLATE (0x08)
                0,                 // [3] FLG: no optional fields
                0,                 // [4] MTIME[0] (little-endian uint32)
                0,                 // [5] MTIME[1]
                0,                 // [6] MTIME[2]
                0,                 // [7] MTIME[3] — MTIME = 0 ("no time")
                0,                 // [8] XFL: extra flags
                (byte) 0xff,       // [9] OS:  0xff = "unknown" (per RFC 1952)
        };
        assertArrayEquals(expectedHeader, Arrays.copyOfRange(compressed, 0, 10));
        // 8-byte trailer: CRC32 of the uncompressed input + ISIZE (input length mod 2^32);
        // both little-endian uint32 (RFC 1952 §2.1, §2.3.1). For 12-byte input, ISIZE = 12.
        final var checksum = new CRC32();
        checksum.update(hello_world_byte_array());
        final var expectedTrailer = ByteBuffer.allocate(Integer.BYTES * 2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt((int) checksum.getValue()) // CRC32
                .putInt(HelloWorld.BYTES)          // ISIZE = 12
                .array();
        assertArrayEquals(
                expectedTrailer,
                Arrays.copyOfRange(compressed, compressed.length - 8, compressed.length));
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __stubService() throws IOException {
        doAnswer(i -> {
            final var deflator = i.getArgument(0, Deflater.class);
            deflator.setInput(hello_world_byte_array());
            return deflator;
        }).when(service()).setInput(any());
        write_stream_writes_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("Deflater")
    @Nested
    class Deflater_Test {

        void __(final boolean nowrap) throws DataFormatException {
            System.out.printf("level nowrap size compressed%n");
            final var output = new byte[64];
            for (final int level : levelStream().toArray()) {
                final byte[] compressed;
                final int length;
                try (var deflater = new Deflater(level, nowrap)) {
                    service().setInput(deflater);
                    assertFalse(deflater.finished());
                    deflater.finish(); // not redundant; we should take the result before closing
                    length = deflater.deflate(output);
                    assertTrue(deflater.finished());
                }
                compressed = Arrays.copyOf(output, length);
                System.out.printf("%5d %6b %4d %s%n", level, nowrap, compressed.length,
                                  HexFormat.of().formatHex(compressed));
                if (!nowrap) {
                    assertZlibWrapped(compressed);
                }
                try (var inflater = new Inflater(nowrap)) {
                    inflater.setInput(compressed);
                    final var uncompressed = new byte[HelloWorld.BYTES];
                    assertEquals(HelloWorld.BYTES, inflater.inflate(uncompressed));
                    assertTrue(inflater.finished());
                    assertArrayEquals(hello_world_byte_array(), uncompressed);
                }
            }
        }

        /**
         * Verifies that {@code "hello, world"} round-trips through a raw {@link Deflater} in
         * <em>nowrap</em> mode (raw DEFLATE, no zlib wrapper) across all compression levels.
         */
        @DisplayName("nowrap")
        @Test
        void __Deflator_Nowrap() throws DataFormatException {
            __(true);
        }

        /**
         * Verifies that {@code "hello, world"} round-trips through a raw {@link Deflater} in
         * <em>wrap</em> mode (standard zlib wrapper) across all compression levels, asserting the
         * zlib wrapper structure on each compressed payload.
         */
        @DisplayName("wrap")
        @Test
        void __Deflator_Wrap() throws DataFormatException {
            __(false);
        }
    }

    @DisplayName("DeflaterOutputStream")
    @Nested
    class DeflaterOutputStream_Test {

        void __(final boolean nowrap, final boolean syncFlush) throws IOException {
            System.out.printf("level syncFlush nowrap size compressed%n");
            final var baos = new ByteArrayOutputStream();
            for (final int level : levelStream().toArray()) {
                final byte[] compressed;
                baos.reset();
                try (var deflater = new Deflater(level, nowrap);
                     var dos = new DeflaterOutputStream(baos, deflater, syncFlush)) {
                    service().write(dos);
                    dos.flush();
                    dos.finish(); // redundant; close() will do finish()
                }
                compressed = baos.toByteArray();
                System.out.printf("%5d %9b %6b %4d %s%n", level, syncFlush, nowrap,
                                  compressed.length, HexFormat.of().formatHex(compressed));
                if (!nowrap) {
                    assertZlibWrapped(compressed);
                }
                try (var bais = new ByteArrayInputStream(compressed);
                     var inflater = new Inflater(nowrap);
                     var iis = new InflaterInputStream(bais, inflater)) {
                    final var uncompressed = iis.readAllBytes();
                    assert uncompressed.length == HelloWorld.BYTES;
                    assertArrayEquals(hello_world_byte_array(), uncompressed);
                }
            }
        }

        /**
         * Verifies that {@code "hello, world"} round-trips through a {@link DeflaterOutputStream}
         * with {@code syncFlush} disabled and a zlib-wrapped {@link Deflater}
         * ({@code nowrap=false}), across all compression levels.
         */
        @DisplayName("no-syncFlush | wrap")
        @Test
        void __NoSyncFlush_Wrap() throws IOException {
            __(false, false);
        }

        /**
         * Verifies that {@code "hello, world"} round-trips through a {@link DeflaterOutputStream}
         * with {@code syncFlush} disabled and a raw {@link Deflater} ({@code nowrap=true}), across
         * all compression levels.
         */
        @DisplayName("no-syncFlush | nowrap")
        @Test
        void __NoSyncFlush_Nowrap() throws IOException {
            __(true, false);
        }

        /**
         * Verifies that {@code "hello, world"} round-trips through a {@link DeflaterOutputStream}
         * with {@code syncFlush} enabled and a zlib-wrapped {@link Deflater}
         * ({@code nowrap=false}), across all compression levels.
         */
        @DisplayName("syncFlush | wrap")
        @Test
        void __SyncFlush_Wrap() throws IOException {
            __(false, true);
        }

        /**
         * Verifies that {@code "hello, world"} round-trips through a {@link DeflaterOutputStream}
         * with {@code syncFlush} enabled and a raw {@link Deflater} ({@code nowrap=true}), across
         * all compression levels.
         */
        @DisplayName("syncFlush | nowrap")
        @Test
        void __SyncFlush_Nowrap() throws IOException {
            __(true, true);
        }
    }

    @DisplayName("GZIPOutputStream")
    @Nested
    class GZIPOutputStream_Test {

        void __(final boolean syncFlush) throws IOException {
            System.out.printf("syncFlush size compressed%n");
            final var baos = new ByteArrayOutputStream();
            try (var gzipos = new GZIPOutputStream(baos, syncFlush)) {
                service().write(gzipos);
                gzipos.flush();
                gzipos.finish(); // redundant; close() will do finish()
            }
            final var compressed = baos.toByteArray();
            System.out.printf("%9b %4d %s%n", syncFlush,
                              compressed.length, HexFormat.of().formatHex(compressed));
            assertGzipWrapped(compressed);
            try (var bais = new ByteArrayInputStream(compressed);
                 var gzipis = new GZIPInputStream(bais)) {
                final var uncompressed = gzipis.readAllBytes();
                assertArrayEquals(hello_world_byte_array(), uncompressed);
            }
        }

        /**
         * Verifies that {@code "hello, world"} round-trips through a {@link GZIPOutputStream} with
         * {@code syncFlush} disabled.
         */
        @DisplayName("no-syncFlush")
        @Test
        void __NoSyncFlush() throws IOException {
            __(false);
        }

        /**
         * Verifies that {@code "hello, world"} round-trips through a {@link GZIPOutputStream} with
         * {@code syncFlush} enabled.
         */
        @DisplayName("syncFlush")
        @Test
        void __SyncFlush() throws IOException {
            __(true);
        }
    }
}
