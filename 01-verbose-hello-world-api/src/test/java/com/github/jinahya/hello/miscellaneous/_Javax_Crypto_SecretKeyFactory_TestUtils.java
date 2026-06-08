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
import java.security.spec.*;
import java.util.concurrent.*;

/**
 * A class providing test utilities for {@link javax.crypto.SecretKeyFactory}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/SecretKeyFactory.html">javax.crypto.SecretKeyFactory</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/SecretKeyFactory.html">javax.crypto.SecretKeyFactory</a>
 * (Java 26)
 */
public final class _Javax_Crypto_SecretKeyFactory_TestUtils {

    public record Raw(SecretKeyFactory factory, KeySpec spec) {

    }

    public record Pbe(SecretKeyFactory factory, KeySpec spec) {

    }

    private static char[] randomPassword() {
        final var password = new char[ThreadLocalRandom.current().nextInt(8, 32)];
        for (var i = 0; i < password.length; i++) {
            password[i] = (char) ThreadLocalRandom.current().nextInt(32, 127);
        }
        return password;
    }

    @LatestLTS
    public static Raw DES()
            throws NoSuchAlgorithmException, InvalidKeyException {
        final var factory = SecretKeyFactory.getInstance("DES");
        final var keyBytes = new byte[8];
        ThreadLocalRandom.current().nextBytes(keyBytes);
        final var spec = new DESKeySpec(keyBytes);
        return new Raw(factory, spec);
    }

    @LatestLTS
    public static Raw DESede()
            throws NoSuchAlgorithmException, InvalidKeyException {
        final var factory = SecretKeyFactory.getInstance("DESede");
        final var keyBytes = new byte[24];
        ThreadLocalRandom.current().nextBytes(keyBytes);
        final var spec = new DESedeKeySpec(keyBytes);
        return new Raw(factory, spec);
    }

    @LatestLTS
    public static Pbe PBEWithMD5AndDES()
            throws NoSuchAlgorithmException {
        final var factory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        final var spec = new PBEKeySpec(randomPassword());
        return new Pbe(factory, spec);
    }

    @LatestJDK
    public static Pbe PBEWithHmacSHA256AndAES_128()
            throws NoSuchAlgorithmException {
        final var factory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
        final var spec = new PBEKeySpec(randomPassword());
        return new Pbe(factory, spec);
    }

    @LatestJDK
    public static Pbe PBEWithHmacSHA256AndAES_256()
            throws NoSuchAlgorithmException {
        final var factory = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_256");
        final var spec = new PBEKeySpec(randomPassword());
        return new Pbe(factory, spec);
    }

    public static Pbe PBKDF2WithHmacSHA_(final String suffix, final int keylen)
            throws NoSuchAlgorithmException {
        final var factory = SecretKeyFactory.getInstance("PBKDF2WithHmac" + suffix);
        final var salt = new byte[16];
        ThreadLocalRandom.current().nextBytes(salt);
        final var spec = new PBEKeySpec(randomPassword(), salt, 1000, keylen);
        return new Pbe(factory, spec);
    }

    @LatestLTS
    @LatestJDK
    public static Pbe PBKDF2WithHmacSHA1(final int keylen)
            throws NoSuchAlgorithmException {
        return PBKDF2WithHmacSHA_("SHA1", keylen);
    }

    @LatestLTS
    @LatestJDK
    public static Pbe PBKDF2WithHmacSHA224(final int keylen)
            throws NoSuchAlgorithmException {
        return PBKDF2WithHmacSHA_("SHA224", keylen);
    }

    @LatestLTS
    @LatestJDK
    public static Pbe PBKDF2WithHmacSHA256(final int keylen)
            throws NoSuchAlgorithmException {
        return PBKDF2WithHmacSHA_("SHA256", keylen);
    }

    @LatestLTS
    @LatestJDK
    public static Pbe PBKDF2WithHmacSHA384(final int keylen)
            throws NoSuchAlgorithmException {
        return PBKDF2WithHmacSHA_("SHA384", keylen);
    }

    @LatestLTS
    @LatestJDK
    public static Pbe PBKDF2WithHmacSHA512(final int keylen)
            throws NoSuchAlgorithmException {
        return PBKDF2WithHmacSHA_("SHA512", keylen);
    }

    private _Javax_Crypto_SecretKeyFactory_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
