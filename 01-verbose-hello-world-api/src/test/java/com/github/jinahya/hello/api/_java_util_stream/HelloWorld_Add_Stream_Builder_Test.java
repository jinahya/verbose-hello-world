package com.github.jinahya.hello.api._java_util_stream;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A class for testing {@link HelloWorld#add(Stream.Builder)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("add(Stream.Builder)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
@SuppressWarnings("removal")
class HelloWorld_Add_Stream_Builder_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <builder> argument is <null>""")
    @Test
    void _ThrowNullPointerException_BuilderIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var builder = (Stream.Builder<Byte>) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.add(builder)
        );
    }

    @DisplayName("should invoke <accept(Consumer)> with the <builder>")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_will_return_the_array();
        @SuppressWarnings("unchecked")
        final var builder = (Stream.Builder<Byte>) Mockito.mock(Stream.Builder.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.add(builder);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).accept((Consumer<? super Byte>) builder);
        Assertions.assertSame(builder, result);
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_will_set_actual_hello_world_bytes();
        final var builder = Stream.<Number>builder();
        // ------------------------------------------------------------------------------------ when
        service.add(builder);
        // ------------------------------------------------------------------------------------ then
        final var expected = hello_world_byte_array();
        final var ints = builder.build().mapToInt(Number::intValue).toArray();
        Assertions.assertEquals(expected.length, ints.length);
        for (var i = 0; i < ints.length; i++) {
            Assertions.assertEquals(expected[i], (byte) ints[i]);
        }
    }
}
