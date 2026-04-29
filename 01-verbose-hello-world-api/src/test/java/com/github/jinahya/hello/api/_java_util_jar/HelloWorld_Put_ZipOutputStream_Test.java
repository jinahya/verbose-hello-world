package com.github.jinahya.hello.api._java_util_jar;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipOutputStream;

/**
 * A class for testing {@link HelloWorld#put(JarOutputStream, String) put(stream, name)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("put(stream, name)")
@Slf4j
class HelloWorld_Put_JarOutputStream_Test
        extends HelloWorldTest {

    /**
     * Verifies that the {@link HelloWorld#put(JarOutputStream, String) put(stream, name)} method
     * throws a {@link NullPointerException} when the {@code stream} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <stream> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_StreamIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var stream = (JarOutputStream) null;
        final var name = "entry";
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.put(stream, name)
        );
    }

    /**
     * Verifies that the {@link HelloWorld#put(JarOutputStream, String) put(stream, name)} method
     * throws a {@link NullPointerException} when the {@code name} argument is {@code null}.
     */
    @DisplayName("""
            should throw a <NullPointerException>
            when the <name> argument is <null>"""
    )
    @Test
    void _ThrowNullPointerException_NameIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var stream = Mockito.mock(JarOutputStream.class);
        final var name = (String) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.put(stream, name)
        );
    }

    /**
     * Asserts {@link HelloWorld#put(JarOutputStream, String)} method invokes
     * {@link HelloWorld#put(ZipOutputStream, String)} method with given {@code stream} and
     * {@code name}.
     *
     * @throws IOException if an I/O error occurs.
     */
    @DisplayName("""
            should invoke <put((ZipOutputStream) stream, name>
            and return the <stream>"""
    )
    @Test
    void __() throws IOException {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_returns_the_array(service());
        final var stream = Mockito.mock(JarOutputStream.class);
        final var name = "hello.txt";
        // ------------------------------------------------------------------------------------ when
        final var result = service.put(stream, name);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).put((ZipOutputStream) stream, name);
        Assertions.assertSame(stream, result);
    }
}
