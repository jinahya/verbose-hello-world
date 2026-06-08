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

import java.security.*;
import java.util.function.*;
import java.util.stream.*;

import static com.github.jinahya.hello.miscellaneous._Java_Security_MessageDigest_TestConstants.*;

/**
 * A class providing test utilities for {@link java.security.MessageDigest}.
 */
@Slf4j
public class _Java_Security_MessageDigest_TestUtils {

    public static Stream<String> latestLtsAlgorithms() {
        return _Java_Security_MessageDigest_TestConstants.ALGORITHMS_LATEST_LTS.stream();
    }

    public static Stream<String> latestJdkAlgorithms() {
        return _Java_Security_MessageDigest_TestConstants.ALGORITHMS_LATEST_JDK.stream();
    }

    // ---------------------------------------------------------------------------------------------
    public static void printMessageDigest(final MessageDigest digest, final byte[] result) {
        System.out.printf("%20s | %20s | %3d(%4d) | %s%n",
                          digest.getProvider().getName(),
                          digest.getAlgorithm(),
                          result.length,
                          result.length << 3,
                          __TestUtils.format(result)
        );
    }

    public static void printMessageDigest(final MessageDigest digest) {
        printMessageDigest(digest, digest.digest());
    }

    // ---------------------------------------------------------------------------------------------
    static void messageDigestAlgorithm(final Provider provider, final Consumer<String> consumer) {
        for (final var service : provider.getServices()) {
            if (!service.getType().equals(MESSAGE_DIGEST_SERVICE_TYPE)) {
                continue;
            }
            consumer.accept(service.getAlgorithm());
        }
    }

    static void messageDigestProviderAndAlgorithm(final BiConsumer<Provider, String> consumer) {
        _Java_Security_Provider_TestUtils.acceptEachProvider(provider -> {
            messageDigestAlgorithm(provider, a -> {
                consumer.accept(provider, a);
            });
        });
    }

    // ---------------------------------------------------------------------------------------------

    private _Java_Security_MessageDigest_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
