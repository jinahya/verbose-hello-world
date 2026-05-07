package com.github.jinahya.hello;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A test class demonstrating password hashing for credential storage using {@code PBKDF2},
 * {@code scrypt}, and {@code Argon2id}: each test signs up a password by deriving a hash, packs
 * {@code salt | hash} into a fixed-length record (the "column"), and at login re-derives the hash
 * with the stored salt to assert the column is reproduced byte-for-byte.
 *
 * <p>This is <em>password hashing for storage</em> (signup/login round-trip), not key derivation
 * for encryption. The function used must be deliberately slow to resist brute-force attacks; each
 * nested class picks parameters following OWASP recommendations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc8018">RFC 8018 &mdash; PKCS #5:
 * Password-Based Cryptography Specification Version 2.1 (PBKDF2)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7914">RFC 7914 &mdash; The scrypt
 * Password-Based Key Derivation Function</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9106">RFC 9106 &mdash; Argon2 Memory-Hard
 * Function for Password Hashing and Proof-of-Work Applications</a>
 */
@DisplayName("Password hashing — PBKDF2 / scrypt / Argon2id (signup/login round-trip)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class _Javax_Crypto_PBE__Test {

    /**
     * Registers the {@link org.bouncycastle.jce.provider.BouncyCastleProvider BouncyCastle} and
     * {@link com.password4j.jca.providers.Password4jProvider Password4j} providers with the JCA so
     * {@code SCRYPT/BC}, {@code argon2}, and other algorithms used by the nested test classes
     * resolve by their standard JCE names.
     */
    @BeforeAll
    static void registerProviders() {
        if (Security.getProvider(org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME)
            == null) {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        }
        com.password4j.jca.providers.Password4jProvider.enable();
    }

    /**
     * A nested test class demonstrating password storage with PBKDF2-HMAC-SHA256: derives a 32-byte
     * hash from a passphrase, packs {@code salt | hash} into a 48-byte fixed-length record (the
     * "column"), and at login re-derives the hash with the stored salt to assert the column is
     * reproduced byte-for-byte.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc8018#section-5.2">RFC 8018, &sect;5.2
     * &mdash; PBKDF2</a>
     */
    @DisplayName("PBKDF2WithHmacSHA256 (salt+hash packed into a fixed-length column)")
    @Nested
    class PBKDF2_Test {

        // JCE PBKDF2 name (RFC 8018 §5.2)
        private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

        // 16 bytes — OWASP recommendation; RFC 8018 requires ≥8
        private static final int SALT_BYTES = 16;

        // 32 bytes (256 bits) — matches HMAC-SHA256 native output
        private static final int HASH_BYTES = 32;

        // 1,000,000 — conservative; OWASP 2023 recommends ≥600,000
        private static final int ITERATION_COUNT = 1_000_000;

        // derived: salt | hash → 48-byte fixed record
        private static final int COLUMN_BYTES = SALT_BYTES + HASH_BYTES;

        /**
         * Verifies that signing up the given {@code password} with PBKDF2-HMAC-SHA256 and packing
         * {@code salt | hash} into a single 48-byte record yields a byte array that is reproduced
         * exactly during login by re-deriving with the stored salt.
         *
         * @param password the password to register and verify.
         */
        @ValueSource(strings = {
                "iloveyou",
                "letmein"
        })
        @ParameterizedTest
        void __(final String password) throws Exception {
            // ------------------------------------------------------------------------------- given
            final var factory = SecretKeyFactory.getInstance(ALGORITHM);
            // -------------------------------------------------------------------------------------
            final byte[] column;
            // ------------------------------------------------------------------------------ signup
            {
                final var salt = new byte[SALT_BYTES];
                ThreadLocalRandom.current().nextBytes(salt);
                final var spec = new PBEKeySpec(
                        password.toCharArray(), salt, ITERATION_COUNT, HASH_BYTES << 3);
                final var start = System.nanoTime();
                final var hash = factory.generateSecret(spec).getEncoded();
                log.debug("elapsed: {}", Duration.ofNanos(System.nanoTime() - start));
                column = new byte[COLUMN_BYTES];
                System.arraycopy(salt, 0, column, 0, SALT_BYTES);
                System.arraycopy(hash, 0, column, SALT_BYTES, HASH_BYTES);
            }
            // ------------------------------------------------------------------------------- login
            {
                final var salt = Arrays.copyOfRange(column, 0, SALT_BYTES);
                final var spec = new PBEKeySpec(
                        password.toCharArray(), salt, ITERATION_COUNT, HASH_BYTES << 3);
                final var hash = factory.generateSecret(spec).getEncoded();
                final var attempt = new byte[COLUMN_BYTES];
                System.arraycopy(salt, 0, attempt, 0, SALT_BYTES);
                System.arraycopy(hash, 0, attempt, SALT_BYTES, HASH_BYTES);
                Assertions.assertArrayEquals(column, attempt);
            }
        }
    }

    /**
     * A nested test class demonstrating password storage with scrypt: derives a 32-byte hash, packs
     * {@code salt | hash} into a 48-byte fixed-length record, and re-derives at login. Memory-hard:
     * each derivation needs ~128 MiB of fast RAM, defeating GPU/ASIC attacks.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc7914">RFC 7914 &mdash; The scrypt
     * Password-Based Key Derivation Function</a>
     */
    @DisplayName("SCRYPT (salt+hash packed into a fixed-length column)")
    @Nested
    class Scrypt_Test {

        // 16 bytes — OWASP recommendation; RFC 7914 doesn't mandate
        private static final int SALT_BYTES = 16;

        // 32 bytes (256 bits) — picked; matches AES-256 sizing
        private static final int HASH_BYTES = 32;

        // derived: salt | hash → 48-byte fixed record
        private static final int COLUMN_BYTES = SALT_BYTES + HASH_BYTES;

        // RFC 7914 §2 parameter names: N (CPU/memory cost), r (block size), p (parallelization)
        // 131,072 — OWASP "interactive login" profile
        private static final int N = 1 << 17;

        // 8 — RFC 7914 §6 example value; common standard
        private static final int r = 8;

        // 1 — RFC 7914 §6 example value; common standard
        private static final int p = 1;

        /**
         * A nested test class running scrypt via the
         * {@link com.password4j.jca.providers.Password4jProvider Password4j} JCA provider, using
         * {@link SecretKeyFactory#getInstance(String) SecretKeyFactory} for the lowercase algorithm
         * name {@code "scrypt"} together with {@link com.password4j.jca.spec.ScryptKeySpec}.
         */
        @DisplayName("Password4j-JCA")
        @Nested
        class Password4J_Test {

            // Password4j-JCA registered name (lowercase)
            private static final String ALGORITHM = "scrypt";

            // Password4j-JCA provider name (set by com.password4j.jca.providers.Password4jProvider's constructor)
            private static final String PROVIDER = "Password4j";

            /**
             * Verifies that signing up the given {@code password} with scrypt (via the
             * Password4j-JCA {@link SecretKeyFactory}) and packing {@code salt | hash} into a
             * 48-byte record yields a byte array that is reproduced exactly during login by
             * re-deriving with the stored salt.
             *
             * @param password the password to register and verify.
             */
            @ValueSource(strings = {
                    "iloveyou",
                    "letmein"
            })
            @ParameterizedTest
            void __(final String password) throws Exception {
                // --------------------------------------------------------------------------- given
                final var factory = SecretKeyFactory.getInstance(ALGORITHM, PROVIDER);
                // ---------------------------------------------------------------------------------
                final byte[] column;
                // -------------------------------------------------------------------------- signup
                {
                    final var salt = new byte[SALT_BYTES];
                    ThreadLocalRandom.current().nextBytes(salt);
                    final var spec = new com.password4j.jca.spec.ScryptKeySpec(
                            password.toCharArray(), salt, N, r, p, HASH_BYTES);
                    final var start = System.nanoTime();
                    final var hash = factory.generateSecret(spec).getEncoded();
                    log.debug("elapsed: {}", Duration.ofNanos(System.nanoTime() - start));
                    column = new byte[COLUMN_BYTES];
                    System.arraycopy(salt, 0, column, 0, SALT_BYTES);
                    System.arraycopy(hash, 0, column, SALT_BYTES, HASH_BYTES);
                }
                // --------------------------------------------------------------------------- login
                {
                    final var salt = Arrays.copyOfRange(column, 0, SALT_BYTES);
                    final var spec = new com.password4j.jca.spec.ScryptKeySpec(
                            password.toCharArray(), salt, N, r, p, HASH_BYTES);
                    final var hash = factory.generateSecret(spec).getEncoded();
                    final var attempt = new byte[COLUMN_BYTES];
                    System.arraycopy(salt, 0, attempt, 0, SALT_BYTES);
                    System.arraycopy(hash, 0, attempt, SALT_BYTES, HASH_BYTES);
                    Assertions.assertArrayEquals(column, attempt);
                }
            }
        }

        /**
         * A nested test class running scrypt via the BouncyCastle JCE provider, using
         * {@link SecretKeyFactory#getInstance(String, String)} with {@code "SCRYPT"} and
         * {@link org.bouncycastle.jcajce.spec.ScryptKeySpec}.
         */
        @DisplayName("BouncyCastle JCE")
        @Nested
        class BouncyCastle_Test {

            // BC registered name (uppercase)
            private static final String ALGORITHM = "SCRYPT";

            /**
             * Verifies that signing up the given {@code password} with scrypt (via the BC
             * {@link SecretKeyFactory}) and packing {@code salt | hash} into a 48-byte record
             * yields a byte array that is reproduced exactly during login by re-deriving with the
             * stored salt.
             *
             * @param password the password to register and verify.
             */
            @ValueSource(strings = {
                    "iloveyou",
                    "letmein"
            })
            @ParameterizedTest
            void __(final String password) throws Exception {
                // --------------------------------------------------------------------------- given
                final var factory = SecretKeyFactory.getInstance(
                        ALGORITHM,
                        org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME);
                // ---------------------------------------------------------------------------------
                final byte[] column;
                // -------------------------------------------------------------------------- signup
                {
                    final var salt = new byte[SALT_BYTES];
                    ThreadLocalRandom.current().nextBytes(salt);
                    final var spec = new org.bouncycastle.jcajce.spec.ScryptKeySpec(
                            password.toCharArray(), salt, N, r, p, HASH_BYTES << 3);
                    final var start = System.nanoTime();
                    final var hash = factory.generateSecret(spec).getEncoded();
                    log.debug("elapsed: {}", Duration.ofNanos(System.nanoTime() - start));
                    column = new byte[COLUMN_BYTES];
                    System.arraycopy(salt, 0, column, 0, SALT_BYTES);
                    System.arraycopy(hash, 0, column, SALT_BYTES, HASH_BYTES);
                }
                // --------------------------------------------------------------------------- login
                {
                    final var salt = Arrays.copyOfRange(column, 0, SALT_BYTES);
                    final var spec = new org.bouncycastle.jcajce.spec.ScryptKeySpec(
                            password.toCharArray(), salt, N, r, p, HASH_BYTES << 3);
                    final var hash = factory.generateSecret(spec).getEncoded();
                    final var attempt = new byte[COLUMN_BYTES];
                    System.arraycopy(salt, 0, attempt, 0, SALT_BYTES);
                    System.arraycopy(hash, 0, attempt, SALT_BYTES, HASH_BYTES);
                    Assertions.assertArrayEquals(column, attempt);
                }
            }
        }
    }

    /**
     * A nested test class demonstrating password storage with Argon2id, run twice through two
     * different APIs (Password4j-JCA and BouncyCastle low-level) using identical parameters so the
     * resulting columns are byte-for-byte equivalent.
     *
     * <p>The constants ({@code SALT_BYTES}, {@code HASH_BYTES}, {@code MEMORY_KB},
     * {@code ITERATIONS}, {@code PARALLELISM}) are not mandated by the spec; they are tunable
     * parameters set to OWASP-recommended values and shared across the two paths.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc9106">RFC 9106 &mdash; Argon2
     * Memory-Hard Function for Password Hashing</a>
     */
    @DisplayName("Argon2id (salt+hash packed into a fixed-length column)")
    @Nested
    class Argon2id_Test {

        // 16 bytes — OWASP recommendation; RFC 9106 requires ≥8
        private static final int SALT_BYTES = 16;

        // 32 bytes — picked; RFC 9106 allows 4..2^32-1
        private static final int HASH_BYTES = 32;

        // derived: salt | hash → 48-byte fixed record
        private static final int COLUMN_BYTES = SALT_BYTES + HASH_BYTES;

        // RFC 9106 §4 "second recommended option": m=64 MiB, t=3, p=4 — for memory-constrained envs
        // 65,536 KiB = 64 MiB — RFC 9106 §4 second option
        private static final int MEMORY_KB = 64 * 1024;

        // 3 passes — RFC 9106 §4 second option (paired with 64 MiB)
        private static final int ITERATIONS = 3;

        // 4 lanes — RFC 9106 §4 recommendation
        private static final int PARALLELISM = 4;

        /**
         * A nested test class running Argon2id via the
         * {@link com.password4j.jca.providers.Password4jProvider Password4j} JCA provider, using
         * the standard {@link SecretKeyFactory} API with
         * {@link com.password4j.jca.spec.Argon2KeySpec}. This is the only mainstream way to call
         * Argon2id through the JCE today; SunJCE does not (yet) ship Argon2.
         */
        @DisplayName("Password4j-JCA")
        @Nested
        class Password4J_Test {

            // Password4j-JCA registered name (lowercase)
            private static final String ALGORITHM = "argon2";

            // Password4j-JCA provider name (set by com.password4j.jca.providers.Password4jProvider's constructor)
            private static final String PROVIDER = "Password4j";

            /**
             * Verifies that signing up the given {@code password} with Argon2id (via the
             * Password4j-JCA {@link SecretKeyFactory}) and packing {@code salt | hash} into a
             * 48-byte record yields a byte array that is reproduced exactly during login by
             * re-deriving with the stored salt.
             *
             * @param password the password to register and verify.
             */
            @ValueSource(strings = {
                    "iloveyou",
                    "letmein"
            })
            @ParameterizedTest
            void __(final String password) throws Exception {
                // ------------------------------------------------------------------------------- given
                final var factory = SecretKeyFactory.getInstance(ALGORITHM, PROVIDER);
                // -------------------------------------------------------------------------------------
                final byte[] column;
                // ------------------------------------------------------------------------------ signup
                {
                    final var salt = new byte[SALT_BYTES];
                    ThreadLocalRandom.current().nextBytes(salt);
                    final var spec = new com.password4j.jca.spec.Argon2KeySpec(
                            password.toCharArray(), salt, MEMORY_KB, ITERATIONS, PARALLELISM,
                            HASH_BYTES, com.password4j.types.Argon2.ID);
                    final var start = System.nanoTime();
                    final var hash = factory.generateSecret(spec).getEncoded();
                    log.debug("elapsed: {}", Duration.ofNanos(System.nanoTime() - start));
                    column = new byte[COLUMN_BYTES];
                    System.arraycopy(salt, 0, column, 0, SALT_BYTES);
                    System.arraycopy(hash, 0, column, SALT_BYTES, HASH_BYTES);
                }
                // ------------------------------------------------------------------------------- login
                {
                    final var salt = Arrays.copyOfRange(column, 0, SALT_BYTES);
                    final var spec = new com.password4j.jca.spec.Argon2KeySpec(
                            password.toCharArray(), salt, MEMORY_KB, ITERATIONS, PARALLELISM,
                            HASH_BYTES, com.password4j.types.Argon2.ID);
                    final var hash = factory.generateSecret(spec).getEncoded();
                    final var attempt = new byte[COLUMN_BYTES];
                    System.arraycopy(salt, 0, attempt, 0, SALT_BYTES);
                    System.arraycopy(hash, 0, attempt, SALT_BYTES, HASH_BYTES);
                    Assertions.assertArrayEquals(column, attempt);
                }
            }
        }

        /**
         * A nested test class running Argon2id via the BouncyCastle low-level API
         * ({@link org.bouncycastle.crypto.generators.Argon2BytesGenerator} +
         * {@link org.bouncycastle.crypto.params.Argon2Parameters}). BouncyCastle does not register
         * a JCE {@link SecretKeyFactory} for Argon2, so this is the canonical idiom for using BC's
         * Argon2 implementation.
         */
        @DisplayName("BouncyCastle (low-level)")
        @Nested
        class BouncyCastle_Test {

            /**
             * Derives a {@value Argon2id_Test#HASH_BYTES}-byte Argon2id hash from the given
             * password and salt, using
             * {@link org.bouncycastle.crypto.generators.Argon2BytesGenerator} configured with the
             * enclosing class's memory, iteration, and parallelism parameters.
             *
             * @param password the password.
             * @param salt     the salt.
             * @return the derived hash.
             */
            private static byte[] derive(final char[] password, final byte[] salt) {
                final var params = new org.bouncycastle.crypto.params.Argon2Parameters.Builder(
                        org.bouncycastle.crypto.params.Argon2Parameters.ARGON2_id)
                        .withVersion(
                                org.bouncycastle.crypto.params.Argon2Parameters.ARGON2_VERSION_13)
                        .withSalt(salt)
                        .withMemoryAsKB(MEMORY_KB)
                        .withIterations(ITERATIONS)
                        .withParallelism(PARALLELISM)
                        .build();
                final var generator = new org.bouncycastle.crypto.generators.Argon2BytesGenerator();
                generator.init(params);
                final var out = new byte[HASH_BYTES];
                generator.generateBytes(
                        new String(password).getBytes(StandardCharsets.UTF_8), out);
                return out;
            }

            /**
             * Verifies that signing up the given {@code password} with Argon2id (via the
             * BouncyCastle low-level
             * {@link org.bouncycastle.crypto.generators.Argon2BytesGenerator}) and packing
             * {@code salt | hash} into a 48-byte record yields a byte array that is reproduced
             * exactly during login by re-deriving with the stored salt.
             *
             * @param password the password to register and verify.
             */
            @ValueSource(strings = {
                    "iloveyou",
                    "letmein"
            })
            @ParameterizedTest
            void __(final String password) {
                // --------------------------------------------------------------------------- given
                // ---------------------------------------------------------------------------------
                final byte[] column;
                // -------------------------------------------------------------------------- signup
                {
                    final var salt = new byte[SALT_BYTES];
                    ThreadLocalRandom.current().nextBytes(salt);
                    final var start = System.nanoTime();
                    final var hash = derive(password.toCharArray(), salt);
                    log.debug("elapsed: {}", Duration.ofNanos(System.nanoTime() - start));
                    column = new byte[COLUMN_BYTES];
                    System.arraycopy(salt, 0, column, 0, SALT_BYTES);
                    System.arraycopy(hash, 0, column, SALT_BYTES, HASH_BYTES);
                }
                // --------------------------------------------------------------------------- login
                {
                    final var salt = Arrays.copyOfRange(column, 0, SALT_BYTES);
                    final var hash = derive(password.toCharArray(), salt);
                    final var attempt = new byte[COLUMN_BYTES];
                    System.arraycopy(salt, 0, attempt, 0, SALT_BYTES);
                    System.arraycopy(hash, 0, attempt, SALT_BYTES, HASH_BYTES);
                    Assertions.assertArrayEquals(column, attempt);
                }
            }
        }
    }
}
