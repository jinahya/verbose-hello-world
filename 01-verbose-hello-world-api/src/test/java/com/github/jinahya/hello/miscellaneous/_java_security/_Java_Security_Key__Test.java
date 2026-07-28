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
import com.github.jinahya.hello.miscellaneous._javax_crypto.*;
import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import javax.crypto.*;
import javax.crypto.interfaces.*;
import javax.crypto.spec.*;
import java.nio.charset.*;
import java.security.*;
import java.security.spec.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class demonstrating key establishment via the JCA Key APIs, organised by the categories
 * laid out in {@code _KEY.asciidoc}:
 * <ul>
 *   <li>{@link KeyTransport_Test} &mdash; RSA-based key transport (§2): sender wraps a fresh AES
 *       data key under the receiver's RSA public key.</li>
 *   <li>{@link KeyAgreement_Test} &mdash; Diffie-Hellman based key agreement (§3): both sides
 *       compute the same shared secret from their own key pairs.</li>
 *   <li>{@link KeyWrapper_Test} &mdash; symmetric AES key wrapping (§4): an AES data key is
 *       wrapped under a previously-shared AES KEK.</li>
 * </ul>
 *
 * <p>Complemented by {@link _Javax_Crypto_KEM__Test} for §5 (KEM) and
 * {@link _Javax_Crypto_KDF_Test} / {@link _Javax_Crypto_PBE_Test} for §6 (key derivation).
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/25/docs/specs/security/standard-names.html#cipher-algorithms">JDK
 * 25 JCA Standard Algorithm Names &mdash; Cipher Algorithms</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc8017">RFC 8017 &mdash; PKCS #1 v2.2: RSA
 * Cryptography Specifications (RSAES-OAEP, RSAES-PKCS1-v1_5)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc3394">RFC 3394 &mdash; Advanced
 * Encryption Standard (AES) Key Wrap Algorithm</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc5649">RFC 5649 &mdash; Advanced
 * Encryption Standard (AES) Key Wrap with Padding Algorithm</a>
 */
@_HideNameFromPublishing
@DisplayName("java.security.Key")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Java_Security_Key__Test {

    /**
     * Prints a one-line summary of a key-transport round-trip &mdash; transformation, data-key
     * algorithm/length, Base64-encoded first/last four characters of the data key, and the wrapped
     * ciphertext byte length &mdash; to {@link System#out}.
     *
     * @param transformation the asymmetric {@link Cipher} transformation.
     * @param providerName   the provider that resolved the transformation.
     * @param dataKey        the transported {@link SecretKey}.
     * @param wrappedBytes   the byte length of the wrapped ciphertext.
     */
    private static void printf(final String transformation, final String providerName,
                               final SecretKey dataKey, final int wrappedBytes) {
        final var encoded = Base64.getEncoder().encodeToString(dataKey.getEncoded());
        System.out.printf("%-42s (%-6s) keyAlg=%-4s (%2d) %s...%s  wrapped=%4d B%n",
                          transformation, providerName,
                          dataKey.getAlgorithm(),
                          dataKey.getEncoded().length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4),
                          wrappedBytes);
    }

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
     * standard algorithm name, and asserts that both sides compute the same shared secret
     * byte-for-byte.
     *
     * @param algorithm the JCE standard {@link KeyAgreement} algorithm name (e.g.,
     *                  {@code "DiffieHellman"}, {@code "ECDH"}, {@code "X25519"}).
     * @param label     a human-readable label used in the printed summary (typically algorithm +
     *                  curve / key size).
     * @param alice     Alice's key pair (private + public).
     * @param bob       Bob's key pair (must share the same group / parameters as Alice's).
     */
    private static void agree(final String algorithm, final String label,
                              final KeyPair alice, final KeyPair bob) throws Exception {
        // ---------------------------------------------------------------------------------- alice
        final var aliceKa = KeyAgreement.getInstance(algorithm);
        aliceKa.init(alice.getPrivate());
        aliceKa.doPhase(bob.getPublic(), true);
        final var aliceSecret = aliceKa.generateSecret();
        // ------------------------------------------------------------------------------------ bob
        final var bobKa = KeyAgreement.getInstance(algorithm);
        bobKa.init(bob.getPrivate());
        bobKa.doPhase(alice.getPublic(), true);
        final var bobSecret = bobKa.generateSecret();
        // ---------------------------------------------------------------------------------- match
        printf(label, aliceKa.getProvider().getName(), aliceSecret);
        assertArrayEquals(aliceSecret, bobSecret);
    }

    /**
     * Wraps {@code dataKey} under {@code wrappingKey} with the given {@link Cipher}
     * {@code transformation}, unwraps the result under {@code unwrappingKey}, and asserts the
     * recovered key is byte-for-byte equal to the original.
     *
     * <p>For asymmetric key transport, {@code wrappingKey} is the receiver's public key and
     * {@code unwrappingKey} is the receiver's private key. For symmetric key wrapping, both are the
     * same shared KEK.
     *
     * @param transformation the {@link Cipher} transformation (e.g.,
     *                       {@code "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"},
     *                       {@code "AES/KW/NoPadding"}).
     * @param wrappingKey    the key passed to {@link Cipher#WRAP_MODE}.
     * @param unwrappingKey  the key passed to {@link Cipher#UNWRAP_MODE}.
     * @param dataKey        the {@link SecretKey} to wrap and unwrap.
     */
    private static void wrap(final String transformation, final Key wrappingKey,
                             final Key unwrappingKey, final SecretKey dataKey) throws Exception {
        // ----------------------------------------------------------------------------------- wrap
        final byte[] wrapped;
        {
            final var cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.WRAP_MODE, wrappingKey);
            wrapped = cipher.wrap(dataKey);
        }
        // --------------------------------------------------------------------------------- unwrap
        final Key unwrapped;
        {
            final var cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.UNWRAP_MODE, unwrappingKey);
            unwrapped = cipher.unwrap(wrapped, dataKey.getAlgorithm(), Cipher.SECRET_KEY);
        }
        // ---------------------------------------------------------------------------------- match
        printf(transformation,
               Cipher.getInstance(transformation).getProvider().getName(),
               (SecretKey) unwrapped, wrapped.length);
        assertEquals(dataKey.getAlgorithm(), unwrapped.getAlgorithm());
        assertArrayEquals(dataKey.getEncoded(), unwrapped.getEncoded());
    }

    /**
     * Runs a single {@link KEM} round-trip against the given receiver key pair and asserts that the
     * sender's encapsulated shared key matches the receiver's decapsulated shared key
     * byte-for-byte.
     *
     * @param algorithm       the JCE standard {@link KEM} algorithm name (e.g., {@code "DHKEM"},
     *                        {@code "ML-KEM-768"}).
     * @param label           a human-readable label used in the printed summary.
     * @param receiverKeyPair the receiver's asymmetric key pair (compatible with
     *                        {@code algorithm}).
     */
    private static void encapsulate(final String algorithm, final String label,
                                    final KeyPair receiverKeyPair) throws Exception {
        final var kem = KEM.getInstance(algorithm);
        // --------------------------------------------------------------------------------- sender
        final var encapsulator = kem.newEncapsulator(receiverKeyPair.getPublic());
        final var encapsulated = encapsulator.encapsulate();
        // ------------------------------------------------------------------------------- receiver
        final var decapsulator = kem.newDecapsulator(receiverKeyPair.getPrivate());
        final var sharedKey = decapsulator.decapsulate(encapsulated.encapsulation());
        // ---------------------------------------------------------------------------------- match
        {
            final var encoded = Base64.getEncoder()
                    .encodeToString(encapsulated.key().getEncoded());
            System.out.printf("%-22s (%-6s) keyAlg=%-8s (%4d) %s...%s  ct=%5d B%n",
                              label, encapsulator.providerName(),
                              encapsulated.key().getAlgorithm(),
                              encapsulated.key().getEncoded().length,
                              encoded.substring(0, 4),
                              encoded.substring(encoded.length() - 4),
                              encapsulated.encapsulation().length);
        }
        assertArrayEquals(encapsulated.key().getEncoded(), sharedKey.getEncoded());
    }

    /**
     * A nested test class demonstrating RSA-based key transport (§2 of {@code _KEY.asciidoc}): the
     * sender generates a fresh AES data key, wraps it under the receiver's RSA public key with each
     * registered asymmetric {@link Cipher} transformation, and the receiver unwraps with the
     * matching private key. Parameterised over every RSA padding listed on the JDK 25 standard
     * names page (the only asymmetric {@link Cipher} algorithms registered by SunJCE).
     */
    @DisplayName("key transport / RSA")
    @Nested
    class KeyTransport_Test {

        // receiver's RSA modulus size; 2048 is the OWASP-2023 minimum for new keys
        private static final int RSA_KEY_SIZE = 2048;

        // sender's data key algorithm + size — AES-256 is the conventional payload
        private static final String DATA_KEY_ALGORITHM = "AES";

        private static final int DATA_KEY_SIZE = 256;

        /**
         * Verifies that an AES data key wrapped under the receiver's RSA public key with the given
         * {@code transformation} is recovered byte-for-byte when the receiver unwraps it with the
         * matching private key.
         *
         * @param transformation a JCE {@link Cipher} transformation for RSA key transport (e.g.,
         *                       {@code "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"}).
         */
        @DisplayName("RSA / AES")
        @ValueSource(strings = {
                "RSA/ECB/PKCS1Padding",
                "RSA/ECB/OAEPWithSHA-1AndMGF1Padding",
                "RSA/ECB/OAEPWithSHA-224AndMGF1Padding",
                "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
                "RSA/ECB/OAEPWithSHA-384AndMGF1Padding",
                "RSA/ECB/OAEPWithSHA-512AndMGF1Padding",
                "RSA/ECB/OAEPWithSHA-512/224AndMGF1Padding",
                "RSA/ECB/OAEPWithSHA-512/256AndMGF1Padding"
        })
        @ParameterizedTest
        void __(final String transformation) throws Exception {
            // ------------------------------------------------------------------------------- given
            final KeyPair receiverKeyPair;
            {
                final var generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(RSA_KEY_SIZE);
                receiverKeyPair = generator.generateKeyPair();
            }
            final SecretKey dataKey;
            {
                final var generator = KeyGenerator.getInstance(DATA_KEY_ALGORITHM);
                generator.init(DATA_KEY_SIZE);
                dataKey = generator.generateKey();
            }
            // -------------------------------------------------------------------------- when/then
            wrap(transformation, receiverKeyPair.getPublic(), receiverKeyPair.getPrivate(),
                 dataKey);
        }
    }

    /**
     * A nested test class demonstrating <em>key agreement</em>: Alice and Bob each generate their
     * own key pair under the same group / curve, then independently combine their own private with
     * the other's public to compute the same shared secret. The test asserts that both sides arrive
     * at byte-identical secrets. Parameterised over every {@link KeyAgreement} algorithm registered
     * by SunJCE and SunEC on JDK 25 ({@code DiffieHellman}, {@code ECDH}, {@code X25519},
     * {@code X448}).
     */
    @DisplayName("KeyAgreement")
    @Nested
    class KeyAgreement_Test {

        /**
         * Verifies that classical finite-field Diffie-Hellman produces the same shared secret on
         * both sides when Alice and Bob share the same DH group (Bob reuses Alice's
         * {@link DHParameterSpec}).
         *
         * @param keySize Alice's DH modulus size in bits.
         */
        @DisplayName("DiffieHellman")
        @ValueSource(ints = {
                2048,
                3072
        })
        @ParameterizedTest
        void __DH(final int keySize) throws Exception {
            // ------------------------------------------------------------------------------- given
            final KeyPair aliceKp;
            {
                final var generator = KeyPairGenerator.getInstance("DH");
                generator.initialize(keySize);
                aliceKp = generator.generateKeyPair();
            }
            final KeyPair bobKp;
            {
                final var aliceParams = ((DHPublicKey) aliceKp.getPublic()).getParams();
                final var generator = KeyPairGenerator.getInstance("DH");
                generator.initialize(aliceParams);
                bobKp = generator.generateKeyPair();
            }
            // --------------------------------------------------------------------------- when/then
            agree("DiffieHellman", "DiffieHellman/" + keySize, aliceKp, bobKp);
        }

        /**
         * Verifies that elliptic-curve Diffie-Hellman over the given NIST P-curve produces the same
         * shared secret on both sides.
         *
         * @param stdName the standard curve name (e.g., {@code "secp256r1"}).
         */
        @DisplayName("ECDH")
        @ValueSource(strings = {
                "secp256r1",
                "secp384r1",
                "secp521r1"
        })
        @ParameterizedTest
        void __ECDH(final String stdName) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(new ECGenParameterSpec(stdName));
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree("ECDH", "ECDH/" + stdName, aliceKp, bobKp);
        }

        /**
         * Verifies that Montgomery-curve Diffie-Hellman ({@code X25519} or {@code X448}) produces
         * the same shared secret on both sides.
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
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance(stdName);
            final var aliceKp = generator.generateKeyPair();
            final var bobKp = generator.generateKeyPair();
            // --------------------------------------------------------------------------- when/then
            agree(stdName, stdName, aliceKp, bobKp);
        }
    }

    /**
     * A nested test class demonstrating symmetric AES key wrapping (§4 of {@code _KEY.asciidoc}):
     * Alice and Bob share a common AES KEK in advance, Alice wraps a fresh AES data key under that
     * KEK so it can be transmitted as opaque bytes, and Bob unwraps with the same KEK.
     * Parameterised over every AES key-wrap transformation registered by SunJCE on JDK 25 (the
     * {@code AESWrap} / {@code AESWrapPad} legacy aliases plus the {@code AES/KW/*} /
     * {@code AES/KWP/*} JDK 17+ forms).
     */
    @DisplayName("key wrapping / AES KW/KWP")
    @Nested
    class KeyWrapper_Test {

        // shared KEK size; AES-256 is the conventional choice for a wrapping key
        private static final int KEK_SIZE = 256;

        // data key algorithm + size; 128 bits = 16 bytes is a multiple of 8, so the same data key
        // works under all five transformations (AESWrap / AES/KW/NoPadding require 8-byte multiples)
        private static final String DATA_KEY_ALGORITHM = "AES";

        private static final int DATA_KEY_SIZE = 128;

        /**
         * Verifies that an AES data key wrapped under a previously-shared AES KEK with the given
         * {@code transformation} is recovered byte-for-byte when the receiver unwraps it with the
         * same KEK.
         *
         * @param transformation a JCE {@link Cipher} transformation for symmetric key wrapping
         *                       (e.g., {@code "AES/KW/NoPadding"}).
         */
        @DisplayName("AES / AES KEK")
        @ValueSource(strings = {
                "AESWrap",
                "AESWrapPad",
                "AES/KW/NoPadding",
                "AES/KW/PKCS5Padding",
                "AES/KWP/NoPadding"
        })
        @ParameterizedTest
        void __(final String transformation) throws Exception {
            // ------------------------------------------------------------------------------- given
            final SecretKey kek;
            {
                final var generator = KeyGenerator.getInstance("AES");
                generator.init(KEK_SIZE);
                kek = generator.generateKey();
            }
            final SecretKey dataKey;
            {
                final var generator = KeyGenerator.getInstance(DATA_KEY_ALGORITHM);
                generator.init(DATA_KEY_SIZE);
                dataKey = generator.generateKey();
            }
            // -------------------------------------------------------------------------- when/then
            wrap(transformation, kek, kek, dataKey);
        }
    }

    /**
     * A nested test class demonstrating key encapsulation (§5 of {@code _KEY.asciidoc}) at a thin,
     * one-test-per-flavour level: one classical {@code DHKEM} round-trip over an X25519 key pair,
     * and one post-quantum {@code ML-KEM-768} round-trip. The full sweep over DHKEM curves and the
     * three ML-KEM parameter sets lives in {@link _Javax_Crypto_KEM__Test}.
     */
    @DisplayName("key encapsulation / KEM")
    @Nested
    class KeyEncapsulation_Test {

        /**
         * Verifies a single classical {@code DHKEM} round-trip with an X25519 receiver key pair.
         */
        @DisplayName("DHKEM / X25519")
        @Test
        void __DHKEM_X25519() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("X25519");
            final var receiverKeyPair = generator.generateKeyPair();
            // -------------------------------------------------------------------------- when/then
            encapsulate("DHKEM", "DHKEM/X25519", receiverKeyPair);
        }

        /**
         * Verifies a single post-quantum {@code ML-KEM-768} round-trip.
         */
        @DisplayName("ML-KEM-768")
        @Test
        void __ML_KEM_768() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyPairGenerator.getInstance("ML-KEM-768");
            final var receiverKeyPair = generator.generateKeyPair();
            // -------------------------------------------------------------------------- when/then
            encapsulate("ML-KEM-768", "ML-KEM-768", receiverKeyPair);
        }
    }

    /**
     * A nested test class demonstrating key derivation (§6 of {@code _KEY.asciidoc}) at a thin,
     * one-test-per-flavour level: one {@code HKDF-SHA256} derivation from uniformly-random IKM (via
     * {@link KDF}), and one {@code PBKDF2WithHmacSHA256} derivation from a passphrase (via
     * {@link SecretKeyFactory}). The full sweeps live in {@link _Javax_Crypto_KDF_Test} and
     * {@link _Javax_Crypto_PBE_Test}.
     */
    @DisplayName("key derivation")
    @Nested
    class KeyDerivation_Test {

        /**
         * Verifies that {@code HKDF-SHA256} derives a deterministic AES {@link SecretKey} from
         * uniformly-random IKM, salt, and info: invoking {@link KDF#deriveKey} twice with the same
         * {@link HKDFParameterSpec} yields byte-identical keys.
         */
        @DisplayName("HKDF-SHA256 / AES")
        @Test
        void __HKDF() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var kdf = KDF.getInstance("HKDF-SHA256");
            final var ikm = new byte[32];
            ThreadLocalRandom.current().nextBytes(ikm);
            final var salt = new byte[16];
            ThreadLocalRandom.current().nextBytes(salt);
            final var info = "hello, world".getBytes(StandardCharsets.UTF_8);
            // -------------------------------------------------------------------------------- when
            final SecretKey key1;
            final SecretKey key2;
            {
                final var spec = HKDFParameterSpec.ofExtract()
                        .addIKM(ikm).addSalt(salt).thenExpand(info, 32);
                key1 = kdf.deriveKey("AES", spec);
            }
            {
                final var spec = HKDFParameterSpec.ofExtract()
                        .addIKM(ikm).addSalt(salt).thenExpand(info, 32);
                key2 = kdf.deriveKey("AES", spec);
            }
            // -------------------------------------------------------------------------------- then
            printf("HKDF-SHA256", kdf.getProviderName(), key1.getEncoded());
            assertEquals("AES", key1.getAlgorithm());
            assertArrayEquals(key1.getEncoded(), key2.getEncoded());
        }

        /**
         * Verifies that {@code PBKDF2WithHmacSHA256} derives a deterministic 256-bit key from the
         * same passphrase + salt + iteration count: invoking
         * {@link SecretKeyFactory#generateSecret} twice yields byte-identical keys.
         */
        @DisplayName("PBKDF2WithHmacSHA256")
        @Test
        void __PBKDF2() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            final var password = "iloveyou".toCharArray();
            final var salt = new byte[16];
            ThreadLocalRandom.current().nextBytes(salt);
            // 100,000 — picked light for a thin happy-path test; OWASP 2023 recommends ≥600,000
            final int iterations = 100_000;
            final int keyBits = 256;
            // -------------------------------------------------------------------------------- when
            final var key1 = factory.generateSecret(
                    new PBEKeySpec(password, salt, iterations, keyBits)).getEncoded();
            final var key2 = factory.generateSecret(
                    new PBEKeySpec(password, salt, iterations, keyBits)).getEncoded();
            // -------------------------------------------------------------------------------- then
            printf("PBKDF2WithHmacSHA256", factory.getProvider().getName(), key1);
            assertArrayEquals(key1, key2);
        }
    }
}