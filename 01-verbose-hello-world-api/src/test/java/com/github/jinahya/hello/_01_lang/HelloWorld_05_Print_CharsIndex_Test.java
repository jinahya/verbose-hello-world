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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for unit-testing {@link HelloWorld#print(char[], int) print(chars, index)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_05_Print_CharsIndex_Arguments_Test
 */
@DisplayName("set(chars, index)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_05_Print_CharsIndex_Test extends _HelloWorldTest {

    @BeforeEach
    void beforeEach() {
        setArray_ToReturnTheArray();
    }

    /**
     * Asserts {@link HelloWorld#print(char[]) print(chars)} method invokes
     * {@link HelloWorld#print(char[], int) set(array, offset)} method with given {@code array} and
     * {@code 0}, and returns the {@code chars}
     */
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var chars = new char[HelloWorld.BYTES];
        final var index = 0;
        // ------------------------------------------------------------------------------------ when
        final var result = service.print(chars, index);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).set(arrayCaptor().capture());
        final var array = arrayCaptor().getValue();
        assertNotNull(array);
        assertEquals(HelloWorld.BYTES, array.length);
        assertSame(chars, result);
    }
}
