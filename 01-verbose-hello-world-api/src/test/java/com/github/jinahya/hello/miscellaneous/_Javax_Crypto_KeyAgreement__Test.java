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

import com.github.jinahya.hello.api.*;
import lombok.*;
import org.bouncycastle.jce.provider.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import javax.crypto.*;
import javax.crypto.interfaces.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

import static com.github.jinahya.hello.miscellaneous._org_bouncycastle_jce_provider__TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class demonstrating <em>key agreement</em> (§3 of {@code _KEY.asciidoc}) via the JCA
 * {@link KeyAgreement} API: Alice and Bob each generate their own key pair under the same group or
 * curve, then independently combine their own private with the other's public to compute the same
 * shared secret. Each test asserts that both sides arrive at byte-identical secrets.
 *
 * <p>Provider policy: every algorithm is bound to {@link BouncyCastleProvider BouncyCastle}
 * explicitly so the assertions don't depend on which JDK distribution provides them. The
 * {@link LatestLTS @LatestLTS} / {@link LatestJDK @LatestJDK} markers on individual tests (and the
 * matching constants in {@link _Javax_Crypto_KeyAgreement__TestConstants}) merely document which
 * algorithms are JCA-mandatory in Java 25 (latest LTS) and Java 26 (latest JDK).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#keyagreement-algorithms">JDK
 * 25 JCA Standard Algorithm Names &mdash; KeyAgreement Algorithms</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc2631">RFC 2631 &mdash; Diffie-Hellman Key
 * Agreement Method</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7748">RFC 7748 &mdash; Elliptic Curves for
 * Security (X25519, X448)</a>
 */
@DisplayName("KeyAgreement")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_KeyAgreement__Test {

    static {
        _org_bouncycastle_jce_provider__TestUtils.addBouncyCastleProvider();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Prints a one-line summary of a key-agreement round-trip &mdash; algorithm/label, provider,
     * shared-secret byte length, and Base64-encoded first/last four characters &mdash; to
     * {@link System#out}.
     *
     * @param label        the algorithm (or algorithm/curve) label.
     * @param providerName the provider that resolved the {@link KeyAgreement} algorithm.
     * @param sharedSecret the raw shared-secret bytes both parties computed.
     */
    private static void printf(final String label, final String providerName,
                               final byte[] sharedSecret) {
        final var encoded = Base64.getEncoder().encodeToString(sharedSecret);
        System.out.printf("%-22s (%-6s) (%3d) %s...%s%n",
                          label, providerName,
                          sharedSecret.length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    /**
     * Runs a two-sided {@link KeyAgreement} between {@code alice} and {@code bob} using the given
     * standard algorithm name and provider, and asserts that both sides compute the same shared
     * secret byte-for-byte.
     *
     * @param algorithm    the JCE standard {@link KeyAgreement} algorithm name (e.g.,
     *                     {@code "DiffieHellman"}, {@code "ECDH"}, {@code "X25519"}).
     * @param providerName the JCE provider name to use for both Alice's and Bob's
     *                     {@link KeyAgreement} instances.
     * @param label        a human-readable label used in the printed summary.
     * @param alice        Alice's key pair (private + public).
     * @param bob          Bob's key pair (must share the same group / parameters as Alice's).
     */
    private static void agree(final String algorithm, final String providerName, final String label,
                              final KeyPair alice, final KeyPair bob) throws Exception {
        // ----------------------------------------------------------------------------------- alice
        final var aliceKa = KeyAgreement.getInstance(algorithm, providerName);
        aliceKa.init(alice.getPrivate());
        aliceKa.doPhase(bob.getPublic(), true);
        final var aliceSecret = aliceKa.generateSecret();
        // ------------------------------------------------------------------------------------- bob
        final var bobKa = KeyAgreement.getInstance(algorithm, providerName);
        bobKa.init(bob.getPrivate());
        bobKa.doPhase(alice.getPublic(), true);
        final var bobSecret = bobKa.generateSecret();
        // ----------------------------------------------------------------------------------- match
        printf(label, aliceKa.getProvider().getName(), aliceSecret);
        assertArrayEquals(aliceSecret, bobSecret);
    }

    /**
     * Verifies that classical finite-field Diffie-Hellman produces the same shared secret on both
     * sides when Alice and Bob share the same DH group (Bob reuses Alice's
     * {@link javax.crypto.spec.DHParameterSpec}).
     *
     * @param keySize Alice's DH modulus size in bits.
     */
    @LatestLTS
    @LatestJDK
    @DisplayName("should produce a matching shared secret via <DiffieHellman>")
    @ValueSource(ints = {
            // 2048 is the largest precomputed DH group BC ships; >=3072 falls back to fresh
            // parameter generation which is impractically slow (~minutes per call)
            2048
    })
    @ParameterizedTest
    void __DH(final int keySize) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final KeyPair aliceKp;
        {
            final var generator = KeyPairGenerator.getInstance("DH", BOUNCY_CASTLE_PROVIDER_NAME);
            generator.initialize(keySize);
            aliceKp = generator.generateKeyPair();
        }
        final KeyPair bobKp;
        {
            final var aliceParams = ((DHPublicKey) aliceKp.getPublic()).getParams();
            final var generator =
                    KeyPairGenerator.getInstance("DH", BOUNCY_CASTLE_PROVIDER_NAME);
            generator.initialize(aliceParams);
            bobKp = generator.generateKeyPair();
        }
        // ------------------------------------------------------------------------------- when/then
        agree("DiffieHellman", BOUNCY_CASTLE_PROVIDER_NAME,
              "DiffieHellman/" + keySize, aliceKp, bobKp);
    }

    /**
     * Verifies that elliptic-curve Diffie-Hellman over the given NIST P-curve produces the same
     * shared secret on both sides. {@code ECDH} is not JCA-mandatory, so the test binds the KeyPair
     * generation and the KeyAgreement to {@link BouncyCastleProvider} explicitly.
     *
     * @param stdName the standard curve name (e.g., {@code "secp256r1"}).
     */
    @DisplayName("should produce a matching shared secret via <ECDH> over the given <EC> curve")
    @ValueSource(strings = {
            "secp256r1",
            "secp384r1",
            "secp521r1"
    })
    @ParameterizedTest
    void __ECDH(final String stdName) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var generator = KeyPairGenerator.getInstance("EC",
                                                           BOUNCY_CASTLE_PROVIDER_NAME);
        generator.initialize(new ECGenParameterSpec(stdName));
        final var aliceKp = generator.generateKeyPair();
        final var bobKp = generator.generateKeyPair();
        // ------------------------------------------------------------------------------- when/then
        agree("ECDH", BOUNCY_CASTLE_PROVIDER_NAME, "ECDH/" + stdName, aliceKp, bobKp);
    }

    /**
     * Verifies that Montgomery-curve Diffie-Hellman ({@code X25519} or {@code X448}) produces the
     * same shared secret on both sides. Neither curve is JCA-mandatory, so the test binds the
     * KeyPair generation and the KeyAgreement to {@link BouncyCastleProvider} explicitly.
     *
     * @param stdName the standard curve name (e.g., {@code "X25519"}).
     */
    @DisplayName("should produce a matching shared secret via the given <XDH> curve")
    @ValueSource(strings = {
            "X25519",
            "X448"
    })
    @ParameterizedTest
    void __XDH(final String stdName) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var generator =
                KeyPairGenerator.getInstance(stdName, BOUNCY_CASTLE_PROVIDER_NAME);
        final var aliceKp = generator.generateKeyPair();
        final var bobKp = generator.generateKeyPair();
        // ------------------------------------------------------------------------------- when/then
        agree(stdName, BOUNCY_CASTLE_PROVIDER_NAME, stdName, aliceKp, bobKp);
    }
}
