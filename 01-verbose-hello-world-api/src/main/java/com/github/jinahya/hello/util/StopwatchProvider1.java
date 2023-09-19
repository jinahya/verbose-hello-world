package com.github.jinahya.hello.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.LongFunction;
import java.util.function.LongSupplier;

/**
 * A stopwatch implementation uses {@link ThreadLocal}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
enum StopwatchProvider1 implements StopwatchProvider<Void> {

    INSTANCE() {
        @Override
        public Void start(final LongSupplier supplier) {
            Objects.requireNonNull(supplier, "supplier is null");
            START_NANOS.set(supplier.getAsLong());
            return null;
        }

        @Override
        public <R> R stop(final Void carrier, final LongSupplier supplier,
                          final LongFunction<? extends R> mapper) {
            Objects.requireNonNull(supplier, "supplier is null");
            Objects.requireNonNull(mapper, "mapper is null");
            try {
                final long start = Optional.ofNullable(START_NANOS.get())
                        .orElseThrow(() -> new IllegalStateException("not started yet"));
                final long elapsed = supplier.getAsLong() - start;
                if (elapsed < 0) {
                    throw new IllegalArgumentException("elapsed(" + elapsed + ") is negative");
                }
                return mapper.apply(elapsed);
            } finally {
                START_NANOS.remove();
            }
        }
    };

    private static final ThreadLocal<Long> START_NANOS = new ThreadLocal<>();
}
