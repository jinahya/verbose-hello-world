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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * A class for unit-testing {@link HelloWorld#set(byte[])} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_01_Set_Array_Arguments_Test
 */
@DisplayName("set(array)")
@Slf4j
class HelloWorld_01_Set_Array_Test extends HelloWorldTest {

    /**
     * Stubs {@link HelloWorld#set(byte[], int) set(array, index)} method to just return the
     * {@code array} argument.
     */
    @BeforeEach
    void stub_SetArrayIndex_ReturnArray() {
        when(service().set(any(), anyInt())).thenAnswer(i -> i.getArgument(0));
//        doAnswer(i -> i.getArgument(0)) // <3>
//                .when(service())        // <1>
//                .set(any(), anyInt());  // <2>
    }

    /**
     * Asserts {@link HelloWorld#set(byte[]) set(array)} method invokes
     * {@link HelloWorld#set(byte[], int) set(array, index)} method with given {@code array} and
     * {@code 0}.
     */
    @DisplayName("invokes set(array, 0)")
    @Test
    void _InvokeSetArrayWithArrayAndZero_() {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: array
        var array = new byte[BYTES];
        // WHEN
        // TODO: Invokes service.set(array)
        // THEN: once, set(array, 0) invoked
        // TODO: Verify set(array, 0) invoked
    }

    /**
     * Asserts {@link HelloWorld#set(byte[]) set(array)} method returns given {@code array}
     * argument.
     */
    @DisplayName("returns array")
    @Test
    void _ReturnArray_() {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: array
        var array = new byte[BYTES];
        // WHEN
        var actual = service.set(array);
        // THEN
        // TODO: Assert that the actual is same as expected.
    }
}
