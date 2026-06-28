package com.github.jinahya.hello.miscellaneous._java_security;

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
import lombok.extern.slf4j.*;

import java.util.*;

/**
 * Constants for testing {@link java.security.KeyPairGenerator}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/KeyPairGenerator.html">java.security.KeyPairGenerator</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/security/KeyPairGenerator.html">java.security.KeyPairGenerator</a>
 * (Java 26)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#keypairgenerator-algorithms">Java
 * Security Standard Algorithm Names / <code>KeyPairGenerator</code> Algorithms</a> (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#keypairgenerator-algorithms">Java
 * Security Standard Algorithm Names / <code>KeyPairGenerator</code> Algorithms</a> (Java 26)
 */
@Slf4j
public final class _Java_Security_KeyPairGenerator_TestConstants {

    public static final String KEY_PAIR_GENERATOR_SERVICE_TYPE = "KeyPairGenerator";

    /**
     * .
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#keypairgenerator-algorithms">Java
     * Security Standard Algorithm Names / <code>KeyPairGenerator</code> Algorithms</a> (Java 25)
     */
    @_LatestLTS
    public static final Map<String, List<Object>> ALGORITHMS_AND_PARAMETERS_LATEST_LTS =
            Map.ofEntries(
                    Map.entry("DiffieHellman", List.of(2048)),
                    Map.entry("DSA", List.of(2048)),
                    Map.entry("RSA", List.of(2048, 4096)),
                    Map.entry("RSASSA-PSS", List.of(2048, 4096)),
                    Map.entry("EC", List.of("secp256r1", "secp384r1", "secp521r1")),
                    Map.entry("X25519", List.of()),
                    Map.entry("X448", List.of()),
                    Map.entry("Ed25519", List.of()),
                    Map.entry("Ed448", List.of()),
                    Map.entry("ML-DSA-44", List.of()),
                    Map.entry("ML-DSA-65", List.of()),
                    Map.entry("ML-DSA-87", List.of()),
                    Map.entry("ML-KEM-512", List.of()),
                    Map.entry("ML-KEM-768", List.of()),
                    Map.entry("ML-KEM-1024", List.of())
            );

    /**
     * .
     *
     * @see <a
     * href="https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#keypairgenerator-algorithms">Java
     * Security Standard Algorithm Names / <code>KeyPairGenerator</code> Algorithms</a> (Java 26)
     */
    @_LatestJDK
    public static final Map<String, List<Object>> ALGORITHMS_AND_PARAMETERS_LATEST_JDK =
            Map.ofEntries(
                    Map.entry("DiffieHellman", List.of(2048)),
                    Map.entry("DSA", List.of(2048)),
                    Map.entry("RSA", List.of(2048, 4096)),
                    Map.entry("RSASSA-PSS", List.of(2048, 4096)),
                    Map.entry("EC", List.of("secp256r1", "secp384r1", "secp521r1")),
                    Map.entry("X25519", List.of()),
                    Map.entry("X448", List.of()),
                    Map.entry("Ed25519", List.of()),
                    Map.entry("Ed448", List.of()),
                    Map.entry("ML-DSA-44", List.of()),
                    Map.entry("ML-DSA-65", List.of()),
                    Map.entry("ML-DSA-87", List.of()),
                    Map.entry("ML-KEM-512", List.of()),
                    Map.entry("ML-KEM-768", List.of()),
                    Map.entry("ML-KEM-1024", List.of())
            );

    private _Java_Security_KeyPairGenerator_TestConstants() {
        throw new AssertionError("instantiation is not allowed");
    }
}
