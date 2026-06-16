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
 * A class providing test utilities for {@link javax.crypto.Mac}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/Mac.html">javax.crypto.Mac</a>
 * (Java 26)
 */
public final class _Javax_Crypto_Mac_TestUtils {

    // ---------------------------------------------------------------------------------------------
    public record Maccing(SecretKey secretKey, Mac mac, AlgorithmParameterSpec params) {

    }

    private static Maccing Hmac_(final String algorithm) throws NoSuchAlgorithmException {
        final var secretKey = _Javax_Crypto_SecretKey_TestUtils.generateSecretKey(algorithm, null);
        final var mac = Mac.getInstance(algorithm);
        return new Maccing(secretKey, mac, null);
    }

    @_LatestLTS
    @_LatestJDK
    public static Maccing HmacSHA1() throws NoSuchAlgorithmException {
        return Hmac_("HmacSHA1");
    }

    @_LatestLTS
    @_LatestJDK
    public static Maccing HmacSHA224() throws NoSuchAlgorithmException {
        return Hmac_("HmacSHA224");
    }

    @_LatestLTS
    @_LatestJDK
    public static Maccing HmacSHA256() throws NoSuchAlgorithmException {
        return Hmac_("HmacSHA256");
    }

    @_LatestLTS
    @_LatestJDK
    public static Maccing HmacSHA384() throws NoSuchAlgorithmException {
        return Hmac_("HmacSHA384");
    }

    @_LatestLTS
    @_LatestJDK
    public static Maccing HmacSHA512() throws NoSuchAlgorithmException {
        return Hmac_("HmacSHA512");
    }

    private _Javax_Crypto_Mac_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
