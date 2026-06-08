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

import java.security.*;
import java.security.spec.*;
import java.util.*;

@Slf4j
@SuppressWarnings({"java:S101"})
public final class _Java_Security_TestUtils {

    public static final List<String> MESSAGE_DIGEST_ALGORITHMS = List.of(
            "SHA-1",
            "SHA-256",
            "SHA-384"
    );

    public static final List<String> SIGNATURE_ALGORITHMS = List.of(
            "RSASSA-PSS",
            "SHA1withDSA",
            "SHA256withDSA",
            "SHA256withECDSA",
            "SHA384withECDSA",
            "SHA1withRSA",
            "SHA256withRSA",
            "SHA384withRSA"
    );

    // DiffieHellman (1024, 2048, 3072, 4096)
    //DSA (1024, 2048)
    //EC (secp256r1, secp384r1)
    //RSA (1024, 2048, 3072, 4096)
    //RSASSA-PSS (2048, 3072, 4096)
    //X25519
//    public static final List<String> KEY_PAIR_ALGORITHMS_AND_KEYSIZES = List.of()

    public static KeyPair generateKeyPair(final String algorithm, final int keysize)
            throws NoSuchAlgorithmException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        final var random = SecureRandom.getInstanceStrong();
        generator.initialize(keysize, random);
        return generator.generateKeyPair();
    }

    public static KeyPair generateKeyPair(final String algorithm, final AlgorithmParameterSpec spec)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final var generator = KeyPairGenerator.getInstance(algorithm);
        final var random = SecureRandom.getInstanceStrong();
        generator.initialize(spec, random);
        return generator.generateKeyPair();
    }

    private _Java_Security_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
