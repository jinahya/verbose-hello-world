package com.github.jinahya.hello.api._java_lang;

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

import com.github.jinahya.hello.api.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;

import java.io.*;

import static com.github.jinahya.hello.api.HelloWorld__TestConstants.*;
import static com.github.jinahya.hello.api.HelloWorld__TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A class exercising {@link HelloWorld#append(Appendable) append(appendable)} against various real
 * {@link Appendable} implementations.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("append(appendable)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Append_Appendable__Test extends HelloWorld__Test {

    /**
     * Stubs the service so that {@code append(appendable)} appends the {@code hello-world-bytes}
     * before each test.
     *
     * @throws IOException if an I/O error occurs while stubbing.
     */
    @BeforeEach
    void __stubService() throws IOException {
        append_appendable_appends_hello_world_bytes(service());
    }

    // ---------------------------------------------------------------------------------------------
    @DisplayName("string builder")
    @Nested
    class StringBuilder_Test {

        /**
         * Verifies that the method appends the {@code hello-world-string} to a real
         * {@link StringBuilder}.
         *
         * @throws IOException if an I/O error occurs.
         */
        @DisplayName("happy path")
        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_sets_hello_world_bytes(service());
            final var appendable = new StringBuilder();
            // -------------------------------------------------------------------------------- when
            service.append(appendable);
            // -------------------------------------------------------------------------------- then
            assertEquals(HELLO_WORLD_STRING, appendable.toString());
        }
    }
}
