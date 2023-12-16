package com.github.jinahya.hello.javax.crypto;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.crypto.KeyAgreement;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

@Slf4j
class KeyAgreementTest {

    static {
//        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
//        Security.insertProviderAt(new gnu.crypto.jce.GnuCrypto(), 1);
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    static Stream<String> requiredToBeSupportedAlgorithmStream() {
        return Stream.of(
                "DiffieHellman"
        );
    }

    static Stream<String> algorithmStream() {
        return Stream.of(
                "DiffieHellman",
                "ECDH",
                "ECMQV",
                "XDH",
                "X25519",
                "X448"
        );
    }

    @DisplayName("getInstance(algorithm-required-to-be-supported)")
    @MethodSource({"requiredToBeSupportedAlgorithmStream"})
    @ParameterizedTest
    void getInstance_DoesNotThrow_RequiredToBeSupported(final String algorithm) {
        assertThatCode(() -> {
            KeyAgreement.getInstance(algorithm);
        }).doesNotThrowAnyException();
    }

    @DisplayName("getInstance(algorithm)")
    @MethodSource({"algorithmStream"})
    @ParameterizedTest
    void getInstance_MayNotBeSupported_(final String algorithm) {
        try {
            final var instance = KeyAgreement.getInstance(algorithm);
            log.debug("supported: algorithm: {}, provider: {}", algorithm, instance.getProvider());
        } catch (final NoSuchAlgorithmException nsae) {
            log.error("not supported: algorithm: {}", algorithm);
        }
    }
}
