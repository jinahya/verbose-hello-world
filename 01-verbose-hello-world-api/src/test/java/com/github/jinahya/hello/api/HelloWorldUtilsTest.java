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

import static com.github.jinahya.hello.api.HelloWorldTestUtils.*;
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
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorldUtilsTest extends HelloWorldTest {

    private static Stream<Supplier<ByteBuffer>> byteBufferSupplierStream() {
        return Stream.of(
                () -> ByteBuffer.allocate(HelloWorld.BYTES),
                () -> ByteBuffer.allocateDirect(HelloWorld.BYTES)
        );
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        set_array_sets_actual_hello_world_bytes(service());
        put_buffer_will_put_actual_hello_world_bytes(service());
        append_packet_appends_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("array(service)")
    @Nested
    class Array_Test {

        @Test
        void __() {
            final var result = HelloWorldUtils.array(service());
            assertArrayEquals(HelloWorldTestUtils.hello_world_byte_array(), result);
        }
    }

    @DisplayName("buffer(service, supplier)")
    @Nested
    class Buffer_Supplier_Test {

        private static Stream<Supplier<ByteBuffer>> byteBufferSupplierStream() {
            return HelloWorldUtilsTest.byteBufferSupplierStream();
        }

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

        @Test
        void __() {
            final var result = HelloWorldUtils.string(service());
            assertEquals(HelloWorldTestConstants.HELLO_WORLD_STRING, result);
        }
    }

    @DisplayName("decode(service, supplier)")
    @Nested
    class Decode_Supplier_Test {

        private static Stream<Supplier<ByteBuffer>> byteBufferSupplierStream() {
            return HelloWorldUtilsTest.byteBufferSupplierStream();
        }

        @MethodSource({"byteBufferSupplierStream"})
        @ParameterizedTest
        void __(final Supplier<ByteBuffer> supplier) throws CharacterCodingException {
            final var result = HelloWorldUtils.decode(service(), supplier);
            assertEquals(hello_world_char_buffer(), result);
        }
    }
}
