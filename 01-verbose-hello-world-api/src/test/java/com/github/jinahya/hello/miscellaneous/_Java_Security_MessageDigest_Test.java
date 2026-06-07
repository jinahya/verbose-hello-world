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
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import static com.github.jinahya.hello.miscellaneous._Java_Security_MessageDigest_TestConstants.*;
import static com.github.jinahya.hello.miscellaneous._Java_Security_MessageDigest_TestUtils.*;
import static com.github.jinahya.hello.miscellaneous._Java_Security_Security_TestUtils.*;

@DisplayName("MessageDigest")
@Slf4j
class _Java_Security_MessageDigest_Test {

    private static void __(final String providerName) {
        final var provider = Security.getProvider(providerName);
        for (final var service : provider.getServices()) {
            if (!service.getType().equals(MESSAGE_DIGEST_SERVICE_TYPE)) {
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
                if (!service.getType().equals(MESSAGE_DIGEST_SERVICE_TYPE)) {
                    continue;
                }
                System.out.printf("%10s %s%n", provider.getName(), service.getAlgorithm());
            }
        }
    }

    @Test
    void algorithms__() {
        securityProviders().forEach(p -> {
            final var algorithms = p.getServices().stream()
                    .filter(s -> s.getType().equals(MESSAGE_DIGEST_SERVICE_TYPE))
                    .map(Provider.Service::getAlgorithm)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            if (algorithms.isEmpty()) {
                return;
            }
            System.out.printf("%-20s%n%s%n%n", p.getName(), algorithms);
        });
    }

    @Test
    void __() {
        final var input = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(input);
        securityProviders(MESSAGE_DIGEST_SERVICE_TYPE).forEach(p -> {
            for (final var algorithm : ALGORITHMS_LATEST_LTS) {
                try {
                    final var digest = MessageDigest.getInstance(algorithm, p);
                    _Java_Security_MessageDigest_TestUtils.printMessageDigest(digest);
                } catch (final Exception _) {
                }
            }
        });
    }

    @Test
    void __providersAndAlgorithms() {
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
        messageDigestProviderAndAlgorithm((p, a) -> {
            System.out.printf("%20s %s%n", p.getName(), a);
        });
    }

    @Test
    void __random() {
        final var plain = new byte[ThreadLocalRandom.current().nextInt(1024)];
        ThreadLocalRandom.current().nextBytes(plain);
//        System.out.printf("plain: %30s%n", __TestUtils.format(plain));
        messageDigestProviderAndAlgorithm((p, a) -> {
            final MessageDigest digest;
            try {
                digest = MessageDigest.getInstance(a, p);
            } catch (final NoSuchAlgorithmException nsae) {
                throw new RuntimeException(nsae);
            }
            try {
                digest.update(plain);
                final var value = digest.digest();
                printMessageDigest(digest, value);
            } catch (final IllegalArgumentException _) {
            }
        });
    }
}
