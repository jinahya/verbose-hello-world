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
import org.jspecify.annotations.*;
import org.junit.jupiter.api.*;

import java.security.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static com.github.jinahya.hello.miscellaneous._Java_Security_Security_TestUtils.*;
import static java.util.stream.Collectors.*;

@DisplayName("Security")
@Slf4j
class _Java_Security_Provider_TestUtils {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public static Stream<Provider> providers(final String serviceType) {
        return Arrays.stream(Security.getProviders())
                .filter(p -> p.getServices().stream()
                        .anyMatch(s -> s.getType().equals(serviceType)));
    }

    public static Stream<Provider.Service> providerServices(final Provider provider) {
        return provider.getServices().stream();
    }

    static void acceptEachProvider(final Consumer<? super Provider> consumer) {
        Arrays.stream(Security.getProviders()).forEach(consumer);
    }

    static void acceptProviderAndService(
            final BiConsumer<? super Provider, ? super Provider.Service> consumer) {
        acceptEachProvider(p -> {
            p.getServices().forEach(s -> consumer.accept(p, s));
        });
    }

    static void applyProviderAndAcceptEachService(
            final Function<
                    ? super Provider,
                    ? extends Consumer<? super Provider.Service>> function) {
        acceptProviderAndService((p, s) -> function.apply(p).accept(s));
    }

    static void printProvidersServicesAndAlgorithms(final long providerLimit,
                                                    final @Nullable String serviceType,
                                                    final long serviceLimit,
                                                    final long algorithmLimit) {
        securityProviders().limit(providerLimit).forEach(p -> {
            System.out.printf("%-20s%n", p.getName());
            p.getServices().stream()
                    .filter(s -> serviceType == null || serviceType.equals(s.getType()))
                    .collect(groupingBy(
                            Provider.Service::getType,
                            LinkedHashMap::new,
                            mapping(Provider.Service::getAlgorithm, toList())))
                    .sequencedEntrySet().stream()
                    .filter(e -> !e.getValue().isEmpty())
                    .limit(serviceLimit)
                    .forEach(e -> {
                        System.out.printf("%30s: %s%n", e.getKey(),
                                          e.getValue().stream()
                                                  .limit(algorithmLimit)
                                                  .collect(joining(", ")));
                    });
            System.out.println();
        });
    }

    private _Java_Security_Provider_TestUtils() {
        throw new AssertionError("instantiation is not allowed");
    }
}
