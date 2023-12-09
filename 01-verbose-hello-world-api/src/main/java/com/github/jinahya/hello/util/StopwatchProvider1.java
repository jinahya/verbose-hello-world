package com.github.jinahya.hello.util;

/*-
 * #%L
 * verbose-hello-world-api
 * %%
 * Copyright (C) 2018 - 2023 Jinahya, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Objects;
import java.util.Optional;
import java.util.function.LongFunction;
import java.util.function.LongSupplier;

/**
 * A stopwatch implementation uses {@link ThreadLocal}.
 *
 * @author Jin Kwon &lt;onacit_at_gmail.com&gt;
 */
enum StopwatchProvider1
        implements StopwatchProvider<Void> {

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
