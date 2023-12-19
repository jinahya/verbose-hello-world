package com.github.jinahya.hello.util.javax.crypto;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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
