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

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static java.util.concurrent.ThreadLocalRandom.current;

/**
 * A class for testing {@link HelloWorld#set(byte[])} method regarding arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_01_Set_Array_Test
 */
@DisplayName("set(byte[]) arguments")
@Slf4j
class HelloWorld_01_Set_Array_Arguments_Test extends HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#set(byte[]) set(array)} method throws a
     * {@link NullPointerException} when the {@code array} argument is {@code null}.
     */
    @DisplayName("[array == null] -> NullPointerException")
    @Test
    void _ThrowNullPointerException_ArrayIsNull() {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: array
        byte[] array = null;
        // WHEN/THEN
        // TODO: Assert service.set(array) throws a NullPointerException.
    }

    /**
     * Asserts {@link HelloWorld#set(byte[]) set(array)} method throws an
     * {@link IndexOutOfBoundsException} when {@code array.length} is less than
     * {@value HelloWorld#BYTES}.
     */
    @DisplayName("[array.length < 12] -> IndexOutOfBoundsException")
    @Test
    void _ThrowIndexOutOfBoundsException_ArrayLengthIsLessThan12() {
        // GIVEN: HelloWorld
        var service = service();
        // GIVEN: array
        var array = new byte[current().nextInt(BYTES)];
        // WHEN/THEN
        // TODO: Assert service.set(array) throws an IndexOutOfBoundsException.
    }
}
