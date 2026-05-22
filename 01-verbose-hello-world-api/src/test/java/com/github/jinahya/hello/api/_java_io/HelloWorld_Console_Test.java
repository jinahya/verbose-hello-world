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

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import java.io.Console;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
class HelloWorld_Console_Test
        extends HelloWorldTest {

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
    @DisplayName("(null)NullPointerException")
    @Test
    void _ThrowNullPointerException_ConsoleIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = Mockito.spy(PrivateHelloWorld.class);
        final Console console = null;
        // ------------------------------------------------------------------------------------ when
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.write(console)
        );
    }

    @DisplayName("invoke write(console.writer)")
    @Test
    void __()
            throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = Mockito.spy(PrivateHelloWorld.class);
        Mockito.doAnswer(AdditionalAnswers.returnsFirstArg())
                .when(service)
                .write(Mockito.any(Writer.class));
        final var console = Optional
                .ofNullable(System.console())
                .orElseGet(() -> Mockito.mock(Console.class, Mockito.RETURNS_DEEP_STUBS));
        // ------------------------------------------------------------------------------------ when
        final var result = service.write(console);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).write(console.writer());
        Assertions.assertSame(console, result);
    }
}
