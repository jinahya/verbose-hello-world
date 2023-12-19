package com.github.jinahya.hello.util.javax.crypto.spec;

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
