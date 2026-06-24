package com.github.jinahya.hello.miscellaneous._javax_crypto;

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
import com.github.jinahya.hello.miscellaneous._org_bouncycastle_jce_provider.*;
import lombok.*;
import org.bouncycastle.crypto.agreement.*;
import org.bouncycastle.jcajce.spec.*;
import org.bouncycastle.jce.provider.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;

import static com.github.jinahya.hello.miscellaneous._org_bouncycastle_jce_provider._org_bouncycastle_jce_provider__TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class demonstrating <em>key agreement</em> (§3 of {@code _KEY.asciidoc}) via the JCA
 * {@link KeyAgreement} API: Alice and Bob each generate their own key pair under the same group or
 * curve, then independently combine their own private with the other's public to compute the same
 * shared secret. Each test asserts that both sides arrive at byte-identical secrets.
 *
 * <p>Provider policy: every algorithm is bound to {@link BouncyCastleProvider BouncyCastle}
 * explicitly so the assertions don't depend on which JDK distribution provides them. The
 * {@link _LatestLTS @LatestLTS} / {@link _LatestJDK @LatestJDK} markers on individual tests (and
 * the matching constants in {@link _Javax_Crypto_KeyAgreement_TestConstants}) merely document which
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
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc3526">RFC 3526 &mdash; More Modular
 * Exponential (MODP) Diffie-Hellman groups for IKE</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7919">RFC 7919 &mdash; Negotiated
 * Finite Field Diffie-Hellman Ephemeral Parameters for TLS</a>
 */
@_HideNameFromPublishing
@DisplayName("javax.crypto.KeyAgreement")
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
     * Returns a fresh DH key pair in the group described by the given {@code dhParams}, generated
     * by a private {@link KeyPairGenerator} instance bound to {@link BouncyCastleProvider}.
     *
     * @param dhParams the shared DH group parameters both parties have pre-agreed on.
     * @return a fresh DH key pair (private + public) within the given group.
     */
    private static KeyPair generateDH(final DHParameterSpec dhParams)
            throws NoSuchAlgorithmException, NoSuchProviderException,
                   InvalidAlgorithmParameterException {
        final var generator = KeyPairGenerator.getInstance("DH", BOUNCY_CASTLE_PROVIDER_NAME);
        generator.initialize(dhParams);
        return generator.generateKeyPair();
    }

    /**
     * Finite-field Diffie-Hellman (FFDH) — modulus group {@code (ℤ/pℤ)*}. Methods enumerate
     * different ways of agreeing on the shared DH group between the two parties.
     *
     * <p>Modern preference (top to bottom):
     * <ol>
     *   <li>{@link #__DH __DH} &mdash; RFC 7919 FFDHE named groups. Current TLS 1.3 standard;
     *       pre-vetted safe primes, no parameter negotiation overhead. <strong>Recommended
     *       default.</strong></li>
     *   <li>{@link #__DH1 __DH1} &mdash; RFC 3526 MODP named groups. Older IKE-era named groups,
     *       still acceptable for non-TLS protocols.</li>
     *   <li>{@link #__DH2 __DH2} &mdash; Alice picks a random group, Bob inherits its
     *       {@link DHParameterSpec}. The TLS 1.2 DHE pattern; RFC 7919 superseded this for TLS.
     *       Kept here only to illustrate the original handshake shape.</li>
     *   <li>{@link #__DH3 __DH3} &mdash; freshly generated DH group via
     *       {@link AlgorithmParameterGenerator}. Theoretically appealing (no reuse) but
     *       practically slow and offers no security benefit over the pre-vetted named groups.</li>
     *   <li>{@link #__DH4 __DH4} &mdash; RFC 2409 Oakley groups at 768 / 1024 bits.
     *       <strong>Do not use:</strong> below modern minimums; kept only as a historical curio.</li>
     * </ol>
     */
    @Nested
    @DisplayName("DiffieHellman")
    class DH_Test {

        /**
         * Verifies that finite-field Diffie-Hellman produces the same shared secret on both sides
         * when Alice and Bob each generate their own key pair in a pre-agreed RFC 7919 FFDHE group
         * (so no runtime parameter transfer is needed).
         *
         * @param groupName the BouncyCastle {@code DHStandardGroups} constant name (e.g.,
         *                  {@code "rfc7919_ffdhe2048"}).
         */
        @_LatestLTS
        @_LatestJDK
        @DocumentedPreference(1)
        @GovernedPreference(1)
        @DisplayName("RFC 7919 FFDHE group")
        @ValueSource(strings = {
                // 2048 only — 3072+ key generation gets slow even with precomputed groups
                "rfc7919_ffdhe2048",
//                "rfc7919_ffdhe3072",
//                "rfc7919_ffdhe4096",
//                "rfc7919_ffdhe6144",
//                "rfc7919_ffdhe8192"
        })
        @ParameterizedTest
        void __DH(final String groupName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var bc = switch (groupName) {
                case "rfc7919_ffdhe2048" -> DHStandardGroups.rfc7919_ffdhe2048;
                case "rfc7919_ffdhe3072" -> DHStandardGroups.rfc7919_ffdhe3072;
                case "rfc7919_ffdhe4096" -> DHStandardGroups.rfc7919_ffdhe4096;
                case "rfc7919_ffdhe6144" -> DHStandardGroups.rfc7919_ffdhe6144;
                case "rfc7919_ffdhe8192" -> DHStandardGroups.rfc7919_ffdhe8192;
                default -> throw new IllegalArgumentException("unknown group: " + groupName);
            };
            final var dhParams = new DHParameterSpec(bc.getP(), bc.getG(), bc.getL());
            final var aliceKp = generateDH(dhParams);
            final var bobKp = generateDH(dhParams);
            // --------------------------------------------------------------------------- when/then
            agree("DiffieHellman", BOUNCY_CASTLE_PROVIDER_NAME,
                  "DiffieHellman/" + groupName, aliceKp, bobKp);
        }

        /**
         * Verifies that finite-field Diffie-Hellman produces the same shared secret on both sides
         * when Alice and Bob each generate their own key pair in a pre-agreed RFC 3526 MODP group
         * (so no runtime parameter transfer is needed).
         *
         * @param groupName the BouncyCastle {@code DHStandardGroups} constant name (e.g.,
         *                  {@code "rfc3526_2048"}).
         */
        @_LatestLTS
        @_LatestJDK
        @DocumentedPreference(1)
        @GovernedPreference(2)
        @DisplayName("RFC 3526 MODP group")
        @ValueSource(strings = {
                // 1536 / 2048 only — 3072+ key generation gets slow even with precomputed groups
                "rfc3526_1536",
                "rfc3526_2048",
//                "rfc3526_3072",
//                "rfc3526_4096",
//                "rfc3526_6144",
//                "rfc3526_8192"
        })
        @ParameterizedTest
        void __DH1(final String groupName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var bc = switch (groupName) {
                case "rfc3526_1536" -> DHStandardGroups.rfc3526_1536;
                case "rfc3526_2048" -> DHStandardGroups.rfc3526_2048;
                case "rfc3526_3072" -> DHStandardGroups.rfc3526_3072;
                case "rfc3526_4096" -> DHStandardGroups.rfc3526_4096;
                case "rfc3526_6144" -> DHStandardGroups.rfc3526_6144;
                case "rfc3526_8192" -> DHStandardGroups.rfc3526_8192;
                default -> throw new IllegalArgumentException("unknown group: " + groupName);
            };
            final var dhParams = new DHParameterSpec(bc.getP(), bc.getG(), bc.getL());
            final var aliceKp = generateDH(dhParams);
            final var bobKp = generateDH(dhParams);
            // --------------------------------------------------------------------------- when/then
            agree("DiffieHellman", BOUNCY_CASTLE_PROVIDER_NAME,
                  "DiffieHellman/" + groupName, aliceKp, bobKp);
        }

        /**
         * Verifies that classical finite-field Diffie-Hellman produces the same shared secret on
         * both sides when Alice and Bob share the same DH group (Bob reuses Alice's
         * {@link javax.crypto.spec.DHParameterSpec}).
         *
         * @param keySize Alice's DH modulus size in bits.
         */
        @_LatestLTS
        @_LatestJDK
        @DocumentedPreference(1)
        @GovernedPreference(3)
        @DisplayName("basic — Alice→Bob parameter transfer")
        @ValueSource(ints = {
                // 2048 is the largest precomputed DH group BC ships; >=3072 falls back to fresh
                // parameter generation which is impractically slow (~minutes per call)
                2048
        })
        @ParameterizedTest
        void __DH2(final int keySize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final KeyPair aliceKp;
            {
                final var generator = KeyPairGenerator.getInstance(
                        "DH", BOUNCY_CASTLE_PROVIDER_NAME);
                generator.initialize(keySize);
                aliceKp = generator.generateKeyPair();
            }
            final KeyPair bobKp;
            {
                final var aliceParams = ((DHPublicKey) aliceKp.getPublic()).getParams();
                final var generator = KeyPairGenerator.getInstance(
                        "DH", BOUNCY_CASTLE_PROVIDER_NAME);
                generator.initialize(aliceParams);
                bobKp = generator.generateKeyPair();
            }
            // --------------------------------------------------------------------------- when/then
            agree("DiffieHellman", BOUNCY_CASTLE_PROVIDER_NAME,
                  "DiffieHellman/" + keySize, aliceKp, bobKp);
        }

        /**
         * Verifies that finite-field Diffie-Hellman produces the same shared secret on both sides
         * when the DH group is freshly generated via {@link AlgorithmParameterGenerator} and shared
         * to both parties — a pure-JCA path with no dependency on BouncyCastle's
         * {@code DHStandardGroups}.
         *
         * @param keySize the DH modulus size in bits.
         */
        @_LatestLTS
        @_LatestJDK
        @DocumentedPreference(1)
        @GovernedPreference(4)
        @Disabled(
                "BC's AlgorithmParameterGenerator(\"DH\") generates a fresh safe prime per call which takes many minutes even at 2048-bit; impractical wall-clock for unit tests")
        @DisplayName("shared AlgorithmParameterGenerator output")
        @ValueSource(ints = {
                2048
        })
        @ParameterizedTest
        void __DH3(final int keySize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var paramGen = AlgorithmParameterGenerator.getInstance(
                    "DH", BOUNCY_CASTLE_PROVIDER_NAME);
            paramGen.init(keySize);
            final var dhParams = paramGen.generateParameters()
                    .getParameterSpec(DHParameterSpec.class);
            final var aliceKp = generateDH(dhParams);
            final var bobKp = generateDH(dhParams);
            // --------------------------------------------------------------------------- when/then
            agree("DiffieHellman", BOUNCY_CASTLE_PROVIDER_NAME,
                  "DiffieHellman/APG/" + keySize, aliceKp, bobKp);
        }

        /**
         * Verifies that finite-field Diffie-Hellman produces the same shared secret on both sides
         * when Alice and Bob each generate their own key pair in a pre-agreed RFC 2409 (IKE Oakley)
         * MODP group. These groups are far too small for modern use (768 / 1024 bits) and are kept
         * here only for historical completeness.
         *
         * @param groupName the BouncyCastle {@code DHStandardGroups} constant name (e.g.,
         *                  {@code "rfc2409_1024"}).
         */
        @NotRecommended("IETF RFC 8247 (Oakley groups SHOULD NOT be used)")
        @NotRecommended("NIST SP 800-131A (DH key size < 2048 prohibited)")
        @DocumentedPreference(1)
        @GovernedPreference(5)
        @DisplayName("legacy RFC 2409 Oakley group")
        @ValueSource(strings = {
                "rfc2409_768",
                "rfc2409_1024"
        })
        @ParameterizedTest
        @Disabled(
                "768 / 1024-bit DH is below the JDK 25 SunJCE legacy threshold; runs only as a curiosity")
        void __DH4(final String groupName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var bc = switch (groupName) {
                case "rfc2409_768" -> DHStandardGroups.rfc2409_768;
                case "rfc2409_1024" -> DHStandardGroups.rfc2409_1024;
                default -> throw new IllegalArgumentException("unknown group: " + groupName);
            };
            final var dhParams = new DHParameterSpec(bc.getP(), bc.getG(), bc.getL());
            final var aliceKp = generateDH(dhParams);
            final var bobKp = generateDH(dhParams);
            // --------------------------------------------------------------------------- when/then
            agree("DiffieHellman", BOUNCY_CASTLE_PROVIDER_NAME,
                  "DiffieHellman/" + groupName, aliceKp, bobKp);
        }
    }

    /**
     * Elliptic-curve Diffie-Hellman (ECDH) on Weierstrass-form curve groups. Methods enumerate
     * curve families and the cofactor variant; choice depends on domain and policy, not
     * cryptographic primitive.
     *
     * <p>Curve-family preference (top to bottom):
     * <ol>
     *   <li>{@link #__ECDH __ECDH} &mdash; NIST P-curves ({@code secp256r1} / {@code secp384r1} /
     *       {@code secp521r1}). <strong>Recommended default</strong> for TLS, FIPS, and general
     *       interoperability; fastest in mainstream stacks.</li>
     *   <li>{@link #__ECDH1 __ECDH1} &mdash; SEC Koblitz curves ({@code secp256k1}). Use only in
     *       blockchain ecosystems (Bitcoin / Ethereum); outside those, no reason to pick this
     *       over NIST.</li>
     *   <li>{@link #__ECDH2 __ECDH2} &mdash; Brainpool (RFC 5639). Chosen when policy prefers
     *       non-NIST-influenced parameters (EU government / banking). Generally slower.</li>
     *   <li>{@link #__ECDH3 __ECDH3} &mdash; Brainpool twisted ({@code …t1}). Slightly faster
     *       Brainpool variant; rarely used in practice.</li>
     *   <li>{@link #__ECDH4 __ECDH4} &mdash; ANSI X9.62 prime-field curves ({@code prime239v*}).
     *       Older standard, mostly superseded by SEC 2 naming; not chosen for new designs.</li>
     *   <li>{@link #__ECDH5 __ECDH5} &mdash; SM2 ({@code sm2p256v1}). Mandatory in Chinese
     *       systems, not used elsewhere; requires SM2's dedicated key-exchange flow rather than
     *       plain ECDH.</li>
     * </ol>
     *
     * <p>Algorithm variants:
     * <ul>
     *   <li>{@link #__ECDHC __ECDHC} &mdash; cofactor variant. Only meaningful when the chosen
     *       curve has cofactor {@code > 1}; for the NIST P-curves (cofactor 1) it produces the
     *       same secret as plain ECDH.</li>
     * </ul>
     */
    @Nested
    @DisplayName("ECDH")
    class ECDH_Test {

        /**
         * Verifies that elliptic-curve Diffie-Hellman over the given NIST P-curve produces the same
         * shared secret on both sides. {@code ECDH} is not JCA-mandatory, so the test binds the
         * KeyPair generation and the KeyAgreement to {@link BouncyCastleProvider} explicitly.
         *
         * @param stdName the standard curve name (e.g., {@code "secp256r1"}).
         */
        @_LatestLTS
        @_LatestJDK
        @DocumentedPreference(2)
        @GovernedPreference(1)
        @DisplayName("NIST P-curve")
        @ValueSource(strings = {
                "secp256r1",
                "secp384r1",
                "secp521r1"
        })
        @ParameterizedTest
        void __ECDH(final String stdName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE_PROVIDER_NAME);
            generator.initialize(new ECGenParameterSpec(stdName));
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree("ECDH", BOUNCY_CASTLE_PROVIDER_NAME, "ECDH/" + stdName, aliceKp, bobKp);
        }

        /**
         * Verifies that elliptic-curve Diffie-Hellman over the given Koblitz curve (SEC 2's
         * {@code secpNNNk1} family — {@code secp256k1} is the curve used by Bitcoin / Ethereum)
         * produces the same shared secret on both sides. Koblitz curves sit outside the NIST
         * P-curve family; BouncyCastle exposes them via JCA, so both KeyPair generation and
         * KeyAgreement bind to {@link BouncyCastleProvider} explicitly.
         *
         * @param stdName the standard Koblitz curve name (e.g., {@code "secp256k1"}).
         */
        @DocumentedPreference(2)
        @GovernedPreference(2)
        @DisplayName("SEC Koblitz curve")
        @ValueSource(strings = {
                "secp192k1",
                "secp224k1",
                "secp256k1"
        })
        @ParameterizedTest
        void __ECDH1(final String stdName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE_PROVIDER_NAME);
            generator.initialize(new ECGenParameterSpec(stdName));
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree("ECDH", BOUNCY_CASTLE_PROVIDER_NAME, "ECDH/" + stdName, aliceKp, bobKp);
        }

        /**
         * Verifies that elliptic-curve Diffie-Hellman over the given Brainpool curve (RFC 5639)
         * produces the same shared secret on both sides. Brainpool curves are not in the NIST
         * P-curve family; BouncyCastle provides them, so the test binds both KeyPair generation and
         * KeyAgreement to {@link BouncyCastleProvider} explicitly.
         *
         * @param stdName the Brainpool curve name (e.g., {@code "brainpoolP256r1"}).
         */
        @DocumentedPreference(3)
        @GovernedPreference(3)
        @DisplayName("Brainpool curve (RFC 5639)")
        @ValueSource(strings = {
                "brainpoolP160r1",
                "brainpoolP192r1",
                "brainpoolP224r1",
                "brainpoolP256r1",
                "brainpoolP320r1",
                "brainpoolP384r1",
                "brainpoolP512r1"
        })
        @ParameterizedTest
        void __ECDH2(final String stdName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE_PROVIDER_NAME);
            generator.initialize(new ECGenParameterSpec(stdName));
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree("ECDH", BOUNCY_CASTLE_PROVIDER_NAME, "ECDH/" + stdName, aliceKp, bobKp);
        }

        /**
         * Verifies that elliptic-curve Diffie-Hellman over a Brainpool <em>twisted</em> curve (RFC
         * 5639's {@code brainpoolPNNNt1} family) produces the same shared secret on both sides.
         *
         * @param stdName the Brainpool twisted curve name (e.g., {@code "brainpoolP256t1"}).
         */
        @DocumentedPreference(3)
        @GovernedPreference(4)
        @DisplayName("Brainpool twisted curve")
        @ValueSource(strings = {
                "brainpoolP256t1",
                "brainpoolP384t1",
                "brainpoolP512t1"
        })
        @ParameterizedTest
        void __ECDH3(final String stdName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE_PROVIDER_NAME);
            generator.initialize(new ECGenParameterSpec(stdName));
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree("ECDH", BOUNCY_CASTLE_PROVIDER_NAME, "ECDH/" + stdName, aliceKp, bobKp);
        }

        /**
         * Verifies that elliptic-curve Diffie-Hellman over the given ANSI X9.62 prime-field curve
         * produces the same shared secret on both sides. ANSI X9.62 names predate the SEC 2 /
         * Brainpool naming and overlap partially (e.g., {@code prime256v1} == {@code secp256r1}).
         *
         * @param stdName the ANSI X9.62 curve name (e.g., {@code "prime239v1"}).
         */
        @DocumentedPreference(3)
        @GovernedPreference(5)
        @DisplayName("ANSI X9.62 prime-field curve")
        @ValueSource(strings = {
                "prime239v1",
                "prime239v2",
                "prime239v3"
        })
        @ParameterizedTest
        void __ECDH4(final String stdName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE_PROVIDER_NAME);
            generator.initialize(new ECGenParameterSpec(stdName));
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree("ECDH", BOUNCY_CASTLE_PROVIDER_NAME, "ECDH/" + stdName, aliceKp, bobKp);
        }

        /**
         * Verifies that elliptic-curve Diffie-Hellman over the Chinese {@code sm2p256v1} curve
         * (GB/T 32918) produces the same shared secret on both sides.
         *
         * <p>Disabled by default: SM2 key exchange has its own dedicated protocol (BC's
         * {@code SM2KeyExchange}) and is not consistently dispatched under the {@code ECDH}
         * algorithm name across BC releases — running this case requires verifying the algorithm
         * name BC currently registers for SM2-curve agreement.
         */
        @Disabled(
                "SM2 key exchange uses BC's dedicated protocol — algorithm name varies by release")
        @GovernedPreference(6)
        @DisplayName("SM2 curve (sm2p256v1)")
        @Test
        void __ECDH5() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var stdName = "sm2p256v1";
            final var generator = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE_PROVIDER_NAME);
            generator.initialize(new ECGenParameterSpec(stdName));
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree("ECDH", BOUNCY_CASTLE_PROVIDER_NAME, "ECDH/" + stdName, aliceKp, bobKp);
        }

        /**
         * Verifies that the cofactor variant {@code ECDHC} (cofactor multiplication applied to the
         * shared point) produces the same shared secret on both sides over the given NIST P-curve.
         * For curves with cofactor 1 (all NIST P-curves), {@code ECDHC} computes the same secret as
         * plain {@code ECDH}; the variant matters only when cofactor &gt; 1.
         *
         * @param stdName the standard curve name (e.g., {@code "secp256r1"}).
         */
        @DisplayName("cofactor variant (ECDHC)")
        @ValueSource(strings = {
                "secp256r1",
                "secp384r1",
                "secp521r1"
        })
        @ParameterizedTest
        void __ECDHC(final String stdName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE_PROVIDER_NAME);
            generator.initialize(new ECGenParameterSpec(stdName));
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree("ECDHC", BOUNCY_CASTLE_PROVIDER_NAME, "ECDHC/" + stdName, aliceKp, bobKp);
        }
    }

    /**
     * Montgomery-curve Diffie-Hellman (X25519 / X448, RFC 7748). All three methods exercise the
     * same cryptographic primitive; they differ only in the JCA API entry point used to reach it.
     *
     * <p>API-style preference (top to bottom):
     * <ol>
     *   <li>{@link #__XDH __XDH} &mdash; {@code KeyPairGenerator.getInstance("XDH")} initialised
     *       with the static {@link NamedParameterSpec#X25519} / {@link NamedParameterSpec#X448}
     *       constants. <strong>Recommended:</strong> type-safe, IDE-completable, canonical form.</li>
     *   <li>{@link #__XDH1 __XDH1} &mdash; {@code "XDH"} umbrella plus
     *       {@code new NamedParameterSpec(stdName)}. Same result, but stringly-typed; useful only
     *       when the curve must be chosen dynamically from external configuration.</li>
     *   <li>{@link #__XDH2 __XDH2} &mdash; algorithm name <em>is</em> the curve
     *       ({@code KeyPairGenerator.getInstance("X25519")} / {@code "X448"}). Concise but couples
     *       the call site to a specific curve, so swapping curves means changing the algorithm
     *       string itself.</li>
     * </ol>
     *
     * <p>Curve choice:
     * <ul>
     *   <li>{@code X25519} &mdash; 128-bit security level; fastest; the typical default.</li>
     *   <li>{@code X448} &mdash; 224-bit security level; ~3–4× slower; pick when a higher
     *       security margin is required.</li>
     * </ul>
     */
    @Nested
    @DisplayName("XDH")
    class XDH_Test {

        /**
         * Verifies that Montgomery-curve Diffie-Hellman produces the same shared secret on both
         * sides when reached via the {@code XDH} umbrella name with the canonical static
         * {@link NamedParameterSpec#X25519} / {@link NamedParameterSpec#X448} constants (as opposed
         * to the string-constructed instances used in {@code __XDH1}).
         */
        @_LatestLTS
        @_LatestJDK
        @DocumentedPreference(1)
        @GovernedPreference(1)
        @DisplayName("XDH + NamedParameterSpec static constants")
        @Test
        void __XDH() throws Exception {
            for (final var spec : List.of(NamedParameterSpec.X25519, NamedParameterSpec.X448)) {
                // --------------------------------------------------------------------------- given
                final var generator = KeyPairGenerator.getInstance(
                        "XDH", BOUNCY_CASTLE_PROVIDER_NAME);
                generator.initialize(spec);
                final var aliceKp = generator.generateKeyPair();
                final var bobKp = generator.generateKeyPair();
                // ----------------------------------------------------------------------- when/then
                agree("XDH", BOUNCY_CASTLE_PROVIDER_NAME, "XDH/" + spec.getName(), aliceKp, bobKp);
            }
        }

        /**
         * Verifies that Montgomery-curve Diffie-Hellman produces the same shared secret on both
         * sides when reached via the {@code XDH} umbrella name with a JCA-standard
         * {@link NamedParameterSpec} ({@link NamedParameterSpec#X25519} /
         * {@link NamedParameterSpec#X448}) — an alternative entry point to using the curve as the
         * algorithm name directly.
         *
         * @param stdName the standard curve name (e.g., {@code "X25519"}).
         */
        @_LatestLTS
        @_LatestJDK
        @DocumentedPreference(2)
        @GovernedPreference(2)
        @DisplayName("XDH + NamedParameterSpec(String)")
        @ValueSource(strings = {
                "X25519",
                "X448"
        })
        @ParameterizedTest
        void __XDH1(final String stdName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("XDH", BOUNCY_CASTLE_PROVIDER_NAME);
            generator.initialize(new NamedParameterSpec(stdName));
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree("XDH", BOUNCY_CASTLE_PROVIDER_NAME, "XDH/" + stdName, aliceKp, bobKp);
        }

        /**
         * Verifies that Montgomery-curve Diffie-Hellman ({@code X25519} or {@code X448}) produces
         * the same shared secret on both sides. Neither curve is JCA-mandatory, so the test binds
         * the KeyPair generation and the KeyAgreement to {@link BouncyCastleProvider} explicitly.
         *
         * @param stdName the standard curve name (e.g., {@code "X25519"}).
         */
        @_LatestLTS
        @_LatestJDK
        @DocumentedPreference(2)
        @GovernedPreference(3)
        @DisplayName("by curve name (X25519 / X448)")
        @ValueSource(strings = {
                "X25519",
                "X448"
        })
        @ParameterizedTest
        void __XDH2(final String stdName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance(
                    stdName, BOUNCY_CASTLE_PROVIDER_NAME);
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree(stdName, BOUNCY_CASTLE_PROVIDER_NAME, stdName, aliceKp, bobKp);
        }
    }

    @Nested
    @DisplayName("ECMQV")
    class ECMQV_Test {

        /**
         * Verifies that elliptic-curve Menezes-Qu-Vanstone agreement over the given NIST P-curve
         * produces the same shared secret on both sides. Unlike plain ECDH, ECMQV is a <em>two-pass
         * MQV</em> protocol: each party holds a static and an ephemeral key pair in the same group,
         * and feeds the ephemeral private + ephemeral public + the other party's ephemeral public
         * into {@link MQVParameterSpec} before running {@link KeyAgreement#doPhase doPhase} against
         * the other party's static public key.
         *
         * @param stdName the standard curve name (e.g., {@code "secp256r1"}).
         */
        @NotRecommended("NIST SP 800-56A rev. 3 (ECMQV removed)")
        @DisplayName("NIST P-curve (static + ephemeral pairs)")
        @ValueSource(strings = {
                "secp256r1",
                "secp384r1",
                "secp521r1"
        })
        @ParameterizedTest
        void __ECMQV(final String stdName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var ecGen = KeyPairGenerator.getInstance("EC", BOUNCY_CASTLE_PROVIDER_NAME);
            ecGen.initialize(new ECGenParameterSpec(stdName));
            final var aliceStatic = ecGen.generateKeyPair();
            final var aliceEphemeral = ecGen.generateKeyPair();
            final var bobStatic = ecGen.generateKeyPair();
            final var bobEphemeral = ecGen.generateKeyPair();
            // ----------------------------------------------------------------------------- alice
            final var aliceKa = KeyAgreement.getInstance("ECMQV", BOUNCY_CASTLE_PROVIDER_NAME);
            aliceKa.init(aliceStatic.getPrivate(),
                         new MQVParameterSpec(aliceEphemeral.getPublic(),
                                              aliceEphemeral.getPrivate(),
                                              bobEphemeral.getPublic()));
            aliceKa.doPhase(bobStatic.getPublic(), true);
            final var aliceSecret = aliceKa.generateSecret();
            // ------------------------------------------------------------------------------- bob
            final var bobKa = KeyAgreement.getInstance("ECMQV", BOUNCY_CASTLE_PROVIDER_NAME);
            bobKa.init(bobStatic.getPrivate(),
                       new MQVParameterSpec(bobEphemeral.getPublic(),
                                            bobEphemeral.getPrivate(),
                                            aliceEphemeral.getPublic()));
            bobKa.doPhase(aliceStatic.getPublic(), true);
            final var bobSecret = bobKa.generateSecret();
            // ----------------------------------------------------------------------------- match
            printf("ECMQV/" + stdName, aliceKa.getProvider().getName(), aliceSecret);
            assertArrayEquals(aliceSecret, bobSecret);
        }
    }
}
