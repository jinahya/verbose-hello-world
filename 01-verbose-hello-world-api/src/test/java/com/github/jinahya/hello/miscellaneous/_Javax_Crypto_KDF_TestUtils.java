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

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.concurrent.*;

/**
 * A class providing test utilities for {@link javax.crypto.KDF}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/KDF.html">javax.crypto.KDF</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/KDF.html">javax.crypto.KDF</a>
 * (Java 26)
 */
public final class _Javax_Crypto_KDF_TestUtils {

    public record Hkdf(KDF kdf, HKDFParameterSpec spec) {

    }

    public static Hkdf HKDF_(final String suffix, final int outputLength)
            throws NoSuchAlgorithmException {
        final var kdf = KDF.getInstance("HKDF-" + suffix);
        final var ikm = new byte[32];
        ThreadLocalRandom.current().nextBytes(ikm);
        final var info = new byte[ThreadLocalRandom.current().nextInt(32 + 1)];
        ThreadLocalRandom.current().nextBytes(info);
        // RFC 5869 §3.1 — salt is optional; HKDF substitutes HashLen zero bytes when omitted
        final var builder = HKDFParameterSpec.ofExtract().addIKM(ikm);
        if (ThreadLocalRandom.current().nextBoolean()) {
            final var salt = new byte[16];
            ThreadLocalRandom.current().nextBytes(salt);
            builder.addSalt(salt);
        }
        final var spec = builder.thenExpand(info, outputLength);
        return new Hkdf(kdf, spec);
    }

    @LatestLTS
    @LatestJDK
    public static Hkdf HKDF_SHA256(final int outputLength)
            throws NoSuchAlgorithmException {
        return HKDF_("SHA256", outputLength);
    }

    @LatestLTS
    @LatestJDK
    public static Hkdf HKDF_SHA384(final int outputLength)
            throws NoSuchAlgorithmException {
        return HKDF_("SHA384", outputLength);
    }

    @LatestLTS
    @LatestJDK
    public static Hkdf HKDF_SHA512(final int outputLength)
            throws NoSuchAlgorithmException {
        return HKDF_("SHA512", outputLength);
    }

    private _Javax_Crypto_KDF_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
