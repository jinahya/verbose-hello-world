package com.github.jinahya.hello.api._java_util;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.BitSet;

/**
 * A class for testing {@link HelloWorld#set(BitSet)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("set(BitSet)")
@Slf4j
class HelloWorld_Set_BitSet_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <bitset> argument is <null>""")
    @Test
    void _ThrowNullPointerException_BitSetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var bitset = (BitSet) null;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.set(bitset)
        );
    }

    @DisplayName("should invoke <set(bitset, 0)>")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        Mockito.doAnswer(i -> i.getArgument(0))
                .when(service)
                .set(ArgumentMatchers.<BitSet>notNull(), ArgumentMatchers.intThat(v -> v >= 0));
        final var bitset = Mockito.spy(new BitSet());
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(bitset);
        // ------------------------------------------------------------------------------------ then
        Mockito.verify(service, Mockito.times(1)).set(bitset, 0);
        Assertions.assertSame(bitset, result);
    }
}
