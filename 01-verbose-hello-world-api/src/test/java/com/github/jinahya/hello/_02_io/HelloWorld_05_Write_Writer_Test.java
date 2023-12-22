package com.github.jinahya.hello._02_io;

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
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * A class for testing {@link HelloWorld#write(Writer) write(writer)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 * @see HelloWorld_05_Write_Writer_Arguments_Test
 */
@DisplayName("write(writer)")
@Slf4j
class HelloWorld_05_Write_Writer_Test
        extends _HelloWorldTest {

    @BeforeEach
    void _beforeEach() {
        _stub_PrintChars_ToReturnTheChars();
    }

    /**
     * Asserts {@link HelloWorld#write(Writer) write(writer)} method invokes
     * {@link HelloWorld#append(Appendable) append(appendable)} method with {@code writer}, and
     * returns the {@code writer}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("(writer)append(writer)")
    @Test
    void __()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        var service = serviceInstance();
        var writer = mock(Writer.class);
        // ------------------------------------------------------------------------------------ when
        var result = service.write(writer);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).append(writer);
        assertSame(writer, result);
    }
}
