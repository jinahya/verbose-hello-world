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

import static com.github.jinahya.hello.miscellaneous._Java_Security_Provider_TestUtils.*;

@DisplayName("Security")
@Slf4j
class _Java_Security_Provider_Test {

    @Test
    void providers__() {
        acceptEachProvider(p -> {
            System.out.printf("provider: %s%n", p.getName());
        });
    }

    @Test
    void providersAndServices_() {
        applyProviderAndAcceptEachService(p -> s -> {
            System.out.printf("%20s / %s%n", p.getName(), s.getType());
        });
    }
}
