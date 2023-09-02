package com.github.jinahya.hello.util;

import java.time.temporal.TemporalAmount;
import java.util.function.LongFunction;

interface Stopwatch<C> {

    C start();

    <T extends TemporalAmount> T stop(C carrier,
                                      LongFunction<? extends T> mapper);
}
