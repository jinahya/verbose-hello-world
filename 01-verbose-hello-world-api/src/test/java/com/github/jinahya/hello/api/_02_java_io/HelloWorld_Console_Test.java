package com.github.jinahya.hello.api._02_java_io;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.Console;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;
import java.util.Optional;

class HelloWorld_Console_Test extends HelloWorldTest {

    private interface PrivateHelloWorld extends HelloWorld {

        default <T extends Console> T write(final T console) throws IOException {
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
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = Mockito.spy(PrivateHelloWorld.class);
        Mockito.doAnswer(i -> i.getArgument(0))
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
