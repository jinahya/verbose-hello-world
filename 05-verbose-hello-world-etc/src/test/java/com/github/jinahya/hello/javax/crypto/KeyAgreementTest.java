package com.github.jinahya.hello.javax.crypto;

/*-
 * #%L
 * verbose-hello-world-etc
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
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
