package com.github.jinahya.hello.misc.c03calc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Calc_Static_Test {

    @Test
    void _ShouldBePositive_CIENT_COUNT() {
        Assertions.assertTrue(Calc.CLIENT_COUNT > 0);
    }
}
