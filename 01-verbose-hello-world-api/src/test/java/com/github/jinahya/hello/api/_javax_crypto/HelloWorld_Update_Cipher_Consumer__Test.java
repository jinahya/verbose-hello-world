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

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Cipher_Consumer__Test extends HelloWorld__Test {

    // JDK 25
    //AES/CBC/NoPadding (128)
    //AES/CBC/PKCS5Padding (128)
    //AES/ECB/NoPadding (128)
    //AES/ECB/PKCS5Padding (128)
    //AES/GCM/NoPadding (128, 256)
    //ChaCha20-Poly1305
    //DESede/CBC/NoPadding (168)
    //DESede/CBC/PKCS5Padding (168)
    //DESede/ECB/NoPadding (168)
    //DESede/ECB/PKCS5Padding (168)
    //RSA/ECB/PKCS1Padding (1024, 2048)
    //RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
    //RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)

    // JDK 26
    // AES/CBC/NoPadding (128)
    // AES/CBC/PKCS5Padding (128)
    // AES/ECB/NoPadding (128)
    // AES/ECB/PKCS5Padding (128)
    // AES/GCM/NoPadding (128, 256)
    // ChaCha20-Poly1305
    // PBEWithHmacSHA256AndAES_128
    // PBEWithHmacSHA256AndAES_256
    // RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
    // RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)

    static final int AES_BLOCK_SIZE = 16;

    static final int DESEDE_BLOCK_SIZE = 8;

    static Key generateSecretKey(final String algorithm, final int keysize) throws Exception {
        final var generator = KeyGenerator.getInstance(algorithm);
        generator.init(keysize);
        return generator.generateKey();
    }

    static KeyPair generateKeyPair(final String algorithm, final int keysize) throws Exception {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(keysize);
        return generator.generateKeyPair();
    }

    private static void printf(final String transformation, final Object parameter,
                               final byte[] encrypted) {
        final var encoded = Base64.getEncoder().encodeToString(encrypted);
        System.out.printf("%40s %20s (%4d) %s...%s%n", transformation,
                          Optional.ofNullable(parameter).orElse(""),
                          encrypted.length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    @SuppressWarnings({"unchecked"})
    void __() {
        doAnswer(i -> {
            final var cipher = i.getArgument(0, Cipher.class);
            final var consumer = i.getArgument(1, Consumer.class);
            consumer.accept(cipher.update(hello_world_byte_array()));
            return cipher;
        }).when(service()).update(any(), any());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("AES/CBC/NoPadding")
    @Nested
    class AES_CBC_NoPadding_Test {

        private static final String ALGORITHM = "AES";

        private static final String MODE = "CBC";

        private static final String PADDING = "NoPadding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                128
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[AES_BLOCK_SIZE];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, r -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            {
                // AES/CBC/NoPadding requires input to be a multiple of the block size (16 bytes).
                // Since "hello, world" is 12 bytes, we pad with 4 zero bytes to complete the block.
                final var updated = cipher.update(new byte[AES_BLOCK_SIZE - HelloWorld.BYTES]);
                if (updated != null) {
                    baos.writeBytes(updated);
                }
            }
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            // decrypted is 16 bytes: 12 bytes of "hello, world" + 4 bytes of zero padding.
            // NoPadding doesn't strip padding on decryption, so we compare only the first 12 bytes.
            assertEquals(AES_BLOCK_SIZE, decrypted.length);
            assertArrayEquals(hello_world_byte_array(), Arrays.copyOf(decrypted, HelloWorld.BYTES));
        }
    }

    @DisplayName("AES/CBC/PKCS5Padding")
    @Nested
    class AES_CBC_PKCS5Padding_Test {

        private static final String ALGORITHM = "AES";

        private static final String MODE = "CBC";

        private static final String PADDING = "PKCS5Padding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                128
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[AES_BLOCK_SIZE];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("AES/ECB/NoPadding")
    @Nested
    class AES_ECB_NoPadding_Test {

        private static final String ALGORITHM = "AES";

        private static final String MODE = "ECB";

        private static final String PADDING = "NoPadding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                128
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            {
                // AES/ECB/NoPadding requires input to be a multiple of the block size (16 bytes).
                // Since "hello, world" is 12 bytes, we pad with 4 zero bytes to complete the block.
                final var updated = cipher.update(new byte[AES_BLOCK_SIZE - HelloWorld.BYTES]);
                if (updated != null) {
                    baos.writeBytes(updated);
                }
                baos.writeBytes(cipher.doFinal());
            }
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            // decrypted is 16 bytes: 12 bytes of "hello, world" + 4 bytes of zero padding.
            // NoPadding doesn't strip padding on decryption, so we compare only the first 12 bytes.
            assertEquals(AES_BLOCK_SIZE, decrypted.length);
            assertArrayEquals(
                    hello_world_byte_array(),
                    Arrays.copyOf(decrypted, HelloWorld.BYTES)
            );
        }
    }

    @DisplayName("AES/ECB/PKCS5Padding")
    @Nested
    class AES_ECB_PKCS5Padding_Test {

        private static final String ALGORITHM = "AES";

        private static final String MODE = "ECB";

        private static final String PADDING = "PKCS5Padding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                128
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("AES/GCM/NoPadding")
    @Nested
    class AES_GCM_NoPadding_Test {

        private static final String ALGORITHM = "AES";

        private static final String MODE = "GCM";

        private static final String PADDING = "NoPadding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        private static final int GCM_IV_LENGTH = 12;  // 12 bytes recommended for GCM

        private static final int GCM_TAG_LENGTH = 128;  // 128 bits

        @ValueSource(ints = {
                128, 256
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[GCM_IV_LENGTH];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            }
            // optional AAD (Additional Authenticated Data) - authenticated but not encrypted
            final byte[] aad;
            if (ThreadLocalRandom.current().nextBoolean()) {
                aad = new byte[ThreadLocalRandom.current().nextInt(1, 32)];
                ThreadLocalRandom.current().nextBytes(aad);
            } else {
                aad = null;
            }
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            if (aad != null) {
                cipher.updateAAD(aad);
            }
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            if (aad != null) {
                cipher.updateAAD(aad);
            }
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            // GCM is a stream cipher mode - no padding needed, decrypted equals original plaintext
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("ChaCha20-Poly1305")
    @Nested
    class ChaCha20_Poly1305_Test {

        private static final String ALGORITHM = "ChaCha20";

        private static final String TRANSFORMATION = "ChaCha20-Poly1305";

        private static final int NONCE_LENGTH = 12;  // 96 bits

        @ValueSource(ints = {
                256  // ChaCha20 only supports 256-bit keys
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final AlgorithmParameterSpec params;
            {
                final var nonce = new byte[NONCE_LENGTH];
                ThreadLocalRandom.current().nextBytes(nonce);
                params = new IvParameterSpec(nonce);
            }
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            // ChaCha20-Poly1305 is a stream cipher (AEAD) - no padding, decrypted equals original
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("DESede/CBC/NoPadding")
    @Nested
    class DESede_CBC_NoPadding_Test {

        private static final String ALGORITHM = "DESede";

        private static final String MODE = "CBC";

        private static final String PADDING = "NoPadding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                168
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[DESEDE_BLOCK_SIZE];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            {
                // DESede block size is 8 bytes. "hello, world" (12 bytes) needs padding to 16 bytes.
                final var paddingNeeded = DESEDE_BLOCK_SIZE - (HelloWorld.BYTES
                                                               % DESEDE_BLOCK_SIZE);
                final var updated = cipher.update(new byte[paddingNeeded]);
                if (updated != null) {
                    baos.writeBytes(updated);
                }
                baos.writeBytes(cipher.doFinal());
            }
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            // decrypted is 16 bytes: 12 bytes of "hello, world" + 4 bytes of zero padding.
            assertEquals(16, decrypted.length);
            assertArrayEquals(
                    hello_world_byte_array(),
                    Arrays.copyOf(decrypted, HelloWorld.BYTES)
            );
        }
    }

    @DisplayName("DESede/CBC/PKCS5Padding")
    @Nested
    class DESede_CBC_PKCS5Padding_Test {

        private static final String ALGORITHM = "DESede";

        private static final String MODE = "CBC";

        private static final String PADDING = "PKCS5Padding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                168
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[DESEDE_BLOCK_SIZE];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new IvParameterSpec(iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("DESede/ECB/NoPadding")
    @Nested
    class DESede_ECB_NoPadding_Test {

        private static final String ALGORITHM = "DESede";

        private static final String MODE = "ECB";

        private static final String PADDING = "NoPadding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                168
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            {
                // DESede block size is 8 bytes. "hello, world" (12 bytes) needs padding to 16 bytes.
                final var required = DESEDE_BLOCK_SIZE - (HelloWorld.BYTES % DESEDE_BLOCK_SIZE);
                final var updated = cipher.update(new byte[required]);
                if (updated != null) {
                    baos.writeBytes(updated);
                }
                baos.writeBytes(cipher.doFinal());
            }
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            // decrypted is 16 bytes: 12 bytes of "hello, world" + 4 bytes of zero padding.
            assertEquals(16, decrypted.length);
            assertArrayEquals(
                    hello_world_byte_array(),
                    Arrays.copyOf(decrypted, HelloWorld.BYTES)
            );
        }
    }

    @DisplayName("DESede/ECB/PKCS5Padding")
    @Nested
    class DESede_ECB_PKCS5Padding_Test {

        private static final String ALGORITHM = "DESede";

        private static final String MODE = "ECB";

        private static final String PADDING = "PKCS5Padding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                168
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    // =================================================================================================================
    // PBE (Password-Based Encryption) - key derived from password + salt + iteration count
    // =================================================================================================================

    @DisplayName("PBEWithHmacSHA256AndAES_128")
    @Nested
    class PBEWithHmacSHA256AndAES_128_Test {

        private static final String TRANSFORMATION = "PBEWithHmacSHA256AndAES_128";

        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var password = "hello, world".toCharArray();
            final var salt = new byte[16];
            ThreadLocalRandom.current().nextBytes(salt);
            final var iterationCount = 65_536;
            final var key = SecretKeyFactory.getInstance(TRANSFORMATION)
                    .generateSecret(new PBEKeySpec(password, salt, iterationCount));
            final var iv = new byte[AES_BLOCK_SIZE];
            ThreadLocalRandom.current().nextBytes(iv);
            final var params = new PBEParameterSpec(
                    salt, iterationCount, new IvParameterSpec(iv)
            );
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, iterationCount, encrypted);
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("PBEWithHmacSHA256AndAES_256")
    @Nested
    class PBEWithHmacSHA256AndAES_256_Test {

        private static final String TRANSFORMATION = "PBEWithHmacSHA256AndAES_256";

        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var password = "hello, world".toCharArray();
            final var salt = new byte[16];
            ThreadLocalRandom.current().nextBytes(salt);
            final var iterationCount = 65_536;
            final var key = SecretKeyFactory.getInstance(TRANSFORMATION)
                    .generateSecret(new PBEKeySpec(password, salt, iterationCount));
            final var iv = new byte[AES_BLOCK_SIZE];
            ThreadLocalRandom.current().nextBytes(iv);
            final var params = new PBEParameterSpec(
                    salt, iterationCount, new IvParameterSpec(iv)
            );
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, iterationCount, encrypted);
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    // =================================================================================================================
    // RSA (asymmetric) - public key encrypts, private key decrypts
    // =================================================================================================================

    @DisplayName("RSA/ECB/PKCS1Padding")
    @Nested
    class RSA_ECB_PKCS1Padding_Test {

        private static final String ALGORITHM = "RSA";

        private static final String MODE = "ECB";

        private static final String PADDING = "PKCS1Padding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                1024, 2048
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = generateKeyPair(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            // RSA encrypts with PUBLIC key
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            // RSA decrypts with PRIVATE key
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    @Nested
    class RSA_ECB_OAEPWithSHA1AndMGF1Padding_Test {

        private static final String ALGORITHM = "RSA";

        private static final String MODE = "ECB";

        // OAEP with SHA-1 for message digest and MGF1 (also using SHA-1)
        private static final String PADDING = "OAEPWithSHA-1AndMGF1Padding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                1024, 2048
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = generateKeyPair(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    @Nested
    class RSA_ECB_OAEPWithSHA256AndMGF1Padding_Test {

        private static final String ALGORITHM = "RSA";

        private static final String MODE = "ECB";

        // OAEP with SHA-256 for message digest and MGF1 (using SHA-256)
        private static final String PADDING = "OAEPWithSHA-256AndMGF1Padding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {
                1024, 2048
        })
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var keyPair = generateKeyPair(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            final var baos = new ByteArrayOutputStream();
            service().update(cipher, (byte[] r) -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            printf(TRANSFORMATION, keysize, encrypted);
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }
}
