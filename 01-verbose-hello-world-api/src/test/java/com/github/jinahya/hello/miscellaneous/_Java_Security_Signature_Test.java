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
import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.security.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static com.github.jinahya.hello.miscellaneous._Java_Security_Security_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Java_Security_Signature_TestConstants.*;
import static com.github.jinahya.hello.miscellaneous._Java_Security_Signature_TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for testing {@link java.security.Signature}.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
 * (Java 25)
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/api/java.base/java/security/Signature.html">java.security.Signature</a>
 * (Java 26)
 */
@DisplayName("java.security.Signature")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Java_Security_Signature_Test {

    /**
     * Verifies that every {@link Signature} algorithm registered with each provider can be
     * printed.
     */
    @DisplayName("algorithms")
    @Test
    void algorithms__() {
        securityProviders().forEach(p -> {
            final var algorithms = p.getServices().stream()
                    .filter(s -> s.getType().equals(SIGNATURE_SERVICE_TYPE))
                    .map(Provider.Service::getAlgorithm).collect(
                            Collectors.toCollection(LinkedHashSet::new));
            if (algorithms.isEmpty()) {
                return;
            }
            System.out.printf("%-20s%n%s%n%n", p.getName(), algorithms);
        });
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code SHA1withDSA} signs and verifies random data.
     *
     * @param keysize the DSA key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA1withDSA")
    @ValueSource(ints = {1024})
    @ParameterizedTest
    void __SHA1withDSA(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var signing = SHA1withDSA(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var signed = signature.sign();
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code SHA256withDSA} signs and verifies random data.
     *
     * @param keysize the DSA key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA256withDSA")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __SHA256withDSA(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var signing = SHA256withDSA(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var signed = signature.sign();
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code SHA256withECDSA} signs and verifies random data.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA256withECDSA")
    @Test
    void __SHA256withECDSA() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var signing = SHA256withECDSA();
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var signed = signature.sign();
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code SHA384withECDSA} signs and verifies random data.
     *
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA384withECDSA")
    @Test
    void __SHA384withECDSA() throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var signing = SHA384withECDSA();
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var signed = signature.sign();
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code SHA1withRSA} signs and verifies random data.
     *
     * @param keysize the RSA key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA1withRSA")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __SHA1withRSA(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var signing = SHA1withRSA(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var signed = signature.sign();
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code SHA256withRSA} signs and verifies random data.
     *
     * @param keysize the RSA key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA256withRSA")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __SHA256withRSA(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var signing = SHA256withRSA(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var signed = signature.sign();
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code SHA384withRSA} signs and verifies random data.
     *
     * @param keysize the RSA key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("SHA384withRSA")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __SHA384withRSA(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var signing = SHA384withRSA(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var signed = signature.sign();
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code RSASSA-PSS} with SHA-256 signs and verifies random data.
     *
     * @param keysize the RSA key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("RSASSA-PSS with SHA-256")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __RSASSA_PSS_SHA_256(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var signing = RSASSA_PSS_SHA_256(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var signed = signature.sign();
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that {@code RSASSA-PSS} with SHA-384 signs and verifies random data.
     *
     * @param keysize the RSA key size, in bits.
     * @throws Exception if any error occurs.
     */
    @_LatestLTS
    @_LatestJDK
    @DisplayName("RSASSA-PSS with SHA-384")
    @ValueSource(ints = {2048})
    @ParameterizedTest
    void __RSASSA_PSS_SHA_384(final int keysize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var data = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(data);
        final var signing = RSASSA_PSS_SHA_384(keysize);
        final var signature = signing.signature();
        // ------------------------------------------------------------------------------------ sign
        signature.initSign(signing.keyPair().getPrivate());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var signed = signature.sign();
        // ---------------------------------------------------------------------------------- verify
        signature.initVerify(signing.keyPair().getPublic());
        if (signing.params() != null) {
            signature.setParameter(signing.params());
        }
        signature.update(data);
        final var verified = signature.verify(signed);
        // ------------------------------------------------------------------------------------ then
        assertTrue(verified);
    }
}
