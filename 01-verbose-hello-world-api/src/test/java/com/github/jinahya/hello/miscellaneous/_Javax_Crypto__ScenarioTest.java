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

import lombok.*;
import org.junit.jupiter.api.*;

/**
 * A class for exploring {@code javax.crypto} integration scenarios.
 */
@DisplayName("javax.crypto / scenario")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
class _Javax_Crypto__ScenarioTest {

    @DisplayName("scenario 1")
    @Nested
    class Scenario1_Test {

        @DisplayName("happy path")
        @Test
        void __() {
            // Alice and Bob both have their keypairs
            // Alice send a message(hello, world) and a signature to Bob
            // Mallory, intercepts the message, modifies it to 'hello, world!' and send to Bob, with Alice's signature
            // Bob, fails to verify
        }
    }

    @DisplayName("scenario 2")
    @Nested
    class Scenario2_Test {

        @DisplayName("happy path")
        @Test
        void __() {
            // Alice and Bob both have their keypairs
            // Alice send a message(hello, world) and a signature to Bob
            // Mallory, intercepts the message, modifies it to 'hello, world!' and send to Bob, with Alice's signature
            // Bob, fails to verify
        }
    }
}
