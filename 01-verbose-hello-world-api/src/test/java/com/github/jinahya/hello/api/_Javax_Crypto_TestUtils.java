package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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

import lombok.extern.slf4j.*;
import org.jspecify.annotations.*;

import javax.crypto.*;
import java.security.*;
import java.util.*;

/**
 * A class providing test utilities for {@link javax.crypto} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.hello._Javax_Crypto_KeyGenerator_Test
 */
@Slf4j
@SuppressWarnings({"java:S101"})
public final class _Javax_Crypto_TestUtils {

    /**
     * A map of {@link KeyGenerator} algorithm names to their representative key sizes (empty list
     * indicates the algorithm uses a fixed default size).
     *
     * @see com.github.jinahya.hello._Javax_Crypto_KeyGenerator_Test
     */
    public static final Map<String, List<Integer>> KEY_GENERATOR_ALGORITHMS_AND_KEYSIZES = Map.of(
            "AES", List.of(128, 256),
            "ChaCha20", List.of(), // 256
            "DESede", List.of(168),
            "HmacSHA1", List.of(), // 160
            "HmacSHA256", List.of() // 256
    );

    /**
     * Generates a secret key using the specified algorithm and (optionally) key size.
     *
     * @param algorithm the standard algorithm name.
     * @param keysize   the key size in bits, or {@code null} to use the provider's default.
     * @return a new secret key.
     * @throws NoSuchAlgorithmException if no provider supports the given {@code algorithm}.
     */
    public static SecretKey generateSecretKey(final String algorithm,
                                              final @Nullable Integer keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyGenerator.getInstance(algorithm);
        if (keysize != null) {
            generator.init(keysize);
        }
        return generator.generateKey();
    }

    private _Javax_Crypto_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
