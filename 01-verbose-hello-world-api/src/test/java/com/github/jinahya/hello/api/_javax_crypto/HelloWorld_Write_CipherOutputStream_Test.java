package com.github.jinahya.hello.api._javax_crypto;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#write(CipherOutputStream) write(stream)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/CipherOutputStream.html#close()">javax.crypto.CipherOutputStream#close()</a>
 */
@Disabled
@DisplayName("write(CipherOutputStream)")
@Slf4j
class HelloWorld_Write_CipherOutputStream_Test
        extends HelloWorldTest {

    static final int AES_BLOCK_SIZE = HelloWorld_Update_Cipher_Test.AES_BLOCK_SIZE;

    static final int DESEDE_BLOCK_SIZE = HelloWorld_Update_Cipher_Test.DESEDE_BLOCK_SIZE;

    static Key generateSecretKey(final String algorithm, final int keysize)
            throws Exception {
        return HelloWorld_Update_Cipher_Test.generateSecretKey(algorithm, keysize);
    }

    private static KeyPair generateKeyPair(final String algorithm, final int keysize)
            throws Exception {
        return HelloWorld_Update_Cipher_Test.generateKeyPair(algorithm, keysize);
    }

    /**
     * Verifies that the {@link HelloWorld#write(CipherOutputStream) write(stream)} method throws a
     * {@link NullPointerException} when the {@code stream} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <stream> argument is <null>""")
    @Test
    void _ThrowNullPointerException_StreamIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final CipherOutputStream stream = null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(stream)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#write(CipherOutputStream) write(stream)} method invokes
     * {@link HelloWorld#write(FilterOutputStream) write((FilterOutputStream) stream)}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <write((FilterOutputStream) stream)>")
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .write(ArgumentMatchers.<FilterOutputStream>notNull());
        final var stream = Mockito.mock(CipherOutputStream.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(stream);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).write((FilterOutputStream) stream);
        Assertions.assertSame(stream, result);
    }

    // =============================================================================================

    @DisplayName("AES/CBC/NoPadding")
    @Nested
    class AES_CBC_NoPadding_Test {

        private static final String ALGORITHM = "AES";

        private static final String MODE = "CBC";

        private static final String PADDING = "NoPadding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {128})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
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
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
                // NoPadding: pad to block size
                cos.write(new byte[AES_BLOCK_SIZE - HelloWorld.BYTES]);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(AES_BLOCK_SIZE, decrypted.length);
            Assertions.assertArrayEquals(
                    hello_world_byte_array(),
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

        @ValueSource(ints = {128})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
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
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("AES/ECB/NoPadding")
    @Nested
    class AES_ECB_NoPadding_Test {

        private static final String ALGORITHM = "AES";

        private static final String MODE = "ECB";

        private static final String PADDING = "NoPadding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {128})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
                // NoPadding: pad to block size
                cos.write(new byte[AES_BLOCK_SIZE - HelloWorld.BYTES]);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(AES_BLOCK_SIZE, decrypted.length);
            Assertions.assertArrayEquals(
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

        @ValueSource(ints = {128})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("AES/GCM/NoPadding")
    @Nested
    class AES_GCM_NoPadding_Test {

        private static final String ALGORITHM = "AES";

        private static final String MODE = "GCM";

        private static final String PADDING = "NoPadding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        private static final int GCM_IV_LENGTH = 12;

        private static final int GCM_TAG_LENGTH = 128;

        @ValueSource(ints = {128, 256})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
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
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("ChaCha20-Poly1305")
    @Nested
    class ChaCha20_Poly1305_Test {

        private static final String ALGORITHM = "ChaCha20";

        private static final String TRANSFORMATION = "ChaCha20-Poly1305";

        private static final int NONCE_LENGTH = 12;

        @ValueSource(ints = {256})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
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
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("DESede/CBC/NoPadding")
    @Nested
    class DESede_CBC_NoPadding_Test {

        private static final String ALGORITHM = "DESede";

        private static final String MODE = "CBC";

        private static final String PADDING = "NoPadding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {168})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
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
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
                // NoPadding: pad to block size (12 bytes → 16 bytes)
                final var paddingNeeded = DESEDE_BLOCK_SIZE - (HelloWorld.BYTES
                                                               % DESEDE_BLOCK_SIZE);
                cos.write(new byte[paddingNeeded]);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(16, decrypted.length);
            Assertions.assertArrayEquals(
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

        @ValueSource(ints = {168})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
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
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key, params);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("DESede/ECB/NoPadding")
    @Nested
    class DESede_ECB_NoPadding_Test {

        private static final String ALGORITHM = "DESede";

        private static final String MODE = "ECB";

        private static final String PADDING = "NoPadding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {168})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
                // NoPadding: pad to block size (12 bytes → 16 bytes)
                final var paddingNeeded = DESEDE_BLOCK_SIZE - (HelloWorld.BYTES
                                                               % DESEDE_BLOCK_SIZE);
                cos.write(new byte[paddingNeeded]);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertEquals(16, decrypted.length);
            Assertions.assertArrayEquals(
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

        @ValueSource(ints = {168})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
            final var key = generateSecretKey(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, key);
            final var baos = new ByteArrayOutputStream();
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, key);
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(hello_world_byte_array(), decrypted);
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

        @ValueSource(ints = {1024, 2048})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
            final var keyPair = generateKeyPair(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            final var baos = new ByteArrayOutputStream();
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    @Nested
    class RSA_ECB_OAEPWithSHA1AndMGF1Padding_Test {

        private static final String ALGORITHM = "RSA";

        private static final String MODE = "ECB";

        private static final String PADDING = "OAEPWithSHA-1AndMGF1Padding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {1024, 2048})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
            final var keyPair = generateKeyPair(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            final var baos = new ByteArrayOutputStream();
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }

    @DisplayName("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    @Nested
    class RSA_ECB_OAEPWithSHA256AndMGF1Padding_Test {

        private static final String ALGORITHM = "RSA";

        private static final String MODE = "ECB";

        private static final String PADDING = "OAEPWithSHA-256AndMGF1Padding";

        private static final String TRANSFORMATION = ALGORITHM + '/' + MODE + '/' + PADDING;

        @ValueSource(ints = {1024, 2048})
        @ParameterizedTest
        void __(final int keysize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_will_set_actual_hello_world_bytes();
            final var keyPair = generateKeyPair(ALGORITHM, keysize);
            final var cipher = Cipher.getInstance(TRANSFORMATION);
            // ----------------------------------------------------------------------------- encrypt
            cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            final var baos = new ByteArrayOutputStream();
            try (var cos = new CipherOutputStream(baos, cipher)) {
                service.write(cos);
            }
            final var encrypted = baos.toByteArray();
            log.debug("encrypted: {}", HexFormat.of().formatHex(encrypted));
            // ----------------------------------------------------------------------------- decrypt
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            final var decrypted = cipher.doFinal(encrypted);
            log.debug("decrypted: {}", HexFormat.of().formatHex(decrypted));
            // -------------------------------------------------------------------------------- then
            Assertions.assertArrayEquals(hello_world_byte_array(), decrypted);
        }
    }
}
