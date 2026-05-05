package com.github.jinahya.hello.api._java_util_zip;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Base64;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
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

    static Stream<Arguments> levelAndNoWrapStream() {
        return levelStream().boxed()
                .flatMap(l -> Stream.of(false, true).map(nr -> Arguments.of(l, nr)));
    }

    private static void printf(final int level, final int length) {
        System.out.printf("level: %d, length: %d%n", level, length);
    }

    private static void printf(final int level, final boolean noWrap, final int length) {
        System.out.printf("level: %d, noWrap: %5b, length: %d%n", level, noWrap, length);
    }

    private static void printf(final int level, final byte[] bytes) {
        System.out.printf("level: %d, length: %d, bytes: %s%n", level, bytes.length,
                          Base64.getEncoder().encodeToString(bytes));
    }

    private static void printf(final int level, final boolean noWrap, final byte[] bytes) {
        System.out.printf("level: %d, noWrap: %5b, length: %d, bytes: %s%n", level, noWrap,
                          bytes.length, Base64.getEncoder().encodeToString(bytes));
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
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var deflater = new Deflater(level)) {
            service().setInput(deflater);
            deflater.finish();
            final var output = new byte[128];
            while (!deflater.finished()) {
                final var len = deflater.deflate(output);
                baos.write(output, 0, len);
            }
            deflater.end(); // redundant, invoked in close()
            baos.flush(); // no-op
            compressed = baos.toByteArray();
        }
        printf(level, compressed);
        try (var inflater = new Inflater()) {
            inflater.setInput(compressed);
            final var uncompressed = new byte[HelloWorld.BYTES];
            final var length = inflater.inflate(uncompressed);
            assert length == HelloWorld.BYTES;
            assert inflater.inflate(new byte[1]) == 0;
            assert inflater.finished();
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
            inflater.end();  // redundant, invoked in close()
        }
        try (var baos = new ByteArrayOutputStream(HelloWorld.BYTES);
             var inflater = new Inflater()) {
            inflater.setInput(compressed);
            final var output = new byte[128];
            while (!inflater.finished()) {
                final var len = inflater.inflate(output);
                baos.write(output, 0, len);
            }
            inflater.end(); // redundant, invoked in close()
            baos.flush(); // no-op
            final var uncompressed = baos.toByteArray();
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
        }
    }

    @MethodSource({"levelAndNoWrapStream"})
    @ParameterizedTest
    void __Deflator(final int level, final boolean noWrap) throws IOException, DataFormatException{
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var deflater = new Deflater(level, noWrap)) {
            service().setInput(deflater);
            deflater.finish();
            final var output = new byte[128];
            while (!deflater.finished()) {
                final var len = deflater.deflate(output);
                baos.write(output, 0, len);
            }
            deflater.end(); // redundant, invoked in close()
            baos.flush(); // no-op
            compressed = baos.toByteArray();
        }
        printf(level, noWrap, compressed);
        try (var inflater = new Inflater(noWrap)) {
            inflater.setInput(compressed);
            final var uncompressed = new byte[HelloWorld.BYTES];
            final var length = inflater.inflate(uncompressed);
            assert inflater.inflate(new byte[1]) == 0;
            assert inflater.finished();
            Assertions.assertEquals(uncompressed.length, length);
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
            inflater.end();  // redundant, invoked in close()
        }
        try (var baos = new ByteArrayOutputStream(HelloWorld.BYTES);
             var inflater = new Inflater(noWrap)) {
            inflater.setInput(compressed);
            final var output = new byte[128];
            while (!inflater.finished()) {
                final var len = inflater.inflate(output);
                baos.write(output, 0, len);
            }
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
        final var size = 128;
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var dos = new DeflaterOutputStream(baos, new Deflater(level), size)) {
            service().write(dos);
            dos.finish(); // redundant, invoked in close()
            baos.flush(); // no-op
            compressed = baos.toByteArray();
        }
        try (var bais = new ByteArrayInputStream(compressed);
             var iis = new InflaterInputStream(bais, new Inflater(), size)) {
            final var uncompressed = iis.readAllBytes();
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
        }
    }

    @MethodSource({"levelAndNoWrapStream"})
    @ParameterizedTest
    void __DeflatorOutputStream(final int level, final boolean noWrap) throws IOException {
        final var size = 128;
        final byte[] compressed;
        try (var baos = new ByteArrayOutputStream();
             var dos = new DeflaterOutputStream(baos, new Deflater(level, noWrap), size)) {
            service().write(dos);
            dos.finish(); // redundant, invoked in close()
            baos.flush(); // no-op
            compressed = baos.toByteArray();
        }
        try (var bais = new ByteArrayInputStream(compressed);
             var iis = new InflaterInputStream(bais, new Inflater(noWrap), size)) {
            final var uncompressed = iis.readAllBytes();
            Assertions.assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(),
                                         uncompressed);
        }
    }
}
