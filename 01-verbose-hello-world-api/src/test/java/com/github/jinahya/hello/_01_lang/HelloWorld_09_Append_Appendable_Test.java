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
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyChar;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#append(Appendable) append(appendable)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_09_Append_Appendable_Arguments_Test
 */
@DisplayName("append(appendable)")
@Slf4j
@SuppressWarnings({
        "java:S101"
})
class HelloWorld_09_Append_Appendable_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        _stub_PrintChars_ToReturnTheChars();
    }

    /**
     * Asserts {@link HelloWorld#append(Appendable) append(appendable)} method invokes
     * {@link HelloWorld#print(char[]) print(chars)} method with an array of
     * {@value HelloWorld#BYTES} characters, appends each character in resulting array to
     * {@code appendable}, and returns the {@code appendable}.
     */
    @DisplayName("-> print(char[12]) -> appendable.append(each-char)")
    @Test
    void __()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = serviceInstance();
        final var appendable = mock(Appendable.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.append(appendable);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).print(charsCaptor().capture());
        final var chars = charsCaptor().getValue();
        assertNotNull(chars);
        assertEquals(HelloWorld.BYTES, chars.length);
        verify(appendable, times(chars.length)).append(anyChar());
        assertSame(appendable, result);
    }
}
