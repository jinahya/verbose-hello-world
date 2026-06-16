package com.github.jinahya.hello.miscellaneous._java_security;

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

import com.github.jinahya.hello.api.annotations.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.security.*;

import static com.github.jinahya.hello.miscellaneous._java_security._Java_Security_Provider_TestUtils.*;

/**
 * A class for testing {@link Security}.
 */
@_NotForPublishing
@DisplayName("java.security")
@Slf4j
class _Java_Security__Test {

    @DisplayName("happy path")
    @Test
    void __() {
        for (final var provider : Security.getProviders()) {
            System.out.printf("%s (%s)%n", provider.getName(), provider.getVersionStr());
            servicesAndAlgorithms(provider).forEach((type, algorithms) -> {
                System.out.printf("\t%s: %s%n", type, String.join(", ", algorithms));
            });
        }
    }
}
