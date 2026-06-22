package com.github.jinahya.hello.api;

import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.nio.*;
import java.nio.charset.*;
import java.util.function.*;
import java.util.stream.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

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

/**
 * A class for testing {@link HelloWorldUtils} utility methods.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorldUtils")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldUtils_Test extends HelloWorld__Test {

    private static Stream<Supplier<ByteBuffer>> byteBufferSupplierStream() {
        return Stream.of(
                () -> ByteBuffer.allocate(HelloWorld.BYTES),
                () -> ByteBuffer.allocateDirect(HelloWorld.BYTES)
        );
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Stubs the service's collaborator methods used by the tests in this class.
     */
    @BeforeEach
    void __() {
        set_array_sets_hello_world_bytes(service());
        put_buffer_put_actual_hello_world_bytes(service());
        append_packet_appends_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("array(service)")
    @Nested
    class Array_Test {

        /**
         * Asserts that {@code HelloWorldUtils.array(service)} returns the
         * {@code hello-world-bytes}.
         */
        @DisplayName("hello-world-bytes")
        @Test
        void __() {
            final var result = HelloWorldUtils.array(service());
            assertArrayEquals(HelloWorld__TestUtils.hello_world_byte_array(), result);
        }
    }

    @DisplayName("buffer(service, supplier)")
    @Nested
    class Buffer_Supplier_Test {

        private static Stream<Supplier<ByteBuffer>> byteBufferSupplierStream() {
            return HelloWorldUtils_Test.byteBufferSupplierStream();
        }

        /**
         * Asserts that {@code HelloWorldUtils.buffer(service, supplier)} returns a
         * {@link ByteBuffer} containing the {@code hello-world-bytes}.
         *
         * @param supplier the {@link ByteBuffer} supplier under test.
         */
        @DisplayName("ByteBuffer / hello-world-bytes")
        @MethodSource({"byteBufferSupplierStream"})
        @ParameterizedTest
        void __(final Supplier<ByteBuffer> supplier) {
            final var result = HelloWorldUtils.buffer(service(), supplier);
            assertEquals(hello_world_byte_buffer(), result);
        }
    }

    @DisplayName("string(service)")
    @Nested
    class String_Test {

        /**
         * Asserts that {@code HelloWorldUtils.string(service)} returns the
         * {@code hello-world-string}.
         */
        @DisplayName("hello-world-string")
        @Test
        void __() {
            final var result = HelloWorldUtils.string(service());
            assertEquals(HelloWorld__TestConstants.HELLO_WORLD_STRING, result);
        }
    }

    @DisplayName("decode(service, supplier)")
    @Nested
    class Decode_Supplier_Test {

        private static Stream<Supplier<ByteBuffer>> byteBufferSupplierStream() {
            return HelloWorldUtils_Test.byteBufferSupplierStream();
        }

        /**
         * Asserts that {@code HelloWorldUtils.decode(service, supplier)} returns a
         * {@link CharBuffer} containing the {@code hello-world-string}.
         *
         * @param supplier the {@link ByteBuffer} supplier under test.
         * @throws CharacterCodingException if a decoding error occurs.
         */
        @DisplayName("CharBuffer / hello-world-string")
        @MethodSource({"byteBufferSupplierStream"})
        @ParameterizedTest
        void __(final Supplier<ByteBuffer> supplier) throws CharacterCodingException {
            final var result = HelloWorldUtils.decode(service(), supplier);
            assertEquals(hello_world_char_buffer(), result);
        }
    }
}
