package com.github.jinahya.hello.api;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2019 Jinahya, Inc.
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
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import static com.github.jinahya.hello.api.HelloWorld__TestConstants.*;
import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class for testing constants defined in {@link HelloWorld} interface.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("HelloWorld constants")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Constants_Test {

    @DisplayName("BYTES")
    @Nested
    class BYTES_Test {

        /**
         * Verifies that {@link HelloWorld#BYTES} equals {@code 12} — the byte length of the
         * {@code hello-world-string} in
         * {@link java.nio.charset.StandardCharsets#US_ASCII US_ASCII}.
         */
        @DisplayName(
                "should equal <12> (the byte length of the <hello-world-string> in <US_ASCII>)")
        @Test
        void _12_BYTES() {
            final var expected = HELLO_WORLD_STRING.getBytes(US_ASCII).length;
            final var actual = HelloWorld.BYTES;
            assertEquals(expected, actual);
        }
    }
}
