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

import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;

@Slf4j
class SecretKeyFactoryTest {

    static {
//        Security.insertProviderAt(new org.bouncycastle.jce.provider.BouncyCastleProvider(), 1);
//        Security.insertProviderAt(new gnu.crypto.jce.GnuCrypto(), 1);
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    static Stream<String> algorithms() {
        return Stream.of(
                "AES",
                "ARCFOUR",
                "ChaCha20",
                "DES",
                "DESede"
//                ,
//                "PBEWith<digest>And<encryption>",
//                "PBEWith<prf>And<encryption>",
//                "PBKDF2With<prf>"
        );
    }

    static Stream<String> algorithmsRequiredToBeSupported() {
        return Stream.of(
                "DESede"
        );
    }

    @DisplayName("getInstance(algorithm-required-to-be-supported)DoesNotThrow")
    @MethodSource({"algorithmsRequiredToBeSupported"})
    @ParameterizedTest(name = "[{index}] algorithm: {0}")
    void getInstance_DoesNotThrow_AlgorithmRequiredToBeSupported(final String algorithm) {
        assertThatCode(() -> {
            SecretKeyFactory.getInstance(algorithm);
        }).doesNotThrowAnyException();
    }

    @DisplayName("getInstance(algorithm)")
    @MethodSource({"algorithms"})
    @ParameterizedTest(name = "[{index}] algorithm: {0}")
    void getInstance_(final String algorithm) {
        try {
            final var instance = SecretKeyFactory.getInstance(algorithm);
            log.error("supported: {}", algorithm);
        } catch (final NoSuchAlgorithmException nsae) {
            log.error("not supported: {}", algorithm);
        }
    }
}
