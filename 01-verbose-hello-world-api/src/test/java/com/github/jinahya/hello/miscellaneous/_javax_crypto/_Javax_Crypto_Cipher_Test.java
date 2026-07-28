package com.github.jinahya.hello.miscellaneous._javax_crypto;

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

import com.github.jinahya.hello.api.annotations.*;
import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import javax.crypto.*;
import java.nio.file.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.miscellaneous._javax_crypto._Javax_Crypto_Cipher_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for testing {@link Cipher} round-trip encryption / decryption with the JCA-mandatory
 * transformations.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
 * (Java 26)
 */
@DisplayName("javax.crypto.Cipher")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_Cipher_Test {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code AES/CBC/NoPadding} round-trips random plain text.
     *
     * @param keysize the AES key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("AES/CBC/NoPadding")
    @ValueSource(ints = {128})
    @ParameterizedTest
    void __AES_CBC_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1, 64) * 16];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = AES_CBC_NoPadding(keysize);
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code AES/CBC/PKCS5Padding} round-trips random plain text.
     *
     * @param keysize the AES key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("AES/CBC/PKCS5Padding")
    @ValueSource(ints = {128})
    @ParameterizedTest
    void __AES_CBC_PKCS5Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = AES_CBC_PKCS5Padding(keysize);
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code AES/ECB/NoPadding} round-trips random plain text.
     *
     * @param keysize the AES key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("AES/ECB/NoPadding")
    @ValueSource(ints = {128})
    @ParameterizedTest
    void __AES_ECB_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1, 64) * 16];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = AES_ECB_NoPadding(keysize);
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code AES/ECB/PKCS5Padding} round-trips random plain text.
     *
     * @param keysize the AES key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("AES/ECB/PKCS5Padding")
    @ValueSource(ints = {128})
    @ParameterizedTest
    void __AES_ECB_PKCS5Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = AES_ECB_PKCS5Padding(keysize);
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code AES/GCM/NoPadding} round-trips random plain text.
     *
     * @param keysize the AES key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("AES/GCM/NoPadding")
    @ValueSource(ints = {128, 256})
    @ParameterizedTest
    void __AES_GCM_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = AES_GCM_NoPadding(keysize);
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        if (symmetric.aad() != null) {
            cipher.updateAAD(symmetric.aad());
        }
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey(), symmetric.params());
        if (symmetric.aad() != null) {
            cipher.updateAAD(symmetric.aad());
        }
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code ChaCha20-Poly1305} round-trips random plain text.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("ChaCha20-Poly1305")
    @Test
    void __ChaCha20_Poly1305() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = ChaCha20_Poly1305();
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        if (symmetric.aad() != null) {
            cipher.updateAAD(symmetric.aad());
        }
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey(), symmetric.params());
        if (symmetric.aad() != null) {
            cipher.updateAAD(symmetric.aad());
        }
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code DESede/CBC/NoPadding} round-trips random plain text.
     *
     * @param keysize the DESede key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("DESede/CBC/NoPadding")
    @ValueSource(ints = {168})
    @_LatestLTS
    @ParameterizedTest
    void __DESede_CBC_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1, 128) * 8];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = DESede_CBC_NoPadding(keysize);
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code DESede/CBC/PKCS5Padding} round-trips random plain text.
     *
     * @param keysize the DESede key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("DESede/CBC/PKCS5Padding")
    @ValueSource(ints = {168})
    @_LatestLTS
    @ParameterizedTest
    void __DESede_CBC_PKCS5Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = DESede_CBC_PKCS5Padding(keysize);
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code DESede/ECB/NoPadding} round-trips random plain text.
     *
     * @param keysize the DESede key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("DESede/ECB/NoPadding")
    @ValueSource(ints = {168})
    @_LatestLTS
    @ParameterizedTest
    void __DESede_ECB_NoPadding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1, 128) * 8];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = DESede_ECB_NoPadding(keysize);
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code DESede/ECB/PKCS5Padding} round-trips random plain text.
     *
     * @param keysize the DESede key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("DESede/ECB/PKCS5Padding")
    @ValueSource(ints = {168})
    @_LatestLTS
    @ParameterizedTest
    void __DESede_ECB_PKCS5Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = DESede_ECB_PKCS5Padding(keysize);
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code PBEWithHmacSHA256AndAES_128} round-trips random plain text.
     *
     * @throws Exception if any error occurs.
     */
    @DisplayName("PBEWithHmacSHA256AndAES_128")
    @_LatestJDK
    @Test
    void __PBEWithHmacSHA256AndAES_128() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = PBEWithHmacSHA256AndAES_128();
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code PBEWithHmacSHA256AndAES_256} round-trips random plain text.
     *
     * @throws Exception if any error occurs.
     */
    @DisplayName("PBEWithHmacSHA256AndAES_256")
    @_LatestJDK
    @Test
    void __PBEWithHmacSHA256AndAES_256() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var symmetric = PBEWithHmacSHA256AndAES_256();
        final var cipher = symmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey(), symmetric.params());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code RSA/ECB/PKCS1Padding} round-trips random plain text.
     *
     * @param keysize the RSA key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("RSA/ECB/PKCS1Padding")
    @ValueSource(ints = {1024, 2048})
    @_LatestLTS
    @ParameterizedTest
    void __RSA_ECB_PKCS1Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1, keysize / 8 - 11 + 1)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var asymmetric = RSA_ECB_PKCS1Padding(keysize);
        final var cipher = asymmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, asymmetric.keyPair().getPublic());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, asymmetric.keyPair().getPrivate());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code RSA/ECB/OAEPWithSHA-1AndMGF1Padding} round-trips random plain text.
     *
     * @param keysize the RSA key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    @ValueSource(ints = {1024, 2048})
    @_LatestLTS
    @_LatestJDK
    @ParameterizedTest
    void __RSA_ECB_OAEPWithSHA_1AndMGF1Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1, keysize / 8 - 42 + 1)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var asymmetric = RSA_ECB_OAEPWithSHA_1AndMGF1Padding(keysize);
        final var cipher = asymmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, asymmetric.keyPair().getPublic());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, asymmetric.keyPair().getPrivate());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code RSA/ECB/OAEPWithSHA-256AndMGF1Padding} round-trips random plain text.
     *
     * @param keysize the RSA key size, in bits.
     * @throws Exception if any error occurs.
     */
    @DisplayName("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    @ValueSource(ints = {1024, 2048})
    @_LatestLTS
    @_LatestJDK
    @ParameterizedTest
    void __RSA_ECB_OAEPWithSHA_256AndMGF1Padding(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1, keysize / 8 - 66 + 1)];
        ThreadLocalRandom.current().nextBytes(plain);
        final var asymmetric = RSA_ECB_OAEPWithSHA_256AndMGF1Padding(keysize);
        final var cipher = asymmetric.cipher();
        // --------------------------------------------------------------------------------- encrypt
        cipher.init(Cipher.ENCRYPT_MODE, asymmetric.keyPair().getPublic());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, asymmetric.keyPair().getPrivate());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }
}
