package com.github.jinahya.hello.miscellaneous;

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

import lombok.extern.slf4j.*;
import org.bouncycastle.jce.provider.*;
import org.junit.jupiter.api.*;

import java.security.*;

import static com.github.jinahya.hello.miscellaneous._Java_Security_Provider_TestUtils.*;

@DisplayName("MessageDigest")
@Slf4j
class _Java_Security_MessageDigest_Test {

    private static final String SERVICE_TYPE = "MessageDigest";

    private static void __(final String providerName) {
        final var provider = Security.getProvider(providerName);
        for (final var service : provider.getServices()) {
            if (!service.getType().equals(SERVICE_TYPE)) {
                continue;
            }
            System.out.printf("%10s %s%n", provider.getName(), service.getAlgorithm());
        }
    }

    @DisplayName("should print every <MessageDigest> algorithm registered with <Security>")
    @Test
    void algorithmsByProviders__() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
        for (final var provider : Security.getProviders()) {
            for (final var service : provider.getServices()) {
                if (!service.getType().equals(SERVICE_TYPE)) {
                    continue;
                }
                System.out.printf("%10s %s%n", provider.getName(), service.getAlgorithm());
            }
        }
    }

    @DisplayName("should print every <MessageDigest> algorithm registered with <Security>")
    @Test
    void algorithms__() {
        for (final var provider : Security.getProviders()) {
            for (final var service : provider.getServices()) {
                if (!service.getType().equals(SERVICE_TYPE)) {
                    continue;
                }

                for (var algorithm : Security.getAlgorithms("MessageDigest")) {
                    System.out.printf("%s%n", service.getAlgorithm());
                }
            }
        }
    }

    @DisplayName("should print every <MessageDigest> algorithm registered with <Security>")
    @Test
    void __() {
        applyProviderAndAcceptEachService(p -> s -> {
            if (!s.getType().equals(SERVICE_TYPE)) {
                return;
            }
            System.out.printf("%20s %s%n", p.getName(), s.getAlgorithm());
        });
    }
}
