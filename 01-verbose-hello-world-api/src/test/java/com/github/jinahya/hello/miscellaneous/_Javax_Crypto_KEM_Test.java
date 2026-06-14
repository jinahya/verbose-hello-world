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

import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import javax.crypto.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class demonstrating the {@link KEM} (Key Encapsulation Mechanism) API: a sender
 * encapsulates a freshly-generated shared {@link SecretKey} under the receiver's public key, the
 * receiver decapsulates the resulting ciphertext with its private key, and the two derived keys are
 * asserted byte-for-byte equal.
 *
 * <p>This is <em>asymmetric key exchange</em>: only the receiver's public key is needed to send,
 * and only the receiver's private key can recover the shared secret. Nested classes cover both the
 * classical Diffie-Hellman-based {@code DHKEM} (RFC 9180, EC and XDH key pairs) and the
 * quantum-resistant lattice-based {@code ML-KEM} (NIST FIPS 203).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://openjdk.org/jeps/452">JEP 452 &mdash; Key Encapsulation Mechanism API</a>
 * @see <a href="https://openjdk.org/jeps/496">JEP 496 &mdash; Quantum-Resistant
 * Module-Lattice-Based Key Encapsulation Mechanism</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9180">RFC 9180 &mdash; Hybrid Public Key
 * Encryption (DHKEM)</a>
 * @see <a href="https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.203.pdf">NIST FIPS 203 &mdash;
 * Module-Lattice-Based Key-Encapsulation Mechanism Standard</a>
 */
@DisplayName("javax.crypto.KEM")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_KEM_Test {

    /**
     * Prints a one-line summary of a KEM round-trip &mdash; algorithm/label, provider, shared-key
     * algorithm/length, Base64-encoded first/last four characters of the shared key, and the
     * ciphertext (encapsulation) byte length &mdash; to {@link System#out}.
     *
     * @param label              the algorithm (or algorithm/curve) label.
     * @param providerName       the provider that resolved the KEM algorithm.
     * @param sharedKey          the shared {@link SecretKey} produced by encapsulation.
     * @param encapsulationBytes the byte length of the ciphertext sent to the receiver.
     */
    private static void printf(final String label, final String providerName,
                               final SecretKey sharedKey, final int encapsulationBytes) {
        final var encoded = Base64.getEncoder().encodeToString(sharedKey.getEncoded());
        System.out.printf("%-22s (%-6s) keyAlg=%-8s (%4d) %s...%s  ct=%5d B%n",
                          label, providerName,
                          sharedKey.getAlgorithm(),
                          sharedKey.getEncoded().length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4),
                          encapsulationBytes);
    }

    /**
     * Runs a sender/receiver KEM round-trip against the given key pair and asserts that the
     * sender's encapsulated key matches the receiver's decapsulated key byte-for-byte.
     *
     * @param kemAlgorithm the KEM standard algorithm name (e.g., {@code "DHKEM"},
     *                     {@code "ML-KEM-512"}).
     * @param label        a human-readable label used in the printed summary (typically algorithm +
     *                     curve / parameter set).
     * @param keyPair      the receiver's asymmetric key pair.
     */
    private static void roundTrip(final String kemAlgorithm, final String label,
                                  final KeyPair keyPair) throws Exception {
        final var kem = KEM.getInstance(kemAlgorithm);
        // --------------------------------------------------------------------------------- sender
        final var encapsulator = kem.newEncapsulator(keyPair.getPublic());
        final var encapsulated = encapsulator.encapsulate();
        // ------------------------------------------------------------------------------- receiver
        final var decapsulator = kem.newDecapsulator(keyPair.getPrivate());
        final var sharedKey = decapsulator.decapsulate(encapsulated.encapsulation());
        // --------------------------------------------------------------------------------- match
        printf(label, encapsulator.providerName(), encapsulated.key(),
               encapsulated.encapsulation().length);
        assertArrayEquals(
                encapsulated.key().getEncoded(),
                sharedKey.getEncoded());
    }

    /**
     * A nested test class demonstrating the classical Diffie-Hellman-based KEM ({@code DHKEM}) from
     * RFC 9180, parameterized over the receiver's EC or XDH key pair: the sender encapsulates a
     * shared {@link SecretKey} using only the receiver's public key, and the receiver decapsulates
     * the same key using only its private key.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9180#section-4.1">RFC 9180, &sect;4.1
     * &mdash; DH-Based KEM (DHKEM)</a>
     */
    @DisplayName("DHKEM")
    @Nested
    class DHKEM_Test {

        // JCE standard KEM name (RFC 9180 §4.1)
        private static final String ALGORITHM = "DHKEM";

        /**
         * Verifies that {@code DHKEM} encapsulates and decapsulates the same shared key when the
         * receiver's key pair is an {@code EC} pair over the given NIST P-curve.
         *
         * @param stdName the standard curve name (e.g., {@code "secp256r1"}).
         */
        @DisplayName("EC")
        @ValueSource(strings = {
                "secp256r1",
                "secp384r1",
                "secp521r1"
        })
        @ParameterizedTest
        void __EC(final String stdName) throws Exception {
            // ----------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(new ECGenParameterSpec(stdName));
            final var keyPair = generator.generateKeyPair();
            // ------------------------------------------------------------------- when/then
            roundTrip(ALGORITHM, ALGORITHM + "/EC/" + stdName, keyPair);
        }

        /**
         * Verifies that {@code DHKEM} encapsulates and decapsulates the same shared key when the
         * receiver's key pair is an XDH pair over the given Montgomery curve.
         *
         * @param stdName the standard curve name (e.g., {@code "X25519"}).
         */
        @DisplayName("XDH")
        @ValueSource(strings = {
                "X25519",
                "X448"
        })
        @ParameterizedTest
        void __XDH(final String stdName) throws Exception {
            // ----------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance(stdName);
            final var keyPair = generator.generateKeyPair();
            // ------------------------------------------------------------------- when/then
            roundTrip(ALGORITHM, ALGORITHM + "/" + stdName, keyPair);
        }
    }

    /**
     * A nested test class demonstrating the quantum-resistant lattice-based KEM ({@code ML-KEM})
     * from NIST FIPS 203, parameterized over the three standardized parameter sets
     * ({@code ML-KEM-512}, {@code ML-KEM-768}, {@code ML-KEM-1024}). All three derive a fixed
     * 32-byte shared secret but differ in public-key, ciphertext, and security-category size.
     *
     * @see <a href="https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.203.pdf">NIST FIPS 203 &mdash;
     * Module-Lattice-Based Key-Encapsulation Mechanism Standard</a>
     */
    @DisplayName("ML-KEM")
    @Nested
    class ML_KEM_Test {

        /**
         * Verifies that {@code ML-KEM} encapsulates and decapsulates the same shared key for the
         * given parameter set.
         *
         * @param algorithm the standard algorithm name (e.g., {@code "ML-KEM-768"}).
         */
        @DisplayName("parameter set")
        @ValueSource(strings = {
                "ML-KEM-512",
                "ML-KEM-768",
                "ML-KEM-1024"
        })
        @ParameterizedTest
        void __(final String algorithm) throws Exception {
            // ----------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance(algorithm);
            final var keyPair = generator.generateKeyPair();
            // ------------------------------------------------------------------- when/then
            roundTrip(algorithm, algorithm, keyPair);
        }
    }
}
