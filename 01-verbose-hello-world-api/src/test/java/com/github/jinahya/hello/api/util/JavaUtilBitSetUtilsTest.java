package com.github.jinahya.hello.api.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.BitSet;

@Slf4j
class JavaUtilBitSetUtilsTest {

    @Test
    void print_EmptyBitSet() {
        final var bitset = new BitSet();
        JavaUtilBitSetUtils.print(bitset);
    }

    @Test
    void print_SingleLong() {
        final var bitset = BitSet.valueOf(new long[] {0xDEADBEEFL});
        JavaUtilBitSetUtils.print(bitset);
    }

    @Test
    void print_MultipleLongs() {
        final var bitset = BitSet.valueOf(new long[] {0xCAFEBABEL, 0x0123456789ABCDEFL});
        JavaUtilBitSetUtils.print(bitset);
    }

    @Test
    void print_HelloWorldBytes() {
        final var bytes = "hello, world".getBytes(StandardCharsets.US_ASCII);
        final var bitset = BitSet.valueOf(bytes);
        JavaUtilBitSetUtils.print(bitset);
    }
}
