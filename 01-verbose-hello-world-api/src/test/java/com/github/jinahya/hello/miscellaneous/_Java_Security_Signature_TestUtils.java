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

import com.github.jinahya.hello.api.*;

import java.security.*;
import java.security.spec.*;

/**
 * A class providing test utilities for {@link java.security.Signature}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
 * (Java 26)
 */
public final class _Java_Security_Signature_TestUtils {

    // ---------------------------------------------------------------------------------------------
    public record Signing(KeyPair keyPair, Signature signature, AlgorithmParameterSpec params) {

    }

    private static Signing withDSA_(final int keysize, final String algorithm)
            throws NoSuchAlgorithmException {
        final var keyPair = _Java_Security_KeyPair_TestUtils.generateKeyPair("DSA", keysize);
        final var signature = Signature.getInstance(algorithm);
        return new Signing(keyPair, signature, null);
    }

    @LatestLTS
    @LatestJDK
    public static Signing SHA1withDSA(final int keysize) throws NoSuchAlgorithmException {
        return withDSA_(keysize, "SHA1withDSA");
    }

    @LatestLTS
    @LatestJDK
    public static Signing SHA256withDSA(final int keysize) throws NoSuchAlgorithmException {
        return withDSA_(keysize, "SHA256withDSA");
    }

    private static Signing withECDSA_(final String curve, final String algorithm)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var keyPair = _Java_Security_KeyPair_TestUtils.generateKeyPair(
                "EC", new ECGenParameterSpec(curve));
        final var signature = Signature.getInstance(algorithm);
        return new Signing(keyPair, signature, null);
    }

    @LatestLTS
    @LatestJDK
    public static Signing SHA256withECDSA()
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return withECDSA_("secp256r1", "SHA256withECDSA");
    }

    @LatestLTS
    @LatestJDK
    public static Signing SHA384withECDSA()
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return withECDSA_("secp384r1", "SHA384withECDSA");
    }

    private static Signing withRSA_(final int keysize, final String algorithm)
            throws NoSuchAlgorithmException {
        final var keyPair = _Java_Security_KeyPair_TestUtils.generateKeyPair("RSA", keysize);
        final var signature = Signature.getInstance(algorithm);
        return new Signing(keyPair, signature, null);
    }

    @LatestLTS
    @LatestJDK
    public static Signing SHA1withRSA(final int keysize) throws NoSuchAlgorithmException {
        return withRSA_(keysize, "SHA1withRSA");
    }

    @LatestLTS
    @LatestJDK
    public static Signing SHA256withRSA(final int keysize) throws NoSuchAlgorithmException {
        return withRSA_(keysize, "SHA256withRSA");
    }

    @LatestLTS
    @LatestJDK
    public static Signing SHA384withRSA(final int keysize) throws NoSuchAlgorithmException {
        return withRSA_(keysize, "SHA384withRSA");
    }

    private static Signing RSASSA_PSS_(final int keysize, final String hashAlgorithm)
            throws NoSuchAlgorithmException {
        final var keyPair = _Java_Security_KeyPair_TestUtils.generateKeyPair("RSA", keysize);
        final var signature = Signature.getInstance("RSASSA-PSS");
        final var saltLength = MessageDigest.getInstance(hashAlgorithm).getDigestLength();
        final var params = new PSSParameterSpec(
                hashAlgorithm, "MGF1", new MGF1ParameterSpec(hashAlgorithm), saltLength, 1);
        return new Signing(keyPair, signature, params);
    }

    @LatestLTS
    @LatestJDK
    public static Signing RSASSA_PSS_SHA_256(final int keysize) throws NoSuchAlgorithmException {
        return RSASSA_PSS_(keysize, "SHA-256");
    }

    @LatestLTS
    @LatestJDK
    public static Signing RSASSA_PSS_SHA_384(final int keysize) throws NoSuchAlgorithmException {
        return RSASSA_PSS_(keysize, "SHA-384");
    }

    private _Java_Security_Signature_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
