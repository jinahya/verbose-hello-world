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
    @DisplayName("should update <hello, world> through every real <Checksum> subtype")
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

            @DisplayName("xxHash32 / Commons-Codec")
            @Test
            void xxHash32_CommonsCodec__() {
                final var checksum = new XXHash32(SEED);
                final var result = service().update(checksum);
                assertSame(checksum, result);
                printf("xxHash32", checksum.getValue());
            }

            @DisplayName("xxHash32 / Lz4")
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

            @DisplayName("xxHash64 / Lz4")
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

    @Nested
    class CheckedOutputStream_Test {

        static Stream<Checksum> checksumStream() {
            return HelloWorld_Update_Checksum__Test.checksumStream();
        }

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
