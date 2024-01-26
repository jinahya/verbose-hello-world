package com.github.jinahya.hello.misc.c03calc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class _CalcStaticTest {

    @Test
    void _ShouldBePositive_CIENT_THREADS() {
        Assertions.assertTrue(_Calc.CLIENT_THREADS > 0);
    }

    @Test
    void _ShouldBePositive_CIENT_COUNT() {
        Assertions.assertTrue(_Calc.CLIENT_COUNT > 0);
    }
}
