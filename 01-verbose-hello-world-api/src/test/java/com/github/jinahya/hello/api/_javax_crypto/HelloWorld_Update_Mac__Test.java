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

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Update_Mac__Test
        extends HelloWorldTest {

    @BeforeAll
    static void registerProviders() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        Password4jProvider.enable();
    }

    private static void printf(final String algorithm, final Object parameter, final byte[] tag) {
        final var encoded = Base64.getEncoder().encodeToString(tag);
        System.out.printf("%30s %20s (%4d) %s...%s%n", algorithm,
                          Optional.ofNullable(parameter).orElse(""),
                          tag.length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    // ---------------------------------------------------------------------------------------------
    @BeforeEach
    void __() {
        Mockito.doAnswer(i -> {
            final var mac = i.getArgument(0, Mac.class);
            mac.update(HelloWorldTestUtils.hello_world_byte_array());
            return mac;
        }).when(service()).update(ArgumentMatchers.<Mac>notNull());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("HmacSHA1")
    @Nested
    class HmacSha1_Test {

        private static final String ALGORITHM = "HmacSHA1";

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

    @DisplayName("HmacSHA256")
    @Nested
    class HmacSHA256_Test {

        private static final String ALGORITHM = "HmacSHA256";

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

    @DisplayName("PBEWithHmacSHA256")
    @Nested
    class PBEWithHmacSHA256_Test {

        private static final String ALGORITHM = "PBEWithHmacSHA256";

        private static final String KEY_FACTORY_ALGORITHM = ALGORITHM + "AndAES_256";

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

    @DisplayName("PBKDF2WithHmacSHA256 (salt+hash packed into a fixed-length column)")
    @Nested
    class PBKDF2_Test {

        private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

        private static final int SALT_BYTES = 16;

        private static final int HASH_BYTES = 32;  // 256 bits

        private static final int ITERATION_COUNT = 1_000_000;

        private static final int COLUMN_BYTES = SALT_BYTES + HASH_BYTES;  // 48-byte record

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

    @DisplayName("SCRYPT/BC (salt+hash packed into a fixed-length column)")
    @Nested
    class Scrypt_Test {

        @Nested
        class BouncyCastle_Test {

            private static final String ALGORITHM = "SCRYPT";

            private static final int SALT_BYTES = 16;

            private static final int HASH_BYTES = 32;

            private static final int COLUMN_BYTES = SALT_BYTES + HASH_BYTES;

            // OWASP-style scrypt parameters
            private static final int N = 1 << 17;  // 131_072 — CPU/memory cost

            private static final int r = 8;        // block size

            private static final int p = 1;        // parallelization

            @ValueSource(strings = {
                    "iloveyou",
                    "letmein"
            })
            @ParameterizedTest
            void __(final String password) throws Exception {
                // ------------------------------------------------------------------------------- given
                final var factory = SecretKeyFactory.getInstance(
                        ALGORITHM, BouncyCastleProvider.PROVIDER_NAME);
                // -------------------------------------------------------------------------------------
                final byte[] column;
                // ------------------------------------------------------------------------------ signup
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
                // ------------------------------------------------------------------------------- login
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

    @DisplayName("Argon2id/BC (salt+hash packed into a fixed-length column)")
    @Nested
    class Argon2id_Test {

        @Nested
        class BouncyCastle_Test {

            private static final int SALT_BYTES = 16;

            private static final int HASH_BYTES = 32;

            private static final int COLUMN_BYTES = SALT_BYTES + HASH_BYTES;

            // OWASP-style Argon2id parameters
            private static final int MEMORY_KB = 64 * 1024;  // 64 MiB

            private static final int ITERATIONS = 3;

            private static final int PARALLELISM = 4;

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

            @ValueSource(strings = {
                    "iloveyou",
                    "letmein"
            })
            @ParameterizedTest
            void __(final String password) {
                // -------------------------------------------------------------------------------------
                final byte[] column;
                // ------------------------------------------------------------------------------ signup
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
                // ------------------------------------------------------------------------------- login
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

    @DisplayName("argon2/Password4j-JCA (salt+hash packed into a fixed-length column)")
    @Nested
    class Argon2id_Password4J_Test {

        @Nested
        class Password4J_Test {

            private static final String ALGORITHM = "argon2";

            private static final int SALT_BYTES = 16;

            private static final int HASH_BYTES = 32;

            private static final int COLUMN_BYTES = SALT_BYTES + HASH_BYTES;

            // OWASP-style Argon2id parameters
            private static final int MEMORY_KB = 64 * 1024;  // 64 MiB

            private static final int ITERATIONS = 3;

            private static final int PARALLELISM = 4;

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
                    final var elapsed = Duration.ofNanos(System.nanoTime() - start);
                    log.debug("argon2id (JCA) signup [{}] {}", password, elapsed);
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
                    final var start = System.nanoTime();
                    final var hash = factory.generateSecret(spec).getEncoded();
                    final var elapsed = Duration.ofNanos(System.nanoTime() - start);
                    log.debug("argon2id (JCA) login  [{}] {}", password, elapsed);
                    final var attempt = new byte[COLUMN_BYTES];
                    System.arraycopy(salt, 0, attempt, 0, SALT_BYTES);
                    System.arraycopy(hash, 0, attempt, SALT_BYTES, HASH_BYTES);
                    Assertions.assertArrayEquals(column, attempt);
                }
            }
        }
    }
}
