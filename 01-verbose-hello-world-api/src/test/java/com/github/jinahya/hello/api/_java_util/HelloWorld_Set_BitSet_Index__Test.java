package com.github.jinahya.hello.api._java_util;

import com.github.jinahya.hello.api.HelloWorldTest;
import com.github.jinahya.hello.api.HelloWorldTestUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.BitSet;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
class HelloWorld_Set_BitSet_Index__Test
        extends HelloWorldTest {

    @BeforeEach
    void __a() {
        Mockito.doAnswer(i -> {
            final var bitset = i.getArgument(0, BitSet.class);
            var index = i.getArgument(1, Integer.class);
            for (int b : HelloWorldTestUtils.hello_world_byte_array()) {
                for (var j = 0; j < Byte.SIZE; j++) {
                    bitset.set(index++, (b & 1) == 1);
                    b >>>= 1;
                }
            }
            return bitset;
        }).when(service()).set(
                ArgumentMatchers.<BitSet>notNull(),
                ArgumentMatchers.intThat(v -> v >= 0)
        );
    }

    @Test
    void __() {
        // ----------------------------------------------------------------------------------- given
        final var bitset = new BitSet();
        // ------------------------------------------------------------------------------------ when
        service().set(bitset, 0);
        // ------------------------------------------------------------------------------------ then
        Assertions.assertEquals(95, bitset.length());
        Assertions.assertEquals(48, bitset.cardinality());
        Assertions.assertEquals(128, bitset.size());
    }
}
