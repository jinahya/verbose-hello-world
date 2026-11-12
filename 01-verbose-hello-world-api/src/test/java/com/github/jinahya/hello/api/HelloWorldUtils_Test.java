package com.github.jinahya.hello.api;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.nio.*;
import java.nio.charset.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @DisplayName("buffer(service)")
    @Nested
    class Buffer_Test {

        /**
         * Asserts that {@code HelloWorldUtils.buffer(service)} returns a {@link ByteBuffer}, not
         * flipped, on which the {@code hello-world-bytes} are put.
         */
        @DisplayName("ByteBuffer / not flipped / hello-world-bytes")
        @Test
        void __() {
            final var result = HelloWorldUtils.buffer(service());
            assertEquals(HelloWorld.BYTES, result.position());
            assertFalse(result.hasRemaining());
            assertEquals(hello_world_byte_buffer(), result.flip());
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

    @DisplayName("decode(service)")
    @Nested
    class Decode_Test {

        /**
         * Asserts that {@code HelloWorldUtils.decode(service)} returns a {@link CharBuffer}
         * containing the {@code hello-world-string}.
         *
         * @throws CharacterCodingException if a decoding error occurs.
         */
        @DisplayName("CharBuffer / hello-world-string")
        @Test
        void __() throws CharacterCodingException {
            final var result = HelloWorldUtils.decode(service());
            assertEquals(hello_world_char_buffer(), result);
        }
    }

    @DisplayName("decode(service, decoder)")
    @Nested
    class Decode_Decoder_Test {

        /**
         * Asserts that {@code HelloWorldUtils.decode(service, decoder)} returns a
         * {@link CharBuffer} containing the {@code hello-world-string} for a decoder of the given
         * ASCII-compatible charset.
         *
         * @param charsetName the name of the charset whose decoder is used.
         * @throws CharacterCodingException if a decoding error occurs.
         */
        @DisplayName("CharBuffer / hello-world-string")
        @ValueSource(strings = {"US-ASCII", "ISO-8859-1", "UTF-8"})
        @ParameterizedTest
        void __(final String charsetName) throws CharacterCodingException {
            final var decoder = Charset.forName(charsetName).newDecoder();
            final var result = HelloWorldUtils.decode(service(), decoder);
            assertEquals(hello_world_char_buffer(), result);
        }

        /**
         * Asserts that {@code HelloWorldUtils.decode(service, decoder)} may be invoked twice in a
         * row with the same decoder, yielding the {@code hello-world-string} each time.
         *
         * @throws CharacterCodingException if a decoding error occurs.
         */
        @DisplayName("same decoder / sequential reuse")
        @Test
        void __reuse() throws CharacterCodingException {
            final var decoder = StandardCharsets.US_ASCII.newDecoder();
            assertEquals(hello_world_char_buffer(), HelloWorldUtils.decode(service(), decoder));
            assertEquals(hello_world_char_buffer(), HelloWorldUtils.decode(service(), decoder));
        }

        /**
         * Asserts that {@code HelloWorldUtils.decode(service, decoder)} throws a
         * {@link NullPointerException} when the {@code decoder} is {@code null}, without
         * invoking the service.
         */
        @DisplayName("NullPointerException / decoder is null")
        @Test
        void _ThrowNullPointerException_DecoderIsNull() {
            assertThrows(NullPointerException.class,
                         () -> HelloWorldUtils.decode(service(), (CharsetDecoder) null));
            verify(service(), never()).put(any());
        }
    }

    @DisplayName("decode(service, charset)")
    @Nested
    class Decode_Charset_Test {

        /**
         * Asserts that {@code HelloWorldUtils.decode(service, charset)} returns a
         * {@link CharBuffer} containing the {@code hello-world-string} for the given
         * ASCII-compatible charset.
         *
         * @param charsetName the name of the charset in which the bytes are decoded.
         * @throws CharacterCodingException if a decoding error occurs.
         */
        @DisplayName("CharBuffer / hello-world-string")
        @ValueSource(strings = {"US-ASCII", "ISO-8859-1", "UTF-8"})
        @ParameterizedTest
        void __(final String charsetName) throws CharacterCodingException {
            final var result = HelloWorldUtils.decode(service(), Charset.forName(charsetName));
            assertEquals(hello_world_char_buffer(), result);
        }

        /**
         * Asserts that {@code HelloWorldUtils.decode(service, charset)} throws a
         * {@link NullPointerException} when the {@code charset} is {@code null}, without
         * invoking the service.
         */
        @DisplayName("NullPointerException / charset is null")
        @Test
        void _ThrowNullPointerException_CharsetIsNull() {
            assertThrows(NullPointerException.class,
                         () -> HelloWorldUtils.decode(service(), (Charset) null));
            verify(service(), never()).put(any());
        }
    }
}
