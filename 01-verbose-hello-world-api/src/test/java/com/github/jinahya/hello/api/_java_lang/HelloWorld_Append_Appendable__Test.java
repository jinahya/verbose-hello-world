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

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.github.jinahya.hello.api.HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Append_Appendable__Test extends HelloWorldTest {

    @Nested
    class StringBuilder_Test {

        @Test
        void __() throws IOException {
            // ------------------------------------------------------------------------------- given
            final var service = set_array_sets_actual_hello_world_bytes(service());
            final var appendable = new StringBuilder();
            // -------------------------------------------------------------------------------- when
            service.append(appendable);
            // -------------------------------------------------------------------------------- then
            assertEquals(HelloWorldTestConstants.HELLO_WORLD_STRING, appendable.toString());
        }
    }
}
