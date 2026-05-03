package com.github.jinahya.hello.api._java_util;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.BitSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for testing {@link HelloWorld#set(BitSet, int)} method.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
@DisplayName("set(BitSet, int)")
@Slf4j
class HelloWorld_Set_BitSet_Index_Test
        extends HelloWorldTest {

    @DisplayName("""
            should throw a <NullPointerException>
            when the <bitset> argument is <null>""")
    @Test
    void _ThrowNullPointerException_BitSetIsNull() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var bitset = (BitSet) null;
        final var index = 0;
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                NullPointerException.class,
                () -> service.set(bitset, index)
        );
    }

    @DisplayName("""
            should throw an <IllegalArgumentException>
            when the <index> argument is negative""")
    @Test
    void _ThrowIllegalArgumentException_IndexIsNegative() {
        // ----------------------------------------------------------------------------------- given
        final var service = service();
        final var bitset = new BitSet();
        final var index = ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, 0);
        // ------------------------------------------------------------------------------- when/then
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.set(bitset, index)
        );
    }

    @DisplayName("should invoke <set(byte[])> and set bits in little-endian order")
    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var service = HelloWorldTestUtils.set_array_sets_random_bytes(service());
        final var bitset = Mockito.spy(new BitSet(HelloWorld.BYTES << 3));
        final var index = 0;
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(bitset, index);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        Assertions.assertArrayEquals(array, bitset.toByteArray());
        Assertions.assertSame(bitset, result);
    }
}
