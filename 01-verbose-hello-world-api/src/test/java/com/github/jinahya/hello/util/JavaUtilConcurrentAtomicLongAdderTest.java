package com.github.jinahya.hello.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.LongAdder;

@Slf4j
class JavaUtilConcurrentAtomicLongAdderTest {

    @Test
    void __() {
        final var adder = new LongAdder();
        adder.add(1L);
        adder.add(Long.MAX_VALUE);
        Assertions.assertEquals(Long.MIN_VALUE, adder.sum());
    }
}
