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
import org.jspecify.annotations.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.lang.invoke.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A class providing test utilities for {@link javax.crypto.Cipher}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/javax/crypto/Cipher.html">javax.crypto.Cipher</a>
 * (Java 26)
 */
public final class _Javax_Crypto_Cipher_TestUtils {

    private static Class<?> declaredInitParameterType(final Object arg) {
        return switch (arg) {
            case Key k -> Key.class;
            case Certificate c -> Certificate.class;
            case AlgorithmParameterSpec s -> AlgorithmParameterSpec.class;
            case AlgorithmParameters p -> AlgorithmParameters.class;
            case SecureRandom r -> SecureRandom.class;
            default -> throw new IllegalArgumentException(
                    "unsupported argument type: " + arg.getClass());
        };
    }

    public static <T extends Cipher> T init(final T cipher, final Object... arguments) {
        final var paramTypes = new Class<?>[arguments.length];
        paramTypes[0] = int.class;
        for (var i = 1; i < arguments.length; i++) {
            paramTypes[i] = declaredInitParameterType(arguments[i]);
        }
        final MethodHandle handle;
        try {
            handle = MethodHandles.publicLookup().findVirtual(
                    Cipher.class, "init",
                    MethodType.methodType(void.class, paramTypes));
        } catch (final NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "no Cipher.init(...) overload for " + Arrays.toString(paramTypes), e);
        }
        try {
            handle.bindTo(cipher).invokeWithArguments(arguments);
        } catch (final GeneralSecurityException e) {
            throw new RuntimeException("failed to init cipher", e);
        } catch (final RuntimeException | Error e) {
            throw e;
        } catch (final Throwable e) {
            throw new RuntimeException(e);
        }
        return cipher;
    }

    public record Symmetric(SecretKey secretKey, Cipher cipher, AlgorithmParameterSpec params,
                            byte @Nullable [] aad) {

    }

    public record Asymmetric(KeyPair keyPair, Cipher cipher, AlgorithmParameterSpec params) {

    }

    public static Symmetric AES_CBC_(final int keysize, final String padding)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        final var key = _Javax_Crypto_SecretKey_TestUtils.generateSecretKey("AES", keysize);
        final var cipher = Cipher.getInstance("AES/CBC/" + padding);
        final var iv = new byte[cipher.getBlockSize()];
        ThreadLocalRandom.current().nextBytes(iv);
        final var params = new IvParameterSpec(iv);
        return new Symmetric(key, cipher, params, null);
    }

    @_LatestLTS
    @_LatestJDK
    public static Symmetric AES_CBC_NoPadding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return AES_CBC_(keysize, "NoPadding");
    }

    @_LatestLTS
    @_LatestJDK
    public static Symmetric AES_CBC_PKCS5Padding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return AES_CBC_(keysize, "PKCS5Padding");
    }

    public static Symmetric AES_ECB_(final int keysize, final String padding)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        final var key = _Javax_Crypto_SecretKey_TestUtils.generateSecretKey("AES", keysize);
        final var cipher = Cipher.getInstance("AES/ECB/" + padding);
        return new Symmetric(key, cipher, null, null);
    }

    @_LatestLTS
    @_LatestJDK
    public static Symmetric AES_ECB_NoPadding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return AES_ECB_(keysize, "NoPadding");
    }

    @_LatestLTS
    @_LatestJDK
    public static Symmetric AES_ECB_PKCS5Padding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return AES_ECB_(keysize, "PKCS5Padding");
    }

    @_LatestLTS
    @_LatestJDK
    public static Symmetric AES_GCM_NoPadding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        final var key = _Javax_Crypto_SecretKey_TestUtils.generateSecretKey("AES", keysize);
        final var cipher = Cipher.getInstance("AES/GCM/NoPadding");
        final var iv = new byte[12];
        ThreadLocalRandom.current().nextBytes(iv);
        final var params = new GCMParameterSpec(128, iv);
        final byte[] aad;
        if (ThreadLocalRandom.current().nextBoolean()) {
            aad = new byte[ThreadLocalRandom.current().nextInt(32 + 1)];
            ThreadLocalRandom.current().nextBytes(aad);
        } else {
            aad = null;
        }
        return new Symmetric(key, cipher, params, aad);
    }

    @_LatestLTS
    @_LatestJDK
    public static Symmetric ChaCha20_Poly1305()
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        final var key = _Javax_Crypto_SecretKey_TestUtils.generateSecretKey("ChaCha20", null);
        final var cipher = Cipher.getInstance("ChaCha20-Poly1305");
        final var nonce = new byte[12];
        ThreadLocalRandom.current().nextBytes(nonce);
        final var params = new IvParameterSpec(nonce);
        final byte[] aad;
        if (ThreadLocalRandom.current().nextBoolean()) {
            aad = new byte[ThreadLocalRandom.current().nextInt(32 + 1)];
            ThreadLocalRandom.current().nextBytes(aad);
        } else {
            aad = null;
        }
        return new Symmetric(key, cipher, params, aad);
    }

    public static Symmetric DESede_CBC_(final int keysize, final String padding)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        final var key = _Javax_Crypto_SecretKey_TestUtils.generateSecretKey("DESede", keysize);
        final var cipher = Cipher.getInstance("DESede/CBC/" + padding);
        final var iv = new byte[cipher.getBlockSize()];
        ThreadLocalRandom.current().nextBytes(iv);
        final var params = new IvParameterSpec(iv);
        return new Symmetric(key, cipher, params, null);
    }

    @_LatestLTS
    public static Symmetric DESede_CBC_NoPadding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return DESede_CBC_(keysize, "NoPadding");
    }

    @_LatestLTS
    public static Symmetric DESede_CBC_PKCS5Padding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return DESede_CBC_(keysize, "PKCS5Padding");
    }

    public static Symmetric DESede_ECB_(final int keysize, final String padding)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        final var key = _Javax_Crypto_SecretKey_TestUtils.generateSecretKey("DESede", keysize);
        final var cipher = Cipher.getInstance("DESede/ECB/" + padding);
        return new Symmetric(key, cipher, null, null);
    }

    @_LatestLTS
    public static Symmetric DESede_ECB_NoPadding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return DESede_ECB_(keysize, "NoPadding");
    }

    @_LatestLTS
    public static Symmetric DESede_ECB_PKCS5Padding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return DESede_ECB_(keysize, "PKCS5Padding");
    }

    public static Symmetric PBEWithHmacSHA256AndAES_(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException,
                   InvalidKeySpecException {
        final var algorithm = "PBEWithHmacSHA256AndAES_" + keysize;
        final var password = new char[ThreadLocalRandom.current().nextInt(8, 32)];
        for (var i = 0; i < password.length; i++) {
            password[i] = (char) ThreadLocalRandom.current().nextInt(32, 127);
        }
        final var key = SecretKeyFactory.getInstance(algorithm)
                .generateSecret(new PBEKeySpec(password));
        final var cipher = Cipher.getInstance(algorithm);
        final var salt = new byte[16];
        ThreadLocalRandom.current().nextBytes(salt);
        final var iv = new byte[cipher.getBlockSize()];
        ThreadLocalRandom.current().nextBytes(iv);
        final var params = new PBEParameterSpec(salt, 1000, new IvParameterSpec(iv));
        return new Symmetric(key, cipher, params, null);
    }

    @_LatestJDK
    public static Symmetric PBEWithHmacSHA256AndAES_128()
            throws NoSuchAlgorithmException, NoSuchPaddingException,
                   InvalidKeySpecException {
        return PBEWithHmacSHA256AndAES_(128);
    }

    @_LatestJDK
    public static Symmetric PBEWithHmacSHA256AndAES_256()
            throws NoSuchAlgorithmException, NoSuchPaddingException,
                   InvalidKeySpecException {
        return PBEWithHmacSHA256AndAES_(256);
    }

    public static Asymmetric RSA_ECB_(final int keysize, final String padding)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        final var keyPair = _Java_Security_KeyPair_TestUtils.generateKeyPair("RSA", keysize);
        final var cipher = Cipher.getInstance("RSA/ECB/" + padding);
        return new Asymmetric(keyPair, cipher, null);
    }

    @_LatestLTS
    public static Asymmetric RSA_ECB_PKCS1Padding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return RSA_ECB_(keysize, "PKCS1Padding");
    }

    @_LatestLTS
    @_LatestJDK
    public static Asymmetric RSA_ECB_OAEPWithSHA_1AndMGF1Padding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return RSA_ECB_(keysize, "OAEPWithSHA-1AndMGF1Padding");
    }

    @_LatestLTS
    @_LatestJDK
    public static Asymmetric RSA_ECB_OAEPWithSHA_256AndMGF1Padding(final int keysize)
            throws NoSuchAlgorithmException, NoSuchPaddingException {
        return RSA_ECB_(keysize, "OAEPWithSHA-256AndMGF1Padding");
    }

    private _Javax_Crypto_Cipher_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
