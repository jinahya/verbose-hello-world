package com.github.jinahya.hello._01_lang;

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

import com.github.jinahya.hello.HelloWorld;
import com.github.jinahya.hello._HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A class for unit-testing {@link HelloWorld#set(byte[])} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_02_Set_Array_Arguments_Test
 */
@DisplayName("set(array)")
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_02_Set_Array_Test extends _HelloWorldTest {

    /**
     * Verifies that {@link HelloWorld#set(byte[]) set(array)} method invokes
     * {@link HelloWorld#set(byte[], int) set(array, index)} method with given {@code array} and
     * {@code 0}, and returns the {@code array}.
     */
    @DisplayName("-> set(array, 0)")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var array = new byte[HelloWorld.BYTES];
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(array);
        // ------------------------------------------------------------------------------------ then
        // TODO: Verify service.set(array, 0) invoked, once
        // TODO: Assert result is same as array
    }
}
