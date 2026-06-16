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
import com.google.common.io.*;
import lombok.*;
import lombok.extern.slf4j.*;
import net.jpountz.xxhash.*;
import org.apache.commons.codec.digest.XXHash32;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.util.concurrent.*;
import java.util.stream.*;
import java.util.zip.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * A class for exploring {@link HelloWorld#update(Checksum) update(checksum)} method with real
 * {@link Checksum} implementations from {@code java.util.zip} and third-party libraries (Apache
 * Commons Codec, lz4-java).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@_NotForPublishing
@DisplayName("update(checksum)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Checksum__Test extends HelloWorld__Test {

    private static Stream<Checksum> checksumStream() {
        return Stream.of(
                spy(new CRC32()),
                spy(new CRC32C()),
                spy(new Adler32())
        );
    }

    private static void printf(final String algorithm, final long value) {
        System.out.printf("%30s: 0x%016x%n", algorithm, value);
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        doAnswer(i -> {
            final var checksum = i.getArgument(0, Checksum.class);
            checksum.update(hello_world_byte_array());
            return checksum;
        }).when(service()).<Checksum>update(any());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the {@link HelloWorld#update(Checksum) update(checksum)} method updates the
     * {@code "hello, world"} bytes through every real {@link Checksum} subtype supplied by
     * {@link #checksumStream()}.
     *
     * @param checksum a real {@link Checksum} instance.
     */
    @DisplayName("real Checksum")
    @MethodSource("checksumStream")
    @ParameterizedTest
    void __(final Checksum checksum) {
        final var service = service();
        final var result = service.update(checksum);
        assertSame(checksum, result);
        printf(checksum.getClass().getSimpleName(), checksum.getValue());
    }

    // ----------------------------------------------------------------------- Apache Commons Codec
    @DisplayName("xxHash")
    @Nested
    class XxHash_Test {

        private static final int SEED = ThreadLocalRandom.current().nextInt();

        @DisplayName("XXH32")
        @Nested
        class XXH32_Test {

            /**
             * Verifies that the {@link HelloWorld#update(Checksum) update(checksum)} method updates
             * the {@code "hello, world"} bytes through Apache Commons Codec's {@link XXHash32}.
             */
            @DisplayName("Commons-Codec")
            @Test
            void xxHash32_CommonsCodec__() {
                final var checksum = new XXHash32(SEED);
                final var result = service().update(checksum);
                assertSame(checksum, result);
                printf("xxHash32", checksum.getValue());
            }

            /**
             * Verifies that the {@link HelloWorld#update(Checksum) update(checksum)} method updates
             * the {@code "hello, world"} bytes through lz4-java's streaming {@code xxHash32}.
             */
            @DisplayName("Lz4")
            @Test
            void xxHash32_Lz4__() {
                try (final var hash = XXHashFactory.fastestInstance().newStreamingHash32(SEED)) {
                    final var checksum = hash.asChecksum();
                    final var result = service().update(checksum);
                    assertSame(checksum, result);
                    printf("xxHash32", checksum.getValue());
                }
            }
        }

        @DisplayName("XXH64")
        @Nested
        class XXH64_Test {

            /**
             * Verifies that the {@link HelloWorld#update(Checksum) update(checksum)} method updates
             * the {@code "hello, world"} bytes through lz4-java's streaming {@code xxHash64}.
             */
            @DisplayName("Lz4")
            @Test
            void xxHash64_Lz4__() {
                try (final var hash = XXHashFactory.fastestInstance().newStreamingHash64(SEED)) {
                    final var checksum = hash.asChecksum();
                    final var result = service().update(checksum);
                    assertSame(checksum, result);
                    printf("xxHash64", checksum.getValue());
                }
            }
        }

        @DisplayName("XXH3")
        @Nested
        class XXH3_Test {

        }
    }

    @DisplayName("CheckedOutputStream")
    @Nested
    class CheckedOutputStream_Test {

        static Stream<Checksum> checksumStream() {
            return HelloWorld_Update_Checksum__Test.checksumStream();
        }

        /**
         * Verifies that {@link CheckedOutputStream} and {@link CheckedInputStream} produce the same
         * {@link Checksum} value when {@link HelloWorld#write(OutputStream) write(stream)} writes
         * the {@code "hello, world"} bytes through them.
         *
         * @param checksum a real {@link Checksum} instance.
         */
        @DisplayName("happy path")
        @MethodSource({"checksumStream"})
        @ParameterizedTest
        void __(final Checksum checksum) throws IOException {
            try (var cos = new CheckedOutputStream(OutputStream.nullOutputStream(), checksum)) {
                service().write(cos).flush();
                checksum.reset();
                try (var cis = new CheckedInputStream(hello_world_inputstream(), checksum)) {
                    ByteStreams.exhaust(cis);
                    assertEquals(cos.getChecksum().getValue(), cis.getChecksum().getValue());
                }
            }
        }
    }
}
