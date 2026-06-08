package com.github.jinahya.hello.miscellaneous;

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

import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.file.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class exploring {@link Cipher} transformations that are not in the JCA-mandatory list
 * covered by {@link _Javax_Crypto_Cipher_Test}: a counter-mode AES, a legacy AES key-wrap, the
 * streaming (non-AEAD) ChaCha20, and a legacy Blowfish block cipher.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#cipher-algorithms">JDK
 * 26 JCA Standard Algorithm Names &mdash; Cipher Algorithms</a>
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_Cipher__Test {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code AES/CTR/NoPadding} (counter mode) round-trips arbitrary-length plain
     * text. CTR turns AES into a stream cipher; the spec page lists the {@code CTR} mode but
     * neither it nor its transformations are JCA-mandatory.
     *
     * @param keysize the AES key size in bits.
     */
    @DisplayName("AES/CTR/NoPadding")
    @ValueSource(ints = {128, 192, 256})
    @ParameterizedTest
    void __AES_CTR_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keysize);
        final var secretKey = keyGen.generateKey();
        final var cipher = Cipher.getInstance("AES/CTR/NoPadding");
        final var iv = new byte[cipher.getBlockSize()];
        ThreadLocalRandom.current().nextBytes(iv);
        final var params = new IvParameterSpec(iv);
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, params);
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, secretKey, params);
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code AESWrap} (RFC 3394 AES Key Wrap) round-trips an AES SecretKey: an AES
     * KEK is generated, an AES data key is wrapped under the KEK, then unwrapped, and the unwrapped
     * key is byte-equal to the original. Listed on the spec page but not in the mandatory
     * transformations list.
     *
     * @param keksize the AES KEK size in bits.
     */
    @DisplayName("AESWrap")
    @ValueSource(ints = {128, 192, 256})
    @ParameterizedTest
    void __AESWrap(final int keksize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final SecretKey kek;
        {
            final var keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(keksize);
            kek = keyGen.generateKey();
        }
        final SecretKey dataKey;
        {
            // 128-bit data key (16 bytes — multiple of 8, required by RFC 3394)
            final var keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            dataKey = keyGen.generateKey();
        }
        final var cipher = Cipher.getInstance("AESWrap");
        // ------------------------------------------------------------------------------------ wrap
        cipher.init(Cipher.WRAP_MODE, kek);
        final var wrapped = cipher.wrap(dataKey);
        // ---------------------------------------------------------------------------------- unwrap
        cipher.init(Cipher.UNWRAP_MODE, kek);
        final var unwrapped = cipher.unwrap(wrapped, "AES", Cipher.SECRET_KEY);
        // ------------------------------------------------------------------------------------ then
        assertEquals("AES", unwrapped.getAlgorithm());
        assertArrayEquals(dataKey.getEncoded(), unwrapped.getEncoded());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code ChaCha20} (the streaming, non-AEAD form &mdash; distinct from the
     * mandatory {@code ChaCha20-Poly1305}) round-trips arbitrary-length plain text. Listed on the
     * spec page but not mandatory; uses a 256-bit key and a 12-byte nonce, with the optional 4-byte
     * counter starting at zero.
     */
    @DisplayName("ChaCha20")
    @Test
    void __ChaCha20() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var keyGen = KeyGenerator.getInstance("ChaCha20");
        keyGen.init(256);
        final var secretKey = keyGen.generateKey();
        final var nonce = new byte[12];
        ThreadLocalRandom.current().nextBytes(nonce);
        final var params = new ChaCha20ParameterSpec(nonce, 0);
        final var cipher = Cipher.getInstance("ChaCha20");
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, params);
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, secretKey, params);
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code Blowfish/CBC/PKCS5Padding} (a 64-bit block cipher predating AES)
     * round-trips arbitrary-length plain text. Listed on the spec page as a legacy algorithm; not
     * mandatory and considered weak for modern use due to its small block size (birthday-bound
     * vulnerabilities above ~32 GiB).
     *
     * @param keysize the Blowfish key size in bits (variable; 128 picked as a representative).
     */
    @DisplayName("Blowfish/CBC/PKCS5Padding")
    @ValueSource(ints = {128})
    @ParameterizedTest
    void __Blowfish_CBC_PKCS5Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var keyGen = KeyGenerator.getInstance("Blowfish");
        keyGen.init(keysize);
        final var secretKey = keyGen.generateKey();
        final var cipher = Cipher.getInstance("Blowfish/CBC/PKCS5Padding");
        final var iv = new byte[cipher.getBlockSize()];
        ThreadLocalRandom.current().nextBytes(iv);
        final var params = new IvParameterSpec(iv);
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, params);
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, secretKey, params);
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }
}
