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

import com.github.jinahya.hello.api.*;
import lombok.extern.slf4j.*;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.verification.*;

import java.util.concurrent.*;

import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#set(byte[])} method.
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
class HelloWorld_Set_Array_Test extends HelloWorld__Test {

    /**
     * Verifies that the {@link HelloWorld#set(byte[]) set(array)} method throws a
     * {@link NullPointerException} when the {@code array} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <array> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_ArrayIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var array = (byte[]) null;
        // ------------------------------------------------------------------------------- when/then
//        assertThrows(
//                NullPointerException.class,
//                () -> service.set(array)
//        );
    }

    /**
     * Verifies that the {@link HelloWorld#set(byte[]) set(array)} method throws an
     * {@link IndexOutOfBoundsException} when {@code array.length} is less than
     * {@link HelloWorld#BYTES}({@value HelloWorld#BYTES}).
     */
    @DisplayName("""
            should throw an <IndexOutOfBoundsException>
            when <array.length> is less than <HelloWorld.BYTES>"""
    )
    @Test
    void _ThrowIndexOutOfBoundsException_ArrayLengthIsLessThan12() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var array = new byte[ThreadLocalRandom.current().nextInt(HelloWorld.BYTES)];
        assert array.length < HelloWorld.BYTES; // always 'true', I know
        // ------------------------------------------------------------------------------- when/then
//        assertThrows(
//                IndexOutOfBoundsException.class, // <expectedType>
//                () -> service.set(array)         // <executable>
//        );
    }

    /**
     * Verifies that the {@link HelloWorld#set(byte[]) set(array)} method invokes
     * {@link HelloWorld#set(byte[], int) set(array, index)} method with given {@code array} and
     * {@code 0}, and returns the {@code array}.
     *
     * @see Mockito#verify(Object, VerificationMode)
     * @see org.junit.jupiter.api.Assertions#assertSame(Object, Object)
     */
    @DisplayName("""
            should invoke <set(array, 0)>
            and returns the <array>"""
    )
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        doAnswer(returnsFirstArg()).when(service).set(any(byte[].class), anyInt());
        final var array = new byte[HelloWorld.BYTES];
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(array);
        // ------------------------------------------------------------------------------------ then
//        final var arrayCaptor = ArgumentCaptor.forClass(byte[].class);
//        final var indexCaptor = ArgumentCaptor.forClass(int.class);
//        verify(service, times(1)).set(arrayCaptor.capture(), indexCaptor.capture());
//        assertSame(array, arrayCaptor.getValue());
//        assertEquals(0, indexCaptor.getValue());
//        assertSame(array, result);
    }
}
