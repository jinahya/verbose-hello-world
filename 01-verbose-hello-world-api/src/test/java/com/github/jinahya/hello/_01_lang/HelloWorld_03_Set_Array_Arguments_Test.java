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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#set(byte[]) set(array)} method regarding arguments
 * verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_03_Set_Array_Test
 */
@DisplayName("set(array) arguments")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_03_Set_Array_Arguments_Test
        extends _HelloWorldTest {

    /**
     * Asserts {@link HelloWorld#set(byte[]) set(array)} method throws a
     * {@link NullPointerException} when the {@code array} argument is {@code null}.
     */
    @DisplayName("[array == null] -> NullPointerException")
    @Test
    void _ThrowNullPointerException_ArrayIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = serviceInstance();
        final var array = (byte[]) null;
        // ------------------------------------------------------------------------------- when/then
        // TODO: Assert service.set(array) throws a NullPointerException.
    }

    /**
     * Asserts {@link HelloWorld#set(byte[]) set(array)} method throws an
     * {@link ArrayIndexOutOfBoundsException} when {@code array.length} is less than
     * {@value HelloWorld#BYTES}.
     */
    @DisplayName("[array.length < 12] -> ArrayIndexOutOfBoundsException")
    @Test
    void _ThrowArrayIndexOutOfBoundsException_ArrayLengthIsLessThan12() {
        // ----------------------------------------------------------------------------------- given
        final var service = serviceInstance();
        final var array = new byte[ThreadLocalRandom.current().nextInt(HelloWorld.BYTES)];
        // ------------------------------------------------------------------------------- when/then
        // TODO: Assert service.set(array) throws an IndexOutOfBoundsException.
    }
}
