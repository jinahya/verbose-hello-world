package com.github.jinahya.hello.api._javax_crypto;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * .
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
 */
class HelloWorld_Update_Cipher_Test extends HelloWorldTest {

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

    private static final int AES_BLOCK_SIZE = 16;

    private static final int DESEDE_BLOCK_SIZE = 8;

    private static Key generateSecretKey(final String algorithm, final int keysize)
            throws Exception {
        final var generator = KeyGenerator.getInstance(algorithm);
        generator.init(keysize);
        return generator.generateKey();
    }

    private static KeyPair generateKeyPair(final String algorithm, final int keysize)
            throws Exception {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(keysize);
        return generator.generateKeyPair();
    }

    // -----------------------------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        HelloWorldTestUtils.stub_set_array_will_set_hello_world_bytes(service());
    }

    // -----------------------------------------------------------------------------------------------------------------

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
            final var service = service();
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
            service.update(cipher, r -> {
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
                baos.writeBytes(cipher.doFinal());
            }
            final var encrypted = baos.toByteArray();
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            // decrypted is 16 bytes: 12 bytes of "hello, world" + 4 bytes of zero padding.
            // NoPadding doesn't strip padding on decryption, so we compare only the first 12 bytes.
            Assertions.assertEquals(AES_BLOCK_SIZE, decrypted.length);
            Assertions.assertArrayEquals(
                    HelloWorldTestUtils.helloWorldBytes(),
                    Arrays.copyOf(decrypted, HelloWorld.BYTES)
            );
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
            final var service = service();
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
            service.update(cipher, r -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.helloWorldBytes(), decrypted);
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
            final var service = service();
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            service.update(cipher, r -> {
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
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            // decrypted is 16 bytes: 12 bytes of "hello, world" + 4 bytes of zero padding.
            // NoPadding doesn't strip padding on decryption, so we compare only the first 12 bytes.
            Assertions.assertEquals(AES_BLOCK_SIZE, decrypted.length);
            Assertions.assertArrayEquals(
                    HelloWorldTestUtils.helloWorldBytes(),
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
            final var service = service();
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            service.update(cipher, r -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.helloWorldBytes(), decrypted);
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
            final var service = service();
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            final AlgorithmParameterSpec params;
            {
                final var iv = new byte[GCM_IV_LENGTH];
                ThreadLocalRandom.current().nextBytes(iv);
                params = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            }
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key, params);
            final var baos = new ByteArrayOutputStream();
            service.update(cipher, r -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            // GCM is a stream cipher mode - no padding needed, decrypted equals original plaintext
            Assertions.assertArrayEquals(HelloWorldTestUtils.helloWorldBytes(), decrypted);
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
            final var service = service();
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
            service.update(cipher, r -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            // ChaCha20-Poly1305 is a stream cipher (AEAD) - no padding, decrypted equals original
            Assertions.assertArrayEquals(HelloWorldTestUtils.helloWorldBytes(), decrypted);
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
            final var service = service();
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
            service.update(cipher, r -> {
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
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            // decrypted is 16 bytes: 12 bytes of "hello, world" + 4 bytes of zero padding.
            Assertions.assertEquals(16, decrypted.length);
            Assertions.assertArrayEquals(
                    HelloWorldTestUtils.helloWorldBytes(),
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
            final var service = service();
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
            service.update(cipher, r -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.helloWorldBytes(), decrypted);
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
            final var service = service();
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            service.update(cipher, r -> {
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
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            // decrypted is 16 bytes: 12 bytes of "hello, world" + 4 bytes of zero padding.
            Assertions.assertEquals(16, decrypted.length);
            Assertions.assertArrayEquals(
                    HelloWorldTestUtils.helloWorldBytes(),
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
            final var service = service();
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            service.update(cipher, r -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.helloWorldBytes(), decrypted);
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
            final var service = service();
            final var keyPair = generateKeyPair(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            // RSA encrypts with PUBLIC key
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            final var baos = new ByteArrayOutputStream();
            service.update(cipher, r -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            // ----------------------------------------------------------------------------- decrypt
            // RSA decrypts with PRIVATE key
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.helloWorldBytes(), decrypted);
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
            final var service = service();
            final var keyPair = generateKeyPair(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            final var baos = new ByteArrayOutputStream();
            service.update(cipher, r -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.helloWorldBytes(), decrypted);
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
            final var service = service();
            final var keyPair = generateKeyPair(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            final var baos = new ByteArrayOutputStream();
            service.update(cipher, r -> {
                if (r != null) {
                    baos.writeBytes(r);
                }
            });
            baos.writeBytes(cipher.doFinal());
            final var encrypted = baos.toByteArray();
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            final var decrypted = cipher.doFinal(encrypted);
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(HelloWorldTestUtils.helloWorldBytes(), decrypted);
        }
    }
}
