package com.github.jinahya.hello.javax.crypto;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A class for testing classes defined in {@link java.security} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#cipher-algorithms"><code>Cipher</code>
 * Algorithms</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#cipher-algorithm-modes"><code>Cipher</code>
 * Algorithm modes</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#cipher-algorithm-paddings"><code>Cihper</code>
 * Algorithm Paddings</code></a>
 */
@DisplayName("javax.crypto.Cipher")
@Slf4j
class Cipher_RSA_Test {

    static {
//        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
//        Security.insertProviderAt(new gnu.crypto.jce.GnuCrypto(), 1);
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private static KeyPair generateKeyPair(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(keysize);
        return generator.generateKeyPair();
    }

    private static IntStream rsaKeysizes() {
        return IntStream.of(
                1024,
                2048
        );
    }

    @DisplayName("RSA/ECB/PKCS1Padding")
    @Nested
    class RSA_ECB_PKCS5Padding_Test {

        private static IntStream keysizes() {
            return rsaKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keySize)
                throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            // https://mbed-tls.readthedocs.io/en/latest/kb/cryptography/rsa-encryption-maximum-data-size/#:~:text=RSA%20is%20only%20able%20to,5%20padding).
            final var plainBytes = new byte[(keySize >> 3) - 11];
            ThreadLocalRandom.current().nextBytes(plainBytes);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "RSA";
            final var mode = "ECB";
            final var padding = "PKCS1Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            try {
                assert cipher.getBlockSize() == 0;
            } catch (final IllegalStateException ise) {
                // BC
            }
            // ----------------------------------------------------------------------------- keyPair
            final var keyPair = generateKeyPair(algorithm, keySize);
            {
                // ----------------------------------------------------- encrypt with the public key
                final byte[] encryptedBytes;
                {
                    cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
                    encryptedBytes = cipher.doFinal(plainBytes);
                }
                // ---------------------------------------------------- decrypt with the private key
                final byte[] decryptedBytes;
                {
                    cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                    decryptedBytes = cipher.doFinal(encryptedBytes);
                }
                // -------------------------------------------------------------------------- verify
                {
                    assertThat(decryptedBytes)
                            .as("decryptedBytes")
                            .isEqualTo(plainBytes);
                }
            }
            {
                // ----------------------------------------------------- encrypt with the public key
                final byte[] encryptedBytes;
                {
                    cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
                    encryptedBytes = cipher.doFinal(plainBytes);
                }
                // ---------------------------------------------------- decrypt with the private key
                final byte[] decryptedBytes;
                {
                    cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());
                    decryptedBytes = cipher.doFinal(encryptedBytes);
                }
                // -------------------------------------------------------------------------- verify
                {
                    assertThat(decryptedBytes)
                            .as("decryptedBytes")
                            .isEqualTo(plainBytes);
                }
            }
        }
    }

    @DisplayName("RSA/ECB/OAEPWithSHA-1AndMGF1Padding")
    @Nested
    class RSA_ECB_OAEAPWithSAH_1AndMGF1Padding_Test {

        private static IntStream keysizes() {
            return rsaKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize)
                throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            // https://mbed-tls.readthedocs.io/en/latest/kb/cryptography/rsa-encryption-maximum-data-size/#:~:text=RSA%20is%20only%20able%20to,5%20padding).
            final var plainBytes = new byte[(keysize >> 3) - 42];
            ThreadLocalRandom.current().nextBytes(plainBytes);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "RSA";
            final var mode = "ECB";
            final var padding = "OAEPWithSHA-1AndMGF1Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            try {
                assert cipher.getBlockSize() == 0;
            } catch (final IllegalStateException ise) {
                // BC
            }
            // ----------------------------------------------------------------------------- keyPair
            final var keyPair = generateKeyPair(algorithm, keysize);
            {
                // ----------------------------------------------------- encrypt with the public key
                final byte[] encryptedBytes;
                {
                    cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
                    encryptedBytes = cipher.doFinal(plainBytes);
                }
                // ---------------------------------------------------- decrypt with the private key
                final byte[] decryptedBytes;
                {
                    cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                    decryptedBytes = cipher.doFinal(encryptedBytes);
                }
                // -------------------------------------------------------------------------- verify
                {
                    assertThat(decryptedBytes)
                            .as("decryptedBytes")
                            .isEqualTo(plainBytes);
                }
            }
            if (false) { // OAEP cannot be used to sign or verify signatures
                // ----------------------------------------------------- encrypt with the public key
                final byte[] encryptedBytes;
                {
                    cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
                    encryptedBytes = cipher.doFinal(plainBytes);
                }
                // ---------------------------------------------------- decrypt with the private key
                final byte[] decryptedBytes;
                {
                    cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());
                    decryptedBytes = cipher.doFinal(encryptedBytes);
                }
                // -------------------------------------------------------------------------- verify
                {
                    assertThat(decryptedBytes)
                            .as("decryptedBytes")
                            .isEqualTo(plainBytes);
                }
            }
        }
    }

    @DisplayName("RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    @Nested
    class RSA_ECB_OAEPWithSAH_256AndMGF1Padding_Test {

        private static IntStream keysizes() {
            return rsaKeysizes();
        }

        @MethodSource({"keysizes"})
        @ParameterizedTest(name = "[{index}] keysize: {0}")
        void __(final int keysize)
                throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
                       IllegalBlockSizeException, BadPaddingException {
            // ------------------------------------------------------------------------------- files
            // https://mbed-tls.readthedocs.io/en/latest/kb/cryptography/rsa-encryption-maximum-data-size/#:~:text=RSA%20is%20only%20able%20to,5%20padding).
            final var plainBytes = new byte[(keysize >> 3) - 66];
            ThreadLocalRandom.current().nextBytes(plainBytes);
            // ------------------------------------------------------------------------------ cipher
            final var algorithm = "RSA";
            final var mode = "ECB";
            final var padding = "OAEPWithSHA-256AndMGF1Padding";
            final var transformation = algorithm + '/' + mode + '/' + padding;
            final var cipher = Cipher.getInstance(transformation);
            try {
                assert cipher.getBlockSize() == 0;
            } catch (final IllegalStateException ise) {
                // BC
            }
            // ----------------------------------------------------------------------------- keyPair
            final var keyPair = generateKeyPair(algorithm, keysize);
            {
                // ----------------------------------------------------- encrypt with the public key
                final byte[] encryptedBytes;
                {
                    cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
                    encryptedBytes = cipher.doFinal(plainBytes);
                }
                // ---------------------------------------------------- decrypt with the private key
                final byte[] decryptedBytes;
                {
                    cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
                    decryptedBytes = cipher.doFinal(encryptedBytes);
                }
                // -------------------------------------------------------------------------- verify
                {
                    assertThat(decryptedBytes)
                            .as("decryptedBytes")
                            .isEqualTo(plainBytes);
                }
            }
            if (false) { // OAEP cannot be used to sign or verify signatures
                // ---------------------------------------------------- encrypt with the private key
                final byte[] encryptedBytes;
                {
                    cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPrivate());
                    encryptedBytes = cipher.doFinal(plainBytes);
                }
                // ----------------------------------------------------- decrypt with the public key
                final byte[] decryptedBytes;
                {
                    cipher.init(Cipher.DECRYPT_MODE, keyPair.getPublic());
                    decryptedBytes = cipher.doFinal(encryptedBytes);
                }
                // -------------------------------------------------------------------------- verify
                {
                    assertThat(decryptedBytes)
                            .as("decryptedBytes")
                            .isEqualTo(plainBytes);
                }
            }
        }
    }
}
