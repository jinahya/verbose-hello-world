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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

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
 */
@DisplayName("javax.crypto.Cipher")
@Slf4j
class CipherTest {

    static {
//        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    static Stream<String> algorithms() {
        return Stream.of(
                "AES",
                "AESWrap",
                "AESWrapPad",
                "ARCFOUR",
                "ChaCha20",
                "ChaCha20-Poly1305",
                "DES",
                "DESede",
                "DESedeWrap",
                "ECIES",
//                "PBEWith<digest>And<encryption>",
//                "PBEWith<prf>And<encryption>",
                "RC2",
                "RC4",
                "RC5",
                "RSA"
        );
    }

    static Stream<String> modes() {
        return Stream.of(
                "NONE",
                "CBC",
                "CCM",
                "CFB", "CFBx",
                "CTR",
                "CTS",
                "ECB",
                "GCM",
                "KW",
                "KWP",
                "OFB", "OFBx",
                "PCBC"
        );
    }

    static Stream<String> paddings() {
        return Stream.of(
                "NoPadding",
                "ISO10126Padding",
                "OAEPPadding",
//                "OAEPWith<digest>And<mgf>Padding",
                "PKCS1Padding",
                "PKCS5Padding",
                "SSL3Padding"
        );
    }

    private static Stream<Arguments> requiredToBeSupportedTransformationsAnsKeysizss() {
        return Stream.of(
                arguments("AES/CBC/NoPadding", List.of(128)),
                arguments("AES/CBC/PKCS5Padding", List.of(128)),
                arguments("AES/ECB/NoPadding", List.of(128)),
                arguments("AES/ECB/PKCS5Padding", List.of(128)),
                arguments("AES/GCM/NoPadding", List.of(128)),
                arguments("DESede/CBC/NoPadding", List.of(168)),
                arguments("DESede/CBC/PKCS5Padding", List.of(168)),
                arguments("DESede/ECB/NoPadding", List.of(168)),
                arguments("DESede/ECB/PKCS5Padding", List.of(168)),
                arguments("RSA/ECB/PKCS1Padding", List.of(1024, 2048)),
                arguments("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", List.of(1024)),
                arguments("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", List.of(2048)),
                arguments("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", List.of(1024, 2048))
        );
    }

    private static Stream<String> transformationsRequiredToBeSupported() {
        return requiredToBeSupportedTransformationsAnsKeysizss()
                .map(a -> (String) a.get()[0]);
    }

    @DisplayName("getInstance(transformation-required-to-be-supported")
    @MethodSource({"transformationsRequiredToBeSupported"})
    @ParameterizedTest
    void getInstance_NotThrow_requiredToBeSupportedTransformations__(final String transformation) {
        try {
            final var instance = Cipher.getInstance(transformation);
            log.debug("supported; algorithm: {}, provider: {}", algorithms(),
                      instance.getProvider());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.warn("unable to get a cipher instance; transformation: {}", transformation, e);
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    static Key generateKey(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyGenerator.getInstance(algorithm);
        generator.init(keysize);
        return generator.generateKey();
    }

    private static KeyPair generateKeyPair(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(keysize);
        return generator.generateKeyPair();
    }
}
