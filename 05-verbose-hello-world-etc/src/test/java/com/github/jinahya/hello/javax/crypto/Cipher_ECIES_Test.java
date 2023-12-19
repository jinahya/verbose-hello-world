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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.NamedParameterSpec;
import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
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
 * Algorithm Paddings</a>
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#parameterspec-names"><code>ParameterSpec</code>
 * Names</a>
 */
// https://stackoverflow.com/a/66428589/330457
@DisplayName("javax.crypto.Cipher")
@Slf4j
class Cipher_ECIES_Test {

    static {
//        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private static KeyPair generateKeyPair(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(keysize);
        return generator.generateKeyPair();
    }

//    private static IntStream rsaKeysizes() {
//        return IntStream.of(
//                1024,
//                2048
//        );
//    }
//

    // https://docs.oracle.com/en/java/javase/21/docs/specs/security/standard-names.html#parameterspec-names
    private static Stream<String> ECGenParameterSpecStdNameStream() {
        return Stream.of(
                "sect163k1", "sect163r1", "sect163r2", "sect193r1", "sect193r2", "sect233k1",
                "sect233r1", "sect239k1", "sect283k1", "sect283r1", "sect409k1", "sect409r1",
                "sect571k1", "sect571r1", "secp160k1", "secp160r1", "secp160r2", "secp192k1",
                "secp192r1", "secp224k1", "secp224r1", "secp256k1", "secp256r1", "secp384r1",
                "secp521r1",
                "brainpoolP256r1", "brainpoolP384r1", "brainpoolP512r1"
        );
    }

    private static Stream<NamedParameterSpec> namedParameterSpecStream() {
        return Stream.of(
                NamedParameterSpec.ED25519,
                NamedParameterSpec.ED448,
                NamedParameterSpec.X25519,
                NamedParameterSpec.X448
        );
    }

    private static Stream<String> namedParameterSpecNameStream() {
        return namedParameterSpecStream().map(NamedParameterSpec::getName);
    }

    private static final String ALGORITHM = "ECIES";

    static KeyPair generateKeyPair(final String algorithm, final String stdName)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
        keyPairGenerator.initialize(new ECGenParameterSpec(stdName));
        return keyPairGenerator.generateKeyPair();
    }

    // https://stackoverflow.com/a/66428589/330457
    private static SecretKey secretKey(final PrivateKey privateKey, final PublicKey publicKey,
                                       final String keyAgreementAlgorithm,
                                       final String keyAlgorithm)
            throws NoSuchAlgorithmException, InvalidKeyException {
        final var keyAgreement = KeyAgreement.getInstance(keyAgreementAlgorithm);
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        final var secret = keyAgreement.generateSecret();
        final var key = MessageDigest.getInstance("MD5").digest(secret);
        return new SecretKeySpec(key, keyAlgorithm);
    }

    @MethodSource({"ECGenParameterSpecStdNameStream"})
    @ParameterizedTest
    void __(final String stdName)
            throws Exception {
        final String keyPairAlgorithm = "ECIES";
        final String keyAgreementAlgorithm = "ECDH";
        final String keyAlgorithm = "AES";
        final var keyPair1 = generateKeyPair(keyPairAlgorithm, stdName);
        final var keyPair2 = generateKeyPair(keyPairAlgorithm, stdName);
        final var secretkey1 = secretKey(keyPair1.getPrivate(), keyPair2.getPublic(),
                                         keyAgreementAlgorithm, keyAlgorithm);
        final var secretkey2 = secretKey(keyPair2.getPrivate(), keyPair1.getPublic(),
                                         keyAgreementAlgorithm, keyAlgorithm);
        log.debug("secretKey1.encoded: {}", HexFormat.of().formatHex(secretkey1.getEncoded()));
        log.debug("secretKey2.encoded: {}", HexFormat.of().formatHex(secretkey2.getEncoded()));
        // -----------------------------------------------------------------------------------------
        final String mode = "CBC";
        final String padding = "PKCS5Padding";
        final String transformation = keyAlgorithm + '/' + mode + '/' + padding;
        final var cipher = Cipher.getInstance(transformation);
        final var blockSize = cipher.getBlockSize();
        final AlgorithmParameterSpec params;
        {
            final var iv = new byte[cipher.getBlockSize()];
            ThreadLocalRandom.current().nextBytes(iv);
            params = new IvParameterSpec(iv);
        }
        // -----------------------------------------------------------------------------------------
        final byte[] plainBytes = new byte[ThreadLocalRandom.current().nextInt(8192)];
        ThreadLocalRandom.current().nextBytes(plainBytes);
        final byte[] encryptedBytes;
        {
            cipher.init(Cipher.ENCRYPT_MODE, secretkey1, params);
            encryptedBytes = cipher.doFinal(plainBytes);
            assertThat(encryptedBytes.length % blockSize).isZero();
        }
        final byte[] decryptedBytes;
        {
            cipher.init(Cipher.DECRYPT_MODE, secretkey2, params);
            decryptedBytes = cipher.doFinal(encryptedBytes);
        }
        assertThat(decryptedBytes).isEqualTo(plainBytes);
    }
}
