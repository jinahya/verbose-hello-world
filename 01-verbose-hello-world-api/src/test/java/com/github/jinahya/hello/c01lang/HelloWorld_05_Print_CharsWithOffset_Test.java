package com.github.jinahya.hello.c01lang;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.jinahya.hello.HelloWorld.BYTES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for unit-testing {@link HelloWorld#print(char[])} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_07_Print_Chars_Arguments_Test
 */
@DisplayName("set(array)")
@Slf4j
class HelloWorld_05_Print_CharsWithOffset_Test extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        _stub_SetArray_ToReturnTheArray();
    }

    /**
     * Asserts {@link HelloWorld#print(char[]) print(chars)} method invokes
     * {@link HelloWorld#print(char[], int) set(array, offset)} method with given {@code array} and
     * {@code 0}.
     */
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- GIVEN
        var service = serviceInstance();
        var chars = new char[BYTES];
        // ------------------------------------------------------------------------------------ WHEN
        var result = service.print(chars);
        // ------------------------------------------------------------------------------------ THEN
        verify(service, times(1)).set(arrayCaptor().capture());
        var array = arrayCaptor().getValue();
        assertNotNull(array);
        assertEquals(BYTES, array.length);
        assertSame(chars, result);
    }
}
