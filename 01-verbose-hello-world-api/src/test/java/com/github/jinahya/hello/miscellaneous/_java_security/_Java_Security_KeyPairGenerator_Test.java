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
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.security.*;

import static com.github.jinahya.hello.miscellaneous._java_security._Java_Security_KeyPairGenerator_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for testing {@link KeyPairGenerator} key-pair generation with the JCA-mandatory
 * algorithms.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/KeyPairGenerator.html">java.security.KeyPairGenerator</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/security/KeyPairGenerator.html">java.security.KeyPairGenerator</a>
 * (Java 26)
 */
@DisplayName("java.security.KeyPairGenerator")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class _Java_Security_KeyPairGenerator_Test {

    private static void verify(final KeyPair keyPair) {
        assertNotNull(keyPair);
        assertNotNull(keyPair.getPrivate());
        assertNotNull(keyPair.getPublic());
        _Java_Security_KeyPair_TestUtils.printKeyPair(keyPair);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code DiffieHellman} generates a key pair at the given {@code keysize}.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("DiffieHellman")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __DiffieHellman(final int keysize) throws Exception {
        verify(DiffieHellman(keysize));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code DSA} generates a key pair at the given {@code keysize}.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("DSA")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __DSA(final int keysize) throws Exception {
        verify(DSA(keysize));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code RSA} generates a key pair at the given {@code keysize}.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("RSA")
    @ValueSource(ints = {2048, 4096})
    @ParameterizedTest
    void __RSA(final int keysize) throws Exception {
        verify(RSA(keysize));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code RSASSA-PSS} generates a key pair at the given {@code keysize}.
     *
     * @param keysize the key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("RSASSA-PSS")
    @ValueSource(ints = {2048, 4096})
    @ParameterizedTest
    void __RSASSA_PSS(final int keysize) throws Exception {
        verify(RSASSA_PSS(keysize));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code EC} generates a key pair over the given {@code stdName} curve.
     *
     * @param stdName the standard curve name (e.g., {@code "secp256r1"}).
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("EC")
    @ValueSource(strings = {"secp256r1", "secp384r1", "secp521r1"})
    @ParameterizedTest
    void __EC(final String stdName) throws Exception {
        verify(EC(stdName));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code X25519} generates a key pair.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("X25519")
    @Test
    void __X25519() throws Exception {
        verify(X25519());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code X448} generates a key pair.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("X448")
    @Test
    void __X448() throws Exception {
        verify(X448());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code Ed25519} generates a key pair.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("Ed25519")
    @Test
    void __Ed25519() throws Exception {
        verify(Ed25519());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code Ed448} generates a key pair.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("Ed448")
    @Test
    void __Ed448() throws Exception {
        verify(Ed448());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code ML-DSA-44} generates a key pair.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("ML-DSA-44")
    @Test
    void __ML_DSA_44() throws Exception {
        verify(ML_DSA_44());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code ML-DSA-65} generates a key pair.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("ML-DSA-65")
    @Test
    void __ML_DSA_65() throws Exception {
        verify(ML_DSA_65());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code ML-DSA-87} generates a key pair.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("ML-DSA-87")
    @Test
    void __ML_DSA_87() throws Exception {
        verify(ML_DSA_87());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code ML-KEM-512} generates a key pair.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("ML-KEM-512")
    @Test
    void __ML_KEM_512() throws Exception {
        verify(ML_KEM_512());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code ML-KEM-768} generates a key pair.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("ML-KEM-768")
    @Test
    void __ML_KEM_768() throws Exception {
        verify(ML_KEM_768());
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code ML-KEM-1024} generates a key pair.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("ML-KEM-1024")
    @Test
    void __ML_KEM_1024() throws Exception {
        verify(ML_KEM_1024());
    }
}
