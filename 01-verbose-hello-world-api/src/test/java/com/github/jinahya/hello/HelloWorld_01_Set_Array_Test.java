package com.github.jinahya.hello;

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

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * A class for unit-testing {@link HelloWorld#set(byte[])} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_01_Set_Array_Arguments_Test
 */
@Slf4j
class HelloWorld_01_Set_Array_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#set(byte[]) set(array)} method invokes
     * {@link HelloWorld#set(byte[], int) set(array, index)} method with given {@code array} and
     * {@code 0}.
     */
    @DisplayName("set(array) invokes set(array, 0)")
    @Test
    void set_InvokeSetArrayWithArrayAndZero_() {
        var array = new byte[HelloWorld.BYTES];
        // TODO: Invoke helloWorld().set(array).
        // TODO: Verify helloWorld() invoked set(array, 0).
    }

    /**
     * Asserts {@link HelloWorld#set(byte[]) set(array)} method returns given {@code array}
     * argument.
     */
    @DisplayName("set(array) returns array")
    @Test
    void set_ReturnArray_() {
        var expected = new byte[HelloWorld.BYTES];
        var actual = helloWorld().set(expected);
        // TODO: Assert that expected and actual refer to the same object.
    }
}
