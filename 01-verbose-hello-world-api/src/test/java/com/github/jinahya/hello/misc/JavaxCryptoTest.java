package com.github.jinahya.hello.misc;

/*-
 * #%L
 * verbose-hello-world-api
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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.util.stream.Stream;

/**
 * A class for testing classes defined in {@link java.security} package.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see <a href="https://bit.ly/486gLAe">Java Security Standard Algorithm Names</a>
 * @see <a href="https://bit.ly/417yswU"><code>MesssageDigest</code> Algorithms</a>
 */
@DisplayName("java.security")
@Slf4j
class JavaxCryptoTest {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    static Stream<Provider> providers() {
        return Stream.of(Security.getProviders());
    }

    @DisplayName("MessageDigest")
    @Nested
    class MessageDigestTest {

        private static Stream<Provider> providers() {
            var type = MessageDigest.class.getSimpleName();
            return JavaxCryptoTest.providers()
                    .filter(p -> p.getServices().stream().anyMatch(s -> s.getType().equals(type)));
        }

        private static Stream<String> supportedAlgorithms() {
            return Stream.of("SHA-1", "SHA-256");
        }

        private static Stream<String> algorithms() {
            return Stream.of(
                    "MD2",
                    "MD5",
                    "SHA-1", "SHA-224", "SHA-256", "SHA-384", "SHA-512/224", "SHA-512/256",
                    "SHA3-224", "SHA3-256", "SHA3-384", "SHA3-512"
            );
        }

        private static Stream<Arguments>
        providerAndRequiredToBeSupportedAlgorithmArgumentsStream() {
            return providers()
                    .flatMap(p -> supportedAlgorithms().map(a -> Arguments.of(p, a)));
        }

        private static Stream<Arguments> providerAndAlgorithmArgumentsStream() {
            return providers()
                    .flatMap(p -> algorithms().map(a -> Arguments.of(p, a)));
        }

        @DisplayName("getInstance(required-to-be-supported)")
        @MethodSource({"providerAndRequiredToBeSupportedAlgorithmArgumentsStream"})
        @ParameterizedTest
        void getInstance_DoesNotThrow_RequiredToBeSupported(final Provider provider,
                                                            final String algorithm) {
            Assertions.assertDoesNotThrow(() -> MessageDigest.getInstance(algorithm, provider));
        }

        @DisplayName("getInstance(algorithm)")
        @MethodSource({"providerAndAlgorithmArgumentsStream"})
        @ParameterizedTest
        void getInstance__(final Provider provider, final String algorithm) {
            try {
                final var instance = MessageDigest.getInstance(algorithm);
                log.info("supported; provider: {}, algorithm: {}", provider, algorithm);
            } catch (final NoSuchAlgorithmException nsae) {
                log.error("not supported: provider: {}, algorithm: {}", provider, algorithm, nsae);
            }
        }
    }
}
