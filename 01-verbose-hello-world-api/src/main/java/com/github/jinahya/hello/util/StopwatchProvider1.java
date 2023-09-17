package com.github.jinahya.hello.util;

import java.time.temporal.TemporalAmount;
import java.util.Optional;
import java.util.function.LongFunction;

enum StopwatchProvider1 implements StopwatchSpi<Void> {

    INSTANCE() {
        @Override
        public Void start() {
            START_NANOS.set(System.nanoTime());
            return null;
        }

        @Override
        public <T extends TemporalAmount> T stop(final Void carrier,
                                                 final LongFunction<? extends T> mapper) {
            final long start = Optional.ofNullable(START_NANOS.get())
                    .orElseThrow(() -> new IllegalStateException("not started yet"));
            try {
                return mapper.apply(System.nanoTime() - start);
            } finally {
                START_NANOS.remove();
            }
        }
    };

    private static final ThreadLocal<Long> START_NANOS = new ThreadLocal<>();
}
