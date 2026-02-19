package com.github.jinahya.hello.api._java_util;

import com.github.jinahya.hello.api.HelloWorld;
import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import com.github.jinahya.hello.api.畵蛇添足;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.Arrays;
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
        final var service = service();
        Mockito.doAnswer(i -> {
            final var array = i.getArgument(0, byte[].class);
            Arrays.fill(array, (byte) -1);
            return array;
        }).when(service).set(ArgumentMatchers.<byte[]>notNull());
        final var nbits = HelloWorld.BYTES << 3;
        final var bitset = Mockito.spy(new BitSet(nbits));
        final var index = 0;
        // ------------------------------------------------------------------------------------ when
        final var result = service.set(bitset, index);
        // ------------------------------------------------------------------------------------ then
        final var array = HelloWorldTestUtils.set_array12_invoked_once(service);
        Assertions.assertArrayEquals(array, bitset.toByteArray());
        Assertions.assertTrue(bitset.length() <= nbits);
        Assertions.assertSame(bitset, result);
    }

    @畵蛇添足
    @Test
    void _添足_畵蛇() {
        // ----------------------------------------------------------------------------------- given
        final var service = set_array_will_set_actual_hello_world_bytes();
        final var bitset = new BitSet();
        var index = ThreadLocalRandom.current().nextInt(16);
        // ------------------------------------------------------------------------------------ when
        service.set(bitset, index);
        // ------------------------------------------------------------------------------------ then
        final var array = new byte[HelloWorld.BYTES];
        for (var i = 0; i < array.length; i++) {
            for (var j = 0; j < Byte.SIZE; j++) {
                array[i] |= (byte) ((bitset.get(index++) ? 0x01 : 0x00) << j);
            }
        }
        Assertions.assertArrayEquals(hello_world_byte_array(), array);
    }
}
