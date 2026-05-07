package com.github.jinahya.hello;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class _Javax_Crypto_KDF_Test {

    private static Stream<String> algorithms() {
        return Stream.of(
                "HKDF-SHA256",
                "HKDF-SHA384",
                "HKDF-SHA512"
        );
    }

    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __deriveData(final String algorithm) throws Exception {
        // ------------------------------------------------------------------------------- given
        final var kdf = KDF.getInstance(algorithm);
        final var ikm = new byte[32];
        ThreadLocalRandom.current().nextBytes(ikm);
        final var salt = new byte[16];
        ThreadLocalRandom.current().nextBytes(salt);
        final var info = "hello, world".getBytes(StandardCharsets.UTF_8);
        final var length = 32;
        // -------------------------------------------------------------------------------- when
        final byte[] out1;
        final byte[] out2;
        {
            final var spec = HKDFParameterSpec.ofExtract()
                    .addIKM(ikm)
                    .addSalt(salt)
                    .thenExpand(info, length);
            out1 = kdf.deriveData(spec);
        }
        {
            final var spec = HKDFParameterSpec.ofExtract()
                    .addIKM(ikm)
                    .addSalt(salt)
                    .thenExpand(info, length);
            out2 = kdf.deriveData(spec);
        }
        // -------------------------------------------------------------------------------- then
        log.debug("{} ({}) encoded={}", algorithm, kdf.getProviderName(),
                  Base64.getEncoder().encodeToString(out1));
        Assertions.assertEquals(length, out1.length);
        Assertions.assertArrayEquals(out1, out2);
    }

    @MethodSource({"algorithms"})
    @ParameterizedTest
    void __deriveKey(final String algorithm) throws Exception {
        // ------------------------------------------------------------------------------- given
        final var kdf = KDF.getInstance(algorithm);
        final var ikm = new byte[32];
        ThreadLocalRandom.current().nextBytes(ikm);
        final var salt = new byte[16];
        ThreadLocalRandom.current().nextBytes(salt);
        final var info = "hello, world".getBytes(StandardCharsets.UTF_8);
        final var length = 32;  // 256-bit AES key
        final var keyAlgorithm = "AES";
        // -------------------------------------------------------------------------------- when
        final SecretKey key1;
        final SecretKey key2;
        {
            final var spec = HKDFParameterSpec.ofExtract()
                    .addIKM(ikm)
                    .addSalt(salt)
                    .thenExpand(info, length);
            key1 = kdf.deriveKey(keyAlgorithm, spec);
        }
        {
            final var spec = HKDFParameterSpec.ofExtract()
                    .addIKM(ikm)
                    .addSalt(salt)
                    .thenExpand(info, length);
            key2 = kdf.deriveKey(keyAlgorithm, spec);
        }
        // -------------------------------------------------------------------------------- then
        log.debug("{} ({}) keyAlg={} encoded={}", algorithm, kdf.getProviderName(),
                  key1.getAlgorithm(),
                  Base64.getEncoder().encodeToString(key1.getEncoded()));
        Assertions.assertEquals(keyAlgorithm, key1.getAlgorithm());
        Assertions.assertEquals(length, key1.getEncoded().length);
        Assertions.assertArrayEquals(key1.getEncoded(), key2.getEncoded());
    }
}
