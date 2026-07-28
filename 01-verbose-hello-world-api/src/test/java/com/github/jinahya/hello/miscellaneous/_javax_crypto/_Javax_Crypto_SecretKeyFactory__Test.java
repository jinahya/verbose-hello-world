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
import lombok.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.*;
import java.util.*;
import java.util.concurrent.*;

import static com.github.jinahya.hello.miscellaneous._org_bouncycastle_jce_provider._org_bouncycastle_jce_provider__TestConstants.*;
import static com.github.jinahya.hello.miscellaneous._org_bouncycastle_jce_provider._org_bouncycastle_jce_provider__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A test class iterating over the {@link SecretKeyFactory} algorithms listed on the <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#secretkeyfactory-algorithms">Java
 * 26 JCA Standard Algorithm Names</a> page. Each test asserts that two
 * {@link SecretKeyFactory#generateSecret(KeySpec)} calls with the same {@link KeySpec} yield
 * byte-identical encoded keys via the BouncyCastle provider.
 *
 * @see <a
 * href="https://docs.oracle.com/en/java/javase/26/docs/specs/security/standard-names.html#secretkeyfactory-algorithms">JDK
 * 26 JCA Standard Algorithm Names &mdash; SecretKeyFactory Algorithms</a>
 */
@_HideNameFromPublishing
@DisplayName("javax.crypto.SecretKeyFactory")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_SecretKeyFactory__Test {

    static {
        addBouncyCastleProvider();
    }

    /**
     * Prints a one-line summary of a derived key &mdash; algorithm / provider label, password /
     * placeholder, key byte length, and Base64-encoded first / last four characters &mdash; to
     * {@link System#out}.
     *
     * @param label    the algorithm (or algorithm / provider) label.
     * @param password the password (or {@code "(raw)"} placeholder for raw-byte specs).
     * @param key      the derived key bytes.
     */
    private static void printf(final String label, final String password, final byte[] key) {
        final var encoded = Base64.getEncoder().encodeToString(key);
        System.out.printf("%-32s [%-10s] (%4d) %s...%s%n",
                          label, password,
                          key.length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * A nested test class for the symmetric-direct {@link SecretKeyFactory} algorithms listed on
     * the JDK 26 standard names page ({@code AES}, {@code ARCFOUR}, {@code ChaCha20}, {@code DES},
     * {@code DESede}). {@code Generic} (PKCS#11-only) is omitted.
     */
    @DisplayName("symmetric direct")
    @Nested
    class Symmetric_Test {

        /**
         * Verifies that {@code AES} generates byte-identical keys from the same
         * {@link SecretKeySpec}.
         *
         * @throws Exception if any error occurs.
         */
        @DisplayName("AES")
        @Test
        void __AES() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var factory = SecretKeyFactory.getInstance("AES", BOUNCY_CASTLE_PROVIDER_NAME);
            final var keyBytes = new byte[32];
            ThreadLocalRandom.current().nextBytes(keyBytes);
            final var spec = new SecretKeySpec(keyBytes, "AES");
            // -------------------------------------------------------------------------------- when
            final var key1 = factory.generateSecret(spec).getEncoded();
            final var key2 = factory.generateSecret(spec).getEncoded();
            // -------------------------------------------------------------------------------- then
            printf("AES/BC", "(raw)", key1);
            assertArrayEquals(key1, key2);
        }

        /**
         * Verifies that {@code ARCFOUR} generates byte-identical keys from the same
         * {@link SecretKeySpec}.
         *
         * @throws Exception if any error occurs.
         */
        @Disabled
        @DisplayName("ARCFOUR")
        @Test
        void __ARCFOUR() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var factory = SecretKeyFactory.getInstance(
                    "ARCFOUR", BOUNCY_CASTLE_PROVIDER_NAME);
            final var keyBytes = new byte[16];
            ThreadLocalRandom.current().nextBytes(keyBytes);
            final var spec = new SecretKeySpec(keyBytes, "ARCFOUR");
            // -------------------------------------------------------------------------------- when
            final var key1 = factory.generateSecret(spec).getEncoded();
            final var key2 = factory.generateSecret(spec).getEncoded();
            // -------------------------------------------------------------------------------- then
            printf("ARCFOUR/BC", "(raw)", key1);
            assertArrayEquals(key1, key2);
        }

        /**
         * Verifies that {@code ChaCha20} generates byte-identical keys from the same
         * {@link SecretKeySpec}.
         *
         * @throws Exception if any error occurs.
         */
        @Disabled
        @DisplayName("ChaCha20")
        @Test
        void __ChaCha20() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var factory = SecretKeyFactory.getInstance(
                    "ChaCha20", BOUNCY_CASTLE_PROVIDER_NAME);
            final var keyBytes = new byte[32];
            ThreadLocalRandom.current().nextBytes(keyBytes);
            final var spec = new SecretKeySpec(keyBytes, "ChaCha20");
            // -------------------------------------------------------------------------------- when
            final var key1 = factory.generateSecret(spec).getEncoded();
            final var key2 = factory.generateSecret(spec).getEncoded();
            // -------------------------------------------------------------------------------- then
            printf("ChaCha20/BC", "(raw)", key1);
            assertArrayEquals(key1, key2);
        }

        /**
         * Verifies that {@code DES} generates byte-identical keys from the same
         * {@link DESKeySpec}.
         *
         * @throws Exception if any error occurs.
         */
        @DisplayName("DES")
        @Test
        void __DES() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var factory = SecretKeyFactory.getInstance("DES", BOUNCY_CASTLE_PROVIDER_NAME);
            final var keyBytes = new byte[8];
            ThreadLocalRandom.current().nextBytes(keyBytes);
            final var spec = new DESKeySpec(keyBytes);
            // -------------------------------------------------------------------------------- when
            final var key1 = factory.generateSecret(spec).getEncoded();
            final var key2 = factory.generateSecret(spec).getEncoded();
            // -------------------------------------------------------------------------------- then
            printf("DES/BC", "(raw)", key1);
            assertArrayEquals(key1, key2);
        }

        /**
         * Verifies that {@code DESede} generates byte-identical keys from the same
         * {@link DESedeKeySpec}.
         *
         * @throws Exception if any error occurs.
         */
        @DisplayName("DESede")
        @Test
        void __DESede() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var factory = SecretKeyFactory.getInstance("DESede", BOUNCY_CASTLE_PROVIDER_NAME);
            final var keyBytes = new byte[24];
            ThreadLocalRandom.current().nextBytes(keyBytes);
            final var spec = new DESedeKeySpec(keyBytes);
            // -------------------------------------------------------------------------------- when
            final var key1 = factory.generateSecret(spec).getEncoded();
            final var key2 = factory.generateSecret(spec).getEncoded();
            // -------------------------------------------------------------------------------- then
            printf("DESede/BC", "(raw)", key1);
            assertArrayEquals(key1, key2);
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * A nested test class for the password-based encryption (PBE) template families listed on the
     * JDK 26 standard names page &mdash;
     * {@link Legacy_Test PBEWith&lt;digest&gt;And&lt;encryption&gt;} and
     * {@link HmacAndAes_Test PBEWith&lt;prf&gt;And&lt;encryption&gt;}.
     */
    @DisplayName("PBE")
    @Nested
    class PBE_Test {

        /**
         * A nested test class iterating over common instantiations of the
         * {@code PBEWith<digest>And<encryption>} template ({@code PBEWithMD5AndDES} +
         * {@code PBEWithMD5AndTripleDES} + {@code PBEWithSHA1AndDESede} +
         * {@code PBEWithSHA1AndRC2_*} + {@code PBEWithSHA1AndRC4_*}) via BouncyCastle.
         */
        @Disabled("BC registers only PBEWithMD5AndDES under the JCE-standard"
                  + " PBEWith<digest>And<encryption> family; everything else"
                  + " (PBEWithMD5AndTripleDES, PBEWithSHA1AndDESede, PBEWithSHA1AndRC2_*,"
                  + " PBEWithSHA1AndRC4_*) is either SunJCE-only or registered by BC under"
                  + " BC-specific names like PBEWITHSHAAND3-KEYTRIPLEDES-CBC")
        @DisplayName("PBEWith<digest>And<encryption>")
        @Nested
        class Legacy_Test {

            /**
             * Verifies that the given legacy PBE {@code algorithm} generates byte-identical keys
             * from the same {@link PBEKeySpec}.
             *
             * @param algorithm the JCE algorithm name.
             * @throws Exception if any error occurs.
             */
            @DisplayName("legacy PBE")
            @ValueSource(strings = {
                    "PBEWithMD5AndDES",
                    "PBEWithMD5AndTripleDES",
                    "PBEWithSHA1AndDESede",
                    "PBEWithSHA1AndRC2_40",
                    "PBEWithSHA1AndRC2_128",
                    "PBEWithSHA1AndRC4_40",
                    "PBEWithSHA1AndRC4_128"
            })
            @ParameterizedTest
            void __(final String algorithm) throws Exception {
                // --------------------------------------------------------------------------- given
                final var factory = SecretKeyFactory.getInstance(
                        algorithm, BOUNCY_CASTLE_PROVIDER_NAME);
                final var password = "iloveyou".toCharArray();
                // ---------------------------------------------------------------------------- when
                final var spec = new PBEKeySpec(password);
                final var key1 = factory.generateSecret(spec).getEncoded();
                final var key2 = factory.generateSecret(spec).getEncoded();
                // ---------------------------------------------------------------------------- then
                printf(algorithm + "/BC", "iloveyou", key1);
                assertArrayEquals(key1, key2);
            }
        }

        /**
         * A nested test class iterating over common instantiations of the
         * {@code PBEWith<prf>And<encryption>} template &mdash; the
         * {@code PBEWithHmacSHA{1|224|256|384|512}AndAES_{128|256}} variants &mdash; via
         * BouncyCastle.
         */
        @Disabled("BC does NOT register the JCE-standard PBEWithHmacSHA*AndAES_{128|256} names."
                  + " These are SunJCE-only; BC has its own variants under different names"
                  + " (e.g., PBEWITHSHAAND128BITAES-CBC-BC).")
        @DisplayName("PBEWith<prf>And<encryption>")
        @Nested
        class HmacAndAes_Test {

            /**
             * Verifies that the given PBE+HMAC+AES {@code algorithm} generates byte-identical keys
             * from the same {@link PBEKeySpec}.
             *
             * @param algorithm the JCE algorithm name.
             * @throws Exception if any error occurs.
             */
            @DisplayName("PBE+Hmac+AES")
            @ValueSource(strings = {
                    "PBEWithHmacSHA1AndAES_128",
                    "PBEWithHmacSHA1AndAES_256",
                    "PBEWithHmacSHA224AndAES_128",
                    "PBEWithHmacSHA224AndAES_256",
                    "PBEWithHmacSHA256AndAES_128",
                    "PBEWithHmacSHA256AndAES_256",
                    "PBEWithHmacSHA384AndAES_128",
                    "PBEWithHmacSHA384AndAES_256",
                    "PBEWithHmacSHA512AndAES_128",
                    "PBEWithHmacSHA512AndAES_256"
            })
            @ParameterizedTest
            void __(final String algorithm) throws Exception {
                // --------------------------------------------------------------------------- given
                final var factory = SecretKeyFactory.getInstance(
                        algorithm, BOUNCY_CASTLE_PROVIDER_NAME);
                final var password = "iloveyou".toCharArray();
                // ---------------------------------------------------------------------------- when
                final var spec = new PBEKeySpec(password);
                final var key1 = factory.generateSecret(spec).getEncoded();
                final var key2 = factory.generateSecret(spec).getEncoded();
                // ---------------------------------------------------------------------------- then
                printf(algorithm + "/BC", "iloveyou", key1);
                assertArrayEquals(key1, key2);
            }
        }
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * A nested test class iterating over common instantiations of the JDK 26 standard names
     * {@code PBKDF2With<prf>} template ({@code PBKDF2WithHmacSHA1} / {@code SHA224} /
     * {@code SHA256} / {@code SHA384} / {@code SHA512}) via BouncyCastle.
     */
    @DisplayName("PBKDF2With<prf>")
    @Nested
    class PBKDF2_Test {

        // 16 bytes — OWASP recommendation; RFC 8018 requires ≥8
        private static final int SALT_BYTES = 16;

        // 32 bytes (256 bits) — matches HMAC-SHA256 native output
        private static final int HASH_BYTES = 32;

        // 100,000 — light for an exploration test; OWASP 2023 recommends ≥600,000 for real password
        // storage
        private static final int ITERATION_COUNT = 100_000;

        /**
         * Verifies that the given {@code PBKDF2WithHmac*} {@code algorithm} derives a deterministic
         * hash from the same {@link PBEKeySpec}.
         *
         * @param algorithm the JCE algorithm name.
         * @throws Exception if any error occurs.
         */
        @DisplayName("PBKDF2WithHmac*")
        @ValueSource(strings = {
                "PBKDF2WithHmacSHA1",
                "PBKDF2WithHmacSHA224",
                "PBKDF2WithHmacSHA256",
                "PBKDF2WithHmacSHA384",
                "PBKDF2WithHmacSHA512"
        })
        @ParameterizedTest
        void __(final String algorithm) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var factory = SecretKeyFactory.getInstance(
                    algorithm, BOUNCY_CASTLE_PROVIDER_NAME);
            final var password = "iloveyou".toCharArray();
            final var salt = new byte[SALT_BYTES];
            ThreadLocalRandom.current().nextBytes(salt);
            // -------------------------------------------------------------------------------- when
            final var spec = new PBEKeySpec(password, salt, ITERATION_COUNT, HASH_BYTES << 3);
            final var hash1 = factory.generateSecret(spec).getEncoded();
            final var hash2 = factory.generateSecret(spec).getEncoded();
            // -------------------------------------------------------------------------------- then
            printf(algorithm + "/BC", "iloveyou", hash1);
            assertEquals(HASH_BYTES, hash1.length);
            assertArrayEquals(hash1, hash2);
        }
    }
}
