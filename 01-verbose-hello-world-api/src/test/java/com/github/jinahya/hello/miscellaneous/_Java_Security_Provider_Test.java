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
import org.junit.jupiter.api.*;

import java.security.*;
import java.util.*;

import static com.github.jinahya.hello.miscellaneous._Java_Security_Security_TestUtils.*;
import static java.util.stream.Collectors.*;

/**
 * A class for testing {@link java.security.Provider}.
 */
@DisplayName("java.security.Provider")
@Slf4j
class _Java_Security_Provider_Test {

    /**
     * Verifies that every registered {@link Provider} can be enumerated and its info printed.
     */
    @Test
    void providers__() {
        securityProviders().forEach(p -> {
            System.out.printf("%s%n%s%n%n",
                              String.format("%s (%s)", p.getName(), p.getVersionStr()),
                              p.getInfo());
        });
    }

    /**
     * Verifies that providers, services, and algorithms can be enumerated together in grouped
     * form.
     */
    @Test
    void providersServicesAndAlgorithms__() {
        securityProviders().limit(4L).forEach(p -> {
            System.out.printf("%-20s%n", p.getName());
            p.getServices().stream()
                    .collect(groupingBy(
                            Provider.Service::getType,
                            LinkedHashMap::new,
                            mapping(Provider.Service::getAlgorithm, toList())))
                    .sequencedEntrySet().stream()
                    .limit(4)
                    .forEach(e -> {
                        System.out.printf("%30s: %s%n", e.getKey(),
                                          e.getValue().stream()
                                                  .limit(4L)
                                                  .collect(joining(", ")));
                    });
//            System.out.printf("%40s: %s%n", "...", "...");
            System.out.println();
        });
    }
}
