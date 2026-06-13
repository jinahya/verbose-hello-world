package com.github.jinahya.hello.api._java_io;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2026 Jinahya, Inc.
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
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * A class for testing {@link HelloWorld#write(java.io.Writer) write(writer)} delegation through a
 * {@link Console}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("write(console)")
class HelloWorld_Console_Test
        extends HelloWorld__Test {

    private interface PrivateHelloWorld
            extends HelloWorld {

        default <T extends Console> T write(final T console)
                throws IOException {
            Objects.requireNonNull(console, "console is null");
            write(console.writer());
            return console;
        }
    }

    // -------------------------------------------------------------------------------- CONSTRUCTORS
    HelloWorld_Console_Test() {
        super();
    }

    // ---------------------------------------------------------------------------------------------

    /**
     * Verifies that the method throws a {@link NullPointerException} when the {@code console}
     * argument is {@code null}.
     */
    @DisplayName("should throw a <NullPointerException> when the <console> argument is <null>")
    @Test
    void _ThrowNullPointerException_ConsoleIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = spy(PrivateHelloWorld.class);
        final Console console = null;
        // ------------------------------------------------------------------------------------ when
        assertThrows(NullPointerException.class, () -> service.write(console));
    }

    /**
     * Verifies that the method invokes {@code write(console.writer())} and returns the
     * {@code console}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("should invoke <write(console.writer)>, and return the <console>")
    @Test
    void __()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = spy(PrivateHelloWorld.class);
        doAnswer(returnsFirstArg())
                .when(service)
                .write(any(Writer.class));
        final var console = Optional
                .ofNullable(System.console())
                .orElseGet(() -> mock(Console.class, RETURNS_DEEP_STUBS));
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(console);
        // ------------------------------------------------------------------------------------ then
        verify(service, times(1)).write(console.writer());
        assertSame(console, result);
    }
}
