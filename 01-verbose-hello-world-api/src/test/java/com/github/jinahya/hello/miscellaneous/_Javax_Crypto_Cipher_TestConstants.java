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

import com.github.jinahya.hello.api.annotations.*;

import java.util.*;

/**
 * Constants for testing {@link javax.crypto.Cipher}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
 * (Java 26)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#cipher-algorithms">
 * Security Standard Algorithm Names / <code>Cipher</code> Algorithms</a> (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#cipher-algorithms">
 * Security Standard Algorithm Names / <code>Cipher</code> Algorithms</a> (Java 26)
 */

public final class _Javax_Crypto_Cipher_TestConstants {

    /**
     * .
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
     * (Java 25)
     */
    @_LatestLTS
    public static final Map<String, List<Object>> REQUIRED_TRANSFORMATIONS_AND_KEYSIZES_LATEST_LTS =
            Map.ofEntries(
                    Map.entry("AES/CBC/NoPadding", List.of(128)),
                    Map.entry("AES/CBC/PKCS5Padding", List.of(128)),
                    Map.entry("AES/ECB/NoPadding", List.of(128)),
                    Map.entry("AES/ECB/PKCS5Padding", List.of(128)),
                    Map.entry("AES/GCM/NoPadding", List.of(128, 256)),
                    Map.entry("ChaCha20-Poly1305", List.of()),
                    Map.entry("DESede/CBC/NoPadding", List.of(168)),
                    Map.entry("DESede/CBC/PKCS5Padding", List.of(168)),
                    Map.entry("DESede/ECB/NoPadding", List.of(168)),
                    Map.entry("DESede/ECB/PKCS5Padding", List.of(168)),
                    Map.entry("RSA/ECB/PKCS1Padding", List.of(1024, 2048)),
                    Map.entry("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", List.of(1024, 2048)),
                    Map.entry("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", List.of(1024, 2048))
            );

    // https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#cipher-algorithms
    public static final List<String> ALGORITHM_NAMES_LATEST_LTS = List.of(
            "AES",
            "AES_128",
            "AES_192",
            "AES_256",
            "AESWrap",
            "AESWrap_128",
            "AESWrap_192",
            "AESWrap_256",
            "AESWrapPad",
            "AESWrapPad_128",
            "AESWrapPad_192",
            "AESWrapPad_256",
            "ARCFOUR",
            "Blowfish",
            "ChaCha20",
            "ChaCha20-Poly1305",
            "DES",
            "DESede",
            "DESedeWrap",
            "ECIES",
            "PBEWithMD5AndDES",
            "PBEWithHmacSHA256AndAES_128",
            "RC2",
            "RC4",
            "RC5",
            "RSA"
    );

    /**
     * .
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
     * (Java 26)
     */
    @_LatestJDK
    public static final Map<String, List<Object>> REQUIRED_TRANSFORMATIONS_AND_KEYSIZES_LATEST_JDK =
            Map.ofEntries(
                    Map.entry("AES/CBC/NoPadding", List.of(128)),
                    Map.entry("AES/CBC/PKCS5Padding", List.of(128)),
                    Map.entry("AES/ECB/NoPadding", List.of(128)),
                    Map.entry("AES/ECB/PKCS5Padding", List.of(128)),
                    Map.entry("AES/GCM/NoPadding", List.of(128, 256)),
                    Map.entry("ChaCha20-Poly1305", List.of()),
                    Map.entry("PBEWithHmacSHA256AndAES_128", List.of()),
                    Map.entry("PBEWithHmacSHA256AndAES_256", List.of()),
                    Map.entry("RSA/ECB/OAEPWithSHA-1AndMGF1Padding", List.of(1024, 2048)),
                    Map.entry("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", List.of(1024, 2048))
            );

    // https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#cipher-algorithms
    public static final List<String> ALGORITHM_NAMES_LATEST_JDK = List.of(
            "AES",
            "AES_128",
            "AES_192",
            "AES_256",
            "AESWrap",
            "AESWrap_128",
            "AESWrap_192",
            "AESWrap_256",
            "AESWrapPad",
            "AESWrapPad_128",
            "AESWrapPad_192",
            "AESWrapPad_256",
            "ARCFOUR",
            "Blowfish",
            "ChaCha20",
            "ChaCha20-Poly1305",
            "DES",
            "DESede",
            "DESedeWrap",
            "ECIES",
            "HPKE",
            "PBEWithMD5AndDES",
            "PBEWithHmacSHA256AndAES_128",
            "RC2",
            "RC4",
            "RC5",
            "RSA"
    );

    private _Javax_Crypto_Cipher_TestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
