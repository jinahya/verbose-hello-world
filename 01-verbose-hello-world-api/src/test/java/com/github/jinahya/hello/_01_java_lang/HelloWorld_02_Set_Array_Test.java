package com.github.jinahya.hello._01_java_lang;

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
import com.github.jinahya.hello.HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;

import java.awt.event.HierarchyListener;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for unit-testing {@link HelloWorld#set(byte[])} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("set(array)")
@Slf4j
@SuppressWarnings({
        "java:S1481", // unused (yet) local variables
        "java:S1854", // useless (yet) assignments
        "java:S2699"  // no assertions (yet)
})
class HelloWorld_02_Set_Array_Test extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#set(byte[]) set(array)} method throws a
     * {@link NullPointerException} when the {@code array} argument is {@code null}.
     */
    @DisplayName("""
            should throw a NullPointerException
            when the <array> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ArrayIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var array = (byte[]) null;
        // ------------------------------------------------------------------------------- when/then
        // verify, service.set(array) throws a NullPointerException.
    }

    /**
     * Verifies that the {@link HelloWorld#set(byte[]) set(array)} method throws an
     * {@link ArrayIndexOutOfBoundsException} when {@code array.length} is less than
     * {@link HelloWorld#BYTES}({@value HelloWorld#BYTES}).
     */
    @DisplayName("""
            should throw an ArrayIndexOutOfBoundsException
            when <array.length> is less than HelloWorld.BYTES"""
    )
    @Test
    void _ThrowArrayIndexOutOfBoundsException_ArrayLengthIsLessThan12() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var array = new byte[ThreadLocalRandom.current().nextInt(HelloWorld.BYTES)];
        assert array.length < HelloWorld.BYTES;
        // ------------------------------------------------------------------------------- when/then
        // verify, service.set(array) throws an ArrayIndexOutOfBoundsException.
    }

    /**
     * Verifies that the {@link HelloWorld#set(byte[]) set(array)} method invokes
     * {@link HelloWorld#set(byte[], int) set(array, index)} method with given {@code array} and
     * {@code 0}, and returns the {@code array}.
     */
    @DisplayName("""
            should invoke set(array, 0)
            and returns the array"""
    )
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        // service.set(array, index) will just return the <array>
        BDDMockito.willAnswer(i -> i.getArgument(0))
                .given(service)
                .set(ArgumentMatchers.any(), ArgumentMatchers.anyInt());
        final var array = new byte[HelloWorld.BYTES];
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(array);
        // ------------------------------------------------------------------------------------ then
        // verify, service.set(array, 0) invoked, once
        // verify, result is same as array
    }
}
