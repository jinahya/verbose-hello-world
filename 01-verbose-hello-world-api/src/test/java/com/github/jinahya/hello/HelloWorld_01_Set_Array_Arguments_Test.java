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

import java.util.Random;

/**
 * A class for testing {@link HelloWorld#set(byte[])} method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_01_Set_Array_Test
 */
@Slf4j
class HelloWorld_01_Set_Array_Arguments_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#set(byte[]) set(array)} method throws a {@link
     * NullPointerException} when the {@code array} argument is {@code null}.
     */
    @DisplayName("set(null) throws NullPointerException")
    @Test
    void set_ThrowNullPointerException_ArrayIsNull() {
        byte[] array = null;
        // TODO: Implement!
    }

    /**
     * Asserts {@link HelloWorld#set(byte[]) set(array)} method throws an {@link
     * IndexOutOfBoundsException} when {@code array.length} is less than {@link HelloWorld#BYTES}.
     */
    @DisplayName("set(array:not-long-enough) throws IndexOutOfBoundsException")
    @Test
    void set_ThrowIndexOutOfBoundsException_ArrayIsNotLongEnough() {
        var length = new Random().nextInt(HelloWorld.BYTES);
        var array = new byte[length];
        // TODO: Implement!
    }
}
