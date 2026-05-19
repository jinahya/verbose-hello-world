package com.github.jinahya.hello.api._java_util;

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

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Add_Collection_Function__Test
        extends HelloWorldTest {

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_actual_hello_world_bytes(service());
        final var collection = new ArrayList<Integer>();
        // ------------------------------------------------------------------------------------ when
        service.add(collection, Byte::intValue);
        // ------------------------------------------------------------------------------------ then
        final var expected = HelloWorldTestUtils.hello_world_byte_array();
        Assertions.assertEquals(expected.length, collection.size());
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals((int) expected[i], (int) collection.get(i));
        }
    }
}
