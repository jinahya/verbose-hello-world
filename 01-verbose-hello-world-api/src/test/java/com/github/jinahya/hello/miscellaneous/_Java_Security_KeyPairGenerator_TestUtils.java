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
import lombok.extern.slf4j.*;

import java.security.*;
import java.security.spec.*;

/**
 * A class providing test utilities for {@link java.security.KeyPairGenerator}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/KeyPairGenerator.html">java.security.KeyPairGenerator</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/security/KeyPairGenerator.html">java.security.KeyPairGenerator</a>
 * (Java 26)
 */
@Slf4j
public final class _Java_Security_KeyPairGenerator_TestUtils {

    private static KeyPair generate(final String algorithm) throws NoSuchAlgorithmException {
        return KeyPairGenerator.getInstance(algorithm).generateKeyPair();
    }

    private static KeyPair generate(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(keysize);
        return generator.generateKeyPair();
    }

    private static KeyPair generate(final String algorithm, final AlgorithmParameterSpec spec)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        generator.initialize(spec);
        return generator.generateKeyPair();
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair DiffieHellman(final int keysize) throws NoSuchAlgorithmException {
        return generate("DiffieHellman", keysize);
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair DSA(final int keysize) throws NoSuchAlgorithmException {
        return generate("DSA", keysize);
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair RSA(final int keysize) throws NoSuchAlgorithmException {
        return generate("RSA", keysize);
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair RSASSA_PSS(final int keysize) throws NoSuchAlgorithmException {
        return generate("RSASSA-PSS", keysize);
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair EC(final String stdName)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return generate("EC", new ECGenParameterSpec(stdName));
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair X25519() throws NoSuchAlgorithmException {
        return generate("X25519");
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair X448() throws NoSuchAlgorithmException {
        return generate("X448");
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair Ed25519() throws NoSuchAlgorithmException {
        return generate("Ed25519");
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair Ed448() throws NoSuchAlgorithmException {
        return generate("Ed448");
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair ML_DSA_44() throws NoSuchAlgorithmException {
        return generate("ML-DSA-44");
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair ML_DSA_65() throws NoSuchAlgorithmException {
        return generate("ML-DSA-65");
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair ML_DSA_87() throws NoSuchAlgorithmException {
        return generate("ML-DSA-87");
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair ML_KEM_512() throws NoSuchAlgorithmException {
        return generate("ML-KEM-512");
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair ML_KEM_768() throws NoSuchAlgorithmException {
        return generate("ML-KEM-768");
    }

    @_LatestLTS
    @_LatestJDK
    public static KeyPair ML_KEM_1024() throws NoSuchAlgorithmException {
        return generate("ML-KEM-1024");
    }

    private _Java_Security_KeyPairGenerator_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
