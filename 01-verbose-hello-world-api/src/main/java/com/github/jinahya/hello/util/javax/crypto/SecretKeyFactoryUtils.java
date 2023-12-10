package com.github.jinahya.hello.util.javax.crypto;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

@Slf4j
public final class SecretKeyFactoryUtils {

    public static SecretKey generateSecret(final String algorithm, final KeySpec keySpec)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        final var instance = SecretKeyFactory.getInstance(algorithm);
        return instance.generateSecret(keySpec);
    }

    public static SecretKey generateSecret(final String algorithm, final byte[] key)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return generateSecret(algorithm, new SecretKeySpec(key, algorithm));
    }

    public static SecretKey generateSecret(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        final var key = new byte[keysize >> 3];
        SecureRandom.getInstanceStrong().nextBytes(key);
        return generateSecret(algorithm, key);
    }

    private SecretKeyFactoryUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
