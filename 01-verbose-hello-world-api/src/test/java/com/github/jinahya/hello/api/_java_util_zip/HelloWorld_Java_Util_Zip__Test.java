package com.github.jinahya.hello.api._java_util_zip;

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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Java_Util_Zip__Test
        extends HelloWorldTest {

    @BeforeEach
    void __() throws IOException {
        HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        HelloWorldTestUtils.write_stream_will_write_actual_hello_world_bytes(service());
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
