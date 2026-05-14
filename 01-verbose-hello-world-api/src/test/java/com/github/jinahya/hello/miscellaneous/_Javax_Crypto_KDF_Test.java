package com.github.jinahya.hello.miscellaneous;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.KDF;
import javax.crypto.SecretKey;
import javax.crypto.spec.HKDFParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto_KDF_Test {

    private static Stream<String> algorithms() {
        return Stream.of(
                "HKDF-SHA256",
                "HKDF-SHA384",
                "HKDF-SHA512"
        );
    }

    /**
     * Prints a one-line summary of raw derived data &mdash; algorithm, provider, byte length, and
     * Base64-encoded first/last four characters &mdash; to {@link System#out}.
     *
     * @param algorithm    the KDF algorithm name.
     * @param providerName the provider that resolved the algorithm.
     * @param data         the derived bytes.
     */
    private static void printf(final String algorithm, final String providerName,
                               final byte[] data) {
        final var encoded = Base64.getEncoder().encodeToString(data);
        System.out.printf("%-12s (%-6s) (%4d) %s...%s%n",
                          algorithm, providerName,
                          data.length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    /**
     * Prints a one-line summary of a derived {@link SecretKey} &mdash; algorithm, provider, key
     * algorithm, byte length, and Base64-encoded first/last four characters &mdash; to
     * {@link System#out}.
     *
     * @param algorithm    the KDF algorithm name.
     * @param providerName the provider that resolved the algorithm.
     * @param key          the derived key.
     */
    private static void printf(final String algorithm, final String providerName,
                               final SecretKey key) {
        final var encoded = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.printf("%-12s (%-6s) keyAlg=%-4s (%4d) %s...%s%n",
                          algorithm, providerName,
                          key.getAlgorithm(),
                          key.getEncoded().length,
                          encoded.substring(0, 4),
                          encoded.substring(encoded.length() - 4));
    }

    // picked; HKDF accepts arbitrary IKM length (RFC 5869 §2.2)
    private static final int IKM_BYTES = 32;

    // picked; HKDF accepts arbitrary salt length (RFC 5869 §3.1)
    private static final int SALT_BYTES = 16;

    // picked; output length L bound by L ≤ 255 × HashLen (RFC 5869 §2.3); 32 = AES-256 / SHA-256
    private static final int OUTPUT_BYTES = 32;

    // picked; non-secret context label (RFC 5869 §3.2 — info is application-defined)
    private static final byte[] INFO = "hello, world".getBytes(StandardCharsets.UTF_8);

    // picked; algorithm name passed to KDF.deriveKey(String, AlgorithmParameterSpec)
    private static final String KEY_ALGORITHM = "AES";

    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __deriveData(final String algorithm) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var kdf = KDF.getInstance(algorithm);
        final var ikm = new byte[IKM_BYTES];
        ThreadLocalRandom.current().nextBytes(ikm);
        final var salt = new byte[SALT_BYTES];
        ThreadLocalRandom.current().nextBytes(salt);
        // ------------------------------------------------------------------------------------ when
        final byte[] out1;
        final byte[] out2;
        {
            final var spec = HKDFParameterSpec.ofExtract()
                    .addIKM(ikm)
                    .addSalt(salt)
                    .thenExpand(INFO, OUTPUT_BYTES);
            out1 = kdf.deriveData(spec);
        }
        {
            final var spec = HKDFParameterSpec.ofExtract()
                    .addIKM(ikm)
                    .addSalt(salt)
                    .thenExpand(INFO, OUTPUT_BYTES);
            out2 = kdf.deriveData(spec);
        }
        // ------------------------------------------------------------------------------------ then
        printf(algorithm, kdf.getProviderName(), out1);
        Assertions.assertEquals(OUTPUT_BYTES, out1.length);
        Assertions.assertArrayEquals(out1, out2);
    }

    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __deriveKey(final String algorithm) throws Exception {
        // ----------------------------------------------------------------------------------- given
        final var kdf = KDF.getInstance(algorithm);
        final var ikm = new byte[IKM_BYTES];
        ThreadLocalRandom.current().nextBytes(ikm);
        final var salt = new byte[SALT_BYTES];
        ThreadLocalRandom.current().nextBytes(salt);
        // ------------------------------------------------------------------------------------ when
        final SecretKey key1;
        final SecretKey key2;
        {
            final var spec = HKDFParameterSpec.ofExtract()
                    .addIKM(ikm)
                    .addSalt(salt)
                    .thenExpand(INFO, OUTPUT_BYTES);
            key1 = kdf.deriveKey(KEY_ALGORITHM, spec);
        }
        {
            final var spec = HKDFParameterSpec.ofExtract()
                    .addIKM(ikm)
                    .addSalt(salt)
                    .thenExpand(INFO, OUTPUT_BYTES);
            key2 = kdf.deriveKey(KEY_ALGORITHM, spec);
        }
        // ------------------------------------------------------------------------------------ then
        printf(algorithm, kdf.getProviderName(), key1);
        Assertions.assertEquals(KEY_ALGORITHM, key1.getAlgorithm());
        Assertions.assertEquals(OUTPUT_BYTES, key1.getEncoded().length);
        Assertions.assertArrayEquals(key1.getEncoded(), key2.getEncoded());
    }
}
