package com.github.jinahya.hello.api._javax_crypto;

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
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * An integration test class for
 * {@link com.github.jinahya.hello.api.HelloWorld#updateAAD(Cipher) HelloWorld.updateAAD(cipher)}
 * method that exercises real AEAD {@link Cipher} transformations &mdash; {@code AES/GCM/NoPadding}
 * and {@code ChaCha20-Poly1305}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.hello.api.HelloWorld#updateAAD(Cipher)
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5116">RFC 5116 &mdash; An Interface and
 * Algorithms for Authenticated Encryption (AEAD)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5288">RFC 5288 &mdash; AES Galois Counter
 * Mode (GCM) Cipher Suites for TLS</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7539">RFC 7539 &mdash;
 * ChaCha20 and Poly1305 for IETF Protocols</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc8439">RFC 8439 &mdash;
 * ChaCha20 and Poly1305 for IETF Protocols (obsoletes RFC 7539)</a>
 */
@DisplayName("updateAAD(cipher)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_UpdateAAD_Cipher__Test extends HelloWorld__Test {

    /**
     * Prints a one-line summary of an AEAD ciphertext &mdash; transformation, optional parameter,
     * byte length, and Base64-encoded first/last four characters &mdash; to {@link System#out}.
     *
     * @param transformation the transformation name to display.
     * @param parameter      an optional parameter to display (may be {@code null}).
     * @param ciphertext     the bytes to summarize.
     */
    private static void printf(final String transformation, final Object parameter,
                               final byte[] ciphertext) {
        final var encoded = Base64.getEncoder().encodeToString(ciphertext);
        System.out.printf("%30s %20s (%4d) %s...%s%n", transformation,
                          Optional.ofNullable(parameter).orElse(""),
                          ciphertext.length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    /**
     * Stubs
     * {@link com.github.jinahya.hello.api.HelloWorld#updateAAD(Cipher) service().updateAAD(cipher)}
     * so that, when invoked with any non-{@code null} {@link Cipher}, it forwards the
     * {@link HelloWorld__TestUtils#hello_world_byte_array() hello-world bytes} to the cipher's
     * {@link Cipher#updateAAD(byte[])} method and returns the cipher.
     */
    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        Mockito.doAnswer(i -> {
            final var cipher = i.getArgument(0, Cipher.class);
            cipher.updateAAD(HelloWorld__TestUtils.hello_world_byte_array());
            return cipher;
        }).when(service()).updateAAD(ArgumentMatchers.<Cipher>notNull());
    }

    /**
     * A nested test class for {@code AES/GCM/NoPadding}: feeds the hello-world bytes as AAD on the
     * encryption side, encrypts the canonical plaintext, then on a freshly initialized cipher feeds
     * the same AAD on the decryption side, and asserts the decrypted bytes match the original
     * plaintext &mdash; verifying that AAD on both sides authenticates.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc5116">RFC 5116 &mdash; AEAD</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc5288">RFC 5288 &mdash; AES-GCM Cipher
     * Suites</a>
     */
    // ---------------------------------------------------------------------------------------------
    @DisplayName("AES/GCM/NoPadding")
    @Nested
    class AES_GCM_NoPadding_Test {

        // JCE std name (AES: FIPS 197; GCM: NIST SP 800-38D)
        private static final String ALGORITHM = "AES";

        private static final String TRANSFORMATION = ALGORITHM + "/GCM/NoPadding";

        // 12 bytes is the recommended IV length for GCM (NIST SP 800-38D §5.2.1.1)
        private static final int GCM_IV_LENGTH = 12;

        // 128 bits is the maximum authentication tag length
        private static final int GCM_TAG_LENGTH = 128;

        /**
         * Verifies that a round trip through a {@link Cipher#getInstance(String) AES/GCM/NoPadding}
         * cipher, with the hello-world bytes fed as AAD on both encryption and decryption sides,
         * recovers the original plaintext.
         *
         * @param keysize the AES key size to test &mdash; {@code 128} or {@code 256} bits.
         */
        @DisplayName("should round-trip plaintext through a <real AES/GCM/NoPadding> cipher")
        @ValueSource(ints = {128, 256})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyGenerator.getInstance(ALGORITHM);
            generator.init(keysize);
            final var key = generator.generateKey();
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[GCM_IV_LENGTH];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            }
            final var plaintext = HelloWorld__TestUtils.hello_world_byte_array();
            // ----------------------------------------------------------------------------- encrypt
            final byte[] ciphertext;
            {
                final var cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, key, params);
                service().updateAAD(cipher);
                ciphertext = cipher.doFinal(plaintext);
                printf(TRANSFORMATION, keysize, ciphertext);
            }
            // ----------------------------------------------------------------------------- decrypt
            final byte[] decrypted;
            {
                final var cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, key, params);
                service().updateAAD(cipher);
                decrypted = cipher.doFinal(ciphertext);
            }
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(plaintext, decrypted);
        }
    }

    /**
     * A nested test class for {@code ChaCha20-Poly1305}: feeds the hello-world bytes as AAD on the
     * encryption side, encrypts the canonical plaintext, then on a freshly initialized cipher feeds
     * the same AAD on the decryption side, and asserts the decrypted bytes match the original
     * plaintext.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc8439">RFC 8439 &mdash; ChaCha20 and
     * Poly1305</a>
     */
    @DisplayName("ChaCha20-Poly1305")
    @Nested
    class ChaCha20_Poly1305_Test {

        // JCE std name (ChaCha20: RFC 7539; Poly1305: RFC 8439)
        private static final String ALGORITHM = "ChaCha20";

        private static final String TRANSFORMATION = "ChaCha20-Poly1305";

        // 12 bytes (96 bits) per RFC 8439 §2.3
        private static final int NONCE_LENGTH = 12;

        /**
         * Verifies that a round trip through a {@link Cipher#getInstance(String) ChaCha20-Poly1305}
         * cipher, with the hello-world bytes fed as AAD on both encryption and decryption sides,
         * recovers the original plaintext.
         */
        @DisplayName("should round-trip plaintext through a <real ChaCha20-Poly1305> cipher")
        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyGenerator.getInstance(ALGORITHM);
            // ChaCha20 only supports 256-bit keys
            generator.init(256);
            final var key = generator.generateKey();
            final AlgorithmParameterSpec params;
            {
                final var nonce = new byte[NONCE_LENGTH];
                ThreadLocalRandom.current().nextBytes(nonce);
                params = new IvParameterSpec(nonce);
            }
            final var plaintext = HelloWorld__TestUtils.hello_world_byte_array();
            // ----------------------------------------------------------------------------- encrypt
            final byte[] ciphertext;
            {
                final var cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.ENCRYPT_MODE, key, params);
                service().updateAAD(cipher);
                ciphertext = cipher.doFinal(plaintext);
                printf(TRANSFORMATION, null, ciphertext);
            }
            // ----------------------------------------------------------------------------- decrypt
            final byte[] decrypted;
            {
                final var cipher = Cipher.getInstance(TRANSFORMATION);
                cipher.init(Cipher.DECRYPT_MODE, key, params);
                service().updateAAD(cipher);
                decrypted = cipher.doFinal(ciphertext);
            }
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(plaintext, decrypted);
        }
    }
}
