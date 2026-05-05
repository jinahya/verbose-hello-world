package com.github.jinahya.hello.api._java_util_zip;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Base64;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_SetInput_Deflater__Test
        extends HelloWorldTest {

    @TempDir
    private static File tempDir;

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
//        System.out.printf("level: %d, length: %d, bytes: %s%n", level, bytes.length,
//                          Base64.getEncoder().encodeToString(bytes));
        System.out.printf("%d (%d) %s%n", level, bytes.length, HexFormat.of().formatHex(bytes));
    }

    private static void printf(final int level, final boolean nowrap, final byte[] bytes) {
//        System.out.printf("level: %d, nowrap: %5b, length: %d, bytes: %s%n", level, nowrap,
//                          bytes.length, Base64.getEncoder().encodeToString(bytes));
        System.out.printf("%d, %5b (%d) %s%n", level, nowrap, bytes.length,
                          HexFormat.of().formatHex(bytes));
    }

    /**
     * Verifies that {@code compressed} is a valid zlib-wrapped DEFLATE stream (RFC 1950) of the
     * {@code "hello, world"} input — checks the 2-byte header (CMF + FLG with the modulo-31
     * constraint) and the 4-byte big-endian Adler-32 trailer.
     */
    private static void assertZlibWrapped(final byte[] compressed) {
        Assertions.assertTrue(compressed.length > 2 + 4); // header(2) + body + adler(4)
        // 2-byte zlib header (RFC 1950 §2.2): CMF + FLG, with (CMF*256 + FLG) % 31 == 0
        final var cmf = compressed[0] & 0xff;
        final var flg = compressed[1] & 0xff;
        Assertions.assertEquals(Deflater.DEFLATED, cmf & 0x0f); // CM = DEFLATE (8)
        Assertions.assertTrue((cmf >>> 4) <= 7);                // CINFO ≤ 7 (window ≤ 32 KiB)
        Assertions.assertEquals(0, (flg & 0x20));               // FDICT not set
        Assertions.assertEquals(0, (cmf * 256 + flg) % 31);     // FCHECK valid
        // 4-byte Adler-32 trailer over the uncompressed input; big-endian (RFC 1950 §2.2)
        final var checksum = new Adler32();
        checksum.update(HelloWorldTestUtils.hello_world_byte_array());
        final var expectedTrailer = ByteBuffer.allocate(Integer.BYTES)
                .order(ByteOrder.BIG_ENDIAN)
                .putInt((int) checksum.getValue())
                .array();
        Assertions.assertArrayEquals(
                expectedTrailer,
                Arrays.copyOfRange(compressed, compressed.length - 4, compressed.length));
    }

    /**
     * Verifies that {@code compressed} is a valid gzip member (RFC 1952) of the
     * {@code "hello, world"} input — checks the fixed 10-byte header and the 8-byte little-endian
     * {@code (CRC32, ISIZE)} trailer.
     */
    private static void assertGzipWrapped(final byte[] compressed) {
        Assertions.assertTrue(compressed.length > 10 + 8); // header(10) + body + trailer(8)
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
        Assertions.assertArrayEquals(expectedHeader, Arrays.copyOfRange(compressed, 0, 10));
        // 8-byte trailer: CRC32 of the uncompressed input + ISIZE (input length mod 2^32);
        // both little-endian uint32 (RFC 1952 §2.1, §2.3.1). For 12-byte input, ISIZE = 12.
        final var checksum = new CRC32();
        checksum.update(HelloWorldTestUtils.hello_world_byte_array());
        final var expectedTrailer = ByteBuffer.allocate(Integer.BYTES * 2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putInt((int) checksum.getValue()) // CRC32
                .putInt(HelloWorld.BYTES)          // ISIZE = 12
                .array();
        Assertions.assertArrayEquals(
                expectedTrailer,
                Arrays.copyOfRange(compressed, compressed.length - 8, compressed.length));
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() throws IOException {
        Mockito.doAnswer(i -> {
            final var deflator = i.getArgument(0, Deflater.class);
            deflator.setInput(HelloWorldTestUtils.hello_world_byte_array());
            return deflator;
        }).when(service()).setInput(ArgumentMatchers.<Deflater>notNull());
        HelloWorldTestUtils.write_stream_will_write_actual_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @MethodSource({"levelStream"})
    @ParameterizedTest
    void __Deflator(final int level) throws IOException, DataFormatException {
        final var nowrap = true;
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var deflater = new Deflater(level, nowrap)) {
            service().setInput(deflater);
            assert !deflater.needsInput(); // input buffer is populated
            deflater.finish();
            for (final var b = new byte[1]; !deflater.finished(); ) {
                baos.write(b, 0, deflater.deflate(b));
            }
            assert deflater.needsInput(); // true; the input buffer is empty
            deflater.end(); // redundant, invoked in close()
            baos.flush(); // no-op
            compressed = baos.toByteArray();
        }
        printf(level, compressed);
//        assertZlibWrapped(compressed); // Deflater(level) defaults to nowrap=false → zlib wrapper
        try (var inflater = new Inflater(nowrap)) {
            inflater.setInput(compressed);
            assert !inflater.needsInput(); // input buffer is populated
            final var uncompressed = new byte[HelloWorld.BYTES];
            final var length = inflater.inflate(uncompressed);
            assert inflater.needsInput(); // true; the input buffer is empty
            assert length == HelloWorld.BYTES;
            assert inflater.inflate(new byte[1]) == 0;
            assert inflater.finished(); // all compressed input has been decompressed
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
            inflater.end();  // redundant, invoked in close()
        }
        try (var baos = new ByteArrayOutputStream(HelloWorld.BYTES);
             var inflater = new Inflater(nowrap)) {
            inflater.setInput(compressed);
            assert !inflater.needsInput(); // input buffer is populated
            for (final var b = new byte[1]; !inflater.finished(); ) {
                baos.write(b, 0, inflater.inflate(b));
            }
            assert inflater.needsInput(); // true; the input buffer is empty
            inflater.end(); // redundant, invoked in close()
            baos.flush(); // no-op
            final var uncompressed = baos.toByteArray();
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
        }
    }

    @MethodSource({"levelAndNowrapStream"})
    @ParameterizedTest
    void __Deflator(final int level, final boolean nowrap) throws IOException, DataFormatException {
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var deflater = new Deflater(level, nowrap)) {
            service().setInput(deflater);
            assert !deflater.needsInput(); // input buffer is populated
            deflater.finish();
            for (final var output = new byte[1]; !deflater.finished(); ) {
                baos.write(output, 0, deflater.deflate(output));
            }
            assert deflater.needsInput(); // true; the input buffer is empty
            deflater.end(); // redundant, invoked in close()
            baos.flush(); // no-op
            compressed = baos.toByteArray();
        }
        printf(level, nowrap, compressed);
        if (!nowrap) {
            assertZlibWrapped(compressed); // wrapper present only when nowrap=false
        }
        try (var inflater = new Inflater(nowrap)) {
            inflater.setInput(compressed);
            assert !inflater.needsInput(); // input buffer is populated
            final var uncompressed = new byte[HelloWorld.BYTES];
            final var length = inflater.inflate(uncompressed);
            assert inflater.needsInput(); // true; the input buffer is empty
            assert length == HelloWorld.BYTES;
            assert inflater.inflate(new byte[1]) == 0;
            assert inflater.finished(); // all compressed input has been decompressed
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
            inflater.end();  // redundant, invoked in close()
        }
        try (var baos = new ByteArrayOutputStream(HelloWorld.BYTES);
             var inflater = new Inflater(nowrap)) {
            inflater.setInput(compressed);
            assert !inflater.needsInput(); // input buffer is populated
            for (final var b = new byte[1]; !inflater.finished(); ) {
                baos.write(b, 0, inflater.inflate(b));
            }
            assert inflater.needsInput(); // true; the input buffer is empty
            inflater.end(); // redundant, invoked in close()
            baos.flush(); // no-op
            final var uncompressed = baos.toByteArray();
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
        }
    }

    @MethodSource({"levelStream"})
    @ParameterizedTest
    void __DeflatorOutputStream(final int level) throws IOException {
        final var nowrap = true;
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var dos = new DeflaterOutputStream(baos, new Deflater(level, nowrap))) {
            service().write(dos);
            dos.finish(); // redundant, invoked in close()
            baos.flush(); // no-op
            compressed = baos.toByteArray();
        }
        printf(level, compressed);
        try (var bais = new ByteArrayInputStream(compressed);
             var iis = new InflaterInputStream(bais, new Inflater(nowrap))) {
            final var uncompressed = iis.readAllBytes();
            assert uncompressed.length == HelloWorld.BYTES;
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
        }
    }

    @MethodSource({"levelAndNowrapStream"})
    @ParameterizedTest
    void __DeflatorOutputStream(final int level, final boolean nowrap) throws IOException {
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var dos = new DeflaterOutputStream(baos, new Deflater(level, nowrap))) {
            service().write(dos);
            dos.finish(); // redundant, invoked in close()
            baos.flush(); // no-op
            compressed = baos.toByteArray();
        }
        printf(level, nowrap, compressed);
        if (!nowrap) {
            assertZlibWrapped(compressed); // wrapper present only when nowrap=false
        }
        try (var bais = new ByteArrayInputStream(compressed);
             var iis = new InflaterInputStream(bais, new Inflater(nowrap))) {
            final var uncompressed = iis.readAllBytes();
            assert uncompressed.length == HelloWorld.BYTES;
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
        }
    }

    @Test
    void __GZIPOutputStream() throws IOException {
        // -------------------------------------------------------------------------------- compress
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var gzipos = new GZIPOutputStream(baos)) { // header (10 bytes) is written here
            service().write(gzipos); // bytes are buffered by the deflater
            gzipos.finish(); // flushes remaining deflate output, then writes the trailer
            // (CRC-32 + ISIZE, 8 bytes); redundant, invoked in close()
            gzipos.flush(); // no-op: deflater is finished (SYNC_FLUSH branch skipped) and
            // baos doesn't buffer; idempotent and harmless
            compressed = baos.toByteArray();
        }
        System.out.printf("(%2d) %s%n", compressed.length,
                          Base64.getEncoder().encodeToString(compressed));
        assertGzipWrapped(compressed);
        // ------------------------------------------------------------------------------ uncompress
        try (var bais = new ByteArrayInputStream(compressed);
             var gzipis = new GZIPInputStream(bais)) {
            final var uncompressed = gzipis.readAllBytes();
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
        }
    }

    @Test
    void __syncFlush_GZIPOutputStream() throws IOException {
        // -------------------------------------------------------------------------------- compress
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream()) {
            final var syncFlush = ThreadLocalRandom.current().nextBoolean();
            try (var gzipos = new GZIPOutputStream(baos, syncFlush)) {
                service().write(gzipos); // 12 bytes given to the deflater via setInput();
                // no compressed output produced yet — baos has only
                // the 10-byte gzip header
                assert baos.size() == 10; // header only, regardless of syncFlush
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
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
        }
    }
}
