package com.github.jinahya.hello.api._java_util;

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

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class for testing {@link HelloWorld#collect(Collection)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("collect(Collection)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Collect_Collection_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <collection> argument is <null>""")
    @Test
    void _ThrowNullPointerException_CollectionIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var collection = (Collection<Byte>) null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.collect(collection)
        );
    }

    @DisplayName("should invoke <set(byte[])>, and <collection.add(b)> for each byte")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_will_return_the_array();
        @SuppressWarnings("unchecked")
        final var collection = (Collection<Byte>) Mockito.mock(Collection.class);
        // ------------------------------------------------------------------------------------ when
        final var result = service.collect(collection);
        // ------------------------------------------------------------------------------------ then
        final var array = set_array12_invoked_once();
        final var inOrder = Mockito.inOrder(collection);
        for (final var b : array) {
            inOrder.verify(collection, Mockito.calls(1)).add(b);
        }
        inOrder.verifyNoMoreInteractions();
        Assertions.assertSame(collection, result);
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_will_set_actual_hello_world_bytes();
        final var collection = new ArrayList<Number>();
        // ------------------------------------------------------------------------------------ when
        service.collect(collection);
        // ------------------------------------------------------------------------------------ then
        final var expected = hello_world_byte_array();
        Assertions.assertEquals(expected.length, collection.size());
        final var actual = collection.stream().mapToInt(Number::intValue).toArray();
        for (var i = 0; i < expected.length; i++) {
            Assertions.assertEquals(expected[i], (byte) actual[i]);
        }
    }
}
