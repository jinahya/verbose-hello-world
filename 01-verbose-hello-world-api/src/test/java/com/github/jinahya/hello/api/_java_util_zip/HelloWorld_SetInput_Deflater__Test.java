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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;
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

    private static void printf(final int level, final int length) {
        System.out.printf("level: %d, length: %d%n", level, length);
    }

    private static void printf(final int level, final boolean nowrap, final int length) {
        System.out.printf("level: %d, nowrap: %5b, length: %d%n", level, nowrap, length);
    }

    private static void printf(final int level, final byte[] bytes) {
        System.out.printf("%d (%d) %s%n", level, bytes.length, HexFormat.of().formatHex(bytes));
    }

    private static void printf(final int level, final boolean nowrap, final byte[] bytes) {
        System.out.printf("%d, %5b (%d) %s%n", level, nowrap, bytes.length,
                          HexFormat.of().formatHex(bytes));
    }

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

    /**
     * Verifies that {@code "hello, world"} round-trips through a raw {@link Deflater} at the given
     * compression {@code level} and back through an {@link Inflater}.
     *
     * @param level a deflater compression level.
     */
    @DisplayName("should round-trip <hello, world> through a raw <Deflater> at the given <level>")
    @MethodSource({"levelStream"})
    @ParameterizedTest
    void __Deflator(final int level) throws IOException, DataFormatException {
        final var nowrap = true;
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var deflater = new Deflater(level, nowrap)) {
            service().setInput(deflater);
            assert !deflater.needsInput();
            deflater.finish();
            for (final var b = new byte[1]; !deflater.finished(); ) {
                baos.write(b, 0, deflater.deflate(b));
            }
            assert deflater.needsInput();
            deflater.end();
            baos.flush();
            compressed = baos.toByteArray();
        }
        printf(level, compressed);
        try (var baos = new ByteArrayOutputStream(HelloWorld.BYTES);
             var inflater = new Inflater(nowrap)) {
            inflater.setInput(compressed);
            assert !inflater.needsInput();
            for (final var b = new byte[1]; !inflater.finished(); ) {
                baos.write(b, 0, inflater.inflate(b));
            }
            assert inflater.needsInput();
            inflater.end();
            baos.flush();
            final var uncompressed = baos.toByteArray();
            assertArrayEquals(hello_world_byte_array(), uncompressed);
        }
    }

    /**
     * Verifies that {@code "hello, world"} round-trips through a raw {@link Deflater} at the given
     * compression {@code level} and {@code nowrap} flag combination, asserting the zlib wrapper
     * when {@code nowrap} is {@code false}.
     *
     * @param level  a deflater compression level.
     * @param nowrap the {@code nowrap} flag passed to {@link Deflater#Deflater(int, boolean)}.
     */
    @DisplayName("""
            should round-trip <hello, world> through a raw <Deflater>
            at the given <level> and <nowrap> flag""")
    @MethodSource({"levelAndNowrapStream"})
    @ParameterizedTest
    void __Deflator(final int level, final boolean nowrap) throws IOException, DataFormatException {
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var deflater = new Deflater(level, nowrap)) {
            service().setInput(deflater);
            assert !deflater.needsInput();
            deflater.finish();
            for (final var output = new byte[1]; !deflater.finished(); ) {
                baos.write(output, 0, deflater.deflate(output));
            }
            assert deflater.needsInput();
            deflater.end();
            baos.flush();
            compressed = baos.toByteArray();
        }
        printf(level, nowrap, compressed);
        if (!nowrap) {
            assertZlibWrapped(compressed);
        }
        try (var inflater = new Inflater(nowrap)) {
            inflater.setInput(compressed);
            assert !inflater.needsInput();
            final var uncompressed = new byte[HelloWorld.BYTES];
            final var length = inflater.inflate(uncompressed);
            assert inflater.needsInput();
            assert length == HelloWorld.BYTES;
            assert inflater.inflate(new byte[1]) == 0;
            assert inflater.finished();
            assertArrayEquals(hello_world_byte_array(), uncompressed);
            inflater.end();
        }
        try (var baos = new ByteArrayOutputStream(HelloWorld.BYTES);
             var inflater = new Inflater(nowrap)) {
            inflater.setInput(compressed);
            assert !inflater.needsInput();
            for (final var b = new byte[1]; !inflater.finished(); ) {
                baos.write(b, 0, inflater.inflate(b));
            }
            assert inflater.needsInput();
            inflater.end();
            baos.flush();
            final var uncompressed = baos.toByteArray();
            assertArrayEquals(hello_world_byte_array(), uncompressed);
        }
    }

    /**
     * Verifies that {@code "hello, world"} round-trips through a {@link DeflaterOutputStream} at
     * the given compression {@code level} and back through an {@link InflaterInputStream}.
     *
     * @param level a deflater compression level.
     */
    @DisplayName(
            "should round-trip <hello, world> through <DeflaterOutputStream> at the given <level>")
    @MethodSource({"levelStream"})
    @ParameterizedTest
    void __DeflatorOutputStream(final int level) throws IOException {
        final var nowrap = true;
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var dos = new DeflaterOutputStream(baos, new Deflater(level, nowrap))) {
            service().write(dos);
            dos.finish();
            baos.flush();
            compressed = baos.toByteArray();
        }
        printf(level, compressed);
        try (var bais = new ByteArrayInputStream(compressed);
             var iis = new InflaterInputStream(bais, new Inflater(nowrap))) {
            final var uncompressed = iis.readAllBytes();
            assert uncompressed.length == HelloWorld.BYTES;
            assertArrayEquals(hello_world_byte_array(), uncompressed);
        }
    }

    /**
     * Verifies that {@code "hello, world"} round-trips through a {@link DeflaterOutputStream} at
     * the given compression {@code level} and {@code nowrap} flag combination, asserting the zlib
     * wrapper when {@code nowrap} is {@code false}.
     *
     * @param level  a deflater compression level.
     * @param nowrap the {@code nowrap} flag passed to {@link Deflater#Deflater(int, boolean)}.
     */
    @DisplayName("""
            should round-trip <hello, world> through <DeflaterOutputStream>
            at the given <level> and <nowrap> flag""")
    @MethodSource({"levelAndNowrapStream"})
    @ParameterizedTest
    void __DeflatorOutputStream(final int level, final boolean nowrap) throws IOException {
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var dos = new DeflaterOutputStream(baos, new Deflater(level, nowrap))) {
            service().write(dos);
            dos.finish();
            baos.flush();
            compressed = baos.toByteArray();
        }
        printf(level, nowrap, compressed);
        if (!nowrap) {
            assertZlibWrapped(compressed);
        }
        try (var bais = new ByteArrayInputStream(compressed);
             var iis = new InflaterInputStream(bais, new Inflater(nowrap))) {
            final var uncompressed = iis.readAllBytes();
            assert uncompressed.length == HelloWorld.BYTES;
            assertArrayEquals(hello_world_byte_array(), uncompressed);
        }
    }

    /**
     * Verifies that {@code "hello, world"} round-trips through a {@link GZIPOutputStream} and that
     * the resulting bytes carry a valid gzip wrapper (RFC 1952).
     */
    @DisplayName("""
            should round-trip <hello, world> through <GZIPOutputStream>
            with a valid <gzip> wrapper""")
    @Test
    void __GZIPOutputStream() throws IOException {
        // -------------------------------------------------------------------------------- compress
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var gzipos = new GZIPOutputStream(baos)) {
            service().write(gzipos);
            gzipos.finish();
            gzipos.flush();
            compressed = baos.toByteArray();
        }
        System.out.printf("(%2d) %s%n", compressed.length,
                          Base64.getEncoder().encodeToString(compressed));
        assertGzipWrapped(compressed);
        // ------------------------------------------------------------------------------ uncompress
        try (var bais = new ByteArrayInputStream(compressed);
             var gzipis = new GZIPInputStream(bais)) {
            final var uncompressed = gzipis.readAllBytes();
            assertArrayEquals(hello_world_byte_array(), uncompressed);
        }
    }

    /**
     * Verifies that {@code "hello, world"} round-trips through a {@link GZIPOutputStream} with the
     * {@code syncFlush} branch toggled, exercising both flush paths.
     */
    @DisplayName("""
            should round-trip <hello, world> through <GZIPOutputStream>
            with the <syncFlush> branch toggled""")
    @Test
    void __syncFlush_GZIPOutputStream() throws IOException {
        // -------------------------------------------------------------------------------- compress
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream()) {
            final var syncFlush = ThreadLocalRandom.current().nextBoolean();
            try (var gzipos = new GZIPOutputStream(baos, syncFlush)) {
                service().write(gzipos);
                assert baos.size() == 10;
                if (syncFlush) {
                    // The trailing gzipos.flush() below will emit a SYNC_FLUSH block:
                    // pending compressed bytes land in baos BEFORE close() runs.
                    // Afterwards, close() invokes finish() to write the trailer.
                } else {
                    // The trailing gzipos.flush() below skips the SYNC_FLUSH branch
                    // (since syncFlush=false) and only flushes baos (a no-op for
                    // ByteArrayOutputStream). Pending compressed bytes stay buffered in
                    // the deflater until close() invokes finish(), which then emits both
                    // the deflate output and the trailer.
                }
                gzipos.flush(); // trailing flush; observable effect depends on syncFlush
                // (see branches above)
                if (syncFlush) {
                    // SYNC_FLUSH block was emitted; baos now holds the gzip header
                    // plus the partial deflate block.
                    assert baos.size() > 10;
                } else {
                    // SYNC_FLUSH branch was skipped; the deflater still holds the
                    // 12-byte input. baos still has just the 10-byte header.
                    assert baos.size() == 10;
                }
            }
            compressed = baos.toByteArray();
            assertGzipWrapped(compressed);
        }
        System.out.printf("(%2d) %s%n", compressed.length,
                          Base64.getEncoder().encodeToString(compressed));
        // ------------------------------------------------------------------------------ uncompress
        try (var bais = new ByteArrayInputStream(compressed);
             var gzipis = new GZIPInputStream(bais)) {
            final var uncompressed = gzipis.readAllBytes();
            assertArrayEquals(hello_world_byte_array(), uncompressed);
        }
    }
}
