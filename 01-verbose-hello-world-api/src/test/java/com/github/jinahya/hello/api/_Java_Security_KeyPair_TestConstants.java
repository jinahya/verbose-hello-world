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

import java.util.*;

/**
 * Constants for testing {@link java.security.KeyPair} usages.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/KeyPairGenerator.html">java.security.KeyPairGenerator</a>
 * (Java® Platform, Standard Edition & Java Development Kit Version 25 API Specification)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#keypairgenerator-algorithms">{@code
 * KeyPairGenerator} Algorithms</a> (Java Security Standard Algorithm Names)
 */
@Slf4j
@SuppressWarnings({"java:S101"})
public final class _Java_Security_KeyPair_TestConstants {

    /**
     * A map of {@link java.security.KeyPairGenerator} algorithm names to their representative
     * parameters (key sizes or curve names).
     */
    public static final Map<String, List<Object>> KEY_PAIR_ALGORITHMS = Map.of(
            "DiffieHellman", List.<Object>of(1024, 2048, 3072, 4096),
            "DSA", List.<Object>of(1024, 2048),
            "EC", List.<Object>of("secp256r1", "secp384r1"),
            "RSA", List.<Object>of(1024, 2048, 3072, 4096),
            "RSASSA-PSS", List.<Object>of(2048, 3072, 4096),
            "X25519", List.of()
    );

    private _Java_Security_KeyPair_TestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
