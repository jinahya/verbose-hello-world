package com.github.jinahya.hello.api._javax_crypto;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.password4j.jca.providers.Password4jProvider;
import com.password4j.jca.spec.Argon2KeySpec;
import com.password4j.types.Argon2;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An integration test class for
 * {@link com.github.jinahya.hello.api.HelloWorld#update(Mac) HelloWorld.update(mac)} method that
 * exercises real {@link Mac} algorithms (HMAC, PBE-MAC) and, additionally, demonstrates
 * password-based key-derivation flows (PBKDF2, scrypt, Argon2id) for credential storage and
 * verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see com.github.jinahya.hello.api.HelloWorld#update(Mac)
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc2104">RFC 2104 &mdash; HMAC: Keyed-Hashing
 * for Message Authentication</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc4231">RFC 4231 &mdash; Identifiers and
 * Test Vectors for HMAC-SHA-224, -256, -384, -512</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6234">RFC 6234 &mdash; US Secure Hash
 * Algorithms (SHA, SHA-based HMAC, HKDF)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc8018">RFC 8018 &mdash; PKCS #5:
 * Password-Based Cryptography Specification Version 2.1 (PBE, PBKDF2)</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc7914">RFC 7914 &mdash; The scrypt
 * Password-Based Key Derivation Function</a>
 * @see <a href="https://datatracker.ietf.org/doc/html/rfc9106">RFC 9106 &mdash; Argon2 Memory-Hard
 * Function for Password Hashing and Proof-of-Work Applications</a>
 */
@DisplayName("HelloWorld#update(Mac) — HMAC, PBE-MAC, and password-based KDFs")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Mac__Test
        extends HelloWorldTest {

    /**
     * Registers the {@link BouncyCastleProvider BouncyCastle} and
     * {@link Password4jProvider Password4j} providers with the JCA so {@code SCRYPT/BC},
     * {@code argon2}, and other algorithms used by the nested test classes resolve by their
     * standard JCE names.
     */
    @BeforeAll
    static void registerProviders() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        Password4jProvider.enable();
    }

    /**
     * Prints a one-line summary of a MAC tag or derived hash &mdash; algorithm, optional parameter,
     * byte length, and Base64-encoded first/last four characters &mdash; to {@link System#out}.
     *
     * @param algorithm the algorithm name to display.
     * @param parameter an optional parameter to display (may be {@code null}).
     * @param tag       the bytes to summarize.
     */
    private static void printf(final String algorithm, final Object parameter, final byte[] tag) {
        final var encoded = Base64.getEncoder().encodeToString(tag);
        System.out.printf("%30s %20s (%4d) %s...%s%n", algorithm,
                          Optional.ofNullable(parameter).orElse(""),
                          tag.length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    /**
     * Stubs {@link com.github.jinahya.hello.api.HelloWorld#update(Mac) service().update(mac)} so
     * that, when invoked with any non-{@code null} {@link Mac}, it forwards the
     * {@link HelloWorldTestUtils#hello_world_byte_array() hello-world bytes} to the mac's
     * {@link Mac#update(byte[])} method and returns the mac.
     */
    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        Mockito.doAnswer(i -> {
            final var mac = i.getArgument(0, Mac.class);
            mac.update(HelloWorldTestUtils.hello_world_byte_array());
            return mac;
        }).when(service()).update(ArgumentMatchers.<Mac>notNull());
    }

    /**
     * A nested test class for {@code HmacSHA1}: authenticates the hello-world bytes with one
     * {@link Mac} and recomputes the tag with another mac initialized with the same key, then
     * asserts the two tags are byte-identical.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2104">RFC 2104 &mdash; HMAC</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc6234">RFC 6234 &mdash; US Secure Hash
     * Algorithms</a>
     */
    // ---------------------------------------------------------------------------------------------
    @DisplayName("HmacSHA1")
    @Nested
    class HmacSha1_Test {

        // JCE std name (HMAC: RFC 2104; SHA-1: FIPS 180-4)
        private static final String ALGORITHM = "HmacSHA1";

        /**
         * Verifies that authenticating the hello-world bytes twice with two
         * {@link Mac#getInstance(String) HmacSHA1} instances initialized with the same key produces
         * byte-identical tags.
         */
        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyGenerator.getInstance(ALGORITHM);
            final var key = generator.generateKey();
            // -------------------------------------------------------------------------------------
            final byte[] tag1;
            // ------------------------------------------------------------------------ authenticate
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key);
                tag1 = service().update(mac).doFinal();
                printf(ALGORITHM, null, tag1);
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key);
                final var tag2 = service().update(mac).doFinal();
                Assertions.assertArrayEquals(tag1, tag2);
            }
        }
    }

    /**
     * A nested test class for {@code HmacSHA256}: authenticates the hello-world bytes with one
     * {@link Mac} and recomputes the tag with another mac initialized with the same key, then
     * asserts the two tags are byte-identical.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc2104">RFC 2104 &mdash; HMAC</a>
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc4231">RFC 4231 &mdash; HMAC-SHA Test
     * Vectors</a>
     */
    @DisplayName("HmacSHA256")
    @Nested
    class HmacSHA256_Test {

        // JCE std name (HMAC: RFC 2104; SHA-256: RFC 6234)
        private static final String ALGORITHM = "HmacSHA256";

        /**
         * Verifies that authenticating the hello-world bytes twice with two
         * {@link Mac#getInstance(String) HmacSHA256} instances initialized with the same key
         * produces byte-identical tags.
         */
        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var generator = KeyGenerator.getInstance(ALGORITHM);
            final var key = generator.generateKey();
            // -------------------------------------------------------------------------------------
            final byte[] tag1;
            // ------------------------------------------------------------------------ authenticate
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key);
                tag1 = service().update(mac).doFinal();
                printf(ALGORITHM, null, tag1);
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key);
                final var tag2 = service().update(mac).doFinal();
                Assertions.assertArrayEquals(tag1, tag2);
            }
        }
    }

    /**
     * A nested test class for {@code PBEWithHmacSHA256}: derives an HMAC key from a passphrase via
     * {@link SecretKeyFactory} (PKCS#5 PBE), authenticates the hello-world bytes twice with the
     * derived key, and asserts the tags match.
     *
     * @see <a href="https://datatracker.ietf.org/doc/html/rfc8018">RFC 8018 &mdash; PKCS #5
     * v2.1</a>
     */
    @DisplayName("PBEWithHmacSHA256")
    @Nested
    class PBEWithHmacSHA256_Test {

        // JCE PBE-MAC name (PKCS#5 v2.1 / RFC 8018)
        private static final String ALGORITHM = "PBEWithHmacSHA256";

        // JCE SecretKeyFactory name (PKCS#5 PBES2)
        private static final String KEY_FACTORY_ALGORITHM = ALGORITHM + "AndAES_256";

        /**
         * Verifies that authenticating the hello-world bytes twice with two
         * {@code PBEWithHmacSHA256} instances initialized with the same password-derived key and
         * parameters produces byte-identical tags.
         */
        @Test
        void __() throws Exception {
            // ------------------------------------------------------------------------------- given
            final var password = "password".toCharArray();
            final SecretKey key;
            {
                final var keySpec = new PBEKeySpec(password);
                final var factory = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
                key = factory.generateSecret(keySpec);
            }
            final var salt = new byte[16];
            {
                ThreadLocalRandom.current().nextBytes(salt);
            }
            final var iterationCount = 1000;
            final AlgorithmParameterSpec params;
            {
                params = new PBEParameterSpec(salt, iterationCount);
            }
            // -------------------------------------------------------------------------------------
            final byte[] tag1;
            // ------------------------------------------------------------------------ authenticate
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key, params);
                tag1 = service().update(mac).doFinal();
                printf(ALGORITHM, iterationCount, tag1);
            }
            // ------------------------------------------------------------------------------ verify
            {
                final var mac = Mac.getInstance(ALGORITHM);
                mac.init(key, params);
                final var tag2 = service().update(mac).doFinal();
                Assertions.assertArrayEquals(tag1, tag2);
            }
        }
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
         * A nested test class running scrypt via the {@link Password4jProvider Password4j} JCA
         * provider, using {@link SecretKeyFactory#getInstance(String) SecretKeyFactory} for the
         * lowercase algorithm name {@code "scrypt"} together with
         * {@link com.password4j.jca.spec.ScryptKeySpec}.
         */
        @DisplayName("Password4j-JCA")
        @Nested
        class Password4J_Test {

            // Password4j-JCA registered name (lowercase)
            private static final String ALGORITHM = "scrypt";

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
                final var factory = SecretKeyFactory.getInstance(ALGORITHM);
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
         * {@link ScryptKeySpec}.
         */
        @DisplayName("BouncyCastle JCE")
        @Nested
        class BouncyCastle_Test {

            // BC registered name (uppercase)
            private static final String ALGORITHM = "SCRYPT";

            /**
             * Verifies that signing up the given {@code password} with scrypt (via the BC
             * {@link SecretKeyFactory}) and packing {@code salt | hash} into a 48-byte record
             * yields a byte array that is reproduced exactly during login by re-deriving with
             * the stored salt.
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
                        ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
                // ---------------------------------------------------------------------------------
                final byte[] column;
                // -------------------------------------------------------------------------- signup
                {
                    final var salt = new byte[SALT_BYTES];
                    ThreadLocalRandom.current().nextBytes(salt);
                    final var spec = new ScryptKeySpec(
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
                    final var spec = new ScryptKeySpec(
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
         * A nested test class running Argon2id via the {@link Password4jProvider Password4j} JCA
         * provider, using the standard {@link SecretKeyFactory} API with {@link Argon2KeySpec}.
         * This is the only mainstream way to call Argon2id through the JCE today; SunJCE does not
         * (yet) ship Argon2.
         */
        @DisplayName("Password4j-JCA")
        @Nested
        class Password4J_Test {

            // Password4j-JCA registered name (lowercase)
            private static final String ALGORITHM = "argon2";

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
                final var factory = SecretKeyFactory.getInstance(ALGORITHM);
                // -------------------------------------------------------------------------------------
                final byte[] column;
                // ------------------------------------------------------------------------------ signup
                {
                    final var salt = new byte[SALT_BYTES];
                    ThreadLocalRandom.current().nextBytes(salt);
                    final var spec = new Argon2KeySpec(
                            password.toCharArray(), salt, MEMORY_KB, ITERATIONS, PARALLELISM,
                            HASH_BYTES, Argon2.ID);
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
                    final var spec = new Argon2KeySpec(
                            password.toCharArray(), salt, MEMORY_KB, ITERATIONS, PARALLELISM,
                            HASH_BYTES, Argon2.ID);
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
         * ({@link Argon2BytesGenerator} + {@link Argon2Parameters}). BouncyCastle does not register
         * a JCE {@link SecretKeyFactory} for Argon2, so this is the canonical idiom for using BC's
         * Argon2 implementation.
         */
        @DisplayName("BouncyCastle (low-level)")
        @Nested
        class BouncyCastle_Test {

            /**
             * Derives a {@value Argon2id_Test#HASH_BYTES}-byte Argon2id hash from the given
             * password and salt, using {@link Argon2BytesGenerator} configured with the enclosing
             * class's memory, iteration, and parallelism parameters.
             *
             * @param password the password.
             * @param salt     the salt.
             * @return the derived hash.
             */
            private static byte[] derive(final char[] password, final byte[] salt) {
                final var params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                        .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                        .withSalt(salt)
                        .withMemoryAsKB(MEMORY_KB)
                        .withIterations(ITERATIONS)
                        .withParallelism(PARALLELISM)
                        .build();
                final var generator = new Argon2BytesGenerator();
                generator.init(params);
                final var out = new byte[HASH_BYTES];
                generator.generateBytes(
                        new String(password).getBytes(StandardCharsets.UTF_8), out);
                return out;
            }

            /**
             * Verifies that signing up the given {@code password} with Argon2id (via the
             * BouncyCastle low-level {@link Argon2BytesGenerator}) and packing {@code salt | hash}
             * into a 48-byte record yields a byte array that is reproduced exactly during login by
             * re-deriving with the stored salt.
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
