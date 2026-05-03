package com.github.jinahya.hello.api._java_util;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.IntFunction;

/**
 * A class for testing
 * {@link HelloWorld#collect(Collection, IntFunction) collect(collection, mapper)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("collect(Collection, IntFunction)")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Collect_Collection_IntFunction_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <collection> argument is <null>""")
    @Test
    void _ThrowNullPointerException_CollectionIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var collection = (Collection<Integer>) null;
        final IntFunction<? extends Integer> mapper = i -> i;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.collect(collection, mapper)
        );
    }

    @DisplayName("""
            should throw a <NullPointerException>
            when the <mapper> argument is <null>""")
    @Test
    void _ThrowNullPointerException_MapperIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var collection = new ArrayList<Integer>();
        final IntFunction<? extends Integer> mapper = null;
        // ----------------------------------------------------------------------------- when / then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.collect(collection, mapper)
        );
    }

    @DisplayName("""
            should invoke <set(byte[])>, and <collection.add(mapper.apply(b & 0xFF))> for each byte""")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_random_bytes(service());
        @SuppressWarnings("unchecked")
        final var collection = (Collection<Integer>) Mockito.mock(Collection.class);
        @SuppressWarnings("unchecked")
        final IntFunction<Integer> mapper = Mockito.mock(IntFunction.class);
        Mockito.when(mapper.apply(Mockito.anyInt())).thenAnswer(i -> (int) i.getArgument(0));
        // ------------------------------------------------------------------------------------ when
        final var result = service.collect(collection, mapper);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        final var inOrder = Mockito.inOrder(mapper, collection);
        for (final var b : array) {
            final var unsigned = b & 0xFF;
            inOrder.verify(mapper, Mockito.calls(1)).apply(unsigned);
            inOrder.verify(collection, Mockito.calls(1)).add(unsigned);
        }
        inOrder.verifyNoMoreInteractions();
        Assertions.assertSame(collection, result);
    }
}
