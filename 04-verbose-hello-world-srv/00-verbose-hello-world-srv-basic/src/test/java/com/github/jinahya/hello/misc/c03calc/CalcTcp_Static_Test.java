package com.github.jinahya.hello.misc.c03calc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CalcTcp_Static_Test {

    @Test
    void _ShouldBePositive_CIENT_THREADS() {
        Assertions.assertTrue(CalcTcp.CLIENT_THREADS > 0);
    }
}
