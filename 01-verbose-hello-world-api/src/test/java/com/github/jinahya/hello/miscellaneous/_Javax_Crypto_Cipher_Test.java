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
import java.nio.file.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.miscellaneous._Javax_Crypto_Cipher_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_Cipher_Test {

    @TempDir
    private static Path tempDir;

    // ---------------------------------------------------------------------------------------------
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
        cipher.updateAAD(symmetric.aad());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey(), symmetric.params());
        cipher.updateAAD(symmetric.aad());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------
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
        cipher.updateAAD(symmetric.aad());
        final var encrypted = cipher.doFinal(plain);
        // --------------------------------------------------------------------------------- decrypt
        cipher.init(Cipher.DECRYPT_MODE, symmetric.secretKey(), symmetric.params());
        cipher.updateAAD(symmetric.aad());
        final var decrypted = cipher.doFinal(encrypted);
        // ------------------------------------------------------------------------------------ then
        assertArrayEquals(plain, decrypted);
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("DESede/CBC/NoPadding")
    @ValueSource(ints = {168})
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
    @DisplayName("DESede/CBC/PKCS5Padding")
    @ValueSource(ints = {168})
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
    @DisplayName("DESede/ECB/NoPadding")
    @ValueSource(ints = {168})
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
    @DisplayName("DESede/ECB/PKCS5Padding")
    @ValueSource(ints = {168})
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
    @DisplayName("RSA/ECB/PKCS1Padding")
    @ValueSource(ints = {1024, 2048})
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
    @DisplayName("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    @ValueSource(ints = {1024, 2048})
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
    @DisplayName("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    @ValueSource(ints = {1024, 2048})
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
