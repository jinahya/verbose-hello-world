package com.github.jinahya.hello.util.javax.crypto.spec;

import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Set;

public final class SecretKeySpecUtils {

    private static final Set<Integer> AES_KEYSIZES = Set.of(
            128,
            192,
            256
    );

    public static SecretKeySpec newAesKey(final int keysize, final SecureRandom random) {
        if (!AES_KEYSIZES.contains(keysize)) {
            throw new IllegalArgumentException("invalid keysize for AES: " + keysize);
        }
        Objects.requireNonNull(random, "random is null");
        final var key = new byte[keysize >> 3];
        random.nextBytes(key);
        return new SecretKeySpec(key, "AES");
    }

    public static SecretKeySpec newAesKey(final int keysize)
            throws NoSuchAlgorithmException {
        return newAesKey(keysize, SecureRandom.getInstanceStrong());
    }

    private SecretKeySpecUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
