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

import javax.crypto.*;
import java.security.*;
import java.security.spec.*;

/**
 * A class providing test utilities for {@link javax.crypto.KEM}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/KEM.html">javax.crypto.KEM</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/KEM.html">javax.crypto.KEM</a>
 * (Java 26)
 */
public final class _Javax_Crypto_KEM_TestUtils {

    public record Kem(KEM kem, KeyPair keyPair) {

    }

    public static Kem DHKEM_EC_(final String curve)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec(curve));
        final var keyPair = generator.generateKeyPair();
        final var kem = KEM.getInstance("DHKEM");
        return new Kem(kem, keyPair);
    }

    @_LatestLTS
    @_LatestJDK
    public static Kem DHKEM_EC_secp256r1()
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return DHKEM_EC_("secp256r1");
    }

    @_LatestLTS
    @_LatestJDK
    public static Kem DHKEM_EC_secp384r1()
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return DHKEM_EC_("secp384r1");
    }

    @_LatestLTS
    @_LatestJDK
    public static Kem DHKEM_EC_secp521r1()
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return DHKEM_EC_("secp521r1");
    }

    public static Kem DHKEM_XDH_(final String curve)
            throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(curve);
        final var keyPair = generator.generateKeyPair();
        final var kem = KEM.getInstance("DHKEM");
        return new Kem(kem, keyPair);
    }

    @_LatestLTS
    @_LatestJDK
    public static Kem DHKEM_X25519()
            throws NoSuchAlgorithmException {
        return DHKEM_XDH_("X25519");
    }

    @_LatestLTS
    @_LatestJDK
    public static Kem DHKEM_X448()
            throws NoSuchAlgorithmException {
        return DHKEM_XDH_("X448");
    }

    public static Kem ML_KEM_(final String algorithm)
            throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        final var keyPair = generator.generateKeyPair();
        final var kem = KEM.getInstance(algorithm);
        return new Kem(kem, keyPair);
    }

    @_LatestLTS
    @_LatestJDK
    public static Kem ML_KEM_512()
            throws NoSuchAlgorithmException {
        return ML_KEM_("ML-KEM-512");
    }

    @_LatestLTS
    @_LatestJDK
    public static Kem ML_KEM_768()
            throws NoSuchAlgorithmException {
        return ML_KEM_("ML-KEM-768");
    }

    @_LatestLTS
    @_LatestJDK
    public static Kem ML_KEM_1024()
            throws NoSuchAlgorithmException {
        return ML_KEM_("ML-KEM-1024");
    }

    private _Javax_Crypto_KEM_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
