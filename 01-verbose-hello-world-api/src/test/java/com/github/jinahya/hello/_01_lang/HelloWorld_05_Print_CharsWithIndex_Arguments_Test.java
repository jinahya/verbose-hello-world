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

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * A class for testing {@link HelloWorld#print(char[], int) print(chars, index)} method regarding
 * arguments verification.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_05_Print_CharsWithIndex_Test
 */
@DisplayName("set(chars, index) arguments")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_05_Print_CharsWithIndex_Arguments_Test extends _HelloWorldTest {

    /**
     * Verifies {@link HelloWorld#print(char[], int) print(chars, index)} method throws a
     * {@link NullPointerException} when the {@code chars} argument is {@code null}.
     */
    @DisplayName("[chars == null] -> NullPointerException")
    @Test
    void _ThrowNullPointerException_ArrayIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var chars = (char[]) null;
        final var index = 0;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                NullPointerException.class,
                () -> service.print(chars, index)
        );
    }

    /**
     * Verifies {@link HelloWorld#print(char[], int) print(chars, index)} method throws an
     * {@link ArrayIndexOutOfBoundsException} when {@code index} is negative.
     */
    @DisplayName("[index < 0] -> ArrayIndexOutOfBoundsException")
    @Test
    void _ThrowArrayIndexOutOfBoundsException_IndexIsNegative() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var chars = new char[0];
        final var index = ThreadLocalRandom.current().nextInt() | Integer.MIN_VALUE;
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                ArrayIndexOutOfBoundsException.class,
                () -> service.print(chars, index)
        );
    }

    /**
     * Asserts {@link HelloWorld#print(char[], int) print(chars, index)} method throws an
     * {@link ArrayIndexOutOfBoundsException} when {@code index + HelloWorld.BYTES} is greater than
     * {@code chars.length}.
     */
    @DisplayName("[index + BYTES > chars.length] -> ArrayIndexOutOfBoundsException")
    @Test
    void _ThrowArrayIndexOutOfBoundsException_IndexPlus12GreaterThanCharsLength() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var chars = new char[ThreadLocalRandom.current().nextInt(BYTES)];
        final var index = ThreadLocalRandom.current().nextInt(1, chars.length << 1);
        // ------------------------------------------------------------------------------- when/then
        assertThrows(
                ArrayIndexOutOfBoundsException.class,
                () -> service.print(chars, index)
        );
    }
}
